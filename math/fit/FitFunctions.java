/*
 * File Name: FitFunctions.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import function.IFloatFunction;
import function.vector.IFloatVectorFunction;

/**
 * Groups a set of ordinary scalar {@link IFloatFunction}s into a single
 * {@link IFloatVectorFunction} for use in {@link LinearFitDouble}.
 *
 * @author mheuer
 * @version	1.0
 * @see IFloatVectorFunction the interface this implements
 * @see LinearFitDouble a consumer of the resulting vector function
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:11Z
 * digest: a6a7771748f67d956b7a5f53ace75170c904709415ce906620e165b3de0a1c6f
 * stale: false
 * tags: [code/function_composition]
 * concepts: [Fit Function Composition]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class FitFunctions 
//extends AFloatVectorFunction
implements IFloatVectorFunction {
	
	private final IFloatFunction[] fitFunctions;
	
	/** initializing Constructor	 */
	public FitFunctions(final IFloatFunction[] fitFunctions_) {
		this.fitFunctions = fitFunctions_;
	}
	
	/**
	 * Fills {@code yOut} by evaluating each grouped scalar function at {@code x}, one
	 * function per output component.
	 * @see function.vector.IFloatVectorFunction#map(double, double[])
	 */
	public void map(final double x, final double[] yOut) {
		final int min = Math.min(fitFunctions.length, yOut.length); 
		for (int i = min; --i >= 0;) {
			yOut[i] = fitFunctions[i].Map(x); }
	}
	
	/**
	 * Fills {@code yOut} by evaluating each grouped scalar function at {@code x_} (narrowed
	 * to {@code float}), one function per output component.
	 * @see function.vector.IFloatVectorFunction#map(double, float[])
	 */
	public void map(final double x_, final float[] yOut) {
		final float x = (float) x_;
		final int min = Math.min(fitFunctions.length, yOut.length); 
		for (int i = min; --i >= 0;) {
			yOut[i] = fitFunctions[i].Map(x); }
	}
	
}
