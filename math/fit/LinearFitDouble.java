/*
 * File Name: LinearFitDouble.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import math.matrix.MatrixSVD;
import math.vector.VectorDouble;
import streamIO.Assert;
import streamIO.Log;
import streamIO.real.random.RandomGauss;
import function.byref.ByRefDouble;
import function.derive.ring.body.GammaP;
import function.vector.IFloatVectorField;
import function.vector.IFloatVectorFunction;

/**
 * Performs a general linear fit of data values to a set of scalar functions, or to a scalar
 * field, by singular value decomposition, since fitting usually creates ill-conditioned
 * linear systems and SVD is used to identify the relevant fitting parameters.
 *
 * <p>To fit a vector field: when individual coefficients per dimension are possible, fit
 * each component to the set of functions. Otherwise, when only a single set of coefficients
 * is needed, perform a linear fit on the flattened vector field by spreading out the
 * elements of each data vector.
 *
 * @author mheuer
 * @version	1.0
 * @see MatrixSVD the singular value decomposition this delegates the solve to
 * @see IFloatVectorFunction one form of fitting function this accepts
 * @see IFloatVectorField the other form of fitting function this accepts
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:50:41Z
 * digest: 6b8de1248e2e14bc46bc8c914ab471193be2702d44c768c0ac5dbed745912cf3
 * stale: false
 * tags: [code/linear_regression, code/singular_value_decomposition]
 * concepts: [Linear Fit (SVD)]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class LinearFitDouble {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(LinearFitDouble.class);
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	final double[] x;
	final double[][] xV;
	final double[] y;
	final double[] stdDev; 
	final int numData; 
	final double[] params; 
	final int numParams;
	final IFloatVectorFunction funcs;
	final IFloatVectorField funcsV;
	final double[] afunc;
	final MatrixSVD matrix;
	 
	/** linear least-squares fit by singular value decomposition (15.4)
	 * 
	 * @param x_ x Coordinates of the Data Points
	 * @param y_ y Coordinates of the Data Points
	 * @param stdDev_ individual Standard Deviations for each Point, can be null 
	 * @param numData_ 
	 * @param params_ Result Coefficients of the Fit
	 * @param numParams_
	 * @param funcs_ the Fitting Functions
	 */
	public LinearFitDouble(final double[] x_, final double[] y_, final double[] sig_
	, final IFloatVectorFunction funcs_, final double[] params_) { 
		this(x_, y_, sig_, x_.length, funcs_, params_, params_.length); }
	 
	/** linear least-squares fit by singular value decomposition (15.4)
	 * 
	 * @param x_ x Coordinates of the Data Points
	 * @param y_ y Coordinates of the Data Points
	 * @param stdDev_ individual Standard Deviations for each Point, can be null 
	 * @param numData_ 
	 * @param params_ Result Coefficients of the Fit
	 * @param numParams_
	 * @param funcs_ the Fitting Functions
	 */
	public LinearFitDouble(final double[] x_, final double[] y_, final double[] sig_, final int numData_
	, final IFloatVectorFunction funcs_, final double[] params_, final int numParams_) {
		this.xV = null;
		this.x=x_;
		this.y=y_;
		this.stdDev=sig_;
		this.numData=numData_;
		this.params=params_;
		this.numParams=numParams_;
		this.funcs=funcs_;
		this.funcsV=null;		
		final double[][] u = new double[numData][numParams]; //
		
		//Setting up the System of linear Equations
		final double[] b;
		if (stdDev == null) {
			b=y; //not modified by solving the Equation
		} else {
			b=VectorDouble.COPY(y, 0, numData);
		}
		afunc=new double[1+numParams];
		for (int i=numData; --i>=0; ) {
			funcs.map(x[i], u[i]); //calculate all Values in one Call
			if (stdDev == null) {
				continue; }
			final double tmp=1/stdDev[i];
			VectorDouble.MUL_AT(u[i], tmp, 0, numParams);
			b[i]*=tmp;
		}
		matrix = new MatrixSVD(u);
		matrix.fixWeights(ByRefDouble.DOUBLE_FULL_ACCURACY);
		matrix.solve(b, params); 
	}
	
	/** linear least-squares fit by singular value decomposition (15.4)
	 * 
	 * @param x_ x Coordinates of the Data Points
	 * @param y_ y Coordinates of the Data Points
	 * @param stdDev_ individual Standard Deviations for each Point, can be null 
	 * @param numData_ 
	 * @param params_ Result Coefficients of the Fit
	 * @param numParams_
	 * @param funcs_ the Fitting Functions
	 */
	public LinearFitDouble(final double[][] x_, final double[] y_, final double[] sig_
	, final IFloatVectorField funcs_, final double[] params_) { 
		this(x_, y_, sig_, x_.length, funcs_, params_, params_.length); }
	 
	/** linear least-squares fit by singular value decomposition (15.4)
	 * 
	 * @param x_ x Coordinates of the Data Points
	 * @param y_ y Coordinates of the Data Points
	 * @param stdDev_ individual Standard Deviations for each Point, can be null 
	 * @param numData_ 
	 * @param params_ Result Coefficients of the Fit
	 * @param numParams_
	 * @param funcs_ the Fitting Functions
	 */
	public LinearFitDouble(final double[][] x_, final double[] y_, final double[] sig_, final int numData_
	, final IFloatVectorField funcs_, final double[] params_, final int numParams_) {
		this.xV=x_;
		this.x = null;
		this.y=y_;
		this.stdDev=sig_;
		this.numData=numData_;
		this.params=params_;
		this.numParams=numParams_;
		this.funcsV=funcs_;
		this.funcs=null;
		final double[][] u = new double[numData][numParams]; //
		
		//Setting up the System of linear Equations
		final double[] b;
		if (stdDev == null) {
			b=y; //not modified by solving the Equation
		} else {
			b=VectorDouble.COPY(y, 0, numData);
		}
		afunc=new double[1+numParams];
		for (int i=numData; --i>=0; ) {
			funcsV.map(xV[i],u[i]);
			if (stdDev == null) {
				continue; }
			final double tmp=1/stdDev[i];
			VectorDouble.MUL_AT(u[i], tmp, 0, numParams);
			b[i]*=tmp;
		}
		matrix = new MatrixSVD(u);
		matrix.fixWeights(ByRefDouble.DOUBLE_FULL_ACCURACY);
		matrix.solve(b, params); 
	}
	
	/**
	 * Evaluates the fitted functions at every data point with the solved {@code params} and
	 * sums the squared, standard-deviation-weighted residual against {@code y}.
	 * @return the Chi² of this Fit	 */
	public double getChiSqr() {
		double chiSqr=0;
		for (int i=numData; --i>=0; ) {
			if (funcs != null) {
				funcs.map(x[i],afunc);
			} else {
				funcsV.map(xV[i],afunc);
			}
			double sum=0;  
			for (int j=numParams; --j >= 0; ) {
				sum += params[j]*afunc[j]; } 
			double tmp=y[i]-sum;
			if (stdDev != null) { 
				tmp/=stdDev[i]; } 
			chiSqr += tmp*tmp;
		}
		return chiSqr; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// static Testing and main() Method
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static final void testSvdFit(){
		final int NPOL = 5;
		final int NPT = 100;
		final double SPREAD = 0.02;
		
		final double[] a=new double[NPOL];
		final double[] x=new double[NPT];
		final double[] y=new double[x.length];
		final double[] sig=new double[x.length];
		final double[] p = {1,2,3,4,5};
		final double[] legendre = {3, 4.4, 4.857142857142818, 1.6, 1.1428571428571392};
		double[][] cvm;
		for (int i=x.length; --i>=0; ) {
			final double x_ = 0.02*i; 
			x[i]=x_;
			y[i]=p[0]+x_*(p[1]+x_*(p[2]+x_*(p[3]+x_*p[4])));
			y[i] *= (1+SPREAD*RandomGauss.NEXT_DOUBLE());
			sig[i]=y[i]*SPREAD+ByRefDouble.DOUBLE_ACCURACY; //don't let the Variance become 0!
		}
		final IFloatVectorFunction fpoly = FitPolynom.SINGLETON;
		double[] sigArg = sig; //null; //
		final LinearFitDouble polyFit = new LinearFitDouble(x,y,sigArg,fpoly,a);  
		double chisq = polyFit.getChiSqr();
		cvm = polyFit.matrix.getCoVarianceMatrix();
		L.n();
		L.n("Polynomial fit:");
		for (int i=0; i<a.length; i++) {
			final double da = Math.sqrt(cvm[i][i]);
			L.n("a[").l(i).l("]=").l(a[i]).l(" +-").l(da); 
			Assert.EQUALS(p[i], a[i], da);
		} 
		L.n("Chi-squared =").l(chisq); 
		L.n("Probability =").l(GammaP.PROBABILITY_CHI_SQR(x.length-a.length, chisq)); 
		
		final IFloatVectorFunction fleg = FitLegendre.SINGLETON; 
		final LinearFitDouble legendreFit = new LinearFitDouble(x,y,sigArg,fleg,a);
		chisq = legendreFit.getChiSqr();
		cvm = polyFit.matrix.getCoVarianceMatrix();
		L.n();
		L.n("FitLegendre polynomial fit:");
		for (int i=0; i<a.length; i++) {
			final double da = Math.sqrt(cvm[i][i]);
			L.n("a[").l(i).l("]=").l(a[i]).l(" +-").l(da).l("	expected:").l(legendre[i]); 
			//Assert.EQUALS(legendre[i], a[i], da); //very susceptible to Errors!
		} 
		L.n("Chi-squared =").l(chisq);
		L.n("Probability =").l(GammaP.PROBABILITY_CHI_SQR(x.length-a.length, chisq)); 
	}
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() throws Exception {
		testSvdFit();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}
