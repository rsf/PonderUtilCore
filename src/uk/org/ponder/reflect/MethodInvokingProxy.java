/*
 * Created on Nov 22, 2005
 */
package uk.org.ponder.reflect;

public interface MethodInvokingProxy {
  public Object invokeMethod(String name, Object[] args);
}
