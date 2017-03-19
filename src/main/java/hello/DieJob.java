package hello;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "diejob")
public class DieJob {
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  Die die;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  long id;
  int quantity;
  int quantityLeft;
  double cost;
}
