/*
 * File Name: MatrixSVD.java
 * Created on: 03.11.2003
 *
 */
package math.matrix;

import streamIO.Assert;
import streamIO.Log;
import function.byref.ByRefDouble;

/**
 * Title: MatrixSVD<p>
 * Description:
 * Purpose:
 * 
 * Performs Singular Value Decomposition of a Matrix: 
 * A=U*w*V^t
 * Also holds the Members of SVD, so it can be processed comfortably. 
 * Especially analyzing the Diagonal Matrix W yields direct Information 
 * on the Mapping Properties like Null Space and Condition. 
 * Additionally the Pseudo Inverse can be constructed 
 * by inverting the Diagonal and inverting/transposing the Trafo Matrices. 
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
 */
public class MatrixSVD {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static final Log L = new Log(MatrixSVD.class);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * a Column orthonormal Matrix spanning the Value of the Mapping.  
	 * 
	 */
	protected final double[][] u;
	
	/**
	 * the Diagonal Matrix with the Weights of the Mapping, 
	 * defines the Characteristics of the Mapping like 
	 * Nullity = # w[i]==0
	 * Rank    = # w[i]!=0
	 * Condition = # max(w[i])/min(w[i])
	 * The Vectors of u[i] and v[i] corresponding to w[i]==0
	 * span the NullSpace i.e. the Range for which A*x=0
	 */
	protected final double[] w; 
	
	/**
	 * othonormal Base for the Mapping (Definition-)Range 
	 * stored in transposed Form
	 */
	protected final double[][] v;
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** private Copy Constructor for testing	 */
	private MatrixSVD(final double[][] u_, final double[] w_, final double[][] v_) { 
		this.u = u_;
		this.v = v_;
		this.w = w_;
	}
	
