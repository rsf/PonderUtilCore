package uk.org.ponder.intutil;

/** The class <code>IntEHInnaBox</code>implements an extendible hash
 * table of ints to ints that lies in-memory, i.e. inna box. The key
 * value must be hashed externally. The array used for storage
 * alternates between storing hash values at even-numbered locations
 * and the required value at odd-numbered ones. A hash value of 0 is
 * invalid, and represents an empty entry.  a stored value of -1 is
 * invalid, and is returned if the item is not found.
 *
 * If hash values are equal, the stored values
 * are indistinguishable and so must be indexes into some structure
 * which can decide which one was actually required. Otherwise, hash
 * values should be truly unique, for example StringVat indices.  
 * This is as good as can be done without callbacks such as equals()
 * and hashCode().
 */

public class IntEHInnaBox {
  public static final int INVALID_VALUE = -1;
  private static final int INVALID_HASH = 0;
  private static final int INITIAL_BITS = 10;
  private static final double MAXIMUM_LOAD = 0.5;

  int current_bits = INITIAL_BITS;
  int[] storage;
  int current_mask;

  int filled = 0;

  public IntEHInnaBox () {
    allocate();
    }

  public IntEHInnaBox (int initialcapacity) {
    current_bits = (int) (Math.log(initialcapacity) / Math.log(2));
    allocate();
    }
  
  private void allocate() {
    storage = new int[1 << current_bits];
    current_mask = (-1 >>> (32 - current_bits)) ^ 1;
    }

  private void checkexpand() {
    if ( (double) (filled + 1) / storage.length > MAXIMUM_LOAD) {
      int[] oldstorage = storage;
      ++ current_bits;
      
      allocate();

      System.out.println("*******Load factor exceeded: *******: current_mask now "+
			 uk.org.ponder.byteutil.ByteWrap.intToHex(current_mask)+" "+current_bits);

      for (int i = 0; i < oldstorage.length; i += 2) {
	if (oldstorage[i] != 0) {
	  put(oldstorage[i], oldstorage[i+1]);
	  }
	} // end for each old element
      } // end if hash overfilled
    }

  /* Get the </code>offset</code>th stored value with the specified hash key.
   * @param hash The hash value required.
   * @param offset In the case where many items are stored with identical hash keys, the
   * number of the item with that hash key that is required.
   * @return The required value, or <code>INVALID_VALID</code> if there is none found.
   */
  // iterate through index numbers sharing hash value
  // invalid hash value is 0, invalid index is -1
  public int get(int hash, int offset) {
    if (hash == INVALID_HASH) ++ hash;
    int initindex = (hash & current_mask);
    int i = initindex;
    int curoffset = 0;
    do {
      //     System.out.println("Found hash value "+ storage[i]+" at location "+ i+
      //	                " offset "+curoffset);
      if (storage[i] == INVALID_HASH) return INVALID_VALUE;
      if (storage[i] == hash) {
	if (curoffset == offset) return storage[i+1];
	++ curoffset;
	}
      i = (i + 2) & current_mask;
      } while (i != initindex);
    return INVALID_VALUE;
    }
  /* Enters the specified value into the table with the specified hash key, unless that
   * value is already stored.
   * @param hash The hash key for the value.
   * @param value The value that is to be stored.
   * @return <code>true</code> if the value was successfully stored, <code>false</code> if
   * it was already present and the table was left unaltered.
   */
  public boolean put(int hash, int value) {
    if (hash == INVALID_HASH) ++ hash;
    checkexpand(); // note that this may change current_mask
    int initindex = (hash & current_mask);
    //    System.out.println("Putting "+ hash+" value " + value +" at "+ initindex);

    int i = initindex;
    do {
      if (storage[i] == INVALID_HASH) break; 
      if (storage[i+1] == value) return false;
      i = (i + 2) & current_mask;
      } while (i != initindex);
    //    System.out.println("Stored at "+i);
    storage[i] = hash;
    storage[i + 1] = value;

    filled++;

    return true;
    }
  }
