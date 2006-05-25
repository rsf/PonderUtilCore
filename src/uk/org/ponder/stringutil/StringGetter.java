/*
 * Created on May 17, 2006
 */
package uk.org.ponder.stringutil;

/** A holder for a single String value, designed for use in proxying situations
 * where the finality of String is a problem.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */
public interface StringGetter {
  public String get();
}
