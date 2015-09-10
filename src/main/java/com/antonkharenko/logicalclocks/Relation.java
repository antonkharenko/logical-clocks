package com.antonkharenko.logicalclocks;

/**
 * Represents logical relation Happens-Before between two events which happens in the distributed
 * system.
 *
 * <p>
 * Happens-Before relation (denoted below as "->") represented with three following rules:
 * <ol>
 * <li>On same process:
 * 
 * <pre>
 * A -> B, if time(A) < time(B) (using the local clock)
 * </pre>
 * 
 * </li>
 * <li>If process P1 sends message M to process P2:
 * 
 * <pre>
 * send(M) -> receive(M)
 * </pre>
 * 
 * </li>
 * <li>Transitivity:
 * 
 * <pre>if A -> B and B -> C then A -> C
 * 
 * <pre/></li>
 * </ol>
 *
 * @author Anton Kharenko
 */
public enum Relation {

  /**
   * Timestamps are equal.
   */
  EQUAL,

  /**
   * Two events are in causal relation and one event happens before another.
   */
  HAPPENS_BEFORE,

  /**
   * Two events are in causal relation and one event happens after another.
   */
  HAPPENS_AFTER,

  /**
   * Two events are concurrent and not in causal relation to each other.
   */
  CONCURRENT
}
