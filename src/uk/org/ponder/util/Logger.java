package uk.org.ponder.util;

import java.io.PrintStream;
import java.io.FileOutputStream;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.org.ponder.byteutil.ByteWrap;

import uk.org.ponder.intutil.intVector;

import uk.org.ponder.arrayutil.ArrayUtil;

/** A useful logging class that <ul>
 * <li> Compactly annotates the output from different threads
 * <li> Supports multiple levels of logging message priority, held in a stack
 * <li> Renders unprintable Unicode characters in a detectable form
 * <li> Can redirect output to console or to file </ul>
 * Coming soon: 
 * <li>
 * <ul> Some form of timestamping. </ul>
 */

public class Logger {
  // The ponder Logger is now deprecated. All new logging should be done
  // via this commons standard logger.
  public static Log log = LogFactory.getLog("PonderUtilCore");
  public static final boolean debugmode = true;
  public static PrintStream logger = System.out;

  // the idea is that all codes less severe than ASSERTION will be stripped out before release,
  // but during testing, at least codes more severe than INFORMATIONAL will be interesting.
  public static final int DEBUG_ENTIRELY_CRITICAL = 0;
  public static final int DEBUG_SEVERE = 5;
  public static final int DEBUG_WARNING = 10;
  public static final int DEBUG_QUITE_INTERESTING = 12;
  public static final int DEBUG_ASSERTION = 13;
  public static final int DEBUG_INFORMATIONAL = 15;
  public static final int DEBUG_EXTRA_INFO = 17;
  public static final int DEBUG_SUBATOMIC = 20;
  public static final int DEBUG_PAINTING = 30;

  public static int DEBUG_LEVEL = DEBUG_INFORMATIONAL;

  private static String[] threadnames = new String[0];
  private static intVector debuglevelstack = new intVector(10);

  /** Sets the destination for debugging messages to a file with the specified name.
   * If this file cannot be opened, output will revert to the console.
   * If <code>null</code> is supplied for the filename, the filename defaults to
   * <code>javalog.txt</code>
   * @param filename The filename to receive logging output.
   */

  public static void setDestinationFile(String filename) {
    if (filename == null)
      filename = "javalog.txt";
    try {
      logger = new PrintStream(new FileOutputStream(filename));
    }
    catch (Exception e) {
      e.printStackTrace(); 
      logger = System.out;
    }
  }

  /** Sets the destination for debugging messages to the console.
   */

  public static void setConsoleOutput() {
    if (logger != System.out) {
      logger.close();
    }
    logger = System.out;
  }

  /** Sets the current debugging level to the specified level. Messages with priorities
   * lower than this level (i.e. with higher integer values) will be suppressed.
   * @param debuglevel The new debugging level to operate at.
   */

  public static void setDebugLevel(int debuglevel) {
    DEBUG_LEVEL = debuglevel;
  }

  /** Pushes the specified debugging level onto the stack, and sets the current debugging
   * level to this level.
   * @param debuglevel The new debugging level.
   */
  public static void pushDebugLevel(int debuglevel) {
    debuglevelstack.addElement(DEBUG_LEVEL);
    setDebugLevel(debuglevel);
  }

  /** Pops the last debugging level that was pushed onto the stack, and sets the
   * current debugging level to the new stack top level.
   */

  public static void popDebugLevel() {
    // can this actually have been wrong all this time? Looks like it leaves the
    // debugging level at the old stack top!
    setDebugLevel(debuglevelstack.popElement());
  }

  /** Allows clients to detect whether a given debugging priority level passes the current
   * threshold.
   * @param debuglevel The debug level to be checked.
   * @return <code>true</code> if a message of the specified priority level would be
   * printed.
   */

  public static boolean passDebugLevel(int debuglevel) {
    return debuglevel <= DEBUG_LEVEL && debugmode;
  }

