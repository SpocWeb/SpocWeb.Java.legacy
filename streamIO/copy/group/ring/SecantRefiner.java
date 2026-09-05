package streamIO.copy.group.ring;

import function.IFunction;

/**
 * Title: SecantRefiner<p>
 * Description:
 * Root (x = 0) Search with Secant Formula, 
 * which differs from the Regula Falsi in that it may not keep a Root bracketed!
 * Doesn't work well for multiple Zeros,
 * except if the Multiplicity is known and given
 * (can also act as a Relaxation Parameter!)
 * Works only on 1-dim. R->R Value Functions.
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
 * @see refiner.SecantFloatRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: f6a21b0bff4a065d1dfb3a7591801a116aed7ae9faed494d3d94a13c8a12f8ec
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class SecantRefiner
extends ARefiner
//implements IStepZero
{

	/**Initializing the Iteration
	 * by giving the Function and a Starting Point.	 */
	public void init(IIntRing xl_, IIntRing xr_, IFunction f_) {
		super.init(xl_, f_);
		xr = (IIntRing) xr_.copy();
		dx = (IIntRing) xr_.sub(xl);
		yr = (IIntRing) f_.Map(xr_);
		dy = (IIntRing) yr.sub(yl); }

	/**Empty Constructor.	 */
	public SecantRefiner(){}

	/**Initializes the Regula Falsi Iteration
	 * by giving the Function and two Starting Points.	 */
	public SecantRefiner(final IIntRing xl, final IIntRing xr, final IFunction f_) {
		init(xl, xr, f_); }

	/**The current right Function Value 	 */
	public IIntRing yr;

	/**The current right x Value 	 */
	public IIntRing xr;

	/**The current y Difference
	 * public, because read from the MultiStep Refiner.  	 */
	public IIntRing dy;

	/**Switches the Calculation of XR off.
	 * Reduces Convergence, but increases Stability.
	 * Should be done only in the Beginning.	 */
	//protected boolean calcXR = true; //false;

	/**Performs a single approximating Step.
	 * Rotation: (x,y)->(xr, yr)->(xl,yl) 	 */
	public IIntRing refine() {	//following two lines just to save Instantiation of new IIntRing Variables.
		dx. divAt(dy).mulAt(yr) ;  //{Regula falsi: new x-Value}
		if (multiplicity != null) { 
			dx.mulAt(multiplicity); }
		//if (calcXR) { 
		xl.copyAt(xr); //} 	//no longer necessary
		xr.subAt(dx);	//don't need to calculate xr any longer.
		yl.copyAt(yr); yr = (IIntRing) (f.Map(xr));
		dy.copyAt(yl.subAt(yr));	//wrong sign on purpose, because dx is inverse after the Rotation!
		return xr; }

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() { 	//RingFuncs only used for testing!
		L.n("Testing ").l(SecantRefiner.class);
		L.n("Searching for the Solution of 0 = ").l(TEST_FUNCTION);
		final IIntRing xLeft  = (IIntRing) TEST_ZERO_POINT.copy().copyAt(new Double(-1.3));
		final IIntRing xRight = (IIntRing) TEST_ZERO_POINT.copy().copyAt(new Double(+3));
		L.n("Startpoints:" + xLeft + "	" + xRight);
		TEST_REFINER(new SecantRefiner(xLeft, xRight, TEST_FUNCTION), TEST_ZERO_POINT, 7);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
