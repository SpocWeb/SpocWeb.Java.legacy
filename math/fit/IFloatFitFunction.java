/*
 * File Name: IFloatFitFunction.java
 * Created on: 21.02.2004
 *
 */
package math.fit;

/**
 * Title: IFloatFitFunction<p>
 * Description:
 * Defines the Interface for a parameterized Function
 * that shares the Parameter state with the Fitting Algorithm
 * and returns both the Function Value 
 * and the Derivatives to the different Parameters with one Call.  
 *
 * Design Decisions / Implementation Details:
 * A new Interface has to be defined, 
 * because continuous Synchronization of Fitting Parameters 
 * with internal Function Parameters is error prone. 
 * Additionally the Functions could interact with each other non-linearly 
 * unlike in the simple Case of additive Gauss or Lorentz Functions.
 * To make coupling more obvious, the Parameters are handed over explicitly, 
 * although FitFloat always uses the same Array and thus could share the State.   
 *
 * Known SubClasses: <none>
 *
 * Known Implementations: 
 * @see math.fit.FitGauss
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface IFloatFitFunction {

	/** 
	 * @param x the Argument to evaluate the Function for 
	 * @param dyda filled with the 1st Derivative of the Value to the internal Parameters 
	 * @return the Function Value y
	 */
	double map(double x, double[] a, double[] dyda); 

	/** 
	 * @param x the Argument to evaluate the Function for 
	 * @param dyda filled with the 1st Derivative of the Value to the internal Parameters 
	 * @return the Function Value y
	 */
	float map(double x, float[] a, float[] dyda); 

	/** 
	 * @param x the Argument to evaluate the Function for 
	 * @param dyda filled with the 1st Derivative of the Value to the internal Parameters 
	 * @return the Function Value y
	 */
	double map(double[] x, double[] a, double[] dyda); 

	/** 
	 * @param x the Argument to evaluate the Function for 
	 * @param dyda filled with the 1st Derivative of the Value to the internal Parameters 
	 * @return the Function Value y
	 */
	float map(float[] x, float[] a, float[] dyda); 

}
