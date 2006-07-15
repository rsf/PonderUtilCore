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
  public String messagecode;
  public Object[] args = null;
  public String targetid = TARGET_NONE;
  public Exception exception;

  public static final int SEVERITY_INFO = 0;
  public static final int SEVERITY_ERROR = 1;
  public int severity = SEVERITY_ERROR;

  public TargettedMessage(String messagecode, String targetid) {
    this.messagecode = messagecode;
    this.targetid = targetid == null? TARGET_NONE : targetid;
  }

  public TargettedMessage(String messagecode, Object[] args, String targetid) {
    this.messagecode = messagecode;
    this.args = args;
    this.targetid = targetid == null? TARGET_NONE : targetid;
  }

  public TargettedMessage(String messagecode) {
    this.messagecode = messagecode;
  }

  public TargettedMessage(String messagecode, Exception exception) {
    this.messagecode = messagecode;
    this.exception = exception;
  }

  public TargettedMessage(String messagecode, Exception exception,
      String targetid) {
    this.messagecode = messagecode;
    this.exception = exception;
    this.targetid = targetid == null? TARGET_NONE : targetid;
  }

  public TargettedMessage(String messagecode, Object[] args) {
    this.messagecode = messagecode;
    this.args = args;
  }
}
