package com.antonkharenko.cloudclock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Anton Kharenko
 */
public class LogicalClock {

	// TODO: write javadoc

	private final AtomicReference<LogicalTimestamp> timeReference = new AtomicReference<LogicalTimestamp>();

	public LogicalClock() {
		this(new LogicalTimestamp());
	}

	public LogicalClock(LogicalTimestamp initialTimestamp) {
		this.timeReference.set(initialTimestamp);
	}

	public LogicalTimestamp time() {
		return timeReference.get();
	}

	public LogicalTimestamp tick() {
		LogicalTimestamp previousTimestamp, nextTimestamp;
		do {
			previousTimestamp = timeReference.get();
			nextTimestamp = previousTimestamp.nextTimestamp();
		} while (!timeReference.compareAndSet(previousTimestamp, nextTimestamp));
		return nextTimestamp;
	}

	public LogicalTimestamp tick(LogicalTimestamp happensBeforeTimestamp) {
		LogicalTimestamp previousTimestamp, nextTimestamp;
		do {
			previousTimestamp = timeReference.get();
			if (previousTimestamp.compareTo(happensBeforeTimestamp) > 0) {
				nextTimestamp = previousTimestamp.nextTimestamp();
			} else {
				nextTimestamp = happensBeforeTimestamp.nextTimestamp();
			}
		} while (!timeReference.compareAndSet(previousTimestamp, nextTimestamp));
		return nextTimestamp;
	}
}
