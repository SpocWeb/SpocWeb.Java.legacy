/*
 * File Name: GoldenFloatMinimizer.java
 * Created on: 03.02.2004
 *
 */
package math.minimizer;

import streamIO.Log;
import function.IFloatFunction;
import function.IMeasurAble;

/**
 * Title: GoldenFloatMinimizer<p>
 * Description:
 * Find a Minimum of a continuous, 1-dim. Function. 
 * The Function doesn't need to be differentiable, only continuous! 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * 
 * similar Classes: 
 * @see math.minimizer.BrentFloatMinimizer which assumes Differentiability 
 * @see math.minimizer.Brent1FloatMinimizer which explicitly uses the Derivative
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class GoldenFloatMinimizer 
extends AFloatMinimizer {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(GoldenFloatMinimizer.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer for the evaluation of the new Test Point	 */
	private boolean testForLess;
	
	public void init(final double xl_, final double x_, final double xr_, final IFloatFunction f_) {
		super.init(xl_, x_, xr_, f_); 
		final double rInt = xr - x_;
		final double lInt = x_ - xl;  
		if ((rInt > lInt) != (x_ < xl)) { //xMid liegt näher bei xl  // == xr+golden(xMid-xr) //{neuer Punkt wird ermittelt}
			xMid = x_; dx = (xr - xMid)*IMeasurAble.CGOLDEN; xTst = xMid; xTst += dx;				yTst = f.Map(xTst); 
		} else { 	// == xl+golden(xTst-xl)
			xTst = x_; dx = (xl - xTst)*IMeasurAble.CGOLDEN; xMid = xTst; xMid += dx; yTst = yMid;	yMid = f.Map(xMid); 
		}
		testForLess = ((yTst < yMid) != maximize);	//{Func braucht NIE wieder an den urspruenglichen Endpunkten ausgewertet zu werden !}
	}
	
	/** empty Constructor 	 */
	public GoldenFloatMinimizer() {}
	
	/** initializing Constructor; 
	 * the Minimum MUST be bracketed! 
	 * @param xl f(xl)>f(x) 
	 * @param x 
	 * @param xr f(xr)>f(x)  
	 * @param f_ 
	 */
	public GoldenFloatMinimizer(final double xl, final double x, final double xr, final IFloatFunction f_) {
		init(xl, x, xr, f_);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Auswertung von f am neuen Punkt	
	 * @return the best Ordinate for the Minimum so far 
	 */
	public double refine() { //
		double tmp;
		if (testForLess) { //{sonst : Wahl des neuen Intervalles}
			tmp = xl; xl = xMid; xMid = xTst; xTst = tmp; dx = (xr - xMid)*IMeasurAble.CGOLDEN; xTst = xMid + dx; yl = yMid; yMid = yTst; yTst = f.Map(xTst); 
		} else {
			tmp = xr; xr = xTst; xTst = xMid; xMid = tmp; dx = (xl - xTst)*IMeasurAble.CGOLDEN; xMid = xTst + dx; yr = yTst; yTst = yMid; yMid = f.Map(xMid); 
		}
		//Besseren Endwert ausgeben
		if (testForLess = ((yTst < yMid) != maximize)) {
			return xTst;
		} 	return xMid; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//Testing single Step of Pegasus Step with Quality Control
		L.n("Testing ");
		L.n("Searching for the Minimum of y = ").l(TEST_FUNCTION);
		final double xLeft  = 0;
		final double xMid   = 3;
		final double xRight = 6;
		final GoldenFloatMinimizer minStep = new GoldenFloatMinimizer(xLeft, xMid, xRight, TEST_FUNCTION);
		TEST_REFINER(minStep, TEST_MIN_POINT, 36);
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
