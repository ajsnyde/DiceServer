package hello;

import java.awt.Image;
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "diebatch")
public class DieBatch {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  long id;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @ElementCollection(targetClass = DieFace.class)
  public List<Die> dice;
  @JsonIgnore
  @Transient
  public ArrayList<Image> faces; // 6 compilations of each face of each die - each to be printed

  DieBatch() {
    dice = new ArrayList<Die>();
  }
}
