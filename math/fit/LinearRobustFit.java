/*
 * File Name: LinearRobustFit.java
 * Created on: 19.02.2004
 *
 */
package math.fit;

import math.vector.HunterFloat;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.integer.random.RandomQuick;
import streamIO.object.IStreamIn;
import streamIO.real.IStreamIn_Float;
import streamIO.real.random.RandomGauss;
import function.IFloatFunction;
import function.byref.ByRefFloat;

/**
 * Defines static methods to fit an affine model {@code y = a + b*x} (linear regression) by
 * minimizing the mean absolute deviation rather than chi-squared, which is much more robust
 * against outliers.
 *
 * <p>The class itself implements a privately used, continuous but not differentiable helper
 * function: {@link #LINEAR_ROBUST_FIT(float[], float[], float[], int, float[], float)} uses
 * instances of this class to fit data to a straight line robustly (Numerical Recipes 15.7),
 * i.e. considering the L1 norm and not the squared norm. The helper array used to find the
 * median is cached per instance.
 *
 * @author mheuer
 * @version	1.0
 * @see IFloatFunction the interface this implements to be minimized by its own bisection search
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:51:11Z
 * digest: d517d50d78c9bf5368ffef7799f402e6307b6723f6bade17b30343ca0f55f12b
 * stale: false
 * tags: [code/linear_regression]
 * concepts: [Robust Linear Fit]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class LinearRobustFit 
implements IFloatFunction {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(LinearRobustFit.class);
	
	/**	fits data to a straight line robustly, least absolute (not squared) deviation (15.7)
	 * 
	 * @param x fist Data Vector 
	 * @param y second Data Vector 
	 * @param ab returns the Parameters a and b in Place 
	 * @return the mean absolute Deviation 
	 */
	final static public float LINEAR_ROBUST_FIT(final float[] x, final float[] y, float[] ab) {
		return LINEAR_ROBUST_FIT(x, y, 0, x.length, ab); }
	
	/**	fits data to a straight line robustly, least absolute (not squared) deviation (15.7)
	 * Acquires a first Guess of the Parameters from regular linear Regression. 
	 * 
	 * @param x fist Data Vector 
	 * @param y second Data Vector 
	 * @param ab returns the Parameters a and b in Place, no Initialization necessary.  
	 * @return the mean absolute Deviation 
	 */
	final static public float LINEAR_ROBUST_FIT(final float[] x, final float[] y
	, final int start, final int stop, float[] ab) {
		//Classical Linear Regression for first Estimates
		float sx=0,sy=0,sxy=0,sxx=0,syy=0;
		for (int j=stop; --j>=start;) {
			final float x_j = x[j]; 
			final float y_j = y[j]; 
			sx += x_j; //use simple Squares for initial Guess!
			sy += y_j;
			sxy += x_j*y_j;
			sxx += x_j*x_j; //syy is only necessary for Chi�! Not for Parameters a and b
			syy += y_j*y_j;
		}
		final int n = stop-start;
		final float del=n*sxx-sx*sx;
		final float a=ab[0]=(sxx*sy-sx*sxy)/del;
		final float b=ab[1]=(n*sxy-sx*sy)/del;
		final float chiSqr=syy+a*a*n+b*b*sxx-2*(a*sy+b*(sxy-a*sx)); //rough Estimate without looping again... 
		final float stdDev=(float)Math.sqrt(chiSqr/del);
		L.n("Initial Guess using linear Regression: a=").l(a).l("	b=").l(b);
		return LINEAR_ROBUST_FIT(x, y, start, stop, ab, stdDev);
	}

	/**	fits data to a straight line robustly, least absolute (not squared) deviation (15.7)
	 * 
	 * @param x fist Data Vector 
	 * @param y second Data Vector 
	 * @param ab 
	 * on Input : requires initial Estimates for the Parameters a and b 
	 * on Output: returns the Parameters a and b in Place 
	 * @return the mean absolute Deviation 
	 */
	public static float LINEAR_ROBUST_FIT(
		final float[] x,
		final float[] y,
		final int start,
		final int stop,
		float[] ab,
		float stdDev) {
		float bb = ab[1]; 
		final LinearRobustFit fn = new LinearRobustFit(x, y, start, stop, ab[0]);
		//Bracketing...
		float b1=bb;
		float f1=fn.Map(b1);
		float b2=bb+ByRefFloat.assignSign(3*stdDev,f1);
		float f2=fn.Map(b2);
		while (f1*f2 > 0) {
			bb=2*b2-b1;
			b1=b2;
			f1=f2;
			b2=bb;
			f2=fn.Map(b2);
		}
		//BiSection
		stdDev*=0.01f;
		while (Math.abs(b2-b1) > stdDev) {
			bb=0.5f*(b1+b2); //BiSection Algorithm
			if (bb == b1) { // || bb == b2) {
				break; } 
			final float f=fn.Map(bb);
			if (f*f1 >= 0) {
				f1=f;
				b1=bb;
			} else {
				f2=f;
				b2=bb;
			}
		}
		ab[0]=fn.aa;
		ab[1]=bb;
		return fn.abdevt/(stop-start);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** initializing Constructor	 */
	protected LinearRobustFit(final float[] xt_, final float[] yt_, final int start_, final int stop_, final float aa_) {
		this.xt = xt_; 
		this.yt = yt_; 
		this.aa = aa_; 
		this.stop = stop_;
		this.start = start_;
		arr = new float[yt.length]; //
	}

	/** fist Index to use in fitting (inclusive) 	 */
	final int start; 

	/** last Index to use in fitting (exclusive) 	 */
	final int stop; 

	/** Data Vector to fit 	*/
	final float[] xt;

	/** Data Vector to fit 	*/
	final float[] yt;

	/** Helper Vectors to efficiently find the Median 	*/
	final float[] arr; 

	/** contains the current Estimation for the Parameter a	*/ 
	float aa;

	/** contains the Sum of the absolute Deviations	*/ 
	float abdevt;
	
    /**
     * Reports that this function imposes no ordering requirement on its caller.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }

	/**
	 * Re-estimates the intercept as the median residual for the given slope, then returns
	 * the sign-weighted deviation used to bracket/bisect the root for the robust fit.
	 * @param b the current Estimate for the Slope of the Line
	 * @return the absolute Deviation between the current Fit and the Data
	 * scaled by the Size of the Data.
	 */
	public double Map(final double b) { return Map((float) b); }

	/**
	 * Re-estimates the intercept as the median residual for the given slope, then returns
	 * the sign-weighted deviation used to bracket/bisect the root for the robust fit.
	 * @param b the current Estimate for the Slope of the Line
	 * @return the absolute Deviation between the current Fit and the Data
	 * scaled by the Size of the Data.
	 */
	public float Map(final float b) {
		float EPS = 1e-7f; 
		//new Estimation for aa using the given b
		for (int j=stop; --j>=start; ) {
			arr[j]=yt[j]-b*xt[j]; } 
		final float median = HunterFloat.GET_MEDIAN(arr, 1, arr.length-1);
		aa = median; 
		abdevt=0;
		float sum=0;
		for (int j=stop; --j>=start; ) {
			float d=yt[j]-(b*xt[j]+aa);
			abdevt += Math.abs(d);
			if (yt[j] != 0) { //scale Deviation with the Size
				d /= Math.abs(yt[j]); } 
			if (Math.abs(d) > EPS) { //Sum: x[i]*Sign(y[i]-a-b*x[i])
				sum += (d >= 0 ? xt[j] : -xt[j]);} 
		}
		return sum;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// static testing an main() Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** test the MedianFit Routine 	 */
	private static final void testMedianFit(){
		final int NPT = 50;
		final int NDATA = NPT; 
		final float SPREAD = 1; 
		
		//Accurate Parameters
		final float a = 2; 
		final float b = 1; 
		//int mwt=1;
		//float abdev,chi2,q,siga,sigb;

		final float[] ab= new float[2];
		final float[] x= new float[1+NDATA];
		final float[] y= new float[1+NDATA];
		final float[] sig= new float[1+NDATA];
		final IStreamIn_Bound_Int iRan = new RandomQuick();//.randomize();
		final IStreamIn_Float ran = new RandomGauss(iRan);
		for (int i=x.length; --i>=0;) {
			x[i]=0.1f*i;
			y[i] = b*x[i]+a;
			if ((i&1) == 0) { //with pure noisy Data, Linear Regression is actually better
				y[i]+=SPREAD*ran.nextFloat(); } //but this Method handles individual Outliers much better! 
			sig[i]=SPREAD;
		}
		final float abdev = LINEAR_ROBUST_FIT(x, y, ab);
		L.n("   #Points: ").l(x.length);
		L.n("   gaussian SPREAD: ").l(SPREAD);
		L.n("   a = ").l(ab[0]); Assert.EQUALS(ab[0], a, 1f/NDATA); //Square Fits converge only as 1/SqRt(NDATA)!
		L.n("   b = ").l(ab[1]); Assert.EQUALS(ab[1], b, 1f/NDATA);
		L.n("   absolute deviation (per data point): ").l(abdev);
	}
	
	private static final void testLinFit() {
		final int NPT = 100;
		final float SPREAD = 0.5f;
		
		final float[][] abSigaSigb = new float[2][2];

		final float[] sig= new float[NPT];
		final float[] x= new float[sig.length];
		final float[] y= new float[sig.length];
		for (int i=sig.length; --i>=0; ) {
			sig[i]=SPREAD;
			x[i]=0.1f*i;
			y[i] = -2*x[i]+1+SPREAD*RandomGauss.NEXT_FLOAT();
		}
		L.n("#Points:").l(sig.length);
		L.n("   gaussian SPREAD: ").l(SPREAD);
		for (float[] sigArg = null; ; ) {
			final float chi2 = VectorFloat.LINEAR_FIT(x,y,sigArg,abSigaSigb);
			if (sigArg == null) {
				L.n("Ignoring standard deviations");
			} else {
				L.n("Including standard deviations");
			}
			L.n("a  =  ").l(abSigaSigb[0][0]).l(" with uncertainty:").l(abSigaSigb[1][0]);
			L.n("b  =  ").l(abSigaSigb[0][1]).l(" with uncertainty:").l(abSigaSigb[1][1]);
			L.n("chi-squared: ").l(chi2);
			Assert.EQUALS(abSigaSigb[0][0],  1.0094490f);
			Assert.EQUALS(abSigaSigb[0][1], -1.9950036f);
			//L.n("goodness-of-fit: ",q);
			if (sigArg == null) { //loop with Test AND Modification in the End
				sigArg = sig;
			} else {
				break; 
			}
		}
	}
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() throws Exception {
		testMedianFit();
		testLinFit();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}
