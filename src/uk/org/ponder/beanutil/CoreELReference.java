/*
 * Created on 23 Jul 2007
 */
package uk.org.ponder.beanutil;

/** Recognizable holder of an EL reference as a String. This will be
 * renamed to ELReference with RSF 0.8 and fused with the version there. **/

public class CoreELReference {
  public CoreELReference() {}
  public CoreELReference(String value) {
    String stripped = BeanUtil.stripEL(value);
    this.value = stripped == null? value : stripped;
  }
  public String value;
  public static CoreELReference make(String value) {
    return value == null? null : new CoreELReference(value);
  }
}
