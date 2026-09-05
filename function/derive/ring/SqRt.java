package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.IMetricIRing;
import function.derive.ADeriveAble;
import function.derive.Identity;

/**This Class encapsulates the Square Root Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 7a8e6ec7ba0f2880c0e229649abe849857bd5098d58a70783f768bd11f4bad5a
 * stale: false
 * tags: [code/mathematical_function, code/derivable_function_contract]
 * concepts: [Function Algebra]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class SqRt
extends ADeriveAble {

	/**This Function returns the Square Function in a typed way.  */
	public static IMetricIRing SQRT(Object arg) { return ((IMetricIRing) arg).SqRt(); }

	/**Local Reference to the single Instance	 */
	final static public SqRt SQRT = new SqRt();

    static { //Initializer
		SQRT.setInverse   (Square.SQUARE);
		SQRT.setDerivative(new CatDerive(Inv.INV,
						   new CatDerive(DoubleAt.DoubleAt, SQRT)));
		SQRT.setIntegral  (new Prod(SQRT, Identity.IDENTITY));
    }

	/**private Constructor for Singleton Implementation	 */
	private SqRt() {
 	}

	/**This Function represents the Square Function.  */
	public Object Map (Object arg) { return ((IMetricIRing) arg).SqRt(); } //SqRt(arg); }

	/**This Function represents the Pred Function: x-1  */
	public double Map(double x) { return Math.sqrt(x); }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.	 */
	public boolean canProcess(Object arg) {	return arg instanceof IMetricIRing; }

}
