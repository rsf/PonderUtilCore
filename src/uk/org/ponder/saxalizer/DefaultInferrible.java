/*
 * Created on Oct 15, 2004
 */
package uk.org.ponder.saxalizer;

/**
 * Classes implementing this tag interface agree to have default
 * accessor information inferred EVEN IF they already implement one of the
 * SAXalizable &c interfaces. Any publically available fields not already
 * mapped through the static accessor interfaces will be added to the 
 * class' mapping info. 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface DefaultInferrible {

}
