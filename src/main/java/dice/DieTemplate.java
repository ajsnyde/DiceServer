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
@Table(name = "DieTemplate")
public class DieTemplate {
	static DieTemplate blank = null;

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	public String id;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
    public Date createdDate;
	
	public DieTemplate() {
	}
}
