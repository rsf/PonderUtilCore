/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.dateutil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import uk.org.ponder.localeutil.LocaleReceiver;

/**
 * A transit bean parsing Date objects into their Locale-specific forms.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */

public class FieldDateTransit extends LocaleReceiver {
  private Date date;

  private SimpleDateFormat shortformat;
  private DateFormat medformat;
  private DateFormat longformat;
  private SimpleDateFormat timeformat;
  private DateFormat longtimeformat;
  //private DateFormat breakformat;
  private TimeZone timezone = TimeZone.getDefault();

  public void setTimeZone(TimeZone timezone) {
    this.timezone = timezone;
  }
  
  public void init() {
   
    Locale locale = getLocale();
    // TODO: Think about sharing these, see LocalSDF for construction costs
    shortformat = (SimpleDateFormat) DateFormat.getDateInstance(
        DateFormat.SHORT, locale);
    medformat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    longformat = DateFormat.getDateInstance(DateFormat.LONG, locale);
    timeformat = (SimpleDateFormat) DateFormat.getTimeInstance(
        DateFormat.SHORT, locale);
    timeformat.setTimeZone(timezone);
    longtimeformat = DateFormat.getTimeInstance(
        DateFormat.LONG, locale);
    longtimeformat.setTimeZone(timezone);
    //breakformat = new SimpleDateFormat(LocalSDF.BREAKER_DATE, locale);
    // do not use new Date(0) because of TZ insanity!!!
    date = LocalSDF.breakformat.parse("20000101000000");
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

  public String getTime() {
    return timeformat.format(date);
  }
  
  public String getLongTime() {
    return longtimeformat.format(date);
  }

  public void setShort(String shortform) throws ParseException {
    Date ydate = shortformat.parse(shortform);
    DateUtil.applyFields(date, ydate, DateUtil.DATE_FIELDS);
  }

  public void setMedium(String medform) throws ParseException {
    Date ydate = medformat.parse(medform);
    DateUtil.applyFields(date, ydate, DateUtil.DATE_FIELDS);
  }

  public void setLong(String longform) throws ParseException {
    Date ydate = longformat.parse(longform);
    DateUtil.applyFields(date, ydate, DateUtil.DATE_FIELDS);
  }

  public void setTime(String time) throws ParseException {
    Date mdate = timeformat.parse(time);
    DateUtil.applyFields(date, mdate, DateUtil.TIME_FIELDS);
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

  public String getTimeFormat() {
    return timeformat.toLocalizedPattern();
  }

}
