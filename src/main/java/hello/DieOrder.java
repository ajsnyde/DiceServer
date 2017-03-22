package hello;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "dieOrder")
public class DieOrder extends Order implements OrderItem {
  @OneToMany(cascade = CascadeType.ALL)
  @ElementCollection(targetClass = DieJob.class)
  private List<DieJob> jobs = new ArrayList<DieJob>();

  @Override
  public double getCost() {
    return jobs.stream().collect(Collectors.summingDouble(sc -> sc.getCost()));
  }
}
