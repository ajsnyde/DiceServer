package diceServer.dice;

import org.springframework.data.repository.CrudRepository;

public interface DieRepo extends CrudRepository<Die, Long> {
}