package com.antonkharenko.cloudclock;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Anton Kharenko
 */
public class LamportClockImplTest {

	@Test
	public void testTick() {
		// Given
		LamportClockImpl lamportClock = new LamportClockImpl();


		// When
		Tick tickBefore = lamportClock.get();
		Tick tickAfter = lamportClock.tick();

		// Then
		assertNotNull(tickBefore);
		assertNotNull(tickAfter);
		assertTrue(tickAfter.getTimestamp() > tickBefore.getTimestamp());
	}

}
