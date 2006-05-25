/*
 * Created on Nov 17, 2005
 */
package uk.org.ponder.conversion;

import uk.org.ponder.saxalizer.SAXalXMLProvider;

/**
 * Converts objects to and from strings, using an XML format for composite objects,
 * but a simple String rendering for leaf objects. In the latter case, type
 * information must be maintained separately after serialization if you expect
 * to be able to reconstruct the object.
 * <p>
 * This class is a slight kludge until we think of some sensible reform of the
 * serialization morass. The point is we are really disinclined to construct XML
 * parsers just to parse, for example, String objects. We also note that the
 * SAXalizer/DeSAXalizer infrastructure is just not geared up for dealing with
 * documents where the root node is actually a leaf.
 * <p>
 * Finally, we note that in every practical case we should be able to determine
 * on delivery whether a given target property actually *IS* a leaf type or not,
 * meaning armature such as &lt;string&gt;Yes it's a string&lt;/string&gt;
 * *ought* to be unnecessary.
 * <p>
 * This should all be solved once we move over to some sort of mature framework
 * like JAXB 2.0 or JiBX.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ConvertUtil {
  public static String render(Object torender, SAXalXMLProvider xmlprovider) {
    StaticLeafParser leafparser = xmlprovider.getMappingContext().saxleafparser;
    if (leafparser.isLeafType(torender.getClass())) {
      return leafparser.render(torender);
    }
    else {
      return xmlprovider.toString(torender);
    }
  }
/** If the class argument is null, the string will be interpreted as XML.
 */
  public static Object parse(String toparse, SAXalXMLProvider xmlprovider,
      Class targetclass) {
    StaticLeafParser leafparser = xmlprovider.getMappingContext().saxleafparser;
    if (targetclass != null && leafparser.isLeafType(targetclass)) {
      return leafparser.parse(targetclass, toparse);
    }
    else {
      return xmlprovider.fromString(toparse);
    }
  }
}
