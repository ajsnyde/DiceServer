package diceServer.dice;

import java.awt.Image;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import diceServer.app.Utils;

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

  public DieBatch() {
    dice = new ArrayList<Die>();
  }

  public void zip(String zipName) {
    try {
      FileOutputStream fos = new FileOutputStream(zipName);
      ZipOutputStream zos = new ZipOutputStream(fos);
      for (Image image : faces) {
        ZipEntry ze = new ZipEntry("image" + image.hashCode() + ".png");
        zos.putNextEntry(ze);
        zos.write(Utils.ImageToByteArray(image));
        zos.closeEntry();
      }
      zos.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
