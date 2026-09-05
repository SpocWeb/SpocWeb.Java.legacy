/*
 * File Name: AdaptiveMCIntegrator.java
 * Created on: 24.01.2004
 *
 */
package math.integration;

import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.random.RandomFast;
import streamIO.real.IStreamIn_Float;
import function.byref.ByRefFloat;

/**
 * Implements an adaptive Grid (VEGAS) Monte Carlo Integration, with possible Restarts of
 * the Grid, the accumulated Values or the Parameters on several Levels.
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
 * mtime: 2026-09-05T11:50:26Z
 * digest: 59b0e83f9700fc5e5e41484b231ada1a296e243a08b14b61dfa216ddc48762a7
 * stale: false
 * tags: [code/numerical_integration]
 * concepts: [Adaptive Monte Carlo Integrator]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class AdaptiveMCIntegrator {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(AdaptiveMCIntegrator.class);
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** between [1,2] A heuristic Value is 2	*/
	final static public float ALPH = 1.5f; //

	/** maximum Number of Gridpoints per Dimension	 */
	final static public int NDMX = 50;
	
	/** Cutoff for small Differences 	*/
	private static final float TINY = 1e-30f; //ByRefFloat.FLOAT_MIN_VALUE;
	 
	/** sample rebinning used by (7.8)
	 * Helper Method to rebin a Vector of Densities xi 
	 * into new Bins defined by Vector r
	 * 
	 * float Accuracy is completely sufficient, 
	 * since double Accuracy cannot be realistically achieved! 
	 * @param rc
	 * @param nd
	 * @param r
	 * @param xin
	 * @param xi
	 */
	private static final void rebin(final float rc, final int nd, final float[] r, final float[] xin, final float[] xi) {
		int k=0;
		float dr=0,xn=0,xo=0;
		for (int i=1;i<nd;i++) {
			while (rc > dr) {
				dr += r[++k];
				xo=xn;
				xn=xi[k];
			}
			dr -= rc;
			xin[i]=xn-(xn-xo)*dr/r[k];
		}
		for (int i=1;i<nd;i++) {
			xi[i]=xin[i]; } 
		xi[nd]=1;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/** Reference to the random Number Generator used	 */
	final IStreamIn_Float ran;

	/** The number of Dimensions for Integration 	 */
	final int ndim; 
	
	/** the Region to integrate over 	 */
	float[][] region;
	
	/** the Function to integrate 	 */
	IParamScalarField fxn; 

	final int[] ia;
	final int[] kg;
	final float[] dt;
	final float[] dx; 
	final float[] x;
	final float[][] d; 
	final float[][] di; 
	final float[][] xi; 
	final float[] r;
	final float[] xin;

	int mds,ndo; //Grid Parameters
	float schi,si,swgt; //Value Parameters

	//additional Parameters that might be retained between Calls! 
	int ng=1;
	int nd=NDMX;
	int npg = 0;
	float xnd = 0;
	float xjac = 0;
	float dxg = 0;
	float dv2g = 0;

	/////////////////////////////////////////////////////////////////////////////////////
	// Constructor
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Allocates the Grid Arrays sized for the given Dimension Count and initializes them
	 * via {@link #reset(IParamScalarField, float[][], int, int)}.
	 *
	 * @param ndim_ the Number of Dimensions to integrate in
	 * @param ran_ the Random Number Generator to use
	 *
	 * @param fxn_ the Function fxn(y[], weigth) to integrate across the R^n Interval
	 * @param regn_ The rectangular (Hyper-)Volume in R^n over which to integrate
	 * @param ncall the number of Function Evaluations to perform
	 * @param maxIter the maximum Number of Iterations (should be between 5 and 10)
	 */
	public AdaptiveMCIntegrator(final int ndim_, final IStreamIn_Float ran_, final IParamScalarField fxn_, final float[][] region_, final int ncall, final int maxIter) {
		this.ndim = ndim_; 
		this.ran = ran_;
		ia = new int[ndim+1];
		kg = new int[ndim+1];
		x = new float[ndim+1];
		dx = new float[ndim+1];
		dt = new float[ndim+1];
		xi = new float[ndim+1][NDMX+1];
		di = new float[NDMX+1][ndim+1];
		d = new float[NDMX+1][ndim+1];
		r = new float[NDMX+1];
		xin = new float[NDMX+1];
		reset(fxn_, region_, ncall, maxIter);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** Normal Entry for a cold Start, resets everything, except for the Dimensions	
	 * 
	 * @param fxn_ the Function fxn(y[], weigth) to integrate across the R^n Interval 
	 * @param regn_ The rectangular (Hyper-)Volume in R^n over which to integrate 
	 * @param ncall
	 * @param maxIter
	 */
	public void reset(final IParamScalarField fxn_, final float[][] region_, final int ncall, final int maxIter) {
		this.fxn = fxn_;
		this.region = region_; 
		mds=ndo=1; //mds = 0 => no stratified Sampling
		for (int j=1; j<=ndim; j++) { //i.e. importance Sampling only! 
			xi[j][1]=1; } 
		resetValues(ncall, maxIter);
	}

	/** Inherit the Parameters and Grid from the previous Call, but not the Values 	 */
	public void resetValues(final int ncall, final int maxIter) {
		si=swgt=schi=0; 
		resetParams(ncall, maxIter);
	} //

	/** inherit Grid and Values from previous Call, but not the Parameters 	 */
	public void resetParams(final int ncall, final int maxIter) {
		if (mds != 0) { //set up for Stratification
			ng=(int)Math.pow(ncall/2.0+0.25,1.0/ndim);
			mds=1;
			if ((2*ng-NDMX) >= 0) {
				mds = -1;
				int tmp=ng/NDMX+1;
				nd=ng/tmp;
				ng=tmp*nd;
			}
		}
		int k=1;
		for (int i=1;i<=ndim;i++) {
			k *= ng;} 
		npg=Math.max(ncall/k,2);
		final float calls=npg*k;
		dxg=1f/ng;
		dv2g=1;
		for (int i=1;i<=ndim;i++) {
			dv2g *= dxg; } 
		dv2g=ByRefFloat.SQR(calls*dv2g)/npg/npg/(npg-1);
		xnd=nd;
		dxg *= xnd;
		xjac=1f/calls;
		for (int j=1;j<=ndim;j++) { //Volume of the HyperCube
			dx[j]=region[1][j]-region[0][j];
			xjac *= dx[j];
		}
		if (nd != ndo) { //if necessary, perform Binning 
			for (int i=1;i<=nd;i++) { 
				r[i]=1; } 
			for (int j=1;j<=ndim;j++) {
				rebin(ndo/xnd,nd,r,xin,xi[j]); } 
			ndo=nd;
		}
		if (L.thresholdLog < Log.LEVEL_DEBUG) { //moderate Log Level
			L.n("Input parameters for vegas: ndim=").l(ndim).l("ncall=").l(calls);
			L.n("itmx=").l(maxIter);
			L.n("ALPH=").l(ALPH);
			L.n("mds=").l(mds).l("nd=").l(nd);
			for (int j=1;j<=ndim;j++) {
				L.n("xl[").l(j).l("]=").l(region[j]).l("xu[").l(j).l("]=").l(region[j+ndim]); }
		}
	}

	/**	performs an adaptive multidimensional Monte Carlo integration in R^n. (7.8)
	 * After each Iteration the Grid is refined
	 * Inherits the Grid, the Values AND the Parameters.
	 * more than 10 Iterations are rarely useful. 
	 * 
	 * @param ndim the Number of Dimensions 
	 * @param init Flag to indicate Initialization 
	 * @param ncall number of Function Calls per Iteration  
	 * @param numIter Number of Iterations (less than 5..10, more doesn't help)
	 * @param stdDevChiSqr if not null, the Values are filled with the Standard Deviation[0] and Chi�[1]
	 */
	public float integrate(final int ncall, final int numIter, final float[] stdDevChiSqr) {
		float integral = 0;
		float chiSquare = 0;
		float stdDev = 0; 
		for (int it=1; it<=numIter; it++) { //Main Iteration Loop 
			float ti=0;
			float tsi=0;
			for (int j=1; j<=ndim; j++) {
				kg[j]=1;
				for (int i=1; i<=nd; i++) {
					d[i][j]=di[i][j]=0; } 
			}
			for (;;) {
				float fb=0;
				float f2b=0;
				for (int k=1; k<=npg; k++) {
					final float wgt = calcWeight(xjac);
					final float f = wgt*fxn.map(x, wgt);
					final float f2=f*f;
					fb += f;
					f2b += f2;
					for (int j=1;j<=ndim;j++) {
						di[ia[j]][j] += f;
						if (mds >= 0) d[ia[j]][j] += f2;
					}
				}
				f2b=(float)Math.sqrt(f2b*npg);
				f2b=(f2b-fb)*(f2b+fb);
				ti += fb;
				if (f2b > TINY) {
				} else {
					f2b = TINY;
				}
				tsi += f2b;
				if (mds < 0) { //Use stratified Sampling
					for (int j=1;j<=ndim;j++) { 
						d[ia[j]][j] += f2b; }
				}
				int k=ndim;
				for (;k>=1;k--) {
					kg[k] %= ng;
					if (++kg[k] != 1) {
						break; } 
				}
				if (k < 1) { 
					break; } 
			}
			//Compute Results for this Iteration 
			tsi *= dv2g;
			final float wgt=1f/tsi;
			si += wgt*ti;
			schi += wgt*ti*ti;
			swgt += wgt;
			integral=si/swgt;
			chiSquare=(schi-si*integral)/(it-0.9999f);
			if (chiSquare < 0) {
				chiSquare = 0; }
			stdDev=(float)Math.sqrt(1/swgt);
			tsi=(float)Math.sqrt(tsi);
			if (L.thresholdLog < Log.LEVEL_DEBUG) { //moderate Log Level
				logState(integral, chiSquare, stdDev, it, ti, tsi); }
			for (int j=1;j<=ndim;j++) { //Refine the Grid...
				float xo=d[1][j];
				float xn=d[2][j];
				d[1][j]=(xo+xn)/2;
				dt[j]=d[1][j];
				for (int i=2;i<nd;i++) {
					final float rc=xo+xn;
					xo=xn;
					xn=d[i+1][j];
					d[i][j] = (rc+xn)/3;
					dt[j] += d[i][j];
				}
				d[nd][j]=(xo+xn)/2;
				dt[j] += d[nd][j];
			}
			for (int j=1;j<=ndim;j++) { //Damping to avoid Destabilization
				float rc=0;
				for (int i=1;i<=nd;i++) {
					if (d[i][j] >= TINY) { //0 => (1-0)/(log(dt)-log(0) == 1/Infinity == 0
					} else { //Compression in Range by ALPHA
						d[i][j] = TINY; 
						L.n("underflow: d[").l(i).l("][").l(j).l("]=").l(d[i][j]);
					}
					r[i]=(float)Math.pow((1-d[i][j]/dt[j])/
						(Math.log(dt[j])-Math.log(d[i][j])),ALPH);
					rc += r[i];
				}
				rebin(rc/xnd,nd,r,xin,xi[j]);
			}
		}
		if (stdDevChiSqr != null) {
			stdDevChiSqr[0] = stdDev;
			stdDevChiSqr[1] = chiSquare;
		}
		return integral; 
	}
	
	/** only here the random Numbers are used! 	*/
	private float calcWeight(float wgt) {
		for (int j=1; j<=ndim; j++) {
			final float xn=(kg[j]-ran.nextFloat())*dxg+1;
			ia[j]=Math.max(Math.min((int)(xn),NDMX),1);
			final float xo, rc;
			if (ia[j] > 1) {
				xo=xi[j][ia[j]]-xi[j][ia[j]-1];
				rc=xi[j][ia[j]-1]+(xn-ia[j])*xo;
			} else {
				xo=xi[j][ia[j]];
				rc=(xn-ia[j])*xo;
			}
			x[j]=region[0][j]+rc*dx[j];
			wgt *= xo*xnd;
		}
		return wgt;
	}

	/** logs the current State to the Logging Stream 	 */
	private void logState(final float integral, final float chiSquare, final float stdDev, final int it, final float ti, final float tsi) {
		L.n("iteration no.").l(it).l("integral = ").l(ti).l("+/-").l(tsi);
		L.n("all iterations:").l("integral =").l(integral).l("+/-").l(stdDev).l("chi^2/IT n = ").l(chiSquare);
		if (L.thresholdLog < Log.LEVEL_TRACE) { //verbose Log Level
			for (int j=1;j<=ndim;j++) {
				L.n(" DATA FOR axis ").l(j);
				L.n("X").l("delta i").l("X").l("delta i").l("X").l("delta i");
				for (int i=1;i<nd;++i) {
					L.n().l(xi[j][i]).l(di[i][j]).l(xi[j][i+1]
					).l(di[i+1][j]).l(xi[j][i+2]).l(di[i+2][j]);
				}
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////

	private static final void testVegas(final String[] args) {
		final int MAX_DIM = 3;
		final float[][] region=new float[2][1+MAX_DIM];
		final RandomFast ran = new RandomFast();
		for(int nDim = 0; ++nDim <= MAX_DIM;) { //TODO: fill these Values from the Command Line 
			int numPoints = 10;
			for (int i = nDim; --i >= 0;) {
				numPoints *= 10; }
			L.n("\nDimension=").l(nDim).l("numPoints=").l(numPoints);
			final float xOff = 0.5f; //0.1f+ran.nextFloat()*.8f; //can be random between 0.1 and 0.9
			final int numIter=5;
			final IParamScalarField fxn = new TestScalarField(xOff, nDim); //ConstScalarField(1); // 
			for (int j=1;j<=nDim;j++) { //initialize the Unit (Hyper-)Cube
				region[0][j]=0;
				region[1][j]=1;
			}
			final AdaptiveMCIntegrator integrator = new AdaptiveMCIntegrator(nDim, ran, fxn, region, numPoints, numIter); 
			final float[] stdDevChiSqr= new float[2]; 
			//integrator.reset(regn, ncall, itmax); 
			float avgi=integrator.integrate(numPoints,numIter,stdDevChiSqr);
			L.n("Number of iterations performed:").l(numIter);
			L.n("Integral=").l(avgi).l("Standard Dev.=").l(stdDevChiSqr[0]).l("Chi-sq. =").l(stdDevChiSqr[1]);
			Assert.EQUALS(TestScalarField.EXPECTED_UNITY_INTEGRAL, avgi, 1.5*stdDevChiSqr[0]);
			//integrator.resetValues(regn, ncall, itmax); //loses all previous Information
			integrator.resetParams(numPoints, numIter); //reuses at least the Function Values
			avgi=integrator.integrate(numPoints,numIter,stdDevChiSqr);
			L.n("Additional iterations performed: ").l(numIter);
			L.n("Integral=").l(avgi).l("Standard Dev.=").l(stdDevChiSqr[0]).l("Chi-sq. =").l(stdDevChiSqr[1]);
			Assert.EQUALS(TestScalarField.EXPECTED_UNITY_INTEGRAL, avgi, 1.5*stdDevChiSqr[0]);
		}
	}

	/** tests all (public) Methods of this Class	 */
	public static void testIt(String[] args) throws Exception {
		testVegas(args); 
	} //

	/** externally visible Main Method	 */
	public static void main(String[] args) throws Exception {
		testIt(args); 
	}

}
