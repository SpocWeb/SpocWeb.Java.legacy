package function.derive.ring.body;	//Function;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.Algebra;
import function.derive.ring.Prod;
import function.derive.ring.Sum;

/**This Class encapsulates the ArcSin Function.  */
final public class ArcSin
extends AFloatDeriveAble //IPartialDerive //
{
	/**Returns the Arcus Sinus of the Angle x: ArcSin(x)
		Werte-Bereich nur [-1,+1]	*/
	final static public MetricBody ARC_SIN(Object arg) {
		return  ((MetricBody) arg).ArcSin(); }

	/*	Returns ArcSin(x) = ln() for x > 1
		Werte-Bereich nur [-1,+1]	*/
	final static public double ARC_SIN(double x) {
		return Math.atan(Math.sqrt(ICountAble.ONE - x*x)); }	//

	/**Local Reference to the single Instance	 */
	final static public ArcSin ARC_SIN = new ArcSin();

	static { //Initializer
		ARC_SIN.setInverse   (Sinus.SINUS);
		ARC_SIN.setDerivative(Algebra.InvSqRt1_xx);
		ARC_SIN.setIntegral  (new Sum (Algebra .SqRt1_xx,
							 new Prod(Identity.IDENTITY, ARC_SIN)));
	}

	/**private Constructor for Singleton Implementation
	 * Sets the Inverse: Sinus
	 * the Derivative: 1/SqRt(1-x*x)
	 * and the Integral: x*ArcSin x + SqRt(1-x^2)	 */
	private ArcSin(){ }

	/**This Function represents the ArcSin Function.	 */
	public Object Map (Object arg) { return ARC_SIN(arg); }

	/*	Returns ArcSin(x) = ln() for x > 1
		Werte-Bereich nur [-1,+1]	*/
	public double Map(double x) { return ARC_SIN(x); }	//

	/** @return The Derivative at x	 */
	public double getDerivative(double x) {
		return ICountAble.ONE / Math.sqrt(ICountAble.ONE - x*x); }
//		return ArcSinDerive(x); }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		double SqRt1_xx = Math.sqrt(ICountAble.ONE - x*x);
		derivative.Value = ICountAble.ONE / SqRt1_xx;
		return Math.atan(SqRt1_xx); }
//		return ArcSinFuncDerive(x, derivative); }

//not necessary to declare more Functions, use the static Object ArcSin instead!

//	public static double ArcSinFuncDerive(double x, ByRefDouble derivative) {

//	public static double ArcSinDerive(double x) {

}
