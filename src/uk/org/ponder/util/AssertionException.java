package uk.org.ponder.util;

/** A runtime exception to be used when an assertion fails.
 */

public class AssertionException extends RuntimeException {
  /** Constructs a new AssertionException with the specified detail message.
   * @param s The detail message for the exception.
   */
  public AssertionException(String s) { super(s); }
  }
