package uk.org.ponder.byteutil;

/** A ByteWrap class that allows a little extra "air". Its sole action is to override the
 * <code>ensureCapacity</code> method so it reserves a specified amount of extra space
 * whenever it is called upon, in addition to the amount explicitly requested.
 * The amount of extra space to be reserved is set by a call to the <code>setAir</code> method.
 */

public class AirByteWrap extends ByteWrap { 
  private int air;
  /** Sets the amount of extra space to be reserved by a call to <code>ensureCapacity</code>.
   * @param air The amount of extra space to be reserved.
   */
  public void setAir(int air) {
    this.air = air;
    }
  /** This method is overridden from <code>ByteWrap</code> to reserve extra space
   * as specified to <code>setAir</code>.
   * @param capacity The capacity explicitly requested.
   */
  public void ensureCapacity(int capacity) {
    super.ensureCapacity(capacity + air);
    size = capacity;
    }
  }
