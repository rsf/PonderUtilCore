/*
 * Created on 29 Feb 2008
 */
package uk.org.ponder.streamutil.read;

import java.io.Reader;

/** Adapts a ReadInputStream to a Reader **/

public class RISReader extends Reader {

  private ReadInputStream ris;

  public RISReader(ReadInputStream ris) {
    this.ris = ris;
  }
  public void close() {
    ris.close();
  }

  public int read(char[] cbuf, int off, int len) {
    return ris.read(cbuf, off, len);
  }

}
