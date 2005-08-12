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
  public String message;
  public Object[] args = null;
  public String targetid = TARGET_NONE;
  public Class exceptionclass;
  
  public static final int SEVERITY_INFO = 0;
  public static final int SEVERITY_ERROR = 1;
  public int severity = 1;
  
  public TargettedMessage(String message, String targetid) {
    this.message = message;
    this.targetid = targetid;
  }
  public TargettedMessage(String message, Object[] args, String targetid) {
    this.message = message;
    this.args = args;
    this.targetid = targetid;
  }
  public TargettedMessage(String message) {
    this.message = message;
  }
  public TargettedMessage(String message, Class exceptionclass) {
    this.message = message;
    this.exceptionclass = exceptionclass;
  }
  public TargettedMessage(String message, Class exceptionclass, String targetid) {
    this.message = message;
    this.exceptionclass = exceptionclass;
    this.targetid = targetid;
  }
}
