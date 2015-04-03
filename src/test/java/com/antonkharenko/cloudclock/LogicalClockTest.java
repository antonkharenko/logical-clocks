package com.antonkharenko.cloudclock;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Anton Kharenko
 */
public class LogicalClockTest {

	// TODO: more unit tests

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
	public void testTickWithOldHappensBeforeTick() {
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
	public void testTickWithNewHappensBeforeTick() {
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

	@Test
	public void testTickThreadSafe() throws Exception {
		// Given
		final int tickCount = 1000000;
		final int threadCount = 100;
		final LogicalTimestamp initialTimestamp = new LogicalTimestamp();
		final LogicalClock clock = new LogicalClock();
		final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		// Single threaded execution
		LogicalTimestamp expectedTimestamp = initialTimestamp;
		for (int i = 0; i < tickCount; i++) {
			expectedTimestamp = expectedTimestamp.nextTimestamp();
		}

		// When
		// Multi threaded execution
		for (int i = 0; i < tickCount; i++) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					clock.tick();
				}
			});
		}
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);

		LogicalTimestamp actualTimestamp = clock.time();

		// Then
		assertEquals(expectedTimestamp, actualTimestamp);
	}

}
