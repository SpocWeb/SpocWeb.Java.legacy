package streamIO.integer.random;

import streamIO.IReSetAble;

/**Returns random Bits distributed in a Uniform fashion
 * i.e. p(1) = p(0) = 0.5
 * This is a faster Implementation than RandomBit, 
 * but less reliable and with a Period of ... 
 * @see streamIO.integer.random.RandomBit is a faster Implementation than this one
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:52:14Z
 * digest: 52de0ca4044fb425f4543c6f0744be4ce930e897d6abbf047ebfbeb33816a7c7
 * stale: false
 * tags: [code/random_number_generation, code/quasi_random_sequence]
 * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class RandomBit2
extends ARandomInt {
	
	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomBit2 RANDOM = new RandomBit2(); 
	
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
	
	/** Local Storage for the Generation of Bits	 
	 * Required, since the returned Value is a Transformation.	 */
	protected int value; // = 12345; //mustn't be 0!
	
	/** Initializing Constructor	*/
	public RandomBit2() { super(1); reSet(); }
	
	// TODO: LOGIC: returns `currItem.Value` (the last single bit, 0 or 1, produced by
	// nextLongInternal()) instead of `this.value` (the actual 18+-bit shift-register
	// state). Unlike the sibling RandomBit.getPosition(), which correctly returns its full
	// state field, this loses all but the last bit, so caching this via mark() and
	// restoring it via reSet() cannot reproduce the generator's sequence.
	/**Changed Semantics! Always returns the full internal random Value
	 * to be cached on mark() and restored on reSet()
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return currItem.Value; }

	/** Resets this generator back to the default seed.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { value = RandomBit.DEFAULT_SEED; return this; }

	/** Sets the internal shift-register state to the given seed.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public void reset(final int _seed) { value = _seed; }
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) {
		final int ret = (int) _seed; 
		reset (ret); 
		return ret; 
	}
	
	/**Random Integer Number 	 */
	protected long nextLongInternal() {
		int newbit;
		newbit = ((value >> 17)
				^ (value >> 4)
				^ (value >> 1)
				^ (value)) & 1;
		value =	  (value << 1) | newbit;
		return newbit; }
	
	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!
	 * The Period is of maximum Length, but since 0 must never occur, one less.    
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return (1 << 18)-1; }
	
}
