//   --   @(#)JacobiItterationsExhaustedException.java 1.3 99/03/22

package uk.org.ponder.matrix;

/**
 * This exception is raised by any routines relying upon a successful
 * application of the Jacobi algorithim for determination of eigen values
 * and vectors. It should never happen. Optionaly the detail string can
 * be set to allow more informative error messages to be generated.
 */

public class JacobiIterationsExhaustedException extends RuntimeException {

	public JacobiIterationsExhaustedException(String s){
		super(s);
	}

}
