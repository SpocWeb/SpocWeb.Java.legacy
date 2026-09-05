package function.derive.ring.body;

import java.io.IOException;

import math.vector.VectorString;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.IDeriveAble;
import function.derive.Identity;
import function.derive.ring.Algebra;
import function.derive.ring.CatDerive;
import function.derive.ring.Diff;
import function.derive.ring.HalfAt;
import function.derive.ring.Inv;
import function.derive.ring.MulAt;
import function.derive.ring.Neg;
import function.derive.ring.Prod;
import function.derive.ring.Resid;
import function.derive.ring.Sign;
import function.derive.ring.Square;
import function.derive.ring.Succ;
import function.derive.ring.Sum;
import function.derive.ring.ThirdAt;

/**Tests the Methods of the Package BodyFuncs
 * This class can take a variable number of parameters on the command
 * line. Program execution begins with the main() method. The class
 * constructor is not invoked unless an object of type 'Class1'
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:42:57Z
 * digest: c64151498bf126230843f8ba510e61e7f366cef6790bf5f70d5114bc27b2acf6
 * stale: false
 * tags: [code/entry_point_code/console_output_code/test]
 * concepts: [Test Harness]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * created in the main() method. */
public class TestBodyFuncs {

	/**Constant containing the Gauss Function e^-(x^2)
	 * It can be normed by dividing it by SqRt(Pi).
	 * Also defines the Integral.	 */
	final static public CatDerive
		Gauss =	new CatDerive(	Exponential.EXPONENTIAL,
				new CatDerive(	Neg.NEG, Square	.SQUARE));	//Integral of the Gauss Function

	/**Returns a normed Gauss Function e^-(x^2)
	 * Smooth, sharp, but expensive Representation of the normed Delta
	 * as a (Gaussian) Bell Curve.
	 * If H is null (not given), it is assumed to 1.
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Delta Function Test Helper]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * The Width is proportional to 1/H, the Height to H.	 */
	final static public CatDerive Delta2(Object H) {
		return 	new CatDerive(			new MulAt(new Algebra(((IGroupM) H).div(IMeasurAble.SqRtPi))),
				new CatDerive(	Gauss, 	new MulAt(new Algebra(H)))); }	//Integral of the Gauss Function

	/**Constant containing the normed Gauss Function e^-(x*x/2)/SqRt(2*Pi)	 */
	final static public CatDerive
		GaussNorm =	Delta2( new BodyDouble( ICountAble.ONE/IMeasurAble.SQRT2PI));

	/**Constant containing the Function (x*x-1)^3/2	 */
	final static public Prod
		xx_1pow3_2= new Prod(	Algebra.SqRtxx_1,
								Algebra.    xx_1); //

	/**Constant containing the Function x*SqRt(x*x-1)	 */
	final static public Prod
		xSqRtxx_1 = new Prod( Identity.IDENTITY, Algebra.SqRtxx_1);

	/**The Integral has to be set afterwards,
	 * because of invalid forward References to SqRtxx_1 itself.	 */
	{
		xSqRtxx_1 .setIntegral(new CatDerive(ThirdAt.ThirdAt, xx_1pow3_2));
		Algebra.SqRtxx_1.setIntegral(
			new CatDerive(HalfAt.HalfAt,
			new Diff(xSqRtxx_1, ArCosH.ArCosH))); }

	/**Constant containing the Function (1-x*x)^3/2	 */
	final static public Prod
		One_xxPow3_2= new Prod(	Algebra.SqRt1_xx,  Algebra.One_xx); //

	/**Constant containing the Function x*SqRt(x*x-1)	 */
	final static public Prod
		xSqRt1_xx = new Prod( Identity.IDENTITY, Algebra.SqRt1_xx);

	/**The Integral has to be set afterwards,
	 * because of invalid forward References to SqRt1_xx itself.	 */
	{
		xSqRt1_xx.setIntegral (new CatDerive(Neg.NEG, new CatDerive(ThirdAt.ThirdAt, One_xxPow3_2)));
		Algebra.SqRt1_xx.setIntegral(
			new CatDerive(HalfAt.HalfAt,
			new Sum(xSqRt1_xx, ArcSin.ARC_SIN)));}

	/**Constant containing the Function (1-x*x)^3/2	 */
	final static public Prod
		xxp1Pow3_2= new Prod(	Algebra.SqRtxxp1,  Algebra.xxp1); //

