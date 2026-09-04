package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.real.IStreamIn_Bound_Float;
import function.byref.combinatoric.ProbFuncs;

/**Returns random Numbers distributed like the FisherF Function
 * i.e. p(x) = FisherF(x, Ny1, Ny2)
 */
public class RandomFisherF
extends ARandomFloat {

	/**Local Storage of the Degrees of Freedom for the first Distribution	 */
	protected final int dim1;

	/**Local Storage of the Degrees of Freedom for the first Distribution	 */
	protected final int dim2;

	/**Local Reference to the ChiSqr Distribution for Ny1	 */
	protected final RandomChiSqr chiSqr1;

	/**Local Reference to the ChiSqr Distribution for Ny2	 */
	protected final RandomChiSqr chiSqr2;

	/**Constructor that takes a Random Number Generator	 */
	public RandomFisherF(final IStreamIn_Bound_Float ran, final int ny1_, final int ny2_) {
		super(ran); 
		chiSqr1 = new RandomChiSqr(ran, this.dim1 = ny1_);
		chiSqr2 = new RandomChiSqr(ran, this.dim2 = ny2_);
	}

	/**Constructor that takes a Random Number Generator	 */
	public RandomFisherF(final IStreamIn_Bound_Int ran, final int ny1_, final int ny2_) {
		super(ran); 
		chiSqr1 = new RandomChiSqr(ran, this.dim1 = ny1_);
		chiSqr2 = new RandomChiSqr(ran, this.dim2 = ny2_);
	}

	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		if (chiSqr1.reSet() == null) return null;
		//this.chiSqr2.reset(); //using the same Generator!
		return this; 
	}
	
	/**Random double Precision Number distributed like ChiSqr/2.
	 * For small Ny directly accumulate the Probabilities,
	 * for large Ny use the Gamma Distribution as Approximation.	 */
	public double nextDoubleInternal() {
		return (dim2*chiSqr1.nextDouble())/
			   (dim1*chiSqr2.nextDouble());
	}
	
	/** @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return 0; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Tests the FisherF Random Number Generator	 */
	static final void testIt(IStreamIn_Bound_Int ran, int N1, int N2, double scale, double shift, int NPTS, int numBins) {
		final float[] compare = new float[numBins];
		for (int i = numBins; --i >= 0;) { 
			compare[i] = (float) (ProbFuncs.pFisherF((i+.5-shift)*scale, N1, N2)*scale); }
		TestRandom.TEST_RANDOM(new RandomFisherF(ran, N1, N2), compare, scale, numBins, shift, NPTS, "FisherF(" + N1 + "," + N2 + ")");
	}
	
}
