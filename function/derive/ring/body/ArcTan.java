package function.derive.ring.body;	//Function;

import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.ring.Algebra;
import function.derive.ring.CatDerive;
import function.derive.ring.Neg;

/**This Class encapsulates the ArcTan Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:54Z
 * digest: bd46768a0613896b508092f26f06af82e30d7b4dab306f9982ddb2704e7d2b68
 * stale: false
 * tags: [code/hyperbolic_function, code/derivable_function_contract]
 * concepts: [Inverse Trigonometric Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class ArcTan
extends AFloatDeriveAble {

	/**Local Reference to the single Instance	 */
	final static public ArcTan ArcTan = new ArcTan();

	static { //Initializer
		ArcTan.setInverse   (Tangens.TANGENS);
		ArcTan.setDerivative(Algebra.InvSqRtxxp1);
		ArcTan.setIntegral  (new CatDerive( Neg       .NEG,
							 new CatDerive( Logarithm .LOGARITHM,
											Cosinus   .Cosinus)));
	}

	/**Initializing private Constructor (Singleton):
	 * sets the Inverse: Tangens
	 * the Derivative:   1/SqRt(1+x*x)
	 * and the Integral: -Logarithm(Cosinus(x))
	 */
	private ArcTan(){ }

	/**This Function represents the ArcTan Function.	 */
	public Object Map (Object arg) { return ((MetricBody) arg).ArcTan(); }

	/**Returns ArcTan(x) for all x.	 */
	public double Map(double x) { return Math.atan(x); }	//

	/**Returns the Derivative of ArcTan(x) for all x: 1/SqRt(1+x*x).	 */
	public double getDerivative(double x) {
		return ICountAble.ONE / Math.sqrt(x*x + ICountAble.ONE); }

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble Derivative) {
		Derivative.Value = getDerivative(x);
		return Math.atan(x); }	//

	/**Tests the ArcTan() Function	 */
	public static void testIt() throws java.io.IOException {
		testArcTan();
		testArcTg ();
	}

	/**Tests the ArcTg() Function	 */
	private static void testArcTg() throws java.io.IOException {
		ByRefDouble X = new ByRefDouble(0);
		MetricBody x1 = new BodyDouble(); //(MetricBody) testInstance.newInstance();
		MetricBody x2 = (MetricBody) x1.newInstance();
		MetricBody x3 = (MetricBody) x1.newInstance();

		System.out.println ();
		System.out.println ("Test von ArcTg (ArcTan mit 2 Argumenten (x,y)) :");
		for (int Z1 = 0; ++Z1 <= 20;)
		 {	//ArcTg is 2Pi Periodic, because both Coordinates are given.
			X.Value = Math.PI*(Z1-10)/10; x1.copyAt(X);
			x3 = (MetricBody)x1.Cos_Sin(x2);
			System.out.println ("Soll : " + x1 + "  Ist : " + x2.ArcTg(x3));
		 };
		System.in.read(); System.in.read();
	};

	/**Tests the ArcTan() Function	 */
	public static void testArcTan() throws java.io.IOException {
		ByRefDouble X = new ByRefDouble(0);
		MetricBody  x = new  BodyDouble(); //(MetricBody) testInstance.newInstance();
		X.Value = +0.0; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = +0.4; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = +0.5; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = +0.6; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = +0.9; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = +1.0; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = +1.1; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = -0.4; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = -0.5; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = -0.6; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = -0.9; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = -1.0; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		X.Value = -1.1; x.copyAt(X); System.out.println("x = " + x + " ; ArcTan(x) = " + x.ArcTan() + " == " + Math.atan(X.Value));
		System.in.read(); System.in.read();
	}

}
