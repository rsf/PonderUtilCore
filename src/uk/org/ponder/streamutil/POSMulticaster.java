/*
 * Created on Apr 25, 2004
 */
package uk.org.ponder.streamutil;

import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;

import uk.org.ponder.stringutil.CharWrap;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class POSMulticaster implements PrintOutputStream {
  ArrayList targets = new ArrayList();

  public POSMulticaster(PrintStream target) {
    addTarget(target);
  }
  
  public POSMulticaster(Writer target) {
    addTarget(target);
  }
  
  public void addTarget(PrintStream target) {
    targets.add(target);
  }
  public void addTarget(Writer target) {
    targets.add(target);
  }
  
  public void println(String toprint) {
    if (incompleteln.size() > 0) {
      incompleteln.append(toprint);
      toprint = incompleteln.toString();
      incompleteln.clear();
    }
    for (int i = 0; i < targets.size(); ++i) {
      Object target = targets.get(i);
      if (target instanceof PrintStream) {
        ((PrintStream) target).println(toprint);
      }
      else if (target instanceof Writer) {
        try {
          ((Writer) target).write(toprint);
        }
        catch (Exception e) {}
      }
    }
  }

  public void flush() {
    for (int i = 0; i < targets.size(); ++i) {
      Object target = targets.get(i);
      if (target instanceof PrintStream) {
        ((PrintStream) target).flush();
      }
      else if (target instanceof Writer) {
        try {
          ((Writer)target).flush();
        }
        catch (Exception e) {}
      }
    }
  }

  public void close() {
    for (int i = 0; i < targets.size(); ++i) {
      Object target = targets.get(i);
      if (target instanceof PrintStream && target != System.out && target != System.err) {
        ((PrintStream) target).close();
      }
      else if (target instanceof Writer) {
        StreamCloseUtil.closeWriter((Writer) target);
      }
    }
    targets.clear();
  }
 
  CharWrap incompleteln = new CharWrap();
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
