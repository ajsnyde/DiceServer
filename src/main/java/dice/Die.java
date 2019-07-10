package dice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Die")
public class Die {
	static Die blank = null;

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	public String id;
	
	@OneToMany(cascade = CascadeType.ALL)
	@ElementCollection(targetClass = DieFace.class)
	private List<DieFace> faces = new ArrayList<DieFace>();
	
	@OneToOne(cascade = CascadeType.ALL)
	@MapsId
	private DieTemplate dieTemplate;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
    public Date createdDate;
	
	public static int innerSquare = 125;
	public static int outerSquare = 157;

	public Die() {
	}

	public DieFace getFace(int i) {
		return faces.get(i);
	}

	public List<DieFace> getFaces() {
		return faces;
	}

	public DieTemplate getDieTemplate() {
		return dieTemplate;
	}

	public void setDieTemplate(DieTemplate dieTemplate) {
		this.dieTemplate = dieTemplate;
	}

	public static Die getBlank() {
		if (blank == null) {
			blank = new Die();
			blank.faces = new ArrayList<DieFace>(Collections.nCopies(6, DieFace.blank));
		}
		return blank;
	}
}
