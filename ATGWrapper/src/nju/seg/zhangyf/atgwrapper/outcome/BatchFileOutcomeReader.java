package nju.seg.zhangyf.atgwrapper.outcome;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public final class BatchFileOutcomeReader {

  public static void main(final String[] args) {
    try (final FileInputStream fi = new FileInputStream("/root/lffResult/FSE/CoralNoExtra-tsc-Cycle1,Num200/batchResultBinary");
        final ObjectInputStream oi = new ObjectInputStream(fi)) {
      @SuppressWarnings({ "unused", "unchecked" }) 
      final BatchFileOutcome<BranchCoverageTestOutcome> outcome = (BatchFileOutcome<BranchCoverageTestOutcome>) oi.readObject();
      System.out.println(outcome);
    } catch (final IOException | ClassNotFoundException ignored) {
      System.out.println(ignored);
    }

    return;
  }
}
