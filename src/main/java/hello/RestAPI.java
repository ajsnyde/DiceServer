package hello;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPI {

  @RequestMapping(value = "/dietest", method = RequestMethod.GET, produces = "application/json")
  public Iterable<Die> getAllDice() {
    return Application.dieRepo.findAll();
  }

  @RequestMapping(value = "/die/{die:.+}", method = RequestMethod.GET, produces = "application/json")
  public Die showDice(@PathVariable long die, Model model) {
    return Application.dieRepo.findOne(die);
  }

  @RequestMapping(value = "/diejobs", method = RequestMethod.GET, produces = "application/json")
  public Iterable<DieJob> showDieJobs() {
    return Application.dieJobRepo.findAll();
  }

}