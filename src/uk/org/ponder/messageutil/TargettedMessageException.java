/*
 * Created on 29 Mar 2008
 */
package uk.org.ponder.messageutil;

/** A convenient exception class to contribute a {@link TargettedMessage} to the
 * current environment, without requiring to inject a particular 
 * {@link TargettedMessageList}, or take particular responsibility for the
 * target.
 * 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class TargettedMessageException extends RuntimeException {
  private TargettedMessage message;
  /** Construct a TargettedMessageException wrapping the supplied 
   * {@link TargettedMessage} object. The <code>targetid</code> field may be
   * left blank, in which case it will be automatically fixed up by the 
   * environment, probably to take account of the EL location of the current
   * operation.
   * 
   * @param message The message structure to wrap
   */
  public TargettedMessageException(TargettedMessage message) {
    this.message = message;
  }
  public TargettedMessageException(TargettedMessage message, Throwable cause) {
    super(cause);
    this.message = message;
  }
  public TargettedMessage getTargettedMessage() {
    return message;
  }
}
