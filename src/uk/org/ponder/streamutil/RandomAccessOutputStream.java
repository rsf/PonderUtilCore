package uk.org.ponder.streamutil;

import java.io.OutputStream;
import java.io.IOException;

public class RandomAccessOutputStream extends OutputStream {
  private RandomAccessWrite internalfile;

  public RandomAccessOutputStream(RandomAccessWrite internalfile) throws IOException {
    System.out.println("RandomAccessOutputStream created at offset "
		       +internalfile.getFilePointer());
    this.internalfile = internalfile;
    }

  private StreamClosedCallback callback;
  
  public void setClosingCallback(StreamClosedCallback callback) {
    this.callback = callback;
    }

  //  public RandomAccessFile getRandomAccessFile() {
  //  return internalfile;
  //  }

  public void write(byte[] array, int start, int length) throws IOException {
    System.out.println("Write performed of length "+length+" at offset "+
		       internalfile.getFilePointer());
    internalfile.write(array, start, length);
    }
  
  public void write(int towrite) throws IOException {
    internalfile.write(towrite);
    }

  /** Close this stream object. Note that this does NOT close the underlying
   * random access file.
   */
  public void close() throws IOException {
    System.out.println("RandomAccessOutputStream closed");
    //    new Throwable().printStackTrace();
    if (callback != null) {
      callback.streamClosed(this);
      callback = null;
      }
    internalfile = null;
    }
  }
