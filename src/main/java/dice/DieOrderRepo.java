package dice;

import org.springframework.data.repository.CrudRepository;

public interface DieOrderRepo extends CrudRepository<DieOrder, String> {
	public DieOrder findFirstBySessionId(String sessionId);
}