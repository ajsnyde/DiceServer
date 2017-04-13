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
import diceServer.dice.DieOrder;
import diceServer.dice.FixtureCompiler;
import diceServer.dice.FixtureCompiler.FixtureType;

@RestController
public class RestAPI {
  String imgFormat = "PNG";

  @RequestMapping(value = "/die/", method = RequestMethod.GET, produces = "application/json")
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
    return Utils.serveIMG("die" + id + "." + imgFormat, Application.dieRepo.findOne(id).getMap(), imgFormat);
  }

  @GetMapping("/die/{id}/face/{faceId}/face")
  @ResponseBody
  public ResponseEntity<Resource> serveFace(@PathVariable long id, @PathVariable int faceId) {
    Die die = Application.dieRepo.findOne(id);
    DieFace face = die.getFace(faceId);
    return Utils.serveIMG("die" + id + "face" + faceId + "." + imgFormat, face.getFace(), imgFormat);
  }

  @RequestMapping(value = "/face/{faceId}", method = RequestMethod.GET, produces = "application/json")
  public DieFace getFace(@PathVariable long faceId) {
    return Application.dieFaceRepo.findOne(faceId);
  }

  @GetMapping("/face/{faceId}/face")
  @ResponseBody
  public ResponseEntity<Resource> serveFace(@PathVariable long faceId) {
    DieFace face = Application.dieFaceRepo.findOne(faceId);
    return Utils.serveIMG("face" + faceId + "." + imgFormat, face.getFace(), imgFormat);
  }

  @RequestMapping(value = "/diebatch/{id}", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<Resource> getBatch(@PathVariable long id) {
    return Application.dieBatchRepo.findOne(id).zip("batch" + id + ".zip");
  }

  @RequestMapping(value = "/diebatch/", method = RequestMethod.GET, produces = "application/json")
  public Iterable<DieBatch> getBatches(@PathVariable long id) {
    return Application.dieBatchRepo.findAll();
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

  @RequestMapping(value = "/dieOrders", method = RequestMethod.GET, produces = "application/json")
  public Iterable<DieOrder> showDieOrders() {
    return Application.dieOrderRepo.findAll();
  }

  @GetMapping("/dieBatch")
  @ResponseBody
  public ResponseEntity<Resource> serveImage() {
    DieBatch batch = new FixtureCompiler(FixtureType.ROWBYROWCELL).compile();
    Application.dieBatchRepo.save(batch);
    return batch.zip("dieBatch" + batch.id + ".zip");
  }

}