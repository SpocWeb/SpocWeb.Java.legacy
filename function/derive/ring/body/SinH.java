package function.derive.ring.body;

//import Stream.Copy.*;
import java.io.IOException;

import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.derive.AFloatDeriveAble;

/**This Class encapsulates the Sinus Hyperbolicus Function.  */
public class SinH
extends AFloatDeriveAble {

	/**Local Reference to the single Instance	 */
	final static public SinH SinH = new SinH();

	static { //Initializer
		SinH.setInverse   (ArSinH.ArSinH);
		SinH.setDerivative(CosH  .CosH  );
		SinH.setIntegral	 (CosH  .CosH  );
	}
	
	/**private Constructor for Singleton Implementation	 */
	private SinH() { }
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/**This Function represents the Sinus Hyperbolicus Function.  */
	public Object Map (Object arg) { return ((MetricBody) arg).SinH(); }
	
	/**SinH Function for double Arguments...	 */
	public double Map(double x) { return SIN_H(x); }
	
	/**SinH Function for float Arguments...	 */
	public float  Map(float  x) { return SIN_H(x); }

	/*	Returns SinH Function Derivative: CosH(x) for all x 	*/
	public double getDerivative(double x) { return CosH.COS_H(x); }	//

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble Derivative) {
		double SH = SIN_H(x); //CH^2 - SH^2 = 1
		Derivative.Value = Math.sqrt(ICountAble.ONE + SH*SH); //Optimization is faster here, since no Check necessary.
		return SH; }

	/**SinH Function for double Arguments...	 */
	final static public double SIN_H(double x) {
		boolean negativ;		//positive Values speed up convergence testing
		double Summe = (negativ = (x < 0)) ? -x : x;
		if (Summe > ICountAble.ONE) {
			double Exp = Math.exp(x); return IMeasurAble.HALF*(Exp-ICountAble.ONE/Exp); } //larger Value, so use dumb Subtraction.
		double Accuracy = ByRefDouble.MUL_ACCURACY(Summe);	//speeds up testing
		double Quadrat= x*x;	 //only for real Values!
		double Faktor = Summe;
		long DIV; int Z1 = 1;
		do {
			DIV = ++Z1;
			Summe += (Faktor *= Quadrat/(DIV *= ++Z1));
		} while (Accuracy < Faktor);	//only for Real Values!
		if (negativ) return -Summe;
		return Summe; }

	/**SinH Function for float Arguments...	 */
	final static public float SIN_H(float x) {
		boolean negativ;		//positive Values speed up convergence testing
		float Summe = (negativ = (x < 0)) ? -x : x;
		if (Summe > ICountAble.ONE) {
			float Exp = (float) Math.exp(x);
			return IMeasurAble.HALF*(Exp-ICountAble.ONE/Exp); } //larger Value, so use dumb Subtraction.
		float Accuracy = ByRefFloat.mulAccuracy(Summe);	//speeds up testing
		float Quadrat= x*x;	 //only for real Values!
		float Faktor = Summe;
		long DIV; int Z1 = 1;
		do {
			DIV = ++Z1;
			Summe += (Faktor *= Quadrat/(DIV *= ++Z1));
		} while (Accuracy < Faktor);	//only for Real Values!
		if (negativ) return -Summe;
		return Summe; }

	/**Tests the SinH() and CosH() Functions	 */
	private static void testSinHCosH() throws java.io.IOException {
		MetricBody test = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody zero	= (MetricBody) test.zero();	//= 0
		MetricBody Pi6	= (MetricBody) test.piSixth();
		MetricBody Pi4	= (MetricBody) test.piQuarter();
		MetricBody Pi3	= (MetricBody) test.piThird();
		MetricBody Pi2	= (MetricBody) test.piHalf();
//		MetricBody r1   = (MetricBody) test.newInstance();  
//		MetricBody r2   = null;

		System.out.println ();
		System.out.println ("Teste : CosH und SinH :");
		System.out.println ("CosH : Soll : " + 2.50917847865806 + "   Ist : " + Pi2.CosH ());
		System.out.println ("SinH : Soll : " + 2.30129890230729 + "   Ist : " + Pi2.SinH ());
		System.out.println ("CosH : Soll : " + 1.60028685770239 + "   Ist : " + Pi3.CosH ());
		System.out.println ("SinH : Soll : " + 1.24936705052398 + "   Ist : " + Pi3.SinH ());
		System.out.println ("CosH : Soll : " + 1.32460908925201 + "   Ist : " + Pi4.CosH ());
		System.out.println ("SinH : Soll : " + 0.86867096148601 + "   Ist : " + Pi4.SinH ());
		System.out.println ("CosH : Soll : " + 1.14023832107643 + "   Ist : " + Pi6.CosH ());
		System.out.println ("SinH : Soll : " + 0.54785347388804 + "   Ist : " + Pi6.SinH ());
		System.out.println ("CosH : Soll : " + 1 + "   Ist : " + zero.CosH());
		System.out.println ("SinH : Soll : " + 0 + "   Ist : " + zero.SinH());
		System.in.read(); System.in.read();

		BodyDouble X = new BodyDouble(1e-9);
		MetricBody x = new BodyDouble(0); //(MetricBody) ((MetricBody) testInstance.newInstance()).zeroAt();	//= 0
		for (int Z1 = 0; ++Z1 <= 10;) {
			x.copyAt(X);
			System.out.println ("CosH : Soll : " + (Math.exp (X.value)+Math.exp (-X.value))/2
								+ "  Ist : " + x.CosH ());
			System.out.println ("SinH : Soll : " + (Math.exp (X.value)-Math.exp (-X.value))/2
								+ "  Ist : " + x.SinH ());
			X.value /= 10;
		}
		System.in.read(); System.in.read();
	}

	/**Tests the CosH_SinH() Function	 */
	private static void testCosH_SinH() throws java.io.IOException {
		MetricBody test = new BodyDouble(); //(MetricBody) testInstance;	//defined in absCopyAble to test the abstract Methods
		MetricBody zero	= (MetricBody) test.zero();	//= 0
		MetricBody Pi6	= (MetricBody) ((MetricBody) test.newInstance()).piHalfAt().thirdAt();
		MetricBody Pi4	= (MetricBody) ((MetricBody) test.newInstance()).piQuarterAt();
		MetricBody Pi3	= (MetricBody) ((MetricBody) test.newInstance()).piAt().thirdAt();
		MetricBody Pi2	= (MetricBody) ((MetricBody) test.newInstance()).piHalfAt();
		MetricBody r1	= (MetricBody) test.newInstance();
		MetricBody r2	= (MetricBody) test.newInstance();

		System.out.println ();
		System.out.println ("Teste : CosH_SinH (gleichzeitige Berechnung von CosH und SinH) :");
		r1 = (MetricBody) Pi2.CosH_SinH (r2);
		System.out.println ("Soll : " + 2.50917847865806 + "   Ist : " + r1);
		System.out.println ("Soll : " + 2.30129890230729 + "   Ist : " + r2);
		r1 = (MetricBody) Pi3.CosH_SinH (r2);
		System.out.println ("Soll : " + 1.60028685770239 + "   Ist : " + r1);
		System.out.println ("Soll : " + 1.24936705052398 + "   Ist : " + r2);
		r1 = (MetricBody) Pi4.CosH_SinH (r2);
		System.out.println ("Soll : " + 1.32460908925201 + "   Ist : " + r1);
		System.out.println ("Soll : " + 0.86867096148601 + "   Ist : " + r2);
		r1 = (MetricBody) Pi6.CosH_SinH (r2);
		System.out.println ("Soll : " + 1.14023832107643 + "   Ist : " + r1);
		System.out.println ("Soll : " + 0.54785347388804 + "   Ist : " + r2);
		r1 = (MetricBody) zero.CosH_SinH(r2);
		System.out.println ("Soll : " + 1 + "   Ist : " + r1);
		System.out.println ("Soll : " + 0 + "   Ist : " + r2);
		System.in.read(); System.in.read();

		System.out.println ();
		System.out.println ("Teste : CosH_SinH nahe bei 0 :");
		ByRefDouble X = new ByRefDouble(1e-9);
		MetricBody x = (MetricBody) ((MetricBody) test.newInstance()).zeroAt();	//= 0
		for (int Z1 = 0; ++Z1 <= 10;) {
			x.copyAt(X);
			r1 = (MetricBody) x.CosH_SinH (r2);
			System.out.println ("SinH : Soll : " + ((Math.exp(X.Value)-Math.exp(-X.Value))/2) + "  Ist : " + r2);
			System.out.println ("CosH : Soll : " + ((Math.exp(X.Value)+Math.exp(-X.Value))/2) + "  Ist : " + r1);
			X.Value /= 10;
		}
		System.in.read(); System.in.read();
	}

	/**Tests the LnP1() Function	 */
	private static void testIt(final String[] args) throws IOException {
		testSinHCosH();
		testCosH_SinH();
		//test();
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
