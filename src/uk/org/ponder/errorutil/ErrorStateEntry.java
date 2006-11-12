/*
 * Created on Oct 10, 2005
 */
package uk.org.ponder.errorutil;

public class ErrorStateEntry {
  /** A unique error ID, for easy location in logs.
   */
  public String errorid;
  public TargettedMessageList messages = new TargettedMessageList();
}
