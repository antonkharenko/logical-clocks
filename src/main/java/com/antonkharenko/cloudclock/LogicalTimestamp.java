package com.antonkharenko.cloudclock;

import java.nio.ByteBuffer;

/**
 * @author Anton Kharenko
 */
public final class LogicalTimestamp implements Comparable<LogicalTimestamp> {

	// TODO: write javadoc

	private final long count;
	private final boolean flip;

	public LogicalTimestamp() {
		this(0L, false);
	}

	public LogicalTimestamp(long count) {
		this(count, false);
	}

	public LogicalTimestamp(long count, boolean flip) {
		if (count < 0) {
			throw new IllegalArgumentException("Count can't be negative.");
		}
		this.count = count;
		this.flip = flip;
	}

	public LogicalTimestamp nextTimestamp() {
		long nextCount = Math.max(0, count + 1);
		boolean nextFlip = nextCount > count ? flip : !flip; // reverse flip on count reset
		return new LogicalTimestamp(nextCount, nextFlip);
	}

	@Override
	public int compareTo(LogicalTimestamp that) {
		if (this.flip == that.flip) {
			return Long.compare(this.count, that.count);
		} else {
			return Long.compare(that.count, this.count);
		}
	}

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
