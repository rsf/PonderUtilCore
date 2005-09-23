/*
 * Created on Apr 25, 2004
 */
package uk.org.ponder.streamutil;

import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;

import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * Multicasts PrintOutputStream data to a collection of targets.
 * TODO: guard exceptions properly.
 * @author Bosmon
 */
public class POSMulticaster implements PrintOutputStream {
  private ArrayList targets = new ArrayList();

  public POSMulticaster(Object target) {
    addTarget(target);
  }
  private PrintOutputStream POSAt(int i) {
    return (PrintOutputStream) targets.get(i);
  }
  public void addTarget(Object target) {
    if (target instanceof Writer) {
      targets.add(new WriterPOS((Writer) target));
    }
    else if (target instanceof PrintStream) {
      targets.add(new PrintStreamPOS((PrintStream) target));
    }
    else if (target instanceof PrintOutputStream) {
      targets.add(target);
    }
    else if (target instanceof StringList) {
      targets.add(new StringListPOS((StringList) target)); 
    }
    else {
      throw new UniversalRuntimeException("Unrecognised PrintOutputStream target of " + target.getClass());
    }
  }
  public void addTarget(Writer target) {
    targets.add(target);
  }
  
  public void println(String toprint) {
    for (int i = 0; i < targets.size(); ++i) {
      POSAt(i).println(toprint);
    }
  }

  public void flush() {
    for (int i = 0; i < targets.size(); ++i) {
      POSAt(i).flush();
    }
  }

  public void close() {
    for (int i = 0; i < targets.size(); ++i) {
      POSAt(i).close();
    }
  }
  
  public PrintOutputStream print(String string) {
    for (int i = 0; i < targets.size(); ++i) {
      POSAt(i).print(string);
    }
    return this;
  }
  
  public void println() {
    println("");
  }
 
  public void println(Object obj) {
    println (""+obj);
  }
  public void write(char[] storage, int offset, int size) {
    for (int i = 0; i < targets.size(); ++i) {
      POSAt(i).write(storage, offset, size);
    }
  }
}
