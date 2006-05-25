/*
 * Created on Nov 23, 2005
 */
package uk.org.ponder.saxalizer.mapping;

import java.util.List;

import uk.org.ponder.arrayutil.TypedListWrapper;

public class MappingLoaderList extends TypedListWrapper {
  
  public void setMappings(List mappings) {
    wrapped = mappings;
  }

  public MappingLoader mappingAt(int i) {
    return (MappingLoader) wrapped.get(i);
  }
  
  public Class getWrappedType() {
    return MappingLoader.class;
  }
}
