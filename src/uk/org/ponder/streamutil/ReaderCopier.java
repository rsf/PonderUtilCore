/*
 * Created on 23-Feb-2005
 */
package uk.org.ponder.streamutil;

import java.io.Reader;
import java.io.Writer;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface ReaderCopier {
  public void copyReader(Reader r, Writer w);
}
