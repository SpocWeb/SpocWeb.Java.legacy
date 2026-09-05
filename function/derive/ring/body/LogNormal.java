/*
 * Created on 09.08.2004
 *
 */
package function.derive.ring.body;

import streamIO.Assert;
import function.byref.ByRefDouble;
import function.derive.AFloatDeriveAble;

/**
 * The LogNormal Distribution is derived from a normal Distribution
 * in that it's Argument is a Logarithm of the (non-negative) Input. 
 * Thus it models the Situation that the relative Deviations fluctuate, 
 * instead of the absolute Deviations, which would be modeled by a Gaussian Normal Distribution. 
 * 
 * It is easiest to first consider the integated (cumulative) Probability: 
 * P(x >= 0) = Gauss(ln((x-x0)/m)/s)) with 
 * x0 as Location Parameter (if not 0 based, but offset) 
 * m as the Scale Parameter to normalize the Width of the Distribution 
 * s as the Shape Parameter to describe the Width of the Distribution 
 * 
 * From this the Density Function can be derived: 
 * p(x >= 0) = e^(-ln((x-x0)/m)�/2s�)/(x-x0)s*SqRt(2Pi) ~ 1/(x^(1+ln(x)))
 * 
 * The Density Curve rises smoothly from (0, 0) 
 * to a Maximum at (1, 1) resp. SqRt(e)
 * and drops to (Infinity, 0)
 *
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:31:21Z
 * digest: a60865a0b026d0cd061821920c4eaa28c8d37dd4b612c32fe398aa0cb4bb5f9c
 * stale: false
 * tags: [code/derivable_function_contract, code/mathematical_function]
 * concepts: [Statistical Distributions, Log-Normal Distribution]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class LogNormal 
extends AFloatDeriveAble {

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables 
	/////////////////////////////////////////////////////////////////////////////////////

	/** The Average of the LogNormal Function, 0 by Default 
	 * This is the Location that the Distribution is centered about. 	 */
	private final double average; 

	/** The Scale of the LogNormal Function, 1 by Default	 */
	private final double scale; 

	/** The Variance of the Gauss Function used, 1 by Default	 */
	private final double stdDev; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/** initializing Constructor 
	 * 
	 * @param _average
	 * @param _scale
	 * @param _stdDev
	 */
	public LogNormal(final double _average, final double _scale, final double _stdDev) {
		this.average = _average; 
		this.stdDev = _stdDev; 
		this.scale = _scale; 
	}

	/** initializing Constructor 	 */
	public LogNormal() {
		this(0, 1, 1); 
	}

	///////////////////////////////////////////////////////////////////////////
	/// Interface IFloatFunction
	///////////////////////////////////////////////////////////////////////////
	
	/**Returns the cumulative LogNormal Distribution's Value at x.
	 * @see function.IFloatFunction#Map(double)	 */
	public double Map(double x) {
		x-=average; 
		x/=scale; 
		return Gauss.GAUSS.Map(Math.log(x)/stdDev);
	}

	///////////////////////////////////////////////////////////////////////////
	/// Interface IFloatDeriveAble
	///////////////////////////////////////////////////////////////////////////
	
	/**Returns the LogNormal Density Function's Value at x, this Distribution's Derivative.
	 * @see function.derive.IFloatDeriveAble#getDerivative(double)	 */
	public double getDerivative(double x) {
		x-=average; 
		x/=scale; 
		return Math.exp(ByRefDouble.SQR(Math.log(x)/stdDev)/2)/(x*stdDev*ByRefDouble.SQRT2PI); 
	}

	/**Calculates the cumulative LogNormal Distribution and its Density at x at the same time.
	 * @see function.derive.IFloatDeriveAble#getFuncDerive(double, function.byref.ByRefDouble)	 */
	public double getFuncDerive(double x, final ByRefDouble derivative) {
		x-=average; 
		x/=scale; 
		final double log = Math.log(x)/stdDev; 
		if (derivative != null) {
			derivative.Value = Math.exp(ByRefDouble.SQR(log)/2)/(x*stdDev*ByRefDouble.SQRT2PI); }
		return Gauss.GAUSS.Map(log);
	}

	/**Wraps arg as a {@link ByRefDouble} after mapping it through the cumulative LogNormal Distribution.
	 * @see function.IFunction#Map(java.lang.Object)	 */
	public Object Map(final Object arg) {
		return new ByRefDouble(Map(ByRefDouble.GET_DOUBLE(arg)));
	}

	///////////////////////////////////////////////////////////////////////////

	/**Verifies that {@code ln}'s getFuncDerive() Value and Derivative agree with Map() and getDerivative().	 */
	public static void testIt(final LogNormal ln, final double value) {
		final ByRefDouble ret = new ByRefDouble(); 
		System.out.println(ln.Map(value));
		Assert.EQUALS(ln.Map(value), ln.getFuncDerive(value, ret)); 
		Assert.EQUALS(ln.getDerivative(value), ret.Value); 
	}
	
	///////////////////////////////////////////////////////////////////////////

	/**Runs {@link #testIt(LogNormal, double)} against a fixed LogNormal Instance at several Points.	 */
	public static void testIt() {
		final double average = 5;
		final double scale = 3; 
		final double stdDev = .5; 
		final LogNormal ln = new LogNormal(average, scale, stdDev);
		//testIt(ln, average); //NaN
		testIt(ln, average+.0001);
		testIt(ln, average+scale);
		testIt(ln, 100);
		//testIt(ln, 0); //NaN
		//TODO: test whether the differentiated Function is the Derivative. 
	}
	
	/**The main entry point for the application: runs {@link #testIt()}.
	 * @param args Array of parameters passed to the application via the command line.	 */
	public static void main(final String[] args) {
		testIt(); 
	}
	
}
