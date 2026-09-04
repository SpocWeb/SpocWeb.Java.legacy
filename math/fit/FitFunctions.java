/*
 * File Name: FitFunctions.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import function.IFloatFunction;
import function.vector.IFloatVectorFunction;

/**
 * Title: FitFunctions<p>
 * Description:
 * Helper Function to group a Set of ordinary Fitting Functions 
 * into a VectorFunction to use in LinearFitDouble.  
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
public class FitFunctions 
//extends AFloatVectorFunction
implements IFloatVectorFunction {
	
	private final IFloatFunction[] fitFunctions;
	
	/** initializing Constructor	 */
	public FitFunctions(final IFloatFunction[] fitFunctions_) {
		this.fitFunctions = fitFunctions_;
	}
	
	/** @see function.vector.IFloatVectorFunction#map(double, double[])	 */
	public void map(final double x, final double[] yOut) {
		final int min = Math.min(fitFunctions.length, yOut.length); 
		for (int i = min; --i >= 0;) {
			yOut[i] = fitFunctions[i].Map(x); }
	}
	
	/** @see function.vector.IFloatVectorFunction#map(double, float[])	 */
	public void map(final double x_, final float[] yOut) {
		final float x = (float) x_;
		final int min = Math.min(fitFunctions.length, yOut.length); 
		for (int i = min; --i >= 0;) {
			yOut[i] = fitFunctions[i].Map(x); }
	}
	
}
