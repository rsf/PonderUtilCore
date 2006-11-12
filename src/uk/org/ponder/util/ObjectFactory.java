/*
 * Created on 10 Nov 2006
 */
package uk.org.ponder.util;
/**
* <p>Interface defining a factory which can return an Object instance
* (possibly shared or independent) when invoked.
* 
* <p>This interface is typically used to encapsulate a generic factory  which
* returns a new instance (prototype) of some target object on each invocation.
* 
* <p>This interface has been introduced and removed from PonderUtilCore a number
* of times under the name "ObjectGetter" - on every occasion before now 
* it has been determined that its use represented some kind of design error. 
* The only current use is in {@link BeanExploder} so that it may receive
* inner bean definitions delivered via RSAC, resulting in a more concise
* Spring definition. This interface is defined here in PUC to avoid a dependence
* at this level on Spring. 
*/
public interface ObjectFactory {
  public Object getObject();
}
