/*
 * File Name: Eigenvalues.java
 * Created on: 29.10.2003
 *
 */
package math.matrix;

import streamIO.Assert;
import streamIO.Log;
import function.byref.ByRefDouble;

/**
 * Title: Eigenvalues<p>
 * Description:
 * Groups static Methods to calculate Eigenvalues of general Matrices.
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Similar Classes: 
 * @see math.MatrixSymmetric which calculates EigenValues AND EigenVectors 
 * for symmetric Matrices. 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class Eigenvalues {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(Eigenvalues.class, 0);

	/////////////////////////////////////////////////////////////////////////////////////

	/** Base of the Machine's Float Point Operations	 */
	final static public int RADIX = 2; 

	/** Balances a nonsymmetric, square math.matrix in Place (11.5), keeping its Eigenvalues, 
	 * by applying Trafos with Diagonal Matrices consisting of binary Powers, 
	 * so no Rounding Errors are introduced. (actually only the Exponent could be modified).  
	 * This O(n*n) Operation considerably increases the Stability of finding the EigenValues  
	 * A symmetric Matrix is already balanced and unaffected by this Operation. 
	 * @param a Matrix to balance in Place
	 */
	final static public void BALANCE(final double[][] a) {
		final int n = a.length;
		final int sqrdx=RADIX*RADIX;
		boolean finished = false;
		while (!finished) {
			finished=true;
			for (int i=0;i<n;i++) { //all Columns/Rows
				final double[] ai = a[i];

				double rowSum = 0;
				double colSum = 0;
				for (int j=0;j<n;j++) {
					if (j != i) { //
						colSum += Math.abs(a[j][i]);
						rowSum += Math.abs(ai[j]);
					}
				}
				if ((colSum != 0) && (rowSum != 0)) { //both nonzero
					final double s=colSum+rowSum; //original Scale

					double factor=1;
					//for small colSum
					double g=rowSum/RADIX;
					while (colSum < g) { //find closest integer Power of 4
						factor *= RADIX; 
						colSum *= sqrdx;
					}

					//for large colSum
					g=rowSum*RADIX;
					while (colSum > g) {
						factor /= RADIX;
						colSum /= sqrdx;
					}

					//apply Similarity Translation
					if ((colSum+rowSum)/factor < 0.95*s) { 
						finished=false;
						g=1/factor;
						for (int j=0;j<n;j++) ai[j] *= g;
						for (int j=0;j<n;j++) a[j][i] *= factor;
					}
				}
			}
		}
	}

	/**	Reduces a general math.matrix in Place to Hessenberg form (11.5)
	 * by an Elimination Algorithm based on Gaussian Elimination. 
	 * The Transformation Matrix is not being retained. 
	 * 
	 * @param a the real (possibly nonsymmetric) Matrix, reduced in Place
	 * Elements a[i][j] with i>j+1 are undefined and assumed to be 0 on return. 
	 */
	final static public void HESSENBERG(final double[][] a) {
		final int n = a.length; 
		for (int m=1; m<n-1; m++) { //m = r+1 in the Text
			double xMax = 0, xAbs= 0;
			int maxRow=m;
			for (int j=m; j<n; j++) { //find the Pivot 
				if (Math.abs(a[j][m-1]) > xAbs) {
					maxRow=j; xMax = a[maxRow][m-1]; xAbs = Math.abs(xMax);
				}
			}
			if (maxRow != m) { //Swapping of same Rows and Cols i and j
				MatrixDouble.SWAP_COLS_AT(a, maxRow, m);
				MatrixDouble.SWAP_ROWS_AT(a, maxRow, m); //efficiently swaps whole Rows, but only the right Part is necessary
			}
			if (xMax != 0) { //Actual Elimination
				ELIMINATE(a, n, m, xMax);
			}
		}
	}

	/** 
	 * @see #HESSENBERG(double[][]) uss this Method exclusively  
	 * @param a the Matrix to perform Elimination for
	 * @param n the Size of the Matrix to consider
	 * @param m the current Index to eliminate 
	 * @param xMax the Value to eliminate for
	 */
	private static void ELIMINATE(final double[][] a, final int n, final int m, final double xMax) {
		final double[] am = a[m];
		for (int i=m+1; i<n; i++) {
			final double[] ai = a[i];
			double y=ai[m-1];
			if (y != 0) {
				y /= xMax;
				ai[m-1]=y;
				for (int j=m; j<n; j++) {
					ai[j] -= y*am[j];
				}
				for (int j=0; j<n; j++) {
					a[j][m] += y*a[j][i];
				}
			}
		}
	}

	/**	calculates eigenvalues of a Hessenberg math.matrix (11.6)
	 * 
	 * @param a (balanced) Matrix to calculate all Eigenvalues from 
	 * @param wr filled with real Parts of the EigenValues 
	 * @param wi filled with imaginary Parts of the EigenValues 
	 */
	final static public void EIGENVALUES(final double[][] a, final double[] wr, final double[] wi) {
		EIGENVALUES(a, a.length, wr, wi); 
	}

	/**	calculates eigenvalues of a Hessenberg math.matrix (11.6)
	 * 
	 * @param a (balanced) Matrix to calculate all Eigenvalues from 
	 * @param n the Size of the Matrix to consider: from 0 to n-1
	 * @param wr filled with real Parts of the EigenValues 
	 * @param wi filled with imaginary Parts of the EigenValues 
	 */
	final static public void EIGENVALUES(final double[][] a, final int n, final double[] wr, final double[] wi) {
		double anorm = Math.abs(a[1][1]);
		for (int i=1;i<n;i++) {
			final double[] ai = a[i];
			for (int j=(i-1);j<n;j++) {
				anorm += Math.abs(ai[j]);
			}
		}
		
		double t = 0;
		for (int nn=n-1;nn >= 0;) { //until all Eigenvalues have been calculated...
			int iterations=0;
			int l;
			do {
				for (l=nn; l>=1; l--) {
					double s=Math.abs(a[l-1][l-1])+Math.abs(a[l][l]);
					if (s == 0) {
						s=anorm; } 
					if ((Math.abs(a[l][l-1]) + s) == s) {
						break; } 
				}
				double x=a[nn][nn];
				if (l == nn) { //real Eigenvalue
					wr[nn]=x+t; L.n("t="+t);
					wi[nn]=0;
					nn-=1; 
				} else {
					double y=a[nn-1][nn-1];
					double w=a[nn][nn-1]*a[nn-1][nn];
					if (l == (nn-1)) { //complex Pair of Eigenvalues
						SET_COMPLEX_EV_PAIR(wr, wi, t, nn, x, y, w);
						nn -= 2;
						continue; //just to save more Nesting...
					} 
					//not found, increase #iterations
					++iterations; L.n("iterations=").l(iterations);
					if (iterations == 30) {
						throw new RuntimeException("Too many iterations in finding Eigenvalues..."); } 
					if (iterations == 10 || iterations == 20) { //adjustment of Shift Parameter t to improve Convergence
						t += x; L.n("t="+t); //
						final double s = EV_SHIFT(a, nn, x);
						y = x = 0.75*s;
						w = -0.4375*s*s;
					}
					IMPROVE_EV(a, nn, l, x, y, w);
				}
			} while (l < nn-1);
		}
	}

	/** performs the Shift on Matrix a by x	 */
	private static double EV_SHIFT(final double[][] a, final int nn, final double x) {
		for (int i=0; i<nn; i++) { //adjusting the Diagonal: A-=x*1
			a[i][i] -= x; } 
		final double s=Math.abs(a[nn][nn-1])+Math.abs(a[nn-1][nn-2]);
		return s;
	}

	/** sets the Values of a complex conjugate Pair of Eigenvalues	 */
	private static void SET_COMPLEX_EV_PAIR(final double[] wr, final double[] wi, final double t, final int nn,
		double x, final double y, final double w) {
		final double p=0.5*(y-x);
		final double q=p*p+w;
		double z=Math.sqrt(Math.abs(q));
		x += t; L.n("t="+t);
		if (q >= 0) {
			z=p+ByRefDouble.ASSIGN_SIGN(z,p);
			wr[nn-1] = wr[nn]=x+z;
			if (z != 0)wr[nn]=x-w/z;
			wi[nn-1] = wi[nn] = 0;
		} else {
			wr[nn-1]=wr[nn]=x+p;
			wi[nn-1]= -(wi[nn]=z);
		}
	}

	/** improve the Estimate for the current Eigenvalue
	 * by modifying a
	 * @param a the Matrix to search Eigenvalues for
	 * @param nn the Index of the current Eigenvalue
	 * @param l 
	 * @param x 
	 * @param y 
	 * @param w 
	 */
	private static void IMPROVE_EV(final double[][] a, final int nn, final int l, double x, double y, double w) {
		int m=(nn-2);
		double p = 0;
		double q = 0; 
		double r = 0;
		for (;m>=0; m--) {
			double z=a[m][m];
			double s=y-z;
			r=x-z;
			p=(r*s-w)/a[m+1][m]+a[m][m+1];
			q=a[m+1][m+1]-z-r-s;
			r=a[m+2][m+1];
			s=Math.abs(p)+Math.abs(q)+Math.abs(r);
			p /= s;
			q /= s;
			r /= s;
			if (m == l) {
				break; } 
			final double u=Math.abs(a[m][m-1])*(Math.abs(q)+Math.abs(r));
			final double v=Math.abs(p)*(Math.abs(a[m-1][m-1])+Math.abs(z)+Math.abs(a[m+1][m+1]));
			if ((u+v) == v) { //faster than testing for abs. Values, if full Accuracy is wanted!
				break; } 
		}
		for (int i=m+2; i<=nn; i++) {
			a[i][i-2]=0;
			if (i != (m+2)) {
				a[i][i-3]=0; } 
		}
		for (int k=m;k<=nn-1;k++) {
			final double[] ak0 = a[k];
			final double[] ak1 = a[k+1];
			if (k != m) {
				p=ak0[k-1];
				q=ak1[k-1];
				r=0;
				if (k != (nn-1)) r=a[k+2][k-1];
				if ((x = Math.abs(p)+Math.abs(q)+Math.abs(r)) != 0) {
					p /= x;
					q /= x;
					r /= x;
				}
			}
			double s=ByRefDouble.ASSIGN_SIGN(Math.sqrt(p*p+q*q+r*r), p);
			if (s != 0) {
				if (k == m) {
					if (l != m)
					ak0[k-1] = -ak0[k-1];
				} else
					ak0[k-1] = -s*x;
				p += s;
				x = p/s;
				y = q/s;
				double z=r/s;
				q /= p;
				r /= p;
				for (int j=k; j<=nn; j++) {
					p=ak0[j]+q*ak1[j];
					if (k != (nn-1)) {
						p += r*a[k+2][j];
						a[k+2][j] -= p*z;
					}
					ak1[j] -= p*y;
					ak0[j] -= p*x;
				}
				int mmin = nn<k+3 ? nn : k+3;
				for (int i=l; i<=mmin; i++) {
					p=x*a[i][k]+y*a[i][k+1];
					if (k != (nn-1)) {
						p += z*a[i][k+2];
						a[i][k+2] -= p*r;
					}
					a[i][k+1] -= p*q;
					a[i][k] -= p;
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	/// testing Methods
	//////////////////////////////////////////////////////////////////////////////////////

	/** @see #BALANCE(double[][]) is tested	 */
	private static final void testBalance() {
		final int NP=5;
		final double[][] a=new double[NP][NP];
		//create a very unsymmetric Matrix
		for (int i=0;i<NP;i++) {
			final double[] ai = a[i];
			for (int j=0;j<NP;j++) {
				ai[j] = ((((i & 1) == 0) && ((j & 1) != 0)) ? 100 : 1);
			}
		}
		writeNorms(a);
		L.n("\n***** Balancing math.matrix *****\n");
		BALANCE(a);
		writeNorms(a);
		L.n();
	}

	/** Writes the Row and Column norms of the given Matrix 
	 * @param a Matrix to calculate and write the Norms for...
	 */
	private static final void writeNorms(final double[][] a) {
		final int NP=a.length;
		for (int i=0;i<NP;i++) {
			final double[] ai = a[i];
			double rowSum=0;
			double colSum=0;
			for (int j=0;j<NP;j++) {
				rowSum += Math.abs(ai[j]);
				colSum += Math.abs(a[j][i]);
			}
			L.n("rowSum["+i+"]="+rowSum);
			L.n("colSum["+i+"]="+colSum);
			L.n();
		}
	}

	/** asymmetric Test Matrix	 */
	static final double[][] testMatrix=
		{ { 1, 2,  0, 0, 0,
		},{-2, 3,  0, 0, 0,
		},{ 3, 4, 50, 0, 0,
		},{-4, 5,-60, 7, 0,
		},{-5, 6,-70, 8,-9}
		};
	
	static final double[][] eigenValues = { { 50, 2,2,7,-9
		},{ 0, -1.7320508075688739, 1.7320508075688739, 0, 0}
	};
	
	/** Tests the Eigenvalues Calculation	 */
	private static final void testEIGENVALUES() {
		final int NP=testMatrix.length;
		double[] wr=new double[NP];
		double[] wi=new double[NP];
		double[][] a=MatrixDouble.COPY(testMatrix);
		L.n("math.matrix:\n");
		for (int i=0; i<NP; i++) {
			for (int j=0; j<NP; j++) {
				L.l(a[i][j]); } 
			L.n("\n");
		}
		BALANCE(a);
		HESSENBERG(a);
		EIGENVALUES(a,wr,wi);
		L.n("eigenvalues:\n");
		L.n("real \t imag.");
		for (int i=0; i<NP; i++) { 
			L.n(wr[i]+"\t"+wi[i]);
		} 
		Assert.EQUALS(eigenValues[0], wr);
		Assert.EQUALS(eigenValues[1], wi);
	}

	static final double testHessenberg[][]=
		{ {1, 2, 300, 4, 5,
		},{2, 3, 400, 5, 6,
		},{3, 4,   5, 6, 7,
		},{4, 5, 600, 7, 8,
		},{5, 6, 700, 8, 9}
		};

	static final double testHessenbergResult[][]= 
		{ { 1, 39.375,   9.618,  3.333,  4 
		},{24, 27.333, 116.097, 48    , 48 
		},{ 0, 85.514, -4.7805, -1.333, -2 
		},{ 0,  0    ,  5.1885,  1.447,  2.171 
		},{ 0,  0    ,  0     ,  0    ,  0 
		}
	};

	/** Tests the Hessenberg Reduction	 */
	private static final void testHessenberg() {
		final int NP=testHessenberg.length;
		double[][] a = MatrixDouble.COPY(testHessenberg);
		L.n("***** original math.matrix *****\n");
		for (int i=0; i<NP; i++) {
			for (int j=0; j<NP; j++) {
				L.l(a[i][j]+", "); } 
			L.n();
		}
		L.n("***** balance math.matrix *****\n");
		BALANCE(a);
		for (int i=0; i<NP; i++) {
			for (int j=0; j<NP; j++) { 
				L.l(a[i][j]); } 
			L.n();
		}
		L.n("***** reduce to hessenberg form *****\n");
		HESSENBERG(a);
		//artificially set the lower Diagonal Elements to 0
		for (int j=0; j<NP-2; j++) {
			for (int i=j+2; i<NP; i++) {
				a[i][j]=0; }
		}
		for (int i=0; i<NP; i++) {
			for (int j=0; j<NP; j++) { 
				L.l(a[i][j]); } 
			L.n();
		}
		Assert.EQUALS(testHessenbergResult, a, .001);
	}

	final static public void main(String[] args) {
		testBalance();
		testHessenberg(); 
		testEIGENVALUES();
	}

}
