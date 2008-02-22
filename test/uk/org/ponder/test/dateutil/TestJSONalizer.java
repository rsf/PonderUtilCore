/*
 * Created on 22 Feb 2008
 */
package uk.org.ponder.test.dateutil;

import uk.org.ponder.conversion.SerializationProvider;
import uk.org.ponder.json.support.JSONProvider;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import junit.framework.TestCase;

public class TestJSONalizer extends TestCase {
  private SAXalizerMappingContext smc = new SAXalizerMappingContext();
  private SerializationProvider json;
  protected void setUp() throws Exception {
    super.setUp();
    JSONProvider conv = new JSONProvider();
    conv.setMappingContext(smc);
    json = conv;
  }
 
  
  public void testArrays() {
   
    String[] empty = new String[] {};
    String ar1 = json.toString(empty);
    assertEquals(ar1, "[]");
    String[] read = (String[]) json.fromString(ar1);
    assertArrays(empty, read);
    
    String[] one = new String[] {"1"};
    String ar2 = json.toString(one);
    assertEquals(ar2, "[\"1\"]");
    String[] read2 = (String[]) json.fromString(ar2);
    assertArrays(one, read2);
    
    String[] two = new String[] {"\"3\\\\4/", null};
    String ar3 = json.toString(two);
    assertEquals(ar3, "[\"\\\"3\\\\\\\\4\\/\", null]");
    String[] read3 = (String[]) json.fromString(ar3);
    assertArrays(two, read3);
    
  }


  private void assertArrays(String[] one, String[] two) {
    assertEquals(one.length, two.length);
    for (int i = 0; i < one.length; ++ i) {
      assertEquals(one[i], two[i]);
    }
  }
}
