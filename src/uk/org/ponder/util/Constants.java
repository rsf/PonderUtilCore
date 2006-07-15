package uk.org.ponder.util;

public interface Constants {
  /** a constant indicating that a double value is missing.*/
  public static final double MissingValue = Double.MIN_VALUE;
  /** A non-interchangeable "private use" Unicode character for use
   * where an identifiable non-null key is required. 
   * Never try to store or transmit this character outside the JVM!
   */
  public static final String UNUSED_STRING = "\ufdd0";
  /** A String value to be used when trying to encode a <code>null</code>
   * value outside the JVM, which cannot otherwise be signalled. 
   */
  // U+25a9 = Unicode Character 'SQUARE WITH DIAGONAL CROSSHATCH FILL'
  // See StaticLeafParser for more details
  public static String NULL_STRING = "\u25a9null\u25a9";
  public static final double root2 = Math.sqrt(2);
  }
