package com.antonkharenko.cloudclock;

import com.sun.istack.internal.NotNull;

/**
 * @author Anton Kharenko
 */
public final class Tick implements Comparable<Tick> {

	private final long timestamp;
	// TODO: make infinite clock
	// private final boolean flip;

	public Tick(long timestamp/*, boolean flip*/) {
		this.timestamp = timestamp;
		//this.flip = flip;
	}

	public Tick nextTick() {
		return new Tick(timestamp + 1);
	}

	@Override
	public int compareTo(Tick that) {
		return Long.compare(this.timestamp, that.timestamp);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Tick tick = (Tick) o;

		if (timestamp != tick.timestamp) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (timestamp ^ (timestamp >>> 32));
	}

	@Override
	public String toString() {
		return "Tick{" +
				"timestamp=" + timestamp +
				'}';
	}
}
