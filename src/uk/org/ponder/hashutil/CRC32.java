package uk.org.ponder.hashutil;

import uk.org.ponder.byteutil.ByteWrap;

/** This class implements the CRC32 algorithm, used to compute integer hash values from
 * a byte stream. Felix thinks it doesn't work correctly due to its use of non-logical
 * right shifts in several of the eat methods.
 */

public class CRC32 implements Hasher {
  public static final int P = 0xEDB88320; // number of considerable magicity!
  public static final int init = 0xffffffff;
  public static int[] bytetable;
  static {
    bytetable = new int[256];
    for (int i = 0; i < 256; ++ i) {
      bytetable[i] = checkbyte(i);
      }
    }
  int checksum;
  public CRC32() {
    checksum = init;
    }
  public void reset() {
    checksum = init;
    }
  public static int checkbyte(int v) {
    for (int i = 8; i > 0; i--) {
      v = (v & 1) != 0? (v >>> 1) ^ P : v >>> 1;
      }
    return v;
    }

  public static int eatquick(char[] buffer, int start, int length) {
    int lcheck = init;
    for (int i = start; i < start + length; ++i) {
      lcheck = (lcheck >> 8) ^ bytetable[(lcheck ^ ((int)buffer[i] >> 8)) & 0xff];
      lcheck = (lcheck >> 8) ^ bytetable[(lcheck ^ (int)buffer[i]) & 0xff];
      }
    return lcheck;
    }

  public static int eatquick(byte[] buffer, int start, int length) {
    int lcheck = init;
    for (int i = start; i < start + length; ++i) {
      lcheck = (lcheck >> 8) ^ bytetable[(lcheck ^ (int)buffer[i]) & 0xff];
      }
    return lcheck;
    }

  public static int eatquick(String s) {
    int lcheck = init;
    for (int i = 0; i < s.length(); ++i) {
      lcheck = (lcheck >> 8) ^ checkbyte((lcheck ^ ((int)s.charAt(i) >> 8)) & 0xff);
      lcheck = (lcheck >> 8) ^ checkbyte((lcheck ^ (int)s.charAt(i)) & 0xff);
      }
    return lcheck;
     }

  public int eat(char[] buffer, int start, int length) {
    for (int i = start; i < start + length; ++i) {
      checksum = (checksum >> 8) ^ bytetable[(checksum ^ ((int)buffer[i] >> 8)) & 0xff];
      checksum = (checksum >> 8) ^ bytetable[(checksum ^ (int)buffer[i]) & 0xff];
      }
    return checksum;
    }
  // nb - semantics of eat changed to match eatquick for thread-safety!
  public int eat(ByteWrap b) {
    int lcheck = init;
    for (int i = 0; i < b.size(); ++i) {
      lcheck = (lcheck >> 8) ^ checkbyte((lcheck ^ b.read_at1(i)) & 0xff);
      }
    return lcheck;
    }

  public int eat(String s) {
   for (int i = 0; i < s.length(); ++i) {
      checksum = (checksum >> 8) ^ checkbyte((checksum ^ ((int)s.charAt(i) >> 8)) & 0xff);
      checksum = (checksum >> 8) ^ checkbyte((checksum ^ (int)s.charAt(i)) & 0xff);
      }
    return checksum;
 
    }
  
  public static void main(String[] args) {
    ByteWrap temphash = new ByteWrap(4);
    for (int i = 0; i < 10; ++ i) {
      temphash.write_at4(0, checkbyte(i));
      System.out.println("Hash of byte "+i+" is "+temphash);
      }
    }
  }
