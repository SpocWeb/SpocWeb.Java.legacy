/*
 * File Name: FitPolynom.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import function.vector.IFloatVectorFunction;

/**
 * Evaluates the successive powers of {@code x} (1, x, x^2, ...) in one sweep, used as a
 * polynomial basis for fitting a set of functions to a data set.
 *
 * @author mheuer
 * @version	1.0
 * @see IFloatVectorFunction the interface this implements
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:57Z
 * digest: 055de3aec6a3bbab4c789b6995dd01961ff94a8775bc31046e88f81d7c9fae87
 * stale: false
 * tags: [code/curve_fitting]
 * concepts: [Polynomial Basis Fit]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FitPolynom 
implements IFloatVectorFunction {
	
	/** Single Instance of this Function	 */
	final static public FitPolynom SINGLETON = new FitPolynom(); 
	
	/** private Constructor to enforce the Singleton Use	 */ 
	private FitPolynom(){}
	
	/**
	 * Fills {@code yOut} with the successive powers of {@code x}: 1, x, x^2, ....
	 * @see function.vector.IFloatVectorFunction#map(double, double[])
	 */
	public void map(final double x, final double[] yOut) {
		yOut[0]=1;
		for (int j=1; j < yOut.length; j++) {
			yOut[j]=yOut[j-1]*x; } 
	}
	
	/**
	 * Fills {@code yOut} with the successive powers of {@code x} (narrowed to {@code float}):
	 * 1, x, x^2, ....
	 * @see function.vector.IFloatVectorFunction#map(double, float[])
	 */
	public void map(final double x, final float[] yOut) {
		final float x_ = (float) x; 
		yOut[0]=1;
		for (int j=1; j < yOut.length; j++) {
			yOut[j]=yOut[j-1]*x_; } 
	}
	
}

