/*
 * Created on 13-Jan-2006
 */
package uk.org.ponder.dateutil;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;

import uk.org.ponder.beanutil.BeanResolver;
import uk.org.ponder.stringutil.StringList;

public class MonthBean {
  public static String[] indexarray = { "0", "1", "2", "3", "4", "5", "6", "7",
      "8", "9", "10", "11" };
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
        return formatsymbols.getMonths()[indexvalue];
      }
    };
  }
}
