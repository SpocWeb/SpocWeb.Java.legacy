package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.IIntRing;
import function.IFunction;
import function.derive.IDeriveAble;

/**
 * Root Search with Newton Formula and bracketing.
 * Doesn't work well for multiple Zeros,
 * except if the Multiplicity is known and given
 * (can also act as a Relaxation Parameter!)
 * Works only on R->R Value Functions. 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 0e344b3aee639cf48be4daab1d33294b7986d7e9a866b68090db4f0de4d44797
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class NewtonRefinerQ
extends ARefinerQ {

	/**Initialisierung einer neuen Suche	 */
	public void init(final IMetricIRing xl, final IMetricIRing xr, final IFunction f0, final IFunction f1) {
		super.init(xl, xr, f0);	//=> f(xl), f(xr), dx and dy calculated
		ddx = (IMetricIRing) xl.newInstance();
		dydx = (IMetricIRing) xl.newInstance();
		x = (IMetricIRing) xl.newInstance();
		y = (IMetricIRing) yl.newInstance();
		dydx = (IMetricIRing) f1.Map(x);
		this.f1 = f1;
	}

	/**Constructor, ordering xl and xr so that yr is positive	 */
	public NewtonRefinerQ() { }

	/**Constructor, ordering xl and xr so that yr is positive	 */
	public NewtonRefinerQ(IMetricIRing xl, IMetricIRing xr, IFunction f0, IFunction f1) {
		init(xl, xr, f0, f1);}

	/**New x Value	 */
	private IFunction f1;

	/**Temporary new x Value	 */
	private IMetricIRing ddx;

	/**Temporary new x Value	 */
	private IMetricIRing dydx;

	/** Flag to switch assigning yr and yl...	 */
	public boolean assignYlYr = true;// false; //not required...

	/**Performs multiple approximating Steps
	 * until the item is bounded with sufficient Accuracy.
	 * And keeps the Zero bounded by keeping yr positive
	 * and the x Values within the current Interval!
	 * Rotation: (x,y)->(xr, yr)->(xl,yl) 	 */
	public IIntRing refine() {	//copy the old Values, reimplements the Newton Algorithm for 1 dim Variable Spaces.
		ddx.copyAt(y); ddx.divAt(dydx); 
		x.subAt(ddx);   
		if (((x.isMoreThan(xr)) == (x.isMoreThan(xl))) //leads out of the Interval
			|| (((IMetricIRing)dy).AbsV().isLessThan(ddx.AbsV()))) { //or is slower than BiSection
			final IIntRing tmp = dy; dy = dx; dx=tmp; 
			dx.copyAt(xr); dx.subAt(xl); dx.halfAt(); //use BiSection
			x.copyAt(xl); x.addAt(dx);
		} else { //regular Newton Step
			final IIntRing tmp = dy; dy = dx; dx = ddx; ddx = (IMetricIRing) tmp; //misuse dy for dxOld
		}
		L.n("xl=").l(xl).l(" xr=").l(xr);
		//if (f01 != null) {
		//	y = f01.getFuncDerive(x, df);
		//} else {
			y = (IMetricIRing) f.Map(x); 
			dydx = (IMetricIRing) f1.Map(x);
		//}
		if (y.negative()) {
			if (assignYlYr) {
				yl.copyAt(y); }
			xl.copyAt(x);
		} else {
			if (assignYlYr) {
				yr.copyAt(y); }
			xr.copyAt(x);
		} 
		return x;
	}

	/**Method to test all Implementations in this class.	 */
	public static void testIt() { 	//RingFuncs only used for testing!
		final IDeriveAble derivative = ((IDeriveAble)TEST_FUNCTION).getDerivative();
		L.n("Testing ").l(NewtonRefinerQ.class);
		L.n("Searching for the Solution of y = 0 = ").l(TEST_FUNCTION).l(" with y' = ").l(derivative);
		final NewtonRefinerQ refiner = new NewtonRefinerQ(
		(IMetricIRing)TEST_FIX_POINT, (IMetricIRing)TEST_MIN_POINT, TEST_FUNCTION, derivative); //
		//NS.ddx= (MetricIRing) ACopyAble.testInstance.newInstance();
		TEST_REFINER(refiner, TEST_ZERO_POINT, 16); //extremely well-behaved!
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
