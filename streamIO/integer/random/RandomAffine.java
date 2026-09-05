package streamIO.integer.random;

import streamIO.IReSetAble;



/**Generic Random Number Generator 
 * using the Affine Congruential Algorithm
 * with Variables of Type Integer
 * Generates a uniform integer Distribution between 0 and Modulus.
 * The Default Parameters are derived from the Numerical Recipes.
 * Using integer Types exclusively! 
 * Speed: very fast(1) due to Integer Arithmetics
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:51:54Z
 * digest: 0b085df0c025fb8482f40e9317fd9cf6dcaa4761c545353e893b2bdec8bcd4a6
 * stale: false
 * tags: [code/random_number_generation, code/quasi_random_sequence]
 * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class RandomAffine
extends ARandomInt {

	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomAffine RANDOM = new RandomAffine(); 
	
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

	private static final int INCREMENT = 54773;
	private static final int FACTOR = 7141;
	private static final int MODULE = 259200;
	
	/** Default-Seed; randomly chosen, but must be != 0	 */
	private static final int DEFAULT_SEED = 123465;
	
	/**Sets of Constants for linear congruential Random Generators
	 * that passed Knuth's "Spectral Test" for Dimensions up to 6
	 * (Modulus, Factor, Increment)
	 * The Increment is chosen close to Modulus*(0.5-SqRt(3)/6)
	 * although any Value relatively prime to the Modulus would do.
	 * Modulus * Factor determines when a Calculation would overflow
	 * The required Number of Calculation Bits to avoid Overflow are noted as Comments.
	 */
	static final int[][]
		RANDOM_GENERATORS = {
													//19 Bit
						 {  6075,  106,   1283},
													//20 Bit
						 {  7875,  211,   1663},
													//21 Bit
						 {  7875,  421,   1663},
													//22 Bit
						 {  6075, 1366,   1283},
						 {  6655,  936,   1399},
						 { 11979,  430,   2531},
													//23 Bit
						 { 14406,  967,   3041},
						 { 29282,  419,   6173},
						 { 53125,  171,  11213},
													//24 Bit
						 { 12960, 1741,   3731},
						 { 14000, 1541,   2957},
						 { 21870, 1291,   4621},
						 { 31104,  625,   6571},
						 {139968,  205,  29573},
													//25 Bit
						 { 29282, 1255,   6173},
						 { 81000,  421,  17117},
						 {134456,  281,  28411},
													//26 Bit
						 { 86436, 1093,  18257},
						 {121500, 1021,  25673},
						 {MODULE,  421,  INCREMENT},
													//27 Bit
						 {117128, 1277,  24749},
						 {121500, 2041,  25673},
						 {312500,  741,  66037},
													//28 Bit
						 {145800, 3661,  30809},
						 {175000, 2661,  36979},
						 {233280, 1861,  49297},
						 {244944, 1597,  51749},
													//29 Bit
						 {139968, 3877,  29573},
						 {214326, 3613,  45287},
						 {714025, 1366, 150889},	//3*larger Period than below, but small Factor
													//30 Bit
						 {134456, 8121,  28411},
						 {MODULE, FACTOR,  INCREMENT},	//used here in RandomInt as Default
													//31 Bit
						 {233280, 9301,  49297},
						 {RandomFast.MODULE, 4096, RandomFast.INCREMENT}, //used in RandomFast, replacing Multiplicaton by a Shift
													//32 Bit
						};
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Stretching Factor	 */
	protected final int factor;
	
	/**Translation Constant	 */
	protected final int increment;
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor, defaults all Values.	 */
	public RandomAffine() { this(DEFAULT_SEED); }
	
	/**Constructor that takes a Seed Value.	 */
	public RandomAffine(final int _seed) { this(_seed, FACTOR, INCREMENT, MODULE); }
	
	/**Constructor that takes all Values.
	 * It is critical to choose good Values for good random Numbers	 */
	public RandomAffine(final int _seed, final int _factor, final int _increment, final int _module) {
		super(_module); 
		this.factor = _factor;
		this.increment = _increment;
		this.maxValue = _module;
		reSet(_seed);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Resets this generator back to the default seed.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { reSet(DEFAULT_SEED); return this; }

	/**Random Long Number 	 */
	protected long nextLongInternal() {
//		Seed *= Factor;
//		Seed += Increment;
//		Seed %= Modulus;
		return (((int)currItem.Value)*factor+increment)% (int) maxValue; }
	
}
