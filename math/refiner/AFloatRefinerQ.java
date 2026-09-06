/*
 * File Name: AFloatRefinerQ.java
 * Created on: 01.02.2004
 *
 */
package math.refiner;

import streamIO.Log;
import function.IFloatFunction;

/**
 * Stepper algorithm with quality control: extends bracketed root search by iterating until
 * the zero is hit in both the x and y direction, and collects the static bracketing helper
 * methods used to find such an interval in the first place.
 *
 * <p>Requires {@code f} to be continuous nearly everywhere (otherwise the search still
 * converges to a sign flip). The stepper routine should keep the right function value
 * positive, to save checks and speed up evaluation.
 *
 * Known SubClasses used in MultiStepYQ,
 * which does a faster Check for Convergence relying on (yr > 0):
 * @see streamIO.copy.group.ring.metric.FalsiRefinerQ
 * @see streamIO.copy.group.ring.metric.NewtonRefinerQ
 * @see streamIO.copy.group.ring.metric.PegasusRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:55:28Z
 * digest: 7869751ceaf41c8d2aea1e9cd4335cf51049fa9479dd87d6e8154c210bf47dd5
 * stale: false
 * tags: [code/root_finding, code/bracket_matching]
 * concepts: [Bracketed Root Refiner Base Class]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class AFloatRefinerQ 
extends SecantFloatRefiner	//SecantRefiner extends ARefiner by xr and yr.
//unfortunately the refine() Method is already preset and will not be required 
{
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(AFloatRefinerQ.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Close to the Factor of the Golden Ratio 	*/
	final static public float ENLARGE_BY = 1.6f; 
	
	/** ENLARGE_BY^NUM_ENLARGEMENTS should not exceed the Float Range 	 */
	final static public int NUM_ENLARGEMENTS = 50; 
	
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Searches outward from the given interval for a sign change bracketing a root, using
	 * the default enlargement factor and iteration count.
	 * @param f the Function to search a Root for (f(x0) = 0)
	 * @param interval the initial Interval
	 * @return the Number of Iterations left, if a Root could be bracketed, -1 otherwise
	 */
	final static public int BRACKET(final IFloatFunction f, final float[] interval) {
		return BRACKET(f, interval, NUM_ENLARGEMENTS, ENLARGE_BY); }
	
	/**
	 * outward search for brackets on roots (9.1)
	 * Expands the Interval exponentially 
	 * until a Root is bracketed, returning the resulting Interval in Place.  
	 * @param f the Function to search a Root for (f(x0) = 0)
	 * @param interval the initial (guessed) Interval 
	 * @return the Number of Iterations left, if a Root could be bracketed, -1 otherwise
	 */
	final static public int BRACKET(final IFloatFunction f, final float[] interval, final int numIter, final float enlargeBy) {
		if (interval[0] == interval[1]) {
			throw new RuntimeException("Bad initial range in interval=["+interval[0]+","+interval[1]+"]"); }
		float f1=f.Map(interval[0]);
		float f2=f.Map(interval[1]);
		for (int j=numIter; --j >= 0; ) {
			if (f1*f2 < 0) 
				return j; 
			if (Math.abs(f1) < Math.abs(f2)) {
				f1=f.Map(interval[0] += enlargeBy*(interval[0]-interval[1]));
			} else {
				f2=f.Map(interval[1] += enlargeBy*(interval[1]-interval[0]));
			}
		}
		return -1; 
	}
	
	/** inward search for brackets on roots (9.1)
	 * Equally subdivides the given Interval 
	 * and searches for Zero Crossings of the Function. 
	 * Applied to the Difference Vector, it searches for Extremums
	 * 
	 * @param f the Function to search Roots for (f(x0) = 0)
	 * @param xLeft
	 * @param xRight
	 * @param intervals filled with the bracketing Intervals 
	 * @return the Number of bracketing Intervals found 
	 */
	final static public float[][] BRACKET(final IFloatFunction f, final float xLeft, final float xRight, final int numIntervals) {
		final float[][] intervals = new float[numIntervals][2];
		final int numIntevals = BRACKET(f, xLeft, xRight, intervals);
		final float[][] ret = new float[numIntevals][];
		System.arraycopy(intervals, 0, ret, 0, numIntevals);
		return ret;
	}
	
	/** inward search for brackets on roots (9.1)
	 * Equally subdivides the given Interval 
	 * and searches for Zero Crossings of the Function. 
	 * Applied to the Difference Vector, it searches for Extremums
	 * 
	 * @param f the Function to search Roots for (f(x0) = 0)
	 * @param xLeft
	 * @param xRight
	 * @param intervals filled with the bracketing Intervals 
	 * @return the Number of bracketing Intervals found 
	 */
	final static public int BRACKET(final IFloatFunction f, final float xLeft, final float xRight, final float[][] intervals) {
		int numBrackets = 0;
		float x, dx=(xRight-xLeft)/intervals.length;
		float fOld, fNew=f.Map(x = xLeft);
		for (int i=intervals.length; --i>=0; ) {
			fOld = fNew; fNew=f.Map(x += dx);
			if (fNew*fOld < 0) {
				final float[] interval = intervals[numBrackets++];
				interval[0]=x-dx;
				interval[1]=x;
				if(numBrackets == intervals.length) {
					break; } 

			}
		}
		return numBrackets;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables 
	////////////////////////////////////////////////////////////////////////////
	
	/**The current x Value	 */
	public double x;
	
	/**The current y Value	 */
	public double y;
	
	/**The Tolerance in x Direction, 
	 * defaulted to 0, 
	 * but can be set externally to avoid infinite Searching when a root is exactly at 0	 */
	public double xTol;
	
	/**Determines, whether the Starting Points can be swapped	 */
	//protected boolean swapPoints = true;
	
	////////////////////////////////////////////////////////////////////////////
	/// Constructor & Initialization
	////////////////////////////////////////////////////////////////////////////
	
	/**Initializes the Stepper	 */
	public void init(final double xl_, final double xr_, final IFloatFunction f_) {
		super.init(xl_, xr_, f_);		
		init();
	}
	
	/**Initializes the Stepper	 */
	public void init(
			final double xl_, final double xr_, 
			final double _yl, final double _yr) {
		super.init(xl_, xr_, _yl, _yr);
		init();
	}
	
	/**
	 * finishes Initialization 
	 * @throws AbstractMethodError
	 */
	private void init() { //throws IllegalArgumentException {
		final boolean lNeg = (yl < 0);
		final boolean rNeg = (yr < 0);
//		dy = (MetricIRing) yr.subt(yl);	//already Part of SecantRefiner
		if (lNeg == rNeg) {
			throw new IllegalArgumentException("Zero is not bracketed between "+yl+" and "+yr);}
		final boolean swapPoints = true; // 
		if (rNeg && swapPoints) {	//Umkehren...
			double tmp; //so dass f in xl negativ => schnellerer Test
			tmp = yl; yl = yr; yr = tmp; dy = -dy; 
			tmp = xl; xl = xr; xr = tmp; dx = -dx;
		}
	}
	
	/**Initializing Constructor.
	 * The Zero of the Function f must be bracketed in the Interval x!
	 * The Stepper should keep the Zero bracketed!
	 * yr is kept positive!	 */
	public AFloatRefinerQ() { }

	/**Initializing Constructor.
	 * The Zero of the Function f must be bracketed in the Interval x!
	 * The Stepper should keep the Zero bracketed!
	 * yr is kept positive!	 */
	public AFloatRefinerQ(final double xl_, final double xr_, final IFloatFunction f_) { 
		super(xl_, xr_, f_); }

	/**
	 * Initialize the Iterator by handing over two Starting Points and their Values. 
	 */
	public AFloatRefinerQ(double _xl, double _xr, double _yl, double _yr) {
		super(_xl, _xr, _yl, _yr);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//Testing single Step of Pegasus Step with Quality Control
		L.n("Testing ").l(AFloatRefinerQ.class);
		L.n("Searching for the Roots of y = ").l(TEST_FUNCTION);
		final float[] interval = {0, 3};
		final int iterLeft = BRACKET(TEST_FUNCTION, interval); 
		
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
