/*
 * Created on Nov 17, 2004
 */
package uk.org.ponder.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A Map wrapper that essentially only allows the get operation to proceed.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public abstract class ObstinateMap implements Map {

  public int size() {
    throw new UniversalRuntimeException("Cannot compute size of " + getClass() + " map");
  }

  public void clear() {
    throw new UniversalRuntimeException("Cannot clear " + getClass() + " map");  
  }

  public boolean isEmpty() {
    return false;
  }
 
  public boolean containsValue(Object value) {
    throw new UniversalRuntimeException("Cannot query value in " + getClass() + " map");  
  }

  public Collection values() {
    throw new UniversalRuntimeException("Cannot assemble values in " + getClass() + " map");  
  }

  public void putAll(Map t) {
    throw new UniversalRuntimeException("Cannot modify " + getClass() + " map");      
  }

  public Set entrySet() {
    throw new UniversalRuntimeException("Cannot assemble entries in " + getClass() + " map");  
  }

  public Set keySet() {
    throw new UniversalRuntimeException("Cannot assemble keys in " + getClass() + " map");   
  }
  
  public Object remove(Object key) {
    throw new UniversalRuntimeException("Cannot modify " + getClass() + " map");    
  }
  
  public Object put(Object key, Object value) {
    throw new UniversalRuntimeException("Cannot modify " + getClass() + " map");    
  }

}
