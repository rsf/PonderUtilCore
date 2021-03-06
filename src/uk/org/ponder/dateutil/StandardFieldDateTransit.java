/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.dateutil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import uk.org.ponder.localeutil.LocaleHolder;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageException;

/**
 * A transit bean parsing Date objects into their Locale-specific forms.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */

public class StandardFieldDateTransit extends LocaleHolder implements FieldDateTransit {
  private Date date;

  private SimpleDateFormat shortformat;
  private SimpleDateFormat medformat;
  private SimpleDateFormat longformat;
  private SimpleDateFormat timeformat;
  private DateFormat longtimeformat;
  private DateFormat iso8601tz;
  private DateFormat iso8601notz;
  //private DateFormat breakformat;
  private TimeZone timezone = TimeZone.getDefault();
  
  private boolean isvalid = true;
  
  private String invalidDateKey = FieldDateTransit.INVALID_DATE_KEY;
  private String invalidTimeKey;

  public void setTimeZone(TimeZone timezone) {
    this.timezone = timezone;
  }
  
  public int getTZOffset() {
    return timezone.getOffset(date.getTime());
  }
  
  public void setInvalidDateKey(String invalidDateKey) {
    this.invalidDateKey = invalidDateKey;
  }
  
  public void setInvalidTimeKey(String invalidTimeKey) {
    this.invalidTimeKey = invalidTimeKey;
  }
  
  public void init() {
   
    Locale locale = getLocale();
    // TODO: Think about sharing these, see LocalSDF for construction costs
    shortformat = (SimpleDateFormat) DateFormat.getDateInstance(
        DateFormat.SHORT, locale);
    shortformat.setLenient(false);
    medformat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    longformat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, locale);
    timeformat = (SimpleDateFormat) DateFormat.getTimeInstance(
        DateFormat.SHORT, locale);
    timeformat.setTimeZone(timezone);
    longtimeformat = DateFormat.getTimeInstance(
        DateFormat.LONG, locale);
    longtimeformat.setTimeZone(timezone);
    iso8601tz = new SimpleDateFormat(LocalSDF.W3C_DATE_TZ);
    iso8601notz = new SimpleDateFormat(LocalSDF.W3C_DATE_NOTZ);
    iso8601notz.setTimeZone(timezone);
    //breakformat = new SimpleDateFormat(LocalSDF.BREAKER_DATE, locale);
    // do not use new Date(0) because of TZ insanity!!!
    date = LocalSDF.breakformat.parse("01012000000000");
  }

  public String getShort() {
    triggerParse();
    return isvalid? shortformat.format(date) : null;
  }

  public String getMedium() {
    triggerParse();
    return isvalid? medformat.format(date) : null;
  }

  public String getLong() {
    triggerParse();
    return isvalid? longformat.format(date) : null;
  }

  public String getTime() {
    triggerParse();
    return isvalid? timeformat.format(date) : null;
  }
  
  public String getLongTime() {
    triggerParse();
    return isvalid? longtimeformat.format(date) : null;
  }

  private List operations = new ArrayList();
  
  private void parse(SimpleDateFormat format, String datestring, int fieldsCode) {
    operations.add(new Operation(datestring, format, fieldsCode));
  }
  
  private void triggerParse() {
    for (int i = 0; i < operations.size(); ++ i) {
      parseImpl((Operation) operations.get(i));
    }
    operations.clear();
  }
  
  private void parseImpl(Operation op) {
    try {
      Date ydate = op.format.parse(op.text);
      DateUtil.applyFields(date, ydate, op.fields);
    }
    catch (Exception e) {
      isvalid = false;
      String key = op.fields == DateUtil.DATE_FIELDS? invalidDateKey :
        (invalidTimeKey == null? invalidDateKey : invalidTimeKey);
      throw new TargettedMessageException(
          new TargettedMessage(key, new Object[] {op.text, op.format.toPattern()}));
    }
  }
  
  private static class Operation {
    public String text;
    public SimpleDateFormat format;
    public int fields;
    public Operation(String text, SimpleDateFormat format, int fields) {
      this.text = text;
      this.format = format;
      this.fields = fields;
    }
  }
  
  
  public void setShort(String shortform) {
    parse(shortformat, shortform, DateUtil.DATE_FIELDS);
  }

  public void setMedium(String medform) {
    parse(medformat, medform, DateUtil.DATE_FIELDS);
  }

  public void setLong(String longform) {
    parse(longformat, longform, DateUtil.DATE_FIELDS);
  }

  public void setTime(String time) {
    parse(timeformat, time, DateUtil.TIME_FIELDS);
  }

  public Date getDate() {
    triggerParse();
    return isvalid? date : null;
  }

  public void setDate(Date date) {
    this.date = date;
  }
  /** Render an ISO8601-formatted value, including timezone information **/
  public String getISO8601TZ() {
    triggerParse();
    return isvalid? iso8601tz.format(date) : null;
  }
  /** Set an ISO 8601-formatted value for which the timezone is to be firmly
   * IGNORED.
   */
  public void setISO8601TZ(String isoform) throws ParseException {
    date = iso8601notz.parse(isoform);
  }
  
  public String getShortFormat() {
    return shortformat.toLocalizedPattern();
  }

  public String getTimeFormat() {
    return timeformat.toLocalizedPattern();
  }

}
