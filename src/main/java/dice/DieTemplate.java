package dice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DieTemplate")
public class DieTemplate {
	static DieTemplate blank = null;

	@Id
	@GeneratedValue
	public long id;

	public DieTemplate() {
	}
}
