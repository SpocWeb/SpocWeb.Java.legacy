/*
 * File Name: MatrixBand.java
 * Created on: 02.11.2003
 *
 */
package math.matrix;

import java.io.IOException;

import math.vector.VectorDouble;
import streamIO.Assert;

/**
 * Represents a matrix in band-diagonal form and provides LU decomposition with partial
 * pivoting to map and solve vectors against it.
 *
 * <p>Side elements are not stored; treat these preferably as periodic border conditions.
 * Also holds static methods for other cases of O(n^2) solutions: Toeplitz and Vandermonde
 * matrices.
 * @see math.MatrixTriDiagonal
 * Can be used for unstable tridiagonal Matrices too,
 * because it implements basic Pivoting!
 *
 * Design Decisions:
 * inherits from AMatrix, 
 * although it needs only those Elements defined there, 
 * not the ones it implicitly inherits from AVector! 
 * 
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:46:12Z
 * digest: 7fc761933a5dfa831e2b2102188db8bd3db0203835acb96204f72de2b5ad0573
 * stale: false
 * tags: [code/band_diagonal_matrix, code/matrix_algebra]
 * concepts: [Band-Diagonal Matrix]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class MatrixBand 
extends AMatrix {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** LU Decomposition, initialized only on Demand, also used as a Flag	 */
	private double[][] decomposition;
	
	/**Overwritten, becaue the rows[] Array is already initialized 
	 * to hold the Size of the Diagonal.  
	 * @return true when this Matrix Contains the LU Decomposition.	 */
	public boolean isDecomposedLU() { return decomposition != null; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Super- and Sub- Diagonals	 */	
	private final double[][] diags;
	
	/** Number of Sub-Diagonals */
	private final int numSubDiags; 
	
	/** Returns the diagonal-storage row at the given position, or {@code null} when out of range.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) {
		if (!indexInRange(i)) 
			return null; 
		return diags[i];
	}
	
	/**Sets (adds or replaces) the component at the specified index.
	 * All other components in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @param	  Item	the component to set (add or replace).
	 * @param	  index   the index of the object to remove.
	 * @return	 the component replaced by 'Item'.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public Object setAt(final int index, final Object value) {
		throw new RuntimeException("Not implemented!"); 
	}
	
	/**Increases the capacity of this VectorInt, if necessary, 
	 * to ensure that it can hold at least the number of components 
	 * specified by the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final public synchronized int setCapacity(final int minCapacity) {
		return minCapacity; 
		/*
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) 
			return oldCapacity;
		final int newCapacity = ENLARGED_CAPACITY(oldCapacity, capacityIncrement, minCapacity); 
		final int[] oldData = items; items = new int[newCapacity];
		if (itemCount > 0) 
			System.arraycopy(oldData, 0, items, 0, itemCount);
		return newCapacity;
		*/
	}

	/** Creates a band matrix over the given compact diagonal storage and sub-diagonal count.
	 *
	 * @param diags_ Diagonals stored as:
	 *   Sub-Diagonal: diags[1..m1][j..n]
	 *       Diagonal: diags[m1+1 ][1..n]
	 * Super-Diagonal: diags[m1+2..m1+m2+1][1..j]
	 *
	 * @param nSubD_ the Number of Sub-Diagonals,
	 * the Number of Super-Diagonals is calculated
	 */
	public MatrixBand(final double[][] diags_, final int nSubD_) {
		diags = diags_;
		numSubDiags = nSubD_; 
		rows = new int[diags[numSubDiags].length];
	}

	/**filling the regular Matrix...
	 * @return a sparsely filled n*n Matrix 
	 */
	public double[][] toRegularMatrix() {
		final double[][] ret=new double[rows.length][rows.length];
		for (int i=rows.length; --i>=0; ) {
			double[] reti = ret[i];
			for (int j=rows.length; --j>=0; ) {
				final int k=i-numSubDiags;
				if ((j>=Math.max(0,k)) && (j<=Math.min(diags.length-1+k, rows.length))) {
					reti[j]=diags[j-k][i];
				//} else  {
				//	reti[j]=0; //not necessary 
				} 
			}
		}
		return ret;
	}

	/**	multiply vector by band diagonal math.matrix (2.4)
	 * 
	 * @param x Vector to map
	 * @param ret a Vector filled with the Result of the Mapping
	 */
	public double[] map(final double x[]) {
		final double[] ret = new double[x.length];
		map(x, ret); 
		return ret;
	}

	/**	multiply vector by band diagonal math.matrix (2.4)
	 * 
	 * @param x Vector to map
	 * @param ret Vector to fill with the Result of the Mapping
	 */
	public void map(final double[] x, final double[] ret) {
		for (int i=0; i<rows.length; i++) {
			final int k=i-numSubDiags;
			final int tmploop=Math.min(diags.length-1, rows.length-1-k);
			double sum = 0;
			for (int j=Math.max(0,-k); j<=tmploop; j++) {
				sum += diags[j][i]*x[j+k]; }
			ret[i] = sum; 
		}
		//return ret;
	}

	/** band diagonal systems, decomposition with partial Pivoting(2.4)
	 * @return the Sign of the Row Permutation
	 */
	private boolean decomposeLU() {
		if (isDecomposedLU()) 
			return sign; 
		decomposition = new double[rows.length][diags.length];

		//Rearrange the Storage (destroys tridiagonal Form!)
		for (int m=numSubDiags, i=0; i<numSubDiags; i++) {
			for (int j=numSubDiags-i; j<diags.length; j++) {
				diags[j-m][i]=diags[j][i]; } 
			m--;
			for (int j=diags.length-1-m; j<diags.length; j++) {
				diags[j][i]=0; } 
		}
		//actual Decomposition!
		sign = true;
		for (int m=numSubDiags-1, k=0; k < rows.length; k++) { //for each Row...
			if (m < rows.length-1) 
				m++; 
			int h=k;
			double max=Math.abs(diags[0][k]); //...find the Pivot
			for (int j=k+1; j<=m; j++) {
				if (max < Math.abs(diags[0][j])) {
					max = Math.abs(diags[0][j]);
					h=j;
				}
			}
			rows[k]=h;
			//if (dum == 0) a[k][1]=1e-20; //avoid Division by Zero...
			if (h != k) { //swap Rows...
				sign = !sign;
				MatrixDouble.SWAP_COLS_AT(diags, h, k);
			}
			//Perform Elimination
			for (int i=k+1; i<=m; i++) {
				final double tmp=diags[0][i]/diags[0][k];
				decomposition[k][i-k-1]=tmp;
				for (int j=1; j<diags.length; j++) {
					diags[j-1][i]=diags[j][i]-tmp*diags[j][k]; } 
				diags[diags.length-1][i]=0;
			}
		}
		return sign;
	}

	/**	band diagonal systems, backsubstitution (2.4)
	 * 
	 * @param b Vector to solve for, replaced by the Solution
	 */
	public void solveLuAt(final double[] b) {
		if(!isDecomposedLU()) { 
			decomposeLU(); }

		//Forward Substition, unscrambling the Permutation
		for (int m=numSubDiags-1, k=0; k < rows.length; k++) {
			final int h=rows[k];
			if (h != k) {
				final double swap = b[k]; b[k] = b[h]; b[h] = swap; 
			}
			if (m < rows.length-1) {
				m++; } 
			final double[] dk = decomposition[k];
			final double bk = b[k];
			for (int i=k+1; i<=m; i++) {
				b[i] -= dk[i-k-1]*bk; } 
		}
		//Backsubstition
		for (int m=0, i=rows.length; --i>=0; ) {
			double sum=b[i];
			for (int k=1; k<=m; k++) {
				sum -= diags[k][i]*b[k+i]; } 
			b[i]=sum/diags[0][i];
			if (m < diags.length-1) m++;
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Toeplitz and Vandermode Matrices
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** solve square Vandermonde Matrix systems V*w=q which take the following Form:
	 * <PRE> 
	 * 
	 * This is used to fit the Weights wi so as to match 
	 * the Values of the first N Moments qi of the Distribution (x1, x2, ..., xn) 
	 * |   1      1   ...    1  |  w0       q0
	 * |  x1     x2   ...   xn  |  w1       q1
	 * |  x1�    x2�  ...   xn� |  w2    =  q2
	 * |   .      .    .     .  |   .       .
	 * |   .      .    .     .  |   .       .
	 * |x1^n-1 x2^n-1 ... xn^n-1| w[n-1]   q[n-1]
	 * 
	 * The transposed Problem solves the Problem of 
	 * Fitting of Polynoms Coefficients ci to yi Values at Values xi  
	 * It is solved by direct Polynom Fitting. 
	 * |1 x1 x1� ... x1^n-1| c1   y1 
	 * |1 x2 x2� ... x1^n-1| c1   y1 
	 * |.  .  .   .     .  | .  = .  
	 * |.  .  .   .     .  | .    .  
	 * |1 xn xn� ... xn^n-1| cn   yn 
	 * 
	 * </PRE>
	 * Vandermonde Matrices are notoriously ill-conditioned. 
	 * 
	 * @param x Polynom Base Points 
	 * @param w 
	 * @param q
	 */
	final static public void solveVandermonde(final double x[], final double w[], final double q[]) {
		double b,s,t,xx;
		final int n = w.length;
		double[] c = new double[n];
		if (n == 1) { w[0]=q[0];
		} else {
			for (int i=0; i<w.length; i++) {
				c[i]=0; } 
			c[n-1] = -x[0];
			for (int i=1; i<w.length; i++) {
				xx = -x[i];
				for (int j=(n-i-1); j<(n-1); j++) {
					c[j] += xx*c[j+1]; } 
				c[n-1] += xx;
			}
			for (int i=0; i<w.length; i++) {
				xx=x[i];
				t=b=1;
				s=q[n-1];
				for (int k=n; --k >= 1;) {
					b=c[k]+xx*b;
					s += q[k-1]*b;
					t=xx*t+b;
				}
				w[i]=s/t;
			}
		}
	}
	
	/**	solve Toeplitz systems R*x=y which take the following Form:
	 * <pre>
	 * |r[0] r[ -1] r[ -2] ... r[ -n]| x0   y0
	 * |r[1] r[  0] r[ -1] ... r[1-n]| x1   y1
	 * |r[2] r[  1] r[  0] ... r[1-n]| x2 = y2
	 * | .      .      .   ...   .   | .    .
	 * |r[n] r[n-1] r[n-2] ... r[ 0 ]| xn   yn
	 * </pre>
	 * 
	 * @param r Vector with 2n+1 Elements
	 * @param x Solution
	 * @param y Input
	 * @param n
	 */
	final static public void solveToeplitz(final double r[], final double x[], final double y[]) {
		final int np1 = y.length;
		final int n = y.length-1;
		if (r[n] == 0) {
			throw new RuntimeException("toeplz-1 singular principal minor"); }
		final double[] g = new double[n];
		final double[] h = new double[n];
		x[0]=y[0]/r[n];
		if (n == 0) { 
			return;	}
		g[0]=r[n-1]/r[n];
		h[0]=r[n+1]/r[n];
		for (int m=0; m<np1; m++) {
			final int m1=m+1;
			double sxn = -y[m1];
			double sd  = -r[n];
			for (int j=0; j<=m; j++) {
				sxn+= r[n+m1-j]*x[j];
				sd += r[n+m1-j]*g[m-j];
			}
			if (sd == 0.0) {
				throw new RuntimeException("toeplz-2 singular principal minor"); } 
			x[m1]=sxn/sd;
			for (int j=0; j<=m; j++) {
				x[j] -= x[m1]*g[m-j];} 
			if (m1 == n) { 
				return; }
			double sgn = -r[n-1-m1];
			double shn = -r[n+1+m1];
			double sgd = -r[n];
			for (int j=0; j<=m; j++) {
				sgn += r[n+j-m1]*g[j];
				shn += r[n+m1-j]*h[j];
				sgd += r[n+j-m1]*h[m-j];
			}
			if (sd == 0 || sgd == 0) {
				throw new RuntimeException("toeplz-3 singular principal minor"); } 
			g[m1]=sgn/sgd;
			h[m1]=shn/sd;
			final int m2=m >> 1;
			final double pp=g[m1];
			final double qq=h[m1];
			for (int k=m, j=0; j<=m2; j++, k--) {
				final double gj=g[j];
				final double gk=g[k];
				final double hj=h[j];
				final double hk=h[k];
				g[j]=gj-pp*hk;
				g[k]=gk-pp*hj;
				h[j]=hj-qq*gk;
				h[k]=hk-qq*gj;
			}
		}
		throw new RuntimeException("toeplz - should not arrive here!");
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Testing Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static final void testToeplitz() {
		System.out.println("testing Toeplitz Matrix:");
		final int N = 5; 
		double[] r = new double[N+N-1];
		double[] x = new double[N];
		double[] y = new double[N];
		double[] s = new double[N];

		for (int i=1;i<=N; i++) {y[i-1]=0.1*i;} 
		for (int i=1;i<N+N;i++) {r[i-1]=1.0/i;}
		solveToeplitz(r,x,y);
		System.out.println("Solution vector:");
		for (int i=0; i<N; i++) {
			System.out.println("x["+i+"] ="+x[i]);} 
		System.out.println("\nTest of solution:");
		System.out.println("mtrx*soln \t original");
		for (int i=0; i<N; i++) {
			double sum=0;
			for (int j=0; j<N; j++) {
				sum += (r[N-1+i-j]*x[j]);} 
			System.out.println(sum+"\t"+y[i]);
			s[i] = sum;
		}
		Assert.EQUALS(y, s);
	}
	
	private static final void testVandermonde() {
		System.out.println("testing Vandermonde Matrix:");
		double[] x={1,1.5,2,2.5,3};
		double[] q={1,1.5,2,2.5,3};

		final double[] s=new double[x.length];
		final double[] w=new double[x.length];
		final double[] term=new double[x.length];
		solveVandermonde(x,w,q);
		System.out.println("\nSolution vector:");
		for (int i=0; i<x.length; i++) {
			System.out.println("w["+i+"]="+w[i]); } 
		System.out.println("\nTest of solution vector:");
		System.out.println("mtrx*sol'n \t original");
		double sum=0;
		for (int i=0; i<x.length; i++) {
			term[i]=w[i];
			sum += w[i];
		}
		System.out.println(sum+"\t"+q[1]);
		s[0] = sum;
		for (int i=1; i<x.length; i++) {
			sum=0;
			for (int j=0; j<x.length; j++) {
				term[j] *= x[j];
				sum += term[j];
			}
			System.out.println(sum+"\t"+q[i]);
			s[i] = sum;
		}
		Assert.EQUALS(q, s);
	}

	/** Tests Multiplication of a Vector with a Band Diagonal */
	private static final void testMul() {
		final int NP = 7;
		final int M1 = 2;
		final int M2 = 1;

		final double[][] a = generateTestMatrix(NP, M1, M2);
		MatrixBand matrix = new MatrixBand(a, M1);
		final double[][] aa = matrix.toRegularMatrix();
		final double[] x = generateTestVector(NP);
		double[] b = matrix.map(x);
		final double[] ax=new double[NP];
		for (int i=0; i<NP; i++) {
			ax[i]=0;
			for (int j=0; j<NP; j++) {
				ax[i] += aa[i][j]*x[j];} 
		}
		System.out.println("\tReference vector\tbanmul vector\n");
		for (int i=0; i<NP; i++) {
			System.out.println(ax[i]+", "+b[i]);} 
		Assert.EQUALS(ax, b);
	}

	//Test Vector
	private static final double[] generateTestVector(final int NP) {
		final double[] x=new double[NP];
		for (int i=0; i<NP; i++) {
			x[i]=(i+1)/10.0;} 
		return x;
	}

	private static final double[][] generateTestMatrix(final int NP, final int M1, final int M2) {
		final double[][] a=new double[M1+1+M2][NP];
		//Lower band 
		for (int i=0; i<M1; i++) { 
			for (int j=0; j<NP; j++) {
				a[i][j]=10*(j+1)+(i+1);} } 
		// Diagonal 
		for (int i=0; i<NP; i++) {
			a[M1][i]=(i+1);} 
		// Upper band 
		for (int i=0; i<M2; i++) {
			for (int j=0; j<NP; j++) {
				a[M1+1+i][j]=0.1*(j+1)+(i+1);} } 
		return a;
	}

	/** Tests the Decomposition and BackSubstition	 */
	private static final void testDecomposition() {
		double[][] a=new double[1+4][1+7];
		double[] x=new double[1+7];
		a = generateTestMatrix(8, 2, 2);
		x = generateTestVector(8);
		testDecomposition(a, x);
		MatrixDouble.RANDOMIZE_AT(a);
		VectorDouble.RANDOMIZE_AT(x);
		testDecomposition(a, x);
	}

	private static void testDecomposition(double[][] a, double[] x) {
		MatrixBand matrix = new MatrixBand(a, 2);
		final double[] b = matrix.map(x);
		System.out.println("Solution:");
		for (int i=0; i<7; i++) {
			System.out.println(i+"\t"+x[i]);} 
		matrix.solveLuAt(b); //solve with b for x
		for (int i=0;i<7; i++) {
			System.out.println(i+"\t"+b[i]+"\t"+x[i]);}
		Assert.EQUALS(b, x);
		System.out.println("The second Series should be identical!");
	}

	/**
	 * tests all Methods of this Class
	 * @param args Command Line Parameters 
	 * @throws IOException
	 */
	final static public void main(final String[] args) {
		testMul();
		testDecomposition();
		testVandermonde();
		testToeplitz();
	}
	
}
