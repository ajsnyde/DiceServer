package hello;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Utils {
  // takes in image, returns cut portion
  public static Image cut(Image image, int x, int y, int width, int height) {
    ImagePlus map = new ImagePlus("", image);
    map.setRoi(x, y, width, height);
    ImageProcessor ip = map.getProcessor();
    ip = ip.crop();
    return ip.getBufferedImage();
  }

  // Pastes clipboard to image at the parametered coordinates. No resizing or checks
  public static Image paste(Image image, Image clipboard, int x, int y) {
    ImagePlus base = new ImagePlus("", image);
    ImagePlus paste = new ImagePlus("", image);
    ImageProcessor baseProcessor = base.getProcessor();
    ImageProcessor pasteProcessor = paste.getProcessor();
    baseProcessor.insert(pasteProcessor, x, y);
    return baseProcessor.getBufferedImage();
  }

  public static byte[] ImageToByteArray(Image image) {
    ImagePlus map = new ImagePlus("", image);
    BufferedImage originalImage = map.getBufferedImage();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] imageInByte = null;
    try {
      ImageIO.write(originalImage, "jpg", baos);
      baos.flush();
      imageInByte = baos.toByteArray();
      baos.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return imageInByte;
  }

  public static BufferedImage ImageToBufferedImage(Image image) {
    ImagePlus imageplus = new ImagePlus();
    imageplus.setImage(image);
    return imageplus.getBufferedImage();
  }

  public static Image ByteArrayToImage(byte[] image) {
    BufferedImage img = null;
    try {
      img = ImageIO.read(new ByteArrayInputStream(image));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return img;
  }

  public static Image readImage(File file) {
    BufferedImage img = null;
    try {
      img = ImageIO.read(new File("strawberry.jpg"));
    } catch (IOException e) {
    }
    return img;
  }

  public static File convert(MultipartFile file) {
    File convFile = new File(file.getOriginalFilename());
    FileOutputStream fos;
    try {
      convFile.createNewFile();
      fos = new FileOutputStream(convFile);
      fos.write(file.getBytes());
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return convFile;
  }
}
