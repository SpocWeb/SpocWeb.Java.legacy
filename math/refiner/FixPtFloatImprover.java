/*
 * File Name: FixPtFloatRefiner.java
 * Created on: 29.01.2004
 *
 */
package math.refiner;

import streamIO.Log;

/**
 * Title: FixPtFloatRefiner<p>
 * Description: 
 * Fixpoint Search according to Banach 
 * Works on R->R Value Functions.
 * Requires f to be differentiable and 1 > |f'| 
 * in the Range considered. 
 * 
 * Linear Convergence: O(1) Method 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see streamIO.copy.group.ring.FixPtRefiner
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
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
