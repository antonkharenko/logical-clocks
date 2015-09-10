package com.antonkharenko.logicalclocks;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This class implements vector clock abstraction. Vector clock is a mechanism for capturing
 * chronological and causal relationships between events and can detect concurrent events in a
 * distributed system. It takes as assumption that number of processes in the system keeps
 * unchanged as well as their index order and the local process index is provided.
 *
 * <p>
 * This implementation provides methods to store and update local vector time in a thread safe and
 * non-blocking way.
 *
 * @author Anton Kharenko
 * @see com.antonkharenko.logicalclocks.VectorTimestamp
 */
public class VectorClock {

  private final AtomicReference<VectorTimestamp> timeReference = new AtomicReference<>();
  private final int processIndex;

  /**
   * Creates instance of logical clock with default initial timestamps and given local process index
   * and the number of processes in a distributed system.
   */
  public VectorClock(int processIndex, int processCount) {
    this(processIndex, new VectorTimestamp(processCount));
  }

  /**
   * Creates instance of logical clock with the given initial timestamps and given local process
   * index.
   */
  public VectorClock(int processIndex, VectorTimestamp initialTimestamp) {
    this.timeReference.set(initialTimestamp);
    this.processIndex = processIndex;
  }

  /**
   * Returns current value of the clock.
   */
  public VectorTimestamp time() {
    return timeReference.get();
  }

  /**
   * Increments the clock time and returns newly set value of the clock.
   *
   * @return New value of the clock.
   */
  public VectorTimestamp tick() {
    VectorTimestamp previousTimestamp, nextTimestamp;
    do {
      previousTimestamp = timeReference.get();
      nextTimestamp = previousTimestamp.nextTimestamp(processIndex);
    } while (!timeReference.compareAndSet(previousTimestamp, nextTimestamp));
    return nextTimestamp;
  }

  /**
   * Increments the value of the clock taking into account that provided timestamp happens before
   * that moment. Returns new value of the clock which happens after previous value of the clock and
   * provided timestamp.
   *
   * @param happensBeforeTimestamp timestamp value which happens in the past
   * @return New value of the clock.
   */
  public VectorTimestamp tick(VectorTimestamp happensBeforeTimestamp) {
    VectorTimestamp previousTimestamp, nextTimestamp;
    do {
      previousTimestamp = timeReference.get();
      nextTimestamp = previousTimestamp.nextTimestamp(processIndex, happensBeforeTimestamp);
    } while (!timeReference.compareAndSet(previousTimestamp, nextTimestamp));
    return nextTimestamp;
  }
}
