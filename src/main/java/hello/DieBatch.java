package hello;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "diebatch")
public class DieBatch {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  long id;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @ElementCollection(targetClass = DieFace.class)
  public List<Die> dice;
}
