package streamIO.integer.random;

import java.util.Random;

/**Random Number Generator encapsulating the standard Java Algorithm
 * Unfortunately I don't know the Implementation,
 * so I cannot be sure of the Algorithm or it's Parameters, like the Modulus  
 * Speed: fast(2)
 */
public class RandomJava
extends ARandomLong {

	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomJava RANDOM = new RandomJava(); 
	
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
	
	/**Random Generator	 */
	protected Random ran;
	
	/**Constructor that takes a Seed Value.		*/
	public RandomJava(final long _seed) {
		super(Long.MAX_VALUE);
		ran = new Random(_seed); }
	
	/**Constructor that takes a Seed Value.		*/
	public RandomJava()	{
		super(Long.MAX_VALUE);
		ran = new Random(); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long seed) { 
		ran.setSeed(seed); return seed; }
	
	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return maxValue; }
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return currItem.Value; } 
	
	/** Random Long Number; any of the 2^64 Numbers! 	 */
	protected long nextLongInternal()	{ return ran.nextLong(); }

	/**Random single Precision Number	 */
	public float nextFloat() { return ran.nextFloat(); }

	/**Random double Precision Number	 */
	public double nextDouble() { return ran.nextDouble(); }
	
}
