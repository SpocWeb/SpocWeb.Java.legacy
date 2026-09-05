package function.derive.ring.body;

import java.io.IOException;

import math.vector.VectorString;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.body.ABodyDouble;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import streamIO.object.IStreamIn;
import function.AFunction;
import function.ICountAble;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.combinatoric.CombiFuncs;

/**This Class encapsulates the Logarithm of the Gamma Function,
 * which is the Extension to the Factorial in the real Domain: Gamma(n+1) = n!
 *
 * Therefore it fulfills the Equation: Gamma(n+1) = n * Gamma(n)
 *
 * This Approximation converges for all complex z.
 *
 * GammaLn	= Ln(GammaFactor (x-1.0))     -GammaFactorLn(x)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:44:00Z
 * digest: b5953fffab7a1eec365f8d326aae93d3bd688fd5e4464674096c3bc2dcb9265e
 * stale: false
 * tags: [code/gamma_function, code/mathematical_function]
 * concepts: [Special Functions, Gamma Function]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * Gamma	=	 GammaFactor (x-1.0)  * e^-GammaFactorLn(x) */
public class GammaLn
extends AFunction
implements IFloatFunction //deriveAble	//IPartialDerive //
{
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(GammaLn.class);

	////////////////////////////////////////////////////////////////////////////

	/**Local Reference to the single Instance	 */
	final static public GammaLn GAMMA_LN = new GammaLn();

	/**private Constructor for Singleton Implementation	 */
	private GammaLn(){}
	
    /**Reports that GammaLn preserves strict ascending order of its argument.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/**This Function represents the GammaLn Function.  */
	public Object Map (Object arg)	{ return GAMMA_LN(arg); }

	/** Returns the Function Value (Mapping) of the Argument arg  */
	public double Map (double arg) { return GAMMA_LN(arg); }

	/** Returns the Function Value (Mapping) of the Argument arg  */
	public float Map (float arg) { return (float) GAMMA_LN(arg); }

	/** Calculates Function and Derivative at the same time,
	 * but returns only the Function Value for now	  */
//	public float FuncDerive (float x,ByRefFloat Derivative) {
//TODO		throw new OperationNotSupported(); }

	/**This Function implements the GammaLn Function.  */
	final static public MetricBody GAMMA_LN (Object X) {
		/*groesste Ungenauigkeiten in 0 < x < 1 => Verschiebung*/
		MetricBody x = (MetricBody) X;
		if (! x.positive()) {	//Spiegelung auf x > 1
//			if (x == Math.floor(x))  throw new AbstractMethodError("Gamma- Function only for x > 0 defined!");
			return 	 (MetricBody)
					((MetricBody)
					((MetricBody)
					((MetricBody)
					x.mul(IMeasurAble.pi)).sinAt().divAt(IMeasurAble.pi)).AbsVAt()).lnAt().negAt()
					.subAt(GAMMA_LN((MetricBody)x.Resid()));
		} //Saving Division by x by using Gamma(n+1) = n * Gamma(n)
			return (MetricBody)
					GammaFactor(x.pred()).lnAt().subAt(GammaFactorLn(x));	//
	}

	/**Returns the Gamma Function stripped by the Factor: e^GammaFactorLn
	 * This Approximation converges for all complex z with Re(z) > 0
	 * and returns the modestly rising Part of Gamma.	 */
	final static public MetricBody GammaFactor (Object X) {
		/*Angepasste Stirling-Approximation mit den ersten N Polen*/
		final MetricBody x	= (MetricBody)((ICopyAble) X).copy();
		final MetricBody tmp	= (MetricBody)x.newInstance();
		final MetricBody d3	= (MetricBody)x.newInstance().copyAt(IMeasurAble.CoeffGamma[0]); /*fast 1, Zeta [?]*/
		for (int i = 1; i < IMeasurAble.CoeffGamma.length; ++i)
			d3.addAt(((MetricBody)tmp.copyAt(IMeasurAble.CoeffGamma[i])).divAt(x.inc()));
		return (MetricBody) d3.mulAt(IMeasurAble.SqRt2).mulAt(IMeasurAble.SqRtPi); }

	/**Returns the Gamma Function stripped by the Factor: e^GammaFactorLn
	 * This Approximation converges for all complex z with Re(z) > 0
	 * and returns the modestly rising Part of Gamma.	 */
	final static public double GammaFactor (double x) {
		/*Angepasste Stirling-Approximation mit den ersten N Polen*/
		double d3	= IMeasurAble.COEFF_GAMMA[0]; /*fast 1, Zeta [?]*/
		for (int i = 1; i < IMeasurAble.COEFF_GAMMA.length; ++i)
			d3 += IMeasurAble.COEFF_GAMMA[i]/(x += ICountAble.ONE);
		return  d3 * IMeasurAble.SQRT2 * IMeasurAble.SQRTPI; }

	/**Returns the Gamma Function stripped by the Factor: GammaFactor
	 * This is the Factor that explodes when x grows.	 */
	final static public MetricBody GammaFactorLn (MetricBody x) {
		MetricBody d2 =  (MetricBody) x.add(new Double(4.5)); /*Hier ist N = 6,kleinGamma = 5*/
		return (MetricBody) d2.subAt(((IGroupM)x.sub(IMeasurAble.Half)).mulAt(d2.ln())); }

	/**Returns the negative Logarithm of the Beta Function,
	 * which is the real Extension to the Reciprocal of the Binomial Coefficients:
	 * since Gamma(n+1) = n! =>
	 * Beta (k+1, (n-k)+1) =
	 * Gamma(k+1) * Gamma((n-k)+1) / Gamma(k+1 + (n-k)+1) =
	 * k!*(n-k)!/(k+(n-k))! = k!*(n-k)! / n! = 1/Combination(n,k) 	 */
	final static public MetricBody BetaLn(MetricBody x, MetricBody y) {
		return (MetricBody)
				GAMMA_LN(y)		  .subAt(
				GAMMA_LN(x.add(y))). addAt(
				GAMMA_LN(x)); }

	/**Returns the Logarithm of the Gamma Function,
	 * which is the real Extension to the Factorial: Gamma(n+1) = n!
	 *
	 * Therefore it fulfills the Equation: Gamma(n+1) = n * Gamma(n)
	 *
	 * This Approximation converges for all complex z.
	 *
	 * GammaLn	= Ln(GammaFactor (x-1.0))     -GammaFactorLn(x)
	 * Gamma	=	 GammaFactor (x-1.0)  * e^-GammaFactorLn(x)
	 */
	final static public double GAMMA_LN (double x) {  /*groesste Ungenauigkeiten in 0 < x < 1 => Verschiebung*/
		if (x <= ICountAble.ZERO) {	//Spiegelung auf x > 1
//			if (x == Math.floor(x))  throw new AbstractMethodError("Gamma- Function only for x > 0 defined!");
			return 	-Math.log(Math.abs(Math.sin(x*IMeasurAble.PI) / IMeasurAble.PI))
					-GAMMA_LN(ICountAble.ONE - x); }
		else	//Saving Division by x by using Gamma(n+1) = n * Gamma(n)
		return Math.log(GammaFactor(x - ICountAble.ONE)) - GammaFactorLn(x); } 	//

	/**Calculates the Gamma Function as Power Series
	 * with Values of the Zeta Function as Coefficients
	 *
	 * Gamma(x) = Int(0..Infinity, exp(-t)*t^(x-1))
	 *
	 * It is Gamma(n+1) = n!
	 */
	final static public double Gamma(double x) {
		x -= 1;
		final int ix = (int) x;
		if (x == ix) {	//Integer Argument, either Factorial or Infinity with Change of Sign
			if (x < 0) {
				throw new AbstractMethodError("Gamma not defined for x="+x); } 
			return CombiFuncs.Fact(ix);	}
		/*Fallunterscheidung fuer schnellere Berechnung*/
		double factorial = 1;	/*Rueckfuehrung in den Bereich [-Halb,+Halb]*/
		for (long X = Math.round (+x); --X > 0;) { factorial *=   x--; } 
		for (long X = Math.round (-x); ++X > 0;) { factorial /= ++x  ; } 
		double f = 0;
		double fa = -x;
		int Z1 = 1; while (++Z1 < IMeasurAble.CoeffZeta.length) { /*Potenzreihe mit Zeta-Funktion*/
			fa *= x;
			f += IMeasurAble.ZetaValues[Z1]*fa/Z1;
		}
		while (-Math.abs(fa) > Math.abs(f)*ByRefDouble.DoubleAccuracy) {
			fa = -fa*x; f += fa/(++Z1);}
		return factorial*Math.exp(f-IMeasurAble.EULER_C*x); }

	/**Returns the Gamma Function stripped by the Factor: GammaFactor
	 * This is the Factor that explodes when x grows.	 */
	final static public double GammaFactorLn (double x) {
		final double d2 =   x + 4.5; /*Hier ist N = 6,kleinGamma = 5*/
		return  d2 - (x - IMeasurAble.HALF)*Math.log(d2); }

	/**Returns the negative Logarithm of the Beta Function,
	 * which is the real Extension to the Reciprocal of the Binomial Coefficients:
	 * since Gamma(n+1) = n! =>
	 * Beta (k+1, (n-k)+1) =
	 * Gamma(k+1) * Gamma((n-k)+1) / Gamma(k+1 + (n-k)+1) =
	 * k!*(n-k)!/(k+(n-k))! = k!*(n-k)! / n! = 1/Combination(n,k) 	 */
	final static public double BetaLn(double x, double y) {
		return  GAMMA_LN(y)-
				GAMMA_LN(x + y) +
				GAMMA_LN(x); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Values of the Error Function in pairs: {x, ErrFc(x)}	 */
	final static public float[][]
		ValuesGamma = {
						{ 1.0f, 1.000000f},
						{ 1.2f, 0.918169f},
						{ 1.4f, 0.887264f},
						{ 1.5f, 0.886227f},
						{ 1.6f, 0.893515f},
						{ 1.8f, 0.931384f},
						{ 2.0f, 1.000000f},
						{ 0.2f, 4.590845f},
						{ 0.4f, 2.218160f},
						{ 0.5f, 1.772454f},
						{ 0.6f, 1.489192f},
						{ 0.8f, 1.164230f},
						{-0.2f, 5.8211485f},
						{-0.4f, 3.7229806f},
						{-0.5f, 3.5449078f},
						{-0.6f, 3.6969327f},
						{-0.8f, 5.7385549f},
						{-1.5f, 2.3632718f},
						{10.0f, 362880.00f},
						{20.0f, 1.216451E17f},
						{30.0f, 8.841762E30f},
						{ 1.0f, 1.000000f}
					   };

	/**Tests the Error Gamma Function with MetricBody Instances	 */
	public static void testGammaLn() throws IOException {
		L.n("Testing Gamma Function():");
		L.n(VectorString.FORMAT("x", 8)).l(
			VectorString.FORMAT("Expected", 22)).l(
			VectorString.FORMAT("GammaLn(x)", 22));
		final BodyDouble x = new BodyDouble();
		for (int i = GammaLn.ValuesGamma.length; --i >= 0; ) {
			final float[] xyPair = GammaLn.ValuesGamma[i];
			x.value = xyPair[0]; 
			final double actual = Math.exp(((ABodyDouble) GammaLn.GAMMA_LN(x)).value); 
			L.n(ByRefDouble.FORMAT(xyPair[0], - 8, 2)).l(
				ByRefDouble.FORMAT(xyPair[1], -22, 7)).l(
				ByRefDouble.FORMAT(actual, -22, 7));
			Assert.EQUALS(actual, xyPair[1], 50*ByRefDouble.DOUBLE_ACCURACY); 
		}
		L.readString();	}

	/**Tests the Error Gamma Function with double Variables	 */
	public static void testGammaLn2() {
		L.n("Testing Gamma Function():");
		L.n(VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("GammaLn(x)", -22));
		for (int i = GammaLn.ValuesGamma.length; --i >= 0; ) {
			float[] xyPair = GammaLn.ValuesGamma[i];
			final double result = Math.exp(GammaLn.GAMMA_LN(xyPair[0]));
			L.n(ByRefDouble.FORMAT(xyPair[0], - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1], -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[1], result, 50*ByRefDouble.DOUBLE_ACCURACY); 
		}
		L.readString(); 
	}

	/**Tests the Beta Function:
	 * Gamma(n+1) = n!
	 * Beta (k+1, (n-k)+1) =
	 * Gamma(k+1) * Gamma((n-k)+1) / Gamma(k+1 + (n-k)+1) =
	 * k!*(n-k)!/(k+(n-k)+1)! = k!*(n-k)! / (n+1)! = 1/(n+1)*Combination(n,k)	 */
	public static void testBeta() throws java.io.IOException {
		int k = (byte) (10 + (byte) ByRefDouble.RANDOM(11));
		L.n ("Testing Combination:");
		for (byte i = 0; i <= k; ++i) {
			final double expected = CombiFuncs.Fact(k) /
			(CombiFuncs.Fact((byte) (k-i))* CombiFuncs.Fact(i));
			final double result = CombiFuncs.Combination (k,i);
			final double result2 = Math.exp(-GammaP.BetaLn(k-i+1,i+1))/(k+1);
			L.n("Comb("+k+","+i+")\t = Soll :"+expected+"\tIst :"+result+"\tIst :"+result2);
			Assert.EQUALS(expected, result);
			Assert.EQUALS(expected, result2);
		}
		L.readString(); 
	}

	/**Tests the Beta Function with BodyDouble Instances:
	 * Gamma(n+1) = n!
	 * Beta (k+1, (n-k)+1) =
	 * Gamma(k+1) * Gamma((n-k)+1) / Gamma(k+1 + (n-k)+1) =
	 * k!*(n-k)!/(k+(n-k)+1)! = k!*(n-k)! / (n+1)! = 1/(n+1)*Combination(n,k)	 */
	public static void testBeta2() throws java.io.IOException {
		final BodyDouble i = new BodyDouble();
		ABodyDouble n = new BodyDouble();
		ABodyDouble k = new BodyDouble();
		n.value = (byte) (10 + (byte) ByRefDouble.RANDOM(11));
		L.n("Testing Combination:");
		for (i.value = 0; i.value <= n.value; ++i.value) {
			k = (ABodyDouble) n.sub(i);
			final IIntRing combin = n.Combination(i);
			final MetricBody gammaLn = GammaLn.BetaLn(
			(ABodyDouble) k.succ(), 
			(ABodyDouble) i.succ());
			final double actual = Math.exp(-
			((ABodyDouble) gammaLn ).value)/
			((ABodyDouble) n.succ()).value;
			L.n("Comb(").l(n).l(",").l(i).l(")\t = Soll :").l(
				(n.Fact().divAt(
				(k.Fact().mulAt(
				 i.Fact()))))).l(
				"\tIst :").l(combin).l(
				"\tIst :").l(actual);
			Assert.EQUALS(combin, new Double(actual));
		}
		L.readString(); 
	}

	/**Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		testGammaLn	();
		testGammaLn2();
		testBeta	();
		testBeta2	();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Syntax: java GammaLn <x>");
			testIt(); 
		} else {
			System.out.println(GAMMA_LN.Map(Double.parseDouble(args[0])));
		}
	}

}
