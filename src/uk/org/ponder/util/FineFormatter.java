/*
 * Created on Mar 22, 2005
 */
package uk.org.ponder.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class FineFormatter extends Formatter {

  public static void configureLogger(Logger logger) {
    Handler[] existing = logger.getHandlers();
    if (existing.length > 0) {
      System.out.println("**** removing " + existing.length + " existing handlesr");
    }
    for (int i = 0; i < existing.length; ++ i) {
      logger.removeHandler(existing[i]);
    }
    logger.setUseParentHandlers(false);
    FineFormatter finef = new FineFormatter();
    ConsoleHandler console = new ConsoleHandler();
    console.setFormatter(finef);
    logger.addHandler(console);
    FileHandler file;
    try {
      file = new FileHandler();
      file.setFormatter(finef);
      //logger.addHandler(file);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  Date dat = new Date();
  private final static String format = "{0,date,yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}";
  private MessageFormat formatter;

  private Object args[] = new Object[1];

  // Line separator string. This is the value of the line.separator
  // property at the moment that the SimpleFormatter was created.
  private String lineSeparator = (String) java.security.AccessController
      .doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

  /**
   * Format the given LogRecord.
   * 
   * @param record
   *          the log record to be formatted.
   * @return a formatted log record
   */
  public synchronized String format(LogRecord record) {
    StringBuffer sb = new StringBuffer();
    // Minimize memory allocations here.
    dat.setTime(record.getMillis());
    args[0] = dat;
    StringBuffer text = new StringBuffer();
    if (formatter == null) {
      formatter = new MessageFormat(format);
    }
    formatter.format(args, text, null);
    sb.append(text);
    sb.append(" ");
    if (record.getSourceClassName() != null) {
      sb.append(record.getSourceClassName());
    }
    else {
      sb.append(record.getLoggerName());
    }
    if (record.getSourceMethodName() != null) {
      sb.append(" ");
      sb.append(record.getSourceMethodName());
    }
    sb.append(lineSeparator);
    String message = formatMessage(record);
    sb.append(record.getLevel().getLocalizedName());
    sb.append(": ");
    sb.append(message);
    sb.append(lineSeparator);
    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        record.getThrown().printStackTrace(pw);
        pw.close();
        sb.append(sw.toString());
      }
      catch (Exception ex) {
      }
    }
    return sb.toString();
  }
}