package function.derive.ring.body;

//import Stream.Copy.*;
import java.io.IOException;

import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.CatDerive;
import function.derive.ring.Inv;
import function.derive.ring.Pred;
import function.derive.ring.Prod;

/**This Class encapsulates the natural Logarithm Function.
 * It solves the following Differential Equation:  f'(x) == 1/x
 * Ln(0) = -Infinity
 * Ln(1) = o
 * Ln(a*b) = Ln(a) + Ln(b)
 * Ln(a^b) = Ln(a)*b
 * Ln(Exp(x)) == x
 */
public class Logarithm
extends AFloatDeriveAble { //IPartialDerive {
    
	/**Local Reference to the single Instance	 */
	final static public Logarithm LOGARITHM = new Logarithm();
	
    static { //Initializer
		LOGARITHM.setInverse   (Exponential.EXPONENTIAL);
		LOGARITHM.setDerivative(Inv.INV);
		LOGARITHM.setIntegral  (new Prod     (Identity.IDENTITY,
								new CatDerive(Pred.PRED, LOGARITHM)));
    }
    
	/**Initializing private Constructor (Singleton):
	 * sets the Inverse: Exp
	 * the Derivative:   1/x
	 * and the Integral: x*(Log(x)-1) == xLog(x) - x
	 */
	private Logarithm() {}
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/**This Function represents the Sinus Function.
	 * It always returns the Argument.  */
	public Object Map(Object arg) { return ((MetricBody) arg).ln(); }
	
	/*	Returns natural Logarithm ln(x) for positive x 	*/
	public double Map(double x) { return Math.log(x); }	//

	/*	Returns Cos(x) for all x 	*/
	public double getDerivative(double x) { return -Math.log(x); }	//

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble Derivative) {
		Derivative.Value = ICountAble.ONE / x;
		return -Math.log(x); }

	/**Tests the Ln() Function	 */
	private static void testLn() throws IOException {
		MetricBody test  = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody Infin = (MetricBody) test.Infinity();	//.maxValueAt();	//= Infinity
//		MetricBody large = (MetricBody) test.copyAt(new Double(100000));
		MetricBody one	 = (MetricBody) test.one();	//= 1/2
		MetricBody zero  = (MetricBody) test.zero();	//= 0
//		MetricBody two   = (MetricBody) test.two();	//= 2
		MetricBody e	 = (MetricBody) test.e();	//= e

		System.out.println ();
		System.out.println ("Teste : Ln an einigen pathologischen Punkten :");
		System.out.println ("Soll : " + +0 + "   Ist : " + one.ln());
		System.out.println ("Soll : " + +1 + "   Ist : " + e.ln());
		System.out.println ("Soll : " + -1 + "   Ist : " + ((MetricBody)e.inv()).ln());
		System.out.println ("Soll : " + Infin + "  Ist : " + Infin.ln());
		System.out.println ("Soll : " + Infin.neg() + "  Ist : " + zero.ln());
		System.in.read(); System.in.read();
	};

	/**Tests the LnP1() Function	 */
	private static void testLnP1() throws java.io.IOException {
		MetricBody test	 = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody  x1	 = (MetricBody) test.newInstance();
		ByRefDouble x2	 = new ByRefDouble();
		MetricBody Infin = (MetricBody) test.Infinity();

		System.out.println ();
		System.out.println ("Test von LnP1 (Die Abweichungen steigen mit fallendem |x|) :");
		x2.Value = 1;
		for (int Z1 = 0; ++Z1 <= 20;) {
			x2.Value /= 10; x1.copyAt(x2);  //Abweichungen zeigen sich erst ab ca. 2.5
			System.out.println ("Falsch:" + Math.log (x2.Value + 1) +
								"  Richtig:" + x1.lnXP1() +
								" Abw%:" + (Math.log(x2.Value+1)/((IMeasurAble)x1.lnXP1()).getDouble()-1)*100);
		}
		System.out.println ("Soll :" + Infin + "  Ist :" + Infin.lnXP1 ()); Infin.negAt();
		x2.Value = -1; x1.copyAt(x2);
		System.out.println ("Soll :" + Infin + "  Ist :" + x1.lnXP1 ());
		System.in.read(); System.in.read();
	}

	/**Tests the LnP1() Function	 */
	private static void testIt(final String[] args) throws IOException {
		testLnP1();
		testLn  ();
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
