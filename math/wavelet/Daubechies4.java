/*
 * File Name: Daubechies4.java
 * Created on: 31.10.2003
 *
 */
package math.wavelet;

import math.vector.VectorDouble;

/**
 * Implements the 4-coefficient Daubechies Wavelet Filter Step (Numerical Recipes 13.10),
 * as a Singleton {@link IWaveletStep}.
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
 * mtime: 2026-09-05T11:52:11Z
 * digest: 587a2f530055a990f0d28d700c4cbbd5c6ccf518807f68de53348ebd1bba81b2
 * stale: false
 * tags: [code/wavelet_transform]
 * concepts: [Daubechies-4 Wavelet Step]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class Daubechies4 implements IWaveletStep {

	/** The single shared Instance of this stateless Filter Step. */
	final static public Daubechies4 SINGLETON = new Daubechies4();

	/** private Singleton Constructor	 */
	private Daubechies4() {}

	/** Normalization Factor shared by all four Filter Coefficients. */
	final static public double C_NORM = Math.sqrt(2)/8;

	/** Square Root of 3, used to derive the four Filter Coefficients. */
	final static public double SQRT3 = Math.sqrt(3);

	/** First Daubechies-4 Filter Coefficient. */
	final static public double C0 = (1+SQRT3)*C_NORM; // 0.4829629131445341;
	/** Second Daubechies-4 Filter Coefficient. */
	final static public double C1 = (3+SQRT3)*C_NORM; // 0.8365163037378079;
	/** Third Daubechies-4 Filter Coefficient. */
	final static public double C2 = (3-SQRT3)*C_NORM; // 0.2241438680420134;
	/** Fourth Daubechies-4 Filter Coefficient. */
	final static public double C3 = (1-SQRT3)*C_NORM; //-0.1294095225512604;

	/**
	 * DAUB4   Daubechies 4-coefficient wavelet filter (13.10)
	 * Wavelet Transform with the Daubechies 4 Coefficients, very fast, Chapter 13.10.7	
	 * @see math.IWaveletStep#wtstep(float[], long, int)
	 */
	final public void wtstep(double[] a, int n, boolean isign) {
		daubechies4Step(a, n, isign);
	}

	/**
	 * Returns this class's fully-qualified name.
	 *
	 * @return a meaningful String Representation for Debugging
	 */
	public String toString() { return getClass().getName(); }

	/**
	 * DAUB4   Daubechies 4-coefficient wavelet filter (13.10)
	 * Wavelet Transform with the Daubechies 4 Coefficients, considerably faster than pwt, Chapter 13.10.7	
	 * @see math.IWaveletStep#wtstep(float[], long, int)
	 */
	final static public void daubechies4Step(final double[] a, final int n, final boolean inverse) {
		if (n < 4) return;
		//local Workspace is Thread-safe, but expensive!
		final double[] wksp = new double[n]; //1..n
		final int n_1= n-1;
		final int nHalf = n >> 1;
		final int nHalfP1= nHalf+1;
		final int nHalf_1= nHalf-1;
		if (inverse) {
			wksp[0] = C2*a[nHalf_1]+C1*a[n_1]+C0*a[0]+C3*a[nHalf];
			wksp[1] = C3*a[nHalf_1]-C0*a[n_1]+C1*a[0]-C2*a[nHalf];
			for (int i=0,j=1; i < nHalf_1; i++) {
				wksp[++j] = C2*a[i]+C1*a[i+nHalf]+C0*a[i+1]+C3*a[i+nHalfP1];
				wksp[++j] = C3*a[i]-C0*a[i+nHalf]+C1*a[i+1]-C2*a[i+nHalfP1];
			}
		} else {
			int i=0,j=0;
			for (; j<=n-4; j+=2,++i) {
				wksp[i      ] = C0*a[j]+C1*a[j+1]+C2*a[j+2]+C3*a[j+3];
				wksp[i+nHalf] = C3*a[j]-C2*a[j+1]+C1*a[j+2]-C0*a[j+3];
			}
			wksp[i      ] = C0*a[n-2]+C1*a[n_1]+C2*a[0]+C3*a[1];
			wksp[i+nHalf] = C3*a[n-2]-C2*a[n_1]+C1*a[0]-C0*a[1];
		}
		VectorDouble.COPY(wksp, 0, n, a);
	}


	/**
	 * DAUB4   Daubechies 4-coefficient wavelet filter (13.10)
	 * Wavelet Transform with the Daubechies 4 Coefficients, considerably faster than pwt, Chapter 13.10.7	
	 * @see math.IWaveletStep#wtstep(float[], long, int)
	 */
	final static public void daubechies4StepOrig(final double[] a, final int n, final boolean inverse) {
		int i,j;
		if (n < 4) return;
		final double[] wksp=new double[n+1]; //1..n
		final int nh = n >> 1;
		final int nhp1= nh+1;
		if (inverse) {
			wksp[1] = C2*a[nh]+C1*a[n]+C0*a[1]+C3*a[nhp1];
			wksp[2] = C3*a[nh]-C0*a[n]+C1*a[1]-C2*a[nhp1];
			for (i=1,j=3;i<nh;i++) {
				wksp[j++] = C2*a[i]+C1*a[i+nh]+C0*a[i+1]+C3*a[i+nhp1];
				wksp[j++] = C3*a[i]-C0*a[i+nh]+C1*a[i+1]-C2*a[i+nhp1];
			}
		} else {
			for (i=1,j=1;j<=n-3;j+=2,i++) {
				wksp[i   ] = C0*a[j]+C1*a[j+1]+C2*a[j+2]+C3*a[j+3];
				wksp[i+nh] = C3*a[j]-C2*a[j+1]+C1*a[j+2]-C0*a[j+3];
			}
			wksp[i   ] = C0*a[n-1]+C1*a[n]+C2*a[1]+C3*a[2];
			wksp[i+nh] = C3*a[n-1]-C2*a[n]+C1*a[1]-C0*a[2];
		}
		for (i=1;i<=n;i++) a[i]=wksp[i];
	}

}
