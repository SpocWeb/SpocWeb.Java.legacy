package function.derive.ring.body;

import java.io.IOException;

import math.vector.VectorString;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.AMetricIRing;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.Quot;

/**This Class encapsulates the real Exponential Integral (CI) Function.
 * It can be calculated by the Power Series or a Continued Fraction.
 * In fact, CI(i*x) = -CI(x) + i*(CI(x) -Pi/2)
 *           ì                           2      4
 *           | Cos (t)                  x      x
 * Ci (x)= - | ------- dt=C + Ln (x) - ---  + --- - ...
 *           x   t                     2*2!   4*4!
 * For large x this Integral is dominated by the Factor Sin(x)/x
 * For small x this Integral approximates x.
 *
 * See Numerical Recipes 2nd Ed. p257 (6.9.8)	 */
public class CI
extends AFloatDeriveAble	//IPartialDerive //
{
	/**Local Reference to the single Instance	 */
	final static public CI CI = new CI();

    static { //Initializer
//		CI.setInverse   (null);
		CI.setDerivative(new Quot(	Cosinus	.Cosinus,
									Identity.IDENTITY));
//		CI.setIntegral  (null);
    }

	/**private Constructor for Singleton Implementation	 */
	private CI() { }

	/**This Function represents the Sinus Function.  */
	public Object Map (Object arg) { return COS_INTEGRAL((MetricBody) arg); }

	/**This Function represents the Sinus Function.  */
	public double Map (double arg) { return COS_INTEGRAL(arg); }

	/** @return The Derivative at x	 */
	public double getDerivative(double x) { return Math.cos(x)/x; }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		derivative.Value = COS_INTEGRAL(x);
		return Math.cos(x)/x; }

	/**The real Cosine Integral calculated by the Power Series only.
	 * very much like the complex CI_CI Function.
	 * In fact, CI(i*x) = CI(x) + i*CI(x)
	 *           ì                           2      4
	 *           | Cos (t)                  x      x
	 * Ci (x)= - | ------- dt=C + Ln (x) - ---  + --- - ...
	 *           x   t                     2*2!   4*4!
	 * Rounding Errors for large x (>2), use CI_SI instead.	 */
	public static MetricBody COS_INTEGRAL(MetricBody x) {
		MetricBody Quadrat	= (MetricBody) ((IGroup) x.sqr()).negAt();
		MetricBody Summe	= (MetricBody) x.ln ().addAt(IMeasurAble.EulerC);
		MetricBody Faktor	= (MetricBody) Quadrat.half();
		ByRefLong  Z1 = new ByRefLong(2);
		ByRefLong  Pr = new ByRefLong();
		while (Faktor.AbsV().isMoreThan(AMetricIRing.BaseAccuracy)) {	//Since Summe is of Order 1
			Summe.addAt(Faktor.div(Z1)); Pr.Value =  (++Z1.Value)*(++Z1.Value);
			Faktor.divAt(Pr).mulAt(Quadrat);
		}
		return	(MetricBody) Summe.addAt(Faktor.divAt(Z1));	}

	/**The real Cosine Integral calculated by the Power Series only.
	 * very much like the complex CI_CI Function.
	 * In fact, CI(i*x) = CI(x) + i*CI(x)
	 *           ì                           2      4
	 *           | Cos (t)                  x      x
	 * Ci (x)= - | ------- dt=C + Ln (x) - ---  + --- - ...
	 *           x   t                     2*2!   4*4!
	 * Rounding Errors for large x (>2), use CI_SI instead.	 */
	public static double COS_INTEGRAL(double x) {
		double Quadrat	= -x*x;
		double Summe	= Math.log(x) + IMeasurAble.EULER_C;
		double Faktor	= Quadrat * IMeasurAble.HALF;
		long  Z1 = 2;
		while (Math.abs(Faktor) > ByRefDouble.DoubleAccuracy) {	//Since Summe is of Order 1
			Summe += Faktor / Z1;
			Faktor *= Quadrat/((++Z1)*(++Z1)); }
		return Summe += Faktor / Z1; }

	/**The real Sine Integral CI Function is calculated by a continued Fraction
	 * This Calculation is optimized for positive real Arguments.
	 * The Number of Iterations must not exceed 20,
	 * because then Rounding Errors falsify the Result.
	 * Using modified Lentz's Method to evaluate continued Fraction.
	 * see Numerical Recipes 2nd Ed. p257 (6.9.9)	 */
/*	private static final MetricBody CI_KB (MetricBody arg) {
		MetricBody sum = (MetricBody) ((MetricBody) arg.newInstance()).zeroAt();
		MetricBody term= (MetricBody) ((MetricBody) arg.newInstance()). oneAt();
		MetricBody prev= (MetricBody)				arg.newInstance();
		ByRefLong k = new ByRefLong(0);
		while (++k.Value <= AOrderAble.MaxIter)
		{
			prev.copyAt(term);	//Since the Sum is of O(1), use Accuracy directly
			if (((MetricBody) term.divAt(arg).mulAt(k)).lessEq(arg.Accuracy())) break;
			if (term.less(prev)) sum. addAt(term);			//Still converging, add the new Term
			else				{sum.subAt(prev); break;}	//starts diverging, remove last Term
		}
		return (MetricBody) arg.exp().divAt(arg).mulAt(sum.inc());
	}
*/
	/**The complex Ci and CI Functions are defined by:
	 * F (x) =Ci(x)+i*CI(x) = Int[0,x] (e^it)/t = Int[0,x] (Cos(t)/t + i Sin(t)/t)
	 * This Calculation is optimized for positive real Arguments.
	 * For negative Arguments it is CI(-x)=-CI(x) and CI(-x)=CI(x)-i*Pi
	 * The Residuum i*Pi is not supplied, because it interferes with CI.
	 * See Numerical Recipes 2nd Ed. p 257 (6.9.8)	 */
/*	final static public MetricBody CI(MetricBody arg)
	{	//Threshold Value here dependent on wanted Accuracy (16 to 20).
		if (arg.negative()) throw new AbstractMethodError();
	    if (((IMeasurAble) arg).getDouble() >= arg.getAccuracyBits())
			 return CI_KB(arg);	//Kettenbruchentwicklung auswerten}
		else return CI_PR(arg);	//Beide Potenzreihen gleichzeitig ausfuehren
	};
*/
	/**Tests all Methods of this Class	 */
	public static void testIt() throws IOException {
		System.out.println("Testing CI Function():");
		System.out.println(	VectorString.FORMAT("x", 8) +
							VectorString.FORMAT("Expected", 22) +
							VectorString.FORMAT("CI(x)", 22));
/*		BodyDouble x = new BodyDouble();
		int i = testByRef.ValuesCI.length;
		while(--i >= 0) {
			float[] xyPair = testByRef.ValuesCI[i];
			System.out.println(	AOrderAble.format(x.Value = xyPair[0]  , - 8, 2) +
								AOrderAble.format(          xyPair[1]  , -22, 7) +
//								AOrderAble.format(((BodyDouble) CI_KB(x)).Value, -22, 7) +
								AOrderAble.format(((BodyDouble) CI(x)).Value, -22, 7)
								);
		}
*/		System.in.read(); System.in.read(); }

}
