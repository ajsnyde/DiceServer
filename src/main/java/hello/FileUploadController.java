package hello;

import hello.storage.StorageFileNotFoundException;
import hello.storage.StorageService;

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

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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
    return serveBMP("die" + id, Application.dieRepo.findOne(id).getMap());
  }

  @GetMapping("/die/{id}/face/{faceId}/face")
  @ResponseBody
  public ResponseEntity<Resource> serveFace(@PathVariable long id, @PathVariable int faceId) {
    Die die = Application.dieRepo.findOne(id);
    DieFace face = die.getFace(faceId);
    return serveBMP("die" + id + "face" + faceId, face.getFace());
  }

  @GetMapping("/diebatch/{id}/face/{faceId}")
  @ResponseBody
  public ResponseEntity<Resource> serveDieBatchFace(@PathVariable long id, @PathVariable long faceId) {
    DieBatch dieBatch = Application.dieBatchRepo.findOne(id);
    return serveBMP("diebatch" + id + "face" + faceId, dieBatch.faces.get((int) faceId));
  }

  public ResponseEntity<Resource> serveBMP(String filename, Image image) {
    File map = new File(filename + ".bmp");
    try {
      ImageIO.write(Utils.ImageToBufferedImage(image), "BMP", map);
    } catch (IOException e) {
      e.printStackTrace();
    }
    storageService.store(map);
    Resource file = storageService.loadAsResource(filename + ".bmp");
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @GetMapping("/die/{die}/face/{face}")
  @ResponseBody
  public String serveImage(@PathVariable long die, @PathVariable long face) {
    return "id: " + Application.dieFaceRepo.findOne(face).id;
  }

  @GetMapping("/dieBatch")
  @ResponseBody
  public ResponseEntity<Resource> serveImage() {
    DieBatch batch = new SimpleCompiler().compile();
    // Application.dieBatchRepo.save(batch);
    // Batch saving is broken due to duplicate Die objects
    return serveBMP("dieBatch", batch.faces.get(1));
  }

  @PostMapping("/Upload")
  public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    Die die = null;
    try {
      die = new Die(ImageIO.read(Utils.convert(file)));
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
  public String pay(@RequestParam("stripeToken") String stripeToken, @RequestParam("dieId") long dieId, @RequestParam("quantity") int quantity) {
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
