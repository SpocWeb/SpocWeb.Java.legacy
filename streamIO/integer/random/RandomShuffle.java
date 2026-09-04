package streamIO.integer.random;



/**Random Number Generator;
 * using a primary Generator for the actual Values.
 * Shuffling the Result Sequence removes serial Correlations of lower Order.
 * The Values of one Generator are shuffled by themselves.
 * This corresponds to ran1() from the Numerical Recipes 2nd Ed.
 * 
 * In Fact this is a stateful Filter on the incoming Stream of random Numbers. 	
 */
public class RandomShuffle
extends ARandomLong {

	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomShuffle RANDOM = new RandomShuffle(); 
	
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

	/**Local Reference to the primary Random Number Generator	 */
	protected final IStreamIn_Bound_Int ran;

	/**Array to store the Values of the primary Random Number Generator	 */
	protected final long[] field;

	/**Current Index of the Array	 */
	protected int index;

	/**Constructor defaulting the Random Number Generator and the Length of the Cache 	 */
	public RandomShuffle() { this(new RandomMix(), 32); }

	/**Constructor taking another Random Number Generator and the Length of the Cache	 */
	public RandomShuffle(final IStreamIn_Bound_Int generator, final int length) {
		super((long) generator.getMaxValue());
		this.ran = generator;
		field = new long[length];	//Initialize the Array with random Numbers
		reSet(0); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Changed Semantics! Always returns the Period of the random Numbers. 
	 * A Generator should not be used to generate more than about 5% of it's Period! 
	 * Due to shuffling the Period is not hit exactly, 
	 * but since the Sequence is only randomized within the Distance of the Vector Size, 
	 * the original Period is still a good Limit for the Number of Numbers to be used. 
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return ran.availAble(); }// maxValue; }
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value. 
	 * ReSetting to the same Value gives the same Sequence, 
	 * but the Value cannot be derived from the current Position. 
	 * Cannot reSet the whole Array and it's Index based on a single Seed! 
	 * Therefore Resetting to a certain Position is not supported. 
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { 
		final long ret = ran.reSet(_seed); 
		index = 0; 
		for (int i = field.length; --i >= 0; ) 
			field [i] = ran.nextLong(); //{Initialisierung des Feldes}
		index = ran.nextInt(field.length);
		return ret; 
	}
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet(). 
	 * Since the State of this Generator consists of several Values, 
	 * it cannot be restored, so this Value is essentially useless. 
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return ran.getPosition(); } // currItem.Value; } 
	
	/**Returns a random Long Number	 */
	protected long nextLongInternal() {
		final long ret = field[index];					//{Ausgeben des Wertes}
		field[index]= ran.nextLong();		//{Aktualisieren der Tabelle}
//		index = (int)(ret*Field.length/MaxValue);//{Aktualisieren des Index}
		index = (int) ret%field.length;			//{Aktualisieren des Index}
		return ret; }

}
