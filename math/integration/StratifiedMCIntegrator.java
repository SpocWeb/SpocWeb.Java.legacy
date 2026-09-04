/*
 * File Name: StratifiedMCIntegrator.java
 * Created on: 25.01.2004
 *
 */
package math.integration;

import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.random.RandomFast;
import streamIO.real.IStreamIn_Float;
import streamIO.real.random.RandomUniformVector;
import function.byref.ByRefFloat;
import function.vector.IFloatScalarField;

/**
 * Title: StratifiedMCIntegrator<p>
 * Description:
 * Implements a recursive stratified Monte Carlo Integration. 
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
public class StratifiedMCIntegrator {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(StratifiedMCIntegrator.class);

	////////////////////////////////////////////////////////////////////////////

	/** The Fraction of remaining Function Evaluations used at each Stage
	 * to explore the Variance of the Function. 
	 */
	static final float PFAC = 0.1f;
	
	/** At least MNPT Evaluations are performend in each terminal Subregion */
	static final int MNPT = 15;
	
	/** A Subregion is further subdivided by BiSection, 
	 * if at least MNBS Function Evaluations are available */
	static final int MNBS = 60;
	static final float TINY = 1e-30f;
	static final float BIG = 1/TINY;
	static final float TWO_THIRDS = 2f/3;
	
	////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////////////
	
	/** The random Number Generator used 	*/
	final IStreamIn_Float ran; 
	
	/** Value of the internal Random Number Generator */
	int iran; 
	
	/**
	 * 
	 */
	public StratifiedMCIntegrator(final IStreamIn_Float ran_) {
		this.ran = ran_;
	}

	/**	recursive multidimensional Stratified Monte Carlo integration (7.8) 
	 * to integrate a Function in R^n 
	 * 
	 * @param func the Function to integrate 
	 * @param regn the rectangular Region over which to integrate  
	 * @param ndim The Number of Dimensions 
	 * @param npts The Number of Function Evaluations. 
	 * @param dith Normally 0, 
	 * but e.g. 0.1 when the Functions active Region 
	 * falls on the Boundary of a 2^n Region Subdivision (2^n Tree)    
	 * @param variance if not null, the Variance (StdDev²) is returned in [0]
	 * @return the mean Value of the Function in this Region. 
	 */
	public float integrate(final IFloatScalarField func, final float region[][], final int nDim, final long numPoints,
	final float dith, final float[] variance) {
		final float[] pt=new float[1+nDim];
		if (numPoints < MNBS) { //Too few Points for Bisection...
			float summ2, summ=summ2=0; 
			for (long i=numPoints; --i>=0; ) { //...do straight Monte Carlo
				RandomUniformVector.RANDOM_VECTOR(ran, pt,region[0],region[1],1,pt.length);
				final float fVal= func.Map(pt);
				summ += fVal;
				summ2 += fVal * fVal;
			}
			variance[0]=Math.max(TINY,(summ2-summ*summ/numPoints)/(numPoints*numPoints));
			return summ/numPoints;
		} 
		//perform preliminary (uniform) sampling 
		final float[] rmid=new float[1+nDim];
		final long npre=Math.max((long)(numPoints*PFAC),MNPT);
		final float[] sigb = new float[2];
		int jb = dimToSubsect(func, region, nDim, dith, pt, rmid, npre, sigb);
		if (jb == 0) { //MNPT may be too small 
			jb=1+(nDim*iran)/175000; } 
		final float rgl=region[0][jb];
		final float rgm=rmid[jb];
		final float rgr=region[1][jb];
		final float fracl=Math.abs((rgm-rgl)/(rgr-rgl)); 
		//Apportion the remaining Points between left and right Interval 
		final long nptl=(long)(MNPT+(numPoints-npre-2*MNPT)*fracl*sigb[0]
			/(fracl*sigb[0]+(1-fracl)*sigb[1])); //see 7.8.23
		final long nptr=numPoints-npre-nptl;
		//Recursion: create two Subregions... 
		final float[][] regn_temp = new float[2][1+nDim];
		for (int j=1;j<=nDim;j++) {
			regn_temp[0][j]=region[0][j];
			regn_temp[1][j]=region[1][j];
		}
		regn_temp[1][jb]=rmid[jb];
		//...integrate them...
		final float[] varl =  new float[1];
		final float avel = integrate(func,regn_temp,nDim,nptl,dith,varl); //
		regn_temp[0][jb]=rmid[jb];
		regn_temp[1][jb]=region[1][jb];
		final float ave = integrate(func,regn_temp,nDim,nptr,dith,variance); //
		//...and recombine the Result, see 7.8.11
		variance[0]=fracl*fracl*varl[0]+(1-fracl)*(1-fracl)*variance[0];
		return fracl*avel+(1-fracl)*ave;
	}

	/** @see #integrate(IFloatScalarField, float[][], int, long, float, float[]) uses this Function exclusively 
	 * 
	 * @param func
	 * @param region
	 * @param nDim
	 * @param dith
	 * @param pt
	 * @param rmid used as Output
	 * @param npre
	 * @param sigb used as Output
	 * @return the Dimension to intersect by. 
	 */
	private int dimToSubsect(
		final IFloatScalarField func,
		final float[][] region,
		final int nDim,
		final float dith,
		final float[] pt,
		final float[] rmid,
		final long npre,
		final float[] sigb) {
		final float[] fmaxl=new float[1+nDim];
		final float[] fmaxr=new float[1+nDim];
		final float[] fminl=new float[1+nDim];
		final float[] fminr=new float[1+nDim];
		for (int j=1;j<=nDim;j++) { //initialize left and right Bounds for each Dimension 
			iran=(iran*2661+36979) % 175000; //Random Number Generator
			final float s=ByRefFloat.assignSign(dith,(float)(iran-87500));
			rmid[j]=(0.5f+s)*region[0][j]+(0.5f-s)*region[1][j]; //choose random Intersections
			fminl[j]=fminr[j] = +BIG;
			fmaxl[j]=fmaxr[j] = -BIG;
		}
		for (long i=npre; --i >= 0;) { //Loop over the Points in the Sample
			RandomUniformVector.RANDOM_VECTOR(ran, pt,region[0],region[1],1,pt.length);
			final float fVal=func.Map(pt);
			for (int j=1; j<=nDim;j++) { //find the left and right Bounds... 
				if (pt[j]<=rmid[j]) { //...for each Dimension. 
					fminl[j]=Math.min(fminl[j],fVal);
					fmaxl[j]=Math.max(fmaxl[j],fVal);
				} else {
					fminr[j]=Math.min(fminr[j],fVal);
					fmaxr[j]=Math.max(fmaxr[j],fVal);
				}
			}
		}
		//Choose the Dimension to bisect
		int jb=0;
		float minSum = BIG;
		sigb[0]=sigb[1]=1; 
		for (int j=1; j<=nDim;j++) {
			if (fmaxl[j] > fminl[j] && fmaxr[j] > fminr[j]) {
				final float sigl=Math.max(TINY, (float) Math.pow(fmaxl[j]-fminl[j], TWO_THIRDS));
				final float sigr=Math.max(TINY, (float) Math.pow(fmaxr[j]-fminr[j], TWO_THIRDS));
				final float sum=sigl+sigr; //see Equation 7.8.24
				if (minSum>=sum) {
					minSum =sum;
					sigb[0]=sigl;
					sigb[1]=sigr;
					jb=j;
				}
			}
		}
		return jb;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// static Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** Testing Method	 */
	public static void testIt() {
		L.n("Testing Monte Carlo Integration by stratified Sampling for several Dimensions");
		for (int nDim = 0; ++nDim<=3; ) {
			testDimension(nDim);
		}
	}

	/** tests the Integration for the given Number of Dimensions	
	 * 
	 * @param nDim
	 */
	private static void testDimension(int nDim) {
		L.n("\nTesting Monte Carlo Integration by stratified Sampling for "+nDim+" Dimensions:");
		final int numTries = 5;
		final float[] var = new float[1];
		final RandomFast ran = new RandomFast();
		final StratifiedMCIntegrator integrator = new StratifiedMCIntegrator(ran); 
		int numPoints = 10;
		for (int i = nDim; --i >= 0;) {
			numPoints *= 10; }
		L.n("Dimension=").l(nDim).l("numPoints=").l(numPoints);
		final float xOff = 0.1f+ran.nextFloat()*.8f; //can be random between 0.1 and 0.9
		final float dith = 0; //.1f;
		final float[][] region=new float[2][1+nDim];
		final IFloatScalarField func = new TestScalarField(xOff, nDim); //ConstScalarField(1); 
		float sumAv=0; 
		float sumSd=0;
		//Perform the Integration several times...
		//ran.randomize(); //makes it non-repeatable; not good for Unit Tests!
		for (int nt=1; nt<=numTries; nt++) {
			for (int j=1;j<=nDim;j++) { //Unity HyperCube
				region[0][j]=0;
				region[1][j]=1;
			}
			final float ave = integrator.integrate(func, region, nDim, numPoints, dith, var);
			final double sd = Math.sqrt(Math.abs(var[0]));
			L.n("avg= ").l(ave).l("+/-").l(sd);
			sumSd += sd;
			sumAv += ByRefFloat.SQR(ave-TestScalarField.EXPECTED_UNITY_INTEGRAL);
			Assert.EQUALS(TestScalarField.EXPECTED_UNITY_INTEGRAL, ave, 2*sd);
		}
		//...and average the Results
		sumAv=(float)Math.sqrt(sumAv/numTries);
		sumSd /= numTries;
		L.n("Fractional error: actual=").l(sumAv).l("indicated= ").l(sumSd);
	}
	
	/** externally visible Main Method	 */
	public static void main(final String[] args) {
		testIt(); 
	}

}
