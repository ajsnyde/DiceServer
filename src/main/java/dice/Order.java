package dice;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import app.Customer;

public abstract class Order implements OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;
  @ManyToOne(cascade = CascadeType.ALL)
  private Customer customer;
}
