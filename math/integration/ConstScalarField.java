/*
 * File Name: ConstScalarField.java
 * Created on: 24.01.2004
 *
 */
package math.integration;

import function.vector.IFloatScalarField;

/**
 * Title: ConstScalarField<p>
 * Description:
 * Implements a constant Scalar Field
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
 */
public class ConstScalarField 
implements IParamScalarField, IFloatScalarField {

	final public double value; 

	/**
	 * 
	 */
	public ConstScalarField(final double value_) {
		this.value = value_; 
	}

	/** @see math.integration.IParamScalarField#map(float[], float)	 */
	public float map(float[] x, float param) {
		return (float) value;
	}

	/** @see math.integration.IParamScalarField#map(double[], double)	 */
	public double map(double[] x, double param) {
		return value;
	}

	/** @see function.vector.IFloatScalarField#Map(double[])	 */
	public double Map(double[] v) {
		return value;
	}

	/** @see function.vector.IFloatScalarField#Map(float[])	 */
	public float Map(float[] v) {
		return (float) value;
	}

}
