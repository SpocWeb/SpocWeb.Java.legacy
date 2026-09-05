package function.derive.ring.body;	//Function;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.Algebra;
import function.derive.ring.CatDerive;
import function.derive.ring.Diff;
import function.derive.ring.Neg;
import function.derive.ring.Prod;

/**This Class encapsulates the ArcCos Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:39Z
 * digest: 260b7226790d4a15ba786c78132ee325fcb0fa213869128285360a6d76bb33cb
 * stale: false
 * tags: [code/hyperbolic_function, code/derivable_function_contract]
 * concepts: [Inverse Trigonometric Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class ArcCos
extends AFloatDeriveAble {

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)
		Werte-Bereich nur [-1,+1]	*/
	final static public MetricBody ARC_COS(Object arg) {
		return  ((MetricBody) arg).ArcCos(); }

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)
		Werte-Bereich nur [-1,+1]	*/
	final static public double ARC_COS(double x) {
		return IMeasurAble.PI_HALF - ArcSin.ARC_SIN.Map(x);} //Werte-Bereich nur [-1,+1]

	/**Local Reference to the single Instance	 */
	final static public ArcCos ARC_COS = new ArcCos();

	static { //Initializer
		ARC_COS.setInverse   (Cosinus.Cosinus);
		ARC_COS.setDerivative(new CatDerive(Neg.NEG, Algebra.InvSqRt1_xx));
		ARC_COS.setIntegral  (new Diff(
							 new Prod(Identity.IDENTITY, ARC_COS),
							 Algebra.SqRt1_xx));
	}

	/**Initializing private Constructor (Singleton):
	 * sets the Inverse: Cosinus
	 * the Derivative:   -1/SqRt(1-x*x)
	 * and the Integral: x*ArcCos(x)-SqRt(1-x*x)
	 */
	private ArcCos() { }

	/**This Function represents the ArcCos Function.	 */
	public Object Map (Object arg) { return ARC_COS(arg); }

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)
		Werte-Bereich nur [-1,+1]	*/
	public double Map(double x) { return ARC_COS(x);} //Werte-Bereich nur [-1,+1]

	/**Returns the ArcCos Function's Derivative, the Negative of ArcSin's Derivative at x.
	 * @return The Derivative at x	 */
	public double getDerivative(double x) {
		return -ArcSin.ARC_SIN.getDerivative(x); }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		double ret = ArcSin.ARC_SIN.getFuncDerive(x, derivative);
		derivative.Value = -derivative.Value;
		return IMeasurAble.PI_HALF - ret; }

}
