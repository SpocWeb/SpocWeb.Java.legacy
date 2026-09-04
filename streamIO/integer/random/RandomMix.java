package streamIO.integer.random;

import streamIO.IReSetAble;


/**Generate a Random Number as the Sum of two independent Linear (not affine) Generators
 * with different (relatively prime) Periods.
 * Excellent Defaults generate Random Numbers with a Period of 2e18 = 2^55
 * sufficient even for Streams taking about 60 years 
 * generating 1 Billion Numbers per Second (1 GHz)
 * To remove low Order Correlations, the Result of this Generator
 * should be shuffled using RandomShuffle. 
 * Speed: slow(6)
 */
public class RandomMix
extends ARandomLong {
	
	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomMix RANDOM = new RandomMix(); 
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public double NEXT_DOUBLE() { return RANDOM.nextDouble(); }

	/**Random double Precision Number from the static Random Number Generator	 */
	final static public double NEXT_DOUBLE(final double MaxDouble) { return RANDOM.nextDouble(MaxDouble); }

	/**Random double Precision Number from the static Random Number Generator	 */
	final static public float NEXT_FLOAT() { return RANDOM.nextFloat(); }

	/**Random double Precision Number from the static Random Number Generator	 */
	final static public float NEXT_FLOAT(final float MaxFloat) { return RANDOM.nextFloat(MaxFloat); }

	/** Random integer Number from the static Random Number Generator	 */
	final static public int NEXT_INT() { return RANDOM.nextInt(); }

	/** Random integer Number from the static Random Number Generator	 */
	final static public int NEXT_INT(final int MaxInt) { return RANDOM.nextInt(MaxInt); }

	/** Random long Number from the static Random Number Generator	 */
	final static public long NEXT_LONG() { return RANDOM.nextLong(); }

	/** Random long Number from the static Random Number Generator	 */
	final static public long NEXT_LONG(final long MaxLong) { return RANDOM.nextLong(MaxLong); }

	/////////////////////////////////////////////////////////////////////////////////////

	private static final int MAX_VALUE_1 = 2147483563;
	private static final int MAX_VALUE_2 = 2147483399;
	private static final int FACTOR_1 = 40014;
	private static final int FACTOR_2 = 40692;
	/**First Random Generator	 */
	protected IStreamIn_Bound_Int ran1;
	
	/**Second Random Generator	 */
	protected IStreamIn_Bound_Int ran2;
	
	/**Empty Constructor	 */
	public RandomMix() { this (0); }	//arbitrary number
	
	/**Constructor that takes the Seed	 */
	public RandomMix(final long seed) { //Seed, Factor, no Increment, Modulus for a linear Generator
		this (	new RandomLinear(seed, FACTOR_1, MAX_VALUE_1),	//Period: 2147483563-1 = 2* 3* 7* 631*81031
				new RandomLinear(seed, FACTOR_2, MAX_VALUE_2));	//Period: 2147483399-1 = 2*19*31*1019* 1789
	}	//The Periods have only 2 as ggT, so the Period is the Product/2, which is 2,3*10^18
	
	/**Constructor taking two already initialized Random Generators
	 * The actual Values are taken from the first one,
	 * the second one only shuffles them. 	 */
	public RandomMix(final IStreamIn_Bound_Int ran1_, final IStreamIn_Bound_Int ran2_) {
		super(ran1_.getMaxValue());	//with the Modulus of either Generators.
		this.ran1 = ran1_;
		this.ran2 = ran2_;
	}

	///////////////////////////////////////////////////////////////////////////
	
	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return ((long)MAX_VALUE_1-1)/2*(MAX_VALUE_2-1); }
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()
	 * Cannot be used, since the full State consists of more than one Scalar!   
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return currItem.Value; } 
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) {
		ran1.reSet(_seed); 
		ran2.reSet(_seed); 
		return _seed; 
	}
	
	/**New Semantic: instead of returning to the indicated Position, 
	 * this Method sets the Seed Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		//super.reSet(); 
		ran1.reSet(); 
		ran2.reSet(); 
		return this; 
	}
	
	/** @see streamIO.integer.IStreamIn_Int#nextLong() 	*/
	protected long nextLongInternal()	{
		long diff;	//Take the Difference...
		if ((diff = ran1.nextLong() - ran2.nextLong()) < 0)
			 diff += maxValue;	//...with the Modulus of either of the Generators.
		return diff; }

	/** @see streamIO.integer.random.ARandomLong#setSeed(long)	 */
	public void reset(final long value_) {
		throw new RuntimeException("not implemented yet!");
		//this.ran1.setValue(value_);	
	}

}
