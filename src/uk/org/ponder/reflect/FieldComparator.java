/*
 * Created on Oct 15, 2004
 */
package uk.org.ponder.reflect;

import java.util.Comparator;

import uk.org.ponder.saxalizer.AccessMethod;
import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * Returns a comparator capable of comparing any objects with {valid
 * SAXalizer mappings for a get accessor method with a given name} by
 * a member mapped by the accessor.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class FieldComparator implements Comparator {

  public static AccessMethod findSingleGetter(Class objclass,
      SAXalizerMappingContext context, String tagname) {
    MethodAnalyser ma = context.getAnalyser(objclass);
    AccessMethod method = ma.getAccessMethod(tagname);
    if (!method.canGet() || method.isDenumerable()) {
      throw new UniversalRuntimeException(
          "Located access method of unsuitable type for name " + tagname
              + " in " + objclass);
    }
    return method;
  }
  
  private AccessMethod accessor;
  public FieldComparator(Class objclass, SAXalizerMappingContext context,
      String fieldname) {
    accessor = findSingleGetter(objclass, context, fieldname);
  }

  public int compare(Object o1, Object o2) {
    Comparable field1 = (Comparable) accessor.getChildObject(o1);
    Comparable field2 = (Comparable) accessor.getChildObject(o2);
    return field1.compareTo(field2);
  }

}