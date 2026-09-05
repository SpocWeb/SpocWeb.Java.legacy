package streamIO.integer.random;

import streamIO.integer.IStreamIn_Int;

/**Fast Random Number Generator using an Affine Congruential Algorithm
 * with good choices of the Parameters, so the first Multiplication
 * can be replaced by a shifting Operation.	 
 * Speed: moderate(3) due to 'long' Arithmetics
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:55:56Z
 * digest: 76eb3ad7312a25c82e42e07c915cd757fd731fdcf9eafc43dbebffbdd7fbacef
 * stale: false
 * tags: [code/random_number_generation, code/quasi_random_sequence]
 * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class RandomFast
extends ARandomLong {
	
	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomFast RANDOM = new RandomFast(); 
	
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
	
	/**
	 * Public streamIO of Random Numbers to save Instantiation
	 * when it is not critical to use an independent streamIO, 
	 * since random Numbers are Quasi-Stateless. 
	 */
	final static public RandomFast STREAM = new RandomFast();
	
	/** Increment for avoiding Dependency from the Size of the previous Value	 */
	protected static final int INCREMENT = 150889;
	
	/** Module of this Generator	 */
	protected static final int MODULE = 714025;
	
	/** Default-Seed of this Generator	 */
	protected static final int DEFAULT_SEED = 0; //123465;
	
	/**Multiplication replaced by faster Shifting Algorithm	 */
	protected static final int FACTOR_BITS = 12;
	//Factor = 1 << FactorBits;//4096;
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor, defaults all Values.	 */
	public RandomFast(){ this(DEFAULT_SEED); }
	
	/** Constructor that takes a Seed Value.	 */
	public RandomFast(final int _seed)	{
		super(MODULE); 	//usable only for 32 Bit signed Integers
		reSet(_seed); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Random Integer Number 	 */
	protected long nextLongInternal() {
/*				Seed <<= FactorBits;	//replaced by:	Seed *= Factor;
				Seed += Increment;
		return	Seed %= MaxValue;
*/		return	currItem.Value = (((currItem.Value << FACTOR_BITS) + INCREMENT) % maxValue);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Test and Main Methods:
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Benchmarks the raw Throughput of this Generator by drawing 200 Million Integers. 	 */
	final static public void main(final String[] arg) {
		final long startTime = System.currentTimeMillis();  
		final IStreamIn_Int ran = new RandomFast(); //RandomMix(); // RandomLong(); // RandomLinear(); //RandomInt(); // RandomBySubt(); // RandomJava(); //RandomQuick(); //RandomFast(); 
		for (long i = 200000000; --i >= 0;) 
			ran.nextInt(); 
		System.out.println("Duration (ms):"+(System.currentTimeMillis()-startTime));
	}

}
