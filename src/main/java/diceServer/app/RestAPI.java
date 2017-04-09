package diceServer.app;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import diceServer.dice.Die;
import diceServer.dice.DieBatch;
import diceServer.dice.DieFace;
import diceServer.dice.DieJob;
import diceServer.dice.FixtureCompiler;
import diceServer.dice.FixtureCompiler.FixtureType;

@RestController
public class RestAPI {

  String imgFormat = "PNG";

  @RequestMapping(value = "/dietest", method = RequestMethod.GET, produces = "application/json")
  public Iterable<Die> getAllDice() {
    return Application.dieRepo.findAll();
  }

  @RequestMapping(value = "/die/{die:.+}", method = RequestMethod.GET, produces = "application/json")
  public Die showDice(@PathVariable long die, Model model) {
    return Application.dieRepo.findOne(die);
  }

  @GetMapping("/die/{id}/map")
  @ResponseBody
  public ResponseEntity<Resource> serveMap(@PathVariable long id) {
    return Utils.serveIMG("die" + id, Application.dieRepo.findOne(id).getMap(), imgFormat);
  }

  @GetMapping("/die/{id}/face/{faceId}/face")
  @ResponseBody
  public ResponseEntity<Resource> serveFace(@PathVariable long id, @PathVariable int faceId) {
    Die die = Application.dieRepo.findOne(id);
    DieFace face = die.getFace(faceId);
    return Utils.serveIMG("die" + id + "face" + faceId, face.getFace(), imgFormat);
  }

  @GetMapping("/diebatch/{id}/face/{faceId}")
  @ResponseBody
  public ResponseEntity<Resource> serveDieBatchFace(@PathVariable long id, @PathVariable long faceId) {
    DieBatch dieBatch = Application.dieBatchRepo.findOne(id);
    return Utils.serveIMG("diebatch" + id + "face" + faceId, dieBatch.faces.get((int) faceId), imgFormat);
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "/diejobs", method = RequestMethod.GET, produces = "application/json")
  public Iterable<DieJob> showDieJobs() {
    return Application.dieJobRepo.findAll();
  }

  @RequestMapping(value = "/dieBatches", method = RequestMethod.GET, produces = "application/json")
  public Iterable<DieBatch> showDieBatches() {
    return Application.dieBatchRepo.findAll();
  }

  @GetMapping("/dieBatch")
  @ResponseBody
  public ResponseEntity<Resource> serveImage() {
    DieBatch batch = new FixtureCompiler(FixtureType.ROWBYROWCELL).compile();
    // Application.dieBatchRepo.save(batch);
    // Batch saving is broken due to duplicate Die objects
    batch.zip("dieBatch.zip");
    return Utils.serveIMG("dieBatch", batch.faces.get(1), imgFormat);
  }

}