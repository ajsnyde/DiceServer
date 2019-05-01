package dice.server.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stripe.model.Charge;

import dice.Die;
import dice.DieJob;
import dice.DieOrder;
import dice.server.storage.StorageFileNotFoundException;
import dice.server.storage.StorageService;

import java.awt.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class FileUploadController {

  @RequestMapping("/cart")
  public String cart(HttpSession session, Model model, @CookieValue(value = "diceServerSession", defaultValue = "NA") String cookie, HttpServletResponse response) {
    // create cookie with session ID if there is no prior session. Otherwise, use existing cookie session ID for TODO: stuffs.
    if (cookie == "NA")
      response.addCookie(new Cookie("diceServerSession", session.getId()));
    else {
      System.out.println(session.getId());
      if (Application.dieOrderRepo.findFirstBySessionId(session.getId()) == null) {
        System.out.println("No Cart Associated with sessionId");
        Application.dieOrderRepo.save(new DieOrder(session.getId()));
      }
      model.addAttribute("cart", Application.dieOrderRepo.findFirstBySessionId(session.getId()));
    }
    return "cart";
  }

  @Autowired
  public FileUploadController(StorageService storageService) {
  }

  @GetMapping({ "/", "/uploadForm" })
  public String uploadForm() throws IOException {
    return "uploadForm";
  }

  @GetMapping("/getBatch")
  public String getBatch(Model model) {
    model.addAttribute("jobs", Application.dieJobRepo.findAll());
    return "getBatch";
  }

  @GetMapping("/help")
  public String gethelp() {
    return "help";
  }

  @GetMapping(value = "/viewDie/{id}")
  public String showDice(@PathVariable String id, Model model, HttpServletResponse response) {
    model.addAttribute("id", id);
    return "viewDice";
  }

  @GetMapping("/dieOrder/{id}/removeJob/{id2}")
  public String removeJob(@PathVariable long id, @PathVariable long id2) {
    Application.dieOrderRepo.findOne(id).removeJob(id2);
    return "redirect:/cart/";
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
  public String createJob(HttpSession session, @RequestParam("redirect") String redirect, @RequestParam("dieId") long dieId, @RequestParam("quantity") int quantity,
      @CookieValue(value = "diceServerSession", defaultValue = "NA") String cookie, HttpServletResponse response) {
    Die die = Application.dieRepo.findOne(dieId);
    if (die != null) {
      DieJob job = new DieJob(die, quantity);
      if (cookie == "NA")
        response.addCookie(new Cookie("diceServerSession", session.getId()));
      // create new cart associated with sessionId if not existing
      DieOrder order = Application.dieOrderRepo.findFirstBySessionId(session.getId());
      if (order == null)
        order = new DieOrder(session.getId());
      order.jobs.add(job);
      Application.dieOrderRepo.save(order);
      Application.dieJobRepo.save(order.getJobs());
    } else
      throw new NullPointerException("Your die wasn't found!");
    return "redirect:" + redirect;
  }

  // Takes a single DieOrder Id and Stripe payment token.
  @PostMapping("/pay")
  public String pay(HttpSession session, @RequestParam Map<String, String> params) {
    System.out.println(params.get("stripeShippingAddressLine1"));
    DieOrder order = Application.dieOrderRepo.findAll().iterator().next();// Application.dieOrderRepo.findOne(Long.parseLong(params.get("dieOrderId")));
    System.out.println("ORDER ID:" + params.get("dieOrderId") + " - #Jobs " + order.jobs.size() + " - Cost: " + order.getCost());
    Charge charge = Utils.charge(params.get("stripeToken"), order);

    return "/uploadForm";
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/badSession")
  public String badSession() {
    return "redirect:/";
  }

  public void checkSessionAndCart() {

  }
}
