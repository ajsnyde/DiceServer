package diceServer.app;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

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
    image.getGraphics().drawImage(clipboard, x, y, null);
    return image;
  }

  public static byte[] ImageToByteArray(Image image) {
    ImagePlus map = new ImagePlus("", image);
    BufferedImage originalImage = map.getBufferedImage();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] imageInByte = null;
    try {
      ImageIO.write(originalImage, "bmp", baos);
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

  public static void charge(String token, int amount) {

    Stripe.apiKey = "sk_test_KxLUHNW5j4SgB2IKOgRnGPwK"; // TEST ONLY - MUST BE REPLACED IN PROD

    Map<String, Object> chargeParams = new HashMap<String, Object>();
    chargeParams.put("amount", amount);
    chargeParams.put("currency", "usd");
    chargeParams.put("description", "Charge for liam.davis@example.com");
    chargeParams.put("source", token);

    RequestOptions options = RequestOptions.builder().setIdempotencyKey(new BigInteger(130, new SecureRandom()).toString(32)).build();

    try {
      Charge.create(chargeParams, options);
    } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
      e.printStackTrace();
    }
  }

  public static String sanitizeFilename(String filename) {
    return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
  }
}
