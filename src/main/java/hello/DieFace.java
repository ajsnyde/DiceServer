package hello;

import java.awt.Image;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "dieface")
public class DieFace {
  @Transient
  public Image face;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  private DieFace() {
  }

  public DieFace(Image face) {
    this.face = face;
  }

  @Column(name = "face", nullable = true)
  public byte[] getFace() {
    return Utils.ImageToByteArray(face);
  }

  public void setFace(byte[] face) {
    this.face = Utils.ByteArrayToImage(face);
  }
}
