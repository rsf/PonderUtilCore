/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface DARReceiver {
  // Users of DARApplier now take responsibility for navigating the
  // TargettedMessageList themselves.
  //public void setRootPath(String rootpath);
  public void addDataAlterationRequest(DataAlterationRequest toadd);
}
