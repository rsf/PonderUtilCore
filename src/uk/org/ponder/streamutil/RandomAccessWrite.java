package uk.org.ponder.streamutil;

import java.io.IOException;

/** This interface abstracts the properties of a writeable random access file.
 */

public interface RandomAccessWrite extends RandomAccessRead {
  /** Writes the specified byte to this file. 
   * @param b the byte to be written. 
   * @exception IOException if an I/O error occurs.
   */
  public void write(int b) throws IOException;
  /** Writes len bytes from the specified byte array starting at offset off to this file. 
   * @param b the data. 
   * @param off the start offset in the data. 
   * @param len - the number of bytes to write. 
   * @exception IOException if an I/O error occurs. 
   */
  public void write(byte b[],
		    int off,
		    int len) throws IOException;
  }
