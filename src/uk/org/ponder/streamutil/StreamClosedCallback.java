package uk.org.ponder.streamutil;

import java.io.IOException;

/** This invaluable interface allows stream classes to make a specified callback
 * when they are closed. It is used by wrapper stream classes such as TrackerOutputStream,
 * TrackerInputStream, RandomAccessRead and RandomAccessWrite.
 */

public interface StreamClosedCallback {
  /** Report that a stream has been closed.
   * @param closedstream The stream that is reporting its closure.
   * @exception IOException if an I/O error occurs.
   */
  public void streamClosed(Object closedstream) throws IOException;
  }
