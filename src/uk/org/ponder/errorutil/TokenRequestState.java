/*
 * Created on Aug 5, 2005
 */
package uk.org.ponder.errorutil;

import java.util.Date;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class TokenRequestState {
  public Date created = new Date();
  public String tokenid;
  int redirectcount = 0;
  
  public RequestSubmittedValueCache rsvc;
  public RequestStateEntry ese;
}
