package com.antonkharenko.cloudclock;

/**
 * @author Anton Kharenko
 */
public class LamportClockImpl implements LamportClock {

	// TODO: make thread safe and non-blocking

	private Tick currentTick;

	public LamportClockImpl() {
		this.currentTick = new Tick(0L);
	}

	@Override
	public synchronized Tick get() {
		return currentTick;
	}

	@Override
	public synchronized Tick tick() {
		currentTick = new Tick(currentTick.getTimestamp() + 1);
		return currentTick;
	}

	@Override
	public synchronized Tick tock(Tick happensBeforeTick) {
		long newTimestamp = Math.max(happensBeforeTick.getTimestamp(), currentTick.getTimestamp()) + 1;
		currentTick = new Tick(newTimestamp);
		return currentTick;
	}
}
