package com.antonkharenko.logicalclocks;

import java.util.Arrays;

/**
 * This class represents specific time value of vector clock at the given moment of time. This class
 * is immutable. It provides convenient operations for working with vector time. In compare to
 * LocalTimestamp it can identify concurrent events by using more space.
 *
 * @author Anton Kharenko
 * @see com.antonkharenko.logicalclocks.LogicalTimestamp
 */
public final class VectorTimestamp {

  private final LogicalTimestamp[] timestamps;

  /**
   * Creates vector clock of given length with default initial timestamps.
   */
  public VectorTimestamp(int vectorLength) {
    timestamps = new LogicalTimestamp[vectorLength];
    for (int i = 0; i < vectorLength; i++) {
      timestamps[i] = new LogicalTimestamp();
    }
  }

  /**
   * Creates vector clock by given array of timestamps.
   */
  public VectorTimestamp(LogicalTimestamp[] timestamps) {
    this.timestamps = Arrays.copyOf(timestamps, timestamps.length);
  }

  /**
   * Returns new timestamp which is in happens after relation to current timestamp taking into
   * account given local process id.
   */
  public VectorTimestamp nextTimestamp(int localIndex) {
    if (localIndex < 0 || localIndex >= timestamps.length)
      throw new IllegalArgumentException("Index out of bounds");

    LogicalTimestamp[] newTimestamps = Arrays.copyOf(timestamps, timestamps.length);
    newTimestamps[localIndex] = newTimestamps[localIndex].nextTimestamp();

    return new VectorTimestamp(newTimestamps);
  }

  /**
   * Returns new timestamp which is in happens after relation to both given timestamp and current
   * timestamp taking into account given local process id.
   */
  public VectorTimestamp nextTimestamp(int localIndex, VectorTimestamp happensBeforeTimestamp) {
    if (localIndex < 0 || localIndex >= timestamps.length)
      throw new IllegalArgumentException("Index out of bounds.");
    if (timestamps.length != happensBeforeTimestamp.timestamps.length)
      throw new IllegalArgumentException("Timestamp vectors length do not match.");

    LogicalTimestamp[] newTimestamps = Arrays.copyOf(timestamps, timestamps.length);
    newTimestamps[localIndex] = newTimestamps[localIndex].nextTimestamp();
    for (int i = 0; i < newTimestamps.length; i++) {
      if (i != localIndex && newTimestamps[i].isBefore(happensBeforeTimestamp.timestamps[i])) {
        newTimestamps[i] = happensBeforeTimestamp.timestamps[i];
      }
    }

    return new VectorTimestamp(newTimestamps);
  }

  /**
   * Returns true if current timestamp happens before given timestamp and timestamps are in a causal
   * relation. In case of false result it either can correspond to the one of possible situations:
   * timestamps are equal, timestamps are concurrent or this timestamp happens after the given
   * timestamp.
   */
  public boolean isHappensBefore(VectorTimestamp that) {
    return compare(that) == Relation.HAPPENS_BEFORE;
  }

  /**
   * Returns true if current timestamp happens after given timestamp and timestamps are in a causal
   * relation. In case of false result it either can correspond to the one of possible situations:
   * timestamps are equal, timestamps are concurrent or this timestamp happens before the given
   * timestamp.
   */
  public boolean isHappensAfter(VectorTimestamp that) {
    return compare(that) == Relation.HAPPENS_AFTER;
  }

  /**
   * Returns true if current timestamp happens concurrently and there is no causal relation between
   * them. In case of false result it either can correspond to the one of possible situations:
   * timestamps are equal, timestamps are in causal relation.
   */
  public boolean isConcurrent(VectorTimestamp that) {
    return compare(that) == Relation.HAPPENS_BEFORE;
  }

  /**
   * Compares two vector timestamps and defines relation between them. Two timestamps may be equal,
   * concurrent or in causal (happens-before) relation.
   *
   * @param that given vector timestamp to compare
   * @return {@code Relation} between current timestamp and the given one.
   * @see com.antonkharenko.logicalclocks.Relation
   */
  public Relation compare(VectorTimestamp that) {
    if (timestamps.length != that.timestamps.length)
      throw new IllegalArgumentException("Timestamp vectors length do not match.");

    Relation relation = Relation.EQUAL;
    for (int i = 0; i < timestamps.length; i++) {
      if (this.timestamps[i].isBefore(that.timestamps[i])) {
        if (relation == Relation.HAPPENS_AFTER)
          return Relation.CONCURRENT;
        relation = Relation.HAPPENS_BEFORE;
      } else if (this.timestamps[i].isAfter(that.timestamps[i])) {
        if (relation == Relation.HAPPENS_BEFORE)
          return Relation.CONCURRENT;
        relation = Relation.HAPPENS_AFTER;
      }
    }

    return relation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VectorTimestamp that = (VectorTimestamp) o;
    return Arrays.equals(timestamps, that.timestamps);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(timestamps);
  }

  @Override
  public String toString() {
    return Arrays.toString(timestamps);
  }
}
