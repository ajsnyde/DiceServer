package diceServer.dice;

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
import diceServer.store.Order;
import diceServer.store.OrderItem;

@Entity
@Table(name = "DieOrder")
public class DieOrder extends Order implements OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  public List<DieJob> jobs = new ArrayList<DieJob>();

  // SessionId associated with this order (cart)
  private String sessionId;

  public DieOrder() {
  }

  public DieOrder(String session) {
    this.sessionId = session;
  }

  @Override
  @Transient
  public double getCost() {
    return jobs.stream().collect(Collectors.summingDouble(sc -> sc.getCost()));
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  long getId() {
    return super.id;
  }

  void setId(long id) {
    super.id = id;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @ElementCollection(targetClass = DieJob.class)
  public List<DieJob> getJobs() {
    return jobs;
  }

  public void setJobs(List<DieJob> jobs) {
    this.jobs = jobs;
  }
}
