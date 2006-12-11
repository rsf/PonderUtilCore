/*
 * Created on Oct 10, 2005
 */
package uk.org.ponder.errorutil;

import uk.org.ponder.messageutil.TargettedMessageList;

public class ErrorStateEntry {
  /** A unique error ID, for easy location in logs.
   */
  public String errorid;
  public TargettedMessageList messages = new TargettedMessageList();
}
