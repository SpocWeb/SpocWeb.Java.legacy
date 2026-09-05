/*
 * File Name: IParamScalarField.java
 * Created on: 24.01.2004
 *
 */
package math.integration;

import function.vector.IFloatVectorFunction;

/**
 * Defines a Scalar Field parameterized by an extra scalar Value, used by the vegas
 * Algorithm to be able to sum up the Weights during Integration.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: 
 * @see math.integration.AdaptiveMCIntegrator#vegas(float[], int, IFloatVectorFunction, int, long, int, int, float[])
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:50:43Z
 * digest: 68b0577f28af13b3332d4e1d6f111e8b1c0a840bc1e497e3c82c0217e710dd2f
 * stale: false
 * tags: [code/numerical_integration]
 * concepts: [Parameterized Scalar Field Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IParamScalarField {

	/**
	 * Evaluates this Field at the given Position under the given scalar Parameter.
	 *
	 * @param x the Position where to evaluate this Field
	 * @param param the Parameter
	 * @return the Value of a Scalar Field parameterized by a scalar Value
	 */
	public float map(final float[] x, final float param);

	/**
	 * Evaluates this Field at the given Position under the given scalar Parameter.
	 *
	 * @param x the Position where to evaluate this Field
	 * @param param the Parameter
	 * @return the Value of a Scalar Field parameterized by a scalar Value
	 */
	public double map(final double[] x, final double param);

}
