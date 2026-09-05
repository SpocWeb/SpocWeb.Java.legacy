/*
 * File Name: TestScalarField.java
 * Created on: 24.01.2004
 *
 */
package math.integration;

import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.vector.IFloatScalarField;

/**
 * Implements a sharp Gaussian test Function, located on the Diagonal at a given Offset,
 * whose Integral over the Unit (Hyper-)Cube should be nearly 1 except when the Offset is
 * positioned close to the Cube's Border.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:51:06Z
 * digest: a53087a650c77c06741378d060a1e167ba85e8660aedc717d93159f5d76fd7cc
 * stale: false
 * tags: [code/numerical_integration]
 * concepts: [Test Scalar Field]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class TestScalarField 
implements IParamScalarField, IFloatScalarField {

	/** Offset of the Gaussian Peak along the Diagonal, in every Dimension. */
	final public float xOff;

	/** Number of Dimensions of the Unit (Hyper-)Cube this Field is defined over. */
	final public int ndim;

	/** The Integral over the Unit Cube in R^n	 */
	final static public int EXPECTED_UNITY_INTEGRAL = 1;

	/**
	 * Constructs the Test Field for the given Peak Offset and Dimension Count.
	 */
	public TestScalarField(final float xOff_, final int ndim_) {
		this.xOff = xOff_;
		this.ndim = ndim_;
	}

	/**
	 * Delegates to {@link #Map(float[])}, ignoring the Parameter.
	 *
	 * @see math.integration.IParamScalarField#map(float[], float)	 */
	public float map(final float[] x, final float param) {
		return Map(x);
	}

	/**
	 * Delegates to {@link #Map(double[])}, ignoring the Parameter.
	 *
	 * @see math.integration.IParamScalarField#map(double[], double)	 */
	public double map(final double[] x, final double param) {
		return Map(x);
	}

	/**
	 * Evaluates the sharp Gaussian test Curve at the given Position.
	 *
	 * @see function.vector.IFloatScalarField#Map(double[])	 */
	public double Map(double[] x) {
		double sum = 0;
		for (int j=1;j<=ndim;j++) {
			sum += (100*ByRefDouble.SQR(x[j]-xOff)); } 
		double ans=(sum < 80 ? Math.exp(-sum) : 0);
		ans *= Math.pow(5.64189, ndim);
		return ans;
	}

	/**
	 * Evaluates the sharp Gaussian test Curve at the given Position.
	 *
	 * @see function.vector.IFloatScalarField#Map(float[])	 */
	public float Map(float[] x) {
		float sum = 0;
		for (int j=1;j<=ndim;j++) {
			sum += (100*ByRefFloat.SQR(x[j]-xOff)); } 
		double ans=(sum < 80 ? Math.exp(-sum) : 0);
		ans *= Math.pow(5.64189, ndim);
		return (float) ans;
	}

}
