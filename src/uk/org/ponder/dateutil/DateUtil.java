/*
 * Created on 27-May-2006
 */
package uk.org.ponder.dateutil;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import uk.org.ponder.util.UniversalRuntimeException;

public class DateUtil {
  public static Date makeLater(Date olddate, Date newdate) {
    if (newdate == null) return olddate;
    if (olddate == null || olddate.before(newdate)) return newdate;
    return olddate;
  }

  public static String[] twoDigitList(int first, int last, int step) {
    int count = (last - first) / step + 1;
    String[] togo = new String[count];
    DecimalFormat format = new DecimalFormat("00");
    int val = first;
    for (int i = 0; i < count; ++ i) {
      togo[i] = format.format(val);
      val += step;
    }
    return togo;
  }
  
  public static String[] twoDigitList(int first, int last) {
    return twoDigitList(first, last, 1);
  }
  
  /** Returns a "maximum length" list of days, currently simply 01..31 **/
  public static String[] dayList() {
    Calendar c = new GregorianCalendar();
    int maxDay = c.getMaximum(Calendar.DAY_OF_MONTH);
    return twoDigitList(1, maxDay);
  }
  
  public static Date parse(DateFormat parser, String datestring) {
    try {
      return parser.parse(datestring);
    }
    catch (ParseException e) {
      throw UniversalRuntimeException.accumulate(e, "Error parsing date " + datestring);
    }
  }

  /**
   * Takes the Date-defining fields from the source and applies them onto the
   * target, preserving its Time-defining fields, or the reverse
   */
  public static void applyFields(Date datetarget, Date datesource, int fields) {
    // This *should* be a TZ-independent operation.
    DateFormat breaker = LocalSDF.breakformat.get();
    String breaktarget = breaker.format(datetarget);
    String breaksource = breaker.format(datesource);
    String fused = fields == DateUtil.DATE_FIELDS ?
        breaksource.substring(0,8) + breaktarget.substring(8)
        : breaktarget.substring(0,8) + breaksource.substring(8);
    Date newtarget = parse(breaker, fused);
    datetarget.setTime(newtarget.getTime());
  }

  public static final int TIME_FIELDS = 1;
  public static final int DATE_FIELDS = 2;
}
