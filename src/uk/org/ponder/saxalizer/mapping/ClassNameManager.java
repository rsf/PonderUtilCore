/*
 * Created on Feb 13, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.util.HashMap;

/**
 * Managing the mapping of nicknames of polymorphic classes using the
 * "tagname*" scheme in SAXAccessMethodSpecs and the 'type="nickname"'
 * attribute, and also allows type of object to be determined based
 * on root tag type.
 * 
 * @author Bosmon
 */
public class ClassNameManager {
  private HashMap forwardnick = new HashMap();
  private HashMap backwardnick = new HashMap();
  private static ClassNameManager instance = new ClassNameManager();
  public static ClassNameManager instance() {
    return instance;
  }
  public void registerClass(String nickname, Class clazz) {
    forwardnick.put(nickname, clazz);
    backwardnick.put(clazz, nickname);
  }
  public Class findClazz(String classname) {
    return (Class)forwardnick.get(classname);
  }
  public String getClassName(Class clazz) {
    return (String)backwardnick.get(clazz);
  }
}
