/*
 * Created on 28 Aug 2007
 */
package uk.org.ponder.beanutil.support;

import uk.org.ponder.reflect.MethodInvokingProxy;

/**
 * A useful "constant return" proxy for the case where one wants a simple method
 * binding that returns a particular result with no further effect. In RSF, for
 * example, this bean would be mapped to the path <code>constantReturn</code>
 * and so a method binding of <code>constantReturn.myResult</code> would have
 * the effect of simply returning the result <code>myResult</code>. This is
 * useful, for example, in the case where the substantive effect of the request
 * is handled purely by value bindings (possibly together with BeanGuards) but
 * one needs a key to supply to the NavigationCase navigation system.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class ConstantReturnInvoker implements MethodInvokingProxy {

  public Object invokeMethod(String name, Object[] args) {
    return name;
  }

}
