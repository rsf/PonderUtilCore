/*
 * Created on 26 Oct 2006
 */
package uk.org.ponder.localeutil;

import java.util.Locale;

/** A utility base class that allows some form of I18N converter to accept
 * a Locale in any number of ways.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public class LocaleHolder implements LocaleSetter {
  private Locale locale = Locale.getDefault();
  private LocaleGetter localegetter;

  public void setLocaleName(String localename) {
    this.locale = LocaleUtil.parseLocale(localename);
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }
  
  public void setLocaleGetter(LocaleGetter localegetter) {
    this.localegetter = localegetter;
  }

  public Locale getLocale() {
    return localegetter == null? locale : localegetter.get();
  }
  
}
