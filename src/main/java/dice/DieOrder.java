package dice;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

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
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	public String id;
	
	@OneToMany(cascade = CascadeType.REMOVE)
	@JoinColumn
	public List<DieJob> jobs;
	
	public Time lastAccessed;
	
	@Enumerated(EnumType.STRING)
	public Type type = Type.CART;

	// SessionId associated with this order (cart)
	private String sessionId;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
    public Date createdDate;

	public DieOrder() {
		jobs = new ArrayList<DieJob>();
	}

	public DieOrder(String session) {
		jobs = new ArrayList<DieJob>();
		this.sessionId = session;
	}

	@Override
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
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	String getId() {
		return id;
	}

	void setId(String id) {
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

	public void removeJob(String id) {
		jobs.removeIf((DieJob j) -> j.id.equals(id));
		Application.dieOrderRepo.save(this);
	}
}