	/**
	 * Constructor performs an immediate in-Place Decomposition 
	 * @param a the Matrix to decompose, in place substituted by and used as u
	 */
	public MatrixSVD(final double[][] a) { 
		final int n = a[a.length>>1].length; //choose the 'middle' Row
		v=new double[n][n]; //and assert a rectangular Matrix...
		w=new double[n];
		//tmp = new double[n];
		u = a; //MatrixDouble.copy(a);
		decomposeSingularValues(); //decompose already in the Constructor
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Adjusts the Weights by setting those to 0, 
	 * which are relatively small compared to the largest. 
	 * This reduces the # of Dependencies and also sensitive Cancellations 
	 */	
	public void fixWeights(final double threshold) {
		int j= w.length; 
		double wMax=w[--j]; //Values w are non-negative! 
		for (; --j >= 0; ) { 
			if (wMax < w[j])
				wMax = w[j]; 
		} 
		final double thresh=threshold*wMax;
		j = w.length; 
		for (; --j >= 0; )
			if (w[j] < thresh) 
				w[j] = 0;  
	}
	
	/** calculates the (Co-)Variances from Singular Value Decomposition (15.4)		*/
	final public double[][] getCoVarianceMatrix() {
		return getCoVarianceMatrix(w.length); }
	
	/** calculates the (Co-)Variances from Singular Value Decomposition (15.4)		*/
	final public double[][] getCoVarianceMatrix(final int maxDim) {
		final double[][] cvm = new double[maxDim][maxDim]; 

		final double[] wti = new double[maxDim];
		for (int i=0;i<maxDim;i++) {
			wti[i]=0;
			if (w[i] != 0) 
				wti[i]=1/(w[i]*w[i]);  
		}
		for (int i=0; i<maxDim; i++) {
			for (int j=0; j<=i; j++) {
				double sum = 0;
				for (int k=0; k<maxDim; k++) 
					sum += v[i][k]*v[j][k]*wti[k];  
				cvm[j][i]=cvm[i][j]=sum;
			}
		}
		return cvm;
	}
	
	/**	Singular Value Decomposition of math.matrix A=U*diag(w)*V (2.6)
	 * 
	 * @param a the Matrix to decompose, in place substituted by u, 
	 * a Column orthonormal Matrix spanning the Value of the Mapping.  
	 * @param w the Diagonal Matrix with the Weights of the Mapping, 
	 * defines the Characteristics of the Mapping like 
	 * Nullity = # w[i]==0
	 * Rank    = # w[i]!=0
	 * Condition = # max(w[i])/min(w[i])
	 * The Vectors of u[i] and v[i] corresponding to w[i]==0
	 * span the NullSpace i.e. the Range for which A*x=0
	 * @param v othonormal Base for the Range 
	 */
	protected final void decomposeSingularValues() {
		//final int n = ; //assume rectangular Matrix, not a frayed one...
		final double[] rv1=new double[w.length];
		double aNorm,g,scale; g=scale=aNorm=0;
		for(int i=-1; ++i<w.length; ) { //Householder Reduction to bidiagonal Form
			int ll=i+1;
			rv1[i]=scale*g;
			g=scale=0;
			if (i < u.length) {
				for (int k=i; k<u.length; k++) {
					scale += Math.abs(u[k][i]); } 
				if (scale != 0) {
					double s1 = 0;
					for (int k=i; k<u.length; k++) {
						u[k][i] /= scale;
						s1 += u[k][i]*u[k][i];
					}
					final double aii=u[i][i];
					g = -ByRefDouble.ASSIGN_SIGN(Math.sqrt(s1),aii);
					final double h=aii*g-s1;
					u[i][i]=aii-g;
					addProd(i, ll, h);
					for (int k=i; k<u.length; k++) {
						u[k][i] *= scale;} 
				}
			}
			w[i] = scale*g;
			g=scale=0;
			if ((i < u.length) && (i != w.length-1)) {
				for (int k=ll; k<w.length; k++) {
					scale += Math.abs(u[i][k]); } 
				if (scale != 0) {
					double s1 = 0;
					for (int k=ll; k<w.length; k++) {
						u[i][k] /= scale;
						s1 += u[i][k]*u[i][k];
					}
					final double ail=u[i][ll];
					g = -ByRefDouble.ASSIGN_SIGN(Math.sqrt(s1),ail);
					final double h=ail*g-s1;
					u[i][ll]=ail-g;
					for (int k=ll; k<w.length; k++) {
						rv1[k]=u[i][k]/h;} 
					addProd(rv1, i, ll);
					for (int k=ll; k<w.length; k++) {
						u[i][k] *= scale;} 
				}
			}
			aNorm=Math.max(aNorm,(Math.abs(w[i])+Math.abs(rv1[i])));
		}
		//Accumulation of right-hand Transformations
		for (int i=w.length; --i>=0; ) {
			final int ll=i+1;
			if (i < w.length-1) {
				if (g != 0) {
					for (int j=ll; j<w.length; j++) {
						v[j][i]=(u[i][j]/u[i][ll])/g; //double Division to avoid Underflow
					}
					addProd(i, ll);
				}
				for (int j=ll; j<w.length; j++) {
					v[i][j]=v[j][i]=0; } 
			}
			v[i][i]=1;
			g=rv1[i];
		}
		//Accumulation of left-hand Transformations
		for (int i=Math.min(u.length,w.length); --i>=0; ) {
			final int ll=i+1;
			g=w[i];
			for (int j=ll; j<w.length; j++) {
				u[i][j]=0;} 
			if (g != 0) {
				g=1/g;
				addProd(g, i, ll);
				for (int j=i; j<u.length; j++) {
					u[j][i] *= g;} 
			} else {
				for (int j=i; j<u.length; j++) {
					u[j][i]=0;} 
			} 
			++u[i][i];
		}
		//Diagonalization of bidiagonal Form
		for (int k=w.length; --k>=0;) { //for all Columns
			for (int its=1;;) {
				if (++its >= 30) {
					throw new RuntimeException("no convergence in 30 svdcmp iterations"); } 
				boolean flag=true;
				int nMin = 0;
				int ll;
				for (ll=k; ll>=0; ll--) { //Test for splitting 
					nMin=ll-1; //rv[0] == 0 always!
					if ((double)(Math.abs(rv1[ll])+aNorm) == aNorm) {
						flag=false;
						break; }
					if ((double)(Math.abs(w[nMin])+aNorm) == aNorm) {
						break; } 
				}
				if (flag) { //cancellation of rv[ll] when ll > 0
					rotate(rv1, aNorm, k, nMin, ll); }
				final double z1=w[k];
				if (ll == k) { //Convergence
					if (z1 < 0) { //Singular Value made non-negative
						w[k] = -z1;
						MatrixDouble.NEG_COL_AT(v, k, 0, w.length);
					}
					break; //jumps out of the its Loop
				}
				double f;
				double x=w[ll]; //Shift from Bottom 2*2 Minor
				final double y1=w[k-1];
				g=rv1[k-1];
				final double h1=rv1[k];
				final double f1=((y1-z1)*(y1+z1)+(g-h1)*(g+h1))/(2*h1*y1);
				g=ByRefDouble.NORM(f1,1);
				f=((x-z1)*(x+z1)+h1*((y1/(f1+ByRefDouble.ASSIGN_SIGN(g,f1)))-h1))/x;
				//Next QR Trafo
				double c, s; c=s=1;
				for (int j=ll; j<k; j++) {
					int i=j+1;
					g=rv1[i];
					double y=w[i];
					double h=s*g;
					g=c*g;
					final double z2=ByRefDouble.NORM(f,h);
					rv1[j]=z2;
					c=f/z2;
					s=h/z2;
					f = x*c+g*s;
					g = g*c-x*s;
					h=y*s;
					y *= c;
					for (int jj=0; jj<w.length; jj++) {
						x=v[jj][j];
						final double z=v[jj][i];
						v[jj][j]=x*c+z*s;
						v[jj][i]=z*c-x*s;
					}
					double z=ByRefDouble.NORM(f,h);
					w[j]=z;
					if (z != 0) { //otherwise the Rotation can be arbitrary if z == 0
						z=1/z;
						c=f*z;
						s=h*z;
					} 
					f=c*g+s*y;
					x=c*y-s*g;
					for (int jj=0; jj<u.length; jj++) {
						y=u[jj][j];
						z=u[jj][i];
						u[jj][j]=y*c+z*s;
						u[jj][i]=z*c-y*s;
					}
				}
				rv1[ll]=0;
				rv1[k]=f;
				w[k]=x;
			}
		}
	}

	/** 
	 * 
	 * @param rv1
	 * @param anorm
	 * @param k
	 * @param nMin
	 * @param ll
	 */
	private void rotate(final double[] rv1, final double anorm, final int k, final int nMin, final int ll) {
		double g;
		double c=0;
		double s=1;
		for (int i=ll; i<=k; i++) {
			final double f=s*rv1[i];
			rv1[i]=c*rv1[i];
			if ((double)(Math.abs(f)+anorm) == anorm) break;
			g=w[i];
			double h=ByRefDouble.NORM(f,g);
			w[i]=h;
			h=1/h;
			c=g*h;
			s = -f*h;
			for (int j=0; j<u.length; j++) {
				final double y=u[j][nMin];
				final double z=u[j][i];
				u[j][nMin]=y*c+z*s;
				u[j][i]=z*c-y*s;
			}
		}
	}

	private void addProd(final int i, final int ll, final double h) {
		for (int j=ll; j<w.length; j++) {
			double s=0;
			for (int k=i; k<u.length; k++) {
				s += u[k][i]*u[k][j];} 
			double tmp=s/h;
			for (int k=i; k<u.length; k++) {
				u[k][j] += tmp*u[k][i];} 
		}
	}

	private void addProd(final double g, final int i, final int ll) {
		for (int j=ll; j<w.length; j++) {
			double s=0;
			for (int k=ll; k<u.length; k++) {
				s += u[k][i]*u[k][j];} 
			final double f=(s/u[i][i])*g;
			for (int k=i; k<u.length; k++) {
				u[k][j] += f*u[k][i];} 
		}
	}

	private final void addProd(final double[] rv1, final int i, final int ll) {
		for (int j=ll; j<u.length; j++) {
			double s=0;
			for (int k=ll; k<w.length; k++) {
				s += u[j][k]*u[i][k];} 
			for (int k=ll; k<w.length; k++) {
				u[j][k] += s*rv1[k];} 
		}
	}

	private final void addProd(final int i, final int ll) {
		for (int j=ll; j<w.length; j++) {
			double s=0;
			for (int k=ll; k<w.length; k++) {
				s += u[i][k]*v[k][j];} 
			for (int k=ll; k<w.length; k++) {
				v[k][j] += s*v[k][i];} 
		}
	}
	
	/**	Singular Value Back-Substitution (2.6)
	 * Solves the Equation A*x = U*diag(w)*V*x = b
	 * @param b right Hand Side of the Equation
	 * @param x Solution
	 */
	final public double[] solve(final double[] b) {
		final double[] ret = new double[u.length];
		solve(b, ret);
		return ret;
	}
	
	/**	Singular Value Back-Substitution (2.6)
	 * Solves the Equation A*x = U*diag(w)*V*x = b
	 * @param b right Hand Side of the Equation
	 * @param x Solution
	 */
	final public void solve(final double[] b, final double[] ret) {
		solve(b, ret, new double[w.length]);
	}
	
	/** temporary Space for Backsubstition, makes it not thread-safe!	 */
	//final double[] tmp; //=new double[n];

	/**	Singular Value Back-Substitution (2.6)
	 * Solves the Equation A*x = U*diag(w)*V*x = b
	 * @param b right Hand Side of the Equation
	 * @param x Solution
	 * Since x has lower Dimensions than b, b is not reused to store x typically
	 * @param tmp temp. Array of same Size as x. handed over for Optimization (consider thread-safety)
	 */
	final public void solve(final double[] b, final double[] x, final double[] tmp) {
		for (int j=0; j<w.length; j++) { //calc U^t*b
			double sum=0;
			if (w[j] != 0) { //only for nonzero w[j] to avoid Null-Space...
				for (int i=0; i<u.length; i++) {
					sum += u[i][j]*b[i]; } 
				sum /= w[j]; //divide by w[j]
			}
			tmp[j]=sum;
		}
		for (int j=0; j<w.length; j++) { //Multiply by V to get Solution.
			double sum=0;
			for (int jj=0; jj<w.length; jj++) {
				sum += v[j][jj]*tmp[jj]; } 
			x[j]=sum;
		}
	}

	/**	
	 * maps x by this (decomposed) Matrix: 
	 * @param x (Column-)Vector to multiply from the right 
	 * @return A*x = U*w*V^t*x
	 */
	final public double[] map(final double[] x) {
		final double[] prod = new double[u.length];
		for (int i=u.length; --i>=0; ) { 
			final double[] ui = u[i];
			double sumi = 0;
			for (int j=v.length; --j>=0; ) {
				double sumj = 0;
				for (int k=v.length; --k>=0; ) {
					sumj += v[k][j]*x[k];
				}
				sumi += ui[j]*w[j]*sumj;
			}
			prod[i] = sumi;
		}
		return prod;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	
	private static final double[][] test1 = {
		{1,2,3
		},{2,3,4
		},{3,4,5
		},{4,5,6
		},{5,6,7
		}
	};
	private static final double[][] test2 = {
		{1,2,3,4,5
		},{2,2,3,4,5
		},{3,3,3,4,5
		},{4,4,4,4,5
		},{5,5,5,5,5
		}
	};

	private static final double[][] test3 = {
		{3.0, 5.3, 5.6, 3.5, 6.8, 5.7
		},{0.4, 8.2, 6.7, 1.9, 2.2, 5.3
		},{7.8, 8.3, 7.7, 3.3, 1.9, 4.8
		},{5.5, 8.8, 3.0, 1.0, 5.1, 6.4
		},{5.1, 5.1, 3.6, 5.8, 5.7, 4.9
		},{3.5, 2.7, 5.7, 8.2, 9.6, 2.9
		}
	};

	private static final void testSVDecomposition() {
		testSVDecomposition(test1);
		testSVDecomposition(test2);
		testSVDecomposition(test3);
		MatrixDouble.RANDOMIZE_AT(test3);
		testSVDecomposition(test3);
	}
	
	private static final void testSVDecomposition(final double[][] a) {

		/* read input matrices */
		//final int m = ; 
		final int n = a[0].length;
		MatrixSVD matrix = new MatrixSVD(MatrixDouble.COPY(a));
		/* perform decomposition */
		//matrix.decomposeSingularValues();

		/* write results */
		L.n("Decomposition matrices:");
		L.n("Matrix u\n");
		for (int k=0; k<a.length; k++) {
			for (int m=1; m<n; m++) {
				L.l("\t"+matrix.u[k][m]);
			}
			L.n();
		}
		L.n("Diagonal of math.matrix w");
		for (int k=0; k<n; k++) {
			L.l("\t"+matrix.w[k]);
		}
		L.n("Matrix v-transpose\n");
		for (int k=0; k<n; k++) {
			for (int m=0; m<n; m++) {
				L.l("\t"+matrix.v[m][k]);
			}
			L.n();
		}
		L.n("Check product against original math.matrix:");
		L.n("Original math.matrix:\n");
		for (int k=0; k<a.length; k++) {
			for (int ll=0;ll<n;ll++)
				L.l("\t"+a[k][ll]);
			L.n();
		}
		L.n("Product u*w*(v-transpose):\n");
		final double[][] prod = new double[a.length][n];
		for (int i=0; i<a.length; i++) {
			final double[] ui = matrix.u[i];
			for (int j=0; j<n; j++) {
				final double[] vj = matrix.v[j];
				double sum = 0;
				for (int k=0; k<n; k++) {
					sum += ui[k]*matrix.w[k]*vj[k];
				}
				L.l("\t"+sum); 
				prod[i][j] = sum;
			}
			L.n();
		}
		Assert.EQUALS(prod, a);
		L.n("***********************************");
		L.n("press RETURN for next problem");
	}

	/** tests the Singular Value Backsubstition	 */
	private static final void testSVBacksubstition() {
		final double[][] a = MatrixDouble.getTestMatrix();
		final double[][] b = MatrixDouble.getTestVectors();		
		testSVBacksubstition(a, b);
		MatrixDouble.RANDOMIZE_AT(a); 
		MatrixDouble.RANDOMIZE_AT(b); 
		testSVBacksubstition(a, b);
	}

	/** tests the Singular Value Backsubstition	 */
	private static final void testSVBacksubstition(final double[][] a, final double[][] b) {
		double wmax,wmin;
		final double[] c = new double[a.length];
		
		MatrixSVD matrix = new MatrixSVD(MatrixDouble.COPY(a));
		L.n("decomposing math.matrix a"); 
		//matrix.decomposeSingularValues();
		L.n("finding maximum singular value:"); 
		wmax=0;
		for (int k=0; k<a.length; k++) {
			if (matrix.w[k] > wmax) {
				wmax=matrix.w[k]; } 
		}
		wmin=wmax*(1.0e-6); L.n("defining 'small' as"+wmin); 
		L.n("zeroing out the 'small' singular values:");  
		for (int k=0; k<a.length; k++)
			if (matrix.w[k] < wmin) {
				L.n("zeroed out w["+k+"]="+matrix.w[k]);  
				matrix.w[k]=0; } 
		// backsubstitute for each right-hand side vector 
		for (int ll=0; ll<b.length; ll++) {
			L.n("Vector number: "+ll);
			for (int k=0; k<a.length; k++) {
				c[k]=b[ll][k]; } 
			final double[] x = matrix.solve(c);
			L.n(" solution vector is:");
			for (int k=0; k<a.length; k++) {
				L.l("\t"+x[k]); } 
			L.n("original right-hand side vector:\n");
			for (int k=0; k<a.length; k++) {
				L.l("\t"+c[k]);} 
			final double[] prod = new double[a.length];
			for (int k=0; k<a.length; k++) {
				double sum = 0;
				for (int j=0; j<a.length; j++) {
					sum += a[k][j]*x[j];
				}
				prod[k] = sum;
			}
			L.n("(orig.math.matrix)*(sol'n vector):\n");
			for (int k=0; k<a.length; k++) {
				L.l("\t"+c[k]); } 
			L.n();
			Assert.EQUALS(c, prod);
			double[] prod2 = matrix.map(x);
			for (int k=0; k<a.length; k++) {
				L.l("\t"+prod2[k]); } 
			Assert.EQUALS(c, prod2);
		}
		L.n("***********************************");
		L.n("press RETURN for next problem");
	}

	/** tests calculating the CoVariance Matrix 	*/
	private static final void testSvdVariance() {
		final double[][] v=
			{  {1, 1, 1, 1, 1, 1, 
			}, {2, 2, 2, 2, 2, 2, 
			}, {3, 3, 3, 3, 3, 3, 
			}, {4, 4, 4, 4, 4, 4, 
			}, {5, 5, 5, 5, 5, 5, 
			}, {6, 6, 6, 6, 6, 6
			}  };
		final double[] w= {0, 1, 2, 3, 4, 5};
		final double[][] tru=
			{  {1.25f, 2.5f, 3.75f,
			}, {2.5f , 5   , 7.5f,
			}, {3.75f, 7.5f, 11.25f
			}  }; 

		L.n("matrix v");
		for (int i=0;i<v.length;i++) {
			L.n(v[i]); }
		L.n("vector w=").l(w);
		final MatrixSVD svd = new MatrixSVD(null, w, v);
		final double[][] cvm = svd.getCoVarianceMatrix(3);
		L.n("CoVariance Matrix ");
		for (int i=0;i<cvm.length;i++) {
			L.n(cvm[i]); }
		L.n("Expected CoVariance Matrix");
		for (int i=0;i<tru.length;i++) {
			L.n(tru[i]); }
		Assert.EQUALS(tru, cvm);
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void testIt() { //throws Exception {
		L.n("Testing ").l(MatrixSVD.class.getName()); 
		testSvdVariance();
		testSVDecomposition();
		testSVBacksubstition();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) { //throws Exception {
		if (args.length <= 0) 
			testIt(); 
	}

}
