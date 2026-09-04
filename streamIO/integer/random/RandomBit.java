package streamIO.integer.random;

import streamIO.IReSetAble;


/**Returns random Bits distributed in a Uniform fashion
 * i.e. p(1) = p(0) = 0.5
 * @see streamIO.integer.random.RandomBit2 is a slower Implementation than this one
 */
public class RandomBit
extends ARandomInt {
	
	/** Reference to the static Random Number Generator, shared between Clients	 */ 
	final static public RandomBit RANDOM = new RandomBit(); 
	
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
	
	/**List of primitive Polynomials Modulo 2
	 * one Example per Degree (in fact there are many)
	 */
	final static public int[][] PrimitivePolynom2 = {
		{ 1,0},
		{ 2,1,0},
		{ 3,1,0},
		{ 4,1,0},
		{ 5,2,0},
		{ 6,1,0},
		{ 7,1,0},
		{ 8,4,3,2,0},
		{ 9,4,0},
		{10,3,0},
		{11,2,0},
		{12,6,4,1,0},
		{13,4,3,1,0},
		{14,5,3,1,0},
		{15,1,0},
		{16,5,3,2,0},
		{17,3,0},
		{18,5,2,1,0},
		{19,5,2,1,0},
		{20,3,0},
		{21,2,0},
		{22,1,0},
		{23,5,0},
		{24,4,3,1,0},
		{25,3,0},
		{26,6,2,1,0},
		{27,5,2,1,0},
		{28,3,0},
		{29,2,0},
		{30,6,4,1,0},
		{31,3,0},
		{32,7,5,3,2,1,0},
		{33,6,4,1,0},
		{34,7,6,5,2,1,0},
		{35,2,0},
		{36,6,5,4,2,1,0},
		{37,5,4,3,2,1,0},
		{38,6,5,1,0},
		{39,4,0},
		{40,5,4,3,0},
		{41,3,0},
		{42,5,4,3,2,1,0},
		{43,6,4,3,0},
		{44,6,5,2,0},
		{45,4,3,1,0},
		{46,8,5,3,2,1,0},
		{47,5,0},
		{48,7,5,4,2,1,0},
		{49,6,5,4,0},
		{50,4,3,2,0},
		{51,6,3,1,0},
		{52,3,0},
		{53,6,2,1,0},
		{54,6,5,4,3,2,0},
		{55,6,2,1,0},
		{56,7,4,2,0},
		{57,5,3,2,0},
		{58,6,5,1,0},
		{59,6,5,4,3,1,0},
		{60,1,0},
		{61,5,2,1,0},
		{62,6,5,3,0},
		{63,1,0},
		{64,4,3,1,0},
		{65,4,3,1,0},
		{66,8,6,5,3,2,0},
		{67,5,2,1,0},
		{68,7,5,1,0},
		{69,6,5,2,0},
		{70,5,3,1,0},
		{71,5,3,1,0},
		{72,6,4,3,2,1,0},
		{73,4,3,2,0},
		{74,7,4,3,0},
		{75,6,3,1,0},
		{76,5,4,2,0},
		{77,6,5,2,0},
		{78,7,2,1,0},
		{79,4,3,2,0},
		{80,7,5,3,2,1,0},
		{81,4,0},
		{82,8,7,6,4,1,0},
		{83,7,4,2,0},
		{84,8,7,5,3,1,0},
		{85,8,2,1,0},
		{86,6,5,2,0},
		{87,7,5,1,0},
		{88,8,5,4,3,1,0},
		{89,6,5,3,0},
		{90,5,3,2,0},
		{91,7,6,5,3,2,0},
		{92,6,5,2,0},
		{93,2,0},
		{94,6,5,1,0},
		{95,6,5,4,2,1,0},
		{96,7,6,4,3,2,0},
		{97,6,0},
		{98,7,4,3,2,1,0},
		{99,7,5,4,0},
		{100,8,7,2,0}
	};
	
	final static public int UPPER_MASK = 1 << (31-1); //1 << (18-1);
	final static public int LOWER_MASK = 1 << ( 3-1); //(IB1+IB2+IB5);
	
	/** Local Storage for the Generation of Bits	 */
	final static public int DEFAULT_SEED = 12345; //mustn't be 0!
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Local Storage for the Generation of Bits	 
	 * Required, since the returned Value is a Transformation.	 */
	protected int value; //mustn't be 0!
	
	/** Initializing Constructor	*/
	public RandomBit() { super(1); reSet(); }
	
	/** Random Integer Number 	 */
	public long nextLongInternal() {
		if((value & UPPER_MASK) != 0) {
			value = ((value ^ LOWER_MASK) << 1) | 1; return 1;
		} else {
			value <<= 1; return 0; }
	}
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { reset(DEFAULT_SEED); return this; }
	
	/**New Semantic: instead of returning to the indicated Position, 
	 * this Method sets the Seed Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { return reSet((int) _seed); }
	
	/**New Semantic: instead of returning to the indicated Position, 
	 * this Method sets the Seed Value.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public int reset(final int _seed) { return value = _seed; }

	/**Changed Semantics! Always returns the internal Seed Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return value; }

	/**Changed Semantics! Always returns the Period of the random Numbers.  
	 * Since 0 must never be returned. 
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return UPPER_MASK-1+UPPER_MASK; }
	
}
