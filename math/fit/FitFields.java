/*
 * File Name: FitFields.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

import function.vector.AFloatVectorField;
import function.vector.IFloatScalarField;
import function.vector.IFloatVectorField;

/**
 * Title: FitFields<p>
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
public class FitFields 
extends AFloatVectorField
implements IFloatVectorField {
	
	private final IFloatScalarField[] fitFunctions;
	
	/** initializing Constructor	 */
	public FitFields(final IFloatScalarField[] fitFunctions_) {
		this.fitFunctions = fitFunctions_;
	}
	
	/** @see function.vector.IFloatVectorField#map(double[], double[])	 */
	public double[] map(double[] x, double[] yOut) {
		final int min = Math.min(fitFunctions.length, yOut.length); 
		for (int i = min; --i >= 0;) {
			yOut[i] = fitFunctions[i].Map(x); }
		return null;
	}

	/** @see function.vector.IFloatVectorField#map(float[], float[])	 */
	public float[] map(float[] x, float[] yOut) {
		final int min = Math.min(fitFunctions.length, yOut.length); 
		for (int i = min; --i >= 0;) {
			yOut[i] = fitFunctions[i].Map(x); }
		return null;
	}

}
