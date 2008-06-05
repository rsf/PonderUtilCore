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
    Object data = toshape.data;
    String leafdata = null;
    if (data instanceof Boolean) {
      leafdata = data == Boolean.TRUE ? "true" : "false";
    }
    else if (data instanceof String[]) {
      leafdata = ((String[])toshape.data)[0];
    }
    else {
      leafdata = (String)toshape.data;      
    }

    DataAlterationRequest togo = new DataAlterationRequest(toshape.path, 
        parser.parse(leafdata), toshape.type);
    return togo;
  }

}
