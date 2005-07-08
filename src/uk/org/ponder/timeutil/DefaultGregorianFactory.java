/*
 * Created on Jul 8, 2005
 */
package uk.org.ponder.timeutil;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DefaultGregorianFactory implements CalendarFactory{
  public static DefaultGregorianFactory instance = new DefaultGregorianFactory();

  public Calendar getCalendar() {
	return new GregorianCalendar();
  }
}
