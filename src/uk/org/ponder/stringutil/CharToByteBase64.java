package uk.org.ponder.stringutil;

/** A utility class to decode Base64-encoded character strings back into
 * byte arrays.
 */

public class CharToByteBase64 {
  
  public final static byte pem_reverse_array[] = new byte[256];
  
  static {
    for (int i = 0; i < 255; i++) {
      pem_reverse_array[i] = -1;
      }
    for (int i = 0; i < ByteToCharBase64.pem_array.length; i++) {
      pem_reverse_array[ByteToCharBase64.pem_array[i]] = (byte) i;
      }
    }
 /**
   * Decode an input array of chars into an output array of bytes. This method is
   * more inefficient than the 5-parameter version below since a new byte output
   * array is allocated and returned with each call.
   * @param inarray An character array holding the data to be converted from
   * Base64 to bytes.
   * @param instart The start position of the character data to be converted.
   * @param inlength The length of the character data to be converted.
   * @return A byte array holding the decoded bytes.
   */
 
  // for example, (11 + 1) * 3 = 36 -> 9??
  public static byte[] decode(char[] inarray, int instart, int inlength) {
    // 3    6    9    12     15   18   21   24
    // 1->1 2->2 3->3 4->3   5->4 6->5 7->6 8->6
    byte[] sufficientbuffer = new byte[((inlength + 1)* 3) / 4];
    decode(inarray, instart, inlength, sufficientbuffer, 0);
    return sufficientbuffer;
    }
  
  /**
   * Decode an input array of chars into an output array of bytes.
   * @param inarray An character array holding the data to be converted from
   * Base64 to bytes.
   * @param instart The start position of the character data to be converted.
   * @param inlength The length of the character data to be converted.
   * @param outarray A byte array to receive the decoded character data.
   * @param outstart The position within the byte array to start writing the
   * decoded data.
   * @return The number of bytes that were decoded and written to the supplied
   * byte array.
   */
  public static int decode(char[] inarray, int instart, int inlength, 
			   byte[] outarray, int outstart) {
    int decodestop;
    for (decodestop = 0; decodestop < inlength; ++ decodestop) {
      System.out.print("char: "+inarray[decodestop + instart] + " ");
      if (pem_reverse_array[ (byte) (inarray[decodestop + instart])] == -1)
	break;
      }
    int outpos = outstart;
    while (decodestop > 0) {
      //      int bytestogo = decodestop * 3 / 4;
      int rem = ((decodestop - 1)% 4) + 1;   // 4->4, 5->1
      int virtualstart = instart + (rem - 4); // 4->0, 3->-1, 2->-2, 1->-3
      int a = 0, b = 0, c = 0, d = 0;
    
      switch (rem) {
	// first set of masks to convert from char to index
	// second set to avoid sign-extension
      case 4:
	//   a      b      c      d
	// 000000 001111 111122 222222
	a = pem_reverse_array[inarray[virtualstart] & 0xff] & 0xff;
	// NOBREAK
      case 3:
	//   b      c      d
	// 001111 111122 222222
	b = pem_reverse_array[inarray[virtualstart + 1] & 0xff] & 0xff;
	// nb sign bit of b & c will be 0, so no problem with right shift
	outarray[outpos++] = (byte)((a << 2) + (b >> 4));
	// NOBREAK
      case 2:
	//   c      d
        // 111122  222222
	c = pem_reverse_array[inarray[virtualstart + 2] & 0xff] & 0xff;
	outarray[outpos++] = (byte)((b << 4) + (c >> 2));
      case 1:
	//   d
        // 222222
	d = pem_reverse_array[inarray[virtualstart + 3] & 0xff] & 0xff;
	outarray[outpos++] = (byte)((c << 6) + d);
	break;
	}
      System.out.println("a "+a+" b "+b+" c "+c+" d "+d);
      instart += rem;
      decodestop -= rem; // should make rem 4 on all subsequent times until 0.
      }
    return outpos - outstart;
    }
  }
