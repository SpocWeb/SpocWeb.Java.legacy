package streamIO.integer.random;

import math.vector.HunterFloat;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.real.IStreamIn_Float;
import streamIO.real.StreamOutPlotter;
import function.byref.ByRefFloat;

/** Returns random Numbers with the Probability Distribution
  * given in the Constructor.
  * The Probabilities 0 <= p[i] <= 1 must add up to Sum(i, p[i]) <= 1.
  * The accumulated Probabilities with 0 <= P[i] <= P[j] <= 1 with i <= j
  * are given by P[i] = Sum (0 <= j <= i, p[i])
  * with the last one assumed to the Difference .
  * The Range of the Return Values is [0..n] then.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:55:55Z
  * digest: 64800fc6fd973b38b4828e7006b772c9eadb06a85c2dbe7c4ae8cb056fe1d6d8
  * stale: false
  * tags: [code/random_number_generation, code/quasi_random_sequence]
  * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class RandomDiscrete
extends ARandomInt
implements IStreamIn_Bound_Int {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(RandomDiscrete.class, 0);
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Local Reference to the Random Number Generator	 */
	protected IStreamIn_Float ran;
	
	/** List of Probabilities	 */
	protected final float[] p;
	
	/** Flag whether the List of Probabilities is ascending	 */
	protected final boolean ascending;
	
	/**Constructor that takes a Random Number Generator
	 * and the List of cumulated (!) Probabilities for each Index.
	 * The Probabilities must add up to 1.
	 * If the Probabilities are not accumulated,
	 * use the Helper Routine 'accumulateAt()'
	 * The last Probability needn't be given,
	 * since they must all up to 1, so it is p(n) = 1 - Sum(i<n, p(i))	 */
	public RandomDiscrete(final float[] probabilities_) {
		this(RandomQuick.RANDOM, probabilities_);
	}
	
	/**Constructor that takes a Random Number Generator
	 * and the List of cumulated (!) Probabilities for each Index.
	 * If the Probabilities are not ascending or descending, 
	 * they are aggregated and normalized to 1.
	 * The last Probability needn't be given,
	 * since they must all up to 1, so it is p(n) = 1 - Sum(i<n, p(i))	 
	 */
	public RandomDiscrete(final IStreamIn_Float ran, final float[] probabilities_) {
		super (probabilities_.length+1);  //The Retun Value is always LESS than MaxValue
		this.p = probabilities_;
		this.ran = ran;
		//if (p.length <= 0) {
		//	return; }
		final int ascending_ = HunterFloat.GET_ORDER_FULL(p, 0, p.length);
		if(ascending_ <= 0) {
			VectorFloat.SUM_AT(p, 0, p.length); //aggregate the Frequencies
			VectorFloat.MUL_AT(p, 1/p[0]); //Normalize to Probabilities
			this.ascending = false; 
		} else {
			this.ascending = (ascending_ > 0); 
			final float d = (ascending ? p[p.length-1] : p[0]) -1;
			if (Math.abs(d) > ByRefFloat.FloatAccuracy) {
				++maxValue;
				if (d > 0) 
					throw new AbstractMethodError("Probabilities must add up to 1 (or less)"); 
			}
		}
	}
	
	/**Random double Precision Number from 0 to n 
	 * distributed according to the Probabilities given in the Constructor.	 */
	protected long nextLongInternal() {
		final float val = ran.nextFloat(); 
		final int ret = HunterFloat.POSITION_IN_SORTED_ARRAY(p, val, ascending);
		if ((ret < 0) || (ret >= p.length))
			L.n("Index out of Bounds:"+ret);
		return ret; 
	}
	
	/** Resets the underlying Random Number Generator.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { ran.reSet(); return this; }
	
	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return ran.availAble(); }
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { return ran.reSet(_seed); } 
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return ran.getPosition(); } 
	
	/////////////////////////////////////////////////////////////////////////////////////
	// static Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Verifies the empirical Distribution of generated Indices against a Binomial
	 * reference Distribution and prints a Plot comparing both.	 */
	public static void testIt() {
		L.enter().println();
		final int n = 20;
		final int nPkte = 400000;
		double xm;
		float[] abstd = new float[n+1];
		float[] p = new float[n+1];
		float[] P = new float[n];
		L.n("Teste Random_Word (erzeugt beliebige diskrete Verteilungen nach Vorlage)");
		L.n("anhand einer Binomial-Verteilung :");
		xm = 13;
		L.n("Mean of the Binomial Distribution (0 .. 20) : ").l(xm);
//		System.in.read (xm);
		xm /= n; 
		for(byte j = n; --j >= 0; ) {
			p[j] = 1f/n;//
			p[j] = (float) function.byref.combinatoric.ProbFuncs.pBin(n, j, xm); abstd[j] = 0; 
		}
		System.arraycopy(p, 0, P, 0, P.length);
		RandomDiscrete Ran = new RandomDiscrete(new RandomShuffle(), P);
		for (int i = nPkte; --i >= 0; )  
			++abstd[Ran.nextInt()]; 
		L.n("x \t p(x) \t Soll \t Graph:");
		L.n(StreamOutPlotter.PLOT(VectorFloat.MUL_AT(abstd, 1f/nPkte), p, 8, 4));
		final double relAbs = 1/Math.sqrt(nPkte);
		Assert.EQUALS(p, abstd, relAbs, relAbs); 
	}
	
	/** main Method for testing	 */
	public static void main(final String[] args) {
		testIt(); 
	}

}
