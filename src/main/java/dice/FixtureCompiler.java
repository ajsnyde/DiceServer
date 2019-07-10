package dice;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
			this.fixture = new Fixture("rowByRowCellGlobal.json");
			break;
		case ROWBYROWGLOBAL:
			this.fixture = new Fixture("rowByRowGlobal.json");
			break;
		default:
			throw new IllegalArgumentException("No such fixture type!");
		}
		maxDice = fixture.getMaxDice();
	}

	/**
	 * Create a DieBatch using the entire job pool, in order, until A) out of jobs, or B) out of space on the fixture
	 * This method can use/leave partial jobs
	 */
	@Override
	public DieBatch compile() {
		DieBatch batch = new DieBatch();
		ArrayList<String> batchDice = batch.dice;

		// pool of diceJobs that aren't complete
		ArrayList<DieJob> dieJobs = new ArrayList<DieJob>();
		Application.dieJobRepo.findByCompletion().iterator().forEachRemaining(dieJobs::add);

		int numDice = 0;
		// go through jobs until no more are available or maxDice is hit - this latter
		// clause is not redundant, and improves efficiency
		for (int i = 0; i < dieJobs.size() && numDice != maxDice; ++i) {
			DieJob job = dieJobs.get(i);
			// add dice from job until no more are required from the job or maxDice is hit
			for (; job.quantityLeft > 0 && numDice != maxDice; job.quantityLeft--) {
				batchDice.add(job.die.id);
				Application.dieJobRepo.save(job);
			}
		}
		batch.faces = getImages(new ArrayList<String>(batch.dice));
		return batch;
	}
	
	/**
	 * Create a DieBatch using the provided jobs, in order, until A) out of jobs, or B) out of space on the fixture
	 * This method can use/leave partial jobs
	 */
	@Override
	public DieBatch compile(List<DieJob> jobs) {
		DieBatch batch = new DieBatch();
		int numDice = 0;
		// go through jobs until no more are available or maxDice is hit
		for(DieJob job: jobs) {
			int diceToAdd = Math.min(maxDice - numDice, job.quantityLeft);
			if(diceToAdd == 0)
				break;
			
			batch.dice.addAll(Collections.nCopies(diceToAdd, job.die.id));
			numDice += diceToAdd;
			job.quantityLeft -= diceToAdd;
			Application.dieJobRepo.save(job);
		}

		batch.faces = getImages(new ArrayList<String>(batch.dice));
		return batch;
	}

	private ArrayList<Image> getImages(ArrayList<String> dice) {
		ArrayList<Image> images = new ArrayList<Image>();

		BufferedImage b_img = new BufferedImage(fixture.xSize, fixture.ySize, 1);
		Graphics2D graphics = b_img.createGraphics();
		graphics.setPaint(new Color(255, 255, 255));
		graphics.fillRect(0, 0, b_img.getWidth(), b_img.getHeight());

		for (int k = 0; k < 6; k++) {
			ImagePlus img = new ImagePlus("Side #" + k, b_img);
			for (int i = 0; i < dice.size() && i < fixture.getMaxDice(); ++i)
				img.setImage(Utils.paste(img.getImage(), Utils.ByteArrayToImage(Application.storageService.get("dieFaces/" + Application.dieRepo.findOne(dice.get(i)).getFace(k).id + ".png")),
						fixture.positions.get(i).x, fixture.positions.get(i).y));
			images.add(img.getImage());
		}
		return images;
	}
}
