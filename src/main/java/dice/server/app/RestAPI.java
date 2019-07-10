package dice.server.app;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dice.Die;
import dice.DieBatch;
import dice.DieFace;
import dice.DieJob;
import dice.DieOrder;
import dice.FixtureCompiler;
import dice.FixtureCompiler.FixtureType;

@RestController
public class RestAPI {
	String imgFormat = "PNG";

	@RequestMapping(value = "/die/", method = RequestMethod.GET, produces = "application/json")
	public Iterable<Die> getAllDice() {
		return Application.dieRepo.findAll();
	}

	@RequestMapping(value = "/die/{die:.+}", method = RequestMethod.GET, produces = "application/json")
	public Die showDice(@PathVariable String die, Model model) {
		return Application.dieRepo.findOne(die);
	}

	@GetMapping("/die/{templateId}/map")
	@ResponseBody
	public ResponseEntity<Resource> serveMap(@PathVariable String templateId) {
		return Utils.serveIMG("die" + templateId + "." + imgFormat, Application.storageService.get("templates/" + templateId + ".png"), imgFormat);
	}

	@GetMapping("/die/{id}/face/{faceId}/face")
	@ResponseBody
	public ResponseEntity<Resource> serveFace(@PathVariable String id, @PathVariable int faceId) {
		Die die = Application.dieRepo.findOne(id);
		DieFace face = die.getFace(faceId);
		return Utils.serveIMG("die" + id + "face" + faceId + "." + imgFormat, Application.storageService.get("dieFaces/" + face.id + ".png"), imgFormat);
	}

	@RequestMapping(value = "/face/{faceId}", method = RequestMethod.GET, produces = "application/json")
	public DieFace getFace(@PathVariable String faceId) {
		return Application.dieFaceRepo.findOne(faceId);
	}

	@GetMapping("/face/{faceId}/face")
	@ResponseBody
	public ResponseEntity<Resource> serveFace(@PathVariable String faceId) {
		DieFace face = Application.dieFaceRepo.findOne(faceId);
		return Utils.serveIMG("face" + faceId + "." + imgFormat, Application.storageService.get("dieFaces/" + face.id + ".png"), imgFormat);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/diejobs", method = RequestMethod.GET, produces = "application/json")
	public Iterable<DieJob> showDieJobs() {
		return Application.dieJobRepo.findAll();
	}

	@GetMapping("/dieJob/{id}/map")
	@ResponseBody
	public ResponseEntity<Resource> serveJobMap(@PathVariable String id) {
		return serveMap(Application.dieJobRepo.findOne(id).getDie().getDieTemplate().id);
	}

	@RequestMapping(value = "/dieOrders", method = RequestMethod.GET, produces = "application/json")
	public Iterable<DieOrder> showDieOrders() {
		return Application.dieOrderRepo.findAll();
	}

	@RequestMapping(value = "/dieBatch", method = RequestMethod.GET, produces = "application/json")
	public Iterable<DieBatch> showDieBatches() {
		return Application.dieBatchRepo.findAll();
	}

	@RequestMapping(value = "/diebatch/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Resource> getBatch(@PathVariable String id) {
		return Application.dieBatchRepo.findOne(id).zip("batch" + id + ".zip");
	}
	
	@PostMapping("/dieBatch")
	@ResponseBody
	public ResponseEntity<Resource> serveImage() {
		DieBatch batch = new FixtureCompiler(FixtureType.ROWBYROWCELL).compile();
		Application.dieBatchRepo.save(batch);
		return batch.zip("dieBatch" + batch.id + ".zip");
	}

}