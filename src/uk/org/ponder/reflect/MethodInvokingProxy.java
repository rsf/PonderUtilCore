/*
 * Created on Nov 22, 2005
 */
package uk.org.ponder.reflect;

/** When encountered in a bean model at path where a method invocation is being
 * attempted via EL, will accept the invocation via this method call rather than
 * having it applied reflectively.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public interface MethodInvokingProxy {
  public Object invokeMethod(String name, Object[] args);
}
