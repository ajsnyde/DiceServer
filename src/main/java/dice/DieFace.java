package dice;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "DieFace")
public class DieFace {
	static DieFace blank = null;

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid")
	@GeneratedValue(generator = "uuid-gen")
	public String id;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
    public Date createdDate;
	
	public DieFace() {
	}
}
