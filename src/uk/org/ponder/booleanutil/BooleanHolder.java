/*
 * Created on 4 Sep 2007
 */
package uk.org.ponder.booleanutil;

public class BooleanHolder implements BooleanGetter {
  private Boolean bollean = Boolean.FALSE;
  
  public BooleanHolder() {}
  
  public BooleanHolder(boolean bollean) {
    this.bollean = bollean? Boolean.TRUE: Boolean.FALSE;
  }
  
  public Boolean get() {
    return bollean;
  }

  public void set(Boolean bollean) {
    this.bollean = bollean;
  }
}
