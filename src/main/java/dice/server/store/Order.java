package dice.server.store;

import java.sql.Time;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public abstract class Order implements OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;
  @ManyToOne(cascade = CascadeType.ALL)
  public Customer customer;

  public Time dateCreated;
}
