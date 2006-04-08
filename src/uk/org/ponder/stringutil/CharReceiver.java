/*
 * Created on 04-Apr-2006
 */
package uk.org.ponder.stringutil;

public interface CharReceiver {
  /** Receives the character c, and returns <code>true</code> if the receiver
   * is filled and cannot accept further output for this round. We always
   * insist on sufficiently non-fiendish receivers that can always accept
   * at least one character of output...
   * @param c
   * @return
   */
  public boolean receiveChar(char c);
}
