/*
 * Created on 22-Aug-2003
 */
package uk.org.ponder.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;


/**
 * The root of unchecked runtime exceptions thrown by the libraries.
 * There is a general movement to make most exceptions runtime exceptions (by not
 * only me!), since exception specifications often add verbosity without 
 * facility.
 * <p>Checked exceptions are most appropriate for signalling problems between
 * libraries with a wide degree of separation. Within a single body of code,
 * unchecked exceptions should be used to propagate error conditions to
 * the next boundary.
 * * http://c2.com/cgi/wiki?CheckedExceptionsConsideredHarmful
 * <p>This class has a useful (and growing) body of schemes for absorbing
 * the information from other types of exception and adding them to an
 * internal chain of exception causes. Eventually it should be extended
 * to encompass SQLExceptions, SAXExceptions, InvocationTargetExceptions 
 * and the like. 
 * <p> What we wish to preserve is
 * a) the ultimate stack trace from the cause of the problem and 
 * b) a set of increasingly detailed messages that can be accreted onto
 * the exception as it winds up the stack.
 * @author Bosmon
 */
public class UniversalRuntimeException extends RuntimeException implements WrappingException {
  private Throwable targetexception;
  private String message;
  public UniversalRuntimeException(String s) {
    message = s;
  }
  public String getMessage() {
    return message;
  }
  public UniversalRuntimeException(Throwable t) {
    message = t.getMessage();
    targetexception = t;
  }
  public Throwable getTargetException() {
    return targetexception;
  }
  
  public static UniversalRuntimeException accumulateMsg(Throwable t, String fullmsg) {
    // QQQQQ Should we unwrap any other types?
     UniversalRuntimeException togo;
     if (t instanceof UniversalRuntimeException) {
       togo = (UniversalRuntimeException) t;
     }
     if (t instanceof InvocationTargetException) {
       InvocationTargetException ite = (InvocationTargetException)t;
       return accumulateMsg(ite.getTargetException(), fullmsg);
       }
     else {
       togo = new UniversalRuntimeException(t);
     }
     togo.message = fullmsg;
     return togo;
   }
  /** Accumulates the message supplied message onto the beginning of 
   * any existing exception message, and wraps supplied exception as the
   * target exception of the returned UniversalRuntimeException.
   * <p>If the supplied exception is already a UniversalRuntimeException,
   * the same object is returned.
   * <p>If the supplied exception is an InvocationTargetException, it is
   * unwrapped and its target exception becomes the wrapped exception.
   * @param t An encountered exception, to be wrapped.
   * @param fullmsg The message to be added to the exceptions information.
   * @return 
   */
  public static UniversalRuntimeException accumulate(Throwable t, String extradetail) {
    UniversalRuntimeException togo = accumulateMsg(t, extradetail + "\n" + t.getMessage()+ "\n");
    return togo;
  }
  // QQQQQ move these three methods to static utility for all WrappingExceptions
  public void printStackTrace() {
    if (targetexception != null) {
      System.err.println(getMessage());
      targetexception.printStackTrace();
    }
    else
      super.printStackTrace();
  }
  public void printStackTrace(PrintWriter pw) {
    if (targetexception != null) {
      pw.print(getMessage());
      targetexception.printStackTrace(pw);
    }
    else
      super.printStackTrace(pw);
  }
  public void printStackTrace(PrintStream ps) {
     if (targetexception != null) {
       ps.print(getMessage());
       targetexception.printStackTrace(ps);
     }
     else
       super.printStackTrace(ps);
   }
}
