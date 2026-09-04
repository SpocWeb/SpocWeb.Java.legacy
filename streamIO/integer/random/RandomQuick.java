package streamIO.integer.random;

import streamIO.Assert;

/**'Quick and Dirty' Random Number Generator using affine Algorithm with automatic Modulus
 * of 2^32 by Truncation of higher Bits using an unsigned 32 Bit Integer.
 * This is the fastest possible Algorithm; as good as any affine 32 Bit one.
 * There is a very fast way to generate Floats from this, by just masking out the Exponent. 
 * Name in Numerical Recipes: ranqd1
 * Speed: very fast(1)
 */
public class RandomQuick
extends ARandomLong {
	
	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomQuick RANDOM = new RandomQuick(); 
		
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public double NEXT_DOUBLE() { return RANDOM.nextDouble(); }
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public double NEXT_DOUBLE(final double MaxValue) { return RANDOM.nextDouble(MaxValue); }
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public float NEXT_FLOAT() { return RANDOM.nextFloat(); }
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public float NEXT_FLOAT(final float MaxValue) { return RANDOM.nextFloat(MaxValue); }
	
	/** Random integer Number from the static Random Number Generator	 */
	final static public int NEXT_INT() { return RANDOM.nextInt(); }
	
	/** Random integer Number from the static Random Number Generator	 */
	final static public int NEXT_INT(final int MaxValue) { return RANDOM.nextInt(MaxValue); }
	
	/** Random long Number from the static Random Number Generator	 */
	final static public long NEXT_LONG() { return RANDOM.nextLong(); }
	
	/** Random long Number from the static Random Number Generator	 */
	final static public long NEXT_LONG(final long MaxValue) { return RANDOM.nextLong(MaxValue); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Offset, Prime close to (SqRt(5)-2)*2^32	 */
	private static final int INCREMENT = 1013904223;
	
	/**Stretching Factor	 */
	private static final int FACTOR = 1664525;
	
	/** The Module is actually 2^32 = 0x0000000100000000 
	 *  but it is used for Masking instead of taking the Modulus! 
	 */
	private static final long MODULE = 0x00000000FFFFFFFFl;
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor, defaults all Values.	 */
	public RandomQuick(){ this(0); } 
	
	/**Constructor that takes a Seed Value.	 */
	public RandomQuick(final long _seed){ 
		super(MODULE+1); 
		reSet(_seed);
	}//must be positive! 
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Random Long Number 	 */
	protected long nextLongInternal() {
//				Seed *= Factor;
//		return	Seed += Increment;
//				Seed &= MaxValue;
		return	currItem.Value = (currItem.Value*FACTOR + INCREMENT) & MODULE;	//No Automatic Truncation!
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Test Sequence to verify	*/
	private static final long[] RESULTS 
	= { 0x00000000l, 
		0x3C6EF35Fl, 
		0x47502932l, 
		0xD1CCF6E9l, 
		0xAAF95334l, 
		0x6252E503l};
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		final RandomQuick ran = new RandomQuick(RESULTS[0]);
		for (int i = 0; ++i < RESULTS.length; ) {
			final long lng = ran.nextLong();  
			Assert.EQUALS(RESULTS[i], lng);
		} 
	}

}
