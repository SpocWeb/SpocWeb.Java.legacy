package function.derive.ring.body;	//Function;

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
import function.derive.ring.Diff;
import function.derive.ring.Neg;
import function.derive.ring.Pred;
import function.derive.ring.Succ;

/**This Class encapsulates the Cosinus Function.
 * Like 'Sinus' it solves this linear Differential Equation: f'' = -f
 * It has the following Properties:
 * Cosinus'     = -Sinus
 * Int[Cosinus] =  Sinus
 * Inv(Cosinus) =  ArcCos
 *
 * cos(-x) == cos(x) 		(Symmetric)
 * cos( x) == cos(x+2*Pi)	(Periodic)
 * -cos(x) == cos(x+  Pi)
 * |cos(x)| <= +1
 * cos(n*Pi) == (-1)^n
 * cos^2 + sin^2 == 1
 * 2*cos^2(x) == 1+cos(2*x)
 * 4*cos^3(x) == 3*cos(x)+cos(3*x)
 * 8*cos^4(x) == cos(4*x)+4*cos(2*x)+3
 *
 * Addition Theorem
 * cos(x+/-y) == cos(x)*cos(y) -/+ sin(x)*sin(y)
 * cos(x+y+z) == cos(x)*cos(y)*cos(z) - sin(x)*sin(y)*cos(z) -
 * 				 sin(x)*cos(y)*sin(z) - cos(x)*sin(y)*sin(z)
 * cos(2*x) == cos^2(x)-sin^2(x)
 * cos(3*x) ==(4*cos^2(x) - 3)*cos(x)
 * cos(4*x) == 8*cos^4(x) - 8*cos^2(x) + 1
 * cos(x/2) == +/-SqRt((1+cos(x))/2)
 *
 * cos(2*x)+cos(2*y) == 2*cos(x+y)*cos(x-y)
 * cos(2*x)-cos(2*y) ==-2*sin(x+y)*sin(x-y)
 * 2*cos(x)*cos(y) == cos(x-y)+cos(x+y)
 *
 * Power Series (converges for all x):
 * cos(x) = 1 - x^2/2! + x^4/4! -... + (-1)^n*x^(2*n)/(2n)!
 *
 */
