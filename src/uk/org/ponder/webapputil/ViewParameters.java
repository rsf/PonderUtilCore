/*
 * Created on Oct 25, 2004
 */
package uk.org.ponder.webapputil;

import java.util.Map;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.FieldHash;

/**
 * The base class abstracting common functionality for specifying a view
 * state of a web application, independent of any particular application 
 * or url mapping technology.
 * In order to get this to go, you must supply a ViewStateHandler 
 * supplying concrete URL mapping and redirect logic via the set method,
 * and then specify this object as an exemplar to the ViewCollection.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
//for example:
//http://localhost:8080/FlowTalkServlet/faces/threaded-message?targetID=3mdugroBVjhPPTubjQkjAQjG
//|-----------------------------------------||---------------|
//       base url                              view id
// in fact, since we only ever have ONE view id, in fact we could simply
// omit baseurl!!

public abstract class ViewParameters implements Cloneable {
  public static final String CURRENT_REQUEST = "ViewParameters for current request";
  public static final String FAST_TRACK_ACTION = "Fast track action";
  private ViewStateHandler viewstatehandler;
  public void setViewStateHandler(ViewStateHandler handler) {
    this.viewstatehandler = handler;
  }
  public ViewStateHandler getViewStateHandler() {
    return viewstatehandler;
  }
  public String viewtoken;
  public String viewID;
  public String errorredirect;

  public abstract FieldHash getFieldHash();
  public abstract void clearActionState();
  public abstract void clearParams();
  public abstract void parsePathInfo(String pathinfo);
  public abstract String toPathInfo();

  /** Fill in the fields of the supplied view parameter object with data
   * from the supplied URL (which may be null), the supplied parameter map
   * and any other statically accessible sources of information.
   */
  public void fromRequest(Map parameters, String pathinfo) {
    parsePathInfo(pathinfo);
    getFieldHash().fromMap(parameters, this);
  }
  
  
// Note that copying does not copy the error token! All command links
// take the original request, and all non-command links should not share
// error state.
  public ViewParameters copyBase() {
    try {
      ViewParameters togo = (ViewParameters) clone(); 
      togo.viewtoken = null;
      return togo;
    }
    catch (Throwable t) {
      return null;
    } // CANNOT THROW! IDIOTIC SYSTEM!!   
  }

  /** Returns the "mid-portion" of the URL corresponding to these parameters,
   * i.e. /view-id/more-path-info?param1=val&param2=val 
   */
  public String toHTTPRequest() {
    StringList[] vals = getFieldHash().fromObj(this);
    CharWrap togo = new CharWrap();
    togo.append(toPathInfo());
    for (int i = 0; i < vals[0].size(); ++i) {
      togo.append(i == 0? '?' : '&');
      togo.append(vals[0].stringAt(i));
      togo.append("=");
      togo.append(vals[1].stringAt(i));
    }
    return togo.toString();
  }


  public String getFullURL() {
    return viewstatehandler.getFullURL(this);
  }

}