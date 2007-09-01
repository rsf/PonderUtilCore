/*
 * Created on 9 Aug 2007
 */
package uk.org.ponder.beanutil.support;

import uk.org.ponder.beanutil.BeanPredicateModel;
import uk.org.ponder.beanutil.PathUtil;
import uk.org.ponder.stringutil.StringList;

public class ListBeanPredicateModel implements BeanPredicateModel {
  private StringList paths = new StringList();

  public void clear() {
    paths.clear();
  }

  public static String matchPath(String spec, String path) {
    String togo = "";
    while (true) {
      if (spec == null || spec.equals(""))
        break;
      if (path == null || path.equals(""))
        return null;
      String spechead = PathUtil.getHeadPath(spec);
      String pathhead = PathUtil.getHeadPath(path);
      // if we fail to match on a specific component, fail.
      if (!(spechead.equals("*") || spechead.equals(pathhead)))
        return null;
      togo = PathUtil.composePath(togo, pathhead);
      spec = PathUtil.getFromHeadPath(spec);
      path = PathUtil.getFromHeadPath(path);
    }
    return togo;
  }

  public String findMatch(String spec, boolean isspec) {
    for (int i = 0; i < paths.size(); ++i) {
      String tocheck = paths.stringAt(i);
      String match = isspec? matchPath(spec, tocheck) : matchPath(tocheck, spec);
      if (match != null)
        return match;
    }
    return null;
  }

  public void setPaths(StringList paths) {
    for (int i = 0; i < paths.size(); ++ i) {
      addPath(paths.stringAt(i));
    }
  }
  
  public void addPath(String path) {
    paths.add(path);
  }

  public boolean isMatch(String path) {
    return findMatch(path, false) != null;
  }
}
