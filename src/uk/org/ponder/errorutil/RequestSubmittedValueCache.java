/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.errorutil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class RequestSubmittedValueCache {
  public String errortoken;
  public void addEntry(SubmittedValueEntry sve) {
    idmap.put(sve.componentid, sve);
    entries.add(sve);
  }
  
  public SubmittedValueEntry byID(String componentid) {
    return (SubmittedValueEntry)idmap.get(componentid);
  }
  HashMap idmap = new HashMap();
  public ArrayList entries = new ArrayList();
  
}
