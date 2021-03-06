package dice.server.app;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stripe.model.Charge;

import dice.Die;
import dice.DieFactory;
import dice.DieJob;
import dice.DieOrder;
import dice.server.storage.AWSFileSystemStorageService;
import dice.server.storage.StorageFileNotFoundException;

@Controller
public class FileUploadController {

	static final int MAX_FILE_SIZE_READ_BYTES = 1024 * 1024 * 10;

	@Autowired
	DieFactory dieFactory;

	private static Logger logger = LogManager.getLogger(FileUploadController.class);

	@RequestMapping("/cart")
	public String cart(HttpSession session, Model model, @CookieValue(value = "diceServerSession", defaultValue = "NA") String cookie, HttpServletResponse response) {
		// create cookie with session ID if there is no prior session. Otherwise, use
		// existing cookie session ID for TODO: stuffs.
		if (cookie == "NA")
			response.addCookie(new Cookie("diceServerSession", session.getId()));
		else {
			logger.debug("sessionId: " + session.getId());
			if (Application.dieOrderRepo.findFirstBySessionId(session.getId()) == null) {
				logger.info("No Cart Associated with sessionId " + session.getId());
				Application.dieOrderRepo.save(new DieOrder(session.getId()));
			}
			model.addAttribute("cart", Application.dieOrderRepo.findFirstBySessionId(session.getId()));
		}
		return "cart";
	}

	@Autowired
	public FileUploadController(AWSFileSystemStorageService storageService) {
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
	public String removeJob(@PathVariable String id, @PathVariable String id2) {
		Application.dieOrderRepo.findOne(id).removeJob(id2);
		return "redirect:/cart/";
	}

	@PostMapping("/Upload")
	public String fileUploadToDie(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		Die die = null;
		try {
			die = dieFactory.createDieFromTemplate(IOUtils.readBytesAndClose(file.getInputStream(), MAX_FILE_SIZE_READ_BYTES));
			Application.dieRepo.save(die);
			logger.info("Newly created dieId: " + die.id);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "redirect:/viewDie/" + die.id;
	}

	@PostMapping("/Job")
	public String createJob(HttpSession session, @RequestParam("redirect") String redirect, @RequestParam("dieId") String dieId, @RequestParam("quantity") int quantity,
			@CookieValue(value = "diceServerSession", defaultValue = "NA") String cookie, HttpServletResponse response) throws BusinessLogicException {
		
		if(quantity > 288 || quantity < 16)
			throw new BusinessLogicException("Quantity needs to be between 16 and 288 dice");
		
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
		DieOrder order = Application.dieOrderRepo.findAll().iterator().next();// Application.dieOrderRepo.findOne(Long.parseLong(params.get("dieOrderId")));
		logger.info("Charging with the following order: " + "ORDER ID:" + params.get("dieOrderId") + " - #Jobs " + order.jobs.size() + " - Cost: " + order.getCost());
		Charge charge = Utils.charge(params.get("stripeToken"), order);

		return "/uploadForm";
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<Object> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
	
	@ExceptionHandler(BusinessLogicException.class)
	@ResponseBody 
	public ResponseEntity<String> handleBusinessLogicException(BusinessLogicException exc) {
		return ResponseEntity.badRequest().body(exc.getLocalizedMessage());
	}

	@GetMapping("/badSession")
	public String badSession() {
		return "redirect:/";
	}

	public void checkSessionAndCart() {

	}
}
