package diceServer.dice;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DieJobRepo extends CrudRepository<DieJob, Long> {

  @Query("SELECT d from DieJob d where d.quantityLeft > 0")
  public List<DieJob> findByCompletion();
}