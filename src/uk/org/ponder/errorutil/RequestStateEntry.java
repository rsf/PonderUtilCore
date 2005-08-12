/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.errorutil;

/**
 * Keyed by a token which identifies a view that has been presented to the user.
 * The token is allocated afresh on each POST, and redirected to a GET view
 * that shares the same token. If the POST is non-erroneous, the rsvc can
 * be blasted, however if it is in error, it will be kept so that the values
 * can be resubmitted back to the user. We should CHANGE the old behaviour
 * that carried along the old token - we simply want to make a new
 * token and erase the entry for the old one.
 * 
 * When a further POST is made again from that view,  
 * it should mark that token as USED, possibly by simply removing the values
 * from the cache.
 * 
 * When the user tries to make a submission from an erased token, there
 * should be some (ideally) application-defined behaviour. But in general
 * we don't have much option but to erase their data, since it will probably
 * conflict somehow. 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class RequestStateEntry {
  public String globaltargetid = null;
  
  public String incomingtokenID;
  public String outgoingtokenID;
  public TargettedMessageList errors = new TargettedMessageList();
  
  // rewrites any queued errors with no target to refer to the
  // specified target.
  public void setGlobalTarget(String globalid) {
    globaltargetid = globalid;
  }
}
