package dice.server.store;

import java.sql.Time;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

public abstract class Order implements OrderItem {
	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	public String id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	public Customer customer;

	public Time dateCreated;
}
