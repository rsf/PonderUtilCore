/*
 * Created on Nov 11, 2005
 */
package uk.org.ponder.conversion;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;

import uk.org.ponder.util.Denumeration;
import uk.org.ponder.util.EnumerationConverter;

/** An "aggregating" object parser/renderer that is capable of dealing with
 * vector (array, list) values by repeated invokation of a LeafObjectParser
 * for each parse/render. 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */
// Should convert this into an interface, but there is not really any kind of
// alternative implementation - all customizability is at the scalarparser level.
public class VectorCapableParser {
  private LeafObjectParser scalarparser;
  public void setScalarParser(LeafObjectParser scalarparser) {
    this.scalarparser = scalarparser;
  }
  /** 
   * Converts some form of multiple Strings into some form of multiple Objects.
   * @param toparse Some form of String collection - either
   * a String[] array, or any kind of Collection with String elements.
   * @param arraytype The class type of the element of returned array, or <code>null</code> if
   * a list (in fact an ArrayList) is required. 
   * @return Either a List or Array of Object according to whether the second parameter
   * represents a Class or is null. 
   */
  // This is the slightly tricky one. It looks like we might actually need to
  // create a whole new concrete collection - unless we can use clear() or something?
  // If we can't clear an existing collection, we probably can't replace it either.
  // Replacing it will probably make us guess the type wrong. This code will go into
  // DARApplier when it detects a vector-valued property, and a LOS-type argument.
  public Object parse(Object toparse, Class arraytype) {
    int size = EnumerationConverter.getEnumerableSize(toparse);
    Object togo = arraytype == null? new ArrayList(size) : Array.newInstance(arraytype, size);
    Denumeration denum = EnumerationConverter.getDenumeration(togo);
    for (Enumeration elemenum = EnumerationConverter.getEnumeration(toparse); elemenum.hasMoreElements();) {
      String elem = (String) elemenum.nextElement();
      Object converted = scalarparser.parse(elem);
      denum.add(converted);
    }
    return togo;
  }
  /** Converts some form of multiple objects into some form of multiple Strings.
   * @param torenders Either some form of Object array or a Collection.
   * @param The object into which the rendered objects are to be placed as Strings - either
   * a String[] array (of the correct size!) or a Collection.
   */
  // This code will go into ValueFixer.
  public void render(Object torenders, Object toreceive) {
    Denumeration denum = EnumerationConverter.getDenumeration(toreceive);
    for (Enumeration rendenum = EnumerationConverter.getEnumeration(torenders); rendenum.hasMoreElements();) {
      Object torender = rendenum.nextElement();
      String rendered = scalarparser.render(torender);
      denum.add(rendered);
    }
  }
  
}
