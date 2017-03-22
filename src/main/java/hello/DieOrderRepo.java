package hello;

import org.springframework.data.repository.CrudRepository;

public interface DieOrderRepo extends CrudRepository<DieOrder, Long> {
}