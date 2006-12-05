/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.errorutil;

import java.io.Serializable;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class TargettedMessage implements Serializable {
  public static final String TARGET_NONE = "No specific target";
  /**
   * A list of possible message codes, in descending order of specificity. This
   * is the same semantics as Spring's "MessageSourceResolvable" system.
   */
  public String[] messagecodes;
  public Object[] args = null;
  public String targetid = TARGET_NONE;
  public Exception exception;

  public static final int SEVERITY_INFO = 0;
  public static final int SEVERITY_ERROR = 1;
  public int severity = SEVERITY_ERROR;

  public void updateMessageCode(String messagecode) {
    messagecodes = new String[] { messagecode };
  }

  public void updateTarget(String targetid) {
    this.targetid = targetid == null ? TARGET_NONE
        : targetid;
  }

  public String acquireMessageCode() {
    return messagecodes == null ? null
        : messagecodes[messagecodes.length - 1];
  }

  public TargettedMessage() {
  }

  public TargettedMessage(String messagecode, String targetid) {
    updateMessageCode(messagecode);
    this.targetid = targetid == null ? TARGET_NONE
        : targetid;
  }

  public TargettedMessage(String messagecode, Object[] args, String targetid) {
    updateMessageCode(messagecode);
    updateTarget(targetid);
    this.args = args;
  }

  public TargettedMessage(String messagecode) {
    updateMessageCode(messagecode);
  }

  public TargettedMessage(String messagecode, Exception exception) {
    updateMessageCode(messagecode);
    this.exception = exception;
  }

  public TargettedMessage(String messagecode, Exception exception,
      String targetid) {
    updateMessageCode(messagecode);
    updateTarget(targetid);
    this.exception = exception;
  }

  public TargettedMessage(String messagecode, Object[] args) {
    updateMessageCode(messagecode);
    this.args = args;
  }

  public TargettedMessage(String messagecode, Object[] args, int severity) {
    updateMessageCode(messagecode);
    this.args = args;
    this.severity = severity;
  }

  public TargettedMessage(String[] messagecodes, Object[] args, String targetid) {
    updateTarget(targetid);
    this.messagecodes = messagecodes;
    this.args = args;
  }

  public TargettedMessage(String[] messagecodes, Object[] args,
      String targetid, int severity) {
    updateTarget(targetid);
    this.messagecodes = messagecodes;
    this.args = args;
    this.severity = severity;
  }
}
