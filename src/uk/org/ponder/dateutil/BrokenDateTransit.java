/*
 * Created on Jan 20, 2006
 */
package uk.org.ponder.dateutil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import uk.org.ponder.errorutil.PropertyException;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A "transit bean" which will accept and collect separate String values for
 * broken up date field values (day, month, year, etc.)
 * into a java.util.Date format.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class BrokenDateTransit {
  public static final String DMY_MODE = "DMY";
  public static final String FULL_MODE = "Full";
  public static final String HMS_MODE = "HMS";

  public Calendar calendar = new GregorianCalendar();
  // UI-exposed fields of transit beans need to be read/write since they may
  // be re-fetched by fixup before rendering.
  public String month = "01";
  public String year;
  public String day = "01";

  public String hour;
  public String minute = "0";
  public String second = "0";
  public String ampm; // "0" for am, "1" for pm

  private String mode = "DMY";

  public void setMode(String mode) {
    this.mode = mode;
  }
  
  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
  }

  private Date date = new Date();

  public BrokenDateTransit() {
    render();
  }

  public void setDate(Date date) {
    this.date = date;
    render();
  }

  public static int parseToZero(String value) {
    int togo = 0;
    try {
      togo = Integer.parseInt(value);
    }
    catch (Exception e) {}
    return togo;
  }
  
  public void parse() {
    try {
      if (mode.equals(DMY_MODE)) {
        int yearint = Integer.parseInt(year);
        int monthint = Integer.parseInt(month);
        int dayint = Integer.parseInt(day);
        calendar.set(yearint, monthint - 1, dayint);
      }
      else if (mode.equals(FULL_MODE)) {
        int yearint = Integer.parseInt(year);
        int monthint = Integer.parseInt(month);
        int dayint = Integer.parseInt(day);
        int hourint = Integer.parseInt(hour);
        int minuteint = parseToZero(minute);
        int secondint = parseToZero(second);
        if (ampm != null) {
          hourint += Integer.parseInt(ampm) == 1 ? 12
              : 0;
        }
        calendar.set(yearint, monthint - 1, dayint, hourint, minuteint, secondint);
      }
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, PropertyException.class,
          "Invalid date format");
    }
    date = calendar.getTime();
  }

  public void render() {
    if (mode.equals(DMY_MODE)) {
      SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
      String formatted = sdf.format(date);
      day = formatted.substring(0, 2);
      month = formatted.substring(2, 4);
      year = formatted.substring(4);
    }
    else if (mode.equals(FULL_MODE)) {
      SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
      String formatted = sdf.format(date);
      day = formatted.substring(0, 2);
      month = formatted.substring(2, 4);
      year = formatted.substring(4, 8);
      hour = formatted.substring(8, 10);
      minute = formatted.substring(10, 12);
      second = formatted.substring(12, 14);
    }
  }

  public Date getDate() {
    parse();
    return date;
  }

}
