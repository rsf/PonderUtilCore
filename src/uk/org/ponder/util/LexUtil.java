/*
 * Created on Feb 11, 2004
 */
package uk.org.ponder.util;

import java.io.IOException;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class LexUtil {
  public static void expect(LexReader lr, String toexpect) throws IOException {
    for (int i = 0; i < toexpect.length(); ++i) {
      char c = lr.get();
      if (c != toexpect.charAt(i))
        throw new UniversalRuntimeException("Expected text " + toexpect + " not found");
    }
  }
  public static void skipWhite(LexReader lr) throws IOException {
    while (true) {
      char c = lr.get();
      if (!Character.isWhitespace(c)) {
        lr.unread(c);
        break;
      }
    }
  }
  public static int readInt(LexReader pbr) throws IOException {
    StringBuffer sb = new StringBuffer();
    char c = pbr.get();
    while (Character.isDigit(c)) {
      sb.append(c);
      c = pbr.get();
    }
    pbr.unread(c);
    int togo = 0;
    try {
      togo = Integer.parseInt(sb.toString());
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Error reading integer: ");
    } 
    return togo;
  }

  public static double readDouble(LexReader lr) throws IOException {
    StringBuffer sb = new StringBuffer();
    char c = lr.get();
    while (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
      sb.append(c);
      c = lr.get();
    }
    lr.unread(c);
    return Double.parseDouble(sb.toString());
  }
  /**
   * @param lr
   */
  public static void expectEmpty(LexReader lr) throws IOException {
    skipWhite(lr);
    if (!lr.EOF()) {
      StringBuffer unexpected = new StringBuffer();
      while (!lr.EOF()) {
        char c = lr.get();
        if (unexpected.length() < 32) {
          unexpected.append(c);
        }
      }
      throw new UniversalRuntimeException("Unexpected trailing data " + unexpected);
    }
  }
}
