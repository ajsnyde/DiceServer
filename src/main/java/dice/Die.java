package dice;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dice.server.app.Utils;

@Entity
@Table(name = "Die")
public class Die {
	static Die blank = null;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;
	@OneToMany(cascade = CascadeType.ALL)
	@ElementCollection(targetClass = DieFace.class)
	private List<DieFace> faces = new ArrayList<DieFace>();
	@Transient
	@JsonIgnore
	private Image map; // initial map file; should be tied to the mapBytes
	@Lob
	@JsonIgnore
	private byte[] mapBytes;
	public String color;

	public static int innerSquare = 125;
	public static int outerSquare = 157;

	private Die() {
		if (mapBytes != null)
			map = Utils.ByteArrayToImage(mapBytes);
	}

	public Die(Image map) {
		this.map = map;
		faces.add(new DieFace(Utils.cutSquare(map, 176, 17, innerSquare)));
		faces.add(new DieFace(Utils.cutSquare(map, 17, 176, innerSquare)));
		faces.add(new DieFace(Utils.cutSquare(map, 176, 176, innerSquare)));
		faces.add(new DieFace(Utils.cutSquare(map, 335, 176, innerSquare)));
		faces.add(new DieFace(Utils.cutSquare(map, 176, 335, innerSquare)));
		faces.add(new DieFace(Utils.cutSquare(map, 176, 494, innerSquare)));
		color = "White";
	}

	public Die(Image map, String color) {
		this(map);
		this.color = color;
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
