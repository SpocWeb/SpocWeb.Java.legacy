package streamIO.copy.group.ring.metric.body;

import java.io.IOException;
import java.io.StreamTokenizer;

/**Concrete final optimized Wrapper- Class for 'double' Values.
 * Float Point Types define a Metric Body.
 *
 * Double-Properties:
 * Bits:	 64 = 8*8Byte
 * Mantissa: 52 = 8*6Byte + 4 Bit	=> 53 Bits ^ 16 Digits Accuracy
 * Exponent: 11 = 8*1Byte + 3 Bit	=> 11 Bits ^ +/- 308 Exponent
 * Sign:	  1 =			1 Bit
 *
 * Design Decisions:
 * <!-- docstate
 * tags: [code/rational_numbers, code/interval_arithmetic]
 * concepts: [Rational Numbers and Interval Arithmetic]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * This Implementation is made 'final' to exploit the resulting benefits. */
final public class BodyDouble //TODO: was final to speed up Operations, undone because of AQuantityDouble
extends ABodyDouble {

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Static Constructor (Factory Method) that takes an Array as Input.
	  */
	final static public BodyDouble[] BODY_DOUBLE(final double[] arg) {
		int i = arg.length;
		BodyDouble[] ret = new BodyDouble[i];
		while (--i >= 0) {
			ret[i] = new BodyDouble(arg [i]); }
		return ret; }

	/**Constructor that takes any Object as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public BodyDouble(final Object arg) { super(arg); }

	/**Constructor that takes s String as Input.
	 * The Argument is converted to 'double' as the common Type.	 */
	public BodyDouble(final StreamTokenizer arg) throws IOException { super(arg); }

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public BodyDouble(ABodyDouble arg) { super(arg); }

	/**Constructor that takes 'double' as Input.	 */
	public BodyDouble(double arg) { super(arg); }

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.	 */
	public BodyDouble() { super(); }


	//////////////
	//	Testing	//
	//////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt() {
		System.out.println("Testing BodyDouble:");
		BodyDouble test  =  new BodyDouble(Math.PI);
//		BodyDouble test1 =  new BodyDouble(2);
		if (testInstance == null) testInstance = test;	//defined in ACopyAble to test the abstract Methods

	}

}
