package uk.org.ponder.saxalizer.support;

import uk.org.ponder.saxalizer.SAMSList;

/* SAXAccessMethodHash provides a quick lookup of an XML tag name to a
 * SAXAccessMethod object corresponding to the tag, for a particular 
 * SAXalizable class.
 */
public class SAXAccessMethodHash {
  /** Class <code>SAXAccessMethodHash</code> stores a hashtable of
   *   <code>set</code> methods that have been
   *   discovered in a class to allow them to be easily invoked when
   *   an object of the type it accepts is at hand. */
  public SAXAccessMethod[] methods;
  /*
   * @param m The <code>SAXAccessMethodSpec</code> for the method to be added to the hash
   * @param parentclazz A class object representing the class actually possessing the 
   * method.
   */
  /** @param tag The tag name (<code>Xxxx</code> stub) of the
   * <code>AccessMethod</code> object that is required.
   * @return The required <code>AccessMethod</code> */
  public SAXAccessMethod get(String tag) {
    if (methods == null)
      return null;
    for (int i = methods.length - 1; i >= 0; --i) {
      if (methods[i].tagname.equals(tag)) {
        return methods[i];
      }
    }
    return null;
  }
    
  public SAXAccessMethodHash(SAMSList samslist, Class parentclass) {
    methods = new SAXAccessMethod[samslist.size()];
    for (int i = 0; i < samslist.size(); ++i) {
      this.methods[i] = new SAXAccessMethod(samslist.SAMSAt(i), parentclass);
    }
  }
  /** An iterator for all gettable methods */
  public class HashSAMIterator implements SAMIterator {
    //int methodtype;
    int currentindex = -1;
    public HashSAMIterator() {
      stepAlong();
    }
    private void stepAlong() {
      while (++currentindex < methods.length) {
        if (methods[currentindex].canGet()) break;
        //if (methodtype == SAXAccessMethodSpec.SET_METHOD && methods[currentindex].setmethod != null) break;
      }
    }
    public boolean valid() {
      return currentindex < methods.length;
    }
    public void next() {
      stepAlong();
    }
    public SAXAccessMethod get() {
      return methods[currentindex];
    }
  }
  
  public SAMIterator getGetEnumeration() {
    return new HashSAMIterator();
  }
}
