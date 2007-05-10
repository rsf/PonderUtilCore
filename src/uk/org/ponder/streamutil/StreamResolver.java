/*
 * Created on 31-Dec-2005
 */
package uk.org.ponder.streamutil;

import java.io.InputStream;

/**
 * Maps a String path onto an InputStream, in a context-dependent manner. Sample implementation
 * is {@link SpringStreamResolver}.
 * @author Antranig Basman (amb26@ponder.org.uk)
 */
public interface StreamResolver {
  public InputStream openStream(String path);
}
