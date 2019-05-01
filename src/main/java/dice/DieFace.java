package dice;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dice.server.app.Utils;

@Entity
@Table(name = "DieFace")
public class DieFace {
	static DieFace blank = null;

	@Transient
	@JsonIgnore
	private Image face;
	@Lob
	@JsonIgnore
	private byte[] faceBytes;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;

	private DieFace() {
	}

	// takes face as inner portion of face, centering it within overall face
	// dimensions.
	public DieFace(Image face) {
		BufferedImage face2 = new BufferedImage(Die.outerSquare, Die.outerSquare, BufferedImage.TYPE_3BYTE_BGR);
		face2.getGraphics().setColor(new Color(255, 255, 255));
		face2.getGraphics().fillRect(0, 0, Die.outerSquare, Die.outerSquare);
		Utils.paste(face2, face, (Die.outerSquare - Die.innerSquare) / 2, (Die.outerSquare - Die.innerSquare) / 2);
		setFace(face2);
	}

	@Column(name = "face", nullable = true)
	public Image getFace() {
		face = Utils.ByteArrayToImage(faceBytes);
		return face;
	}

	public void setFace(Image face) {
		this.faceBytes = Utils.ImageToByteArray(face);
		this.face = face;
	}

	public void setFaceBytes(byte[] faceBytes) {
		face = Utils.ByteArrayToImage(faceBytes);
		this.faceBytes = faceBytes;

	}

	@Column(name = "faceBytes", nullable = true)
	public byte[] getFaceBytes() {
		return faceBytes;
	}

	public static DieFace getBlank() {
		if (blank == null) {
			BufferedImage b_img = new BufferedImage(Die.outerSquare, Die.outerSquare, 0);
			Graphics2D graphics = b_img.createGraphics();
			graphics.setPaint(new Color(255, 255, 255));
			graphics.fillRect(0, 0, b_img.getWidth(), b_img.getHeight());
			blank = new DieFace(b_img);
		}
		return blank;
	}
}
