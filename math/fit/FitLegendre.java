/*
 * File Name: FitLegendre.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import function.vector.IFloatVectorFunction;

/**
 * Title: FitLegendre<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
class FitLegendre 
implements IFloatVectorFunction {
	
	/** Single Instance of this Function	 */
	final static public FitLegendre SINGLETON = new FitLegendre(); 
	
	/** private Constructor to enforce the Singleton Use	 */ 
	private FitLegendre(){}
	
	/** @see function.vector.IFloatVectorFunction#map(double, double[])	 */
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
	
	/** @see function.vector.IFloatVectorFunction#map(double, float[])	 */
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
