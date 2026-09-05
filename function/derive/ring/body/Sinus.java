package function.derive.ring.body;

//import Stream.Copy.*;
import java.io.IOException;

import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.CatDerive;
import function.derive.ring.HalfAt;
import function.derive.ring.Inv;
import function.derive.ring.MulAt;
import function.derive.ring.Neg;
import function.derive.ring.Quot;
import function.derive.ring.Square;
import function.derive.ring.Succ;

/**This Class encapsulates the Sinus Function.
 * Like 'Cosinus' it solves this linear Differential Equation: f'' = -f
 * It has the following Properties:
 * Sinus'     = Cosinus
 * Int[Sinus] = -Cosinus
 * Inv(Sinus) = ArcSin
 *
 * sin(-x) == -sin(x) 	(Asymmetric)
 * sin( x) == sin(x+2*Pi) (Periodic)
 * sin(-x) == sin(x+  Pi)
 * |sin(x)| <= +1
 * sin(n*Pi) == 0
 * sin^2 + cos^2 == 1
 * 2*sin^2(x) == 1-cos(2*x)
 * 4*sin^3(x) == 3*sin(x)-sin(3*x)
 * 8*sin^4(x) == cos(4*x)-4*cos(2*x)+3
 *
 * Addition Theorem
 * sin(x+/-y) == sin(x)*cos(y) +/- cos(x)*sin(y)
 * sin(x+y+z) == sin(x)*cos(y)*cos(z) + cos(x)*sin(y)*cos(z) +
 * 				 cos(x)*cos(y)*sin(z) - sin(x)*sin(y)*sin(z)
 * sin(2*x) == 2*sin(x)*cos(x)
 * sin(3*x) == sin(x)*(3 - 4*sin^2(x))
 * sin(4*x) == 4*sin(x)*(2*cos^3(x) - cos(x))
 * sin(x/2) == +/-SqRt((1-cos(x))/2)
 *
 * sin(2*x) +/- sin(2*y) == 2*sin(x+/-y) * cos(x-/+y)
 * 2*sin(x)  *  sin(y)   == cos(x-y) - cos(x+y)
 *
 * Power Series (converges for all x):
 * sin(x) = x - x^3/3! + x^5/5! -... + (-1)^n*x^(2*n+1)/(2n+1)!
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:43:17Z
 * digest: bd322cf4c58c97b2fc182214c826f16015b5951035c383166c2ddc08f882a685
 * stale: false
 * tags: [code/trigonometric_function, code/derivable_function_contract]
 * concepts: [Trigonometric Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class Sinus
extends AFloatDeriveAble 	//IPartialDerive //
{
	/**Local Reference to the single Instance	 */
	final static public Sinus SINUS = new Sinus();

	/** Sinc-Function :      Sin  (x)/x
	 * @param arg any Number
	 * @return Sinc(x) = Sin(x)/x
	 */
	final static public double Sinc(double arg) {
		if (arg == ICountAble.ZERO) return ICountAble.ONE;
		return Math.sin(arg)/arg; }

	/**Sinc-Function :      Sin  (x)/x
	 * Unfortunately this Definition does not extend to x = 0,
	 * where the Value becomes 1  */
	final static public Quot Sinc =  new Quot(Sinus.SINUS, Identity.IDENTITY);

	/**Airy Function is used e.g. in Light and Electro Dynamics for Reflexions	 */

	/**The standardized Airy Function with Finesse F == 1: 1/(1+Sin^2) */
	final static public CatDerive Airy = new CatDerive(Square.SQUARE,
										 new CatDerive(Sinus .SINUS, HalfAt.HalfAt));

	/** Airy-Function with Finesse F == 1: 1/(1+Sin^2)
	 * Fastest Implementation with Airy Characteristics, not normed
	 * @param arg any Number
	 * @return Airy(x) = 1/(1+sin^2(x))
	 */
	public static MetricBody Airy(MetricBody arg) {
		MetricBody ret = arg.sin(); ret.sqrAt(); ret.inc(); ret.invAt();
		return ret;	}

	/** Airy-Function with Finesse F == 1: 1/(1+Sin^2)
	 * Fastest Implementation with Airy Characteristics, not normed
	 * @param arg any Number
	 * @return Airy(x) = 1/(1+sin^2(x))
	 */
	public static double Airy(double arg) {
		double ret = Math.sin(arg);
		return ICountAble.ONE/
			  (ICountAble.ONE + ret*ret); }

	/** Airy-Function with Finesse F: 1/(1+F*Sin^2 (x/2))
	 * when the Finesse is null, it is assumed to 1.
	 * @param arg any Number
	 * @param F Finess of the System.
	 * @return Airy(x, F) = 1/(1+F*sin^2(x))
	 */
	public static double Airy(double arg, double F) {
		double ret = Math.sin(IMeasurAble.HALF * arg);
		return ICountAble.ONE/
			  (ICountAble.ONE + F*ret*ret); }

	/** Airy-Function with Finesse F: 1/(1+F*Sin^2 (x/2))
	 * when the Finesse is null, it is assumed to 1.
	 * @param arg any Number or MetricIRing
	 * @param F Finesse of the System
	 * @return Airy(x, F) = 1/(1+F*sin^2(x))
	 */
	public static MetricBody Airy(MetricBody arg, Object F) {
		MetricBody x_2 = (MetricBody) arg.half();
		if (F == null) return Airy(x_2);
		x_2.sinAt().sqrAt().mulAt(F); x_2.inc(); x_2.invAt();
		return x_2;	}

	/** Returns an Airy Function with the given Finesse.
	 * @param Finesse Finesse of the System
	 * @return An Airy Function with the given Finesse
	 */
	final static public CatDerive getAiry(Object Finesse) {
		return 	new CatDerive(Inv .INV ,
				new CatDerive(Succ.SUCC,
				new CatDerive(new MulAt(Finesse), Airy))); }

	//	Sinus Section

	static { //Initializer
		SINUS.setInverse   (ArcSin .ARC_SIN );
		SINUS.setDerivative(Cosinus.Cosinus);
		SINUS.setIntegral  (new CatDerive(Neg.NEG, Cosinus.Cosinus));
	}

	/**private Constructor for Singleton Implementation	 */
	private Sinus() { }

	/** This Function represents the Sinus Function of any MetricIRing.
	 * @param arg any Number or MetricIRing
	 * @return Sinus(x)
	 */
	public Object Map (Object arg) { return ((MetricBody) arg).sin(); }

	/** Returns the Sinus Function Value (mapping) of the 'double' Argument arg
	 * @param arg any Number, throws loss of Accuracy, when too large.
	 * @return sin(x)
	 */
	public double Map (double arg) { return Math.sin(arg); }

	/**Returns the Sinus Function's Derivative: Cos(x) for all x.	 */
	public double getDerivative(double x) { return Math.cos(x); }	//

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble Derivative) {
		Derivative.Value = Math.cos(x); //Optimization possible, but equally slow.
		return Math.sin(x); }

	/**Returns Sinus(x)
	 * Expects x to be in the Range [-Pi/4, +Pi/4]	 */
	protected static IMetricIRing SinPi_4(IMetricIRing x) {	//sin(x) = -sin(-x)
		if (!x.negative()) return	PRsin(x);	//positive Values speed up convergence testing
		x.negAt();
		IMetricIRing tmp = PRsin(x); tmp.negAt();
		return tmp; }	//caching is cheaper than casting!

	/**Returns Sinus(x)
	 * Expects x to be in the Range [-Pi/4, +Pi/4]	 */
	protected static double SinPi_4(double x) {	//sin(x) = -sin(-x)
		if (x >= ICountAble.ZERO) return	PRsin(x);	//positive Values speed up convergence testing
		return -PRsin(-x); }

	/**Power Series for Sinus(x)
	 * x must be in the Range [0, Pi/4]	 */
	protected static IMetricIRing PRsin(IMetricIRing x) {
		boolean add = true;
		IMetricIRing Accuracy= (IMetricIRing)x.mulAccuracy();	//speeds up testing
	    IMetricIRing Quadrat	= (IMetricIRing)x.sqr ();	//).neg();	//Factor changes Sign every times.
	    IMetricIRing Summe	= (IMetricIRing)x.copy();
	    IMetricIRing Faktor	= x;
		int Z1 = 1;	ByRefLong Divisor = new ByRefLong();
		do {	//faster Convergence Checking when Factor stays positive!
			Divisor.Value = ++Z1;
			Divisor.Value*= ++Z1; 	//potentially weakly defined!!!
			Faktor.divAt(Divisor).mulAt(Quadrat);
			if (add = !add)	Summe.addAt (Faktor);
			else			Summe.subAt(Faktor);
	    } while (Faktor.isMoreThan(Accuracy));
		return Summe; }

	/**Power Series for Sinus(x)
	 * x must be in the Range [0, Pi/4]	 */
	protected static double PRsin(double x) {
		boolean add = true;
		double Accuracy = ByRefDouble.MUL_ACCURACY(x);	//speeds up testing
	    double Quadrat  = x*x;	//).neg();	//Factor changes Sign every times.
	    double Summe    = x;
	    double Faktor	= x;
		int Z1 = 1;
		do {	//faster Convergence Checking when Factor stays positive!
			Faktor *= Quadrat / ((++Z1) * (++Z1));
			if (add = !add)	Summe += Faktor;
			else			Summe -= Faktor;
	    } while (Faktor > Accuracy);
		return Summe; }

	/**Returns Sinus(x), exploiting the 2*Pi Periodicity to reduce x into the Base Range first.	 */
	public static IMetricIRing  sin(IMetricIRing x) {	//return ((MetricIRing)copy()).sinAt(); }
		return (IMetricIRing) SinPi(x.Rem(IMeasurAble.TwoPi)); }	//sin(x) = sin (x+2Pi) Range (-pi,+pi)

	/**Returns the Sinus of the angle x, but modifies x: sin(x)
	 * x must be in the Range of [-Pi, +Pi]
	 * Rather use sin() as the base for trigonometric calculations
	 * than tan(), because the coefficients are easier to calculate.	 */
	protected static IMetricIRing SinPi(IMetricIRing x) {	//return ((MetricIRing)copy()).sinAt(); }
		//sin(x) = sin (x+2Pi) Range (-pi,+pi)
		boolean negativ; if (negativ = x.negative()) x.negAt();		//Range: [0, Pi]
			 if (x.isMoreThan  (IMeasurAble.ThreePiQuarter)) { //sin(x) = -sin(x-Pi) = sin(Pi-x)
				 x.subAt(IMeasurAble.pi).negAt(); x = PRsin(x); }	//Range: [0, Pi/4]
		else if (x.isMoreThan  (IMeasurAble.PiQuarter)) {		//sin(x) = cos(x+Pi/2) = -cos(x-Pi/2)
				 x.subAt(IMeasurAble.PiHalf); x = Cosinus.cos(x); }	//Range: [-Pi/4, +Pi/4]
		else x = PRsin(x);
		if (negativ)return (IMetricIRing) x.negAt();
		else		return x; }

	/**Airy Function is used e.g. in Light and Electro Dynamics for Reflexions	 */
	/**Tests Airy-Function with Finesse F: 1/(1+F*Sin^2 (x/2))	 */
	public static void testAiry() throws IOException { }

	/**Tests the Sin() Function	 */
	private static void testSin() throws IOException {
		MetricBody test = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody zero = (MetricBody) test.zero();	//= 0
		MetricBody Pi6  = (MetricBody) test.piSixth();
		MetricBody Pi4  = (MetricBody) test.piQuarter();
		MetricBody Pi3  = (MetricBody) test.piThird();
		MetricBody Pi2  = (MetricBody) test.piHalf();

		System.out.println ();
		System.out.println ("Teste : Sin");
		System.out.println ("Soll : " + 0 + "   Ist : " + zero.sin());
		System.out.println ("Soll : " + 0.5 + "   Ist : " + Pi6.sin());
		System.out.println ("Soll : " + 1.0/Math.sqrt(2) + "   Ist : " + Pi4.sin());
		System.out.println ("Soll : " + Math.sqrt(3)/2 + "   Ist : " + Pi3.sin());
		System.out.println ("Soll : " + 1 + "   Ist : " + Pi2.sin());
		System.in.read(); System.in.read();
	}

	/**Tests the Sin() Function	 */
	private static void testIt(final String[] args) throws IOException {
		testSin();
		testAiry();
	}

	/**
	  * The main entry point for the application.
	  *
	  * @param args Array of parameters passed to the application
	  * via the command line.	 */
	public static void main(final String[] args)
	throws IOException {
		testIt(args); 
	}
	
}
