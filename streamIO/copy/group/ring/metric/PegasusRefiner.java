package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.IIntRing;
import function.IFunction;

/**
 * Title: PegasusRefiner<p>
 * Description:
 * Pegasus Zero Algorithm is a Melange of several Algorithms:
 * You can choose between the Pegasus- and the Andersson/Bjoerk Algorithm
 * King's Modifikation can be added also.
 * Convergence is guaranteed; Speed is at best of golden Rule Speed (1.618...)
 * but at least linear (BiSection).
 * Requires y to be differentiable and y' to be continuous for best Convergence,
 * but works also for only continuous y(x). 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see math.refiner.PegasusFloatRefiner
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
 * digest: f727c22459b894a956a3c9b5b7fc6dc3076ab0c6170e2270afb757726c834fd2
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class PegasusRefiner
extends ARefinerQ {

	/**Empty Constructor. 	 */
	public PegasusRefiner(){}

	/**Initializing Constructor.
	 * The Zero of the Function y must be bracketed in the Interval x! 	 */
	public PegasusRefiner(IMetricIRing xl_, IMetricIRing xr_, IFunction f_) {
		super(xl_, xr_, f_); }

	/**Determines, if Pegasus Steps are performed	 */
	public boolean usePegasus = true;

	/**Determines, if Illinois Steps (Bisection every 2nd Step) are performed	 */
	public boolean useBisection = false;

	/**Determines, if Secant Steps are performed.
	 * Switched on alternatively.	 */
	private boolean useSecant;

	/**
	 * Performs a single approximating Step
	 * by doing a Regula Falsi Step or BiSection.
	 * yl is kept positive, so the check can be performed faster. 
	 */
	public IIntRing refine() {
		dx=  (IMetricIRing) xl.sub(xr);
		dy=  (IMetricIRing) yl.sub(yr);
		x =  (IMetricIRing) xl.sub(yl.div(dy).mulAt(dx));  //{Regula falsi: new x-Value}
		y = ((IMetricIRing) f.Map(x));
		if (!useSecant && y.negative()) {
			yl = yr; xl = xr; useSecant = true;
		} else { //{yr und y positiv}
			 //{Illinois ist gemischte Regula-falsi und Bisektion}
			if (useBisection)
				yl.halfAt();
			else  //Bisection: yl*g=1/2
			if (usePegasus ) { //Inverse quadratische Interpolation
				yl.divAt(y.add(yr)).mulAt(yr);	//{=> yl*g aus (0,1)}
			} else { 	//{Pegasus ist gemischte ? und Bisektion}
				if (y.notLessThan(yr))
					yl.halfAt(); //out of Bounds, Bisection: yl*g=1/2
				else
					yl.subAt(y.div(yr).mul(yl));	//{=> yl*g aus (0,1)}
		    }
			useSecant = false;	//{modifizierter Schritt}
		}
		yr = y;
		xr = x;
		return (IIntRing) x; }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() { //
		L.n("Testing ").l(PegasusRefiner.class);
		L.n("Searching for the Root of y = "+TEST_FUNCTION);
		final IMetricIRing xLeft  = (IMetricIRing) TEST_ZERO_POINT.copy();
		final IMetricIRing xRight = (IMetricIRing) TEST_ZERO_POINT.copy();
		final PegasusRefiner refiner = new PegasusRefiner();
		boolean useSecant = false; 
		do {
			refiner.useSecant = useSecant; 
			boolean useBisection = false; 
			do {
				refiner.useBisection = useBisection;
				boolean usePegasus = false;  
				do {
					refiner.usePegasus = usePegasus; 
					L.n(useSecant?"not ":"").l("using Secants"); 
					L.n(useBisection?"not ":"").l("using Bisection"); 
					L.n(usePegasus?"not ":"").l("using Pegasus"); 
					xLeft.copyAt(new Double(0));
					xRight.copyAt(new Double(3));
					refiner.init(xLeft, xRight, TEST_FUNCTION); 
					TEST_REFINER(refiner, TEST_ZERO_POINT, 9);
				} while(usePegasus = !usePegasus); 
			} while(useBisection = !useBisection); 
		} while(useSecant = !useSecant); 
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
