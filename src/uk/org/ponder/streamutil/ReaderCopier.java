/*
 * Created on 23-Feb-2005
 */
package uk.org.ponder.streamutil;

import java.io.Reader;

import uk.org.ponder.streamutil.write.PrintOutputStream;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface ReaderCopier {
  public void copyReader(Reader r, PrintOutputStream pos);
}
