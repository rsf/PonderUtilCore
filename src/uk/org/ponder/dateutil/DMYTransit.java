/*
 * Created on Jan 20, 2006
 */
package uk.org.ponder.dateutil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import uk.org.ponder.errorutil.PropertyException;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A "validation bean" which will accept and collect separate String values for
 * day, month and year into a java.util.Date format.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class DMYTransit {
  public Calendar calendar = new GregorianCalendar();
  // UI-exposed fields of transit beans need to be read/write since they may
  // be re-fetched by fixup before rendering.
  public String month = "01";
  public String year;
  public String day = "01";

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
  }

  public Date getDate() {
    try {
      int yearint = Integer.parseInt(year);
      int monthint = Integer.parseInt(month);
      int dayint = Integer.parseInt(day);
      calendar.set(yearint, monthint, dayint);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, PropertyException.class,
          "Invalid date format");
    }
    return calendar.getTime();
  }

}
