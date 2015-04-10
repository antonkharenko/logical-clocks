package com.antonkharenko.cloudclock;

import java.util.*;

/**
 * @author Anton Kharenko
 */
public final class VectorTimestamp {

	// TODO: consider string/object identifier instead of numeric index and Map/Set/List data structure instead of array (?)
	private Map<String, ProcessTimestamp> timestampsMap;

	// TODO: remove
	private LogicalTimestamp[] timestamps;

	public VectorTimestamp(String processId) {
		timestampsMap = new HashMap<String, ProcessTimestamp>();
		timestampsMap.put(processId, new ProcessTimestamp(processId, new LogicalTimestamp(), false));
		timestampsMap = Collections.unmodifiableMap(timestampsMap);
	}

	public VectorTimestamp(Collection<String> processes) {
		timestampsMap = new HashMap<String, ProcessTimestamp>(processes.size());
		for (String processId : processes) {
			timestampsMap.put(processId, new ProcessTimestamp(processId, new LogicalTimestamp(), false));
		}
		timestampsMap = Collections.unmodifiableMap(timestampsMap);
	}

	public VectorTimestamp(Map<String, ProcessTimestamp> timestampsMap) {
		this.timestampsMap = new HashMap<String, ProcessTimestamp>(timestampsMap);
		this.timestampsMap = Collections.unmodifiableMap(timestampsMap);
	}

	// TODO: remove
	public VectorTimestamp(int vectorLength) {
		timestamps = new LogicalTimestamp[vectorLength];
		for (int i = 0; i < vectorLength; i++) {
			timestamps[i] = new LogicalTimestamp();
		}
	}

	// TODO: remove
	public VectorTimestamp(LogicalTimestamp[] timestamps) {
		this.timestamps = Arrays.copyOf(timestamps, timestamps.length);
	}

	// TODO: remove
	public VectorTimestamp nextTimestamp(int localIndex) {
		if (localIndex < 0 || localIndex >= timestamps.length)
			throw new IllegalArgumentException("Index out of bounds");

		LogicalTimestamp[] newTimestamps = Arrays.copyOf(timestamps, timestamps.length);
		newTimestamps[localIndex] = newTimestamps[localIndex].nextTimestamp();

		return new VectorTimestamp(newTimestamps);
	}

	public VectorTimestamp nextTimestamp(String processId) {
		if (!timestampsMap.containsKey(processId))
			throw new IllegalArgumentException("Unknown process ID: " + processId);

		ProcessTimestamp oldProcessTimestamp = timestampsMap.get(processId);
		ProcessTimestamp newProcessTimestamp = new ProcessTimestamp(
				processId,
				oldProcessTimestamp.getTimestamp().nextTimestamp(),
				oldProcessTimestamp.isRemovable());
		Map<String, ProcessTimestamp> newTimestamps = new HashMap<String, ProcessTimestamp>(timestampsMap.size());
		newTimestamps.put(processId, newProcessTimestamp);

		return new VectorTimestamp(newTimestamps);
	}

	// TODO: remove
	public VectorTimestamp nextTimestamp(int localIndex, VectorTimestamp happensBeforeTimestamp) {
		if (localIndex < 0 || localIndex >= timestamps.length)
			throw new IllegalArgumentException("Index out of bounds.");
		if (timestamps.length != happensBeforeTimestamp.timestamps.length)
			throw new IllegalArgumentException("Timestamp vectors length do not match.");

		LogicalTimestamp[] newTimestamps = Arrays.copyOf(timestamps, timestamps.length);
		newTimestamps[localIndex] = newTimestamps[localIndex].nextTimestamp();
		for (int i = 0; i < newTimestamps.length; i++) {
			if (i != localIndex && newTimestamps[i].isBefore(happensBeforeTimestamp.timestamps[i])) {
				newTimestamps[i] = happensBeforeTimestamp.timestamps[i];
			}
		}

		return new VectorTimestamp(newTimestamps);
	}

