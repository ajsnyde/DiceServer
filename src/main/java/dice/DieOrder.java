package dice;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import dice.server.app.Application;
import dice.server.store.Order;
import dice.server.store.OrderItem;

@Entity
@Table(name = "DieOrder")
public class DieOrder extends Order implements OrderItem {

  public enum Type {
    CART, PAIDFOR
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;
  @OneToMany(cascade = CascadeType.REMOVE)
  @JoinColumn
  public List<DieJob> jobs;
  public Time lastAccessed;
  @Enumerated(EnumType.STRING)
  public Type type = Type.CART;

  // SessionId associated with this order (cart)
  private String sessionId;

  public DieOrder() {
    jobs = new ArrayList<DieJob>();
  }

  public DieOrder(String session) {
    jobs = new ArrayList<DieJob>();
    this.sessionId = session;
  }

  @Transient
  public double getCost() {
    return getJobs().stream().collect(Collectors.summingDouble(sc -> sc.getCost()));
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
    return id;
  }

  void setId(long id) {
    this.id = id;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @ElementCollection(targetClass = DieJob.class)
  public List<DieJob> getJobs() {
    return jobs;
  }

  public void setJobs(List<DieJob> jobs) {
    this.jobs = jobs;
  }

  public void removeJob(long id) {
    jobs.removeIf((DieJob j) -> j.id == id);
    Application.dieOrderRepo.save(this);
  }
}
