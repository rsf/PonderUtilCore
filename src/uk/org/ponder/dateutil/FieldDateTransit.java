/*
 * Created on Dec 4, 2006
 */
package uk.org.ponder.dateutil;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import uk.org.ponder.localeutil.LocaleSetter;

/* Interface to a transit bean parsing Date objects into their Locale-specific 
 * forms. */ 

public interface FieldDateTransit extends LocaleSetter {
  public void setTimeZone(TimeZone timezone);
  public String getShort();
  public String getMedium();
  public String getLong();
  public String getTime();
  public String getLongTime();
  public void setShort(String shortform) throws ParseException;
  public void setMedium(String medform) throws ParseException;
  public void setLong(String longform) throws ParseException;
  public void setTime(String time) throws ParseException;
  public Date getDate();
  public void setDate(Date date);
  public String getShortFormat();
  public String getTimeFormat();
}