/*
 * Created on Apr 25, 2004
 */
package uk.org.ponder.streamutil;


/**
 * The class 
 * 
 * @author Bosmon
 */
public interface PrintOutputStream {
  public void println(String toprint);
  public void flush();
  public void close();
  public void print(String string);
  public void println();
  public void println(Object obj);
 
}
