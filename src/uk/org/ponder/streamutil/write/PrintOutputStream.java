/*
 * Created on Apr 25, 2004
 */
package uk.org.ponder.streamutil.write;


/**
 * PrintOutputStream has a number of advantages over the Java (non-)interface
 * "Writer". <br>
 * Firstly, it actually is an interface. <br> 
 * Secondly, it incurs no peculiar locking overheads in its operation.<br>
 * Thirdly, its methods throw no checked exceptions. <br>
 * There are a number of wrappers for various kinds of targets, for example 
 * standard Writers, PrintStreams, StringList and standard String. There is
 * also a POSMulticaster for forking output to multiple streams. Note that
 * at the pre-1.5 level, StringWriter not includes the sync overhead of the
 * Writer, but also the StringBuffer it constructs.
 * @author Bosmon
 */
public interface PrintOutputStream {
  public void println(String toprint);
  public void flush();
  public void close();
  public PrintOutputStream print(String string);
  public void println();
  public void println(Object obj);
  public void write(char[] storage, int offset, int size);
}
