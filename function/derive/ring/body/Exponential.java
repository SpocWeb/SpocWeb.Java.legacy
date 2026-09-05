package function.derive.ring.body;	//Function;

//import Stream.Copy.*;
import java.io.IOException;

import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;
import function.derive.AFloatDeriveAble;

/**This Class encapsulates the Exponential Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:44:35Z
 * digest: 0361bccde7cab1bf6df4283c1c570af7b834da6503d9efb0a814180e1866133b
 * stale: false
 * tags: [code/exponential_function, code/derivable_function_contract]
 * concepts: [Exponential Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Exponential
extends AFloatDeriveAble { //IPartialDerive {

	/**Local Reference to the single Instance	 */
	final static public Exponential EXPONENTIAL = new Exponential();

    static { //Initializer
		EXPONENTIAL.setInverse(Logarithm.LOGARITHM);
		EXPONENTIAL.setDerivative(EXPONENTIAL);
		EXPONENTIAL.setIntegral  (EXPONENTIAL);
    }

	/**private Constructor for Singleton Implementation	 */
	private Exponential(){ }
	
    /**Reports that Exponential preserves strict ascending order of its argument.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/**This Function represents the Exponential Function.	 */
	public Object Map (Object arg) { return ((MetricBody) arg).exp(); }

	/**Returns exp(x) for all x.	 */
	public double Map(double x) { return Math.exp(x); }	//

	/**Returns the Exponential Function's Derivative: exp(x) for all x.	 */
	public double getDerivative(double x) { return Math.exp(x); }	//

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble Derivative) {
		return Derivative.Value = Math.exp(x); }

	/**Tests the ExpM1() Function	 */
	private static void testIt(final String[] args) throws IOException {
		testExpM1();
		testExp  ();
	}

	/**Tests the Exp() Function	 */
	private static void testExp() throws IOException {
		MetricBody test  = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody Infin = (MetricBody) test.Infinity();	//.maxValueAt();	//= Infinity
		MetricBody large = (MetricBody) test.copyAt(new Double(100000));
		MetricBody one	 = (MetricBody) test.one();	//= 1/2
//		MetricBody zero  = (MetricBody) test.zero();	//= 0
//		MetricBody two   = (MetricBody) test.two();	//= 2

		System.out.println ();
		System.out.println ("Teste : Exp an einigen pathologischen Punkten :");
		System.out.println ("Soll : " + 0 + "   Ist : " + ((MetricBody)Infin.neg()).exp());
		System.out.println ("Soll : " + Infin + "  Ist : " + large.exp());
		System.out.println ("Soll : " + Math.E + "   Ist : " + one.exp());
		System.out.println ("Soll : " + 1/Math.E + "   Ist : " + ((MetricBody)one.neg()).exp());
		System.out.println ("Soll : " + Infin + "  Ist : " + Infin.exp());
		System.out.println ("Soll : " + 0     + "  Ist : " + ((MetricBody)Infin.neg()).exp());
		System.in.read(); System.in.read();
	};

	/**Tests the ExpM1() Function	 */
	private static void testExpM1() throws IOException {
		MetricBody test	 = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody  x1	 = (MetricBody) test.newInstance();
		ByRefDouble x2	 = new ByRefDouble();
		MetricBody Infin = (MetricBody) test.Infinity();

		System.out.println ();
		System.out.println ("Test von ExpM1 (Die Abweichungen steigen mit fallendem |x|) :");
		x2.Value = 1;
		for (int Z1 = 0; ++Z1 <= 20;) {
			x2.Value /= 10; x1.copyAt(x2);  //Abweichungen zeigen sich erst ab ca. 2.5
			System.out.println ("Falsch: " + (Math.exp(x2.Value)-1) +
								"  Richtig: " + x1.expM1() +
								" Abw%:" + ((Math.exp (x2.Value)-1)/((IMeasurAble)x1.expM1()).getDouble() - 1)*100);
		}	//the projective Nature of Complex numbers does not allow for a correct Calculation of this.
		System.out.println ("Soll : " + Infin + "  Ist : " + Infin.expM1 ()); Infin.negAt();
		System.out.println ("Soll : " + -1    + "  Ist : " + Infin.expM1 ());
		System.in.read(); System.in.read();
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

	/**Returns the Exponential -1. This is more accurate. 	 */
	final static public double ExpM1(double x) {
		boolean negativ;		//positive Values speed up convergence testing
		double	Summe	= (negativ = (x < 0)) ? -x : x;
		if (Summe > 1.0) return Math.exp(Summe)-1.0;
		double Accuracy	= Summe * ByRefDouble.DoubleAccuracy;	//speeds up testing
		double Quadrat	= Summe;
		double Faktor	= Summe;
		long DIV = 1;
		while ((++DIV < ByRefInt.MAX_ITER) && (Accuracy < Quadrat))
			Summe += (Faktor *= Quadrat / DIV);
		if (negativ) Summe /= (Summe + 1.0);	//very bad accuracy!
		else		 Summe += Faktor;		//zusaetzliche Genauigkeit
		return Summe; }
	
}
