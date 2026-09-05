package function.derive.ring.body;

import java.io.IOException;

import math.vector.VectorString;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.group.ring.metric.body.ABodyDouble;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;
import function.byref.ByRefLong;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.Quot;

/**This Class encapsulates the Exponential Integral (EI) Function.
 * It can be calculated by the Power Series or a Continued Fraction.
 * In fact, EI(i*x) = -CI(x) + i*(SI(x) -Pi/2)
 *         x                          1      2
 *         | Exp (t)                 x      x
 * EI (x)= | ------ dt=C + Ln (x) + ---  + --- + ...
 *       -Inf   t                   1*1!   2*2!
 *
 * For large x this Integral is dominated by the Factor Exp(x)/x
 * For small x this Integral approximates Ln (x)+C
 *
 * This is related to the general exponential Integral ExpInt
 * and thus to the incomplete Gamma Function:
 *
 * EI(x) = -ExpInt(1, -x) = -GammaP(0, -x)
 * i.e. EI is the analytical Continuation of ExpInt anf GammaP
 * for negative x.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:43:08Z
 * digest: a2d65a19b857d1ebd7dd60c49fb7f9ec6534e816c084d474ac7821f93b47999f
 * stale: false
 * tags: [code/numerical_integration, code/exponential_function]
 * concepts: [Special Functions, Exponential Integral]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * See Numerical Recipes 2nd Ed. p257 (6.9.8)	 */
