/*
 * Created on 13-Jan-2006
 */
package uk.org.ponder.dateutil;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;

import uk.org.ponder.beanutil.BeanResolver;
import uk.org.ponder.stringutil.StringList;

/** A bean exposing an indexed range of month names, for a particular Locale **/

public class MonthBean {
  public static String[] indexarray = { "01", "02", "03", "04", "05", "06", "07", "08",
      "09", "10", "11", "12" };
  public static StringList indexes = new StringList(indexarray);
  private Locale locale = Locale.getDefault();
  private DateFormatSymbols formatsymbols;

  public void setLocale(String locale) {
    this.locale = new Locale(locale);
  }

  public void init() {
    formatsymbols = new DateFormatSymbols(locale);
  }

  public List getIndexes() {
    return indexes;
  }

  public BeanResolver getNames() {
    return new BeanResolver() {
      public String resolveBean(Object bean) {
        int indexvalue = Integer.parseInt((String) bean);
        return formatsymbols.getMonths()[indexvalue - 1];
      }
    };
  }
}
