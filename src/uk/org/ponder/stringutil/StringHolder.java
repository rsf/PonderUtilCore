/*
 * Created on 20-May-2006
 */
package uk.org.ponder.stringutil;

/** Class holding a single String value, accessible in practically every
 * possible way.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class StringHolder implements StringGetter {
  private String value;

  public StringHolder() {}
  
  public StringHolder(String value) {
    this.value = value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void append(String toappend) {
    this.value = this.value + toappend;
  }
  
  public String get() {
    return value;
  }
  
}
