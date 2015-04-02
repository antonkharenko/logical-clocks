package com.antonkharenko.cloudclock;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Anton Kharenko
 */
public class LogicalTimestampTest {

	@Test
	public void testToBytes() {
		LogicalTimestamp ts = new LogicalTimestamp(Long.MAX_VALUE - 1001, true);
		byte[] tsBytes = ts.toBytes();

		LogicalTimestamp tsBack = LogicalTimestamp.fromBytes(tsBytes);
		assertEquals(ts, tsBack);

	}

}
