package uk.org.ponder.hashutil;

import java.io.InputStream;
import java.io.IOException;

import uk.org.ponder.streamutil.StreamCopyUtil;

public class StreamDigestor {
  /** Returns a byte array representing an SHA-1 digest of the supplied stream.
   * This method closes the supplied stream.
   * @param stream The stream to be digested
   * @return A byte array holding the required digest.
   * @exception IOException if an I/O error occurs.
   */
  public static byte[] digest(InputStream stream) throws IOException {
    SHA1 sha = new SHA1();
    byte[] buffer = new byte[StreamCopyUtil.PROCESS_BUFFER_SIZE];
    try {
      while (true) {
	int bytesread = stream.read(buffer);
	if (bytesread > 0) {
	  sha.update(buffer, 0, bytesread);
	  }
	if (bytesread == -1) break;
	}
      }
    finally {
      stream.close();
      }
    return sha.digest();
    }
  }
