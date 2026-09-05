package function.derive.ring.body;	//Function;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.Algebra;
import function.derive.ring.Diff;
import function.derive.ring.Prod;

/**This Class encapsulates the ArSinH Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:34Z
 * digest: 85c0bb6b876b4fdc0504e9eb68ad6333ee7188e9dff133f35cd91d28c8bcd87c
 * stale: false
 * tags: [code/hyperbolic_function, code/derivable_function_contract]
 * concepts: [Inverse Hyperbolic Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class ArSinH
extends AFloatDeriveAble {

	/**Local Reference to the single Instance	 */
	final static public ArSinH ArSinH = new ArSinH();

	static { //Initializer
		ArSinH.setInverse   (SinH.SinH);
		ArSinH.setDerivative(Algebra.InvSqRtxxp1);
		ArSinH.setIntegral  (new Diff(
							 new Prod(Identity.IDENTITY, ArSinH),
							 Algebra.SqRtxxp1));
	}

	/**private Constructor for Singleton Implementation	 */
	private ArSinH(){}
	
	/**This Function represents the ArSinH Function.	 */
	public Object Map (Object arg) { return ((MetricBody) arg).ArSinH(); }
	
    /**Reports that ArSinH preserves strict ascending order of its argument.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }

	/**Returns ArSinH(x) = ln(x + SqRt(x^2+1)).	 */
	public double Map(double x) { return Math.log(x + Math.sqrt(x*x+1)); }

	/**Returns the ArSinH Function's Derivative at x.
	 * @return The Derivative at x	 */
	public double getDerivative(double x) {
		return ICountAble.ONE / Math.sqrt(x*x+1); }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		double SqRt1pXX = Math.sqrt(x*x + 1);
		derivative.Value = ICountAble.ONE / SqRt1pXX;
		return Math.log(x + SqRt1pXX); }

}
