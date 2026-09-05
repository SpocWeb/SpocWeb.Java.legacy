/*
 * File Name: ConstScalarField.java
 * Created on: 24.01.2004
 *
 */
package math.integration;

import function.vector.IFloatScalarField;

/**
 * Implements a Scalar Field that returns the same constant Value at every Position.
 *
 * Known Uses:
 * @see math.integration.AdaptiveMCIntegrator
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:50:35Z
 * digest: dbadb7c4b1fd50cfc670cff0ccae97b3e847cbcac221d464a9cbd35ab791ea45
 * stale: false
 * tags: [code/numerical_integration]
 * concepts: [Constant Scalar Field]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class ConstScalarField 
implements IParamScalarField, IFloatScalarField {

	/** The constant Value returned for every Position, independent of Parameter. */
	final public double value;

	/**
	 * Constructs a Scalar Field that returns the given constant Value everywhere.
	 */
	public ConstScalarField(final double value_) {
		this.value = value_;
	}

	/**
	 * Returns {@link #value} regardless of Position or Parameter.
	 *
	 * @see math.integration.IParamScalarField#map(float[], float)	 */
	public float map(float[] x, float param) {
		return (float) value;
	}

	/**
	 * Returns {@link #value} regardless of Position or Parameter.
	 *
	 * @see math.integration.IParamScalarField#map(double[], double)	 */
	public double map(double[] x, double param) {
		return value;
	}

	/**
	 * Returns {@link #value} regardless of Position.
	 *
	 * @see function.vector.IFloatScalarField#Map(double[])	 */
	public double Map(double[] v) {
		return value;
	}

	/**
	 * Returns {@link #value} regardless of Position.
	 *
	 * @see function.vector.IFloatScalarField#Map(float[])	 */
	public float Map(float[] v) {
		return (float) value;
	}

}
