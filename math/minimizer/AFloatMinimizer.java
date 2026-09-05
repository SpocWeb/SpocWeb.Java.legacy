/*
 * File Name: AFloatMinimizer.java
 * Created on: 31.01.2004
 *
 */
package math.minimizer;

import math.refiner.SecantFloatRefiner;
import streamIO.Assert;
import streamIO.Log;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;

/**
 * Base class that brackets and iteratively refines the local minimum of a one-dimensional
 * function, in the manner of the golden rule: {@code xl------xm--xt------xr}.
 *
 * <p>The Idea is to calculate a Test Point in the larger Interval
 * and to compare it's Function Value to the current Minimum Estimation.
 * If it is smaller, it is chosen as the new MidPoint,
 * else the old one is kept, but the right border is set to the Test Point
 * (because it fulfills the inequation below).
 * The Points are chosen so that the Interval is of same Size in both cases:
 * (xr-xMid) == (xTst-xl)
 * Let's assume, the Algorithm is settled,
 * so that the golden Ratios are realized, then the following is true:
 * (xr-xMid) = g*(xr-xl) and (xMid-xl) == g*(xr-xMid)
 * (the Ratio of the smaller Interval to the larger one is the same as
 *	the Ratio of the larger  Interval to the Sum of both Intervals.)
 * With this is (xTst-xl) == (xr-xMid) == g*(xr-xl)
 *
 * The Reason why the Minimum is searched for is
 * that very often Funtion are positive definite and unlimited (see x^2).
 * To find the Maximum, just negate the Function.
 *
 * Prerequisites:
 * f: R -> R is continuous, but not necessarily differentiable.
 * Minimum is 'bracketed' in xTst > x > xMid so that yMid > y < yTst
 *
 * This is the Equivalent to the BiSection Algorithm in Zero Finding.
 * Very robust, but only linearly converging.  
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see streamIO.copy.group.ring.metric.GoldenMinimizer
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:46:26Z
 * digest: b079ad63abb3bf5004b508d8f5eaa8c2ac8cf7339b17bbd1bc5462195524b9f3
 * stale: false
 * tags: [code/minimum_search, code/bracket_matching, code/optimization]
 * concepts: [Numerical Optimization]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public abstract class AFloatMinimizer 
extends SecantFloatRefiner {	//ARefinerQ {	//swaps the Points unnecessarily!
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(AFloatMinimizer.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Limit for Extrapolation	 */
	final static public double LIMIT = 100; 

	/**  bracket the minimum of a function (10.1)
	 * 
	 * @param x [0..2] in/out Positions bracketing the Minimum; 
	 * only x[0] and x[1] need to be given. 
	 * @param func the Function to bracket
	 * @return the Function Values at the 3 bracket Ordinates
	 */
	final static public double[] BRACKET(final double[] x, final IFloatFunction func) {
		return BRACKET(x, func, null); }

	/**  bracket the minimum of a function (10.1)
	 * 
	 * @param x [0..2] in/out Positions bracketing the Minimum; 
	 * only x[0] and x[1] need to be given. 
	 * @param func the Function to bracket
	 * @param y [0..2] the Return Array for the Ordinates
	 * @return the Function Values at the 3 bracket Ordinates
	 */
	final static public double[] BRACKET(final double[] x, final IFloatFunction func
	, double[] y) {
		if (y == null) { //if the Size is not sufficient, rather let an ArrayOutOfBoundsException fly
			y = new double[3]; }
		y[0]=func.Map(x[0]);
		y[1]=func.Map(x[1]);
		if (y[1] > y[0]) {
			final double tmpX = x[0]; x[0] = x[1]; x[1] = tmpX; 
			final double tmpY = y[0]; y[0] = y[1]; y[1] = tmpY; 
		}
		x[2]=x[1]+IMeasurAble.ONEGOLDEN*(x[1]-x[0]);
		y[2]=func.Map(x[2]);
		while (y[1] > y[2]) {
			final double x1_x0 = x[1]-x[0];
			final double x1_x2 = x[1]-x[2];  
			final double r=x1_x0*(y[1]-y[2]);
			final double q=x1_x2*(y[1]-y[0]);
			double yTry; //TODO: the Quotient is not well defined. 
			double xTry=x[1]-((x1_x2)*q-(x1_x0)*r)/ 
				(2*ByRefDouble.ASSIGN_SIGN(Math.max(Math.abs(q-r),ByRefDouble.DOUBLE_FULL_ACCURACY),q-r));
			final double xLimit=x[1]+LIMIT*(x[2]-x[1]);
			if ((x[1]>xTry) == (xTry > x[2])) {
				yTry=func.Map(xTry);
				if (yTry < y[2]) {
					x[0]=x[1]; x[1]=xTry;
					y[0]=y[1]; y[1]=yTry;
					return y; } 
				if (yTry > y[1]) {
					x[2]=xTry;
					y[2]=yTry;
					return y; }
				xTry=x[2]+IMeasurAble.ONEGOLDEN*(x[2]-x[1]);
				yTry=func.Map(xTry);
			} else if ((x[2] > xTry) == (xTry > xLimit)) {
				yTry=func.Map(xTry);
				if (yTry < y[2]) {
					x[1] = x[2]; x[2]= xTry; xTry=x[2]+IMeasurAble.ONEGOLDEN*(x[2]-x[1]);
					y[1] = y[2]; y[2]= yTry; yTry=func.Map(xTry);
				}
			} else if ((xTry > xLimit) == (xLimit > x[2])) {
				yTry=func.Map(xTry=xLimit);
			} else {
				xTry=x[2]+IMeasurAble.ONEGOLDEN*(x[2]-x[1]);
				yTry=func.Map(xTry);
			}
			x[0] = x[1]; x[1]=x[2]; x[2]=xTry;
			y[0] = y[1]; y[1]=y[2]; y[2]=yTry;
		}
		return y;
	}
	
	/**Middle Point x Value	 */	protected double xMid;
	/**Middle Point y Value	 */	protected double yMid;
	/**Test Point x Value	 */	protected double xTst;
	/**Test Point y Value	 */	protected double yTst;
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Switches on Searching for the Maximum instead of the Minimum	 */
	public boolean maximize;
	
	/**Initializes the Stepper.
	 * @see #BRACKET(double[], IFloatFunction) calculates the middle Point, since none is chosen 
	 * (and hopefully smaller/larger than xl and xr)	 
	 */
	public void init(final double xl_, final double xr_, final IFloatFunction f_) {
		final double[] x = {xl_, xr_, 0};
		final double[] y = new double[3]; 
		BRACKET(x, f_, y);
		this.xl  = x[0]; 
		this.yl  = y[0]; 
		this.xr  = x[2]; 
		this.yr  = y[2]; 
		xTst=xMid= x[1];
		yTst=yMid= y[1]; 
		this.f = f_; 
		//init(xl_, (xl_+xr_)*.5, xr_, f_); 
	}
	
	/**Initializes the Stepper	 */
	public void init(final double xl_, double x_, final double xr_, final IFloatFunction f_) {
		super.init(xl_, xr_, f_);	//Evaluation of f at xl and xr not necessary, but used to verify bracketing!
		xMid = x_;
		yMid = f.Map(x_); 
		maximize = yMid > yl; 
		if ((yMid > yr) != maximize) { 
			throw new AbstractMethodError((maximize?"Maximum":"Minimum")+" NOT bracketed by ("+xl+","+yl+"),("+x_+","+yMid+"),("+xr+","+yr+")"); }
		final boolean rNeg = (xr < x_);
		final boolean lNeg = (x_ < xl);
		if (lNeg ^ rNeg) {	//Check if x is between xl and xr.
			throw new AbstractMethodError((maximize?"Maximum":"Minimum")+" NOT bracketed by ("+xl+","+yl+"),("+xMid+","+yMid+"),("+xr+","+yr+")"); }
	}
	
	/**Empty Constructor, init() has to be called to use it! 	 */
	AFloatMinimizer(){}
	
	/**Constructor, tests whether the Minimum is really bracketed! 	 */
	AFloatMinimizer(final double xl, final double x, final double xr, final IFloatFunction f_) {
		init(xl, x, xr, f_); }
	
	/** Auswertung von f am neuen Punkt	
	 * @return the new Estimate for the Minimum
	 */
	public abstract double refine();
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//Testing single Step of Pegasus Step with Quality Control
		L.n("Testing ").l(AFloatMinimizer.class);
		L.n("Searching for the Bracket of a Minimum of y = ").l(TEST_FUNCTION);
		final double[] x = {0, 1, 0};
		final double[] y = BRACKET(x, TEST_FUNCTION);
		L.n(" with  Start Values:").l(x);
		L.n("bracketing x-Values:").l(x); 
		L.n("bracketing y-Values:").l(y); 
		for (int i = x.length; --i >= 0;) {
			Assert.EQUALS(y[i], TEST_FUNCTION.Map(x[i])); }
		Assert.IS_TRUE(y[0] > y[1]);
		Assert.IS_TRUE(y[2] > y[1]);
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
