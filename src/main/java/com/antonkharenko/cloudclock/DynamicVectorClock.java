package com.antonkharenko.cloudclock;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Anton Kharenko
 */
public class DynamicVectorClock {

	private final AtomicReference<VectorTimestamp> timeReference = new AtomicReference<VectorTimestamp>();

	public void markProcessAsRemovable(String processId) {
		//TODO
	}

	public void removeProcess(String processId) {
		//TODO
	}

}
