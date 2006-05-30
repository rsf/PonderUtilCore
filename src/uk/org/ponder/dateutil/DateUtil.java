/*
 * Created on 27-May-2006
 */
package uk.org.ponder.dateutil;

import java.util.Date;

public class DateUtil {
  public static Date makeLater(Date olddate, Date newdate) {
    if (newdate == null) return olddate;
    if (olddate == null || olddate.before(newdate)) return newdate;
    return olddate;
  }
}
