//   --   @(#)LUDecomp.java 1.4 99/03/22

/**
 * A special form of Matrix that holds a copy of the row interchange
 * information generated during a LU decompose operation. It also holds
 * the value of a flg that indicates the odd/even number of row interchanges.
 */

package uk.org.ponder.matrix;

public class LUDecomp extends Matrix {

	public final static double TINY=Double.NaN;

	int	indx[];		// Table of row interchanges.
	double	d;		// +1.0, -1.0 (row interchanges even, odd)
	double	twod[][];	// 2D version of the matrix

	/**
	 * LU Decomposition of a Matrix a. Specalised case of a Matrix
	 * operator. This constructor performs the LU decomposition of
	 * the supplied matrix and creates an instance of its decomposition.
	 */

	public LUDecomp(Matrix m) throws SingularException, NotSquareException {

		// Make a new matrix of the correct size but initalised
		// with zeros. Call deepCopy to duplicate the values 
		// from the supplied matrix. These get overwritten by
		// the decomposition routine.
		super(m.rows, m.cols);

		if(m.rows != m.cols) {
			throw new NotSquareException();
		}
		twod = m.asArray();
		indx = new int[m.rows];

		LUdecomp();

		twod=null;	// Dont need it any more.
	}

	/**
	 * A relatively faithful port of the ludcmp routine from numerical
	 * recipies in 'C'. (See chapter 2). Principal changes are to make
	 * the arrays run over the range 0..n-1 rather than the botched
	 * version in the book. Changes have also been made to allow for
	 * the encapsulation of the mechanisim within an object framework.
	 */
	private void LUdecomp() throws SingularException {

		// As in the book this routine overwrites the copy of the
		// data array with the LU decomposition of the original
		// data. The main difference is that this is a local copy
		// of the data that was supplied with the constructor.

		int		i, imax=-1, j, k;
		int		n=rows; 			// Matrix dimension
		double		big, dum, sum, temp;
		double[]	vv = new double[rows]; 
		double[][]	a = twod;

		d = 1.0;	// No row interchanges yet.

		for(i=0; i < n; i++) { 	// Loop over rows to get the implicit
			big = 0.0;	// scaling information.

			for(j=0; j<n; j++) {
				if((temp=Math.abs(a[i][j])) > big)
					big = temp;
			}

			if(big == 0.0) {
				throw new SingularException();
			}

			// No nonzero largest element.

			// Save the scaling.
			vv[i] = 1.0/big;
	
		}

		for(j=0; j<n; j++) {		// Loop over col. in Crout's
			for(i=0; i<j; i++) {	// method.
				sum = a[i][j];	// eqn. 2.3.12 except for i=j
				for(k=0; k<i; k++) {
					sum -= a[i][k]*a[k][j];
				}
				a[i][j]=sum;
			}
			big=0.0;		// Init search for largest 
			for(i=j; i<n; i++) {	// pivot element.
				sum=a[i][j];
				for(k=0; k<j; k++){
					sum -= a[i][k]*a[k][j];
				}
				a[i][j]=sum;
				if((dum=vv[i]*Math.abs(sum)) >= big) {
					// Is the figure of merit for this
					// pivot better than that found so far?
					big = dum;
					imax = i;
				}
			}
			if(j != imax) {	// Do we need to interchange rows?
				for(k=0; k<n; k++) {
					dum = a[imax][k];
					a[imax][k] = a[j][k];
					a[j][k] = dum;
				}
				d = -d;	// Change the parity of d.
				vv[imax] = vv[j]; // Also interchange the scale.
			}

			indx[j] = imax;
			
			if(a[j][j] == 0.0) {
				// If the pivot element is zero the matrix is
				// singular (at least to the precision of the
				// algorithim) For some applications on 
				// singular matricies, it is desirable to
				// substitute TINY for zero.
				a[j][j] = TINY;
			}

			if(j != n) {

				// Now, finally, divide by the pivot element.
				dum = 1.0/(a[j][j]);
				for(i=j+1; i<n; i++) {
					a[i][j] *= dum;
				}
			}
		}

		for(i=0; i < rows; i++) {
			for(j=0; j < cols; j++) {
				setMval(i, j, a[i][j]);
			}
		}

	}

	/**
	 * Perform back substitution based on this LU decomposed matrix. This
	 * effectively solves a set of N linear equations A.X = B.
	 * @param data The 1 x N data vector. (RHS vector B)
	 * @returns solution vector X.
	 */
	public double[] backSubstitute(double[] data) throws SizeMismatchException {

		int		i, ii=-1, ip, j;
		int		n = rows;
		double		sum;

		if(n != data.length) {
			throw new SizeMismatchException();
		}

		double[]	b = new double[n];

		for(i=0; i<n; i++) {
			b[i] = data[i];
		}

		for(i=0; i<n; i++) {	// When ii is set to a positive value
			ip = indx[i];	// it will become the index of the
			sum = b[ip];	// first nonvanishing element of b.
			b[ip] = b[i];	// We now do the forward substitution
					// (eqn. 2.3.6) The only new wrinkle
			if(ii != -1) {	// is to unscramble the permutation
					// as we go.
				for(j=ii; j<=i-1; j++)
					sum -= getMval(i, j)*b[j];
			} else if(sum != 0.0) {
					// A nonzero element was encountered
					// so from now on we have to do the
				ii = i; // sums in the loop above.
			}
			b[i] = sum;
		}

		for(i=n-1; i>=0; i--) {	// Now we do the back substitution,
			sum = b[i];	// (eqn. 2.3.7)
			for(j=i+1; j<n; j++)
				sum -= getMval(i, j)*b[j];
			b[i] = sum/getMval(i, i);// Store a component of the
						// solution vector X.
		}

		return b;
	}

	/**
	 * Return the inverse of the original matrix used in the construction
	 * of this LUDecomp object. Simply involves back substitution of the
	 * identity matrix.
	 * @returns The inverse matrix of that used in the creation of this
	 * LUDecomp object.
	 */
	public Matrix luinvert() throws SizeMismatchException {

		int		i, j, n=rows;
		Matrix		ret = new Matrix(rows, cols); // nb. class col.
		double[]	tmp, col = new double[n];

		for(j=0; j<n; j++) {
			for(i=0; i<n; i++) 
				col[i] = 0.0;
			col[j] = 1.0;
			tmp = backSubstitute(col);

			for(i=0; i<n; i++)
				ret.setMval(i, j, tmp[i]);
		}
		return ret;
	}

	public double ludeterminant() {

		int		n = rows;
		double		res = d;	// d is a class variable

		for(int i=0; i<n; i++)
			res *= getMval(i, i);

		return res;
	}

	/**
	 * Render the contents of this LU decomposition into a string
	 * primarily for debugging use. 
	 */
	public String toString() {

		String	tmp=super.toString();

		tmp = tmp + "\n" + "[";
		for(int i=0; i<indx.length; i++) {
			if(i != 0)
				tmp  = tmp + ", " + indx[i];
			else
				tmp  = tmp + indx[i];
		}
		tmp = tmp + "] (" + d +")\n";

		return(tmp);
	}
}
