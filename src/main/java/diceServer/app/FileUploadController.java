package diceServer.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import diceServer.dice.Die;
import diceServer.dice.DieBatch;
import diceServer.dice.DieFace;
import diceServer.dice.DieJob;
import diceServer.dice.Fixture;
import diceServer.dice.FixtureCompiler;
import diceServer.storage.StorageFileNotFoundException;
import diceServer.storage.StorageService;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

@Controller
public class FileUploadController {

  private final StorageService storageService;

  @Autowired
  public FileUploadController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping("/")
  public String listUploadedFiles(Model model) throws IOException {
    model.addAttribute("files", storageService.loadAll().map(path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString()).build().toString())
        .collect(Collectors.toList()));
    return "uploadForm";
  }

  @GetMapping("/getBatch")
  public String getBatch(Model model) {
    model.addAttribute("jobs", Application.dieJobRepo.findAll());
    return "getBatch";
  }

  @GetMapping("/badSession")
  public String badSession() {
    return "redirect:/";
  }

  @GetMapping("/files/{filename}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
    Resource file = storageService.loadAsResource(filename);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @GetMapping("/viewDie/{id}")
  public String showDice(@PathVariable String id, Model model) {
    model.addAttribute("id", id);
    return "viewDice";
  }

  @GetMapping("/die/{id}/map")
  @ResponseBody
  public ResponseEntity<Resource> serveMap(@PathVariable long id) {
    return serveIMG("die" + id, Application.dieRepo.findOne(id).getMap(), "PNG");
  }

  @GetMapping("/die/{id}/face/{faceId}/face")
  @ResponseBody
  public ResponseEntity<Resource> serveFace(@PathVariable long id, @PathVariable int faceId) {
    Die die = Application.dieRepo.findOne(id);
    DieFace face = die.getFace(faceId);
    return serveIMG("die" + id + "face" + faceId, face.getFace(), "PNG");
  }

  @GetMapping("/diebatch/{id}/face/{faceId}")
  @ResponseBody
  public ResponseEntity<Resource> serveDieBatchFace(@PathVariable long id, @PathVariable long faceId) {
    DieBatch dieBatch = Application.dieBatchRepo.findOne(id);
    return serveIMG("diebatch" + id + "face" + faceId, dieBatch.faces.get((int) faceId), "PNG");
  }

  public ResponseEntity<Resource> serveIMG(String filename, Image image, String format) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(Utils.ImageToBufferedImage(image), format, baos);
      baos.flush();
      byte[] imageInByte = baos.toByteArray();
      baos.close();
      storageService.store(filename + "." + format, imageInByte);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Resource file = storageService.loadAsResource(filename + "." + format);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @GetMapping("/dieBatch")
  @ResponseBody
  public ResponseEntity<Resource> serveImage() {
    Fixture fixture = new Fixture(new File("C:\\Users\\Dreadhawk\\Desktop\\DiceServer\\resources\\fixture2.json"));
    DieBatch batch = new FixtureCompiler(fixture).compile();
    // Application.dieBatchRepo.save(batch);
    // Batch saving is broken due to duplicate Die objects
    batch.zip("dieBatch.zip");
    return serveIMG("dieBatch", batch.faces.get(1), "PNG");
  }

  @PostMapping("/Upload")
  public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
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
  public String pay(@RequestParam("stripeToken") String stripeToken, @RequestParam("dieId") long dieId, @RequestParam("quantity") int quantity, HttpSession session) {
    session.setAttribute("session", new Session());
    System.out.println(session.getAttribute("session"));

    try {
      Utils.charge(stripeToken, quantity * 300);
      createJob(dieId, quantity);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/viewDie/" + dieId;
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }
}
