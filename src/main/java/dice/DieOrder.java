package dice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import store.Order;
import store.OrderItem;

@Entity
@Table(name = "dieOrder")
public class DieOrder extends Order implements OrderItem {
  @OneToMany(cascade = CascadeType.ALL)
  @ElementCollection(targetClass = DieJob.class)
  private List<DieJob> jobs = new ArrayList<DieJob>();

  @Override
  @Transient
  public double getCost() {
    return jobs.stream().collect(Collectors.summingDouble(sc -> sc.getCost()));
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  long getId() {
    return super.id;
  }

  void setId(long id) {
    super.id = id;
  }
}
