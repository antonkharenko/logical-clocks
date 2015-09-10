[![Build Status](https://travis-ci.org/antonkharenko/logical-clocks.svg)](https://travis-ci.org/antonkharenko/logical-clocks)

# Logical Clocks

Simple implementation of Lamport and Vector clocks.

## How to use

``` java

// 
// Logical Timestamp
// 

// Initialize
LogicalTimestamp ts0 = new LogicalTimestamp();
LogicalTimestamp ts1 = ts.nextTimestamp();
LogicalTimestamp ts100 = LogicalTimestamp.fromLong(100L);

// Serialize and deserialize
byte[] tsAsBytes = ts0.toBytes();
LogicalTimestamp deserializedTs = tsAsBytes.fromBytes(tsAsBytes);

// Compare
ts0.isBefore(ts1); // true
ts1.isAfter(ts0); // true
ts0.compareTo(ts1); // < 0
ts1.compareTo(ts0); // > 0
ts0.equals(ts1); // false
ts0.isAfter(ts1); //false
ts1.isBefore(ts1); // false

// 
// Logical Clock (thread-safe)
// 

// Initialize
LogicalTimestamp initialTimestamp = new LogicalTimestamp();
LogicalClock clock = new LogicalClock(initialTimestamp);

// Time & tick
LogicalTimestamp currentTimestamp = clock.time();
LogicalTimestamp nextTimestamp = clock.tick(); // thread-safe
LogicalTimestamp newCurrentTimestamp = clock.time(); // happens after currentTimestamp

```

See Unit tests for more examples.