  /** Prints a message with a particular priority level, followed by a newline.
   * @param toprint The message to be printed.
   * @param debuglevel The priority level of the message. If the current priority level
   * is higher than the supplied level, printing of the message will be suppressed.
   * If the current debug level is <code>DEBUG_ASSERTION</code>, a message of with
   * priority <code>DEBUG_ASSERTION</code> will trigger an AssertionException.
   */

  public static void println(String toprint, int debuglevel) {
    if (debuglevel <= DEBUG_LEVEL && debugmode)
      println(toprint);
    // release mode is detected by debuglevel == DEBUG_ASSERTION
    // this logic appears to be wrong!! I would say
    // if (DEBUG_LEVEL == DEBUG_ASSERTION && debuglevel == DEBUG_ASSERTION) ??!
    if (debuglevel == DEBUG_ASSERTION && debuglevel < DEBUG_LEVEL) {
      throw new AssertionException(toprint);
    }
  }

  /** Prints a message with a particular priority level, without a newline.
   * @param toprint The message to be printed.
   * @param debuglevel The priority level of the message. If the current priority
   * level is higher than the supplied level, printing of the message will be suppressed.
   */

  public static void print(String toprint, int debuglevel) {
    if (debuglevel <= DEBUG_LEVEL && debugmode)
      print(toprint);
  }

  /** Prints the supplied object to the debug stream followed by a newline.
   * @param toprint The object to be printed.
   */

  public static void println(Object toprint) {
    printInternal(toprint.toString() + '\n');
  }

  /** Prints the supplied object to the debug stream.
   * @param toprint The object to be printed.
   */

  public static void print(Object toprint) {
    printInternal(toprint.toString());
  }

  private static void printInternal(String toprint) {
    StringBuffer buildup = new StringBuffer(toprint.length() + 16);
    String prefix = null;
    synchronized (threadnames) {
      String threadname = Thread.currentThread().getName();
      int threadindex = ArrayUtil.indexOf(threadnames, threadname);
      boolean newthread = false;
      if (threadindex == -1) {
        threadnames = (String[]) ArrayUtil.append(threadnames, threadname);
        threadindex = threadnames.length - 1;
        newthread = true;
      }

      buildup.append('[').append(Integer.toString(threadindex)).append("] ");
      prefix = buildup.toString();
      if (newthread) {
        logger.println(
          prefix
            + " new thread "
            + Thread.currentThread()
            + " detected by Logger with name "
            + threadname);
      }
    }
    for (int i = 0; i < toprint.length(); ++i) {
      char charat = toprint.charAt(i);
      if (charat >= 32 && charat < 127 || charat == '\r' || charat == '\n') {
        buildup.append(charat);
      }
      else if (charat == '\t') {
        buildup.append("        ");
      }
      else
        buildup.append(ByteWrap.charToHex(charat));
      if (charat == '\n' && i != toprint.length() - 1) {
        buildup.append(prefix);
      }
    }
    logger.print(buildup.toString());
  }
  // Den Bortkomne, by Hans-Ulrich Treichel?
  /** Prints the supplied message to the debug stream, followed by a newline.
   * @param The message to be printed.
   */

  public static void println(String toprint) {
    printInternal(toprint + '\n');
  }

  /** Prints the supplied message to the debug stream.
   * @param The message to be printed.
   */

  public static void print(String toprint) {
    printInternal(toprint);
  }

  /** Prints a stack trace for the supplied exception to the debug stream, with the
   * default priority level of <code>DEBUG_SEVERE</code>.
   * @param t The throwable for which a stack trace is required.
   */

  public static void printStackTrace(Throwable t) {
    printStackTrace(t, DEBUG_SEVERE);
  }

  /** Prints a stack trace for the supplied exception to the debug stream, with the
   * specified priority level.
   * @param t The throwable for which a stack trace is required.
   * @param debuglevel The priority level for the stack trace.
   */

  public static void printStackTrace(Throwable t, int debuglevel) {
    if (debuglevel <= DEBUG_LEVEL && debugmode) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      printInternal(sw.toString());
    }
  }
}
