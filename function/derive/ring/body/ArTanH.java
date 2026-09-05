package function.derive.ring.body;	//Function;

import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.ring.Algebra;
import function.derive.ring.CatDerive;

/**This Class encapsulates the ArTanH Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:38Z
 * digest: a39c89224c3e0ad02bfe338122b6598df60f2241e2682b01c2a62669b231b567
 * stale: false
 * tags: [code/hyperbolic_function, code/derivable_function_contract]
 * concepts: [Inverse Hyperbolic Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class ArTanH
extends AFloatDeriveAble {

	/**Local Reference to the single Instance	 */
	final static public ArTanH ArTanH = new ArTanH();

	static { //Initializer
		ArTanH.setInverse   (TanH.TanH);
		ArTanH.setDerivative(Algebra.Inv1_xx);
		ArTanH.setIntegral  (new CatDerive(Logarithm.LOGARITHM, CosH.CosH));
	}

	/**private Constructor for Singleton Implementation	 */
	private ArTanH() { }
	
    /**Reports that ArTanH preserves strict ascending order of its argument.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }

	/**This Function represents the ArTanH Function.	 */
	public Object Map (final Object arg) { return ((MetricBody) arg).ArTanH(); }

	/**Returns ArTanH(x) = ln((1+x)/(1-x))/2 for -1 < x < 1.	 */
	public double Map(double x) { return Math.log((1+x)/(1-x))*IMeasurAble.HALF; }

	/**Returns the ArTanH Function's Derivative at x.
	 * @return The Derivative at x	 */
	public double getDerivative(double x) {
		return ICountAble.ONE / (1 - x*x); }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		double xp1, xm1;
		derivative.Value = ICountAble.ONE / ((xp1 = (1 + x))*(xm1 = (1 - x)));
		return Math.log(xp1/xm1)*IMeasurAble.HALF; }

}
