package uk.org.ponder.streamutil;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;

/** This class wraps a <code>java.io.RandomAccessFile</code> object so that it
 * conforms to the <code>RandomAccessRead</code> interface.
 */

public class RandomAccessReadWrapper implements RandomAccessRead {
  private StreamClosedCallback callback;
  protected RandomAccessFile internalraf;

  protected RandomAccessReadWrapper() {}
  /** Constructs a RandomAccessReadWrapper wrapping the supplied RandomAccessFile.
   * @param internalraf The RandomAccessFile to be wrapped.
   */
  public RandomAccessReadWrapper(RandomAccessFile internalraf) {
    this.internalraf = internalraf;
    }

  /** Constructs a RandomAccessFile from the supplied File object, and then 
   * wraps it with a RandomAccessReadWrapper.
   * @param file A file to be opened as a RandomAccessFile and wrapped.
   */
  public RandomAccessReadWrapper(File file) throws IOException {
    internalraf = new RandomAccessFile(file, "r");
    }
  
  /** Constructs a RandomAccessFile for the specified filename, and then
   * wraps it with a RandomAccessReadWrapper.
   * @param filename A file to be opened as a RandomAccessFile and wrapped.
   */
  public RandomAccessReadWrapper(String filename) throws IOException {
    internalraf = new RandomAccessFile(filename, "r");
    }

  public long length() throws IOException {
    return internalraf.length();
    }

  public void close() throws IOException {
    try {
      internalraf.close();
      }
    finally {
      if (callback != null) callback.streamClosed(internalraf);
      }
    }

  public int read() throws IOException{
    return internalraf.read();
    }

  public int read(byte b[],
		  int off,
		  int len) throws IOException {
    return internalraf.read(b, off, len);
    }

  public void seek(long pos) throws IOException {
    internalraf.seek(pos);
    }

  public long getFilePointer() throws IOException {
    return internalraf.getFilePointer();
    }

  public void setStreamClosedCallback(StreamClosedCallback callback) {
    this.callback = callback;
    }

  }
