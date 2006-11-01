/*
 * Created on 1 Nov 2006
 */
package uk.org.ponder.localeutil;

import java.util.Locale;

/** Utilities for dealing with Locales.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public class LocaleUtil {
  public static Locale parseLocale(String localestring) {
    String[] locValues = localestring.trim().split("_");
    if (locValues.length == 1) {
      return new Locale(locValues[0]);
    }
    else if (locValues.length == 2) {
      return new Locale(locValues[0], locValues[1]);
    }
    else {
      return new Locale(locValues[0], locValues[1], locValues[2]);
    }
  }
}
