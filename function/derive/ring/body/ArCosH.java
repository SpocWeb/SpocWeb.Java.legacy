package function.derive.ring.body;	//Function;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.Algebra;
import function.derive.ring.Diff;
import function.derive.ring.Prod;

/**This Class encapsulates the ArCosH Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:31Z
 * digest: cc55969b53f0bf98711c23ba39710f30f60174aa91fdfd8945f903c1034e0fa3
 * stale: false
 * tags: [code/hyperbolic_function, code/derivable_function_contract]
 * concepts: [Inverse Hyperbolic Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class ArCosH
extends AFloatDeriveAble {

	/**Local Reference to the single Instance	 */
	final static public ArCosH ArCosH = new ArCosH();

	static { //Initializer
		ArCosH.setInverse   (CosH.CosH);
		ArCosH.setDerivative(Algebra.InvSqRtxx_1);
		ArCosH.setIntegral  (new Diff(
							 new Prod(Identity.IDENTITY, ArCosH),
									  Algebra.SqRtxx_1));
	}

	/**private Constructor for Singleton Implementation	 */
	private ArCosH() { }

	/**This Function represents the ArCosH Function.	 */
	public Object Map (Object arg) { return ((MetricBody) arg).ArCosH(); }

	/**This Function represents the ArCosH Function.	 */
	public double Map (double x) { return Math.log(x + Math.sqrt(x*x-1)); }

	/**Returns the ArCosH Function's Derivative: 1/SqRt(x^2-1) for x > 1.	 */
	public double getDerivative(double x) { return 1 / Math.sqrt(x*x-1); }	//

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble Derivative) {
		double SqRtXX_1 = Math.sqrt(x*x-1); //Initialization pobably optimized
		Derivative.Value = 1 / SqRtXX_1; //into the next Expression
		return Math.log(x + SqRtXX_1); }

}
