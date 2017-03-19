package hello;

import java.util.ArrayList;
import java.util.List;

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
      }
    }
    return batch;
  }

}
