package uk.org.ponder.streamutil;

import java.io.IOException;

/** This interface abstracts the properties of a read-only random access file.
 */

public interface RandomAccessRead {
  /** Returns the length of the file.
   * @return The length of the file as a <code>long</code>.
   * @exception IOException if an I/O error occurs.
   */
  public long length() throws IOException;
  /** Closes this RandomAccessFile.
   */
  public void close() throws IOException;
  /** Reads a byte of data from this file. This method blocks if no input is yet available. 
   * @return the next byte of data, or -1 if the end of the file is reached. 
   * @exception IOException if an I/O error occurs. 
   */
  public int read() throws IOException;
  /** Reads up to len bytes of data from this file into an array of bytes. 
   * This method blocks until at least one byte of input is available. 
   * @param b the buffer into which the data is read. 
   * @param off the start offset of the data. 
   * @param len the maximum number of bytes read. 
   * @return the total number of bytes read into the buffer, 
   * or -1 if there is no more data because the end of the file has been reached. 
   * @exception IOException if an I/O error occurs. 
   */
  public int read(byte b[],
		int off,
		int len) throws IOException;
  /** Sets the file-pointer offset, measured from the beginning of this file, 
   * at which the next read or write occurs. The offset may be set beyond the end of the file. 
   * Setting the offset beyond the end of the file does not change the file length.
   * The file length will change only by writing after the offset has been set beyond the end
   * of the file. 
   *
   * @param pos the offset position, measured in bytes from the beginning of the file, 
   * at which to set the file pointer. 
   * @exception IOException if an I/O error occurs. 
   */
  public void seek(long pos) throws IOException;
  /** Returns the current offset in this file. 
   * @return the offset from the beginning of the file, in bytes, 
   * at which the next read or write occurs. 
   * @exception IOException if an I/O error occurs. 
   */
  public long getFilePointer() throws IOException;
  /** Sets the callback used to report when this file is closed.
   * @param callback A callback used to report when this file is closed.
   */
  public void setStreamClosedCallback(StreamClosedCallback callback);
  }
