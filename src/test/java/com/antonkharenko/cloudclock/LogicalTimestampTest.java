package com.antonkharenko.cloudclock;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

/**
 * @author Anton Kharenko
 */
public class LogicalTimestampTest {

	@Test
	public void testNextTimestampAndComparisonInit() {
		// Given
		LogicalTimestamp ts = new LogicalTimestamp();

		// When
		LogicalTimestamp nextTs = ts.nextTimestamp();

		// Then
		assertNotNull(nextTs);
		assertTrue(ts.isBefore(nextTs));
		assertTrue(nextTs.isAfter(ts));
		assertTrue(ts.compareTo(nextTs) < 0);
		assertTrue(nextTs.compareTo(ts) > 0);
		assertFalse(ts.equals(nextTs));
		assertFalse(ts.isAfter(nextTs));
		assertFalse(nextTs.isBefore(nextTs));
	}

	@Test
	public void testNextTimestampAndComparison() {
		// Given
		LogicalTimestamp ts = LogicalTimestamp.fromLong(1000L);

		// When
		LogicalTimestamp nextTs = ts.nextTimestamp();

		// Then
		assertNotNull(nextTs);
		assertTrue(ts.isBefore(nextTs));
		assertTrue(nextTs.isAfter(ts));
		assertTrue(ts.compareTo(nextTs) < 0);
		assertTrue(nextTs.compareTo(ts) > 0);
		assertFalse(ts.equals(nextTs));
		assertFalse(ts.isAfter(nextTs));
		assertFalse(nextTs.isBefore(nextTs));
	}

	@Test
	public void testNextTimestampAndComparisonMaxValue() {
		// Given
		LogicalTimestamp ts = LogicalTimestamp.fromLong(Long.MAX_VALUE);

		// When
		LogicalTimestamp nextTs = ts.nextTimestamp();

		// Then
		assertNotNull(nextTs);
		assertTrue(ts.isBefore(nextTs));
		assertTrue(nextTs.isAfter(ts));
		assertTrue(ts.compareTo(nextTs) < 0);
		assertTrue(nextTs.compareTo(ts) > 0);
		assertFalse(ts.equals(nextTs));
		assertFalse(ts.isAfter(nextTs));
		assertFalse(nextTs.isBefore(nextTs));
	}

	@Test
	public void testNextTimestampAndComparisonMinValue() {
		// Given
		LogicalTimestamp ts = LogicalTimestamp.fromLong(Long.MIN_VALUE);

		// When
		LogicalTimestamp nextTs = ts.nextTimestamp();

		// Then
		assertNotNull(nextTs);
		assertTrue(ts.isBefore(nextTs));
		assertTrue(nextTs.isAfter(ts));
		assertTrue(ts.compareTo(nextTs) < 0);
		assertTrue(nextTs.compareTo(ts) > 0);
		assertFalse(ts.equals(nextTs));
		assertFalse(ts.isAfter(nextTs));
		assertFalse(nextTs.isBefore(nextTs));
	}

	@Test
	public void testNextTimestampAndComparisonFlipMaxValue() {
		// Given
		LogicalTimestamp ts = LogicalTimestamp.fromLong(-1);

		// When
		LogicalTimestamp nextTs = ts.nextTimestamp();

		// Then
		assertNotNull(nextTs);
		assertTrue(ts.isBefore(nextTs));
		assertTrue(nextTs.isAfter(ts));
		assertTrue(ts.compareTo(nextTs) < 0);
		assertTrue(nextTs.compareTo(ts) > 0);
		assertFalse(ts.equals(nextTs));
		assertFalse(ts.isAfter(nextTs));
		assertFalse(nextTs.isBefore(nextTs));
	}

	@Test
	public void testBytesSerialization() {
		// Given
		LogicalTimestamp original = LogicalTimestamp.fromLong(-1001);

		// When
		byte[] tsBytes = original.toBytes();
		LogicalTimestamp deserialized = LogicalTimestamp.fromBytes(tsBytes);

		// Then
		assertEquals(original, deserialized);
	}

	@Test
	public void testLongSerialization() {
		// Given
		LogicalTimestamp original = LogicalTimestamp.fromLong(-1);

		// When
		long longValue = original.toLong();
		LogicalTimestamp deserialized = LogicalTimestamp.fromLong(longValue);

		// Then
		assertEquals(original, deserialized);
	}

	@Test
	public void testJavaSerialization() throws Exception {
		// Given
		LogicalTimestamp original = LogicalTimestamp.fromLong(-1001);

		// When
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOutput);
		out.writeObject(original);

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOutput.toByteArray()));
		LogicalTimestamp deserialized = (LogicalTimestamp) in.readObject();

		// Then
		assertEquals(original, deserialized);
	}

}
