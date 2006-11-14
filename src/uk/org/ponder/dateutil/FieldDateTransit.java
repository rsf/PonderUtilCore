/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.dateutil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.org.ponder.localeutil.LocaleReceiver;

/** A transit bean parsing Date objects into their Locale-specific 
 * forms.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */

public class FieldDateTransit extends LocaleReceiver {
  private Date date = new Date();
  
  private SimpleDateFormat shortformat;
  private DateFormat medformat;
  private DateFormat longformat;
  
  public void init() {
    Locale locale = getLocale();
    shortformat = (SimpleDateFormat) DateFormat.getDateInstance(SimpleDateFormat.SHORT, locale);
    medformat = DateFormat.getDateInstance(SimpleDateFormat.MEDIUM, locale);
    longformat = DateFormat.getDateInstance(SimpleDateFormat.LONG, locale);
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
  
  public void setShort(String shortform) throws ParseException {
    date = shortformat.parse(shortform);
  }

  public void setMedium(String medform) throws ParseException {
    date = medformat.parse(medform);
  }
  
  public void setLong(String longform) throws ParseException {
    date = longformat.parse(longform);
  }
  
  public Date getDate() {
    return date;
  }
  
  public void setDate(Date date) {
    this.date = date;
  }
  
  public String getShortFormat() {
    return shortformat.toLocalizedPattern();
  }
}
