package com.antonkharenko.cloudclock;

import java.io.Serializable;

/**
 * This class represents specific time value of logical clock at the given moment of time. This class is
 * immutable. It provides convenient operations for working with logical time.
 *
 * <p>Logical timestamps assigned to each event in the distributed system obey causality, but they can not
 * distinguish concurrent events. Logical timestamps not guaranteed to be ordered or unequal for concurrent
 * events:<pre>
 * E1 -> E2 => timestamp(E1) < timestamp(E2), but
 * timestamp(E1) < timestamp(E2) => {E1 -> E2} or {E1 and E2 are concurrent}</pre>
 *
 * <p>In order to identify concurrent events see {@link com.antonkharenko.cloudclock.VectorTimestamp}.
 *
 * @author Anton Kharenko
 * @see com.antonkharenko.cloudclock.LogicalClock
 * @see com.antonkharenko.cloudclock.VectorTimestamp
 */
public final class LogicalTimestamp implements Comparable<LogicalTimestamp>, Serializable {

	private static final long serialVersionUID = -919934135310565056L;

	// TODO: one cyclic long field is actually enough (remove flip field)
	private final long count;
	private final boolean flip;

	/**
	 * Creates instance of logical timestamp at the initial moment of time.
	 */
	public LogicalTimestamp() {
		this(0L, false);
	}

	/**
	 * Creates instance of logical timestamp at the given logical time.
	 *
	 * @param count logical time counter value
	 */
	public LogicalTimestamp(long count) {
		this(count, false);
	}

	/**
	 * Creates instance of logical timestamp at the given logical time.
	 *
	 * @param count logical time counter value.
	 * @param flip indicates period when time counter was reset to initial value.
	 */
	public LogicalTimestamp(long count, boolean flip) {
		if (count < 0) {
			throw new IllegalArgumentException("Count can't be negative.");
		}
		this.count = count;
		this.flip = flip;
	}

	/**
	 * Returns timestamp which is next in time comparing to this timestamp instance.
	 */
	public LogicalTimestamp nextTimestamp() {
		long nextCount = Math.max(0, count + 1);
		boolean nextFlip = nextCount > count ? flip : !flip; // reverse flip on count reset
		return new LogicalTimestamp(nextCount, nextFlip);
	}

	/**
	 * Converts given byte array into corresponding logical timestamp. It is supposed that given byte array
	 * was produced by {@link LogicalTimestamp#toBytes()} method.
	 */
	public static LogicalTimestamp fromBytes(byte[] bytes) {
		long count = 0;
		boolean flip = bytes[0] < 0;
		for (byte aByte : bytes) {
			count <<= Byte.SIZE;
			count += aByte & 0xFF;
		}
		if (flip) {
			count &= Long.MAX_VALUE;
		}
		return new LogicalTimestamp(count, flip);
	}

	/**
	 * Converts given long value into corresponding logical timestamp. It is supposed that given long
	 * was produced by {@link LogicalTimestamp#toLong()} method.
	 */
	public static LogicalTimestamp fromLong(long longValue) {
		boolean flip = longValue < 0;
		long count = flip ? (longValue & Long.MAX_VALUE) : longValue;
		return new LogicalTimestamp(count, flip);
	}

	/**
	 * Converts this timestamp into a byte array representation. It can be converted
	 * back by {@link LogicalTimestamp#fromBytes(byte[])} method.
	 */
	public byte[] toBytes() {
		byte[] bytes = new byte[8];
		for (int i = 0; i < Long.BYTES; i++) {
			bytes[i] = (byte) (count >> (Long.BYTES - i - 1 << 3));
		}
		if (flip) {
			bytes[0] |= Byte.MIN_VALUE;
		}
		return bytes;
	}

	/**
	 * Converts this timestamp into a byte array representation. It can be converted
	 * back by {@link LogicalTimestamp#fromBytes(byte[])} method.
	 */
	public long toLong() {
		long longValue = count;
		if (flip)
			longValue |= Long.MIN_VALUE;
		return longValue;
	}

	/**
	 * Returns true if the given timestamp is smaller than this timestamp.
	 */
	public boolean isBefore(LogicalTimestamp timestamp) {
		return compareTo(timestamp) < 0;
	}

	/**
	 * Returns true if the given timestamp is bigger than this timestamp.
	 */
	public boolean isAfter(LogicalTimestamp timestamp) {
		return compareTo(timestamp) > 0;
	}

	/**
	 * Compares two logical timestamps.
	 *
	 * @param   that  the logical timestamp to be compared.
	 * @return  the value {@code 0} if this timestamp is happens at same logical time to the argument timestamp;
	 * 			a value less than {@code 0} if this timestamp is smaller than the argument timestamp;
	 * 			and a value greater than {@code 0} if this timestamp is bigger than the argument timestamp.
	 */
	@Override
	public int compareTo(LogicalTimestamp that) {
		if (this.flip == that.flip) {
			return Long.compare(this.count, that.count);
		} else {
			return Long.compare(that.count, this.count);
		}
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null || getClass() != that.getClass()) {
			return false;
		}

		LogicalTimestamp tick = (LogicalTimestamp) that;

		if (count != tick.count) {
			return false;
		}
		if (flip != tick.flip) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (count ^ (count >>> 32));
		result = 31 * result + (flip ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Tick{" +
				"count=" + count +
				", flip=" + flip +
				'}';
	}
}
