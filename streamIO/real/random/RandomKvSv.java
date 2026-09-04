package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.integer.random.IStreamIn_Bound_Int;
import function.byref.combinatoric.ProbFuncs;

/**Returns random negative Numbers distributed in a Kolmogorov-Smirnov fashion,
 * i.e. p(x) = pKvSv(x) = 
 *
 * Processing Time rises exponentially with Exp1
 */
public class RandomKvSv
	extends ARandomFloat {
	
	private static final int Exp1 = 5;
	private static final int Exp2 = Exp1 << 1;
	private static final int BXP2 = 1 << Exp2;
	private static final int BXP1 = 1 << Exp1;
	private static final float BXP_1 = 1f/BXP1;
	
	protected static final int[] arr = new int[BXP1];
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** Type-safe Reference to the Generator	 */
	final IStreamIn_Bound_Int rnd;
	
	/**Constructor that takes a Random Number Generator	 */
	public RandomKvSv(final IStreamIn_Bound_Int _ran){ super(_ran); rnd = _ran; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		if (ran.reSet() == null) 
			return null;
		return this; 
	}
	
	/**Generates a Random double Precision Number with Kolmogorov- Smirnov Distribution	 */
	public int nextInt() {
		int max = 0;
		for (int i = BXP1; --i >= 0;)  arr[i] = 0; //Clear 
		for (int i = BXP2; --i >= 0;)++arr[rnd.nextInt(BXP1)]; //fill uniformly 
		for (int i = 0; ++i < BXP1 ;)  arr[i] += arr[i-1]; //aggregate 
		for (int i =-1; ++i < BXP1 ;) { //Find Index of max . Deviation. 
			final int j = Math.abs(arr[i]-((i+1) << Exp1)); 
			if (max < j) 
				max = j; 
		}
		return max; 
	}
	
	/**Generates a Random double Precision Number with Kolmogorov- Smirnov Distribution	 */
	public double nextDoubleInternal() { return nextInt()*BXP_1; }
	
	/**Generates a Random double Precision Number with Kolmogorov- Smirnov Distribution	 */
	public long nextLong() { return nextInt(); }
	
	/** @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return 0; }
	
	////////////////////////////////////////////////////////////////////////////////////
	
	/**Tests the Kolmogorov-Smirnov Random Number Generator	 */
	static final void testIt
	(final IStreamIn_Bound_Int ran
	, double scale, double shift, int NPTS, int numBins) {
		final float[] compare = new float[numBins];
		for (int i = numBins; --i >= 0;) { //TODO: KvSv Distribution is not quite correct!!!
			compare[i] = (float) (ProbFuncs.pKvSv((i-shift)*scale)*scale); 
		} 
		TestRandom.TEST_RANDOM(new RandomKvSv(ran), compare, scale, numBins, shift, NPTS, "KvSv()");
	}
	
}
