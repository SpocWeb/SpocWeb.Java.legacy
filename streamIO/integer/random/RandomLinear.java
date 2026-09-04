package streamIO.integer.random;

import streamIO.Assert;
import streamIO.Log;

/**Random Number Generator using a linear (not the affine) Algorithm: 
 * x[n+1] = (x[n]*a)% m 
 * A Consequence is that x must never become 0! 
 * To avoid integer Overflow, Schrage's Result can be applied: 
 * m = q*a + r with the Factorization of m by a it shows that when q > r and 0 < x < m-1
 * (a*x)%m = a(x%q) - r(x/q) if this is > 0 and +=m otherwise. 
 * Since Java supports 64 Bit long Numbers, it is not necessary to apply Schrage's Algorithm 
 * for Module Multiplication.   
 * 
 * This has the Disadvantage that a small Number will followed by a small Number, 
 * but saves the Addition of the Increment and has the maximum Period of 2^31! 
 * The Parameters are based on ran0 of the Numerical Recipes 2nd Ed. (7.1.3) 
 * Linear Generators (must) never return Zero and the Seed is modified according to this.
 * 
 * RandomQuick is a Generator based on MaxValue = 0x80000000	
 * Speed: medium(3)
 * 
 * known Subclasses: 
 * @see streamIO.integer.random.RandomLong
 */
public class RandomLinear
extends ARandomLong {
	
	private static final Log L = new Log(RandomLinear.class); 
	
	/** The Module used (implicitly): 0x7FFFFFFF == 2^32-1 ==2147483647	
	 * Results in 
	 * Factor    q = 127773
	 * Remainder r =   2836
	 * */
	private static final int MODULE = Integer.MAX_VALUE; 
	
	/** The Default Seed 	*/
	protected static final int DEFAULT_SEED = 0;
	
	private static final int IA = 16807;
	private static final int IM = 2147483647;
	private static final int IQ = 127773;
	private static final int IR = 2836;
	private static final int MASK = 123459876;
	
	/** The possible Factors usable for 2^32 as Module  	 */
	private static final int[] FACTOR = {
			16807, //= 7^5
			48271, 
			69621}; 
	
	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomLinear RANDOM = new RandomLinear(); 
	
	/**Random double Precision Number from the static Random Number Generator	 */
	public static double NEXT_DOUBLE() { return RANDOM.nextDouble(); }

	/**Random double Precision Number from the static Random Number Generator	 */
	public static double NEXT_DOUBLE(final double MaxDouble) { return RANDOM.nextDouble(MaxDouble); }

	/**Random double Precision Number from the static Random Number Generator	 */
	public static float NEXT_FLOAT() { return RANDOM.nextFloat(); }

	/**Random double Precision Number from the static Random Number Generator	 */
	public static float NEXT_FLOAT(final float MaxFloat) { return RANDOM.nextFloat(MaxFloat); }

	/** Random integer Number from the static Random Number Generator	 */
	public static int NEXT_INT() { return RANDOM.nextInt(); }

	/** Random integer Number from the static Random Number Generator	 */
	public static int NEXT_INT(final int MaxInt) { return RANDOM.nextInt(MaxInt); }

	/** Random long Number from the static Random Number Generator	 */
	public static long NEXT_LONG() { return RANDOM.nextLong(); }

	/** Random long Number from the static Random Number Generator	 */
	public static long NEXT_LONG(final long MaxLong) { return RANDOM.nextLong(MaxLong); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reference Implementation of ran0 from Numerical Recipes ran0
	 * 	“Minimal” random number generator of Park and Miller. 
	 * Returns a uniform random deviate between 0.0 and 1.0. 
	 * Set or reset idum to any integer value (except the unlikely value MASK)
	 * to initialize the sequence; 
	 * idum must not be altered between calls for successive deviates in a sequence.
	 */
	public long nextValue() {
		//currItem.Value ^= MASK; //XORing with MASK allows use of zero and other simple bit patterns for idum. 
		long k=(currItem.Value)/IQ;
		currItem.Value=IA*(currItem.Value-k*IQ)-IR*k; //Compute idum=(IAcurrItem.Value) % IM without overflows by Schrage’s method. 
		if (currItem.Value < 0) 
			currItem.Value += IM;
		//ans=AM*(currItem.Value); //Convert idum to a floating result.
		//currItem.Value ^= MASK; //Unmask before return.
		return currItem.Value;
	}
	
	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return maxValue-1; }
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { 
		currItem.Value = MASK ^ _seed; //don't let the Seed become 0!
		return _seed; 
	}
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return MASK ^ currItem.Value; } 
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Stretching Factor	 */
	protected final long factor; //
	
	/** Empty Constructor, defaults all Values. Gives a Period of 2^32-2 = 2*10^9	 */
	public RandomLinear() { this(DEFAULT_SEED); }	 

	/** Constructor that takes a Seed Value.
	  * Prevents that Seed == 0	 */
	public RandomLinear(final long seed_) { this(seed_, FACTOR[0], MODULE); }
	
	/** Constructor that takes all Parameters for the linear Generator.	 */
	public RandomLinear(final long seed_, final long factor_, final long maxValue_) {
		super(maxValue_); 
		this.currItem.Value = seed_ ^ MASK; 
		this.factor = factor_; 
	}

	///////////////////////////////////////////////////////////////////////////
	
	/** Random Long Number 	 */
	protected long nextLongInternal() {
//		Seed *= Factor;
//		Seed %= MaxValue;
//		return Seed = (Seed*Factor)&MaxValue;	//Although MaxValue is 0x7FFFFFFF
		return currItem.Value = (currItem.Value*factor)%maxValue;	//Although MaxValue is 0x7FFFFFFF
		//the Modulus is not the same as just truncating the higher Bits
	}	//by just using Integers (would give negative Numbers and use 0x80000000)
		//or just doing Seed &= 0x7FFFFFFF != Seed %= 0x7FFFFFFF
		//e.g.	13h & 7 = 3 != 5 = 13h % 7 this is only true for Powers of 2:
		//		13h & (8-1) = 3 == 13h % 8
	
	/** Tests all Methods of this Class	 */
	public static void testMarkReSet() {
		L.enter(); 
		final RandomLinear ran = new RandomLinear(); 
		ran.randomize(); //use a random Number... 
		final long[] expected = new long[50]; 
		for(int i = ran.nextInt(50); --i >= 0; )
			ran.nextInt(); //...to randomize it completely
		//final long pos = ran.getPosition();
		ran.mark(); 
		for(int i = expected.length; --i >= 0;)
			expected[i] = ran.nextValue(); 
		ran.reSet(); //pos); //also test the reset() Method
		for(int i = expected.length; --i >= 0;)
			Assert.EQUALS(expected[i], ran.nextInt());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		testMarkReSet();
	}

}
