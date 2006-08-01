/*
 * Created on 25 Jul 2006
 */
package uk.org.ponder.mapping;

import uk.org.ponder.beanutil.PathUtil;
import uk.org.ponder.stringutil.StringList;


public class ListBeanInvalidationModel implements BeanInvalidationModel {
  private StringList invalidpaths = new StringList();
  
  public BeanInvalidationIterator iterator() {
    return new BIMIterator();
  }
  
  public void clear() {
    invalidpaths.clear();
  }
  
  public static String matchPath(String spec, String path) {
    String togo = "";
    while (true) {
      if (spec == null || spec.equals("")) break;
      if (path == null || path.equals("")) return null;
      String spechead = PathUtil.getHeadPath(spec);
      String pathhead = PathUtil.getHeadPath(path);
      // if we fail to match on a specific component, fail.
      if (!(spechead.equals("*") || spechead.equals(pathhead))) return null;
      togo = PathUtil.composePath(togo, pathhead);
      spec = PathUtil.getFromHeadPath(spec);
      path = PathUtil.getFromHeadPath(path);
    }
    return togo;
  }
  
  public String invalidPathMatch(String spec) {
    for (int i = 0; i < invalidpaths.size(); ++ i) {
      String tocheck = invalidpaths.stringAt(i);
      String match = matchPath(spec, tocheck);
      if (match != null) return match;
    }
    return null;
  }
  
  class BIMIterator implements BeanInvalidationIterator {
    String path = "";
    
    public void invalidate(String childpath) {
      String fullpath = PathUtil.composePath(path, childpath);
      invalidpaths.add(fullpath);
    }

    public void pop() {
      String totail = PathUtil.getToTailPath(path);
      path = totail;
    }

    public void push(String childpath) {
      String fullpath = PathUtil.composePath(path, childpath);
      path = fullpath;
    }
    
  }
  
}
