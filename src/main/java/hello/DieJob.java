package hello;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DieJob")
public class DieJob implements OrderItem {
  @OneToOne(cascade = CascadeType.ALL)
  public Die die;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;
  public int quantity;
  @Column(name = "quantityLeft")
  public int quantityLeft;
  public double cost;
  @ManyToOne(cascade = CascadeType.ALL)
  private DieOrder order;

  public DieJob() {
    cost = 0;
  }

  public DieJob(Die die, int quantity) {
    this.die = die;
    this.quantity = this.quantityLeft = quantity;
    this.cost = quantity * 3.00; // default cost per die - temporary
  }

  @Override
  public double getCost() {
    return cost;
  }
}
