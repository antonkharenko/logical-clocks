package com.antonkharenko.cloudclock;

/**
 * @author Anton Kharenko
 */
public class LamportClockImpl implements LamportClock {

	// TODO: make thread safe and non-blocking at same time

	private Tick currentTick;

	public LamportClockImpl() {
		this.currentTick = new Tick(0L);
	}

	public LamportClockImpl(Tick initialTick) {
		this.currentTick = initialTick;
	}

	@Override
	public synchronized Tick get() {
		return currentTick;
	}

	@Override
	public synchronized Tick tick() {
		currentTick = currentTick.nextTick();
		return currentTick;
	}

	@Override
	public synchronized Tick tock(Tick happensBeforeTick) {
		if (currentTick.compareTo(happensBeforeTick) > 0) {
			currentTick = currentTick.nextTick();
		} else {
			currentTick = happensBeforeTick.nextTick();
		}
		return currentTick;
	}
}
