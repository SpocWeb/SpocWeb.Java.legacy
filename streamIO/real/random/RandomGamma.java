package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.real.FilterInMul;
import streamIO.real.IStreamIn_Float;
import function.byref.combinatoric.ProbFuncs;

/**Returns random Numbers distributed in a Gamma fashion
 * i.e. p(x) = x^(a-1)*exp(-x)/Gamma(a)
 * 
 * The Range of x is [0,1]
 * It is the Waiting Time for the a-th Event of a Poisson Distribution.
 * For a = 1 it is the Exponential Distribution.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:27:50Z
 * digest: 64a2cd6e0a4aa239651d28bf9889eeb0a0154ac65d0346558dcb8cab2210c5be
 * stale: false
 * tags: [code/random_number_generator, code/statistical_distribution]
 * concepts: [Gamma-Distributed Random Generator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class RandomGamma
extends ARandomFloat {

	/**Local Reference to the Random Lorentz-distributed Number Generator
	 * 
	 * if not null, indicates that the Number is to be found by Iteration
	 * otherwise (for large a) analytically using the Rejection Method.	
	 */
	protected final IStreamIn_Float RanLorentz;

	/**Factor: a-1	 */
	protected double a_1;

	/**Factor: a-1	 */
	protected int a__1;

	/**Scaling Factor	 */
	protected double s;

	/**Constructor that takes a Random Number Generator
	 * and the 	 */
	public RandomGamma(final IStreamIn_Bound_Int _ran, final double a) {
		super(new FilterInMul(_ran, 1)); //needs 0..1 Range 
		//super(new FilterInLin(_ran, 0, 1)); //needs 0..1 Range 
		a_1 = a-1;
		a__1 = (int) a_1;
		if (a < 6) {
			RanLorentz = null; 
		} else {
			RanLorentz = new RandomLorentz(ran); //needs [-1,+1) Range
			s = Math.sqrt(a+a_1);
		}
	}

	/**Constructor that takes a Random Number Generator with Range [0,1)
	 */
	public RandomGamma(final IStreamIn_Float ran, final double a) {
		super(ran); 
		a_1 = a-1; 
		a__1 = (int) a_1;
		if (a < 6) {
			RanLorentz = null; 
		} else {
			RanLorentz = new RandomLorentz(ran);
			s = Math.sqrt(a+a_1);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Resets the wrapped random stream, failing if it cannot be reset.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		if (ran.reSet() == null) return null;
		//if (RanLorentz != null) //not necessary, same Generator!
		return this;
	}
	
	/**Random double Precision Number	 */
	public double nextDoubleInternal() {
		if (RanLorentz != null) 
			return Gamma_Reject(); //Rejection-Methode verwenden
		//Noch kein echtes Maximum => keine Zurueckweisungsmethode, sondern iterierte Exp.-Verteilung
		double x = ran.nextDouble();
		for(int i = a__1; --i >= 0; ) //just add up the Probabilities
			x *= ran.nextDouble();  //between 0 and 1
		return -Math.log(x); //between Infinity and 0
	}
	
	/** Calculation by the Rejection Method 	 */
	protected double Gamma_Reject() {
		double x, u;
		do {
			double y; 
			do {
				y  = RanLorentz.nextDouble();
				x = a_1 + s*y; 
			} while (x <= 0);
			u = ((1+y*y)*Math.exp(a_1*Math.log(x/a_1)-s*y));
		} while (u < ran.nextDouble());	//TODO: this Number needs to be generated only once!
//		Hilf = a*Ln (f/a)-sx*g /*Zurueckweisen mit zweiter Zufallsfunktion*/
//		while ( NOT Negativ (Hilf) OR (Ran <= (1.0+Sqr (g))*Exp (Hilf));
		return x; }
	
	/** Returns 0, the lower bound of the Gamma distribution.
	 * @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return 0; }

	/////////////////////////////////////////////////////////////////////////////////////
	
	static final void testIt
	(final IStreamIn_Bound_Int ran, float a, int NPTS, int numBins) {
		final float[] compare = new float[numBins];
		for (int i = numBins; --i >= 0; ) { 
			compare[i] = (float) ProbFuncs.pGamma(i+.5, a); } 
		TestRandom.TEST_RANDOM(new RandomGamma(ran, a), compare, 1, numBins,  0, NPTS, "Gamma(" + a + ")");			/**Tests the Gamma Random Number Generator	 */
	}

}
