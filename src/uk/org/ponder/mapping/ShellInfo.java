/*
 * Created on 3 Aug 2007
 */
package uk.org.ponder.mapping;

/**
 * A structure of "shells" through a bean model. The 0th element of
 * <code>shells</code> will be the bean root requested, segments[0] will be a
 * property request on this bean. The array of shells will be filled out until a
 * property can no longer be navigated.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class ShellInfo {
  public String[] segments;
  public Object[] shells;
}
