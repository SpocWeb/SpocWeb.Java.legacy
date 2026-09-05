package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.real.FilterInMul;
import streamIO.real.IStreamIn_Bound_Float;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;
import function.byref.combinatoric.ProbFuncs;

/**Returns random Numbers with Chi^2 distribution of Ny Degrees of Freedom
 * i.e: p(2x) = Chi^2(x)
 * Scaling by 2 is left to the ClientApplication to increase Performance!
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:27:23Z
 * digest: 2f4de13528755c46773f57f30eb0be8f75ce7cd613c31db8d6a5e2aaf69d3f9b
 * stale: false
 * tags: [code/random_number_generator, code/statistical_distribution]
 * concepts: [Chi-Squared Random Generator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class RandomChiSqr
extends ARandomFloat {

	/**Local Reference to the Random Number Generator, 
	 * also acts as a Flag to indicate a large Ny	 */
	protected final RandomGamma gammaRan;

	/**Local Storage for the Parameter Ny	 */
	protected final int ny;

	/**Constructor that takes a Random Number Generator	 */
	public RandomChiSqr(final IStreamIn_Bound_Float ran_, final int ny_) {
		super(ran_);
		this.ny = ny_;
		if (ny > 12) {
			gammaRan = new RandomGamma(ran_, ny*IMeasurAble.HALF);
		} else {
			gammaRan = null; 
		}
	}

	/**Constructor that takes a Random Number Generator	 */
	public RandomChiSqr(IStreamIn_Bound_Int ran_, int ny_) {
		super(new FilterInMul(ran_, 1));
		this.ny = ny_;
		if (ny > 12) {
			gammaRan = new RandomGamma(ran_, ny*IMeasurAble.HALF);
		} else {
			gammaRan = null; 
		}
	}

	///////////////////////////////////////////////////////////////////////////
	
	/** Resets the wrapped random stream, failing if it cannot be reset.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		if (     ran.reSet() == null) return null;
		return this;
	}
	
	/**Random double Precision Number distributed like ChiSqr/2.
	 * For small Ny directly accumulate the Probabilities,
	 * for large Ny use the Gamma Distribution as Approximation.	 */
	public double nextDoubleInternal() {
		if (gammaRan != null) { 
			return 2*gammaRan.Gamma_Reject(); } 	//2 * die Gamma-Verteilung mit Ny/2 Freiheitsgraden
		/*=Sum(z^2) mit normalverteiltem z und eingesetzt in die Box-Muller-Transformation*/
		float Summe = ran.nextFloat();	//Multiplying the Probabilities saves taking the Logarithm!
		int Z1 = ny >> 1; while(--Z1 > 0) Summe *= ran.nextFloat();
		Summe  = (float) Math.log(Summe); if (ByRefInt.IS_ODD(ny))
		Summe += Math.log(ran.nextFloat())*ByRefDouble.SQR(Math.cos(IMeasurAble.TWO_PI*ran.nextFloat()));
		return -Summe; }
	
	/** Returns 0, the lower bound of the Chi-squared distribution.
	 * @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return 0; }

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Tests the ChiSqr Random Number Generator	 */
	static final void testIt(IStreamIn_Bound_Int ran, int numPoints, double scale, double shift, int NPTS, int numBins) {
		final float[] compare = new float[numBins]; //Binning doesn't work well!
		for (int i = numBins; --i >= 0;) { //Chi� has a very steep Start at 0! 
			compare[i] = (float) (ProbFuncs.pChiSqr((i+.5-shift)*scale*2, numPoints)*scale*2); }
		TestRandom.TEST_RANDOM(new RandomChiSqr(ran, numPoints), compare, scale, numBins, shift, NPTS, "ChiSqr(" + numPoints + ")");
	}

}
