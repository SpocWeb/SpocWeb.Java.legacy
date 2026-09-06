/*
 * File Name: FittingFloat.java
 * Created on: 20.02.2004
 *
 */
package math.fit;

import math.matrix.MatrixSVD;
import streamIO.Log;
import streamIO.real.random.RandomGauss;
import function.byref.ByRefFloat;
import function.vector.IFloatVectorFunction;

/**
 * Collects static methods for fitting functions to data by singular value decomposition and
 * by normal equations, and implements {@link IFloatVectorFunction} itself only to supply a
 * test basis function for its own self-tests.
 *
 * <p>Fitting is the more flexible cousin of inter-/extrapolation, because it allows for
 * noise and lets the caller analyze and possibly reduce the number of free variables.
 *
 * @author mheuer
 * @version	1.0
 * @see IFloatVectorFunction the basis-function interface these fits are performed against
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:49:24Z
 * digest: 0257be445b51248eae5b246bfcf437ea1e2de0fd3ec79a972465aeec524819c2
 * stale: false
 * tags: [code/curve_fitting, code/singular_value_decomposition]
 * concepts: [Linear Least-Squares Fit (SVD)]
 * facets: {layer: utility, status: broken, complexity: high}
 * -->
 */
public class FittingFloat
implements IFloatVectorFunction {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(FittingFloat.class);

	/** exposes the (protected) Decomposition Results of {@link MatrixSVD} to {@link #svdfit} */
	private static final class SvdAccess extends MatrixSVD {
		SvdAccess(final double[][] a) { super(a); }
		/** @return the Diagonal Matrix with the Weights of the Mapping */
		double[] weights() { return w; }
		/** @return the orthonormal Base for the Range, in transposed Form */
		double[][] rangeBase() { return v; }
	}
	
	/**
	 * Solves a linear least-squares fit of {@code funcs} to {@code (x, y)} by singular value
	 * decomposition (Numerical Recipes 15.4) and returns the resulting chi-squared.
	 * {@code u}, {@code v} and {@code w} are filled with the decomposition of the design
	 * matrix and {@code a} with the solved coefficients; all four are pure out-parameters.
	 * @return the resulting chi-squared
	 */
	final static public double svdfit(final float[] x, final float[] y, final float[] sig, final int ndata, final float[] a, final int ma
	, final float[][] u, final float[][] v, final float[] w, final IFloatVectorFunction funcs) {
		int j,i;
		float tmp,sum;

		final float[] b=new float[1+ndata];
		final float[] afunc=new float[1+ma];
		for (i=1;i<=ndata;i++) {
			funcs.map(x[i],afunc);
			tmp=1/sig[i];
			for (j=1;j<=ma;j++) {
				u[i][j]=afunc[j]*tmp; }
			b[i]=y[i]*tmp;
		}
		final double[][] design=new double[ndata][ma]; //0-based copy for MatrixSVD
		final double[] rhs=new double[ndata];
		for (i=1;i<=ndata;i++) {
			for (j=1;j<=ma;j++) {
				design[i-1][j-1]=u[i][j]; }
			rhs[i-1]=b[i];
		}
		final SvdAccess svd=new SvdAccess(design); //decomposes design in place into U
		final float TOL = 1e-5f;
		svd.fixWeights(TOL); //discards singular Values below TOL*max
		final double[] w0=svd.weights();
		final double[][] v0=svd.rangeBase();
		for (i=1;i<=ndata;i++) {
			for (j=1;j<=ma;j++) {
				u[i][j]=(float) design[i-1][j-1]; }
		}
		for (j=1;j<=ma;j++) {
			w[j]=(float) w0[j-1];
			for (i=1;i<=ma;i++) {
				v[i][j]=(float) v0[i-1][j-1]; }
		}
		final double[] a0=new double[ma];
		svd.solve(rhs, a0); //Back-Substitution of the Solution into a
		for (j=1;j<=ma;j++) {
			a[j]=(float) a0[j-1]; }
		double chisq=0;
		for (i=1;i<=ndata;i++) {
			funcs.map(x[i],afunc);
			for (sum=0,j=1;j<=ma;j++) {
				sum += a[j]*afunc[j];} 
			tmp=(y[i]-sum)/sig[i];
			chisq += tmp*tmp;
		}
		return chisq; 
	}
	
	/**
	 * Fills {@code cvm} with the covariance matrix derived from an existing singular value
	 * decomposition (Numerical Recipes 15.4).
	 */
	final static public void GET_CO_VARIANCES(final float[][] v, final int ma, final float[] w, final float[][] cvm) {
		int k,j,i;
		float sum;

		final float[] wti=new float[1+ma];
		for (i=1;i<=ma;i++) {
			wti[i]=0;
			if (w[i] != 0) {
				wti[i]=1/(w[i]*w[i]); } 
		}
		for (i=1;i<=ma;i++) {
			for (j=1;j<=i;j++) {
				for (sum=0,k=1;k<=ma;k++) {
					sum += v[i][k]*v[j][k]*wti[k]; } 
				cvm[j][i]=cvm[i][j]=sum;
			}
		}
	}
	
	/**
	 * Performs a general linear least-squares fit of {@code funcs} to {@code (x, y)} by
	 * normal equations (Numerical Recipes 15.2/15.4), solving only the parameters flagged
	 * {@code true} in {@code ia} and leaving the rest of {@code a} unchanged.
	 * @throws RuntimeException when no parameter in {@code ia} is selected to be fitted
	 * @return the resulting chi-squared
	 */
	final static public double lfit(final float[] x, final float[] y, final float[] sig, final int ndat, final float[] a, final boolean[] ia, final int ma
	, final float[][] covar, final IFloatVectorFunction funcs) {
		int i,j,k,l,m,mfit=0;
		float ym,wt,sum,sig2i;

		final float[][] beta=new float[1+ma][1+1];
		final float[] afunc=new float[1+ma];
		for (j=1;j<=ma;j++) {
			if (ia[j]) {
				mfit++;} 
		} 
		if (mfit == 0) {
			throw new RuntimeException("no parameters selected to be fitted"); } 
		for (j=1;j<=mfit;j++) {
			for (k=1;k<=mfit;k++) covar[j][k]=0;
			beta[j][1]=0;
		}
		for (i=1;i<=ndat;i++) {
			funcs.map(x[i],afunc);
			ym=y[i];
			if (mfit < ma) {
				for (j=1;j<=ma;j++) {
					if (!ia[j]) {
						ym -= a[j]*afunc[j];} 
				}
			}
			sig2i=1/ByRefFloat.SQR(sig[i]);
			for (j=0,l=1;l<=ma;l++) {
				if (ia[l]) {
					wt=afunc[l]*sig2i;
					for (j++,k=0,m=1;m<=l;m++)
						if (ia[m]) covar[j][++k] += wt*afunc[m];
					beta[j][1] += ym*wt;
				}
			}
		}
		for (j=2;j<=mfit;j++)
			for (k=1;k<j;k++)
				covar[k][j]=covar[j][k];
		//gaussj(covar,mfit,beta,1); //Matrix is ill-conditioned!
		for (j=0,l=1;l<=ma;l++) {
			if (ia[l]) {
				a[l]=beta[++j][1]; } 
		}
		double chisq=0;
		for (i=1;i<=ndat;i++) {
			funcs.map(x[i],afunc);
			for (sum=0,j=1;j<=ma;j++) {
				sum += a[j]*afunc[j]; } 
			chisq += ByRefFloat.SQR((y[i]-sum)/sig[i]);
		}
		FitFloat.SORT_CO_VARIANCE(covar,ma,ia,mfit);
		return chisq; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// static Testing and main() Method
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static final void testSvdFit(){
		final int NPT = 100;
		final float SPREAD = 0.02f;
		final int NPOL = 5;

		final float[] a=new float[1+NPOL];
		final float[] w=new float[1+NPOL];
		final float[] x=new float[1+NPT];
		final float[] y=new float[1+NPT];
		final float[] sig=new float[1+NPT];
		final float[][] cvm=new float[1+NPOL][1+NPOL];
		final float[][] v  =new float[1+NPOL][1+NPOL];
		final float[][] u  =new float[1+NPT][1+NPOL];
		for (int i=1;i<=NPT;i++) {
			x[i]=0.02f*i;
			y[i]=1+x[i]*(2+x[i]*(3+x[i]*(4+x[i]*5)));
			y[i] *= (1+SPREAD*RandomGauss.NEXT_FLOAT());
			sig[i]=y[i]*SPREAD;
		}
		final IFloatVectorFunction fpoly = FitPolynom.SINGLETON; 
		double chisq = svdfit(x,y,sig,NPT,a,NPOL,u,v,w,fpoly);
		GET_CO_VARIANCES(v,NPOL,w,cvm);
		L.n("polynomial fit:");
		for (int i=1;i<=NPOL;i++) {
			L.n("a[").l(i).l("]=").l(a[i]).l(" +-").l(Math.sqrt(cvm[i][i])); } 
		L.n("Chi-squared =").l(chisq); 
		final IFloatVectorFunction fleg = FitLegendre.SINGLETON; 
		chisq = svdfit(x,y,sig,NPT,a,NPOL,u,v,w,fleg);
		GET_CO_VARIANCES(v,NPOL,w,cvm);
		L.n("FitLegendre polynomial fit:");
		for (int i=1;i<=NPOL;i++) {
			L.n("a[").l(i).l("]=").l(a[i]).l(" +-").l(Math.sqrt(cvm[i][i])); } 
		L.n("Chi-squared =").l(chisq);
	}
	
	private static final void testSvdVariance() {
		final int NP = 6;
		final int MA = 3;
		final float[][] vtemp=
			{  {1, 1, 1, 1, 1, 1, 
			}, {2, 2, 2, 2, 2, 2, 
			}, {3, 3, 3, 3, 3, 3, 
			}, {4, 4, 4, 4, 4, 4, 
			}, {5, 5, 5, 5, 5, 5, 
			}, {6, 6, 6, 6, 6, 6
			}  };
		final float[] w= {0, 0, 1, 2, 3, 4, 5};
		final float[][] tru=
			{  {1.25f, 2.5f, 3.75f,
			}, {2.5f , 5   , 7.5f,
			}, {3.75f, 7.5f, 11.25f
			}  }; 

		final float[][] cvm=new float[1+MA][1+MA];
		final float[][] v=vtemp;
		L.l("\nmatrix v\n");
		for (int i=1;i<=NP;i++) {
			L.n(v[i]); }
		L.n("vector w=").l(w);
		GET_CO_VARIANCES(v,MA,w,cvm);
		L.l("\ncovariance matrix from svdvar\n");
		for (int i=1;i<=MA;i++) {
			L.n(cvm[i]); }
		L.l("\nexpected covariance matrix\n");
		for (int i=1;i<=MA;i++) {
			L.n(tru[i-1]); }
	}
	
	private static final void testLinFit(){
		final int NPT = 100;
		final int NTERM = 5;
		final float SPREAD = 0.1f; 

		int i,j;
		double chisq;

		final boolean[] ia= new boolean[1+NTERM];
		final float[] a=new float[1+NTERM];
		final float[] x=new float[1+NTERM];
		final float[] y=new float[1+NTERM];
		final float[] sig=new float[1+NTERM];
		final float[][] covar=new float[1+NTERM][1+NTERM];

		final IFloatVectorFunction funcs = new FittingFloat(); 
		for (i=1;i<=NPT;i++) {
			x[i]=0.1f*i;
			funcs.map(x[i],a);
			y[i]=0;
			for (j=1;j<=NTERM;j++) {
				y[i] += j*a[j]; }
			y[i] += SPREAD*RandomGauss.NEXT_FLOAT();
			sig[i]=SPREAD;
		}
		for (i=1;i<=NTERM;i++) { ia[i]=true; }
		chisq = lfit(x,y,sig,NPT,a,ia,NTERM,covar,funcs);
		L.n("parameter").l("uncertainty");
		for (i=1;i<=NTERM;i++) {
			L.n("  a[").l(i).l("]=").l(a[i]).l(Math.sqrt(covar[i][i])); } 
		L.l("chi-squared = ").l(chisq);
		L.l("full covariance matrix\n");
		for (i=1;i<=NTERM;i++) {
			L.n(covar[i]);
		}
		L.l("\npress RETURN to continue...\n");
		L.readString();
		/* Now check results of restricting fit parameters */
		for (i=2;i<=NTERM;i+=2) {
			ia[i]=false; }
		chisq = lfit(x,y,sig,NPT,a,ia,NTERM,covar,funcs);
		L.n("parameter").l("	uncertainty");
		for (i=1;i<=NTERM;i++) {
			L.n("  a[").l(i).l("]=").l(a[i]).l(Math.sqrt(covar[i][i])); } 
		L.n("chi-squared = ").l(chisq);
		L.n("full covariance matrix\n");
		for (i=1;i<=NTERM;i++) {
			L.n(covar[i]);
		}
	}
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() throws Exception {
		testLinFit();
		testSvdFit();
		testSvdVariance();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Implementation of IFloatVectorFunction for Testing Purposes
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test basis function for {@code testLinFit()}: fills {@code yOut} with the constant
	 * term, {@code x} itself, then {@code sin(i*x)} for each further term.
	 * @see function.vector.IFloatVectorFunction#map(double, double[])
	 */
	public void map(final double x, final double[] yOut) {
		yOut[1]=1;
		yOut[2]=x;
		for (int i=3;i<yOut.length;i++) {
			yOut[i]=Math.sin(i*x); }
	}

	/**
	 * Test basis function for {@code testLinFit()}: fills {@code yOut} with the constant
	 * term, {@code x} (narrowed to {@code float}), then {@code sin(i*x)} for each further
	 * term.
	 * @see function.vector.IFloatVectorFunction#map(double, float[])
	 */
	public void map(final double x, final float[] yOut) {
		yOut[1]=1;
		yOut[2]=(float)x;
		for (int i=3;i<yOut.length;i++) {
			yOut[i]=(float)Math.sin(i*x); } 
	}
	
}
