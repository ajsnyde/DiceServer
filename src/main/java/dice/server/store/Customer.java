package dice.server.store;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import dice.DieOrder;

@Entity
@Table(name = "customer")
public class Customer extends com.stripe.model.Customer {
	String name;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@ElementCollection(targetClass = DieOrder.class)
	private List<DieOrder> orders = new ArrayList<DieOrder>();

	String address;

}
