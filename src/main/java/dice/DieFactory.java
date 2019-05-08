package dice;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dice.server.app.Utils;
import dice.server.storage.AWSFileSystemStorageService;

@Component
public class DieFactory {

	@Autowired
	AWSFileSystemStorageService storageService;

	public static int innerSquareLength = 125;
	public static int outerSquareLength = 157;
	public static int[] templateXValues = { 176, 17, 176, 335, 176, 176 };
	public static int[] templateYValues = { 17, 176, 176, 176, 335, 494 };

	private DieFactory() {
	}

	public Die createDieFromTemplate(byte[] file) {
		try {
			Image template = ImageIO.read(new ByteArrayInputStream(file));
			DieTemplate dieTemplate = new DieTemplate();
			storageService.put("templates/" + dieTemplate.id + ".png", file);

			Die die = new Die();
			die.setDieTemplate(dieTemplate);
			List<DieFace> faces = die.getFaces();

			for (int i = 0; i < 5; i++) {
				DieFace face = new DieFace();
				faces.add(face);
				storageService.put("dieFaces/" + face.id + ".png", Utils.ImageToByteArray(isolateDieFace(template, templateXValues[i], templateYValues[i])));
			}
			return die;

		} catch (IOException e) {
			e.printStackTrace();
			throw new DieFactoryException("Couldn't create die: " + e.getLocalizedMessage());
		}
	}

	// takes a template, cuts out the square provided, centers it on a proper sized
	// square
	public Image isolateDieFace(Image source, int x, int y) {
		Image innerFace = Utils.cutSquare(source, x, y, innerSquareLength);
		return sanitizeDieFace(innerFace);
	}

	// takes an inner face and pastes it (centered) onto a proper size square
	public Image sanitizeDieFace(Image face) {
		BufferedImage blank = new BufferedImage(Die.outerSquare, Die.outerSquare, BufferedImage.TYPE_3BYTE_BGR);
		blank.getGraphics().setColor(new Color(255, 255, 255));
		blank.getGraphics().fillRect(0, 0, Die.outerSquare, Die.outerSquare);
		return Utils.paste(blank, face, (Die.outerSquare - Die.innerSquare) / 2, (Die.outerSquare - Die.innerSquare) / 2);
	}

	public void testService() {
		storageService.put("asdf", new byte[] { 0 });
	}
}