public class EI
extends AFloatDeriveAble	//IPartialDerive //
{
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(EI.class);

	////////////////////////////////////////////////////////////////////////////

	/**Local Reference to the single Instance	 */
	final static public EI EI = new EI();

    static { //Initializer
		EI.setDerivative(new Quot(	Exponential.EXPONENTIAL,
									Identity   .IDENTITY));
    }

	/**private Constructor for Singleton Implementation	 */
	private EI(){}

	/**This Function represents the Exponential Integral (EI) Function.  */
	public Object Map (final Object arg) { return EXP_INTEGRAL((MetricBody) arg); }

	/**This Function represents the Exponential Integral (EI) Function.  */
	public double Map (final double arg) { return EXP_INTEGRAL(arg); }

	/**Returns the Exponential Integral's Derivative, Exp(x)/x.
	 * @return The Derivative at x	 */
	public double getDerivative(final double x) { return Math.exp(x)/x; }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(final double x, final ByRefDouble derivative) {
		derivative.Value = Math.exp(x)/x;
		return EXP_INTEGRAL(x); }
		
	/////////////////////////////////////////////////////////////////////////////////////

	/**The real Exponential Integral (EI) calculated by the Power Series only.
	 * very much like the complex CI_SI Function.
	 * In fact, EI(i*x) = CI(x) + i*SI(x)
	 *         x                          1      2
	 *         | Exp (t)                 x      x
	 * Ei (x)= | ------ dt=C + Ln (x) + ---  + --- + ...
	 *       -Inf   t                   1*1!   2*2!
	 *
	 * Converges slowly, but reliable for large x (>2),
	 * This Calculation is optimized for positive real Arguments less than 2
	 * See Numerical Recipes 2nd Ed. p257 (6.9.8)	 */
	final static public MetricBody EI_PR(final MetricBody x) {
		MetricBody Summe  = (MetricBody) x.ln().addAt(x).addAt(IMeasurAble.EulerC);
		MetricBody Faktor = (MetricBody) ((MetricBody) x.sqr()).halfAt();
		ByRefLong  Z1 = new ByRefLong(2);
		while (Faktor.AbsV().isMoreThan(Summe.mulAbsAccuracy()))	//Since Summe is NOT of Order 1
		{	//Since EI is growing fast, it has to be considered for Convergence
			Summe.addAt(Faktor.div(Z1)); ++Z1.Value;
			Faktor.divAt(Z1).mulAt(x);	//{konvergiert langsam}
		}
		return	(MetricBody) Summe.addAt(Faktor.divAt(Z1)); }

	/**The complex Ci_Si Function is calculated by a continued Fraction
	 * This Calculation is optimized for positive real Arguments.
	 * The Number of Iterations must not exceed 20,
	 * because then Rounding Errors falsify the Result.
	 * Using modified Lentz's Method to evaluate continued Fraction.
	 * see Numerical Recipes 2nd Ed. p257 (6.9.9)	 */
	private static final MetricBody EI_KB (MetricBody arg) {
		MetricBody sum = (MetricBody) ((MetricBody) arg.newInstance()).zeroAt();
		MetricBody term= (MetricBody) ((MetricBody) arg.newInstance()). oneAt();
		MetricBody prev= (MetricBody)				arg.newInstance();
		ByRefLong k = new ByRefLong(0);
		while (++k.Value <= ByRefInt.MAX_ITER) {
			prev.copyAt(term);	//Since the Sum is of O(1), use Accuracy directly
			if (((MetricBody) term.divAt(arg).mulAt(k)).notMoreThan(arg.Accuracy())) break;
			if (term.isLessThan(prev)) sum. addAt(term);			//Still converging, add the new Term
			else				{sum.subAt(prev); break;}	//starts diverging, remove last Term
		}
		return (MetricBody) arg.exp().divAt(arg).mulAt(sum.inc()); }

	/**The complex Ci and Si Functions are defined by:
	 * F (x) =Ci(x)+i*Si(x) = Int[0,x] (e^it)/t = Int[0,x] (Cos(t)/t + i Sin(t)/t)
	 * This Calculation is optimized for positive real Arguments.
	 * For negative Arguments it is SI(-x)=-SI(x) and CI(-x)=CI(x)-i*Pi
	 * The Residuum i*Pi is not supplied, because it interferes with SI.
	 * See Numerical Recipes 2nd Ed. p 257 (6.9.8)	 */
	final static public MetricBody EXP_INTEGRAL(MetricBody arg)
	{	//Threshold Value here dependent on wanted Accuracy (16 to 20).
		if (arg.negative()) throw new AbstractMethodError();
	    if (((IMeasurAble) arg).getDouble() >= arg.getAccuracyBits())
			 return EI_KB(arg);	  //Kettenbruchentwicklung auswerten}
		else return EI_PR(arg); } //Beide Potenzreihen gleichzeitig ausfuehren

	/**The real Exponential Integral (EI) calculated by the Power Series only.
	 * very much like the complex CI_SI Function.
	 * In fact, EI(i*x) = CI(x) + i*SI(x)
	 *         x                          1      2
	 *         | Exp (t)                 x      x
	 * Ei (x)= | ------ dt=C + Ln (x) + ---  + --- + ...
	 *       -Inf   t                   1*1!   2*2!
	 *
	 * Converges slowly, but reliable for large x (>2),
	 * This Calculation is optimized for positive real Arguments less than 2
	 * See Numerical Recipes 2nd Ed. p257 (6.9.8)	 */
	final static public double EI_PR(double x) {
		double Summe  =  Math.log(x) + x + IMeasurAble.EULER_C;
//		if (x <= AOrderAble.DoubleAccuracy) return Summe; //already treated below!
		double Faktor =  x*x*IMeasurAble.HALF;
		long Z1 = 2;
		while (Math.abs(Faktor) > ByRefDouble.MUL_ABS_ACCURACY(Summe))	//Since EI is NOT of Order 1
		{	//and growing fast, it has to be considered for Convergence
			Summe += Faktor / Z1;
			Faktor*= x/(++Z1);	//{konvergiert langsam}
		}
		return	 Summe + Faktor / Z1; }

	/**The complex Ci_Si Function is calculated by a continued Fraction
	 * This Calculation is optimized for positive real Arguments.
	 * The Number of Iterations must not exceed 20,
	 * because then Rounding Errors falsify the Result.
	 * Using modified Lentz's Method to evaluate continued Fraction.
	 * see Numerical Recipes 2nd Ed. p257 (6.9.9)	 */
	private static final double EI_KB (double arg) {
		double sum =  ICountAble.ZERO;
		double term=  ICountAble.ONE ;
		double prev;
		long k = 0;
		while (++k <= ByRefInt.MAX_ITER) {
			prev = term;	//Since the Sum is of O(1), use Accuracy directly
			if ((term *= k/arg) <= ByRefDouble.DoubleAccuracy) break;
			if (term < prev) sum += term;			//Still converging, add the new Term
			else			{sum -= prev; break;}	//starts diverging, remove last Term
		}
		return Math.exp(arg)*(sum + ICountAble.ONE)/arg; }

	/**The complex Ci and Si Functions are defined by:
	 * F (x) =Ci(x)+i*Si(x) = Int[0,x] (e^it)/t = Int[0,x] (Cos(t)/t + i Sin(t)/t)
	 * This Calculation is optimized for positive real Arguments.
	 * For negative Arguments it is SI(-x)=-SI(x) and CI(-x)=CI(x)-i*Pi
	 * The Residuum i*Pi is not supplied, because it interferes with SI.
	 * See Numerical Recipes 2nd Ed. p 257 (6.9.8)	 */
	final static public double EXP_INTEGRAL(double arg)
	{	//Threshold Value here dependent on wanted Accuracy (16 to 20).
		if (arg <= ICountAble.ZERO) throw new AbstractMethodError();
	    if (arg >= IMeasurAble.DOUBLE_MANTISSA_BITS)
			 return EI_KB(arg);	  //Kettenbruchentwicklung auswerten}
		else return EI_PR(arg); } //Beide Potenzreihen gleichzeitig ausfuehren

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Values of the Exponential Integral (EI) Function in pairs: {x, EI(x)}	 */
	final static public float[][]
		ValuesEI = {
					{ 0.1f, -1.622810f},
					{ 0.2f, -0.821761f},
					{ 0.3f, -0.302669f},
					{ 0.4f,  0.104768f},
					{ 0.5f,  0.454220f},
					{ 0.6f,  0.769881f},
					{ 0.7f,  1.064910f},
					{ 0.8f,  1.347400f},
					{ 0.9f,  1.62281f},
					{ 2.2f,  5.73261f},
					{ 2.4f,  6.60067f},
					{ 3.5f, 13.9254f},
					{ 3.9f, 18.3157f},
					{ 5.0f, 40.1853f},
					{ 5.4f, 54.1935f},
					{11.0f,  6071.41f},
					{12.0f, 14959.5f},
					{12.5f, 23565.1f},
					{13.0f, 37197.7f},
					{13.5f, 58827.0f},
					{14.0f, 93192.5f},
					{14.5f, 147866.f},
					{15.0f, 234956.f}
					};

	/**Tests all Methods of this Class	 */
	public static void testIt() throws IOException {
		L.n("Testing EI Function:");
		L.n(VectorString.FORMAT("x", 8)).l(
			VectorString.FORMAT("Expected", 22)).l(
			VectorString.FORMAT("EI_PR", 22)).l(
			VectorString.FORMAT("EI_KB", 22)).l(
			VectorString.FORMAT("EI(x)", 22)).l(
			VectorString.FORMAT("EI(x)", 22));
		final ABodyDouble x = new BodyDouble();
		for (int i = ValuesEI.length; --i >= 0;) {
			final float[] xyPair = ValuesEI[i];
			x.value = xyPair[0];
			final float eiDouble = (float) EXP_INTEGRAL(xyPair[0]); 
			final MetricBody eiBody = EXP_INTEGRAL(x); 
			L.n(ByRefDouble.FORMAT(xyPair[0], - 8, 2)).l(
				ByRefDouble.FORMAT(xyPair[1], -22, 7)).l(
				ByRefDouble.FORMAT(((ABodyDouble) EI_PR(x)).value, -22, 7)).l(
				ByRefDouble.FORMAT(((ABodyDouble) EI_KB(x)).value, -22, 7)).l( 
				ByRefDouble.FORMAT(((ABodyDouble) eiBody).value, -22, 7)).l( 
				ByRefDouble.FORMAT(eiDouble, -22, 7));
			Assert.EQUALS(eiDouble, xyPair[1]);
			Assert.EQUALS(eiBody, new Double(xyPair[1]));
		}
		L.readString(); }

	/**The main entry point for the application.
	 * Prints out the Factorial of the Value passed via the Command Line, 
	 * otherwise performs the self-test.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		if (args.length > 0) {
			System.out.println(EXP_INTEGRAL(Double.parseDouble(args[0])));
		} else {
			testIt(); 
		}
	}
	
}
