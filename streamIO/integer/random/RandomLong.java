package streamIO.integer.random;


/**Generic Random Number Generator using the Linear Congruential Algorithm
 * with Variables of Type Long as compared to
 * @see RandomAffine which uses int.
 * The Default Parameters are taken from the Table in RandomInt (last Row).
 * Speed: moderate(3)
 * <!-- docstate
 * tags: [code/random_number_generation, code/quasi_random_sequence]
 * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class RandomLong
extends RandomLinear {
	
	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomLong RANDOM = new RandomLong(); 
	
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
	
	private static final int INCREMENT = 150889;
	private static final int FACTOR = 4096;
	private static final int MODULE = 714025;
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Translation Constant	 */
	protected final long increment;
	
	/**Empty Constructor, defaults all Values.	 */
	public RandomLong() { this(DEFAULT_SEED); }
	//The Factor 4096 corresponds to a simple Shift by 12 Bits!!!
	
	/**Constructor that takes a Seed Value.	 */
	public RandomLong(final long seed_){ this(seed_, FACTOR, INCREMENT, MODULE); }
	
	/**Constructor that takes all Values.
	 * It is critical to choose good Values for good random Numbers	 */
	public RandomLong(final long seed_, final long factor_, final long increment_, final long module_) {
		super(seed_, factor_, module_); 
		this.increment = increment_; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/**New Semantic: instead of returning to the indicated Position, 
	 * this Method sets the Seed Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public void reset(final int _seed) { //throws IOException {
		currItem.Value = _seed; 
	}

	/**Random Long Number 	 */
	protected long nextLongInternal() {
//		Seed *= Factor;
//		Seed += Increment;
//		Seed %= Modulus;
		return currItem.Value = (currItem.Value*factor+increment)%maxValue;
	}

}
