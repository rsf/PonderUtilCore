/*
 * Created on Jan 20, 2006
 */
package uk.org.ponder.xml;

import java.util.Iterator;
import java.util.Map;

import uk.org.ponder.streamutil.write.StringPOS;

public class XMLUtil {

  public static void dumpAttributes(Map attrs, XMLWriter xmlw) {
    for (Iterator keyit = attrs.keySet().iterator(); keyit.hasNext();) {
      String key = (String) keyit.next();
      String attrvalue = (String) attrs.get(key);
      dumpAttribute(key, attrvalue, xmlw);
    }
  }

  public static void dumpAttribute(String name, String value, XMLWriter xmlw) {
    xmlw.writeRaw(" ").writeRaw(name).writeRaw("=\"");
    xmlw.write(value);
    xmlw.writeRaw("\"");
  }

  /** A slow method for XML-encoding text **/
  public static String encode(String toencode) {
    StringPOS pos = new StringPOS();
    XMLWriter xmlw = new XMLWriter(pos);
    xmlw.write(toencode);
    return pos.toString();
  }
  
}
