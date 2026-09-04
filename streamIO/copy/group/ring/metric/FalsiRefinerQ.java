package streamIO.copy.group.ring.metric;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IRing;
import function.IFunction;

/**
 * Root Finding (x0 Value for which f(x0)==0) with Falsi Step
 * Doesn't work well for multiple Zeros,
 * except if the Multiplicity is known and given
 * (can also act as a Relaxation Parameter!)
 * Works only on R->R Value Functions. 
 */
public class FalsiRefinerQ
extends ARefinerQ {

	/**Initializes the Stepper	 */
	public void init(IIntRing xl_, IIntRing xr_, IFunction f_) {
		super.init(xl_, xr_, f_);	//=> f(xl), f(xr), dx and dy calculated
		x = (IMetricIRing) xl.newInstance();
		y = (IMetricIRing) yl.newInstance();
		//calcXR = true;	//force the calculation of xr (although it's not needed with Regula Falsi)
	}

	/**Empty Constructor	 */
	public FalsiRefinerQ() { }

	/**Initializing Constructor	 */
	public FalsiRefinerQ(IMetricIRing xl, IMetricIRing xr, IFunction f) {
		init(xl, xr, f);}	//=> f(xl), f(xr), dx and dy calculated

	/**Performs multiple approximating Steps.
	 * And keeps the Zero bounded by keeping yr positive!
	 * x stays bounded anyway, because Regula Falsi	works like that.
	 * Rotation: (x,y)->(xr, yr)->(xl,yl) 	 */
	public IIntRing refine() {	//copy the old Values
		x.copyAt(xl);
		y.copyAt(yl);
		super.refine();
		if (((IMetricIRing) yr).positive()) {	//choose the next Interval
			if (((IMetricIRing) yr).isLessThan (yl)) {	//only for improvements in y
				//new positive Value-> restore negative Value from (xOld, yOld)
				xl.copyAt(x);
				yl.copyAt(y);
				((IRing) dx.copyAt(xr)).subAt(xl);
				((IRing) dy.copyAt(yl)).subAt(yr);	//Wrong Sign!
			}
		} else {
			if (((IMetricIRing) yr).isMoreThan(y)) {	//only for improvements in y
				//new negative Value-> swap xl and xr
				IIntRing tmp;
				tmp = xl; xl = xr; xr = tmp;
				tmp = yl; yl = yr; yr = tmp;
				dx.negAt();
				dy.negAt();
			}
		}
		return xl; }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//RingFuncs only used for testing!
		L.n("Testing ").l(FalsiRefinerQ.class);
		L.n("Searching for the Root of x = ").l(TEST_FUNCTION);
		final IMetricIRing xLeft  = (IMetricIRing) TEST_ZERO_POINT.copy().copyAt(new Double(-0.3));
		final IMetricIRing xRight = (IMetricIRing) TEST_ZERO_POINT.copy().copyAt(new Double(+3));
		L.n("Startpoints:" + xLeft + "	" + xRight);
		TEST_REFINER(new FalsiRefinerQ(xLeft, xRight, TEST_FUNCTION), TEST_ZERO_POINT, 6);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
