/*
 * File Name: FitFloat.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import math.matrix.MatrixFloat;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.Log;
import streamIO.real.random.RandomGauss;
import function.byref.ByRefFloat;
import function.derive.ring.body.GammaP;

/**
 * Performs general (non-linear) fitting of an {@link IFloatFitFunction} to a data set by
 * Levenberg-Marquardt minimization of chi-squared.
 *
 * <p>Since fitting is essentially minimization, nonlinear fitting cannot be performed in a
 * single step, but requires iteration. Since the Hessian matrix is based on chi-squared, it
 * is well-known and can be used for inverse quadratic minimization of
 * Chi²=C0-d*a+a*D*a/2.
 *
 * @author mheuer
 * @version	1.0
 * @see IFloatFitFunction the function type this fits
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:48:27Z
 * digest: 5b385606dca9bf6412d19c27e2ab14d05e0eab0443c4bfa26808e525ab322d37
 * stale: false
 * tags: [code/curve_fitting]
 * concepts: [Nonlinear Curve Fit (Levenberg-Marquardt style)]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class FitFloat {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(FitFloat.class, -0);
	
	/////////////////////////////////////////////////////////////////////////////////////
	// static Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Counts how many entries of {@code bool_} are switched on to fit.
	 * @return the actual Number of Parameters switched on to fit	 */
	final static public int GET_NUM_TRUE(final boolean[] bool_) {
		return GET_NUM_TRUE(bool_, bool_.length); }

	/**
	 * Counts how many of the first {@code numParams_} entries of {@code bool_} are switched
	 * on to fit.
	 * @return the actual Number of Parameters switched to true.
	 * when the bool_ is null, returns numParams_. 	 */
	final static public int GET_NUM_TRUE(final boolean[] bool_, final int numParams_) {
		if (bool_ == null) {
			return numParams_; }
		int mfit=0; 
		for (int j=0; j<numParams_; j++) {
			if (bool_[j]) { 
				++mfit; }
		} 
		return mfit;
	}
	
	/** rearrange the covariance matrix accoding to the Flags 
	 * that switch fitting on / off 
	 * @param covar the Matrix to rearrange
	 * @param numParams the Number of Parameters
	 * @param vary flags indicating what Parameters to vary
	 * @param numFit the total Number of Parameters
	 */
	final static public void SORT_CO_VARIANCE(final float[][] covar, final int numParams, 
	final boolean[] vary, final int numFit) {
		//All non-varied Parameters have Variance 0
		for (int i=numFit; i<numParams; i++) {
			MatrixFloat.FILL_ROW(covar, i, 0, 0, i+1); //including the Diagonal
			MatrixFloat.FILL_COL(covar, i, 0, 0, i);
		}
		int k=numFit;
		for (int j=numParams; --j>=0;) {
			if ((vary == null) || vary[j]) { //
				--k;
				MatrixFloat.SWAP_COLS_ROWS_AT(covar, k,j);
				//MatrixFloat.swapColsAt(covar, j, k); //for (int i=0; i<numParams; i++) { final float tmp = covar[i][k]; covar[i][k] = covar[i][j]; covar[i][j] = tmp; } 
				//MatrixFloat.swapRowsAt(covar, j, k); // for (int i=0; i<numParams; i++) { final float tmp = covar[k][i]; covar[k][i] = covar[j][i]; covar[j][i] = tmp; } 
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Convergence Factor,	 */ 
	float lamda = 0.001f; 
	/** Cache for the previous Value of chi�	 */
	float oldChiSqr;
	/** Parameter Vector testing the new Configuration, 
	 * shared with the external Parameter Array handed over in the Constructor
	 */
	final float[] params;
	/** Cache for the previous Parameter Configuration	 */
	final float[] oldParams;
	/** the used Size of the Parameter Vector	 */ 
	final int numParams; 
	/** dParams = params - oldParams	 */
	final float[] dParams;
	final float[] oneDa;
	final float[] beta;
	final float[][] alpha;
	final float[][] coVar; 
	/** Pivot Index for inverting the coVar Matrix	 */
	final int[] rows;
	/** LU-Decomposition of the coVar Matrix	 */
	final float[][] lu; 
	/** Parameter for querying the Derivatives of the Fitting Functions	 */
	final float[] dyda;
	/** Array of Switches to activate fitting on the individual Parameters	 */
	final boolean[] vary;
	/** Reference to the parameterized Fitting Function	 */ 
	final IFloatFitFunction funcs;
	
	/** Initializing Constructor	
	 * 
	 * @param funcs_ the Function to fit, always called with a_
	 * @param params_ Parameter Vector, changed implicitly and used to call funcs
	 */
	public FitFloat(final IFloatFitFunction funcs_, final float[] params_) {
		this(funcs_, params_, params_.length, null); }
	
	/** Initializing Constructor	
	 * 
	 * @param funcs_ the Function to fit, always called with a_
	 * @param params_ Parameter Vector, changed implicitly and used to call funcs
	 * @param numParams_ the Number of Parameters
	 */
	public FitFloat(final IFloatFitFunction funcs_, final float[] params_, final int numParams_) {
		this(funcs_, params_, numParams_, null); }
	
	/** Initializing Constructor	
	 * 
	 * @param funcs_ the Function to fit, always called with a_
	 * @param params_ Parameter Vector, changed implicitly and used to call funcs
	 * @param vary_ switch the individual Parameters on or off; can be null, if not needed
	 */
	public FitFloat(final IFloatFitFunction funcs_, final float[] params_, final boolean[] vary_) {
		this(funcs_, params_, params_.length, vary_); }
	
	/** Initializing Constructor	
	 * 
	 * @param funcs_ the Function to fit, always called with a_
	 * @param params_ Parameter Vector, changed implicitly and used to call funcs
	 * @param vary_ switch the individual Parameters on or off; can be null, if not needed 
	 * @param numParams_ the Number of Parameters
	 */
	public FitFloat(final IFloatFitFunction funcs_, final float[] params_, final int numParams_, final boolean[] vary_) {
		this.numParams = numParams_; 
		this.params = params_; //always using this Vector to query funcs_!!!
		this.vary = vary_;
		this.funcs= funcs_;
		this.oneDa= new float[numParams];
		this.dyda = new float[numParams]; //
		this.oldParams    = new float[numParams];
		this.beta = new float[numParams];
		this.alpha= new float[numParams][numParams];
		this.lu   = new float[numParams][numParams];
		this.rows = new int  [numParams];
		this.coVar= new float[numParams][numParams];
		this.dParams   = new float[numParams];
		oldChiSqr=Float.POSITIVE_INFINITY;  
		VectorFloat.COPY(params, 0, numParams_, oldParams);
	}

	/** prepares the linear Substitute Problem and solve it 	 */
	private void prepareAndSolve(final int numFit) {
		MatrixFloat.COPY_AT(lu, alpha);
		final float a_1 = 1+lamda; 
		for (int j=0; j<numFit; j++) {
			oneDa[j]=beta[j];
			lu[j][j]*=a_1; //TODO: research: skipping this Line actually improves Convergence!!!
		}
		L.n("lu:",-2).l(lu,-2);
		L.n("oneDa:",-2).l(oneDa,-2);
		MatrixFloat.ONE_AT(coVar);		// //numParams
		MatrixFloat.SPLIT_LU_AT(lu, rows, numFit);
		MatrixFloat.SOLVE_LU_AT(lu, rows, numFit, coVar);
		MatrixFloat.SOLVE_LU_AT(lu, rows, numFit, oneDa);
	}

	/**
	 * Solves the linearized fitting matrix for the currently varied parameters and rearranges
	 * the result back to full parameter order.
	 * @return the CoVariance Matrix used to determine the Variances	 */
	public float[][] getCoVariance() {
		final int numFit = GET_NUM_TRUE(vary, numParams); 
		prepareAndSolve(numFit);
		SORT_CO_VARIANCE(coVar,numParams,vary,numFit);
		return coVar;
	}
	
	/** nonlinear least-squares fit, Marquardt's method (15.5)
	 * 
	 * @param x x Coordinates of the Data to fit
	 * @param y y Coordinates of the Data to fit
	 * @param sig y Standard Deviations of the Data to fit; can be null, if not needed
	 * @param numData #items to consider in the fit. 
	 * @return the current chi�
	 */
	public float fit(final float[] x, final float[] y) {
		return fit(x, y, null, x.length); }
	
	/** nonlinear least-squares fit, Marquardt's method (15.5)
	 * 
	 * @param x x Coordinates of the Data to fit
	 * @param y y Coordinates of the Data to fit
	 * @param sig y Standard Deviations of the Data to fit; can be null, if not needed
	 * @param numData #items to consider in the fit. 
	 * @return the current chi�
	 */
	public float fit(final float[] x, final float[] y, final float[] sig) {
		return fit(x, y, sig, x.length); }

	/** nonlinear least-squares fit, Marquardt's method (15.5)
	 * 
	 * @param x x Coordinates of the Data to fit
	 * @param y y Coordinates of the Data to fit
	 * @param sig y Standard Deviations of the Data to fit; can be null, if not needed
	 * @param numData #items to consider in the fit. 
	 * @return the current chi�
	 */
	public float fit(final float[][] x, final float[] y, final float[] sig, final int numData) {
		preFit();
		return fit(calcChiSqr(x,y,sig,numData));
	}
	
	/** nonlinear least-squares fit, Marquardt's method (15.5)
	 * 
	 * @param x x Coordinates of the Data to fit
	 * @param y y Coordinates of the Data to fit
	 * @param sig y Standard Deviations of the Data to fit; can be null, if not needed
	 * @param numData #items to consider in the fit. 
	 * @return the current chi�
	 */
	public float fit(final float[][] x, final float[] y, final float[] sig) {
		return fit(x, y, sig, x.length); }

	/** nonlinear least-squares fit, Marquardt's method (15.5)
	 * 
	 * @param x x Coordinates of the Data to fit
	 * @param y y Coordinates of the Data to fit
	 * @param sig y Standard Deviations of the Data to fit
	 * @param numData #items to consider in the fit. 
	 * @return the current chi�
	 */
	public float fit(final float[] x, final float[] y, final float[] sig, final int numData) {
		preFit();
		return fit(calcChiSqr(x,y,sig,numData));
	}
	
	/** nonlinear least-squares fit, Marquardt's method (15.5)
	 * 
	 * @param chiSqr the proposed new chi� 
	 * @return the current chi�
	 */
	private float fit(final float chiSqr) {
		if (oldChiSqr > chiSqr) { //Improvement!
			oldChiSqr = chiSqr;
			VectorFloat.COPY(params,  oldParams); //always sized properly, so do a full Copy
			VectorFloat.COPY(dParams, beta); 
			MatrixFloat.COPY_AT(alpha, coVar); 
			lamda *= 0.1;
		} else { //no Improvement, increase the Factor
			lamda *= 10;
		}
		return oldChiSqr;
	}

	/** preparation of the next Fitting Step	 */
	private void preFit() {
		final int numFit = GET_NUM_TRUE(vary, numParams); 
		prepareAndSolve(numFit);
		VectorFloat.COPY(oneDa, 0, numFit, dParams); 
		for (int j=-1,l=0; l<numParams; l++) {
			if ((vary == null) || vary[l]) { //
				params[l]=oldParams[l]+dParams[++j]; }
		}
	}
	
	/**	used to evaluate the linearized Fitting Matrix alpha and to calculate Chi�
	 * 
	 * @param x x Coordinates of the Data to fit
	 * @param y y Coordinates of the Data to fit
	 * @param sig y Standard Deviations of the Data to fit
	 * @param numData #items to consider in the fit. 
	 * @return the current chi�
	 */
	private float calcChiSqr(final float[] x, final float[] y, final float[] sig, final int numData){
		final int mfit=GET_NUM_TRUE(vary, numParams);
		VectorFloat.FILL_AT(dParams, 0, 0, mfit);
		MatrixFloat.FILL_LOWER(coVar, 0, mfit);
		float chiSqr=0;
		for (int i=0; i<numData; i++) {
			chiSqr += diffSqr(y, sig, i, funcs.map(x[i], params, dyda)); }
		L.n("coVar:",-2).l(coVar,-2);
		MatrixFloat.COPY_LOWER_TO_UPPER(coVar);// COPY_LOWER_TO_UPPER(coVar, coVar.length);
		return chiSqr;
	}
	
	/**	used to evaluate the linearized Fitting Matrix alpha and to calculate Chi�
	 * 
	 * @param x x Coordinates of the Data to fit
	 * @param y y Coordinates of the Data to fit
	 * @param sig y Standard Deviations of the Data to fit
	 * @param numData #items to consider in the fit. 
	 * @return the current chi�
	 */
	private float calcChiSqr(final float[][] x, final float[] y, final float[] sig, final int numData){
		final int mfit=GET_NUM_TRUE(vary, numParams);
		VectorFloat.FILL_AT(dParams, 0, 0, mfit);
		MatrixFloat.FILL_LOWER(coVar, 0, mfit);
		float chiSqr=0;
		for (int i=0; i<numData; i++) {
			chiSqr += diffSqr(y, sig, i, funcs.map(x[i], params, dyda)); }
		L.n("coVar:",-2).l(coVar,-2);
		MatrixFloat.COPY_LOWER_TO_UPPER(coVar);// COPY_LOWER_TO_UPPER(coVar, coVar.length);
		return chiSqr;
	}
	
	/** calculates dy and fills the CoVariance Matrix 
	 * 
	 * @param y the List of measured Values 
	 * @param yFunc the fitted Value
	 * @param sig the Standard Deviation, can be null 
	 * @param i the current Index applied to y
	 * @return dy=(y[i]-yFunc)�/sig[i]
	 */
	private float diffSqr(final float[] y, final float[] sig, int i, final float yFunc) {
		float sig_i=1;
		if (sig != null) {
			sig_i/=ByRefFloat.SQR(sig[i]); }
		final float dy=y[i]-yFunc;
		for (int j=-1,n=0; n<numParams; n++) {
			if ((vary == null) || vary[n]) { //fill the Matrix sparsely!
				final float wt=dyda[n]*sig_i; ++j;
				for (int k=-1,m=0; m<=n; m++) {
					if ((vary == null) || vary[m]) { //
						coVar[j][++k] += wt*dyda[m]; } 
				}
				dParams[j] += dy*wt;
			}
		}
		return dy*dy*sig_i;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// static Testing and main() Method
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static final void testFitting() {
		final int NPT = 10000;
		final float SPREAD = 0.001f;

		final float[] params= {5, 2, 3, 2, 5, 3};
		final float[] gues= {4.5f, 2.2f, 2.8f, 2.5f, 4.9f, 2.8f};

		final boolean[] vary=new boolean[params.length];
		final float[] x=new float[NPT];
		final float[] y=new float[x.length];
		final float[] sig=new float[x.length];
		final FitGauss gauss = FitGauss.SINGLETON;//(a); 
		for (int i=x.length; --i>=0;) {
			x[i]=0.1f*i;
			y[i]=gauss.map(x[i], params, null);
			y[i] *= (1+SPREAD*RandomGauss.NEXT_FLOAT());
			sig[i]=SPREAD*y[i]+ByRefFloat.FLOAT_ACCURACY;
		}
		L.n();
		L.n("try a sum of two Gaussians");
		L.n("Fit ALL Parameters");
		final float[] a= new float[params.length]; 
		for (int i=0; i<vary.length; i++) { vary[i]=true; } 
		for (int i=0; i<a.length; i++) { a[i]=gues[i]; }
		for (int iter=1;iter<=2;iter++) {
			final FitFloat fitter = new FitFloat(gauss,a,vary); 
			float chiSqr = fitter.fit(x,y,sig);//null);//
			for (int k=1,itst=0;itst < 4; k++) {
				L.n("Iteration #").l(k).l("	chi-squared:").l(chiSqr).l("	lamda:").l(fitter.lamda);
				L.n(a);
				final float oldChiSqr=chiSqr;
				chiSqr = fitter.fit(x,y,sig);
				if (chiSqr > oldChiSqr) {
					itst=0;
				} else if (Math.abs(oldChiSqr-chiSqr) < 0.1) {
					++itst; //stop after 4 consecutive real Improvements
				} 
			}
			L.n("Uncertainties:");
			final float[][] coVar = fitter.getCoVariance(); 
			double maxDev = 0; 
			for (int i=0;i<coVar.length;i++) {
				final double stdDev = Math.sqrt(coVar[i][i]);
				if (maxDev < stdDev) {
					maxDev = stdDev; }
				L.l(stdDev); } 
			L.n("Expected results:").l(params);
			L.n("Quality of Fit:").l(GammaP.PROBABILITY_CHI_SQR(x.length,chiSqr));
			Assert.EQUALS(params, a, 0, maxDev);
			if (iter == 1) {
				L.n();
				L.readString("press return to continue with constraint");
				L.n("holding a[2] and a[5] constant");
				for (int j=0;j<params.length;j++) { //offset the current Results
					a[j] -= 0.1; } 
				a[2]=params[2]; vary[2]=false;
				a[5]=params[5]; vary[5]=false;
			}
		}
	}
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() throws Exception {
		testFitting();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}
