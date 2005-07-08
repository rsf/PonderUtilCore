/*
 * Created on Jul 8, 2005
 */
package uk.org.ponder.timeutil;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * Java Calendar objects are baroque and relatively expensive. This provides
 * a Thread-local store of them, which is dipped into for various 
 * straightforward operations.
 */

public class CalendarStore {
  // A "default" CalendarStore for those happy with the default.
  // The behaviour of using this is identical to Calendar.instance()
  // (unless you are living in Thailand) but it is now easy to 
  // provide an application-wide different default with only a little
  // effort.
  public static CalendarStore instance = new CalendarStore();
  
  public Calendar get() {
    return (Calendar) store.get();
  }
  
  public int getHour(Date toget) {
    Calendar got = get();
    got.setTime(toget);
    return got.get(Calendar.HOUR_OF_DAY);
  }
  
  private CalendarFactory factory = DefaultGregorianFactory.instance;
  private ThreadLocal store = new ThreadLocal() {
    public Object initialValue() {
      return factory.getCalendar();
    }
  };
  
  public void setCalendarFactory(CalendarFactory factory) {
    this.factory = factory;
  }
}
