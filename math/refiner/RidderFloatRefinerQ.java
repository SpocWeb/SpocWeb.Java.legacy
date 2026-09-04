/*
 * File Name: RidderFloatRefinerQ.java
 * Created on: 01.02.2004
 *
 */
package math.refiner;

import streamIO.Log;
import function.IFloatFunction;

/**
 * Title: RidderFloatRefinerQ<p>
 * Description:
 * 
 * Root (x = 0) Search with a modified Secant False Position Formula, 
 * which requires 2 Evaluations and has a quadratic Convergence, 
 * thus an actual Order of SqRt(2), but is extraordinarily robust. 
 * @see math.refiner.BrentFloatRefinerQ is only slightly better than this Implementation 
 * but much more complex. But since it needs only a single Evaluation, 
 * it can easily be driven from outside.  
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see math.refiner.FalsiFloatRefinerQ  O(1.618... = golden  at best)
 * @see math.refiner.RidderFloatRefinerQ O(1.414... = SqRt(2) at best)
 * @see math.refiner.BrentFloatRefinerQ  O(2 at best without evaluating the Derivative )
 * @see math.refiner.NewtonFloatRefinerQ O(2 at best with    evaluating the Derivative )
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
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
	 * @see refiner.IFloatRefiner#refine()
	 * Performs a single approximating Step.
	 * @return xNew, the best Ordinate for the Root so far 
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
