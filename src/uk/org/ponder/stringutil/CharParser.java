package uk.org.ponder.stringutil;

/** A useful class which quickly parses numbers from character arrays rather than
 * from strings as the slovenly Java libraries do.
 */

public class CharParser {
  private static final int MAXO10 = Integer.MAX_VALUE / 10;   //  214748364
  private static final int MAXO16 = Integer.MAX_VALUE / 16;   //  134217727

  /** Parses a positive integer from the contents of a character array. The 
   * specified section of the array must contain no non-numeric characters.
   * @param buffer The array containing the data to be parsed.
   * @param start The start position of the numeric data.
   * @param length The length of the numeric data.
   * @exception NumberFormatException If the specified array section includes
   * non-numeric characters or represents an integer out of range for a positive
   * signed 32-bit integer.
   */

  public static int parsePositiveInt(char[] buffer, int start, int length) 
  throws NumberFormatException {
    int accumulate = 0;
    for (int i = 0; i < length; ++ i) {
      char c = buffer[i + start];
      if (c < '0' || c > '9') 
	throw new NumberFormatException("Invalid character "+c+" in positive integer");
      if (accumulate > MAXO10)
	throw new NumberFormatException("Number found greater than maximum integer: " +
				        new String(buffer, start, length));
      accumulate *= 10;
      int uncess = Integer.MAX_VALUE - accumulate;
      int digit = (c - '0');
      if (digit > uncess) 
	throw new NumberFormatException("Number found greater than maximum integer: " +
				        new String(buffer, start, length));
      accumulate += (c - '0');
      }
    return accumulate;
    }

  /** Parses a single hex digit as its integer equivalent.
   * @return The integer corresponding to the supplied hex digit, or -1 if the
   * character is not a valid hex digit.
   */

  public static int fromHex(char digit) {
    digit = Character.toUpperCase(digit);
    if (digit <= '9' && digit >= '0') return digit - '0';
    else if (digit <= 'F' && digit >= 'A') return 10 + digit - 'A';
    else return -1;
    }
  /** Parses a positive integer stored as hexadecimal from the contents of a 
   * character array. The specified section of the array must contain no non-hexadecimal
   * characters.
   * @param buffer The array containing the data to be parsed.
   * @param start The start position of the numeric data.
   * @param length The length of the numeric data.
   * @exception NumberFormatException If the specified array section includes
   * non-hexadecimal characters or represents an integer out of range for a positive
   * signed 32-bit integer.
   */

  public static int parseHexInt(char[] buffer, int start, int length) 
    throws NumberFormatException {
    int accumulate = 0;
    for (int i = 0; i < length; ++ i) {
      char c = buffer[i + start];
      int digit = fromHex(c);
      if (digit == -1)
	throw new NumberFormatException("Invalid character "+c+" in hex string");
      if (accumulate > MAXO16)
	throw new NumberFormatException("Number found greater than maximum integer: " +
					new String(buffer, start, length));
      accumulate *= 16;
      int uncess = Integer.MAX_VALUE - accumulate;
      if (digit > uncess)
	throw new NumberFormatException("Number found greater than maximum integer: " +
					new String(buffer, start, length));
      accumulate += digit;
      }
    return accumulate;
    }
  }
