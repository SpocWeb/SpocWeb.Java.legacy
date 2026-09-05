/*
 * File Name: FalsiFloatRefinerQ.java
 * Created on: 01.02.2004
 *
 */
package math.refiner;

import streamIO.Log;
import function.IFloatFunction;

/**
 * Root finding ({@code x0} for which {@code f(x0)==0}) with the false-position (Regula
 * Falsi) step method, working only on R-&gt;R value functions; doesn't work well for
 * multiple zeros, except if the multiplicity is known and given (multiplicity can also act
 * as a relaxation parameter).
 *
 * <p>False positioning keeps the root bracketed, but converges slower than the secant
 * method, which always moves to the next point.
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
 * mtime: 2026-09-05T11:56:54Z
 * digest: 32d9f1ef42567acbcb2729761ce8a7cd4d3264029abb6c59722ad9ccda2f698a
 * stale: false
 * tags: [code/root_finding]
 * concepts: [Regula Falsi Root Refiner]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class FalsiFloatRefinerQ 
extends AFloatRefinerQ {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(FalsiFloatRefinerQ.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Initializes the Stepper	 */
	public void init(double xl_, double xr_, IFloatFunction f_) {
		super.init(xl_, xr_, f_);	//=> f(xl), f(xr), dx and dy calculated
		calcXR = true;	//force the calculation of xr (although it's not needed with Regula Falsi)
	}

	/**Empty Constructor	 */
	public FalsiFloatRefinerQ() { }

	/**Initializing Constructor	 */
	public FalsiFloatRefinerQ(double xl, double xr, IFloatFunction f) {
		init(xl, xr, f);}	//=> f(xl), f(xr), dx and dy calculated

	/**Performs multiple approximating Steps.
	 * And keeps the Zero bounded by keeping yr positive!
	 * x stays bounded anyway, because Regula Falsi	works like that.
	 * Rotation: (x,y)->(xr, yr)->(xl,yl) 	 
	 * @return xl, the best Ordinate for the Root so far 
	 */
	public double refine() {	//copy the old Values
		x = xl;
		y = yl;
		super.refine();
		if (yr > 0) {	//choose the next Interval
			if (yr < yl) {	//only for improvements in y
				//new positive Value-> restore negative Value from (xOld, yOld)
				xl = x;
				yl = y;
				dx = xr - xl;
				dy = yl - yr;	//Wrong Sign!
			}
		} else {
			if (yr > y) {	//only for improvements in y
				//new negative Value-> swap xl and xr
				double tmp;
				tmp = xl; xl = xr; xr = tmp;
				tmp = yl; yl = yr; yr = tmp;
				dx = -dx;
				dy = -dy;
			}
		}
		return xl; }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//RingFuncs only used for testing!
		L.n("Testing ").l(FalsiFloatRefinerQ.class);
		L.n("Searching for the Root of x = ").l(TEST_FUNCTION);
		final double xLeft  = -0.3;
		final double xRight = +3;
		L.n("Startpoints:" + xLeft + "	" + xRight);
		TEST_REFINER(new FalsiFloatRefinerQ(xLeft, xRight, TEST_FUNCTION), TEST_ZERO_POINT, 6);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