	public VectorTimestamp nextTimestamp(String localProcessId, VectorTimestamp happensBeforeTimestamp) {
		if (!timestampsMap.containsKey(localProcessId))
			throw new IllegalArgumentException("Unknown process ID: " + localProcessId);

		Set<String> processSet = new HashSet<String>();
		processSet.addAll(timestampsMap.keySet());
		processSet.addAll(happensBeforeTimestamp.timestampsMap.keySet());

		Map<String, ProcessTimestamp> newTimestamps = new HashMap<String, ProcessTimestamp>(timestampsMap.size());
		for (String processId : processSet) {
			if (localProcessId.equals(processId)) {
				//TODO: for local process
			} else {
				//TODO: compare for other processes taking into account removable property
			}
		}

		return new VectorTimestamp(newTimestamps);
	}


	/**
	 * Returns true if current timestamp happens before given timestamp and timestamps are in a causal relation.
	 * In case of false result it either can correspond to the one of possible situations: timestamps are equal,
	 * timestamps are concurrent or this timestamp happens after the given timestamp.
	 */
	public boolean isHappensBefore(VectorTimestamp that) {
		return compare(that) == Relation.HAPPENS_BEFORE;
	}

	/**
	 * Returns true if current timestamp happens after given timestamp and timestamps are in a causal relation.
	 * In case of false result it either can correspond to the one of possible situations: timestamps are equal,
	 * timestamps are concurrent or this timestamp happens before the given timestamp.
	 */
	public boolean isHappensAfter(VectorTimestamp that) {
		return compare(that) == Relation.HAPPENS_AFTER;
	}

	/**
	 * Returns true if current timestamp happens concurrently and there is no causal relation between them.
	 * In case of false result it either can correspond to the one of possible situations: timestamps are equal,
	 * timestamps are in causal relation.
	 */
	public boolean isConcurrent(VectorTimestamp that) {
		return compare(that) == Relation.HAPPENS_BEFORE;
	}

	/**
	 * Compares two vector timestamps and defines relation between them. Two timestamps may be equal, concurrent or
	 * in causal (happens-before) relation.
	 *
	 * @param that given vector timestamp to compare
	 * @return {@code Relation} between current timestamp and the given one.
	 *
	 * @see com.antonkharenko.cloudclock.Relation
	 */
	public Relation compare(VectorTimestamp that) {
		if (timestamps.length != that.timestamps.length)
			throw new IllegalArgumentException("Timestamp vectors length do not match.");

		Relation relation = Relation.EQUAL;
		for (int i = 0; i < timestamps.length; i++) {
			if (this.timestamps[i].isBefore(that.timestamps[i])) {
				if (relation == Relation.HAPPENS_AFTER)
					return Relation.CONCURRENT;
				relation = Relation.HAPPENS_BEFORE;
			} else if (this.timestamps[i].isAfter(that.timestamps[i])) {
				if (relation == Relation.HAPPENS_BEFORE)
					return Relation.CONCURRENT;
				relation = Relation.HAPPENS_AFTER;
			}
		}

		// TODO: compare maps

		return relation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VectorTimestamp that = (VectorTimestamp) o;
		return Arrays.equals(timestamps, that.timestamps);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(timestamps);
	}

	@Override
	public String toString() {
		return "VectorTimestamp{" +
				"timestamps=" + Arrays.toString(timestamps) +
				'}';
	}

	public static final class ProcessTimestamp {

		private final String processId;
		private final LogicalTimestamp timestamp;
		private final boolean removable;

		public ProcessTimestamp(String processId, LogicalTimestamp timestamp, boolean removable) {
			this.processId = processId;
			this.timestamp = timestamp;
			this.removable = removable;
		}

		public String getProcessId() {
			return processId;
		}

		public LogicalTimestamp getTimestamp() {
			return timestamp;
		}

		public boolean isRemovable() {
			return removable;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			ProcessTimestamp that = (ProcessTimestamp) o;

			if (removable != that.removable) {
				return false;
			}
			if (processId != null ? !processId.equals(that.processId) : that.processId != null) {
				return false;
			}
			if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = processId != null ? processId.hashCode() : 0;
			result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
			result = 31 * result + (removable ? 1 : 0);
			return result;
		}

		@Override
		public String toString() {
			return "ProcessTimestamp{" +
					"processId='" + processId + '\'' +
					", timestamp=" + timestamp +
					", removable=" + removable +
					'}';
		}
	}
}
