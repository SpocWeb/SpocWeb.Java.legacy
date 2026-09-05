/*
 * File Name: BrentFloatMinimizer.java
 * Created on: 03.02.2004
 *
 */
package math.minimizer;

import streamIO.Log;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;

/**
 * Finds the minimum of a one-dimensional function by Brent's method (10.2), without
 * requiring derivative information.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see math.refiner.Brent1FloatMinimizer which uses the Derivative 
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:46:34Z
 * digest: 10a4df5dab48893ab0bbcc6064ac2f8ffe1efc36484c712f910e01176e11b341
 * stale: false
 * tags: [code/minimum_search, code/optimization]
 * concepts: [Brent's Method Minimizer]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class BrentFloatMinimizer 
extends AFloatMinimizer {

	/** Logger for Testing, modify Threshold for switching Logging */
	protected static Log L = new Log(BrentFloatMinimizer.class, 1);

	/** The Tolerance for determining Zeros 	 */
	public static double DEFAULT_TOLERANCE = ByRefDouble.DOUBLE_FULL_ACCURACY; 
	
	////////////////////////////////////////////////////////////////////////////

	double yTry,xTry;
	double xd=0; 
	double xe=0;

	/** The Tolerance for determining Zeros 	 */
	double tolerance; 
	
	/** Empty Constructor 	 */
	public BrentFloatMinimizer() {}
	
	/**Initializer 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 */
	public void init(final double xl_, final IFloatFunction f_) { 
		init(xl_, f_, ByRefDouble.DoubleAccuracy); }
	
	/**Initializer 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 */
	public void init(final double xl_, final double x_, final IFloatFunction f_) { 
		init(xl_, x_, f_, ByRefDouble.DoubleAccuracy); }
	
	/**Initializer 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 */
	public void init(final double xl_, final double x_, final double xr_, final IFloatFunction f_) { 
		init(xl_, x_, xr_, f_, ByRefDouble.DoubleAccuracy); }
	
	/**Initializer 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 * @param f1_ Function returning the Derivative 
	 * @param tol Tolerance for the Root to find
	 */
	public void init(final double xl_, final IFloatFunction f_, final double tol) {
		super.init(xl_, f_);
		initSelf(tol);
	}
	
	/**Initializer 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 * @param f1_ Function returning the Derivative 
	 * @param tol Tolerance for the Root to find
	 */
	public void init(final double xl_, final double x_, final IFloatFunction f_, final double tol) {
		super.init(xl_, x_, f_);
		initSelf(tol);
	}
	
	/**Initializer 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 * @param f1_ Function returning the Derivative 
	 * @param tol Tolerance for the Root to find
	 */
	public void init(final double xl_, final double x_, final double xr_, final IFloatFunction f_, final double tol) {
		super.init(xl_, x_, xr_, f_);
		initSelf(tol);
	}
	
	/** post-Initialization of this Class 	 */
	private void initSelf(final double tol) {
		if (xl > xr) {
			final double xTmp = xl; xl = xr; xr = xTmp; dx = -dx; 
			final double yTmp = yl; yl = yr; yr = yTmp; dy = -dy; 
		}
		this.tolerance = tol; 
		xTry=xTst=xMid;
		yTry=yTst=yMid;
	}
	
	/**initializing Constructor 
	 * 
	 * @param xl_ Border to enclose the Minimum
	 * @param x_  inside the Interval, Border to enclose the Minimum
	 * @param xr_ Border to enclose the Minimum
	 * @param f_ Function returning the Value 
	 * @param f1_ Function returning the Derivative 
	 * @param tol Tolerance for the Root to find
	 */
	public BrentFloatMinimizer(final double xl_, final double x_, final double xr_, final IFloatFunction f_, final double tol) {
		init(xl_, x_, xr_, f_, tol); 
	}
	
	/**	
	 * find minimum of a function using derivative information (10.3)		
	 * Brent's Algorithm to calculate the Minimum in one Dimension, Chapter 10.2
	 * 
	 * @return the best Ordinate so far 
	 */
	public double refine() {
		return refine(tolerance); }
	
	/**	
	 * find minimum of a function using derivative information (10.3)		
	 * Brent's Algorithm to calculate the Minimum in one Dimension, Chapter 10.2
	 * @param tol The Tolerance to use in x-Direction 
	 * @return the best Ordinate (x-Value) for the Minimum so far 
	 */
	public double refine(final double tol) {
		L.n("xl=").l(xl).l(" xMid=").l(xMid).l(" xr=").l(xr);
		final double xm=0.5*(xl+xr);
		final double tol1=tol*(Math.abs(xl)+Math.abs(xr))+ByRefDouble.DOUBLE_FULL_ACCURACY; 
		//final double tol2=tol1+tol1;
		
		/////////////////////////////////////////////////////////////
		// determine a new Trial Point
		//if (Math.abs(xMid-xm) <= (tol2-0.5*(xr-xl))) {
		//	return xMid; } //also stop the Loop then!
		if (Math.abs(xe) <= tol1) { //golden Step
			goldenStep(xm);
		} else {//try a parabolic Fit
			final double r=(xMid-xTry)*(yMid-yTst);
			double q=(xMid-xTst)*(yMid-yTry);
			double p=(xMid-xTst)*q-(xMid-xTry)*r;
			q=2*(q-r);
			if (q > 0) {
				p =-p; 
			} else {
				q =-q;  
			}
			final double eTemp=xe; xe=xd;
			if (Math.abs(p) >= Math.abs(0.5*q*eTemp) || p <= q*(xl-xMid) || p >= q*(xr-xMid))
				goldenStep(xm);
			else {
				xd=p/q;
				/*
				final double xNew=xMid+xd;
				if ((xNew-xl < tol2) || (xr-xNew < tol2)) {
					xd=ByRefDouble.assignSign(tol1, xm-xMid); }
				*/ 
			}
		}
		
		/////////////////////////////////////////////////////////////
		//make a significant Change! 
		final double xNew=xMid+xd; //=(Math.abs(xd) >= tol1 ? xMid+xd : xMid+ByRefDouble.assignSign(tol1,xd));
		final double yNew=f.Map(xNew); //single Evaluation per Refinement
		if (yNew <= yMid) { //Improvement
			if(xNew>= xMid) {
				xl  = xMid;
			} else {
				xr  = xMid;
			} 
			xTst=xTry; yTst=yTry; 
			xTry=xMid; yTry=yMid; 
			xMid=xNew; yMid=yNew;
		} else { //no Improvement
			if (xMid > xNew) {
				xl   = xNew; 
			} else {
				xr   = xNew;
			} 
			if (yNew <= yTry || xTry == xMid) {
				xTst=xTry; yTst=yTry;
				xTry=xNew; yTry=yNew;
			} else if (yNew <= yTst || xTst == xMid || xTst == xTry) {
				xTst=xNew; yTst=yNew;
			}
		}
		return xMid;
	}

	private void goldenStep(final double xm) {
		xe=(xMid >= xm ? xl-xMid : xr-xMid); 
		xd=IMeasurAble.CGOLDEN*xe;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//Testing single Step of Pegasus Step with Quality Control
		L.n("Testing ").l(BrentFloatMinimizer.class);
		L.n("Searching for the Minimum of y = ").l(TEST_FUNCTION);
		final double xLeft  = 0;
		final double xMid   = 3;
		final double xRight = 6;
		final BrentFloatMinimizer minStep = new BrentFloatMinimizer(xLeft, xMid, xRight, TEST_FUNCTION, 1e-16);
		TEST_REFINER(minStep, TEST_MIN_POINT, 11);
		L.n("xMin=").l(minStep.xMid).l("yMin=").l(minStep.yMid); 
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}