package uk.org.ponder.streamutil;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;

/** This class wraps a <code>java.io.RandomAccessFile</code> object so that it
 * conforms to the <code>RandomAccessWrite</code> interface.
 */

public class RandomAccessWriteWrapper extends RandomAccessReadWrapper 
  implements RandomAccessWrite {

  /** Constructs a RandomAccessWriteWrapper wrapping the supplied RandomAccessFile.
   * @param internalraf The RandomAccessFile to be wrapped.
   */

  public RandomAccessWriteWrapper(RandomAccessFile internalraf) {
    super(internalraf);
    }

  /** Constructs a RandomAccessFile from the supplied File object, and then 
   * wraps it with a RandomAccessWriteWrapper.
   * @param file A file to be opened as a RandomAccessFile and wrapped.
   */

  public RandomAccessWriteWrapper(File file) throws IOException {
    internalraf = new RandomAccessFile(file, "rw");
    }

  /** Constructs a RandomAccessFile for the specified filename, and then
   * wraps it with a RandomAccessWriteWrapper.
   * @param filename A file to be opened as a RandomAccessFile and wrapped.
   */
  
  public RandomAccessWriteWrapper(String filename) throws IOException {
    internalraf = new RandomAccessFile(filename, "rw");
    }

  public void write(int b) throws IOException {
    internalraf.write(b);
    }
  
  public void write(byte b[],
		    int off,
		    int len) throws IOException {
    internalraf.write(b, off, len);
    }

  }

