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
 * Combines an array of scalar fields into a single {@link IFloatVectorField}, mapping each
 * component of the output vector through its own {@link IFloatScalarField}.
 *
 * @author mheuer
 * @version	1.0
 * @see IFloatVectorField the interface this implements
 * @see IFloatScalarField each output component's own field
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:46:34Z
 * digest: 2303fcb1ec640b5f55df581b76f97e4e19cf68f54820afea782d1160593bceaa
 * stale: false
 * tags: [code/function_composition]
 * concepts: [Vector Field Fit Composition]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
public class FitFields
extends AFloatVectorField
implements IFloatVectorField {

	private final IFloatScalarField[] fitFunctions;

	/** initializing Constructor	 */
	public FitFields(final IFloatScalarField[] fitFunctions_) {
		this.fitFunctions = fitFunctions_;
	}

	// TODO: LOGIC: always returns null instead of the populated yOut array, breaking the
	// IFloatVectorField#map(double[], double[]) contract for any caller that uses the return
	// value rather than only the yOut out-parameter.
	/**
	 * Fills {@code yOut} by evaluating each scalar field at {@code x}, one field per output
	 * component.
	 * @see function.vector.IFloatVectorField#map(double[], double[])
	 */
	public double[] map(double[] x, double[] yOut) {
		final int min = Math.min(fitFunctions.length, yOut.length);
		for (int i = min; --i >= 0;) {
			yOut[i] = fitFunctions[i].Map(x); }
		return null;
	}

	// TODO: LOGIC: always returns null instead of the populated yOut array, breaking the
	// IFloatVectorField#map(float[], float[]) contract for any caller that uses the return
	// value rather than only the yOut out-parameter.
	/**
	 * Fills {@code yOut} by evaluating each scalar field at {@code x}, one field per output
	 * component.
	 * @see function.vector.IFloatVectorField#map(float[], float[])
	 */
	public float[] map(float[] x, float[] yOut) {
		final int min = Math.min(fitFunctions.length, yOut.length);
		for (int i = min; --i >= 0;) {
			yOut[i] = fitFunctions[i].Map(x); }
		return null;
	}

}
