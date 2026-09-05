package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.integer.random.IStreamIn_Bound_Int;
import function.byref.combinatoric.ProbFuncs;

/**Returns random Numbers distributed in a Beta Function Shape
 * i.e. p(x) = BetaI(x, a, b)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:27:14Z
 * digest: 9bc764a5943a6dd62e245c427b417b04574812d7970e8b1a3cc95f864b332ad5
 * stale: false
 * tags: [code/random_number_generator, code/statistical_distribution]
 * concepts: [Beta-Distributed Random Generator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class RandomBeta
extends ARandomFloat {
	
	/**Local Reference to the Gamma Distribution for a	 */
	protected final RandomGamma aGamma;
	
	/**Local Reference to the Gamma Distribution for b	 */
	protected final RandomGamma bGamma;
	
	/**Constructor that takes a Random Number Generator	 */
	/*
	public RandomBeta(final IStreamIn_Bound_Float ran, final double a, final double b) {
		super(ran);
		aGamma = new RandomGamma(ran, a);
		bGamma = new RandomGamma(ran, b);
	}
	*/
	
	/**Constructor that takes a Random Number Generator	 */
	public RandomBeta(final IStreamIn_Bound_Int ran, final double a, final double b) {
		super(ran);
		aGamma = new RandomGamma(ran, a);
		bGamma = new RandomGamma(ran, b);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Resets both underlying Gamma generators, failing if either cannot be reset.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
	    if (aGamma.reSet() == null) return null;
	    if (bGamma.reSet() == null) return null; //give it a Chance to initialize!
		return this;
	}
	
	/**Random double Precision Number distributed like ChiSqr/2.
	 * For small Ny directly accumulate the Probabilities,
	 * for large Ny use the Gamma Distribution as Approximation.	 */
	public double nextDoubleInternal() {
		final double a = aGamma.nextDouble();
		return a / ( a + bGamma.nextDouble()); }
		
	/** Returns 0, the lower bound of the Beta distribution.
	 * @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return 0; }

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Tests the Beta Random Number Generator	 */
	static final void testIt(final IStreamIn_Bound_Int ran, double a, double b, double scale, double shift, int NPTS, int numBins) {
		final float[] compare = new float[numBins];
		for (int i = numBins; --i >= 0;) { 
			compare[i] = (float) (ProbFuncs.pBeta((i+.5-shift)*scale, a, b)*scale); }
		TestRandom.TEST_RANDOM(new RandomBeta(ran, a, b), compare, scale, numBins, shift, NPTS, "Beta(" + a + "," + b +")");
	}
	
}
