/*
 * File Name: FitGauss.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

/**
 * Title: FitGauss<p>
 * Description:
 * Implements a parameterized linear Combination of Gauss Functions
 * Calculates the Sum of several Gauss Functions 
 * with Location, Width and Amplitude in a[], 
 * as well as Derivatives for all Parameters for Fitting.
 *
 * Design Decisions / Implementation Details:
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
public class FitGauss 
implements IFloatFitFunction {

	/** Parameters for the Positions, Widths and Heights of the Gauss Functions. 	*/
	final static public FitGauss SINGLETON = new FitGauss();

	/** Initializing Constructor
	 * @param posWidthHeight_ Parameters for the Positions, Widths and Heights of the Gauss Functions.
	 * Shared by Reference with the Fitting Algorithm. 
	 */
	//public FitGauss(final float[] posWidthHeight_) {}

	/** @see math.fit.IFloatFitFunction#map(double, double[])	 */
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

	/** @see math.fit.IFloatFitFunction#map(double, float[])	 */
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

	/** @see math.fit.IFloatFitFunction#map(double[], double[], double[])	 */
	public double map(double[] x, double[] a, double[] dyda) {
		// TODO Auto-generated method stub
		return 0;
	}

	/** @see math.fit.IFloatFitFunction#map(float[], float[], float[])	 */
	public float map(float[] x, float[] a, float[] dyda) {
		// TODO Auto-generated method stub
		return 0;
	}

}
