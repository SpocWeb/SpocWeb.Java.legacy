/*
 * File Name: WaveletStep.java
 * Created on: 01.11.2003
 *
 */
package math.wavelet;

import math.vector.VectorDouble;

/**
 * Title: WaveletStep<p>
 * Description:
 * Purpose:
 * Implementation of Wavelet Stepper Routine. 
 * Defines static Instances (Flyweight Pattern) 
 * that hold the Coefficients required to perform the Transformation. 
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
final public class WaveletStep implements IWaveletStep {

	private static final double c4[]={Daubechies4.C0, Daubechies4.C1, Daubechies4.C2, Daubechies4.C3};
	
	final static public double C6_NORM = Math.sqrt(2)/32;
	
	final static public double SQRT10 = Math.sqrt(10);
	
	final static public double C6_SQRT = Math.sqrt(5+SQRT10+SQRT10);
	
	private static final double c6[]={
		( 1+   SQRT10+C6_SQRT  )*C6_NORM, 
		( 5+   SQRT10+C6_SQRT*3)*C6_NORM, 
		(10-2*(SQRT10-C6_SQRT) )*C6_NORM,
		(10-2*(SQRT10+C6_SQRT) )*C6_NORM, 
		( 5+   SQRT10-C6_SQRT*3)*C6_NORM,
		( 1+   SQRT10-C6_SQRT  )*C6_NORM};
		
	private static final double c12[]={0.111540743350, 0.494623890398, 0.751133908021,
		0.315250351709,-0.226264693965,-0.129766867567,
		0.097501605587, 0.027522865530,-0.031582039318,
		0.000553842201, 0.004777257511,-0.001077301085};
		
	private static final double c20[]={0.026670057901, 0.188176800078, 0.527201188932,
		0.688459039454, 0.281172343661,-0.249846424327,
		-0.195946274377, 0.127369340336, 0.093057364604,
		-0.071394147166,-0.029457536822, 0.033212674059,
		0.003606553567,-0.010733175483, 0.001395351747,
		0.001992405295,-0.000685856695,-0.000116466855,
		0.000093588670,-0.000013264203};
	
	private static final WaveletStep[] flyWeights= new WaveletStep[21]; 

	final static public WaveletStep GET_PARTIAL_TRAFO(final int numCoefficients) {
		if (flyWeights[numCoefficients] == null) {
			throw new NullPointerException("No Trafo defined for "+numCoefficients+" Coefficients!"); }
		return flyWeights[numCoefficients];
	}

	/** Further Wavelet Filters can be included in the obvious Manner.	 */	
	static {
		flyWeights[ 4] = new WaveletStep(c4);
		flyWeights[ 6] = new WaveletStep(c6);
		flyWeights[12] = new WaveletStep(c12);
		flyWeights[20] = new WaveletStep(c20);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	private final double[][] coeffs;

	/** @return a meaningful String Representation for Debugging	 */
	public String toString() { return getClass().getName()+"["+coeffs.length+"]"; }

	/** initialize coefficients for Partial Wavelet Transform, Chapter 13.10
	 * Initializing Routine for implementing the Daubechies wavelet Filters
	 * with 4, 12 and 20 Coefficients as selected by n.
	 * daub4 is considerably faster than PWT for n=4!
	 * Applies an arbitrary wavelet filter to data Vector a[1..n] 
	 * 
	 * @param coeffs The Coefficients
	 */
	private WaveletStep(final double[] coeffs_) {
		boolean positive = false; 
		coeffs = new double[coeffs_.length][2]; //faster Access
		for (int k=0; k<coeffs_.length; k++) {
			coeffs[(coeffs.length-1)-k][1]=(positive ? (coeffs[k][0] = coeffs_[k]) : -(coeffs[k][0] = coeffs_[k]));
			positive = !positive;
		}
	}

	/**
	 * @see math.IWaveletStep#wtstep(double[], int, boolean)
	 * Partial Wavelet Transform, Chapter 13.10	
	 * Applies an arbitrary wavelet Filter to Data Vector a[1..n] (iSign = true)
	 * or it's Transpose (iSign = false)
	 * Used hierarchically by wt1 and wtn.
	 * The actual Filter is determined by a previous call to pwtset, which initializes wfilt
	 * 
	 * @param a the Vector to filter
	 * @param n the Filter to use
	 * @param isign
	 */
	final public void wtstep(final double[] a, final int n, final boolean inverse) {
		if (n < 4) return;
		//local Workspace is Thread-safe, but expensive!
		final double[] wksp=new double[n]; //1..n
		//VectorDouble.fillAt(wksp, 0, 0, n); //for Workspace Reuse!

		final int offset=-(coeffs.length >> 1);
		final int nmod=coeffs.length*n;
		final int n_1=n-1; //used as binary Mask
		final int nHalf=n >> 1;
		if (inverse) {
			for (int ii=0,i=0;i<n;i+=2,ii++) {
				final double ai=a[ii];
				final double ai1=a[ii+nHalf];
				final int ni=i+nmod+offset;
				for (int k=0; k<coeffs.length; k++) {
					final double[] ck = coeffs[k];
					wksp[n_1 & (ni+k+1)] += ck[0]*ai+ck[1]*ai1;
				}
			}
		} else {
			for (int ii=0,i=0;i<n;i+=2,ii++) {
				final int ni=i+nmod+offset;
				for (int k=0; k<coeffs.length; k++) {
					final double[] ck = coeffs[k];
					final double ak = a[n_1 & (ni+k+1)];
					wksp[ii      ] += ck[0]*ak;
					wksp[ii+nHalf] += ck[1]*ak;
				}
			}
		}
		VectorDouble.COPY(wksp, 0, n, a);
	}

}
