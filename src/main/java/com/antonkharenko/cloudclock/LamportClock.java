package com.antonkharenko.cloudclock;

/**
 * @author Anton Kharenko
 */
public interface LamportClock {

	Tick get();

	Tick tick();

	Tick tock(Tick happensBeforeTick);

}
