package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.real.IStreamIn_Bound_Float;
import function.IMeasurAble;
import function.byref.combinatoric.ProbFuncs;

/**Returns random Numbers distributed like the FisherZ Function
 * i.e. p(x) = FisherZ(x, Ny1, Ny2)
 */
public class RandomFisherZ
extends ARandomFloat {
	
	/**Constructor that takes a Random Number Generator	 */
	public RandomFisherZ(final IStreamIn_Bound_Float ran, final int Ny1, final int Ny2) {
		super(new RandomFisherF(ran, Ny1, Ny2));}
	
	/**Constructor that takes a Random Number Generator	 */
	public RandomFisherZ(final IStreamIn_Bound_Int ran, final int Ny1, final int Ny2) {
		super(new RandomFisherF(ran, Ny1, Ny2));}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		if (ran.reSet() == null) return null;
		return this; 
	}
	
	/**Random double Precision Number distributed like ChiSqr/2.
	 * For small Ny directly accumulate the Probabilities,
	 * for large Ny use the Gamma Distribution as Approximation.	 */
	public double nextDoubleInternal() {
		return IMeasurAble.HALF * Math.log(ran.nextDouble());}
	
	/** @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return Double.NEGATIVE_INFINITY; }

	/////////////////////////////////////////////////////////////////////////////////////

	/**Tests the FisherZ Random Number Generator	 */
	static final void testIt(final IStreamIn_Bound_Int ran, final int N1, final int N2, final double scale, final double shift, final int NPTS, final int numBins) {
		final float[] compare = new float[numBins];
		for (int i = numBins; --i >= 0;) { 
			compare[i] = (float) (ProbFuncs.pFisherZ((i+.5-shift)*scale, N1, N2)*scale); }
		TestRandom.TEST_RANDOM(new RandomFisherZ(ran, N1, N2), compare, scale, numBins, shift, NPTS, "FisherZ(" + N1 + "," + N2 + ")");
	}

}
