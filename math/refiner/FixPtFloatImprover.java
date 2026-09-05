/*
 * File Name: FixPtFloatRefiner.java
 * Created on: 29.01.2004
 *
 */
package math.refiner;

import streamIO.Log;

/**
 * Fixpoint search according to Banach, working on R-&gt;R value functions; requires
 * {@code f} to be differentiable with {@code |f'| < 1} in the range considered, and
 * converges linearly.
 *
 * Similar Classes:
 * @see streamIO.copy.group.ring.FixPtRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:55:35Z
 * digest: 56e2f0df4260044a7a138f1f32170539d1684ced584275fd4a1bbca9cc7e6f75
 * stale: false
 * tags: [code/fixed_point_iteration]
 * concepts: [Fixed-Point Iterative Improver]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class FixPtFloatImprover 
extends AFloatImprover {
	
	private static final Log L = new Log(FixPtFloatImprover.class);  
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor	 */
	public FixPtFloatImprover (){}
	
	/**Performs a single approximating Step: x = f(x)
	 * @return xl, the new Estimate for the FixPoint 
	 */
	public double improve(final double yValue) {
		dx = (yl = yValue);	//{x-Abstand und y-Abstand werden kontrolliert}
		return xl = dx; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() { 	//RingFuncs only used for testing!
		L.n("Testing ");
		/*
		L.n("Searching for the FixPoint: Solution of x = ").l(TEST_FUNCTION.getClass());
		TEST_REFINER(new FixPtFloatImprover(3, TEST_FUNCTION), TEST_FIX_POINT, 45);
		*/
	} 

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
