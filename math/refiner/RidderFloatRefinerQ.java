/*
 * File Name: RidderFloatRefinerQ.java
 * Created on: 01.02.2004
 *
 */
package math.refiner;

import streamIO.Log;
import function.IFloatFunction;

/**
 * Root (x = 0) search with a modified secant false-position formula, which requires 2
 * evaluations and has quadratic convergence, an actual order of {@code sqrt(2)}, and is
 * extraordinarily robust.
 *
 * <p>{@link BrentFloatRefinerQ} is only slightly better than this implementation but much
 * more complex; since it needs only a single evaluation, it can easily be driven from
 * outside.
 *
 * Similar Classes:
 * @see FalsiFloatRefinerQ  O(1.618... = golden  at best)
 * @see RidderFloatRefinerQ O(1.414... = SqRt(2) at best)
 * @see BrentFloatRefinerQ  O(2 at best without evaluating the Derivative )
 * @see NewtonFloatRefinerQ O(2 at best with    evaluating the Derivative )
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:58:09Z
 * digest: 8dd05697679e8158c93e9d1e4b9b08be367768f41f3f8e92bd3b44aa7a8dfbe3
 * stale: false
 * tags: [code/root_finding]
 * concepts: [Ridders' Method Root Refiner]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class RidderFloatRefinerQ 
extends AFloatRefinerQ {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(RidderFloatRefinerQ.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor.	 */
	public RidderFloatRefinerQ(){}

	/**Initializes the Regula Falsi Iteration
	 * by giving the Function and two Starting Points.	 */
	public RidderFloatRefinerQ(final double xl, final double xr, final IFloatFunction f_) {
		super(xl, xr, f_); }

	/**
	 * Performs a single Ridders' method step: evaluates the function at the midpoint and at
	 * an exponential-fit update point, then keeps the root bracketed by the pair of points
	 * with opposite sign.
	 * @return xNew, the best Ordinate for the Root so far
	 * @see IFloatRefiner#refine()
	 */
	public double refine() {	//following two lines just to save Instantiation of new double Variables.
		final double xMid=0.5*(xl+xr);
		final double fMid=f.Map(xMid); //1st Function call

		final double s=Math.sqrt(fMid*fMid-yl*yr);
		//update Formula for exponential Fit
		final double xNew=xMid+(xMid-xl)*((yl >= yr ? 1 : -1)*fMid/s);
		final double fNew=f.Map(xNew); //2nd Function call
		//keep Root bracketed!
		//Choose xr, so yr has opposite Sign from fNew
		final boolean fNewNeg = (fNew < 0); 
		if ((fMid < 0) != fNewNeg) {
			xl=xMid; //use both new Points
			yl=fMid;
			xr=xNew;
			yr=fNew; 
		} else { //otherwise use only xNew, not xMid!
			//since f(xl)=yl < 0...
			if (fNewNeg) {
				xl=xNew;
				yl=fNew;
			} else {
				xr=xNew;
				yr=fNew;
			}
		} 
		return xNew;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//RingFuncs only used for testing!
		L.n("Testing ").l(RidderFloatRefinerQ.class);
		L.n("Searching for the Root of ").l(TEST_FUNCTION.getClass());
		final float xLeft = -1.3f; 
		final float xRight = 3;
		final RidderFloatRefinerQ refiner = new RidderFloatRefinerQ(xLeft, xRight, TEST_FUNCTION);
		TEST_REFINER(refiner, TEST_ZERO_POINT, 4);
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
