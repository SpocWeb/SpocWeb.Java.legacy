/*
 * File Name: Gauss.java
 * Created on: 12.02.2004
 *
 */
package function.derive.ring.body;

import streamIO.Assert;
import streamIO.Log;
import streamIO.object.IStreamIn;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.derive.AFloatDeriveAble;

/**
 * Title: Gauss<p>
 * Description:
 * Defines the cumulative Gauss Function 
 * and it's Derivative, the Bell Curve.  
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class Gauss 
extends AFloatDeriveAble {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(Gauss.class, 0);
	
	final static public Gauss GAUSS = new Gauss();

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables 
	/////////////////////////////////////////////////////////////////////////////////////

	/** The Average of the desired Gauss Function, 0 by Default	 */
	private final double average; 

	/** The Variance of the desired Gauss Function, 1 by Default	 */
	private final double stdDev; 

	/** Empty Constructor defaulting to the normed Curve 	 */
	public Gauss() { this(0, 1); }

	/** initializing Constructor 
	 * 
	 */
	public Gauss(final double average_, final double stdDev_) { 
		this.average = average_; 
		this.stdDev = stdDev_;
	}
	
	///////////////////////////////////////////////////////////////////////////

	/** @see function.derive.IFloatDeriveAble#getDerivative(double)	 */
	public double getDerivative(final double x) {
		return pGauss((x-average)/stdDev)/stdDev; }
	
	/** 
	 * TODO: not scaled properly! 
	 * @see function.derive.IDeriveAble#getDerivative()	 */
