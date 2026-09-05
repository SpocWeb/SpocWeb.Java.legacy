package streamIO.integer.random;

import streamIO.IReSetAble;
import streamIO.integer.AStreamIn_Bound;
import streamIO.object.IStreamIn;

/** Abstract Random Number Generator using an Integer (long) Generator
  * and emulating various Generators of other simple Types.
  * Optimizations are very important, since most Algorithms with random Numbers
  * need many Data, for their Accuracy typically is of O(SqRt(n))
  *
  * The Optimization here supports Generation of derived integer Numbers
  * without using float Point Arithmetics (Overflow possible though!)
  * and float Point Generators working without Norming twice,
  * first to the Range [0..1) and then to [Min..Max)
  * Expects the Subclasses to set MaxValue,
  * because it is being used for norming the Results,
  * saving a call to getMaxValue() for Performance Reasons. 
  * 
  * TODO: Check whether this Class can be substituted by ARandomInt!
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:50:07Z
  * digest: 8c5a7bce4c41ada8eff936cbc7eb3e912295014cea9098e4f3fe3809e0d39b22
  * stale: false
  * tags: [code/random_number_generation, code/quasi_random_sequence]
  * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class ARandomLong
extends AStreamIn_Bound {
	
	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return maxValue; }
	//abstract public long availAble(); //{ return maxValue; }
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { return currItem.Value = _seed; }
	//abstract public long reSet(final long _seed); 
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	//abstract public long getPosition(); // { return currItem.Value; } 
	public long getPosition() { return currItem.Value; } 
	// not applicable to all Generators!
	
	/** Resets the internal random value back to the previously marked seed.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { reSet(mark); return this; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Modulus to keep the Values in Range	 */
	protected final long maxValue;
	
	/** Returns zero, the lower bound of every generator in this hierarchy.
	 * @return the minimum Value	 */
	public long getMinValue() { return 0; }

	/** Returns this generator's modulus.
	 * @return the maximum Value	 */
	public long getMaxValue() { return maxValue; }
	
	/** Initializing Constructor
	  * enforces Initialization of MaxValue */
	public ARandomLong(final long MaxValue_) { this.maxValue = MaxValue_; }

	/////////////////////////////////////////////////////////////////////////////////////
	
	/** randomizes this Stream by the current Time 	 */
	public ARandomLong randomize() { 
		reSet(System.currentTimeMillis()); 
		return this; }
	
	/** sets this Stream to the given Value 	 
	 * TODO: integrate this with the reset(int value) Method 
	 */
	//public abstract long reSet(final long value_);

	/**Random double Precision Number	 */
	public double nextDouble() { return ((double)nextLong())/maxValue; }

	/**Random single Precision Number	 */
	public float nextFloat() { return ((float)nextLong())/maxValue; }

	//////////////////////////////
	//	Scaled random Numbers	//
	//////////////////////////////

	//Optimization: only Integer Arithmetics

	/** Random Integer Number from 0 to MaxInt-1	 */
	public int nextInt(final int MaxInt) {
//		return (int)((nextLong()* MaxInt)/MaxValue);}
		return (int) (nextLong()% MaxInt); } //may not exhaust the Space up to MaxInt!
//		return       (nextInt ()% MaxInt); }

	/** Random Long Number from 0 to MaxLong-1	 */
	public long nextLong(final long MaxLong) {
//		return (nextLong()* MaxLong)/MaxValue;}
		return (nextLong()% MaxLong);} //may not exhaust the Space up to MaxLong!

	//no Optimization, only with a cached MaxValue!

	/**Random single Precision Number from 0 to MaxFloat	 */
	public float nextFloat(final float MaxFloat) {
		return (MaxFloat*nextLong())/maxValue; }
//		return (MaxFloat*nextInt ())/MaxValue; }

	/**Random double Precision Number from 0 to MaxFloat	 */
	public double nextDouble(final double MaxDouble) {
		return (MaxDouble*nextLong())/maxValue; }

	/** Reports this stream's elements as pseudo-randomly ordered.
	  * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().
	  *
	  * This Implementation should normally go into a Subclass,
	  * but since Ordering is quite special, it is defaulted here!
	  */
	public byte getOrder() { return IStreamIn.ORDER_RANDOM_PSEUDO; }

	/** Returns the largest mark size, since any internal random value can be restored.
	 * @see streamIO.integer.AStreamIn_Bound#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }

}
