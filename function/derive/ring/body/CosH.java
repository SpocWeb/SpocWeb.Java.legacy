package function.derive.ring.body;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.byref.ByRefLong;
import function.derive.AFloatDeriveAble;
import function.derive.Identity;
import function.derive.ring.CatDerive;
import function.derive.ring.Diff;
import function.derive.ring.Succ;

/**This Class encapsulates the Cosinus Hyperbolicus Function.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:42:34Z
 * digest: 1695ba63d9af1e849aaad4d4403c0147a654545f0ebb78e132faa9eea1ff7872
 * stale: false
 * tags: [code/hyperbolic_function, code/derivable_function_contract]
 * concepts: [Hyperbolic Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class CosH
extends AFloatDeriveAble {

	/**Local Reference to the single Instance of CosHMinus1 	 */
	final static public CosH CosHMinus1 = new CosH();

	/**Local Reference to the single Instance of CosH	 */
	final static public CatDerive CosH = new CatDerive(Succ.SUCC, CosHMinus1);

	/**Returns the asymmetric Cosh : a==0 => CosH
	 * Exp (x/(a+1))+Exp (x/(a-1))   a<=1 => Exp (-t/2)
	 * Singularity with a==1: [0..1[
	 */
	public static CatDerive ACosh(double a) {
		return null; //TODO: implement this
	}

	static { //Initializer
		CosH.setInverse   (ArCosH.ArCosH);
		CosH.setDerivative(SinH  .SinH  );
		CosH.setIntegral  (SinH  .SinH  );
		CosHMinus1.setInverse   (new CatDerive(ArCosH.ArCosH, Succ.SUCC));
		CosHMinus1.setDerivative(SinH  .SinH  );
		CosHMinus1.setIntegral  (new Diff(Sinus.SINUS,
										  Identity.IDENTITY));
	}

	/**private Constructor for Singleton Implementation	 */
	private CosH(){ }

	/**This Function represents the CosH-1 Function.  */
	public Object Map (Object arg) { return ((MetricBody) arg).cosHm1 (); }

	/**Returns CosH(x)-1.	 */
	public double Map(double x) { return COS_H_M1(x); }

	/**Returns CosH(x)-1.	 */
	public float  Map(float  x) { return COS_H_M1(x); }

	/**Returns the CosH-1 Function's Derivative: SinH(x) for all x.	 */
	public double getDerivative(double x) { return SinH.SIN_H(x); }	//

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble Derivative) {
		Derivative.Value = SinH.SIN_H(x); //Optimization is faster here, since no Check necessary.
		return Math.sqrt(ICountAble.ONE + Derivative.Value*Derivative.Value); } //CH^2 - SH^2 = 1

	/**Returns CosH(x)-1
	 * Gives better accuracy.	 */
	final static public MetricBody COS_H  (MetricBody x) { x = COS_H_M1(x); x.inc(); return x; }

	/**Returns CosH(x)-1
	 * Gives better accuracy.	 */
	final static public double COS_H  (double x) { return COS_H_M1(x) + ICountAble.ONE; }

	/**Returns CosH(x)-1
	 * Gives better accuracy.	 */
	final static public MetricBody COS_H_M1(MetricBody x) { //see also SinH()
	    MetricBody Quadrat  = (MetricBody) x.sqr();
		MetricBody Accuracy = (MetricBody) Quadrat.mulAccuracy();	//speeds up testing
	    MetricBody Summe    = (MetricBody) Quadrat.copy(); Summe.halfAt();
	    MetricBody Faktor   = (MetricBody) Summe  .copy();
		int Z1 = 2;	ByRefLong Divisor = new ByRefLong();
		do {
			Divisor.Value = (++Z1)*(++Z1);
			Faktor.divAt(Divisor).mulAt(Quadrat);
			Summe.addAt(Faktor);
	    } while (Faktor.isMoreThan(Accuracy));
		return Summe; }

	/**Returns CosH(x)-1.
		This Power Series gives better accuracy and converges for all x,
		but only fast for small x.
		For complex or Matrix Arguments the AbsV() Function has to be used in the Check
		Additionally you cannot exploit the Periodicity.  */
	final static public double COS_H_M1(double x) {	// return (MIntRing_(copy()).cosM1At(); }
		double Quadrat	= x*x;
		if (Quadrat	> ICountAble.ONE){
			double Exp = Math.exp(x);
			return IMeasurAble.HALF*(Exp + ICountAble.ONE/Exp) - ICountAble.ONE; }	//larger Value, so use dumb Calculation!
		double Accuracy	= ByRefDouble.MUL_ACCURACY(Quadrat);	//speeds up testing
		double Summe	= IMeasurAble.HALF * Quadrat;
		double Faktor	= Summe;
		int Z1 = 1;	long Divisor;
		do {
			Divisor = ++Z1;
			Summe += (Faktor *= Quadrat / (Divisor*= ++Z1));
		} while (Faktor > Accuracy);
		return Summe; }

	/**Returns CosH(x)-1.
		This Power Series gives better accuracy and converges for all x,
		but only fast for small x.
		For complex or Matrix Arguments the AbsV() Function has to be used in the Check
		Additionally you cannot exploit the Periodicity.  */
	final static public float  COS_H_M1(float  x) {	// return (MIntRing_(copy()).cosM1At(); }
		float Quadrat	= x*x;
		if (Quadrat	> ICountAble.ONE) {
			float Exp = (float) Math.exp(x);
			return IMeasurAble.HALF*(Exp + ICountAble.ONE/Exp) - ICountAble.ONE; }	//larger Value, so use dumb Calculation!
		float Accuracy	= ByRefFloat.mulAccuracy(Quadrat);	//speeds up testing
		float Summe	= IMeasurAble.HALF * Quadrat;
		float Faktor	= Summe;
		int Z1 = 1;	long Divisor;
		do {
			Divisor = ++Z1;
			Summe += (Faktor *= Quadrat / (Divisor*= ++Z1));
		} while (Faktor > Accuracy);
		return Summe; }

	/**Tests asymmetrischer Cosh : a=0 => CosH
	 * Exp (x/(1+a))+Exp (x/(1-a)) a=1 => Exp (-t)	 */
	public static void testACosh() throws java.io.IOException { }

}

