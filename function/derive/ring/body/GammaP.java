package function.derive.ring.body;

import java.io.IOException;

import math.vector.VectorString;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.body.ABodyDouble;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import streamIO.object.IStreamIn;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;
import function.derive.AFloatDeriveAble;
import function.derive.IDeriveAble;
import function.derive.ring.AddAt;
import function.derive.ring.CatDerive;
import function.derive.ring.Neg;
import function.derive.ring.Prod;
import function.derive.ring.Quot;

/**This Class encapsulates the normed incomplete GammaP Function
 * together with the Logarithm of the complete Gamma.
 *
 * The Function for 'double' Values is defined in Combinatoric.ProbFuncs
 *
 *              1     x
 *  P (x,a):=-------- I Exp (-t)*t^(a-1) dt
 *           Gamma(a) 0
 *				  Infin
 * with Gamma(a):=  I Exp (-t)*t^(a-1) dt
 *					0
 *
 * GammaPLn	= Ln(GammaPFactor (x, a))     -GammaPFactorLn(x)
 * GammaP	=	 GammaPFactor (x, a)  * e^-GammaPFactorLn(x)
 * 
 * @see function.derive.ring.body.Gauss 
 * The Gaussian Integral is calculcated as the 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:44:10Z
 * digest: 54ad0d16ffae1e59e092b9673bf7480e32ffc857a3eab9f0e2cd2d5f2590c9bf
 * stale: false
 * tags: [code/gamma_function, code/derivable_function_contract]
 * concepts: [Special Functions, Incomplete Gamma Function]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class GammaP
extends AFloatDeriveAble {	//IPartialDerive { //

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(GammaLn.class);
	
	////////////////////////////////////////////////////////////////////////////
	
	/**continuous Parameter, not changeable from outside	 */
	protected MetricBody a;

	/**Norming, calculated on Initialization, not changeable from outside	 */
	protected MetricBody GamLn;

	/**Returns GammaLn(a), the norming Factor for this Function.	 */
	public MetricBody GammaLn()	{ return (MetricBody) GamLn.copy(); }
	//Create Copy to keep TX short and avoid Side Effects!
	
    /**Reports that GammaP preserves strict ascending order of its argument.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }

	/**Returns the Derivative of this Function as a symbolic Expression, Exp(-x)*x^(a-1).
	 * @return the Derivative of this Function	 */
	public IDeriveAble getDerivative() {
		if (Derivative != null) return Derivative; //Exp (-t)*t^(a-1)
		Derivative = //new MulAt(, //Norming
					 new Prod(new CatDerive(Exponential.EXPONENTIAL, Neg.NEG), new Power(a.pred()));
		return Derivative; }

	/**Initializing Constructor
	 * Also sets the Derivative:
	 * f'(x, a)	= x^(a-1)*e^-x / Gamma(a)
	 *			= x^(a-1)/e^(x+GammaLn(a))
	 *
	 */
	public GammaP(Object a_) {
		this.a = (MetricBody) ((MetricBody) a_).copy();	//Create Copy to keep TX short and avoid Side Effects!
		GamLn  = GammaLn.GAMMA_LN(a);	//Optimization: GammaLn(a) has to be calculated only once on Initialization!
		setDerivative(	new Quot(new Power(a.pred()),
						new CatDerive(Exponential.EXPONENTIAL, new AddAt(GamLn))));
	}

	/**This Function represents the GammaLn Function.  */
	public Object Map (Object arg)	{ return GAMMA_P_NORMED((MetricBody) arg, a, GamLn); }

	/**This Function represents the GammaLn Function.  */
	public double Map (double arg)	{
		return GAMMA_P(arg, ByRefDouble.GET_DOUBLE(a),
								 ByRefDouble.GET_DOUBLE(GamLn)); }

	/**Returns this incomplete Gamma Function's Derivative, Exp(-x)*x^(a-1).
	 * @return The Derivative at x	 */
	public double getDerivative(double x) { //Exp (-t)*t^(a-1)
		return Math.exp(-x)*Math.pow(x, ByRefDouble.GET_DOUBLE(a) - 1); }

	/**Calculates Function and Derivative at the same time.
	 * This is economic, because both have similar Characteristics
	 * and thus the same characteristic Elements which speeds up calculation.
	 * @param  derivative ByRef Object used to return the Value of the Derivative at x
	 * @return Function Value at x 	 */
	public double getFuncDerive(double x, ByRefDouble derivative) {
		derivative.Value = getDerivative(x); //TODO optimize and norm this!
		return GAMMA_P(x, ByRefDouble.GET_DOUBLE(a),
							   ByRefDouble.GET_DOUBLE(GamLn)); }
	
	///////////////////////////////////////////////////////////////////////////////
	//  cumulative Probability Distributions based on this Function
	///////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Returns the Probability for the accumulated X^2 Statistics:
	 *               n |y[i]-y(x[i])|2         n |N[i]-n[i]|2
	 *         X^2:=Sum--------------    X^2:=Sum-----------
	 *              i=1     V[i]              i=1   n[i]    
	 * i.e: the Probability that the X^2 with N Degrees of Freedom 
	 * (minus #Fitting Parameters) indicates Similarity of Sample and Model Distribution.
	 * For a fixed Value x[i] = x and y(x[i]) = y, the Mean, as well as
	 * V[i] = V is the Variance of y. 
	 * N[i] and n[i] are the actual and the expected Frequency of Bin Counts
	 * and since Counts n[i] also have a Variance of n[i]
	 * 
	 * This Function rises smoothly with chiSqr from [0,0] through [degreesOfFreedom-1, 0.5] 
	 * to [+Infinity, 1] with a Width of SqRt(degreesOfFreedom).
	 * I.e. the Value of ChiSqr grows proportionally to the Degrees of Freedom.  
	 * 
	 * With DoF > 30 the Chi� Distribution is very close to the Normal Distribution:
	 * SqRt(2*Chi�) = Gauss(mean=SqRt(2*n-1),var=1) for n > 30 
	 * 
	 * @param degreesOfFreedom the Number of free Variables, 
	 * usually the Sample Size minus the Number of fitted or normed Parameters   
	 * 
	 * @param chiSqr the squared Sum of the Differences to the expected Value, normed by the Variance
	 *   
	 * @return the Probability for a Value of chi� smaller than the given, 
	 * based on the null Hypothesis that the Sample is from the same Model. 
	 * The Complement is the Confidence to accept the Null Hypothesis. 
	 */
	final static public double PROBABILITY_CHI_SQR(final double degreesOfFreedom, final double chiSqr) {
		return GAMMA_P(chiSqr*0.5, degreesOfFreedom*0.5); }
	
	/**Computes the cumulative Error Function with arbitrary Accuracy using GammaP.
	 * @return the cumulative Error Function with arbitrary Accuracy using GammaP,
	 * the incomplete Gamma Function:	(1+Sign (x)*GammP (Sqr (x)/2,Halb))/2	 */
	final static public double PROBABILITY_GAUSS_CUM(final double x) {
		double tmp = GammaP.GAMMA_P(x*x*0.5, 0.5, (ByRefDouble) null);
		if (x < 0) 
			tmp = -tmp;  
		return 0.5*(1+tmp); }

	/**Returns the cumulative Poisson Distribution with arbitrary Accuracy using GammaP,
	 * 
	 * @param x > 0 Expectation Value of the Distribution 
	 * @param k >=0 the Number of Events to observe. 
	 * normally the integer Value is the Truncation of this Value. 
	 * i.e. 
	 * P(1)=P(k < 2) = PROBABILITY_POISSON_CUM(1.999999999999)
	 * P(0)=P(k < 1) = PROBABILITY_POISSON_CUM(0.999999999999) = e^-x
	 * @return the cumulative Probability of k or less Events 
	 * from a Distribution with Expectation Value x
	 */
	final static public double PROBABILITY_POISSON_CUM(final double x, final double k) {
		return 1-GammaP.GAMMA_P(x, k, (ByRefDouble) null); }

	///////////////////////////////////////////////////////////////////////////////
	//  static Methods for calculating this Function
	///////////////////////////////////////////////////////////////////////////////

	/**Returns the normed incomplete GammaP Function
	 * together with the Logarithm of the complete Gamma.
	 *              1     x
	 *  P (x,a):=-------- I Exp (-t)*t^(a-1) dt
	 *           Gamma(a) 0
	 *				  Infin
	 * with Gamma(a):=  I Exp (-t)*t^(a-1) dt
	 *					0
	 *
	 * GammaPLn	= Ln(GammaPFactor (x, a))     -GammaPFactorLn(x)
	 * GammaP	=	 GammaPFactor (x, a)  * e^-GammaPFactorLn(x)
	 */
	final static public MetricBody GAMMA_P(MetricBody x, MetricBody a, MetricBody gamLn) {
		gamLn.copyAt(GammaLn.GAMMA_LN(a));	//Optimization: GammaLn(a) has to be calculated only once on Initialization!
		return GAMMA_P_NORMED(x, a, gamLn); }
	
	/**Uses the Parameter NormLn for Normalization.
	 * If NormLn = GammaLn(a), the Function reaches 1 for x -> Infinity	 */
	final static public MetricBody GAMMA_P_NORMED(MetricBody x, MetricBody a, MetricBody NormLn) {
		boolean Complement;
		MetricBody GammaP = (MetricBody)
						((Complement = (x.isLessThan(a.succ()))) ?
						GammaPFactorPS(x , a) :		//Power Series
						GammaPFactorCF(x , a))		//Continued Fraction
	.mulAt(((MetricBody)GammaPFactorLn(x , a).subAt(NormLn)).expAt());
		if (!Complement) { 
			return (MetricBody) GammaP.ResidAt(); } 
		return GammaP; }
	
	/**Returns the pricipal Factor of the Gamma Function: exp(-x)*x^a
	 * stripped by the Modification from GammaPFactorPS / GammaPFactorCS
	 * This is the Factor that explodes when x grows
	 * and is cancelled with GammaLn(a).	 */
	final static public MetricBody GammaPFactorLn(Object x, Object a){
		return  (MetricBody)
				((MetricBody)
				((MetricBody)x).ln().mulAt(a)).subAt(x);}
	
	/**Returns the Gamma Function stripped by the Factor: e^GammaFactorLn
	 * This Power Series converges for all complex z with 0 < Re(z) < a+1
	 * and returns the modestly rising Part of Gamma.	 */
	final static public MetricBody GammaPFactorPS(final MetricBody x, final MetricBody a) {
		if (! a.positive()) {
			throw new AbstractMethodError(a+" = a but the incomplete Gamma- Functions is defined only for a >= 0"); } 
		if (x.negative()) {
			throw new AbstractMethodError(x+" = x but the incomplete Gamma- Functions is defined only for x > 0"); } 
		if (x.isZero()) {
			return (MetricBody) x.one(); } 
		MetricBody factor = (MetricBody) a.inv ();
		MetricBody sum = (MetricBody) factor.copy();
		MetricBody a_= (MetricBody) a.copy(); //don't modify a in Place!
		int i = 0;	//AOrderAble.MaxIter;
		do {
			if (++i > ByRefInt.MAX_ITER) {
				throw new AbstractMethodError(i+" Iterations were not sufficient for x="+x+" and a="+a); } 
			sum.addAt(factor.mulAt(x.div(a_.inc())));
		} while (factor.AbsV().isMoreThan(sum.mulAbsAccuracy()));
		return sum; } 	//GammaP = f * x^a / e^x

	/**Returns the Gamma Function stripped by the Factor: e^GammaFactorLn
	 * This Power Series converges for all complex z with 0 < Re(z) < a+1
	 * and returns the modestly rising Part of Gamma.	 */
	final static public double GammaPFactorPS(final double x, double a) {
		testParameters(x, a); 
		if (x == ICountAble.ZERO) {
			return ICountAble.ONE; } 
		double factor = ICountAble.ONE/a;
		double sum = factor;
		int i = 0;	//AOrderAble.MaxIter;
		do {
			if (++i > ByRefInt.MAX_ITER) {
				throw new AbstractMethodError(i+" Iterations were not sufficient for x="+x+" and a="+a); } 
			sum += (factor *= (x / (a += ICountAble.ONE)));
		} while (Math.abs(factor) > ByRefDouble.MUL_ABS_ACCURACY(sum));
		return sum; } 	//GammaP = f * x^a / e^x

	/**Returns the Gamma Function stripped by the Factor: e^GammaFactorLn
	 * This Continued Fraction converges for all complex z with Re(z) > a + 1
	 * and returns the modestly rising Part of Gamma.	 */
	final static public MetricBody GammaPFactorCF(MetricBody x, MetricBody a) {
//		if ((! a.positive()) || (x.negative())) throw new AbstractMethodError("Incomplete Gamma- Functions only for a >= 0 and x > 0 defined!");
		if (x.isZero()) { return (MetricBody)((MetricBody) x.newInstance()).oneAt(); } 
		MetricBody tmp;
		final MetricBody a_ = (MetricBody) a.neg ();
		MetricBody f  = (MetricBody) ((MetricBody) x.newInstance()).zeroAt();
		MetricBody fa = (MetricBody) ((MetricBody) x.newInstance()).zeroAt();
		final MetricBody b0 = (MetricBody) ((MetricBody) x.newInstance()).zeroAt();
		final MetricBody b1 = (MetricBody) ((MetricBody) x.newInstance()). oneAt();
		final MetricBody a0 = (MetricBody) ((MetricBody) x.newInstance()). oneAt();
		final MetricBody a1 = (MetricBody) x.copy(); //Cannot use one(), because that is made constant!
		final MetricBody factor = (MetricBody) x.newInstance(); factor.oneAt();   /*Der Renormalisierungs-Faktor gegen Ueberlauf der Rekursion */
		final ByRefInt i = new ByRefInt(0);	 //AOrderAble.MaxIter;
		do { /*Zaehler bzw. Nenner*/
			if (++i.Value > ByRefInt.MAX_ITER) {
				throw new AbstractMethodError(i+" Iterations were not sufficient for x="+x+" and a="+a); } 
			a_.inc();
			((IGroupM) ((IGroup) a0.mulAt(a_)).addAt(a1)).mulAt(factor); /*RekursionsSchritt*/
			((IGroupM) ((IGroup) b0.mulAt(a_)).addAt(b1)).mulAt(factor);
			factor.mulAt(i);
			((IGroup) a1.mulAt(factor)).addAt(x.mul(a0));
			((IGroup) b1.mulAt(factor)).addAt(x.mul(b0));
			if (! a1.isZero()) { /*Renormalisieren*/
				((IGroupM) factor.copyAt(a1)).invAt();
				((IGroupM) f.copyAt(b1)).mulAt(factor); /*neues Ergebnis*/
				tmp = f; f = fa; fa = tmp; }
		} while (!fa.equals(f)); // AbsDist(fa).grtr(f.mulAbsAccuracy()));
		return f; }	//GammaP = f * x^a / e^x

	/**Returns the Gamma Function stripped by the Factor: e^GammaFactorLn
	 * This Continued Fraction converges for all complex z with Re(z) > a + 1
	 * and returns the modestly rising Part of Gamma.	 */
	final static public double GammaPFactorCF(final double x, final double a) {
		testParameters(x, a); 
		if (x == ICountAble.ZERO) {
			return ICountAble.ONE; } 
		double a_ =  -a;
		double f  =  ICountAble.ZERO;
		double fa =  ICountAble.ZERO;
		double b0 =  ICountAble.ZERO;
		double b1 =  ICountAble.ONE ;
		double a0 =  ICountAble.ONE ;
		double a1 =  x;
		double Faktor = ICountAble.ONE;   /*Der Renormalisierungs-Faktor gegen Ueberlauf der Rekursion */
		int i = 0;	 //AOrderAble.MaxIter;
		do {	/*Zaehler bzw. Nenner*/
			if (++i > ByRefInt.MAX_ITER) {
				throw new AbstractMethodError(i+" Iterations were not sufficient for x="+x+" and a="+a); } 
			a_ += ICountAble.ONE;
			a0 = (a0*a_ + a1)*Faktor; /*RekursionsSchritt*/
			b0 = (b0*a_ + b1)*Faktor;
			Faktor *= i;
			a1 = a1*Faktor + x * a0;
			b1 = b1*Faktor + x * b0;
			if (a1 != ICountAble.ZERO) { /*Renormalisieren*/
				Faktor = ICountAble.ONE / a1;
				f = b1 * Faktor; /*neues Ergebnis*/
				final double tmp = f; f = fa; fa = tmp; } //swap both results
		} while (Math.abs(f - fa) > ByRefDouble.MUL_ABS_ACCURACY(f)); //both of same Size and != 0, don't need to add them!
		return f; }	//GammaP = f * x^a / e^x

	/**
	 * @param x
	 * @param a
	 * @throws AbstractMethodError when the Parameters are not in the Range for the incomplete Gamma Function. 
	 */
	private static void testParameters(final double x, final double a) throws AbstractMethodError {
		if (a < 0) {
			throw new AbstractMethodError(a+" = a but the incomplete Gamma- Functions is defined only for a >= 0"); } 
		if (x < 0) {
			throw new AbstractMethodError(x+" = x but the incomplete Gamma- Functions is defined only for x > 0"); }
	}
	
	/**Returns the normed incomplete GammaP Function
	 * together with the Logarithm of the complete Gamma.
	 *              1     x
	 *  P (x,a):=-------- I Exp (-t)*t^(a-1) dt
	 *           Gamma(a) 0
	 *				  Infin
	 * with Gamma(a):=  I Exp (-t)*t^(a-1) dt
	 *					0
	 *
	 * GammaPLn	= Ln(GammaPFactor (x, a))     -GammaPFactorLn(x)
	 * GammaP	=	 GammaPFactor (x, a)  * e^-GammaPFactorLn(x)
	 */
	final static public double GAMMA_P(final double x, final double a, final double[] gamLn) {
		final double gammLn = GammaLn.GAMMA_LN(a);
		if ((gamLn != null) && gamLn.length > 0) {
			gamLn[0] = gammLn; }	//Optimization: GammaLn(a) has to be calculated only once on Initialization!
		return GAMMA_P(x, a, gammLn); }

	/**Returns the normed incomplete GammaP Function
	 * together with the Logarithm of the complete Gamma.
	 *              1     x
	 *  P (x,a):=-------- I Exp (-t)*t^(a-1) dt
	 *           Gamma(a) 0
	 *				  Infin
	 * with Gamma(a):=  I Exp (-t)*t^(a-1) dt
	 *					0
	 *
	 * GammaPLn	= Ln(GammaPFactor (x, a))     -GammaPFactorLn(x)
	 * GammaP	=	 GammaPFactor (x, a)  * e^-GammaPFactorLn(x)
	 */
	final static public double GAMMA_P(final double x, final double a) {
		return GAMMA_P(x, a, (double[]) null); }

	/**Returns the normed incomplete GammaP Function
	 * together with the Logarithm of the complete Gamma.
	 *              1     x
	 *  P (x,a):=-------- I Exp (-t)*t^(a-1) dt
	 *           Gamma(a) 0
	 *				  Infin
	 * with Gamma(a):=  I Exp (-t)*t^(a-1) dt
	 *					0
	 *
	 * GammaPLn	= Ln(GammaPFactor (x, a))     -GammaPFactorLn(x)
	 * GammaP	=	 GammaPFactor (x, a)  * e^-GammaPFactorLn(x)
	 */
	final static public double GAMMA_P(final double x, final double a, final ByRefDouble gamLn) {
		final double gammLn = GammaLn.GAMMA_LN(a);
		if (gamLn != null) {
			gamLn.Value = gammLn; }	//Optimization: GammaLn(a) has to be calculated only once on Initialization!
		return GAMMA_P(x, a, gammLn); }

	/**Returns the normed incomplete GammaP Function
	 * together with the Logarithm of the complete Gamma.
	 *              1     x
	 *  P (x,a):=-------- I Exp (-t)*t^(a-1) dt
	 *           Gamma(a) 0
	 *				  Infin
	 * with Gamma(a):=  I Exp (-t)*t^(a-1) dt
	 *					0
	 *
	 * GammaPLn	= Ln(GammaPFactor (x, a))     -GammaPFactorLn(x)
	 * GammaP	=	 GammaPFactor (x, a)  * e^-GammaPFactorLn(x)
	 * 
	 * @param x
	 * @param a
	 * @param gammaLn should be GammaLn(a) and is used for Normalization 
	 * and this Function should reach 1 for x -> Infinity 
	 * @return
	 */
	final static public double GAMMA_P(final double x, final double a, final double gammaLn) {
		final boolean complement = (x < a + 1);
		final double GammaP = (complement ?
						 GammaPFactorPS(x , a) :	//Power Series
						 GammaPFactorCF(x , a)) *	//Continued Fraction
				Math.exp(GammaPFactorLn(x , a) - gammaLn);
		if (!complement) {
			return 1 - GammaP;} 
		return GammaP; }

	/**Returns the Gamma Function stripped by the Factor: GammaFactor
	 * stripped by the Modification from GammaPFactorPS / GammaPFactorCS
	 * This is the Factor that explodes when x grows
	 * and is cancelled with GammaLn(a).
	 * @return the pricipal Factor of the Gamma Function: exp(-x)*x^a	 
	 */
	final static public double GammaPFactorLn(double x, double a){
		return (a*Math.log(x)) - x; }

	//////////////////////////////
	//	Combinatoric Functions	//
	//////////////////////////////
	
	/**Returns the Gamma Function stripped by the Factor: e^GammaFactorLn
	 * This Approximation converges for all complex z with Re(z) > 0
	 * and returns the modestly rising Part of Gamma.	 */
	final static public double GammaFactor (double x) {
		/*Angepasste Stirling-Approximation mit den ersten N Polen*/
		double d3 = IMeasurAble.COEFF_GAMMA[0]; /*fast 1, Zeta [?]*/
		for (int i = 1; i < IMeasurAble.COEFF_GAMMA.length; ++i)
			d3 += IMeasurAble.COEFF_GAMMA[i]/++x;
		return IMeasurAble.SQRT2PI*d3; }

	/**Returns the Factor that lets the Gamma Function explode when x grows:
	 * e^GammaFactorLn	 */
	final static public double GammaFactorLn (double x) {
		double d2 =  x + 4.5; /*Hier ist N = 6,kleinGamma = 5*/
		return d2 - (x - IMeasurAble.HALF)*Math.log(d2); }

	/**Returns the negative Logarithm of the Beta Function,
	 * which is the real Extension to the Reciprocal of the Binomial Coefficients:
	 * since Gamma(n+1) = n! =>
	 * Beta ((n-k)+1, k+1)	= Gamma(k+1) * Gamma((n-k)+1) / Gamma(k+1 + (n-k)+1) =
	 *						= k!*(n-k)!/(k+(n-k)+1)! = k!*(n-k)! / (n+1)!
	 *						= 1/(n+1)*Combination(n,k)
	 *
	 * 				Gamma (x) * Gamma (y)	 1
	 * Beta(x,y):= ---------------------- = Int t^(x-1)*(1-t)^(y-1)
	 *                 Gamma (x+y)			t=0
	 */
	final static public double BetaLn(double x, double y) {
		return 	GammaLn.GAMMA_LN(x) +
				GammaLn.GAMMA_LN(y) -
				GammaLn.GAMMA_LN(x+y); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Values of the incomplete GammaP Function in pairs: {a, x, GammaP(a,x)}	 */
	private static final float[][]
		ValuesGammaP = {{ 0.1f, 3.1622777E-02f, 0.7420263f},
						{ 0.1f, 3.1622777E-01f, 0.9119753f},
						{ 0.1f, 1.5811388f,		0.9898955f},
						{ 0.5f, 7.0710678E-02f, 0.2931279f},
						{ 0.5f, 7.0710678E-01f, 0.7656418f},
						{ 0.5f, 3.5355339f,		0.9921661f},
						{ 1.0f, 0.1000000f,		0.0951626f},
						{ 1.0f, 1.0000000f,		0.6321206f},
						{ 1.0f, 5.0000000f,		0.9932621f},
						{ 1.1f, 1.0488088E-01f, 0.0757471f},
						{ 1.1f, 1.0488088f,		0.6076457f},
						{ 1.1f, 5.2440442f,		0.9933425f},
						{ 2.0f, 1.4142136E-01f, 0.0091054f},
						{ 2.0f, 1.4142136f,		0.4130643f},
						{ 2.0f, 7.0710678f,		0.9931450f},
						{ 6.0f, 2.4494897f,		0.0387318f},
						{ 6.0f, 12.247449f,		0.9825937f},
						{11.0f, 16.583124f,		0.9404267f},
						{26.0f, 25.495098f,		0.4863866f},
						{41.0f, 44.821870f,		0.7359709f},
						{ 1.0f, 0.1000000f,		0.0951626f}
	};
	
	private static final void testPoissonCum() {
		L.n("Testing the Poisson based on the incomplete Gamma Function():");
		testPoissonCum(2.5); 
	}

	private static final void testPoissonCum(final double expectationValue) {
		L.n("Testing the Poisson based on the incomplete Gamma Function():");
		Assert.EQUALS(1, PROBABILITY_POISSON_CUM(expectationValue, 10000));
		Assert.EQUALS(Math.exp(-expectationValue), PROBABILITY_POISSON_CUM(expectationValue, 1));
		Assert.EQUALS(0, PROBABILITY_POISSON_CUM(expectationValue, 0));
	}

	/**Tests the incomplete Gamma Function	 */
	private static final void testGammaP() {
		L.n("Testing the incomplete Gamma Function():");
		L.n(VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("a", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("GammaP(x,a)", -22));
		final ByRefDouble GammaLn = new ByRefDouble();
		for (int i = GammaP.ValuesGammaP.length; --i >= 0;)	{
			final float[] xyPair = GammaP.ValuesGammaP[i];
			final double result = GammaP.GAMMA_P(xyPair[1], xyPair[0], GammaLn);
			L.n(ByRefDouble.FORMAT(xyPair[1] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[2], -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[2], result, 50*ByRefDouble.DOUBLE_ACCURACY, 50*ByRefDouble.DOUBLE_ACCURACY);
		}
		L.readString(); 
	}

	/**Tests the incomplete Gamma Function with MetricBody Instances	 */
	private static final void testGammaP2() throws IOException {
		L.n("Testing the incomplete Gamma Function():");
		L.n(VectorString.FORMAT("x", 8) +
			VectorString.FORMAT("a", 8) +
			VectorString.FORMAT("Expected", 22) +
			VectorString.FORMAT("GammaP(x,a)", 22));
		final ABodyDouble gammaLn = new BodyDouble();
		final ABodyDouble a = new BodyDouble();
		final ABodyDouble x = new BodyDouble();
		for (int i = GammaP.ValuesGammaP.length; --i >= 0;)	{
			final float[] xyPair = GammaP.ValuesGammaP[i];
			x.value = xyPair[1];
			a.value = xyPair[0];
			final float actual = (float)((ABodyDouble) GAMMA_P(x, a, gammaLn)).value; 
			L.n(ByRefDouble.FORMAT(xyPair[1], - 8, 2) +
				ByRefDouble.FORMAT(xyPair[0], - 8, 2) +
				ByRefDouble.FORMAT(xyPair[2], -22, 7) +
				ByRefDouble.FORMAT(actual, -22, 7));
			Assert.EQUALS(actual, xyPair[2]);
		}
		L.readString();
	}

	/**Tests the incomplete Gamma Function	 */
	public static void testIt() throws IOException {
		testPoissonCum();
		testGammaP(); 
		testGammaP2(); 
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Syntax: java GammaP <x> <a>");
			testIt(); 
		} else {
			final double[] gamLn = new double[1];  
			System.out.println(
			GAMMA_P(
			Double.parseDouble(args[0]), 
			Double.parseDouble(args[1]), gamLn));
			System.out.println(gamLn[0]); 
		}
	}

}
