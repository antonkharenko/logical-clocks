package com.antonkharenko.logicalclocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

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
    LogicalTimestamp initialTimestamp = new LogicalTimestamp();
    LogicalClock clock = new LogicalClock(initialTimestamp);

    // When
    LogicalTimestamp after = clock.tick();

    // Then
    assertNotNull(after);
    assertTrue(after.isAfter(initialTimestamp));
  }

  @Test
  public void testTickOnCounterOverflow() {
    // Given
    LogicalTimestamp initialTimestamp = LogicalTimestamp.fromLong(Long.MAX_VALUE);
    LogicalClock clock = new LogicalClock(initialTimestamp);

    // When
    LogicalTimestamp after = clock.tick();

    // Then
    assertNotNull(after);
    assertTrue(after.isAfter(initialTimestamp));
  }

  @Test
  public void testTickWithOldHappensBeforeTick() {
    // Given
    LogicalTimestamp happensBeforeTimestamp = LogicalTimestamp.fromLong(10L);
    LogicalTimestamp initialTimestamp = LogicalTimestamp.fromLong(100L);
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
    LogicalTimestamp happensBeforeTimestamp = LogicalTimestamp.fromLong(100L);
    LogicalTimestamp initialTimestamp = LogicalTimestamp.fromLong(10L);
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
