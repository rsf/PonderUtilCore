/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.hashutil;

import java.security.SecureRandom;

import uk.org.ponder.stringutil.ByteToCharBase64;
import uk.org.ponder.stringutil.CharWrap;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class EighteenIDGenerator  {
  SecureRandom random = new SecureRandom();

  public String generateID() {
    byte[] eighteen = new byte[18];
    random.nextBytes(eighteen);
    CharWrap togo = new CharWrap();
    ByteToCharBase64.writeBytes(togo, eighteen, 0, 18, false);
    return togo.toString();
  }

}
