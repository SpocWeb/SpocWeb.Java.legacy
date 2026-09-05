package function.derive.ring.body;

//import Stream.Copy.*;
import streamIO.Log;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.IFloatFunction;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;

/**DawsonInt.java
 *
 * Created on 2. Januar 2001, 10:19
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:43:07Z
 * digest: 3aa591587d36cdd0a3b03a4af5cda326c634326f0304bf3279d21dba7be1364c
 * stale: false
 * tags: [code/numerical_integration, code/mathematical_function]
 * concepts: [Special Functions, Dawson Integral]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class DawsonInt
//extends AFunction
implements IFloatFunction { //AFloatDeriveAble {
    
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(DawsonInt.class, 0);
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	discrete Probability Distributions	
	/////////////////////////////////////////////////////////////////////////////////////
	
    /** Creates new DawsonInt */
    public DawsonInt () {}
    
	/**Number of symmetric odd Coefficients used in the Sampling-Theorem Representation.	 */
	final static public int DNMax = 8;	//{symmetrische ungerade Koeffizienten}
	/**Number of Coefficients precomputed for the Power Series Representation.	 */
	final static public int PNMax = 5;	//
	/**Cached exp(-x^2) Sample Coefficients, lazily built by {@link #DawsonInit()}.	 */
	public static ByRefDouble[]	DawsonC; //{von -15 ... +15}
	/**Sampling Step Size for the Sampling-Theorem Representation.	 */
	final static public ByRefDouble	DawsonH = new ByRefDouble(0.4);
	/**Precomputed Power Series Coefficients -2/(2i+1)!!, refined in Place by {@link #DawsonInit()}.	 */
	final static public ByRefDouble[]
	DPRCoeff = {new ByRefDouble(-2.0),
				new ByRefDouble(-2.0/3),
				new ByRefDouble(-2.0/5),
				new ByRefDouble(-2.0/7),
				new ByRefDouble(-2.0/9),
				new ByRefDouble(-2.0/11)}; //{usw.}

	/**Initializes the Dawson Integral Algorithm,
		is being called by the Implementation below	*/
	final static public void DawsonInit() {
		DawsonC = new ByRefDouble[DNMax+1];
	//		DPRCoeff= new ByRefDouble[PNMax+1];
		int i = 0; while (++i <= DNMax) {double x = ((i << 1) -1)*DawsonH.Value;
										 DawsonC [i] = new ByRefDouble(Math.exp(-x*x));}
			i = 1; while (++i <= PNMax)  DPRCoeff[i].Value *= DPRCoeff[i-1].Value;
	}	//DPRCoeff[i] = (-2)^i/(2i+1)!!

	/**Dawsons Integral is defined as
	 * exp(-x^2)*Int(0,x, exp(t^2)	 */
	final static public IMetricIRing DawsonInt_(IMetricIRing x) {	//TODO: Horner Schema verwenden
		if (ByRefFloat.getFloat(x) < 0.2) { //{Potenzreihe verwenden}
			//Horner Schema with 4 Coefficients
			IMetricIRing xx = (IMetricIRing) x.sqr();
			IMetricIRing tmp= (IMetricIRing) xx.mul (DPRCoeff [5]);
			tmp.addAt(DPRCoeff [4]); tmp.mulAt(xx);
			tmp.addAt(DPRCoeff [3]); tmp.mulAt(xx);
			tmp.addAt(DPRCoeff [2]); tmp.mulAt(xx);
			tmp.addAt(DPRCoeff [1]); tmp.mulAt(xx);
			tmp.inc(); tmp.mulAt(x);
			return tmp; }
		//{Sampling-Theorem-Repraesentation verwenden}
		if (DawsonC == null) { DawsonInit(); } 
		IMetricIRing xx = (IMetricIRing) x .AbsV();
		IMetricIRing n0 = (IMetricIRing) xx.div (DawsonH); n0.halfAt(); n0.roundAt().dblAt(); //{naechste gerade Zahl bei xx/DawsonH = max. El.}
		IMetricIRing d1 = (IMetricIRing) n0.succ();
		IMetricIRing d2 = (IMetricIRing) n0.pred();
		MetricBody  xp = (MetricBody ) xx.sub(n0.mul(DawsonH));
		MetricBody  e1 = (MetricBody ) xp.dbl (); e1.mulAt(DawsonH); e1.expAt();
		IMetricIRing e2 = (IMetricIRing) e1.sqr ();
		IMetricIRing Sum= (IMetricIRing) x .zero();
		int i = 0; while (++i <= DNMax) {
			IMetricIRing tmp = (IMetricIRing) d2.mul(e1); tmp.invAt(); tmp.addAt(e1.div(d1)); tmp.mulAt(DawsonC [i]);
			Sum.addAt(tmp);
			d1.inc().inc();
			d2.dec().dec();
			e1.mulAt(e2);
		}
		if (x.negative()) Sum.negAt();
		xp.sqrAt(); xp.negAt();
		Sum.divAt(IMeasurAble.SqRtPi).mulAt(xp.expAt());
		return Sum; }
	
    /**Reports that DawsonInt imposes no particular Ordering on its Argument.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }
    
	/** Returns the Function Value (mapping) of the Argument arg  */
	public double Map(double x) { return DAWSON_INTEGRAL(x); }
	
	/** Returns the Function Value (mapping) of the Argument arg  */
	public float  Map(float  x) { return (float) DAWSON_INTEGRAL(x); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Dawsons Integral is defined as
	 * exp(-x^2)*Int(0,x, exp(t^2)	 */
	final static public double DAWSON_INTEGRAL(double x) {	//TODO: Horner Schema verwenden
		if (x < 0.2)  //{Potenzreihe verwenden}
		{	//Horner Schema with 4 Coefficients
			double xx = x*x;
			double tmp= xx * DPRCoeff [5].Value;
			tmp +=   DPRCoeff[4].Value; tmp *= xx;
			tmp +=   DPRCoeff[3].Value; tmp *= xx;
			tmp +=   DPRCoeff[2].Value; tmp *= xx;
			tmp +=   DPRCoeff[1].Value; tmp *= xx;
			tmp += ICountAble.ONE; tmp *= x ;
			return tmp; }
		else {  //{Sampling-Theorem-Repraesentation verwenden}
			if (DawsonC == null) DawsonInit();
			double xx =  Math.abs(x);
			long n0 =  Math.round(xx /(DawsonH.Value + DawsonH.Value)) << 1; //{naechste gerade Zahl bei xx/DawsonH = max. El.}
			long d1 =  n0 + 1;
			long d2 =  n0 - 1;
			double xp = xx - (n0 * DawsonH.Value);
			double e1 = Math.exp((xp+xp)*DawsonH.Value);
			double e2 = e1 * e1;
			double Sum= ICountAble.ZERO;
			int i = 0; while (++i <= DNMax) {
				double tmp = (ICountAble.ONE / (d2 * e1) + e1 / d1) * DawsonC [i].Value;
				Sum += tmp;
				d1 += 2;
				d2 -= 2;
				e1 *= e2;
			}
			if (x < ICountAble.ZERO) Sum = -Sum;
			return Sum * Math.exp(-xp*xp) / IMeasurAble.SQRTPI; }
	}

	/**Reference Value Pairs {@code {x, DawsonInt(x)}} used by {@link #testIt()}.	 */
	final static public double[][] dawsonValues = {
		{0.04, 0.0399573606}, 
		{0.16, 0.1572970920}, 
		{1.60, 0.3999398943}, 
		{10.0, 0.0502538471}
	};
	
	/**Verifies {@link #DawsonInt_(IMetricIRing)} against the reference {@link #dawsonValues} Pairs.	 */
	final static public void testIt() throws Exception {
		final IMetricIRing x = new BodyDouble(); //(MetricIRing) testInstance.copy();
		for (int i = dawsonValues.length; --i >= 0;) {
			x.copyAt(new ByRefDouble(dawsonValues[i][0]));
			final IMetricIRing y = DawsonInt_(x);
			L.n("  x = ").l(dawsonValues[i][0]).l(
				"; y = ").l(dawsonValues[i][1]).l(
				" Dawson (x) = ").l(y);
			//Assert.EQUALS(y, new Double(dawsonValues[i][1])); 
		}
		L.readString();
	}

	///////////////////////////////

	//	everything for Dawson's Integral	//
	/*
	private static final int DNMax = 8;	//{symmetrische ungerade Koeffizienten}
	private static final int PNMax = 5;	//
	private static		 ByRefDouble[]	DawsonC; //{von -15 ... +15}
	private static final ByRefDouble	DawsonH =	new ByRefDouble(0.4);
	private static final ByRefDouble[]	DPRCoeff = {new ByRefDouble(-2.0),
													new ByRefDouble(-2.0/3),
													new ByRefDouble(-2.0/5),
													new ByRefDouble(-2.0/7),
													new ByRefDouble(-2.0/9),
													new ByRefDouble(-2.0/11)}; //{usw.}

	private static final void DawsonInit() {
		DawsonC = new ByRefDouble[DNMax+1];
	//	DPRCoeff= new ByRefDouble[PNMax+1];
		int i = 0; while (++i <= DNMax) {double x = ((i << 1) -1)*DawsonH.Value;
										 DawsonC [i] = new ByRefDouble(Math.exp (-x*x)); }
			i = 1; while (++i <= PNMax)  DPRCoeff[i].Value *= DPRCoeff[i-1].Value;
	}	//DPRCoeff[i] = (-2)^i/(2i+1)!!

	/**Dawsons Integral is defined as
	 * exp(-x^2)*Int(0,x, exp(t^2)	 */
	/*public MetricBody DawsonInt ()
	{	//TODO: Horner Schema verwenden
		if (((IMeasurAble) this).getFloat() < 0.2)  //{Potenzreihe verwenden}
		{	//Horner Schema with 4 Coefficients
			MetricBody xx  = (MetricBody) sqr();
			MetricBody ret = (MetricBody) xx.mul (DPRCoeff [5]);
			ret.addAt(DPRCoeff [4]); ret.mulAt(xx);
			ret.addAt(DPRCoeff [3]); ret.mulAt(xx);
			ret.addAt(DPRCoeff [2]); ret.mulAt(xx);
			ret.addAt(DPRCoeff [1]); ret.mulAt(xx);
			ret.inc(); ret.mulAt(this);
			return ret;
		} else {   //{Sampling-Theorem-Repraesentation verwenden}
			if (DawsonC == null) DawsonInit();
			MetricBody xx = (MetricBody) AbsV();
			MetricBody n0 = (MetricBody) xx.div(DawsonH); n0.halfAt(); n0.roundAt().dblAt(); //{naechste gerade Zahl bei xx/DawsonH = max. El.}
			MetricBody d1 = (MetricBody) n0.succ();
			MetricBody d2 = (MetricBody) n0.pred();
			MetricBody xp = (MetricBody) xx.subt(n0.mul(DawsonH));
			MetricBody e1 = (MetricBody) xp.dbl(); e1.mulAt(DawsonH); e1.expAt();
			MetricBody e2 = (MetricBody) e1.sqr ();
			MetricBody Sum= (MetricBody) newInstance(); Sum.zeroAt(); 	//zero() returns a Constant!
			int i = 0; while (++i <= DNMax)	{
				MetricBody tmp = (MetricBody) d2.mul(e1);
				tmp.invAt(); tmp.addAt(e1.div(d1)); tmp.mulAt(DawsonC [i]);
				Sum.addAt(tmp);
				d1.inc().inc();
				d2.dec().dec();
				e1.mulAt(e2); }
			if (negative()) Sum.negAt();
			xp.sqrAt(); xp.negAt();
			Sum.divAt(IMeasurAble.SqRtPi).mulAt(xp.expAt());
			return Sum; }
	}
	*/
	/**The main entry point for the application.
	 * Prints out the Factorial of the Value passed via the Command Line, 
	 * otherwise performs the self-test.
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		if (args.length > 0) {
			System.out.println(DAWSON_INTEGRAL(Double.parseDouble(args[0])));
		} else {
			testIt(); 
		}
	}
		
}