	/**Constant containing the Function x*SqRt(x*x-1)	 */
	final static public Prod
		xSqRtxxp1 = new Prod( Identity.IDENTITY, Algebra.SqRtxxp1);

	/**The Integral has to be set afterwards,
	 * because of invalid forward References to SqRt1_xx itself.	 */
	{
		xSqRtxxp1.setIntegral (new CatDerive(ThirdAt.ThirdAt, xxp1Pow3_2));
		Algebra.SqRtxxp1.setIntegral(
			new CatDerive(HalfAt.HalfAt,
			new Sum ( xSqRtxxp1, ArSinH.ArSinH)));}

	{	//Static Initialization of the Functions from Algebra...
		Algebra.InvSqRtxx_1.setIntegral(ArcCos.ARC_COS);
		Algebra.InvSqRt1_xx.setIntegral(ArcSin.ARC_SIN);
		Algebra.InvSqRtxxp1.setIntegral(ArSinH.ArSinH);
	}

	/**Integral of the Sigmoid Function: ln(1+exp(x))
	 * Should be defined as a static Member of a Body Class	 */
	final static public CatDerive
		IntSigmoid =new CatDerive(	Logarithm  .LOGARITHM,
					new CatDerive(	Succ	   .SUCC,
									Exponential.EXPONENTIAL));

	/**Constant containing the Sigmoid Function 1/(1+e^-x)
	 * It is normed and thus be used as a Simulation of the Delta3 Function.
	 * Also defines the Integral;
	 * the Derivative and Inverse can be determined analytically.
	 * It is often used as Switching Function in neural Networks.
	 * The Analytical Representation is invertAble,
	 * but takes longer to calculate than the numeric Representation,
	 * which uses a Variable to replace a Negation and Inversion by a Division:
	 * 1/(1+exp(-x)) = u/(1+u) with u = exp(x)
	 *
	 * Peculiarity: Sigmoid' = Sigmoid - Sigmoid^2, makes it easy to calculate.
	 * See also: Gauss for Delta2,
	 *			 Lorentz for Delta3	 */
	final static public CatDerive
		Sigmoid =	new CatDerive(	Inv .INV ,
					new CatDerive(	Succ.SUCC,
					new CatDerive(	Exponential.EXPONENTIAL,
									Neg.NEG)));

	{
		Sigmoid.setIntegral (IntSigmoid);
	}

	/**Returns the Derivative of a Sigmoid Function
	 * as a (slightly asymmetric) Simulation of the Delta Function
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Delta Function Test Helper]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static IDeriveAble Delta4(Object H) {
		return  new CatDerive(							new MulAt(H),
				new CatDerive(Sigmoid.getDerivative (), new MulAt(H))); }


	/**Returns the general Exponential Integral
	 * represented by the incomplete Gamma Function:
	 * ExpInt (n,x) = Int(1, Infinity, Exp(-x*t)/t^n)	for x > 0
	 *				= x^-a*Gamma(a)*(1-GammaP(a, x)) with a = 1-n
	 * Thus it is related to the incomplete Gamma Function,
	 * but you cannot really exploit the Cancellation of the Factor x^a
	 * Three Special Cases have to be handled:
	 * ExpInt (0,x) = Exp(-x)/x
	 * ExpInt (1,x) =-EI (-x)	//this is not covered by EI
	 * ExpInt (n,0) = 1/(n-1)
	 *
	 * (1-GammaP(a, x))*Gamma(a)/x^a with a = 1-n < 0	for n < 1
	 * <!-- docstate
	 * tags: [code/testing, code/numerical_integration]
	 * concepts: [Exponential Integral Test Helper]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static IDeriveAble ExpInt(MetricBody n) {
		MetricBody a = (MetricBody) n.pred();
		IDeriveAble equivalent;
		equivalent =new GammaP(a.neg());
		equivalent =new Prod(
					new CatDerive(new MulAt(((GammaP) equivalent).GammaLn().expAt()),
					new CatDerive(Resid.RESID,
									 equivalent)),
									new Power(a));
		return equivalent; }

	/**Logarithmic Integral, defined as the Composition of EI and Logarithm: EI(Ln(x)).	 */
	final static public IDeriveAble LI = new CatDerive(EI.EI, Logarithm.LOGARITHM);

