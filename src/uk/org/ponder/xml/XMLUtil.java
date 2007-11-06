/*
 * Created on Jan 20, 2006
 */
package uk.org.ponder.xml;

import java.util.Iterator;
import java.util.Map;

import uk.org.ponder.streamutil.write.StringPOS;
import uk.org.ponder.stringutil.ByteToCharBase64;
import uk.org.ponder.stringutil.CharToByteBase64;
import uk.org.ponder.stringutil.CharWrap;

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
  
  /** Flatten a id (assumed generated via the EighteenIDGenerator) into
   * one that may form a valid XML ID.
   * http://www.w3.org/TR/REC-xml/#NT-Name
   */
  public static String produceXMLID(String id) {
    int oldi = CharToByteBase64.pem_reverse_array[id.charAt(0)];
    if (oldi == -1) {
      throw new IllegalArgumentException("First character of id " + id + " not recognized");
    }
    CharWrap togo = new CharWrap(24);
    togo.append(ByteToCharBase64.pem_array[oldi % 52]);
    for (int i = 1; i < id.length(); ++ i) {
      char c = id.charAt(i);
      togo.append(c == '('? '.' : c);
    }
    return togo.toString();
  }
  
  public static final boolean isXMLNameChar(char c) {
    return Character.isLetter(c) || Character.isDigit(c) || c == '.' || c == '-' || c == '_' || c == ':';
  }
  
}
