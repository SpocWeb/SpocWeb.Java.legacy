/*
 * File Name: FitLegendre.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import function.vector.IFloatVectorFunction;

/**
 * Evaluates the Legendre polynomials up to the requested degree at a point {@code x}, using
 * the standard three-term recurrence, for use as a fitting basis.
 *
 * @author mheuer
 * @version	1.0
 * @see IFloatVectorFunction the interface this implements
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:51Z
 * digest: 2f07d4bc12d7e64e816f390e376eae776816e02c87eefe4192af9a0c78e9cc8c
 * stale: false
 * tags: [code/curve_fitting]
 * concepts: [Legendre Polynomial Basis Fit]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
class FitLegendre 
implements IFloatVectorFunction {
	
	/** Single Instance of this Function	 */
	final static public FitLegendre SINGLETON = new FitLegendre(); 
	
	/** private Constructor to enforce the Singleton Use	 */ 
	private FitLegendre(){}
	
	/**
	 * Fills {@code yOut} with the Legendre polynomials P0..P(n-1) evaluated at {@code x}.
	 * @see function.vector.IFloatVectorFunction#map(double, double[])
	 */
	public void map(final double x, final double[] yOut) {
		yOut[0]=1;
		yOut[1]=x;
		if (yOut.length <= 2) {
			return; }
		final double twox=2*x;
		double f2=x;
		int d=1;
		for (int j=2; j < yOut.length; j++) {
			final int d_1=d++;
			f2 += twox;
			yOut[j]=(f2*yOut[j-1]-d_1*yOut[j-2])/d;
		}
	}
	
	/**
	 * Fills {@code yOut} with the Legendre polynomials P0..P(n-1) evaluated at {@code x}
	 * (narrowed to {@code float}).
	 * @see function.vector.IFloatVectorFunction#map(double, float[])
	 */
	public void map(final double x, final float[] yOut) {
		final float x_ = (float) x;
		yOut[0]=1;
		yOut[1]=x_;
		if (yOut.length <= 2) {
			return; }
		final float twox=2*x_;
		float f2=x_;
		int d=1;
		for (int j=2; j < yOut.length; j++) {
			final int d_1=d++;
			f2 += twox;
			yOut[j]=(f2*yOut[j-1]-d_1*yOut[j-2])/d;
		}
	}
	
}
