package uk.org.ponder.streamutil;

import java.io.InputStream;
import java.io.IOException;

import uk.org.ponder.util.Logger;

//import uk.org.ponder.byteutil.ByteWrap;

/** RandomAccessInputStream abstracts a preexisting specified portion of a RandomAccessFile
 * as an InputStream.
 */

public class RandomAccessInputStream extends InputStream {
  private RandomAccessRead internalfile;
  private long currentpos;
  private long finalpos;

  public RandomAccessInputStream(RandomAccessRead internalfile) throws IOException {
    this(internalfile, 0, -1L);
    }

  public RandomAccessInputStream(RandomAccessRead internalfile, long initialpos) 
    throws IOException {
    this(internalfile, initialpos, -1L);
    }

  public RandomAccessInputStream(RandomAccessRead internalfile, long initialpos, long length) 
    throws IOException {
    this.internalfile = internalfile;
    this.currentpos = initialpos;
    this.finalpos = initialpos + length;
    internalfile.seek(initialpos);
    }
  /*
  public RandomAccessFile getRandomAccessFile() {
    return internalfile;
    }
  */
  private StreamClosedCallback callback;
  
  public void setClosingCallback(StreamClosedCallback callback) {
    this.callback = callback;
    }

  public int read(byte[] array, int start, int length) throws IOException {
    if (currentpos >= finalpos) return -1;
    // if sufficiently near the end, truncate read request
    if (finalpos != -1L && currentpos + length > finalpos)
      length = (int)(finalpos - currentpos); // must be representable, since length is!
    
    int bytesread = internalfile.read(array, start, length);
    //    Logger.println("read: "+new ByteWrap(array, start, bytesread),
    //		   Logger.DEBUG_INFORMATIONAL);
    if (bytesread != -1) 
      currentpos += bytesread;
    return bytesread;
    }
  
  public int read() throws IOException {
    int togo = -1;
    if (finalpos != -1L && currentpos < finalpos) {
      togo = internalfile.read();
      ++ currentpos;
      }
    return togo;
    }

  /** Close this stream object. Note that this does NOT close the underlying
   * random access file.
   */
  public void close() throws IOException {
    if (callback != null) {
      Logger.println("RandomAccessInputStream closing callback",
		     Logger.DEBUG_INFORMATIONAL);
      callback.streamClosed(this);
      callback = null;
      }
    internalfile = null;
    currentpos = -1L;
    }
  }
