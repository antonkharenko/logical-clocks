package com.antonkharenko.cloudclock;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Anton Kharenko
 */
public class LogicalClockTest {

	@Test
	public void testGet() {
		// Given
		LogicalTimestamp initialTimestamp = new LogicalTimestamp();
		LogicalClock clock = new LogicalClock(initialTimestamp);

		// When
		LogicalTimestamp actualTimestamp = clock.time();

		// Then
		assertNotNull(actualTimestamp);
		assertEquals(initialTimestamp, actualTimestamp);
	}

	@Test
	public void testTick() {
		// Given
		LogicalClock clock = new LogicalClock();


		// When
		LogicalTimestamp before = clock.time();
		LogicalTimestamp after = clock.tick();

		// Then
		assertNotNull(before);
		assertNotNull(after);
		assertTrue(after.compareTo(before) > 0);
	}

	@Test
	public void testTickOnCounterOverflow() {
		// Given
		LogicalTimestamp initialTimestamp = new LogicalTimestamp(Long.MAX_VALUE);
		LogicalClock clock = new LogicalClock(initialTimestamp);

		// When
		LogicalTimestamp before = clock.time();
		LogicalTimestamp after = clock.tick();

		// Then
		assertNotNull(before);
		assertNotNull(after);
		assertTrue(after.compareTo(before) > 0);
	}

	@Test
	public void testTockWithOldHappensBeforeTick() {
		// Given
		LogicalTimestamp happensBeforeTimestamp = new LogicalTimestamp(10L);
		LogicalTimestamp initialTimestamp = new LogicalTimestamp(100L);
		LogicalClock clock = new LogicalClock(initialTimestamp);

		// When
		LogicalTimestamp resultTimestamp = clock.tick(happensBeforeTimestamp);

		// Then
		assertNotNull(resultTimestamp);
		assertTrue(resultTimestamp.compareTo(happensBeforeTimestamp) > 0);
		assertTrue(resultTimestamp.compareTo(initialTimestamp) > 0);
	}

	@Test
	public void testTockWithNewHappensBeforeTick() {
		// Given
		LogicalTimestamp happensBeforeTimestamp = new LogicalTimestamp(100L);
		LogicalTimestamp initialTimestamp = new LogicalTimestamp(10L);
		LogicalClock clock = new LogicalClock(initialTimestamp);

		// When
		LogicalTimestamp resultTimestamp = clock.tick(happensBeforeTimestamp);

		// Then
		assertNotNull(resultTimestamp);
		assertTrue(resultTimestamp.compareTo(happensBeforeTimestamp) > 0);
		assertTrue(resultTimestamp.compareTo(initialTimestamp) > 0);
	}

}