	/**the Integral over the Gaussian Error Function:
	 * Int(-Infinity, x, exp(-x^2/2)/SQRT2PI)
	 * A way to get GaussIntegral with arbitrary Precision is defined by the
	 * incomplete Gamma Function:	(1+Sign (x)*GammP (Sqr (x)/2,Halb))/2
	 */
	final static public IDeriveAble
		GaussIntegral =	new CatDerive(HalfAt.HalfAt,
						new CatDerive(Succ.SUCC,
						new Prod(
						new CatDerive(new GammaP(new BodyDouble(IMeasurAble.HALF)),
						new CatDerive(HalfAt.HalfAt,
										 Square.SQUARE)),
										 Sign.Sign))	//Use Sign as 2nd Factor always, because it delivers only Constants!
										 );	//Sign as Factor can be Optimized

	{ GaussIntegral.setDerivative (GaussNorm); }

	/**Tests the Error Gamma Function
	 *
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Gaussian Integral Self-Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void testGaussIntegral() throws IOException {
		System.out.println("Testing Gauss Function():");
		System.out.println(	VectorString.FORMAT("x", 8) +
							VectorString.FORMAT("Gauss   (x)", 12) +
							VectorString.FORMAT("GaussInt(x)", 12) +
							VectorString.FORMAT("Gauss   (x)", 12) +
							VectorString.FORMAT("GaussInt(x)", 12));
		BodyDouble x = new BodyDouble();
		for(int i = function.derive.ring.body.Gauss.ValuesGauss.length; --i >= 0;) {
			float[] xyPair = function.derive.ring.body.Gauss.ValuesGauss[i];
			System.out.println(	ByRefDouble.FORMAT(x.value = xyPair[0]  , - 8, 2) +
								ByRefDouble.FORMAT(          xyPair[1]  , -12, 7) +
								ByRefDouble.FORMAT(          xyPair[2]  , -12, 7) +
								ByRefDouble.FORMAT(((BodyDouble) GaussNorm    .Map(x)).value, -12, 7) +
								ByRefDouble.FORMAT(((BodyDouble) GaussIntegral.Map(x)).value, -12, 7)
								);
		}
		System.in.read(); System.in.read();	}

	/**Values of the Exponential Integral: {n, x, ExpInt(n,x)}	 */
	final static public float[][]
		ValuesExpInt = {
		{ 0, 1.0f, 0.3678794f},
		{ 2, 0.0f, 1.0000000f},
		{ 3, 0.0f, 0.5000000f},
		{ 4, 0.0f, 0.3333333f},
		{ 2, 0.5f, 0.3266439f},
		{ 3, 0.5f, 0.2216044f},
		{ 4, 0.5f, 0.1652428f},
		{10, 0.5f, 0.0634583f},
		{20, 0.5f, 0.0310612f},
		{ 2, 5.0f, 0.9964690E-03f},
		{20, 5.0f, 0.2782746E-03f},
		{ 2,50.0f, 0.3711783E-23f},
		{20,50.0f, 0.2766423E-23f},
		{ 0, 1.0f, 0.3678794f}
	};

	/**Tests the Error Gamma Function
	 *
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Exponential Integral Self-Test]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void testExpInt() throws IOException {
		System.out.println("Testing ExpInt Function():");
		System.out.println(	VectorString.FORMAT("n", 8) +
							VectorString.FORMAT("x", 8) +
							VectorString.FORMAT("ExpInt(x)", 22) +
							VectorString.FORMAT("ExpInt(x)", 22));
		BodyDouble x = new BodyDouble();
		BodyDouble n = new BodyDouble();
		int i = ValuesExpInt.length;
		while(--i >= 0)	{
			float[] xyPair = ValuesExpInt[i];
			System.out.println(	ByRefDouble.FORMAT(n.value = xyPair[0]  , - 8, 2) +
								ByRefDouble.FORMAT(x.value = xyPair[1]  , -22, 7) +
								ByRefDouble.FORMAT(          xyPair[2]  , -12, 7) +
								ByRefDouble.FORMAT(((BodyDouble) ExpInt(n).Map(x)).value, -22, 7)
								);
		}
		System.in.read(); System.in.read();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		GammaLn.testIt();
		IDeriveAble f;
		f = Sinus.SINUS;
		System.out.println("Function  :" + f);
		System.out.println("Derivative:" + f.getDerivative());
		System.out.println("Integral  :" + f.getIntegral  ());
		System.out.println("Inverse   :" + f.getInverse   ());
		BetaI.testIt();
		ElliptInt.testIt();
		testExpInt();	//TODO: not properly defined yet!
		GammaP.testIt();
		EI.testIt();
		testGaussIntegral();
	}
}
