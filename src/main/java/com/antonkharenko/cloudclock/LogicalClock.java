package com.antonkharenko.cloudclock;

/**
 * @author Anton Kharenko
 */
public class LogicalClock {

	// TODO: make thread safe and non-blocking at same time
	// TODO: write javadoc

	private LogicalTimestamp time;

	public LogicalClock() {
		this.time = new LogicalTimestamp();
	}

	public LogicalClock(LogicalTimestamp initialTimestamp) {
		this.time = initialTimestamp;
	}

	public synchronized LogicalTimestamp time() {
		return time;
	}

	public synchronized LogicalTimestamp tick() {
		time = time.nextTimestamp();
		return time;
	}

	public synchronized LogicalTimestamp tick(LogicalTimestamp happensBeforeTimestamp) {
		if (time.compareTo(happensBeforeTimestamp) > 0) {
			time = time.nextTimestamp();
		} else {
			time = happensBeforeTimestamp.nextTimestamp();
		}
		return time;
	}
}
