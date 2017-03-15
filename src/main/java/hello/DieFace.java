package hello;

import java.awt.Image;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "dieface")
public class DieFace {
  @Transient
  public Image face;
  @Lob
  public byte[] faceBytes;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  private DieFace() {
  }

  public DieFace(Image face) {
    setFace(face);
  }

  @Column(name = "face", nullable = true)
  public Image getFace() {
    return face;
  }

  public void setFace(Image face) {
    this.face = face;
    this.faceBytes = Utils.ImageToByteArray(face);
  }

  @Column(name = "faceBytes", nullable = true)
  public byte[] getFaceBytes() {
    return faceBytes;
  }

  public void setFaceBytes(byte[] faceBytes) {
    this.faceBytes = faceBytes;
    face = Utils.ByteArrayToImage(faceBytes);
  }
}
