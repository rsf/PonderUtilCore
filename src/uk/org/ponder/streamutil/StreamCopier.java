/*
 * Created on Jan 18, 2005
 */
package uk.org.ponder.streamutil;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface StreamCopier {
  public void copyStream(InputStream in, OutputStream out);
}
