package diceServer.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stripe.model.Charge;

import diceServer.dice.Die;
import diceServer.dice.DieJob;
import diceServer.storage.StorageFileNotFoundException;
import diceServer.storage.StorageService;

import java.awt.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

@Controller
public class FileUploadController {

  @Autowired
  public FileUploadController(StorageService storageService) {
  }

  @GetMapping("/")
  public String uploadForm() throws IOException {
    return "uploadForm";
  }

  @GetMapping("/getBatch")
  public String getBatch(Model model) {
    model.addAttribute("jobs", Application.dieJobRepo.findAll());
    return "getBatch";
  }

  @GetMapping("/viewDie/{id}")
  public String showDice(@PathVariable String id, Model model) {
    model.addAttribute("id", id);
    return "viewDice";
  }

  @PostMapping("/Upload")
  public String fileUploadToDie(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    Die die = null;
    try {
      Image image = ImageIO.read(Utils.convert(file));
      die = new Die(image);
      die.setMap(Files.readAllBytes(Utils.convert(file).toPath()));
      die.setMap(Utils.ByteArrayToImage(Files.readAllBytes(Utils.convert(file).toPath())));
      Application.dieRepo.save(die);
      Application.dieFaceRepo.save(die.getFaces());
      System.out.println(die.getMapBytes().hashCode());
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "redirect:/viewDie/" + die.id;
  }

  @PostMapping("/Job")
  public String createJob(@RequestParam("dieId") long dieId, @RequestParam("quantity") int quantity) {
    Die die = Application.dieRepo.findOne(dieId);
    if (die != null) {
      DieJob job = new DieJob(die, quantity);
      Application.dieJobRepo.save(job);
    } else
      throw new NullPointerException("Your die wasn't found!");
    return "redirect:/viewDie/" + die.id;
  }

  @PostMapping("/pay")
  public String pay(@RequestParam Map<String, String> params, HttpSession session) {
    session.setAttribute("session", new Session());
    System.out.println(session.getAttribute("session"));
    System.out.println(params.get("stripeShippingAddressLine1"));

    Charge charge = Utils.charge(params.get("stripeToken"), Integer.parseInt(params.get("quantity")) * 300);
    createJob(Long.parseLong(params.get("dieId")), Integer.parseInt(params.get("quantity")));

    return "redirect:/viewDie/" + params.get("dieId");
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/badSession")
  public String badSession() {
    return "redirect:/";
  }
}
