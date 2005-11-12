/*
 * Created on Jun 17, 2005
 */
package uk.org.ponder.streamutil.write;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.StringList;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class StringListPOS implements PrintOutputStream {
  public StringList stringlist = null;
  private CharWrap incompleteline = new CharWrap();
  
  public StringListPOS() {
    stringlist = new StringList();
  }
  public StringListPOS(StringList stringlist) {
    this.stringlist = stringlist;
  }
  
  public void println(String toprint) {
    if (incompleteline.size() > 0) {
      incompleteline.append(toprint);
      stringlist.add(incompleteline.toString());
      incompleteline.clear();
    }
    else {
      stringlist.add(toprint);
    }
  }

  public void flush() {
  }

  public void close() {
  }

  public PrintOutputStream print(String string) {
    incompleteline.append(string);
    return this;
  }

  public void println() {
    println("");
  }

  public void println(Object obj) {
    println(obj.toString());
  }
  public void write(char[] storage, int offset, int size) {
    incompleteline.append(storage, offset, size);
  }
}
