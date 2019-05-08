package dice;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

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

	private DieFactory() {
	}

	public void createDieFromTemplate(byte[] file) {
		try {

			Image image = ImageIO.read(new ByteArrayInputStream(file));
			storageService.put("templates/generatedNameTiedToDatabaseObjectDieTemplateId.png", file);
			storageService.put("dieFaces/generatedNameTiedToDatabaseObjectDieFaceId.png",
					Utils.ImageToByteArray(Utils.cutSquare(image, 176, 17, innerSquareLength)));
			storageService.put("dieFaces/generatedNameTiedToDatabaseObjectDieFaceId.png",
					Utils.ImageToByteArray(Utils.cutSquare(image, 17, 176, innerSquareLength)));
			storageService.put("dieFaces/generatedNameTiedToDatabaseObjectDieFaceId.png",
					Utils.ImageToByteArray(Utils.cutSquare(image, 176, 176, innerSquareLength)));
			storageService.put("dieFaces/generatedNameTiedToDatabaseObjectDieFaceId.png",
					Utils.ImageToByteArray(Utils.cutSquare(image, 335, 176, innerSquareLength)));
			storageService.put("dieFaces/generatedNameTiedToDatabaseObjectDieFaceId.png",
					Utils.ImageToByteArray(Utils.cutSquare(image, 176, 335, innerSquareLength)));
			storageService.put("dieFaces/generatedNameTiedToDatabaseObjectDieFaceId.png",
					Utils.ImageToByteArray(Utils.cutSquare(image, 176, 494, innerSquareLength)));

			ArrayList<DieFace> faces = new ArrayList<DieFace>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testService() {
		storageService.put("asdf", new byte[] { 0 });
	}
}
