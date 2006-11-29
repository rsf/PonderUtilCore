/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.dateutil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.org.ponder.localeutil.LocaleReceiver;

/**
 * A transit bean parsing Date objects into their Locale-specific forms.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */

public class FieldDateTransit extends LocaleReceiver {
  private Date date = new Date();

  private SimpleDateFormat shortformat;
  private DateFormat medformat;
  private DateFormat longformat;
  private SimpleDateFormat timeformat;

  public void init() {
    Locale locale = getLocale();
    shortformat = (SimpleDateFormat) DateFormat.getDateInstance(
        SimpleDateFormat.SHORT, locale);
    medformat = DateFormat.getDateInstance(SimpleDateFormat.MEDIUM, locale);
    longformat = DateFormat.getDateInstance(SimpleDateFormat.LONG, locale);
    timeformat = (SimpleDateFormat) DateFormat.getTimeInstance(
        SimpleDateFormat.SHORT, locale);
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

  public void setShort(String shortform) throws ParseException {
    Date ydate = shortformat.parse(shortform);
    applyDateFields(date, ydate);
  }

  public void setMedium(String medform) throws ParseException {
    Date ydate = medformat.parse(medform);
    applyDateFields(date, ydate);
  }

  public void setLong(String longform) throws ParseException {
    Date ydate = longformat.parse(longform);
    applyDateFields(date, ydate);
  }

  public void setTime(String time) throws ParseException {
    Date mdate = timeformat.parse(time);
    applyDateFields(date, mdate);
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

  /**
   * Takes the Date-defining fields from the source and applies them onto the
   * target, preserving its Time-defining fields.
   */
  public void applyDateFields(Date datetarget, Date datesource) {
    Calendar ds = Calendar.getInstance(getLocale());
    ds.setTime(datesource);
    Calendar dt = Calendar.getInstance(getLocale());
    dt.setTime(datetarget);
    transferField(ds, dt, Calendar.YEAR);
    transferField(ds, dt, Calendar.MONTH);
    transferField(ds, dt, Calendar.DAY_OF_MONTH);
    datetarget.setTime(dt.getTimeInMillis());
  }

  public void applyTimeFields(Date datetarget, Date datesource) {
    Calendar ds = Calendar.getInstance(getLocale());
    ds.setTime(datesource);
    Calendar dt = Calendar.getInstance(getLocale());
    dt.setTime(datetarget);
    transferField(ds, dt, Calendar.HOUR);
    transferField(ds, dt, Calendar.MINUTE);
    transferField(ds, dt, Calendar.SECOND);
    transferField(ds, dt, Calendar.MILLISECOND);
    datetarget.setTime(dt.getTimeInMillis());
  }

  public static void transferField(Calendar ds, Calendar dt, int field) {
    int fieldval = ds.get(field);
    dt.set(field, fieldval);
  }
}
