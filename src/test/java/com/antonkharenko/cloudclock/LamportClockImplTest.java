package com.antonkharenko.cloudclock;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Anton Kharenko
 */
public class LamportClockImplTest {

	@Test
	public void testGet() {
		// Given
		Tick initialTick = new Tick(0L);
		LamportClockImpl lamportClock = new LamportClockImpl(initialTick);

		// When
		Tick tick = lamportClock.get();

		// Then
		assertNotNull(tick);
		assertEquals(initialTick, tick);
	}

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
		assertTrue(tickAfter.compareTo(tickBefore) > 0);
	}

	@Test
	public void testTickOnCounterOverflow() {
		// Given
		Tick initialTick = new Tick(Long.MAX_VALUE);
		LamportClockImpl lamportClock = new LamportClockImpl(initialTick);

		// When
		Tick tickBefore = lamportClock.get();
		Tick tickAfter = lamportClock.tick();

		// Then
		assertNotNull(tickBefore);
		assertNotNull(tickAfter);
		assertTrue(tickAfter.compareTo(tickBefore) > 0);
	}

	@Test
	public void testTockWithOldHappensBeforeTick() {
		// Given
		Tick happensBeforeTick = new Tick(10L);
		Tick initialTick = new Tick(100L);
		LamportClockImpl lamportClock = new LamportClockImpl(initialTick);

		// When
		Tick resultTick = lamportClock.tock(happensBeforeTick);

		// Then
		assertNotNull(resultTick);
		assertTrue(resultTick.compareTo(happensBeforeTick) > 0);
		assertTrue(resultTick.compareTo(initialTick) > 0);
	}

	@Test
	public void testTockWithNewHappensBeforeTick() {
		// Given
		Tick happensBeforeTick = new Tick(100L);
		Tick initialTick = new Tick(10L);
		LamportClockImpl lamportClock = new LamportClockImpl(initialTick);

		// When
		Tick resultTick = lamportClock.tock(happensBeforeTick);

		// Then
		assertNotNull(resultTick);
		assertTrue(resultTick.compareTo(happensBeforeTick) > 0);
		assertTrue(resultTick.compareTo(initialTick) > 0);
	}

}
