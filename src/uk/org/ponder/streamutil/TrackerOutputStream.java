package uk.org.ponder.streamutil;

import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

import uk.org.ponder.util.Logger;

/** A TrackerOutputStream wraps an OutputStream and guarantees to call a specified 
 * callback immediately after the stream it wraps is closed.
 */

public class TrackerOutputStream extends FilterOutputStream {
  private StreamClosedCallback callback;
  /** Constructs a TrackerOutputStream wrapping the specified OutputStream,
   * to call the supplied callback on closure.
   * @param is The OutputStream to be wrapped.
   * @param callback The callback to be after the output stream is closed.
   */
  public TrackerOutputStream(OutputStream is, StreamClosedCallback callback) {
    super(is);
    this.callback = callback;
    }

  public void close() throws IOException {
    try {
      super.close();
      Logger.println("TrackerOutputStream closed", Logger.DEBUG_INFORMATIONAL);
      }
    finally {
      try {
	if (callback != null) callback.streamClosed(out);
	}
      finally {
	callback = null;
	}
      }
    }
  /** Returns the underlying output stream which has been wrapped.
   * @return the underlying output stream which has been wrapped.
   */

  public OutputStream getUnderlyingStream() {
    return out;
    }
  }
