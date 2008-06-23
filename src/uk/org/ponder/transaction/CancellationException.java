/*
 * Created on 22 Jun 2008
 */
package uk.org.ponder.transaction;

/** An exception class representing that the work unit in progress is to be
 * cancelled (that is, a transaction to be rolled back). The exception itself
 * carries no further semantics and is primarily intended for framework
 * signalling. 
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public class CancellationException extends RuntimeException {

}
