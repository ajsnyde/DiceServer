package diceServer.dice;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import diceServer.app.Application;
import diceServer.app.Utils;
import ij.ImagePlus;

public class SimpleCompiler implements BatchCompilerStrategy {

  // static might be a mistake here..
  static int maxDice = 50;

  @Override
  public DieBatch compile() {
    DieBatch batch = new DieBatch();
    List<Die> batchDice = batch.dice;

    // pool of diceJobs that aren't complete
    ArrayList<DieJob> dieJobs = new ArrayList<DieJob>();
    Application.dieJobRepo.findByCompletion().iterator().forEachRemaining(dieJobs::add);

    int numDice = 0;
    // go through jobs until no more are available or maxDice is hit - this latter clause is not redundant, and improves efficiency
    for (int i = 0; i < dieJobs.size() && numDice != maxDice; ++i) {
      DieJob job = dieJobs.get(i);
      // add dice from job until no more are required from the job or maxDice is hit
      for (; job.quantityLeft > 0 && numDice != maxDice; job.quantityLeft--) {
        batchDice.add(job.die);
        Application.dieJobRepo.save(job);
      }
    }
    batch.faces = getImages(new ArrayList<Die>(batch.dice));
    return batch;
  }

  private ArrayList<Image> getImages(ArrayList<Die> dice) {
    int rows = 5;
    int cols = 5;
    ArrayList<Image> images = new ArrayList<Image>();

    BufferedImage b_img = new BufferedImage(Die.innerSquare * rows, Die.innerSquare * cols, 1);
    Graphics2D graphics = b_img.createGraphics();
    graphics.setPaint(new Color(255, 255, 255));
    graphics.fillRect(0, 0, b_img.getWidth(), b_img.getHeight());

    for (int k = 0; k < 6; k++) {
      ImagePlus img = new ImagePlus("Side #" + k, b_img);
      for (int i = 0; i < rows; ++i)
        for (int j = 0; j < cols && (((i * rows) + j) < dice.size()); ++j) {
          img.setImage(Utils.paste(img.getImage(), dice.get((i * rows) + j).getFace(k).getFace(), j * Die.innerSquare, i * Die.innerSquare));
        }
      images.add(img.getImage());
    }
    return images;
  }
}
