/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.errorutil;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class TargettedMessage {
  public static final String TARGET_NONE = "No specific target";
  public String messagecode;
  public Object[] args = null;
  public String targetid = TARGET_NONE;
  public Class exceptionclass;

  public static final int SEVERITY_INFO = 0;
  public static final int SEVERITY_ERROR = 1;
  public int severity = 1;

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

  public TargettedMessage(String messagecode, Class exceptionclass) {
    this.messagecode = messagecode;
    this.exceptionclass = exceptionclass;
  }

  public TargettedMessage(String messagecode, Class exceptionclass,
      String targetid) {
    this.messagecode = messagecode;
    this.exceptionclass = exceptionclass;
    this.targetid = targetid == null? TARGET_NONE : targetid;
  }

  public TargettedMessage(String messagecode, Object[] args) {
    this.messagecode = messagecode;
    this.args = args;
  }
}
