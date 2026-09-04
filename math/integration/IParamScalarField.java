/*
 * File Name: IParamScalarField.java
 * Created on: 24.01.2004
 *
 */
package math.integration;

import function.vector.IFloatVectorFunction;

/**
 * Title: IParamScalarField<p>
 * Description:
 * Defines the Interface for a Scalar Field parameterized by a scalar Value 
 * Used in the vegas Algorithm to be able to sum up the Weights during Integration. 
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
 */
public interface IParamScalarField {

	/** 
	 * @param x the Position where to evaluate this Field
	 * @param param the Parameter 
	 * @return the Value of a Scalar Field parameterized by a scalar Value
	 */
	public float map(final float[] x, final float param); 

	/** 
	 * @param x the Position where to evaluate this Field
	 * @param param the Parameter 
	 * @return the Value of a Scalar Field parameterized by a scalar Value
	 */
	public double map(final double[] x, final double param); 

}
