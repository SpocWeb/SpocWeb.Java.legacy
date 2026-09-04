/*
 * File Name: SecantFloatRefiner.java
 * Created on: 29.01.2004
 *
 */
package math.refiner;

import streamIO.Log;
import streamIO.real.StreamOutPlotter;
import function.IFloatFunction;
import function.byref.ByRefDouble;

/**
 * Title: SecantFloatRefiner<p>
 * Description:
 * Root (x = 0) Search with Secant Formula, 
 * which differs from the Regula Falsi in that 
 * it always accepts the newly extrapolated Zero Position 
 * and thus may not keep a Root bracketed! 
 * 
 * Doesn't work well for multiple Zeros,
 * except if the Multiplicity is known and given
 * (Multiplicity can also act as a Relaxation Parameter!)
 * 
 * Works only on 1-dim. R->R Value Functions. 
 * For R^n->R^n Functions you need a HyperCube 
 * 
 * Requires f to be differentiable and f' to be continuous.
 * Converges with 1.618..., but only near Zeroes.
 * No guaranteed Convergence, because not bounded.  
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see streamIO.copy.group.ring.SecantRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class SecantFloatRefiner 
extends AFloatRefiner {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static Log L = new Log(SecantFloatRefiner.class, 0);
	
	////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**The current right Function Value 	 */
	public double yr;
	
	/**The current right x Value 	 */
	public double xr;
	
	/**The current y Difference
	 * public, because read from the MultiStep Refiner.  	 */
	public double dy;
	
	/**Switches the Calculation of XR off.
	 * Reduces Convergence, but increases Stability.
	 * Should be done only in the Beginning.	 */
	protected boolean calcXR = true;//false;
	
	////////////////////////////////////////////////////////////////////////////
	/// Initialization & Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor.	 */
	public SecantFloatRefiner(){}
	
	/**Initializes the Regula Falsi Iteration
	 * by giving the Function and two Starting Points.	 */
	public SecantFloatRefiner(final double _xl, final double _xr, final IFloatFunction _f) {
		init(_xl, _xr, _f); }
	
	/**Initializing the Iteration
	 * by giving the Function and a Starting Point.	 */
	public SecantFloatRefiner(final double _xl, final double _xr, double _yl, double _yr) {
		init(_xl, _xr, _yl, _yr); }
	
	/**Initializing the Iteration
	 * by giving the Function and a Starting Point.	 */
	public void init(final double _xl, final double _xr, double _yl, double _yr) {
		super.init(_xl, _yl); 
		this.xr = _xr;
		this.dx = _xr - xl;
		this.yr = _yr;
		this.dy = yr - yl; 
		this.f = null; 
	}
	
	/**Initializing the Iteration
	 * by giving the Function and a Starting Point.	 */
	public void init(final double xl_, final double xr_, final IFloatFunction f_) {
		init(xl_, xr_, f_.Map(xl_), f_.Map(xr_));
		this.f = f_; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	
	/** refines the Solution until the given Tolerance is fulfilled
	 * 
	 * @param tolerance the relative and absolute x Tolerance of the Solution
	 * @param maxIter the maximum Number of Iterations to use 
	 * @return the Number of Iterations left. 
	 * If 0 the Algorithm didn't converge (fast enough) 
	 */
	public int solve(final int maxIter, final double tolerance, final boolean raiseException) {
		for (int i = maxIter; --i >= 0;) { //
			refine(tolerance); 
			L.n("Iterations left:").l(i).l(" xl=").l(xl).l(" xr=").l(xr);
			if (ByRefDouble.EQUALS(xl, xr, tolerance, tolerance)) {
				return i; }
		}
		if (raiseException) {
			throw new RuntimeException("Maximum Number of Iterations exceeded:"+maxIter); }
		return 0; 
	}
	
	/**
	 * @see refiner.IFloatRefiner#refine()
	 * Performs a single approximating Step.
	 * Rotation: (x,y)->(xr, yr)->(xl,yl) 	
	 * @return xr, the new Estimate for the Root. 
	 */
	public double refine() {	//following two lines just to save Instantiation of new double Variables.
		dx*=multiplicity*yr/dy;  //{Regula falsi: new x-Value}
		//if (multiplicity != 1) { dx*=multiplicity; } 
		if (calcXR) {
			xl = xr; } 	//no longer necessary
		xr -= dx;	//don't need to calculate xr any longer.
		yl = yr; yr = f.Map(xr);
		dy = (yl -= yr);	//wrong sign on purpose, because dx is inverse after the Rotation!
		return xr; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//RingFuncs only used for testing!
		L.n("Testing ").l(SecantFloatRefiner.class);
		L.n("Searching for the Root of ").l(TEST_FUNCTION.getClass());
		final float xLeft = -1.3f; 
		final float xRight = 3; 
		L.n(StreamOutPlotter.PLOT(TEST_FUNCTION, xLeft, 20, xRight, 2, 2));
		TEST_REFINER(new SecantFloatRefiner(xLeft, xRight, TEST_FUNCTION), TEST_ZERO_POINT, 8);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
