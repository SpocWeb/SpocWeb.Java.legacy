/*
 * File Name: IFloatFitFunction.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

/**
 * Defines a parameterized fitting function that shares its parameter state with the fitting
 * algorithm and returns both the function value and the derivatives to each parameter in one
 * call.
 *
 * <p>A dedicated interface is needed because continuous synchronization of fitting
 * parameters with internal function parameters is error prone, and functions could interact
 * with each other non-linearly, unlike the simple case of additive Gauss or Lorentz
 * functions. To make coupling more obvious, parameters are handed over explicitly, although
 * {@link FitFloat} always uses the same array and thus could share the state.
 *
 * Known Implementations:
 * @see FitGauss
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:46:51Z
 * digest: 2e4adb36d517b1e0b6e00881f607457dd898afee435e0456f394b8c1aa020166
 * stale: false
 * tags: [code/function_interface]
 * concepts: [Fit Function Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IFloatFitFunction {

	/**
	 * Evaluates the function at {@code x} and fills {@code dyda} with the 1st derivative of
	 * the value to each internal parameter.
	 * @param x the Argument to evaluate the Function for
	 * @param dyda filled with the 1st Derivative of the Value to the internal Parameters
	 * @return the Function Value y
	 */
	double map(double x, double[] a, double[] dyda);

	/**
	 * Evaluates the function at {@code x} and fills {@code dyda} with the 1st derivative of
	 * the value to each internal parameter.
	 * @param x the Argument to evaluate the Function for
	 * @param dyda filled with the 1st Derivative of the Value to the internal Parameters
	 * @return the Function Value y
	 */
	float map(double x, float[] a, float[] dyda);

	/**
	 * Evaluates the function at each element of {@code x} and fills {@code dyda} with the
	 * 1st derivative of the value to each internal parameter.
	 * @param x the Argument to evaluate the Function for
	 * @param dyda filled with the 1st Derivative of the Value to the internal Parameters
	 * @return the Function Value y
	 */
	double map(double[] x, double[] a, double[] dyda);

	/**
	 * Evaluates the function at each element of {@code x} and fills {@code dyda} with the
	 * 1st derivative of the value to each internal parameter.
	 * @param x the Argument to evaluate the Function for
	 * @param dyda filled with the 1st Derivative of the Value to the internal Parameters
	 * @return the Function Value y
	 */
	float map(float[] x, float[] a, float[] dyda);

}
