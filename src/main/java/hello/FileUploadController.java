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

  @GetMapping("/files/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
    Resource file = storageService.loadAsResource(filename);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @GetMapping("/viewDie/{id:.+}")
  public String showDice(@PathVariable String id, Model model) {
    model.addAttribute("id", id);
    return "viewDice";
  }

  @GetMapping("/die/{id:.+}/map")
  @ResponseBody
  public ResponseEntity<Resource> serveMap(@PathVariable long id) {
    Die die = Application.dieRepo.findOne(id);
    System.out.println(die.getMap());
    File map = new File(die.id + ".bmp");
    try {
      ImageIO.write(Utils.ImageToBufferedImage(Utils.ByteArrayToImage(die.getMap())), "BMP", map);
    } catch (IOException e) {
      e.printStackTrace();
    }
    storageService.store(map);
    Resource file = storageService.loadAsResource(die.id + ".bmp");
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @GetMapping("/die/{id:.+}/face/{faceId:.+}/face")
  @ResponseBody
  public ResponseEntity<Resource> serveFace(@PathVariable long id, @PathVariable long faceId) {
    Die die = Application.dieRepo.findOne(id);
    DieFace face = die.faces.get((int) faceId);
    File map = new File(die.id + "-" + face.id + ".bmp");
    try {
      ImageIO.write(Utils.ImageToBufferedImage(Utils.ByteArrayToImage(face.getFaceBytes())), "BMP", map);
    } catch (IOException e) {
      e.printStackTrace();
    }
    storageService.store(map);
    Resource file = storageService.loadAsResource(die.id + "-" + face.id + ".bmp");
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @GetMapping("/die/{die:.+}/face/{face:.+}")
  @ResponseBody
  public String serveImage(@PathVariable long die, @PathVariable long face) {
    return "id: " + Application.dieFaceRepo.findOne(face).id;
  }

  @RequestMapping("/die/{die:.+}")
  @ResponseBody
  public String showDice(@PathVariable long die, Model model) {
    return "id: " + Application.dieRepo.findOne(die).id;
  }

  @PostMapping("/Upload")
  public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    Die die = null;
    try {
      die = new Die(ImageIO.read(Utils.convert(file)));
      die.setMap(Files.readAllBytes(Utils.convert(file).toPath()));
      die.setMap(Utils.ByteArrayToImage(Files.readAllBytes(Utils.convert(file).toPath())));
      Application.dieRepo.save(die);
      Application.dieFaceRepo.save(die.faces);
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

  @RequestMapping(value = "**", method = RequestMethod.GET)
  public String getAnythingelse() {
    return "redirect:/YOURMOTHER.html";
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }
}
