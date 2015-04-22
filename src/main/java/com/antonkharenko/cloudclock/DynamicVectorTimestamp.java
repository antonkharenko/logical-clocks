package com.antonkharenko.cloudclock;

import java.util.*;

/**
 * @author Anton Kharenko
 */
public class DynamicVectorTimestamp {

	// TODO: consider string/object identifier instead of numeric index and Map/Set/List data structure instead of array (?)
	private Map<String, ProcessTimestamp> timestampsMap;

	public DynamicVectorTimestamp(String processId) {
		timestampsMap = new HashMap<String, ProcessTimestamp>();
		timestampsMap.put(processId, new ProcessTimestamp(processId, new LogicalTimestamp(), false));
		timestampsMap = Collections.unmodifiableMap(timestampsMap);
	}

	public DynamicVectorTimestamp(Collection<String> processes) {
		timestampsMap = new HashMap<String, ProcessTimestamp>(processes.size());
		for (String processId : processes) {
			timestampsMap.put(processId, new ProcessTimestamp(processId, new LogicalTimestamp(), false));
		}
		timestampsMap = Collections.unmodifiableMap(timestampsMap);
	}

	public DynamicVectorTimestamp(Map<String, ProcessTimestamp> timestampsMap) {
		this.timestampsMap = new HashMap<String, ProcessTimestamp>(timestampsMap);
		this.timestampsMap = Collections.unmodifiableMap(timestampsMap);
	}

	public DynamicVectorTimestamp nextTimestamp(String processId) {
		if (!timestampsMap.containsKey(processId))
			throw new IllegalArgumentException("Unknown process ID: " + processId);

		ProcessTimestamp oldProcessTimestamp = timestampsMap.get(processId);
		ProcessTimestamp newProcessTimestamp = new ProcessTimestamp(
				processId,
				oldProcessTimestamp.getTimestamp().nextTimestamp(),
				oldProcessTimestamp.isRemovable());
		Map<String, ProcessTimestamp> newTimestamps = new HashMap<String, ProcessTimestamp>(timestampsMap.size());
		newTimestamps.put(processId, newProcessTimestamp);

		return new DynamicVectorTimestamp(newTimestamps);
	}


	public DynamicVectorTimestamp nextTimestamp(String localProcessId, DynamicVectorTimestamp happensBeforeTimestamp) {
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

		return new DynamicVectorTimestamp(newTimestamps);
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
