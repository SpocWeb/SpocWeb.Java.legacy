package streamIO.integer.random;

import streamIO.IReSetAble;

/**Integer Random deviate by Donald Knuth's subtractive method.
 * Uniform Random Integer Number Generator, Numerical Recipes 2nd Ed. Chapter 7.1
 * Since the Algorithm is completely different, the Results should succeed,
 * where the Random Generators fail.
 *
 * The Types can also be 'short' instead of 'int' without changing the Results,
 * if the Constants are changed accordingly. 
 * Speed: very fast(1)
 */
public class RandomBySubt
extends ARandomInt {

	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomBySubt RANDOM = new RandomBySubt(); 
	
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

	/** According to Knuth this can be any large Number	 */
	static final int MODULE16 = 4000000;
	
	/** According to Knuth this can be any large Number	 */
	static final int MODULE32 = 1000000000;

	/** According to Knuth this can be any large Number	 */
	static final int SEED32 =  161803398;//for 32 Bit Integers
	
	/** According to Knuth this can be any large Number	 */
	static final int SEED16 =  1618033;	//for 16 Bit Integers

	/** 56 is a magic number! (see Knuth et al.) It should not be modified	 */
	private static final int LENGTH = 56;
	
	//frequently used
	private static final int LengthM1 = LENGTH-1;

	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	//Array containing Values for further Calculation
	final int[] ma = new int [LENGTH];	//32 Bit Integers in Java!
	
	int iNext;
	
	int iNextP;

	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		reset(0); return this; 
	}

	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return maxValue; }
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return currItem.Value; } 
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { //throws IOException {
	    final int seed = (int) _seed; 
	    reset (seed); 
	    return seed; 
	}

	/**New Semantic: instead of returning to the indicated Position, 
	 * this Method sets the Seed Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public void reset(final int seed) { //throws IOException {
		//initialize the last Element using the Seed and the MaxValue
		int mk = 1;
		int mj = ma[LengthM1] = Math.abs(SEED32 - Math.abs(seed)) % (int) maxValue;
		for (int i = 1; i < LengthM1; i++) {//The rest of the table is initialized
			final int ii = (21*i) % LengthM1;		//with Values that are not very random
			ma[ii] = mk; mk = mj-mk;
			if (mk < 0) mk += maxValue;	//Keep the Values in Range
			mj = ma[ii];
		}
		for (int k=1;k<=4;k++)	//Randomize the Numbers by warming up the Generator
			for (int i=1;i<=LengthM1;i++) {
				ma[i] -= ma[1+(i+30) % LengthM1];
				if (ma[i] < 0) ma[i] += maxValue;	//Keep the Values in Range
			}
		iNextP= 31;	//Also this Constant is special!
		iNext =  0;	//Indices for the first generated Number
	}

	/**Initializing Constructor	 */
	public RandomBySubt(){ this(0); }

	/**Initializing Constructor	 */
	public RandomBySubt(final int seed) {
		super(MODULE32);	//for 32 Bit Integers
		//super(MODULE16);	//for 16 Bit Integers
		reset(seed); 
	}
	
	/**Generator Method, all others are derived	 */
	protected long nextLongInternal() {
		if (++iNext  == LENGTH) iNext  = 1; //increment iNext and iNextP
		if (++iNextP == LENGTH) iNextP = 1;	//wrapping around 56 to 1
		int mj = ma[iNext] - ma[iNextP];
		if (mj < 0) 
			mj += maxValue; //Keep the Value in Range
		ma[iNext] = mj;
		return mj; }

}
