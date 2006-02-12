/*
 * Created on Apr 1, 2005
 */
package uk.org.ponder.dateutil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A thread-local SimpleDateFormat wrapper. If the default constructor is
 * used, supplies a W3C standard date format with millisecond resolution.
 * This class is somewhat useful since construction of a SimpleDateFormat runs
 * at around 13µs (2Ghz A64), with ThreadLocal gets around 300 times faster at
 * 40ns or so. NB, GregorianCalendar runs at about 2µs.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class LocalSDF {
  public static LocalSDF w3cformat = new LocalSDF();
  
  // this represents w3c standard dates as defined in 
  // http://www.w3.org/TR/NOTE-datetime
  public static final String W3C_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  private String formatstring;
  private ThreadLocal formatter = new ThreadLocal() {
    public Object initialValue() {
      if (formatstring == null) {
        SimpleDateFormat format = new SimpleDateFormat(W3C_DATE);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        format.setLenient(false);
        return format;
      }
      return new SimpleDateFormat(formatstring);
    }
  };
  public LocalSDF() {
  }
  public LocalSDF(String formatstring) {
    this.formatstring = formatstring;
  }
  public SimpleDateFormat get() {
    return (SimpleDateFormat) formatter.get();
  }
  public String format(Date toformat) {
    return get().format(toformat);
  }
  public Date parse(String datestring) {
    try {
      return get().parse(datestring);
    }
    catch (ParseException e) {
      throw UniversalRuntimeException.accumulate(e, "Error parsing date " + datestring);
    }
  }
}
