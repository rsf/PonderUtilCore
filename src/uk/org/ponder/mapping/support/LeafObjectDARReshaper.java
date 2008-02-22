/*
 * Created on Apr 11, 2006
 */
package uk.org.ponder.mapping.support;

import uk.org.ponder.conversion.LeafObjectParser;
import uk.org.ponder.mapping.DARReshaper;
import uk.org.ponder.mapping.DataAlterationRequest;

public class LeafObjectDARReshaper implements DARReshaper {

  private LeafObjectParser parser;

  public LeafObjectDARReshaper(LeafObjectParser parser) {
    this.parser = parser;
  }
  
  public DataAlterationRequest reshapeDAR(DataAlterationRequest toshape) {
    String leafdata = toshape.data instanceof String[] ? 
        ((String[])toshape.data)[0] : (String)toshape.data;
    DataAlterationRequest togo = new DataAlterationRequest(toshape.path, 
        parser.parse(leafdata), toshape.type);
    return togo;
  }

}
