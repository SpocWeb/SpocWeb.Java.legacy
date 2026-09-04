/*
 * File Name: FitPolynom.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import function.vector.IFloatVectorFunction;

/**
 * Title: FitPolynom<p>
 * Description:
 * Purpose:
 * Evaluates a Set of Polynoms in one Sweep (Optimization) 
 * used for fitting a Set of Functions to a Data Set. 
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
public class FitPolynom 
implements IFloatVectorFunction {
	
	/** Single Instance of this Function	 */
	final static public FitPolynom SINGLETON = new FitPolynom(); 
	
	/** private Constructor to enforce the Singleton Use	 */ 
	private FitPolynom(){}
	
	/** @see function.vector.IFloatVectorFunction#map(double, double[])	 */
	public void map(final double x, final double[] yOut) {
		yOut[0]=1;
		for (int j=1; j < yOut.length; j++) {
			yOut[j]=yOut[j-1]*x; } 
	}
	
	/** @see function.vector.IFloatVectorFunction#map(double, float[])	 */
	public void map(final double x, final float[] yOut) {
		final float x_ = (float) x; 
		yOut[0]=1;
		for (int j=1; j < yOut.length; j++) {
			yOut[j]=yOut[j-1]*x_; } 
	}
	
}

