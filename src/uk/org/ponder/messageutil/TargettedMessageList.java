/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.messageutil;

import java.io.Serializable;
import java.util.ArrayList;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.StringList;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
// NB - this class is ALMOST identical to the Spring "Errors" interface,
// which I was put off since the only implementation there is "BindException"
// which couples to all sorts of other greasy stuff, including the dreaded
// BeanWrapper. However, it may be worth going back to Errors and Validator
// at least, which are fairly clean in of themselves.
public class TargettedMessageList implements Serializable {
  private ArrayList errors = new ArrayList();
  public int size() {
    return errors.size();
  }
  public void addMessage(TargettedMessage message) {
    if (nestedpath != null && nestedpath.length() != 0 && 
        !message.targetid.equals(TargettedMessage.TARGET_NONE)) {
      message.targetid = nestedpath + message.targetid;
    }
    errors.add(message);
  }
  public void addMessages(TargettedMessageList list) {
    for (int i = 0; i < list.size(); ++ i) {
      addMessage(list.messageAt(i));
    }
  }
  
  public boolean isError() {
    for (int i = 0; i < size(); ++ i) {
      TargettedMessage mess = messageAt(i);
      if ((mess.severity & 1) == TargettedMessage.SEVERITY_ERROR) return true;
    }
    return false;
  }
  
  private StringList pathstack;
  
  private String nestedpath = null;
  
  public void pushNestedPath(String extrapath) {
    if (extrapath == null) {
		extrapath = "";
	}
	if (extrapath.length() > 0 && !extrapath.endsWith(".")) {
		extrapath += '.';
	}
    if (pathstack == null) {
      pathstack = new StringList();
    }
    pathstack.add(extrapath);
	this.nestedpath = extrapath;
  }
  
  public void popNestedPath() {
    int top = pathstack.size() - 1;
    nestedpath = pathstack.stringAt(top);
    pathstack.remove(top);
  }
  
  public TargettedMessage messageAt(int i) {
    return (TargettedMessage)errors.get(i);
  }
  
  public void setMessageAt(int i, TargettedMessage message) {
    errors.set(i, message);
  }
  
  public void clear() {
    errors.clear();
  }
  public String pack() {
    CharWrap togo = new CharWrap();
    for (int i = 0; i < size(); ++ i) {
      TargettedMessage mess = messageAt(i);
      togo.append("Target: " + mess.targetid + " message " + mess.acquireMessageCode() + "\n");
    }
    return togo.toString();
  }
  
  public StringList render(MessageLocator locator) {
    StringList togo = new StringList();
    for (int i = 0; i < size(); ++ i) {
      TargettedMessage message = messageAt(i);
      togo.add(locator.getMessage(message.messagecodes, message.args));
    }
    return togo;
  }
  
}
