/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

import java.util.ArrayList;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DARList extends ArrayList{
  public DataAlterationRequest DARAt(int i) {
    return (DataAlterationRequest)get(i);
  }
}
