/*
 * Created on Jun 17, 2005
 */
package uk.org.ponder.streamutil;

import java.io.PrintStream;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class PrintStreamPOS implements PrintOutputStream {
  private PrintStream printstream;
  public PrintStreamPOS(PrintStream printstream) {
    this.printstream = printstream;
  }
  public void println(String toprint) {
    printstream.println(toprint);    
  }
  public void flush() {
    printstream.flush();
  }
  public void close() {
    if (printstream != System.out && printstream != System.err) {
      printstream.close();
    }
  }
  public void print(String string) {
    printstream.print(string);
  }
  public void println() {
    printstream.println();
  }
  public void println(Object obj) {
    printstream.println(obj);
  }
}
