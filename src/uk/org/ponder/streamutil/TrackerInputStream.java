package uk.org.ponder.streamutil;

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;

/** A TrackerInputStream wraps an InputStream and guarantees to call a specified 
 * callback immediately after the stream it wraps is closed.
 */

public class TrackerInputStream extends FilterInputStream {
  private StreamClosedCallback callback;

  /** Constructs a TrackerInputStream wrapping the specified InputStream,
   * to call the supplied callback on closure.
   * @param is The InputStream to be wrapped.
   * @param callback The callback to be after the input stream is closed.
   */

  public TrackerInputStream(InputStream is, StreamClosedCallback callback) {
    super(is);
    this.callback = callback;
    }

  public void close() throws IOException {
    try {
      super.close();
      }
    finally {
      try {
	if (callback != null) callback.streamClosed(in);
	}
      finally {
	callback = null;
	}
      }
    }
  
  /** Returns the underlying input stream which has been wrapped.
   * @return the underlying input stream which has been wrapped.
   */

  public InputStream getUnderlyingStream() {
    return in;
    }
  }
