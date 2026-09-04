package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.real.IStreamIn_Bound_Float;
import function.IMeasurAble;
import function.byref.combinatoric.ProbFuncs;

/** Returns random Numbers distributed like the Student Function
  * i.e. p(x) = Student(x, Ny1, Ny2)
  * @see RandomStudent
  */
public class RandomStudent
extends ARandomFloat { //only for the Method 'nextFloat()'

	/** Local Storage of the Degrees of Freedom for the first Distribution	 */
	protected final float ny;

	/** Local Reference to the ChiSqr Distribution for Ny	 */
	protected final RandomChiSqr ChiSqr;

	/** Constructor that takes a Random Number Generator	 */
	public RandomStudent(final IStreamIn_Bound_Int ran, final int ny_) {
		super(new RandomGauss (ran));
		ChiSqr = new RandomChiSqr(ran, ny_);
		this.ny = ny_ * IMeasurAble.HALF; }
	
	/** Constructor that takes a Random Number Generator	 */
	public RandomStudent(final IStreamIn_Bound_Float ran, final int ny_) {
		super(new RandomGauss (ran));
		ChiSqr = new RandomChiSqr(ran, ny_);
		this.ny = ny_ * IMeasurAble.HALF; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		if (ChiSqr.reSet() == null) return null;
		if (   ran.reSet() == null) return null; //give it a Chance to initialize
		return this; 
	}
	
	/** Random double Precision Number distributed like ChiSqr/2.
	  * For small Ny directly accumulate the Probabilities,
	  * for large Ny use the Gamma Distribution as Approximation.	 */
	public double nextDoubleInternal() {
		return ran.nextDouble()*Math.sqrt(ny/ChiSqr.nextDouble()); }
	
	/** @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return Double.NEGATIVE_INFINITY; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Tests the Student Random Number Generator	 */
	static final void testIt(IStreamIn_Bound_Int ran, int N, double scale, double shift, int NPTS, int numBins) {
		final float[] compare = new float[numBins];
		for (int i = numBins; --i >= 0;) { 
			compare[i] = (float) (ProbFuncs.pStudent((i-shift)*scale, N)*scale); }
		TestRandom.TEST_RANDOM(new RandomStudent(ran, N), compare, scale, numBins, shift+.5, NPTS, "Student(" + N + ")");
	}

}
