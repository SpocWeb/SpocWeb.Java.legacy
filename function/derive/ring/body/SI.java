package function.derive.ring.body;

import java.io.IOException;

import math.vector.VectorString;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.AMetricIRing;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;
import function.derive.AFloatDeriveAble;

/**This Class encapsulates the real part of the Exponential Integral (SI)
 * which is the Integral of the Sinc Function.
 * It can be calculated by the Power Series or a Continued Fraction.
 * In fact, SI(i*x) = -CI(x) + i*(SI(x) -Pi/2)
 *         x                  3      5
 *         | Sin (t)         x      x
 * SI (x)= | ------- dt=x - ---  + ---  - ...
 *         0    t           3*3!   5*5!
 * For large x this Integral is dominated by the Factor Sin(x)/x
 * For small x this Integral approximates x.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:42:39Z
 * digest: 6022a25baeace861a426b72acc8588f4594b80ed049aebf664b9f1db127edf9b
 * stale: false
 * tags: [code/numerical_integration, code/trigonometric_function]
 * concepts: [Special Functions, Sine Integral]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * See Numerical Recipes 2nd Ed. p257 (6.9.8)	 */
final public class SI
extends AFloatDeriveAble	//IPartialDerive //
{

	/**Local Reference to the single Instance	 */
	final static public SI SI = new SI();

    static { //Initializer
		SI.setDerivative (Sinus.Sinc);
    }

	/**private Constructor for Singleton Implementation	 */
	private SI() { }

	/**This Function represents the SI (Integral of the Sinc) Function.  */
	public Object Map (Object arg) { return SIN_INTEGRAL((MetricBody) arg); }

	/**This Function represents the SI (Integral of the Sinc) Function.  */
	public double Map (double arg) { return SIN_INTEGRAL(arg); }

	/**Returns the Sine Integral's Derivative, the Sinc Function Sin(x)/x.
	 * @return The Derivative at x	 */
	public double getDerivative(double x) {
		return Math.sin(x)/x; }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		derivative.Value = Math.sin(x)/x;
		return SIN_INTEGRAL(x); }

	//  static Calculation Methods taking all Parameters


	/**The real Sine Integral calculated by the Power Series only.
	 * very much like the complex CI_SI Function.
	 * In fact, SI(i*x) = CI(x) + i*SI(x)
	 *         x                  3      5
	 *         | Sin (t)         x      x
	 * SI (x)= | ------- dt=x - ---  + ---  - ...
	 *         0    t           3*3!   5*5!
	 * Rounding Errors for large x (>2), use CI_SI instead.	 */
	final static public MetricBody SIN_INTEGRAL(MetricBody x) {
		MetricBody Quadrat	= (MetricBody) ((IGroup) x.sqr()).negAt();
		MetricBody Summe	= (MetricBody) x.copy();
		MetricBody Faktor   = (MetricBody)
							 ((MetricBody) x.mul(Quadrat)).halfAt().thirdAt();
		ByRefLong  Z1 = new ByRefLong(3);
		ByRefLong  Pr = new ByRefLong( );
		while (Faktor.AbsV().isMoreThan(AMetricIRing.BaseAccuracy)) { 	//Since Summe is of Order 1
			Summe.addAt(Faktor.div(Z1)); Pr.Value =  (++Z1.Value)*(++Z1.Value);
			Faktor.divAt(Pr).mulAt(Quadrat); }
		return	(MetricBody) Summe.addAt(Faktor.divAt(Z1)); }

	/**The real Sine Integral calculated by the Power Series only.
	 * very much like the complex CI_SI Function.
	 * In fact, SI(i*x) = CI(x) + i*SI(x)
	 *         x                  3      5
	 *         | Sin (t)         x      x
	 * SI (x)= | ------- dt=x - ---  + ---  - ...
	 *         0    t           3*3!   5*5!
	 * Rounding Errors for large x (>2), use CI_SI instead.	 */
	final static public double SIN_INTEGRAL(double x) {
		double Quadrat	=  -x*x;
		double Summe	=   x;
		double Faktor   =   x*Quadrat/6.0; //ICountAble.SIX;
		long Z1 = 3;
		while (Math.abs(Faktor) > ByRefDouble.DoubleAccuracy) { 	//Since Summe is of Order 1
			Summe += Faktor / Z1;
			Faktor *= Quadrat/((++Z1)*(++Z1)); }
		return Summe + Faktor / Z1; }

	/**The real Sine Integral SI Function, calculated by a continued Fraction
	 * This Calculation is optimized for positive real Arguments.
	 * The Number of Iterations must not exceed 20,
	 * because then Rounding Errors falsify the Result.
	 * Using modified Lentz's Method to evaluate continued Fraction.
	 * see Numerical Recipes 2nd Ed. p257 (6.9.9)	 */
/*	private static final MetricBody SI_KB (MetricBody arg) {
		MetricBody sum = (MetricBody) ((MetricBody) arg.newInstance()).zeroAt();
		MetricBody term= (MetricBody) ((MetricBody) arg.newInstance()). oneAt();
		MetricBody prev= (MetricBody)				arg.newInstance();
		ByRefLong k = new ByRefLong(0);
		while (++k.Value <= AOrderAble.MaxIter) {
			prev.copyAt(term);	//Since the Sum is of O(1), use Accuracy directly
			if (((MetricBody) term.divAt(arg).mulAt(k)).lessEq(arg.Accuracy())) break;
			if (term.less(prev)) sum. addAt(term);			//Still converging, add the new Term
			else				{sum.subAt(prev); break;}	//starts diverging, remove last Term
		}
		return (MetricBody) arg.exp().divAt(arg).mulAt(sum.inc()); }
*/
	/**The complex Ci and SI Functions are defined by:
	 * F (x) =Ci(x)+i*SI(x) = Int[0,x] (e^it)/t = Int[0,x] (Cos(t)/t + i Sin(t)/t)
	 * This Calculation is optimized for positive real Arguments.
	 * For negative Arguments it is SI(-x)=-SI(x) and CI(-x)=CI(x)-i*Pi
	 * The Residuum i*Pi is not supplied, because it interferes with SI.
	 * See Numerical Recipes 2nd Ed. p 257 (6.9.8)	 */
/*	final static public MetricBody SI(MetricBody arg)
	{	//Threshold Value here dependent on wanted Accuracy (16 to 20).
		if (arg.negative()) throw new AbstractMethodError();
	    if (((IMeasurAble) arg).getDouble() >= arg.getAccuracyBits())
			 return SI_KB(arg);	//Kettenbruchentwicklung auswerten}
		else return SI_PR(arg);	//Beide Potenzreihen gleichzeitig ausfuehren
	};
*/
	/**Tests all Methods of this Class	 */
	public static void testIt() throws IOException {
		System.out.println("Testing SI Function():");
		System.out.println(	VectorString.FORMAT("x", 8) +
							VectorString.FORMAT("Expected", 22) +
							VectorString.FORMAT("SI(x)", 22));
/*		BodyDouble x = new BodyDouble();
		int i = EI.ValuesSI.length;
		while(--i >= 0) {
			float[] xyPair = SI.ValuesEI[i];
			System.out.println(	AOrderAble.format(x.Value = xyPair[0]  , - 8, 2) +
								AOrderAble.format(          xyPair[1]  , -22, 7) +
//								AOrderAble.format(((BodyDouble) EI_KB(x)).Value, -22, 7) +
								AOrderAble.format(((BodyDouble) SI(x)).Value, -22, 7)
								);
		}
*/		System.in.read(); System.in.read(); }

}
