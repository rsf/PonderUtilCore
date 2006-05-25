/*
 * Created on 25-Oct-2003
 */
package uk.org.ponder.arrayutil;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import uk.org.ponder.util.AssertionException;

/**
 * Contains useful utility methods for operating on java.util.List instances.
 * @author Bosmon
 *
 * The class 
 */
public class ListUtil {
  public static final List EMPTY_LIST = new ArrayList(0);
  
  public static Enumeration getEnumeration(final List list) {
    return new Enumeration () {
      int pos = 0;      
      public boolean hasMoreElements() {
        return pos < list.size();
      }
      public Object nextElement() {
        return list.get(pos++);
      }
    };
  } 
  
  public static void append(List list, Object[] toappend) {
    if (toappend != null) {
      for (int i = 0; i < toappend.length; ++ i) {
        list.add(toappend[i]);
      }
    }
  }
  
  public static void setSafe(List list, int index, Object toset) {
    if (index >= list.size()) {
      expandSize(list, index + 1);
    }
    list.set(index, toset); 
  }


  public static Object getSafe(List list, int index) {
    if (index >= list.size()) {
      expandSize(list, index + 1);
    }
    return list.get(index);
  }
  
  public static void expandSize(List list, int newsize) {
    for (int i = list.size(); i < newsize; ++ i) {
      list.add(null);
      }
  }
  
  public static void restrictSize(List list, int newsize) {
    for (int i = list.size() - 1; i >= newsize; -- i) {
      list.remove(i);
    }
  }
  public static void setSize(List list, int newsize) {
    if (newsize > list.size()) {
      expandSize(list, newsize);
    }
    else if (newsize < list.size()) {
      restrictSize(list, newsize);
    }
  }
  public static Object pop(List list) {
    int size = list.size();
    if (size == 0) {
      throw new AssertionException("Attempt to pop from empty stack");
    }
    else {
      Object togo = list.get(size - 1);
      list.remove(size - 1);
      return togo;
    }
  }
  
  public static Object peek(List list) {
    return list.size() == 0 ? null : list.get(list.size() - 1);
  }
}
