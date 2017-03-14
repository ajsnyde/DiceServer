package hello;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "die")
public class Die {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @ElementCollection(targetClass = DieFace.class)
  public List<DieFace> faces = new ArrayList<DieFace>();
  @Transient
  public Image map; // initial map file
  public int square = 200;

  private Die() {
  }

  public Die(Image map) {
    this.map = map;
    faces.add(new DieFace(Utils.cut(map, square, 0, square, square)));
    faces.add(new DieFace(Utils.cut(map, 0, square, square, square)));
    faces.add(new DieFace(Utils.cut(map, square, square, square, square)));
    faces.add(new DieFace(Utils.cut(map, 2 * square, square, square, square)));
    faces.add(new DieFace(Utils.cut(map, square, 2 * square, square, square)));
    faces.add(new DieFace(Utils.cut(map, square, 3 * square, square, square)));
  }

  @Column(name = "map", nullable = true)
  public byte[] getMap() {
    return Utils.ImageToByteArray(map);
  }

  public void setMap(byte[] map) {
    this.map = Utils.ByteArrayToImage(map);
  }
}
