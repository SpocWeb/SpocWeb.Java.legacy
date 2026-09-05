/*
 * File Name: ADoubleRefiner.java
 * Created on: 29.01.2004
 *
 */
package math.refiner;

import streamIO.Assert;
import streamIO.Log;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.ring.body.Cosinus;

/**
 * Abstract base class for searching for the root of a function, adding iteration-control
 * ({@link #solve(int, double, boolean)}) and self-test scaffolding on top of
 * {@link AFloatImprover}'s state and {@link IFloatRefiner}'s single-step contract.
 *
 * Similar Classes:
 * @see streamIO.copy.group.ring.ARefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:54:40Z
 * digest: d5ff8db23e256ccfd3574813e355dcd4b2da8892797489a9861e254d5a8f5188
 * stale: false
 * tags: [code/root_finding]
 * concepts: [Root Refiner Base Class]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public abstract class AFloatRefiner 
extends AFloatImprover 
implements IFloatRefiner {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	protected static Log L = new Log(AFloatRefiner.class);
	
	////////////////////////////////////////////////////////////////////////////
	
	/**The (a priori) Multiplicity of the Zero.
	 * Set by NewtonRefiner2, required for Correction by Regula Falsi. 
	 * Also acts as an (Over-)Relaxation Factor to speed up Searches.  
	 */
	public double multiplicity = 1;
	
	/**Local Reference to the Function for which the Zero has to be determined.	 */
	protected IFloatFunction f;
	
	////////////////////////////////////////////////////////////////////////////
	
	/**Initializing the Iteration
	 * by giving the Function and a Starting Point.	 */
	public void init(final double _x, final double _y) {
		super.init(_x, _y);
		f = null; 
	}
	
	/**Initializing the Iteration
	 * by giving the Function and a Starting Point.	 */
	public void init(final double _x, final IFloatFunction _f) {
		super.init(_x, _f.Map(xl)); 
		f = _f;
	}
	
	/**Empty Constructor.	 */
	public AFloatRefiner()	{}
	
	/**Initializing Constructor for Iteration
	 * by giving the Function and a Starting Point.	 */
	public AFloatRefiner(final double _x, final double _y) {
		init(_x, _y);
	}
	
	/**Initializing Constructor for Iteration
	 * by giving the Function and a Starting Point.	 */
	public AFloatRefiner(final double _x, final IFloatFunction _f) {
		init(_x, _f); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/**	
	 * find a Solution up to the given Tolerance. 
	 * Since the Tolerance is usually not required 
	 * for Calculation of the Refinement, it is ignored here. 
	 * In Exchange the Return Value could be tested for Convergence! 
	 * @param tol The Tolerance to use in x-Direction 
	 * @return the best Ordinate (x-Value) for the Minimum so far 
	 */
	public double refine(final double tol) {
		return refine(); }
	
	/** refines the Solution until the given Tolerance is fulfilled
	 * 
	 * @param tolerance the relative and absolute x Tolerance of the Solution
	 * @param maxIter the maximum Number of Iterations to use 
	 * @return the Number of Iterations left. 
	 * If 0 the Algorithm didn't converge (fast enough) 
	 */
	public int solve(final int maxIter) { 
		return solve(maxIter, ByRefDouble.DoubleAccuracy); }
	
	/** refines the Solution until the given Tolerance is fulfilled
	 * 
	 * @param tolerance the relative and absolute x Tolerance of the Solution
	 * @param maxIter the maximum Number of Iterations to use 
	 * @return the Number of Iterations left. 
	 * If 0 the Algorithm didn't converge (fast enough) 
	 */
	public int solve(final int maxIter, final double tolerance) {
		return solve(maxIter, tolerance, false); }
	
	/** refines the Solution until the given Tolerance is fulfilled
	 * 
	 * @param tolerance the relative and absolute x Tolerance of the Solution
	 * @param maxIter the maximum Number of Iterations to use 
	 * @return the Number of Iterations left. 
	 * If 0 the Algorithm didn't converge (fast enough) 
	 */
	public int solve(final int maxIter, final boolean raiseException) { 
		return solve(maxIter, ByRefDouble.DOUBLE_ACCURACY, raiseException); }

	/** refines the Solution until the given Tolerance is fulfilled
	 * 
	 * @param tolerance the relative and absolute x Tolerance of the Solution
	 * @param maxIter the maximum Number of Iterations to use 
	 * @return the Number of Iterations left. 
	 * If 0 the Algorithm didn't converge (fast enough) 
	 */
	public int solve(final int maxIter, final double tolerance, final boolean raiseException) {
		double xOld, xNew = refine(tolerance); 
		for (int i = maxIter; --i >= 0;) { //
			xOld = xNew; xNew = refine(); 
			if (ByRefDouble.EQUALS(xOld, xNew, tolerance)) {
				return i; }
		}
		if (raiseException) {
			throw new RuntimeException("Maximum Number of Iterations exceeded:"+maxIter); }
		return 0; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Counter for Testing	 */
	final static public IFloatFunction TEST_FUNCTION = Cosinus.Cosinus;

	/** Fixpoint for the given Test Function 	 */
	final static public double TEST_FIX_POINT = 0.7390851332151607;

	/** One Zeropoint for the given Test Function 	 */
	final static public double TEST_ZERO_POINT = IMeasurAble.PI_HALF;

	/** One Minimum for the given Test Function 	 */
	final static public double TEST_MIN_POINT = IMeasurAble.PI; 

	/**Method to test a Refiner Instance.	 */
	protected static final void TEST_REFINER(final AFloatRefiner refiner, final double solution, final int maxIter) {	//RingFuncs only used for testing!
		if (refiner.solve(maxIter) == 0) {
			Assert.FAIL("Maximum Number of Iterations exceeded:"+maxIter+" at x="+refiner.xl+" with y="+refiner.yl); } 
		Assert.EQUALS(refiner.xl, solution); 
	}

}
