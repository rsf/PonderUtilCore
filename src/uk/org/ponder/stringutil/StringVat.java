package uk.org.ponder.stringutil;

import uk.org.ponder.hashutil.CRC32;

import uk.org.ponder.intutil.IntEHInnaBox;

/** This class was intended to provide an atom table of strings represented
 * as (relatively) opaque integer indices. This allows fast comparison via
 * comparison of indices, and pre-interning of character data that would
 * otherwise have required a String to be temporarily allocated.
 * In general this class was found to be more trouble than it was worth
 * (despite working correctly), in particular because deletion was never
 * implemented and because thread-safety issues would probably have 
 * degraded performance more than was initially hoped in any case.
 */

public class StringVat {
  private static final int PAGE_SIZE = 1024*128;
  private static final int MAX_PAGES = 128;

  private CRC32 crc = new CRC32();
  private IntEHInnaBox indexhash = new IntEHInnaBox();

  char[][] storage = new char[MAX_PAGES][];
  int last_page = 0;
  int last_page_pos = 0;
  StringVat() {
    storage[0] = new char[PAGE_SIZE];
    }

  public static StringVat vat = new StringVat();

  private void make_space(int length) {
    if (length > (PAGE_SIZE - last_page_pos)) {
      ++ last_page;
      storage[last_page] = new char[PAGE_SIZE];
      last_page_pos = 0;
      }
    }

  public boolean equals(int index, String tocompare) {
    char[] correct_page = storage [index / PAGE_SIZE];
    index %= PAGE_SIZE;
    synchronized (correct_page) {
      int length = (int)correct_page[index ++ ];
      if (length != tocompare.length()) 
	return false;
      for (int i = index; i < index + length; ++ i) {
	if (correct_page[i] != tocompare.charAt(i - index)) return false;
	}
      }
    return true;
    }

  public boolean equals(int index, char[] array, int start, int length) {

    char[] correct_page = storage [index / PAGE_SIZE];
    index %= PAGE_SIZE;
    synchronized (correct_page) {
      int storedlength = (int)correct_page[index ++ ];
      
      if (length != storedlength) 
	return false;
      for (int i = index; i < index + length; ++ i) {
	if (correct_page[i] != array[i - index + start]) return false;
	}
      }
    return true;

    }

  public void appendto(CharWrap buf, int index) {

    char[] correct_page = storage [index / PAGE_SIZE];
    index %= PAGE_SIZE;
    synchronized (correct_page) {
      int length = (int)correct_page[index ++ ];
      
      buf.append(correct_page, index, length);
      }
    }

  public void concatto(CharWrap buf, int index1, int index2) {

    buf.clear();
    appendto(buf, index1);
    appendto(buf, index2);
    }

  private int findindex(int hash, char[] array, int start, int length) {
    int offset = -1;
    // scan through each entry in the hash with the same hashcode
    while (true) {
      ++ offset;
      int testindex = indexhash.get(hash, offset);
      if (testindex == IntEHInnaBox.INVALID_VALUE) break;

      char[] correct_page = storage [testindex / PAGE_SIZE ];
      testindex %= PAGE_SIZE;
      synchronized (correct_page) {
	int testlength = (int)correct_page[testindex ++ ];

	if (testlength != length) continue;
	for (int i = testindex; i < testindex + length; ++ i) {
	  if (correct_page[i] != array[i - testindex + start]) continue;
	  }
	}
      // string at index matches required to store
      return testindex - 1;
      } // end scanning for 
    return IntEHInnaBox.INVALID_VALUE;
    }

  public int findindex(char[] array, int start, int length) {
    int hash = CRC32.eatquick(array, start, length);
    return findindex(hash, array, start, length);
    }

  public int findindex(String string) {
    // KEEP IDENTICAL to previous method apart from String
    int length = string.length();
    int hash = CRC32.eatquick(string);
    int offset = -1;
    // scan through each entry in the hash with the same hashcode
    while (true) {
      ++ offset;
      int testindex = indexhash.get(hash, offset);
      if (testindex == IntEHInnaBox.INVALID_VALUE) break;

      char[] correct_page = storage [testindex / PAGE_SIZE ];
      testindex %= PAGE_SIZE;
      synchronized (correct_page) {
	int testlength = (int)correct_page[testindex ++ ];

	if (testlength != length) continue;
	for (int i = testindex; i < testindex + length; ++ i) {
	  if (correct_page[i] != string.charAt(i - testindex)) continue;
	  }
	}
      // string at index matches required to store
      return testindex - 1;
      } // end scanning for 
    return IntEHInnaBox.INVALID_VALUE;
    }

  public int storeunique(CharWrap wrap) {
    return storeunique(wrap.storage, wrap.offset, wrap.size);
    }

  public int storeunique(char[] array, int start, int length) {
    int hash = CRC32.eatquick(array, start, length);

    int findindex = findindex(hash, array, start, length);
    if (findindex != IntEHInnaBox.INVALID_VALUE)
      return findindex;
    int newindex = store(array, start, length);
    indexhash.put(hash, newindex);
    return newindex;
    }

  public int store(CharWrap wrap) {
    return store(wrap.storage, wrap.offset, wrap.size);
    }

  public int store(char[] array, int start, int length) {
    make_space(length);
    char[] correct_page = storage[last_page];
    int postogo = last_page_pos + last_page * PAGE_SIZE;
    //    System.out.println("Storing length "+ length+" at index "+ last_page_pos);
    synchronized (correct_page) {
      correct_page[last_page_pos++] = (char)length;
      System.arraycopy(array, start, correct_page, last_page_pos, length);
      }
    last_page_pos += length;
    return postogo;
    }

  public String toString(int index) {
    char[] correct_page = storage [index / PAGE_SIZE];
    index %= PAGE_SIZE;
    synchronized (correct_page) {
      int length = (int)correct_page[index ++ ];
      //    System.out.println("Recovered length "+ length+" now at index "+index);
      String togo = new String(correct_page, index, length);
      return togo.intern();
      }
    }
  
  public static void main(String[] argv) {
    char[] buffer = {'h', 'e', 'l', 'l', 'o', '1'};
    int[] indices = new int[7];
    for (int i = 0; i < 7; ++ i) {
      indices[i] = vat.store(buffer, 0, i);
      System.out.println("String "+i+" stored "+vat.toString(indices[i])+" at index "+indices[i]);
      }
    for (int i = 0; i < 7; ++ i) {
      System.out.println("Found "+vat.toString(indices[i])+" at index "+indices[i]);
      }
    int index1 = vat.storeunique(buffer, 0, 6);
    int index2 = vat.storeunique(buffer, 0, 6);
    System.out.println("Storeunique on identicals: "+index1+" and "+ index2);
    System.out.println("Equals: "+ vat.equals(index1, buffer, 0, 6));
    buffer[5] = '2';
    int index3 = vat.storeunique(buffer, 0, 6);
    System.out.println("Storeunique on nonidentical: "+index3);
    System.out.println("Equals: "+vat.equals(index1, buffer, 0, 6));
    
    buffer[5] = ' ';
    CharWrap svb = new CharWrap();
    svb.append(buffer, 0, 6);
    char[] buffer2 = {'w', 'o', 'r', 'l', 'd'};
    svb.append(buffer2, 0, 5);
    int nextindex = vat.store(svb);
    System.out.println("String "+vat.toString(nextindex)+" stored at index "+nextindex);
    vat.appendto(svb, indices[6]);
    int finalindex = vat.store(svb);
    System.out.println("String "+vat.toString(finalindex)+" stored at index "+finalindex);
    }
  
  }
