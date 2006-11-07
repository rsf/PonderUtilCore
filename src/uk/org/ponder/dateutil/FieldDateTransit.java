/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.dateutil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.org.ponder.localeutil.LocaleReceiver;
import uk.org.ponder.util.ExceptionRunnable;

public class FieldDateTransit extends LocaleReceiver {
  private Date date = new Date();
  
  private DateFormat shortformat;
  private DateFormat medformat;
  private DateFormat longformat;
  
  public void init() {
    Locale locale = getLocale();
    shortformat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale);
    medformat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, locale);
    longformat = SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale);
  }
  
  public String getShort() {
    return shortformat.format(date);
  }
  
  public String getMedium() {
    return medformat.format(date);
  }
  
  public String getLong() {
    return longformat.format(date);
  }
  
  public void setShort(final String shortform) {
    invoke(new ExceptionRunnable() {
      public void run() throws Exception {
        date = shortformat.parse(shortform);
      }});
  }

  private void invoke(ExceptionRunnable runnable) {
    // TODO Auto-generated method stub
    
  }
  
  
  
  
}
