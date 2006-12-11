/*
 * Created on Nov 17, 2005
 */
package uk.org.ponder.messageutil;

import uk.org.ponder.util.UniversalRuntimeException;

public class BlankMessageLocator extends MessageLocator {
  public String getMessage(String[] code, Object[] args) {
    throw new UniversalRuntimeException("Message locator not configured - " +
            "unable to resolve message with key " +code[0]);
  }
}