final public class Cosinus
extends AFloatDeriveAble {

	/**Local Reference to the single Instance of Cosinus	 */
	final static public Cosinus Cosinus = new Cosinus();

	/**Local Reference to the single Instance of CosinusMinus1	 */
	final static public CatDerive CosinusMinus1 = new CatDerive(Pred.PRED , Cosinus);

    static { //Initializer
		CosinusMinus1.setInverse   (new CatDerive(ArcCos.ARC_COS, Succ   .SUCC    ));
		CosinusMinus1.setDerivative(new CatDerive(Neg   .NEG   , Sinus   .SINUS   ));
		CosinusMinus1.setIntegral  (new Diff	 (Sinus .SINUS , Identity.IDENTITY));
		Cosinus.setInverse   (ArcCos.ARC_COS);
		Cosinus.setDerivative(new CatDerive(Neg.NEG, Sinus.SINUS));
		Cosinus.setIntegral  (Sinus.SINUS);
	}

	/**private Constructor for Singleton Implementation	 */
	private Cosinus(){ }

	/**This Function represents the Cosinus Function.	 */
	public Object Map (Object arg) { return ((MetricBody) arg).cos(); }

	/*	Returns Cos(x) for all x 	*/
	public double Map(double x) { return Math.cos(x); }	//

	/*	Returns Cos(x) for all x 	*/
	public double getDerivative(final double x) { return -Math.sin(x); }	//

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (final double x, final ByRefDouble Derivative) {
		Derivative.Value = -Math.sin(x); //Optimization possible, but equally slow.
		return Math.cos(x); }

	/**Returns Cosinus(x)	 */
	public static IMetricIRing cos(final IMetricIRing x) {
		IMetricIRing ret = cosM1(((IMetricIRing) x.copy())); ret.inc(); return ret; }

	/**Returns Cos(x)-1
	 * Gives better accuracy, but modifies x.	 */
	public static IMetricIRing cosM1(IMetricIRing x) { //return ((MetricIRing)copy()).cosM1At(); }
		//cos(x) = cos (x+2Pi) Range (-pi,+pi)
		IMetricIRing ret; x = (IMetricIRing) x.Rem(IMeasurAble.TwoPi).AbsVAt();	//cos (x) = cos(-x) Range: [0, Pi]
		if		(x.isMoreThan  (IMeasurAble.ThreePiQuarter)) {	//cos(x) = -cos(x-Pi) = cos(Pi-x)
				 x.subAt(IMeasurAble.pi); ret = CosM1Pi_4(x); ret.addAt(ICountAble.Two); ret.negAt(); return ret; }
		else if (x.isMoreThan  (IMeasurAble.PiQuarter)) {	//cos(x) = sin(x+Pi/2) = -sin(x-Pi/2)
				 x.addAt (IMeasurAble.PiHalf); ret = Sinus.SinPi_4(x); ret.dec(); return ret; }
		return CosM1Pi_4(x); }

	/**Returns Cos(x)-1
	 * Gives better accuracy, but modifies x.
	 * Requires the Argument to be in the Range [-Pi/4, +Pi/4] at max [-1,+1]
	 * For complex or Matrix Arguments the AbsV() Function has to be used in the Check
	 * Additionally you cannot exploit the Periodicity.  */
	public static IMetricIRing CosM1Pi_4(IMetricIRing x) { //return ((MetricIRing)copy()).cosM1At(); }
		boolean add = false;
	    IMetricIRing Quadrat  = x; Quadrat.sqrAt();
		IMetricIRing Accuracy = (IMetricIRing) Quadrat.mulAccuracy();	//speeds up testing
	    IMetricIRing Faktor   = (IMetricIRing) Quadrat.copy(); Faktor.halfAt();
	    IMetricIRing Summe    = (IMetricIRing) Faktor.neg();
		int Z1 = 3;	ByRefLong Divisor = new ByRefLong();
		do {
			Divisor.Value = (Z1++)*(Z1++);	//potentially weakly defined!!!
			Faktor.divAt(Divisor).mulAt(Quadrat);
			if (add = !add)	Summe.addAt (Faktor);
			else			Summe.subAt(Faktor);
	    } while (Faktor.isMoreThan(Accuracy));
		return Summe; }

	/**Returns Cosinus(x)-1 for 'double' Arguments	 */
	public static double cosM1(double x) { //cos(x) = cos (x+2Pi) Range (-pi,+pi)
		x = Math.abs(Math.IEEEremainder(x, IMeasurAble.TWO_PI));	//cos (x) = cos(-x) Range: [0, Pi]
		if		(x >  IMeasurAble.THREE_PI_QUARTER) {	//cos(x) = -cos(x-Pi) = cos(Pi-x)
				 return -CosM1Pi_4(x - IMeasurAble.PI) - ICountAble.TWO; }
		else if (x >  IMeasurAble.PI_QUARTER) {	//cos(x) = sin(x+Pi/2) = -sin(x-Pi/2)
				 return Math .sin    (x + IMeasurAble.PI_HALF)-ICountAble.ONE; }
//				 return Sinus.SinPi_4(x + IMeasurAble.PI_HALF)-ICountAble.ONE; }
		return CosM1Pi_4(x); }

	/**Returns Cos(x)-1
	 * Gives better accuracy!
	 * Requires the Argument to be in the Range [-Pi/4, +Pi/4] at max [-1,+1]
	 * For complex or Matrix Arguments the AbsV() Function has to be used in the Check
	 * Additionally you cannot exploit the Periodicity.  */
	public static double CosM1Pi_4(double x) { //
		boolean add = false; x*=x;
		double Accuracy = ByRefDouble.MUL_ACCURACY(x);	//speeds up testing
	    double Faktor   = x*IMeasurAble.HALF;
	    double Summe    = -Faktor;
		int Z1 = 3;
		do {
			Faktor *= x/((Z1++)*(Z1++));
			if (add = !add)	Summe += Faktor;
			else			Summe -= Faktor;
	    } while (Faktor > Accuracy);
		return Summe; }

	/**Tests the CosM1() Function	 */
	private static void testCosM1() throws java.io.IOException {
		MetricBody test	 = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody  x1	 = (MetricBody) test.newInstance();
		ByRefDouble x2	 = new ByRefDouble();
//		MetricBody Infin = (MetricBody) test.Infinity();

		System.out.println ();
		System.out.println ("Test von CosM1 (Die Abweichungen steigen mit fallendem |x|) :");
		x2.Value = 1;
		for (int Z1 = 0; ++Z1 <= 20;) {
			x2.Value /= 10; x1.copyAt(x2);  //Abweichungen zeigen sich erst ab ca. 2.5
			System.out.println ("Falsch:" + (Math.cos (x2.Value) - 1) +
								"  Richtig:" + x1.cosM1() +
								" Abw%:" + ((Math.cos (x2.Value) - 1)/((IMeasurAble)x1.cosM1()).getDouble()-1)*100);
		}
		System.in.read(); System.in.read();
	}

	/**Tests the Cos() Function	 */
	private static void testCos() throws java.io.IOException {
		MetricBody test = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody zero = (MetricBody) test.zero();	//= 0
		MetricBody Pi6  = (MetricBody) test.piSixth();
		MetricBody Pi4  = (MetricBody) test.piQuarter();
		MetricBody Pi3  = (MetricBody) test.piThird();
		MetricBody Pi2  = (MetricBody) test.piHalf();

		System.out.println ();
		System.out.println ("Teste : Cos");
		System.out.println ("Soll : " + 0 + "   Ist : " + Pi2.cos ());
		System.out.println ("Soll : " + 0.5 + "   Ist : " + Pi3.cos());
		System.out.println ("Soll : " + 1.0/Math.sqrt(2)   + "   Ist : " + Pi4.cos());
		System.out.println ("Soll : " +     Math.sqrt(3)/2 + "   Ist : " + Pi6.cos());
		System.out.println ("Soll : " + 1 + "   Ist : " + zero.cos());
		System.in.read(); System.in.read();
	}

	/**Tests the Cos_Sin() Function	 */
	private static void testCos_Sin() throws java.io.IOException {
		MetricBody test = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody zero = (MetricBody) test.zero();	//= 0
		MetricBody Pi6  = (MetricBody) test.piSixth();
		MetricBody Pi4  = (MetricBody) test.piQuarter();
		MetricBody Pi3  = (MetricBody) test.piThird();
		MetricBody Pi2  = (MetricBody) test.piHalf();
		MetricBody r1	= (MetricBody) test.newInstance();
		MetricBody r2	= (MetricBody) test.newInstance();

		System.out.println ();
		System.out.println ("Teste : Cos_Sin (gleichzeitige Berechnung von Cosinus und Sinus) :");
		r1 = (MetricBody) Pi2.Cos_Sin (r2);
		System.out.println ("Soll : " + 1 + "   Ist : " + r2);
		System.out.println ("Soll : " + 0 + "   Ist : " + r1);
		r1 = (MetricBody) Pi3.Cos_Sin (r2);
		System.out.println ("Soll : " + Math.sqrt(3)/2 + "   Ist : " + r2);
		System.out.println ("Soll : " + 0.5 + "   Ist : " + r1);
		r1 = (MetricBody) Pi4.Cos_Sin (r2);
		System.out.println ("Soll : " + 1/Math.sqrt(2) + "   Ist : " + r2);
		System.out.println ("Soll : " + 1/Math.sqrt(2) + "   Ist : " + r1);
		r1 = (MetricBody) Pi6.Cos_Sin (r2);
		System.out.println ("Soll : " + 0.5 + "   Ist : " + r2);
		System.out.println ("Soll : " + Math.sqrt(3)/2 + "   Ist : " + r1);
		r1 = (MetricBody) zero.Cos_Sin (r2);
		System.out.println ("Soll : " + 0 + "   Ist : " + r2);
		System.out.println ("Soll : " + 1 + "   Ist : " + r1);
		r1 = (MetricBody) ((MetricBody) Pi4.neg().trpl()).Cos_Sin (r2);
		System.out.println ("Soll : " + -1/Math.sqrt(2) + "   Ist : " + r2);
		System.out.println ("Soll : " + -1/Math.sqrt(2) + "   Ist : " + r1);
		System.in.read(); System.in.read();
	}

	/**Tests the CosM1() Function	 */
	private static void testIt(final String[] args) throws IOException {
		System.out.println("Testing " + Cosinus.class.getName());
		testCosM1();
		testCos  ();
		testCos_Sin();
	}

	/**
	  * The main entry point for the application.
	  *
	  * @param args Array of parameters passed to the application
	  * via the command line.	 */
	public static void main(final String[] args)
	throws IOException, java.sql.SQLException {
		testIt(args); 
	}
	
}

