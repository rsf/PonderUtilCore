/*
 * Created on 23 Jul 2007
 */
package uk.org.ponder.beanutil;

/** Recognizable holder of an EL reference as a String **/

public class ELReference {
  public ELReference() {}
  public ELReference(String value) {
    String stripped = BeanUtil.stripEL(value);
    this.value = stripped == null? value : stripped;
  }
  public String value;
  public static ELReference make(String value) {
    return value == null? null : new ELReference(value);
  }
}
