package uk.org.ponder.streamutil;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/** This utility class slightly streamlines the process of creating a matched
 * pair of PipedInputStream and PipedOutputStream. Little used after conversion
 * to the BytePen model.
 */

public class StreamPair {
  public PipedInputStream inputstream;
  public PipedOutputStream outputstream;
  /** Creates a connected pair of PipedOutputStream and PipedInputStream.
   * @exception IOException If an I/O error occurs.
   */
  public StreamPair() throws IOException {
    outputstream = new PipedOutputStream();
    inputstream = new PipedInputStream(outputstream);
    //    outputstream.connect(inputstream);
    }
  }
