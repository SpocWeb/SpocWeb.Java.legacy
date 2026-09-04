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
 * Title: TestScalarField<p>
 * Description:
 * Test Function to integrate over the Unit (Hyper-)Cube: 
 * This is a quite sharp Gaussian Curve with a Width of only 0.01, 
 * located on the Diagonal at the given Offset.  
 * The Integral should be nearly 1, 
 * except when you position the Offset close to the Border of the (Hyper-)Cube.  
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
public class TestScalarField 
implements IParamScalarField, IFloatScalarField {

	final public float xOff; 

	final public int ndim;

	/** The Integral over the Unit Cube in R^n	 */
	final static public int EXPECTED_UNITY_INTEGRAL = 1; 

	/**
	 * 
	 */
	public TestScalarField(final float xOff_, final int ndim_) {
		this.xOff = xOff_;
		this.ndim = ndim_; 
	}

	/** @see math.integration.IParamScalarField#map(float[], float)	 */
	public float map(final float[] x, final float param) {
		return Map(x); 
	}

	/** @see math.integration.IParamScalarField#map(double[], double)	 */
	public double map(final double[] x, final double param) {
		return Map(x); 
	}

	/** @see function.vector.IFloatScalarField#Map(double[])	 */
	public double Map(double[] x) {
		double sum = 0;
		for (int j=1;j<=ndim;j++) {
			sum += (100*ByRefDouble.SQR(x[j]-xOff)); } 
		double ans=(sum < 80 ? Math.exp(-sum) : 0);
		ans *= Math.pow(5.64189, ndim);
		return ans;
	}

	/** @see function.vector.IFloatScalarField#Map(float[])	 */
	public float Map(float[] x) {
		float sum = 0;
		for (int j=1;j<=ndim;j++) {
			sum += (100*ByRefFloat.SQR(x[j]-xOff)); } 
		double ans=(sum < 80 ? Math.exp(-sum) : 0);
		ans *= Math.pow(5.64189, ndim);
		return (float) ans;
	}

}
