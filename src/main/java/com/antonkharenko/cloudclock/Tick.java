package com.antonkharenko.cloudclock;

/**
 * @author Anton Kharenko
 */
public class Tick {

	private final long timestamp;
	// TODO: make infinite clock
	// private final boolean flip;

	public Tick(long timestamp/*, boolean flip*/) {
		this.timestamp = timestamp;
		//this.flip = flip;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
