package hello;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "die")
public class Die {
  static Die blank = null;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonView(Long.class)
  public long id;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @ElementCollection(targetClass = DieFace.class)
  private List<DieFace> faces = new ArrayList<DieFace>();
  @Transient
  @JsonIgnore
  private Image map; // initial map file; should be tied to the mapBytes
  @Lob
  @JsonIgnore
  private byte[] mapBytes;
  public static int square = 200;

  private Die() {
    if (mapBytes != null)
      map = Utils.ByteArrayToImage(mapBytes);
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

  public void setMap(byte[] mapBytes) {
    this.map = Utils.ByteArrayToImage(mapBytes);
    this.mapBytes = mapBytes;
  }

  public void setMap(Image map) {
    mapBytes = Utils.ImageToByteArray(map);
    this.map = map;
  }

  public Image getMap() {
    if (map == null)
      this.map = Utils.ByteArrayToImage(mapBytes);
    return map;
  }

  public DieFace getFace(int i) {
    return faces.get(i);
  }

  public List<DieFace> getFaces() {
    return faces;
  }

  @Column(name = "mapBytes", nullable = true)
  public byte[] getMapBytes() {
    return mapBytes;
  }

  public static Die getBlank() {
    if (blank == null) {
      blank = new Die();
      blank.faces = new ArrayList<DieFace>(Collections.nCopies(6, DieFace.blank));
    }
    return blank;
  }
}