/*	public IDeriveAble getDerivative() {
		return 
			new CatDerive(Exponential.EXPONENTIAL, 
			new CatDerive(Neg.NEG, Square.SQUARE));
	}
	
	/** @see function.derive.IFloatDeriveAble#getFuncDerive(double, function.byref.ByRefDouble)	 */
	public double getFuncDerive(double x, final ByRefDouble derivative) {
		x-=average; 
		x/=stdDev; 
		if (derivative != null) {
			derivative.Value=pGauss(x); }
		return pGaussCum(x); }
	
	/** @see function.derive.IFloatDeriveAble#getFuncDerive(float, function.byref.ByRefFloat)	 */
	public float getFuncDerive(float x, final ByRefFloat derivative) {
		x-=average; 
		x/=stdDev; 
		if (derivative != null) {
			derivative.Value=(float) pGauss(x); }
		return pGaussCum(x); }
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/** @see function.IFloatFunction#Map(double)	 */
	public double Map(final double arg) {
		return pGaussCum((arg-average)/stdDev); }

	/** @see function.IFloatFunction#Map(float)	 */
	public float Map(final float arg) {
		return pGaussCum((arg-(float)average)/(float)stdDev); }
	
	/** @see function.IFunction#Map(java.lang.Object)	 */
	public Object Map(Object arg) { return new ByRefDouble(ByRefDouble.GET_DOUBLE(arg)); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * @param z Fisher's z Statistic, derived from r, Person's linear Correlation Coefficient  
	 * @param n The Sample Size used to determine z
	 * @return Fisher's Probability of the Null Hypothesis that there is no Correlation.
	 * (i.e. low Values indicate a high Significance of the Correlation).
	 * The Parameter z is derived from r and the Number of Items, 
	 * but only reliable for N > 10
	 */
	final static public float PROB_Z_CORRELATION(final float z) {
		return 1-Gauss.pGaussCum(Math.abs(z)); }
	
	/**Returns the Probability of the normed gaussian Bell curve
	 * with Mean 0 and Variance 1.	 */
	final static public double pGauss(final double x) {
		return Math.exp(-x*x*IMeasurAble.HALF)/IMeasurAble.SQRT2PI; }

	/**Returns the accumulated Probability (Integral)
	 * of the normed gaussian Bell curve with Mean 0 and Variance 1, i.e:
	 * the Probability for a Value less than x.
	 * ErrorFc(x) = x - x^3/(2*3) + x^5/5*4*2 - x^7/7*6*4*2 + ...
	 *
	 * This Power Series converges everywhere, still Computation is a Problem,
	 * because of Extinction. Polynoms tend to oscillate for larger x.
	 * With Exp you can exploit the Separation of Mantissa and Exponent.
	 * With Sin, Cos etc. you can exploit the Period to reduce x.
	 * With ArcTan you can exploit ArcTan(1/x) = IMeasurAble.PI/2 - ArcTan(x)
	 * This Approximation doesn't have these Properties.
	 * So you have to switch between a Power Series and Continued Fraction.	 */
	final static public double pGaussCum2(final double x)	{	//Solution starts oscillating for x > 7
		if (Math.abs(x) > 7) { //Values don't cancel well and oscillate for larger Values 
			if (x < 0) return 0; 
			return 1;}
		int Z1 = 2;		//no use to evaluate it only for x > 0
		double Summe  = x;	//Factor and even Sum changes Sign
		double Quadrat = -ByRefDouble.SQR(x);	//doesn't converge fast enough
		double Faktor = x*Quadrat*IMeasurAble.HALF;	//Pot2MulI (Faktor,-1);
	//	while (Math.abs(Faktor) > AOrderAble.DoubleAccuracy*Math.abs(Summe))
		while (Math.abs(Faktor) > ByRefDouble.DoubleAccuracy) {
			//Since an Offset of 0.5 is added, you don't need extra Accuracy at x==0
			Summe += Faktor /++Z1;
			Faktor*= Quadrat/++Z1;
		}
		return (Summe + Faktor/++Z1)/IMeasurAble.SQRT2PI + IMeasurAble.HALF; }

	/**Returns the Error Function with arbitrary Accuracy using GammaP,
	 * the incomplete Gamma Function:	(1+Sign (x)*GammP (Sqr (x)/2,Halb))/2	 */
	final static public double pGaussCum(final double x) {
		return GammaP.PROBABILITY_GAUSS_CUM(x); }

	/**Coefficients for the fast Chebyshev Error Function Approximation	 */
	private static final float[]
	COEFF_ERFC={-1.26551223f,
				  +1.00002368f,
				  +0.37409196f,
				  +0.09678418f,
				  -0.18628806f,
				  +0.27886807f,
				  -1.13520398f,
				  +1.48851587f,
				  -0.82215223f,
				  +0.17087277f};

	/**Returns the Error Function by a fast Chebyshev Approximation
	 * with an Accuracy of 1.2E-7 anywhere (sufficient for float).
	 *
	 * A way to get GaussIntegral with arbitrary Precision is defined by the
	 * incomplete Gamma Function:	Sign (x)*(1+GammP (Sqr (x)/2,Halb))/2	 */
	final static public float pGaussCum(final float x) {
		final boolean neg = x < 0;
		final double z = (neg ? -x/IMeasurAble.SQRT2 : x/IMeasurAble.SQRT2);  	/*Error-Funktion = 2*Gamma-1 mit einer Genauigkeit < 1.2E-7*/
		final double f = 2/(2 + z);
		final float ret =(float)(
					f*Math.exp(-z*z +
					   COEFF_ERFC[0]+
					f*(COEFF_ERFC[1]+
					f*(COEFF_ERFC[2]+
					f*(COEFF_ERFC[3]+
					f*(COEFF_ERFC[4]+
					f*(COEFF_ERFC[5]+
					f*(COEFF_ERFC[6]+
					f*(COEFF_ERFC[7]+
					f*(COEFF_ERFC[8]+
					f* COEFF_ERFC[9])))))))))*0.5);
		if (neg) {
			return ret; } 
		return 1-ret; }

	/**Values of the normed Gauss Function and it's Integral: {x, Gauss(x), GaussCum(x)}	 */
	final static public float[][]
		ValuesGauss = {
						{ 2.0f, 0.0539910f, 0.9772499f},
						{ 1.8f, 0.0789502f, 0.9640697f},
						{ 1.6f, 0.1109208f, 0.9452007f},
						{ 1.5f, 0.1295176f, 0.9331928f},
						{ 1.4f, 0.1497275f, 0.9192433f},
						{ 1.2f, 0.1941860f, 0.8849303f},
						{ 1.0f, 0.2419707f, 0.8413447f},
						{ 0.8f, 0.2896915f, 0.7881446f},
						{ 0.6f, 0.3332246f, 0.7257469f},
						{ 0.5f, 0.3520653f, 0.6914625f},
						{ 0.4f, 0.3682701f, 0.6554217f},
						{ 0.2f, 0.3910427f, 0.5792597f},
						{ 0.0f, 0.3989423f, 0.5000000f},
						{-0.2f, 0.3910427f, 0.4207403f},
						{-0.4f, 0.3682701f, 0.3445783f},
						{-0.5f, 0.3520653f, 0.3085375f},
						{-0.6f, 0.3332246f, 0.2742531f},
						{-0.8f, 0.2896915f, 0.2118554f},
						{-1.5f, 0.1295176f, 0.0668072f},
						{-2.0f, 0.0539910f, 0.0227501f}
					};
	
	/////////////////////////////////////////////////////////////////////////////////////
	// static Testing & Main Functions
	/////////////////////////////////////////////////////////////////////////////////////

	/**Tests the Error Gamma Function	 */
	public static void testGaussIntegral() throws Exception {
		L.n("Testing Gauss Function():");
		L.n("x").l("	Gauss   (x)").l("	GaussInt(x)").l("Gauss   (x)").l("GaussInt(x)");
		for(int i = ValuesGauss.length; --i >= 0;) {
			final float[] xyPair = ValuesGauss[i];
			final float pGauss     = (float) pGauss    (xyPair[0]);
			final float pGaussCum1 =         pGaussCum (xyPair[0]);
			final float pGaussCum2 = (float) pGaussCum2(xyPair[0]);
			final float pGaussCum3 = (float) pGaussCum((double)xyPair[0]);  
			L.n().l(xyPair[0]).l(xyPair[1]).l(xyPair[2]).l(pGauss).l(pGaussCum1).l(pGaussCum2).l(pGaussCum3);
			Assert.EQUALS(xyPair[1], pGauss); 
			Assert.EQUALS(xyPair[2], pGaussCum1); 
			Assert.EQUALS(xyPair[2], pGaussCum2); 
			Assert.EQUALS(xyPair[2], pGaussCum3); 
		}
		Gauss gauss = new Gauss(); 
		//gauss.Derivative();
		L.readString();	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testGaussIntegral();
	}

}
