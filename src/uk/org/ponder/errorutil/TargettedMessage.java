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
  public String targetid = TARGET_NONE;
  
  public TargettedMessage(String message, String targetid) {
    this.message = message;
    this.targetid = targetid;
  }
}
