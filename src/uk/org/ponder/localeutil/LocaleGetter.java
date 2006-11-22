/*
 * Created on May 29, 2006
 */
package uk.org.ponder.localeutil;

import java.util.Locale;

/** The primitive proxy for a Locale-valued dependency. Necessary since Locale
 * is a final concrete class and cannot be proxied.
 * 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public interface LocaleGetter {
  public Locale get();
}
