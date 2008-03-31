/*
 * Created on 6 Nov 2007
 */
package uk.org.ponder.test.dateutil;

import uk.org.ponder.hashutil.EighteenIDGenerator;
import uk.org.ponder.hashutil.IDGenerator;
import uk.org.ponder.xml.XMLUtil;
import junit.framework.TestCase;

public class TestXMLUtil extends TestCase {
  public void testFlatten() {
    IDGenerator idGenerator = new EighteenIDGenerator();
    for (int i = 0; i < 100; ++ i) {
      String original = idGenerator.generateID();
      String flat = XMLUtil.produceXMLID(original);
//      System.out.println(original + " " + flat);
      assertTrue(Character.isLetter(flat.charAt(0)));
      for (int j = 1; j < flat.length(); ++ j) {
       assertTrue(XMLUtil.isXMLNameChar(flat.charAt(j)));
      }
    }
  }
}
