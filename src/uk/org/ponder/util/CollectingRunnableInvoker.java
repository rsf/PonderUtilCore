/*
 * Created on 1 Dec 2006
 */
package uk.org.ponder.util;

import java.util.List;

public class CollectingRunnableInvoker implements RunnableInvoker {
  private List wrappers;
  public void setWrappers(List wrappers) {
    this.wrappers = wrappers;
  }
  public void invokeRunnable(Runnable toinvoke) {
    invokeWrappers(wrappers, toinvoke);
  }
  
  public static void invokeWrappers(final List wrappers, final Runnable toinvoke) {
    if (wrappers == null) {
      toinvoke.run();
    }
    else {
      new Runnable() {
        int i = 0;
        public void run() {
          if (i == wrappers.size()) toinvoke.run();
          else {
            RunnableInvoker invoker = (RunnableInvoker) wrappers.get(i);
            ++i;
            invoker.invokeRunnable(this);
          }
        }
      }.run();
    }
  }

}
