/*
 * File Name: MatrixQR.java
 * Created on: 08.11.2003
 *
 */
package math.matrix;

import math.vector.VectorDouble;
import streamIO.Assert;
import function.byref.ByRefDouble;

/**
 * QR-decomposable matrix that retains its decomposition in place as instance state, so a
 * changed coefficient can be re-solved via an O(n^2) update rather than a full re-decomposition.
 *
 * <p>QR decomposition can also be performed on non-square matrices.
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
 * mtime: 2026-09-05T12:47:06Z
 * digest: 78fa0b49e68ebbb0185f72de8a9abf7840acf96b220224039015313c31f75d66
 * stale: false
 * tags: [code/qr_decomposition, code/numerical_linear_algebra]
 * concepts: [QR Decomposition]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class MatrixQR extends MatrixDouble {

	////////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Constructs an empty QR-decomposable matrix with the given initial capacity and capacity increment.
	 * @param initialCapacity
	 * @param capacityIncrement_
	 */
	public MatrixQR(final int initialCapacity, final int capacityIncrement_) {
		super(initialCapacity, capacityIncrement_);
	}

	/** Constructs an empty QR-decomposable matrix with the given initial capacity.
	 * @param initialCapacity
	 */
	public MatrixQR(final int initialCapacity) {
		super(initialCapacity);
	}

	/** Constructs an empty QR-decomposable matrix with the default initial capacity.
	 */
	public MatrixQR() {
		super();
	}

	/** Constructs a QR-decomposable matrix by copying the given double array.
	 * @param a
	 */
	public MatrixQR(final double[][] a) {
		super(a);
	}

	/** Constructs a QR-decomposable matrix by copying from the given object of any supported type.
	 * @param arg
	 */
	public MatrixQR(final Object arg) {
		super(arg);
	}

	/** Constructs a QR-decomposable matrix from the given object, with the given capacity increment.
	 * @param arg
	 * @param capacityIncrement_
	 */
	public MatrixQR(final Object arg, final int capacityIncrement_) {
		super(arg, capacityIncrement_);
	}

	/** Constructs a QR-decomposable matrix by copying the given float array, with the given capacity increment.
	 * @param arg
	 * @param capacityIncrement_
	 */
	public MatrixQR(final float[][] arg, final int capacityIncrement_) {
		super(arg, capacityIncrement_);
	}

	/** Constructs a QR-decomposable matrix by copying the given float array.
	 * @param arg
	 */
	public MatrixQR(final float[][] arg) {
		super(arg);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables for QR Decomposition and Backsubstition
	////////////////////////////////////////////////////////////////////////////////////////

	/** Diagonal Elements of the upper Right Matrix R.	 */
	protected double[] diag;
	
	/** Householder Trafo Factors: Q[j]=1-u[j]*u[j]/c[j]	*/
	protected double[] c;

	////////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables for QR Updates
	////////////////////////////////////////////////////////////////////////////////////////	

	/** Householder Trafo Matrix: Q	*/
	protected double[][] q;

	/** Transpose = Inverse of Householder Trafo Matrix: Q^t*Q=1	*/
	protected double[][] qt;

	/** Explicit full right upper Triangular Matrix R so that: A=Q*R	*/
	protected double[][] r;

	////////////////////////////////////////////////////////////////////////////////////////
	/// Member Methods for QR Decomposition and Update
	////////////////////////////////////////////////////////////////////////////////////////

	/** Solving the Equation A*x=b by QR backsubstitution (2.10)
	 * 
	 * @param b right Side of the Equation, overwritten in Place by the Solution x. 
	 */
	public void solveAt(final double[] b) {
		decomposeAt();
		SOLVE_QR(items, c, diag, b);
	}

	/** Mapping the Vector b from the right: A*b for QR Decomposition (2.10)
	 * @param b Column Vector multiplied from the right 
	 */
	public double[] map(final double[] b) {
		if (c == null) {
			return MAP(items, b);
		}
		calcFullQR();
		final double[] ret = new double[b.length];
		//calculate A*b=Q*R*b <=> xi=qij*rjk*bk
		for(int i = ret.length; --i >= 0; ) {
			double xi = 0;
			for(int j = ret.length; --j >= 0; ) {
				final double[] rj = items[j];
				double xij = diag[j]*b[j]; //rjk==diag[j] f�r j = k
				for(int k = ret.length; --k > j; ) { //rjk==0 f�r k < j
					xij+=rj[k]*b[k]; 
				}
				xi+=q[i][j]*xij; //qij=qji
			}
			ret[i] = xi;
		}
		return ret; 
	}

	/** Mapping the Vector b from the left: b*A for QR Decomposition (2.10)
	 * @param b Row Vector multiplied from the left 
	 */
	public double[] mapTrp(final double[] b) {
		if (c == null) {
			return MAP(b, items);
		}
		calcFullQR();
		final double[] ret = new double[b.length];
		//calculate b*A=b*Q*R <=> xk=bi*qij*rjk <=> xk=bi*qij*rjk
		for(int i = ret.length; --i >= 0; ) {
			final double bi = b[i];
			final double[] qi = q[i];
			for(int j = ret.length; --j >= 0; ) {
				final double xij = bi*qi[j];
				final double[] rj = items[j];
				ret[j]+=xij*diag[j]; //rjk==diag[j] f�r j = k
				for(int k = ret.length; --k > j; ) { //rjk==0 f�r k < j
					ret[k]+=xij*rj[k]; 
				}
			}
		}
		return ret; 
	}

	/**	Solves the Equation R*x=b 
	 * 
	 * @param a original Matrix modified in Place by QR Decomposition
	 * @param diag Diagonal Elements of the upper Right Matrix R. 
	 * @param b right Side of the Equation, overwritten in Place by the Solution x. 
	 */
	public void solveR(final double[] b) {
		decomposeAt();
		SOLVE_R(items, diag, b);
	} 
	
	
	/**	QR decomposition of a Matrix into an upper right R and an orthogonal Matrix Q: A=Q*R with Q*Q^t=1 
	 * This requires double as many Operations as simple LR Decomposition. 
	 * @param a original Matrix modified in Place by QR Decomposition
	 * @param c Householder Trafo Factors: Q[j]=1-u[j]*u[j]/c[j]
	 * @param diag Diagonal Elements of the upper Right Matrix R. 
	 * @return true if the Matrix could be decomposed without Singularities, 
	 * false if there was one or more Singularities during the Process. 
	 */
	public boolean decomposeAt(){
		if (c == null) {
			c = new double[itemCount];
			diag = new double[itemCount];
			DECOMPOSE_QR(items, c, diag);
		}
		return true;  
	}

	/** Calculates the explicit Q and R matrices 
	 * 
	 * @param q orthonormal Householder Matrix Q with Q*Q^t = 1
	 * @param qt Transpose of q == Inverse of q
	 * @param r upper right Triangular Matrix 
	 */
	private void calcFullQR() {
		decomposeAt();
		if (qt == null) {
			qt = new double[itemCount][itemCount];
			q  = new double[itemCount][itemCount];
			r  = new double[itemCount][itemCount];
			CALC_FULL_QR(items, q, qt, r, diag);
		}
	}
	
	/**	update an explicit QR decomposition in O(n�) Operations (2.10):
	 * Q'*R' = A' = A+s*t = Q*R+s*t = Q*(R+u*v)
	 * This is relatively easy, because no Pivoting was performed. 
	 * 
	 * @param r Matrix R 
	 * @param qt Matrix Q^t the Transpose of the Transformation Matrix
	 * @param u Correction Column Vector
	 * @param v Correction Row Vector
	 */
	public void update(final double u[], final double v[]) {
		calcFullQR();
		UPDATE_QR(r, qt, u, v);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	/// static Methods for QR Decomposition and Update
	////////////////////////////////////////////////////////////////////////////////////////

	/** Solving the Equation A*x=b by QR backsubstitution (2.10)
	 * 
	 * @param a original Matrix modified in Place by QR Decomposition
	 * @param c Householder Trafo Factors: Q[j]=1-u[j]*u[j]/c[j]
	 * @param diag Diagonal Elements of the upper Right Matrix R. 
	 * @param b right Side of the Equation, overwritten in Place by the Solution x. 
	 */
	final static public void SOLVE_QR(final double[][] a, final double[] c, final double[] diag, final double[] b) {
		final int n = a.length;
		//Calculate b'=Q^t*b in Place
		for (int j=0; j<n-1; j++) {
			double sum = 0;
			for (int i=j; i<n; i++) {
				sum += a[i][j]*b[i]; } 
			final double tau = sum/c[j];
			for (int i=j; i<n; i++) {
				b[i] -= tau*a[i][j]; } 
		}
		//solve for x: R*x=Q^t*b
		SOLVE_R(a, diag, b);
	}

	/**	Solves the Equation R*x=b 
	 * 
	 * @param a original Matrix modified in Place by QR Decomposition
	 * @param diag Diagonal Elements of the upper Right Matrix R. 
	 * @param b right Side of the Equation, overwritten in Place by the Solution x. 
	 */
	final static public void SOLVE_R(final double[][] a, final double[] diag, final double[] b) {
		final int n = a.length;
		b[n-1] /= diag[n-1];
		for (int i=n-2; i>=0; i--) {
			double sum = 0;
			for (int j=i+1; j<n; j++) {
				sum += a[i][j]*b[j];} 
			b[i]=(b[i]-sum)/diag[i];
		}
	}
	
	/**	Jacobi rotation used by of the given Matrix r (2.10)
	 * 	Creates the Rotation Matrix for tan x = a/b 
	 * @param r Matrix to rotate
	 * @param qt Q^t transposed Transformation Matrix
	 * @param i The Coordinates i and i+1 are rotated
	 * @param a Abscissa to determine the Angle
	 * @param b Ordinate to determine the Angle
	 */
	final static public void ROTATE_AT
	( final double[][] r, final double[][] qt, final int i, final double a, final double b) {
		double cos,sin;
	
		//Calculate cos(t) and sin(t) with tan(t)=sin(t)/cos(t)=b/a
		if (a == 0) {
			cos=0;
			sin=(b >= 0 ? 1 : -1);
		} else if (Math.abs(a) > Math.abs(b)) {
			final double fact=b/a;
			cos=ByRefDouble.ASSIGN_SIGN(1/Math.sqrt(1+(fact*fact)),a);
			sin=fact*cos;
		} else {
			final double fact=a/b;
			sin=ByRefDouble.ASSIGN_SIGN(1/Math.sqrt(1+(fact*fact)),b);
			cos=fact*sin;
		}
		//perform the Rotation on R
		for (int j=i; j<r.length; j++) {
			final double y=r[i  ][j];
			final double w=r[i+1][j];
			r[i  ][j]=cos*y-sin*w;
			r[i+1][j]=sin*y+cos*w;
		}
		//perform the Rotation on Q^t
		for (int j=0; j<r.length; j++) {
			final double y=qt[i  ][j];
			final double w=qt[i+1][j];
			qt[i  ][j]=cos*y-sin*w;
			qt[i+1][j]=sin*y+cos*w;
		}
	}
	
	/**	update an explicit QR decomposition in O(n�) Operations (2.10):
	 * Q'*R' = A' = A+s*t = Q*R+s*t = Q*(R+u*v)
	 * This is relatively easy, because no Pivoting was performed. 
	 * 
	 * @param r Matrix R 
	 * @param qt Matrix Q^t the Transpose of the Transformation Matrix
	 * @param u Correction Column Vector
	 * @param v Correction Row Vector
	 */
	final static public void UPDATE_QR(final double[][] r, final double[][] qt, final double[] u, final double[] v) {
		UPDATE_QR(r, qt, u, v, null);
	}
	
	/**	update an explicit QR decomposition in O(n�) Operations (2.10):
	 * Q'*R' = A' = A+s*t = Q*R+s*t = Q*(R+u*v)
	 * This is relatively easy, because no Pivoting was performed. 
	 * 
	 * @param r Matrix R 
	 * @param qt Matrix Q^t the Transpose of the Transformation Matrix
	 * @param u Correction Column Vector
	 * @param v Correction Row Vector
	 */
	final static public void UPDATE_QR(final double[][] r, final double[][] qt, final double[] u, final double[] v, final double[][] q) {
		final int n = r.length;
		int k = n;
		for (;--k>=0;) {
			if (u[k] != 0) {
				break;} 
		}
		if (k < 0) {
			k=0; } 
		for (int i=k; --i>=0; ) {
			ROTATE_AT(r,qt,i,u[i],-u[i+1]);
			if (u[i] == 0) {
				u[i]=Math.abs(u[i+1]); 
			} else if (Math.abs(u[i]) > Math.abs(u[i+1])) {
				u[i]=Math.abs(u[i])*Math.sqrt(1+ByRefDouble.SQR(u[i+1]/u[i]));
			} else {
				u[i]=Math.abs(u[i+1])*Math.sqrt(1+ByRefDouble.SQR(u[i]/u[i+1]));
			} 
		}
		for (int j=0; j<n; j++) {
			r[0][j] += u[0]*v[j]; } 
		for (int i=0; i<k; i++) {
			ROTATE_AT(r,qt,i,r[i][i],-r[i+1][i]); } 
	}
	
	/**	QR decomposition of a Matrix into an upper right R and an orthogonal Matrix Q: A=Q*R with Q*Q^t=1 
	 * This requires double as many Operations as simple LR Decomposition. 
	 * @param a original Matrix modified in Place by QR Decomposition
	 * @param c Householder Trafo Factors: Q[j]=1-u[j]*u[j]/c[j]
	 * @param diag Diagonal Elements of the upper Right Matrix R. 
	 * @return true if the Matrix could be successfully decomposed, false if it was singular during the Process. 
	 */
	final static public boolean DECOMPOSE_QR(final double[][] a, final double[] c, final double[] diag){
		final int n = a.length;
		double scale,sigma,sum,tau;
		boolean ret = true;
		for (int k=0; k<n-1; k++) {
			scale=0;
			for (int i=k; i<n; i++) {
				scale=Math.max(scale,Math.abs(a[i][k])); } 
			if (scale == 0) {
				ret = false;
				c[k]=diag[k]=0;
			} else {
				for (int i=k; i<n; i++) {
					a[i][k] /= scale;}
				sum=0; 
				for (int i=k; i<n; i++) {
					sum += ByRefDouble.SQR(a[i][k]); } 
				sigma=ByRefDouble.ASSIGN_SIGN(Math.sqrt(sum),a[k][k]);
				a[k][k] += sigma;
				c[k]=sigma*a[k][k];
				diag[k] = -scale*sigma;
				for (int j=k+1; j<n; j++) {
					sum=0;
					for (int i=k; i<n; i++) {
						sum += a[i][k]*a[i][j]; } 
					tau=sum/c[k];
					for (int i=k; i<n; i++) {
						a[i][j] -= tau*a[i][k]; } 
				}
			}
		}
		diag[n-1]=a[n-1][n-1];
		if (diag[n-1] == 0) {
			return false; }
		return ret; 
	}

	/** Calculates the explicit Q and R matrices from the decomposed Form
	 * 
	 * @param a original Matrix modified in Place by QR Decomposition
	 * @param diag Diagonal Elements of the upper Right Matrix R. 
	 * @param q orthonormal Householder Matrix Q with Q*Q^t = 1
	 * @param qt Transpose of q == Inverse of q
	 * @param r upper right Triangular Matrix 
	 */
	public static void CALC_FULL_QR(
		final double[][] a,
		final double[][] q,
		final double[][] qt,
		final double[][] r,
		final double[] diag) {
		for (int k=r.length; --k>=0; ) {
			for (int i=r.length; --i>=0; ) {
				if (i > k) {
					r[k][i]=a[k][i];
					q[k][i]=0;
				} else if (i < k) {
					r[k][i]=q[k][i]=0;
				} else {
					r[k][i]=diag[k];
					q[k][i]=1;
				}
			}
		}
		//Calculate Q
		for (int i=r.length-1; --i>=0; ) {
			double con = 0;
			for (int k=i; k<a.length; k++) {
				con += a[k][i]*a[k][i]; } 
			con/=2;
			for (int k=i; k<qt.length; k++) {
				for (int ll=i; ll<qt.length; ll++) {
					qt[k][ll]=0;
					for (int j=i;j<a.length;j++) {
						qt[k][ll] += q[j][ll]*a[k][i]*a[j][i]/con;
					}
				}
			}
			for (int k=i; k<q.length; k++) {
				for (int j=i; j<q.length; j++) {
					q[k][j] -= qt[k][j]; } 
			}
		}
		//fill up Q^t 
		for (int k=qt.length; --k>=0; ) {
			for (int j=qt.length; --j>=0; ) { 
				qt[k][j]=q[j][k];}} 
	}
		
	//////////////////////////////////////////////////////////////////////////////////////
	/// static Testing and Main Methods
	//////////////////////////////////////////////////////////////////////////////////////
	
	/** tests the QR Decomposition	 */
	private static final void testDecomposeQR(){
		L.n("tests the QR Decomposition:\n");
		
		/* Print out a-math.matrix for comparison with product of
		   Q and R decomposition matrices */
		MatrixQR matrix = new MatrixQR(getTestMatrix()); 
		L.n("Original math.matrix:\n"); STREAM(matrix.items);
		/* Perform the decomposition */
		if (!matrix.decomposeAt()) {
			System.err.print("Singularity in QR decomposition.\n"); }
		matrix.calcFullQR(); //math.matrix.items, math.matrix.q, math.matrix.qt, math.matrix.r, math.matrix.diag);
		/* compute product of Q and R matrices for comparison
		   with original math.matrix. */
		double[][] x = CAT(matrix.q,matrix.r);
		L.n("Product of Q and R matrices:\n");
		STREAM(x);
		Assert.EQUALS(getTestMatrix(), x); 
		L.n("Q math.matrix of the decomposition:\n");
		STREAM(matrix.q);
		L.n("R math.matrix of the decomposition:\n");
		STREAM(matrix.r);
		L.n("***********************************\n");
	}

	/** tests Solving by QR Backsubstition	 */
	private static final void testSolveQR() {
		L.n("tests Solving by QR Backsubstition:\n");
		final MatrixQR matrix = new MatrixQR(getTestMatrix()); 
		final double[][] b = getTestVectors();
		final int m = b.length;
		/* Do qr decomposition */
		boolean decomposed = matrix.decomposeAt();
		if (!decomposed) {
			System.err.print("Singularity in QR decomposition.\n"); }
		/* Solve equations for each right-hand vector */
		for (int k=0; k<m; k++) {
			final double[] x = VectorDouble.COPY(b[k]);
			matrix.solveAt(x);
			/* Test results with original math.matrix */
			L.n("right-hand side vector:\n");
			VectorDouble.STREAM(b[k], System.out);
			L.n("\nResult of math.matrix applied to sol'n vector");
			double[] y = MAP(getTestMatrix(), x);
			VectorDouble.STREAM(y, System.out);
			Assert.EQUALS(b[k], y);
			y = matrix.map(x);
			VectorDouble.STREAM(y, System.out);
			Assert.EQUALS(b[k], y);
			L.n("*********************************\n");
		}
	}
	
	/** tests updating a QR Decomposition	 */
	private static final void testUpdateQR() {
		L.n("tests updating a QR Decomposition:\n");
		double[] u, v;
		
		final double[][] au = getTestMatrix(); 
		final MatrixQR matrix = new MatrixQR(getTestMatrix()); 
		final double[][] s1 = getTestVectors();
		final int NP = matrix.itemCount;
		final int n = NP; 
		u=new double[NP];
		v=new double[NP];
		/* Print out a-math.matrix for comparison with product of
		   Q and R decomposition matrices */
		L.n("Original math.matrix:\n");
		STREAM(matrix.items);
		/* updated math.matrix we'll use later */
		for (int k=0; k<n; k++) {
			for (int i=0; i<n; i++) {
				au[k][i] += s1[0][k]*s1[1][i];
			} 
		}
		/* Perform the decomposition */
/*		final boolean decomposed = math.matrix.decomposeAt();
		if (!decomposed) {
			System.err.print("Singularity in QR decomposition.\n");} 
*/		// find the Q and R matrices 
		matrix.calcFullQR(); //performs Decomposition implicitly!
		// compute product of Q and R matrices for comparison with original math.matrix. 
		double[][] x = CAT(matrix.q, matrix.r);
		L.n("Product of Q and R matrices:\n"); STREAM(x);  
		L.n("Q math.matrix of the decomposition:\n"); STREAM(matrix.q);
		L.n("R math.matrix of the decomposition:\n"); STREAM(matrix.r);
		Assert.EQUALS(getTestMatrix(), x);
		//updating Matrix A'=A+s*t <=> A'=Q*(R+u*v)
		for (int k=0; k<n; k++) {
			v[k]=s1[1][k]; //v=t
			u[k]=0;
			for (int i=0; i<n; i++) { //u=Q^t*s
				u[k] += matrix.qt[k][i]*s1[0][i];} 
		}
		matrix.update(u, v);
		TRP_AT(matrix.qt);
		x = CAT(matrix.qt, matrix.r); //
		L.n("Updated math.matrix:\n");	STREAM(au);
		L.n("Product of new Q and R matrices:\n"); STREAM(x);
		L.n("New Q math.matrix:\n"); STREAM(matrix.qt);
		L.n("New R math.matrix:\n"); STREAM(matrix.r);
		Assert.EQUALS(au, x);
		L.n("***********************************\n");
	}
		
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		L.n("Testing ").l(MatrixQR.class.getName()); 
		testDecomposeQR();
		testSolveQR();
		testUpdateQR();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) { //throws java.io.IOException {
		testIt(args);
	}

}
