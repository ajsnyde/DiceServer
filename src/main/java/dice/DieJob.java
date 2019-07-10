package dice;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import dice.server.store.OrderItem;

@Entity
@Table(name = "DieJob")
public class DieJob implements OrderItem {
	@OneToOne(cascade = CascadeType.ALL)
	public Die die;
	
	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	public String id;
	
	public int quantity;
	
	@Column(name = "quantityLeft")
	public int quantityLeft;
	
	public double cost;

	@ManyToOne(cascade = CascadeType.ALL) // - deletes entire order...
	@JoinColumn
	public DieOrder order;

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

	public Die getDie() {
		return die;
	}

	public void setDie(Die die) {
		this.die = die;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getQuantityLeft() {
		return quantityLeft;
	}

	public void setQuantityLeft(int quantityLeft) {
		this.quantityLeft = quantityLeft;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
}
