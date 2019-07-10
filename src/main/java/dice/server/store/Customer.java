package dice.server.store;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import dice.DieOrder;

@Entity
@Table(name = "customer")
public class Customer extends com.stripe.model.Customer {
	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid")
	@GeneratedValue(generator = "uuid-gen")
	public String id;
	
	String name;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@ElementCollection(targetClass = DieOrder.class)
	private List<DieOrder> orders = new ArrayList<DieOrder>();

	String address;

}
