/*
 * File Name: FitGauss.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

/**
 * Implements a parameterized linear combination of Gauss functions, calculating the sum of
 * several Gauss functions with location, width and amplitude packed into {@code a[]}, plus
 * the derivative of that sum for every parameter, for use by a fitting algorithm.
 *
 * @author mheuer
 * @version	1.0
 * @see IFloatFitFunction the interface this implements
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:43Z
 * digest: 2ec33d51a02c429f3c3ed777346631bbff111bb425fc8656d2c5a195a69c3837
 * stale: false
 * tags: [code/curve_fitting]
 * concepts: [Gaussian Basis Function Fit]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class FitGauss
implements IFloatFitFunction {

	/** Parameters for the Positions, Widths and Heights of the Gauss Functions. 	*/
	final static public FitGauss SINGLETON = new FitGauss();

	/** Initializing Constructor
	 * @param posWidthHeight_ Parameters for the Positions, Widths and Heights of the Gauss Functions.
	 * Shared by Reference with the Fitting Algorithm. 
	 */
	//public FitGauss(final float[] posWidthHeight_) {}

	/**
	 * Returns the sum of the Gauss functions encoded in {@code posWidthHeight} at {@code x},
	 * filling {@code dyda} with the derivative to each position/width/height parameter
	 * unless {@code dyda} is {@code null}.
	 * @see IFloatFitFunction#map(double, double[], double[])
	 */
	public double map(final double x, final double[] posWidthHeight, final double[] dyda) {
		double y=0;
		for (int i=0; i<posWidthHeight.length; i+=3) {
			final double arg=(x-posWidthHeight[i+1])/posWidthHeight[i+2];
			final double exp=Math.exp(-arg*arg);
			final double fac=posWidthHeight[i]*exp*2*arg;
			y += posWidthHeight[i]*exp;
			if (dyda == null) {
				continue; }
			dyda[i]=exp;
			dyda[i+1]=fac/posWidthHeight[i+2];
			dyda[i+2]=fac*arg/posWidthHeight[i+2];
		}
		return y;
	}

	/**
	 * Returns the sum of the Gauss functions encoded in {@code posWidthHeight} at {@code x_}
	 * (narrowed to {@code float}), filling {@code dyda} with the derivative to each
	 * position/width/height parameter unless {@code dyda} is {@code null}.
	 * @see IFloatFitFunction#map(double, float[], float[])
	 */
	public float map(final double x_, final float[] posWidthHeight, final float[] dyda) {
		final float x = (float) x_;
		float y=0;
		for (int i=0; i<posWidthHeight.length; i+=3) {
			final float arg=(x-posWidthHeight[i+1])/posWidthHeight[i+2];
			final float exp=(float) Math.exp(-arg*arg);
			final float fac=posWidthHeight[i]*exp*2*arg;
			y += posWidthHeight[i]*exp;
			if (dyda == null) {
				continue; }
			dyda[i]=exp;
			dyda[i+1]=fac/posWidthHeight[i+2];
			dyda[i+2]=fac*arg/posWidthHeight[i+2];
		}
		return y;
	}

	// TODO: LOGIC: unimplemented stub that always returns 0 without evaluating anything or
	// filling dyda, unlike the single-x overload above; any caller relying on the
	// IFloatFitFunction#map(double[], double[], double[]) contract for a vector x silently
	// gets a wrong (zero) result instead of an error.
	/**
	 * Not implemented: always returns {@code 0} without evaluating the Gauss functions or
	 * filling {@code dyda}.
	 * @see IFloatFitFunction#map(double[], double[], double[])
	 */
	public double map(double[] x, double[] a, double[] dyda) {
		// TODO Auto-generated method stub
		return 0;
	}

	// TODO: LOGIC: unimplemented stub that always returns 0 without evaluating anything or
	// filling dyda, unlike the single-x overload above; any caller relying on the
	// IFloatFitFunction#map(float[], float[], float[]) contract for a vector x silently gets
	// a wrong (zero) result instead of an error.
	/**
	 * Not implemented: always returns {@code 0} without evaluating the Gauss functions or
	 * filling {@code dyda}.
	 * @see IFloatFitFunction#map(float[], float[], float[])
	 */
	public float map(float[] x, float[] a, float[] dyda) {
		// TODO Auto-generated method stub
		return 0;
	}

}
