/*
 * Created on Apr 25, 2004
 */
package uk.org.ponder.streamutil;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class POSMulticaster implements PrintOutputStream {
  ArrayList targets = new ArrayList();

  public void addTarget(PrintStream target) {
    targets.add(target);
  }
  public void println(String toprint) {
    if (incompleteln.length() > 0) {
      incompleteln.append(toprint);
      toprint = incompleteln.toString();
      incompleteln.setLength(0);
    }
    for (int i = 0; i < targets.size(); ++i) {
      Object target = targets.get(i);
      if (target instanceof PrintStream) {
        ((PrintStream) target).println(toprint);
      }
    }
  }

  public void flush() {
    for (int i = 0; i < targets.size(); ++i) {
      Object target = targets.get(i);
      if (target instanceof PrintStream) {
        ((PrintStream) target).flush();
      }
    }
  }

  public void close() {
    for (int i = 0; i < targets.size(); ++i) {
      Object target = targets.get(i);
      if (target instanceof PrintStream && target != System.out && target != System.err) {
        ((PrintStream) target).close();
      }
    }
    targets.clear();
  }
 
  StringBuffer incompleteln = new StringBuffer();
  public void print(String string) {
    incompleteln.append(string);
  }
  
  public void println() {
    println("");
  }
 
  public void println(Object obj) {
    println (""+obj);
  }
}
