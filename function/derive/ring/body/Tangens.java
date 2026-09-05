package function.derive.ring.body;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.CatDerive;
import function.derive.ring.Diff;
import function.derive.ring.Neg;
import function.derive.ring.Square;
import function.derive.ring.Succ;

/**This Class encapsulates the Tangens Function.
 * It solves this Differential Equation: f' = 1+f^2
 * It has the following Properties:
 * Tangens      =  Sinus/Cosinus = 1/CoTangens
 * Tangens'     =  1+Tangens^2 = 1/Cosinus^2
 * Int[Tangens] =  -Log(Cos(x))
 * Inv(Tangens) =  ArcTan
 *
 * tan(-x) == -tan(x) 		(ASymmetric)
 * tan( x) == tan(x+Pi)		(Periodic)
 * tan( x) == cot(x+Pi/2)
 * tan(n*Pi) == 0
 *
 * Addition Theorem
 * tan(x+/-y) == (tan(x)+/-tan(y)) / (1 -/+ tan(x)*tan(y))
 * tan(2*x) == 2*tan(x)/(1-tan^2(x)) == 2/(cot(x) - tan(x))
 * tan(3*x) == tan(x)*(3 - tan^2(x))/(1-3*tan^2(x))
 * tan(4*x) == 4*tan(x)*(1-tan^2(x))/(1 - 6*tan^2(x) + tan^4(x))
 * tan(x/2) == +/-SqRt((1-cos(x))/(1+cos(x)))
 *
 * tan(x) +/- tan(y) == sin(x+/-y)/(cos(x)*cos(y))
 * tan(x)  *  tan(y) ==(tan(x)+tan(y))/(1/tan(x) + 1/tan(y))
 *
 * Power Series (converges for |x| < Pi/2):
 * tan(x) = x + x^3/3 + x^5*2/15 + x^7*17/315 + x^9*62/2835 + ...
 * 			+ x^(2*n-1)*2^(2n)*(2^(2n)-1)*Bernoulli[n]/(2n)!
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:42:55Z
 * digest: 67bb11546b0f6be09484477434add76e8999162d36cb55c3eb965bc74a5d3b32
 * stale: false
 * tags: [code/trigonometric_function, code/derivable_function_contract]
 * concepts: [Trigonometric Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Tangens
extends AFloatDeriveAble {

	/**Local Reference to the single Instance	 */
	final static public Tangens TANGENS = new Tangens();

	/**Square of the Tangens Function: tan^2	 */
	final static public CatDerive SQR_TANGENS = new CatDerive(Square.SQUARE, TANGENS);

	static { //Initializer
		 SQR_TANGENS.setIntegral  (	new Diff(TANGENS, Identity.IDENTITY)); //Inverse and Derivative are automatically calculated
			 TANGENS.setInverse   (ArcTan.ArcTan);
			 TANGENS.setDerivative(	new CatDerive(Succ.SUCC, SQR_TANGENS));
			 TANGENS.setIntegral  (	new CatDerive(Neg .NEG ,
									new CatDerive(Logarithm.LOGARITHM,
												  Cosinus  .Cosinus)));
	}

	/**Initializing private Constructor (Singleton):
	 * sets the Inverse: Tangens
	 * the Derivative:   1+tan^2 = 1/cos^2
	 * and the Integral: -Log(Cos(x))
	 */
	private Tangens(){ }

	/**This Function represents the Tangens Function.
	 * It always returns the Argument.  */
	public Object Map (final Object arg) { return ((MetricBody) arg).tan(); }

	/**Returns Tangens(x) for all x.	 */
	public double Map(final double x) { return Math.tan(x); }	//

	/**Returns the Tangens Function's Derivative: 1+tan^2(x).
	 * @return The Derivative at x	 */
	public double getDerivative(final double x) {
		double ret = Math.tan(x); return 1.0 + ret*ret; }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(final double x, final ByRefDouble derivative) {
		double ret = Math.tan(x);
		derivative.Value = 1.0 + ret*ret;
		return ret; }

}
