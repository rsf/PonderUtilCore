/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.errorutil;

import java.util.ArrayList;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.StringList;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class TargettedMessageList {
  private ArrayList errors;
  public int size() {
    return errors.size();
  }
  public void addMessage(TargettedMessage message) {
    if (nestedpath != null && nestedpath.length() != 0) {
      message.targetid = nestedpath + message.targetid;
    }
    errors.add(message);
  }
  public void addMessages(TargettedMessageList list) {
    for (int i = 0; i < list.size(); ++ i) {
      addMessage(list.messageAt(i));
    }
  }
  private StringList pathstack = new StringList();
  
  private String nestedpath = null;
  
  public void pushNestedPath(String nestedpath) {
    pathstack.add(this.nestedpath);
    if (nestedpath == null) {
		nestedpath = "";
	}
	if (nestedpath.length() > 0 && !nestedpath.endsWith(".")) {
		nestedpath += '.';
	}
	this.nestedpath = nestedpath;
  }
  
  public void popNestedPath() {
    int top = pathstack.size() - 1;
    nestedpath = pathstack.stringAt(top);
    pathstack.remove(top);
  }
  
  public TargettedMessage messageAt(int i) {
    return (TargettedMessage)errors.get(i);
  }
  public void clear() {
    errors.clear();
  }
  public String pack() {
    CharWrap togo = new CharWrap();
    for (int i = 0; i < size(); ++ i) {
      TargettedMessage mess = messageAt(i);
      togo.append("Target: " + mess.targetid + " message " + mess.message + "\n");
    }
    return togo.toString();
  }
}
