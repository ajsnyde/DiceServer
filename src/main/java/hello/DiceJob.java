package hello;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import hello.storage.StorageService;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class DiceJob {
  ImagePlus image;
  StorageService storage;
  public String baseName;
  File originalFile;
  int square = 200;

  public DiceJob(File file, StorageService storage) {
    this.storage = storage;
    originalFile = file;
    int num = 1;

    baseName = file.getName().replaceAll("\\.[.]*", "");

    cut(file, square, 0, square, square, baseName + num++);
    cut(file, 0, square, square, square, baseName + num++);
    cut(file, square, square, square, square, baseName + num++);
    cut(file, 2 * square, square, square, square, baseName + num++);
    cut(file, square, 2 * square, square, square, baseName + num++);
    cut(file, square, 3 * square, square, square, baseName + num++);
  }

  public void cut(File file, int x, int y, int width, int height, String filename) {
    image = new ImagePlus(file.getAbsolutePath());
    image.setRoi(x, y, width, height);
    ImageProcessor ip = image.getProcessor();
    ip = ip.crop();

    BufferedImage image1 = ip.getBufferedImage();
    try {
      file = new File(filename + ".bmp");
      ImageIO.write(image1, "BMP", file);
      storage.store(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
