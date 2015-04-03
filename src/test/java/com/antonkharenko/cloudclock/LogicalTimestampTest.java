package com.antonkharenko.cloudclock;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

/**
 * @author Anton Kharenko
 */
public class LogicalTimestampTest {

	// TODO: more unit tests

	@Test
	public void testToBytes() {
		LogicalTimestamp ts = new LogicalTimestamp(Long.MAX_VALUE - 1001, true);
		byte[] tsBytes = ts.toBytes();

		LogicalTimestamp tsBack = LogicalTimestamp.fromBytes(tsBytes);
		assertEquals(ts, tsBack);

	}

	@Test
	public void testJavaSerialization() throws Exception {
		LogicalTimestamp original = new LogicalTimestamp(Long.MAX_VALUE - 1001, true);

		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOutput);
		out.writeObject(original);

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOutput.toByteArray()));
		LogicalTimestamp deserialized = (LogicalTimestamp) in.readObject();

		assertEquals(original, deserialized);
	}

}
