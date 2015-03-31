package com.antonkharenko.cloudclock;

/**
 * @author Anton Kharenko
 */
public final class Tick implements Comparable<Tick> {

	private final long counter;
	private final boolean flip;

	public Tick(long counter) {
		this(counter, false);
	}

	public Tick(long counter, boolean flip) {
		this.counter = counter;
		this.flip = flip;
	}

	public Tick nextTick() {
		long nextTimestamp = Math.max(0, counter + 1);
		boolean nextFlip = nextTimestamp > counter ? flip : !flip; // reverse flip on counter reset
		return new Tick(nextTimestamp, nextFlip);
	}

	@Override
	public int compareTo(Tick that) {
		if (this.flip == that.flip) {
			return Long.compare(this.counter, that.counter);
		} else {
			return Long.compare(that.counter, this.counter);
		}

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

		if (counter != tick.counter) {
			return false;
		}
		if (flip != tick.flip) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (counter ^ (counter >>> 32));
		result = 31 * result + (flip ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Tick{" +
				"counter=" + counter +
				", flip=" + flip +
				'}';
	}
}
