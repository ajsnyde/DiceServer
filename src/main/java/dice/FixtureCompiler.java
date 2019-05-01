package dice;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import dice.server.app.Application;
import dice.server.app.Utils;
import ij.ImagePlus;

public class FixtureCompiler implements BatchCompilerStrategy {

  public enum FixtureType {
    ROWBYROWCELL, ROWBYROWGLOBAL
  }

  Fixture fixture;
  int maxDice = 0;

  public FixtureCompiler(FixtureType type) {
    switch (type) {
    case ROWBYROWCELL:
      this.fixture = new Fixture(new File("fixture1.json"));
      break;
    case ROWBYROWGLOBAL:
      this.fixture = new Fixture(new File("fixture2.json"));
      break;
    default: 
      throw new IllegalArgumentException("No such fixture type!");
    }
    maxDice = fixture.getMaxDice();
  }

  @Override
  public DieBatch compile() {
    DieBatch batch = new DieBatch();
    ArrayList<Long> batchDice = batch.dice;

    // pool of diceJobs that aren't complete
    ArrayList<DieJob> dieJobs = new ArrayList<DieJob>();
    Application.dieJobRepo.findByCompletion().iterator().forEachRemaining(dieJobs::add);

    int numDice = 0;
    // go through jobs until no more are available or maxDice is hit - this latter clause is not redundant, and improves efficiency
    for (int i = 0; i < dieJobs.size() && numDice != maxDice; ++i) {
      DieJob job = dieJobs.get(i);
      // add dice from job until no more are required from the job or maxDice is hit
      for (; job.quantityLeft > 0 && numDice != maxDice; job.quantityLeft--) {
        batchDice.add(job.die.id);
        Application.dieJobRepo.save(job);
      }
    }
    batch.faces = getImages(new ArrayList<Long>(batch.dice));
    return batch;
  }

  private ArrayList<Image> getImages(ArrayList<Long> dice) {
    ArrayList<Image> images = new ArrayList<Image>();

    BufferedImage b_img = new BufferedImage(fixture.xSize, fixture.ySize, 1);
    Graphics2D graphics = b_img.createGraphics();
    graphics.setPaint(new Color(255, 255, 255));
    graphics.fillRect(0, 0, b_img.getWidth(), b_img.getHeight());

    for (int k = 0; k < 6; k++) {
      ImagePlus img = new ImagePlus("Side #" + k, b_img);
      for (int i = 0; i < dice.size() && i < fixture.getMaxDice(); ++i)
        img.setImage(Utils.paste(img.getImage(), Application.dieRepo.findOne(dice.get(i)).getFace(k).getFace(), fixture.positions.get(i).x, fixture.positions.get(i).y));
      images.add(img.getImage());
    }
    return images;
  }
}
