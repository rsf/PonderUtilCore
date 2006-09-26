/*
 * Created on 27-May-2006
 */
package uk.org.ponder.dateutil;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    int maxDay = c.getMaximum(GregorianCalendar.DAY_OF_MONTH);
    return twoDigitList(1, maxDay);
  }
  
}
