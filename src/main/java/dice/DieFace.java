package dice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DieFace")
public class DieFace {
	static DieFace blank = null;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public long id;

	public DieFace() {
	}
}
