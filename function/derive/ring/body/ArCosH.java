package function.derive.ring.body;	//Function;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.Algebra;
import function.derive.ring.Diff;
import function.derive.ring.Prod;

/**This Class encapsulates the ArCosH Function.  */
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

	/*	Returns SinH Function Derivative: CosH(x) for all x 	*/
	public double getDerivative(double x) { return 1 / Math.sqrt(x*x-1); }	//

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble Derivative) {
		double SqRtXX_1 = Math.sqrt(x*x-1); //Initialization pobably optimized
		Derivative.Value = 1 / SqRtXX_1; //into the next Expression
		return Math.log(x + SqRtXX_1); }

}
