package function;

/**This class does not extend Number, because not every Group maps to numeric Values.
 * Instead it presents the conversion Routine to convert from Number Types.
 * This is unfortunately necessary, because Number does not implement an Interface.
 *
 * Class with only static Methods for Number Classes:
 * All countable Classes can be converted to these Integer Types.
 * usually implemented together with the Integer Interface.
 * The Problem is that static Methods cannot be declared in Interfaces
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:12:24Z
 * digest: 2bc750392113bca43e816dea20a5b7b248149cca02b8d0b17666c2a29f62c21e
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface ICountAble
extends IMeasurAble {

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	byte   getByte();

	/**Returns the Object Value represented by a 16 Bit Integer	 */
	short getShort();

	/**Returns the Object Value represented by a 32 Bit Integer	 */
	int     getInt();

	/**Returns the Object Value represented by a 64 Bit Integer	 */
	long   getLong();

	////////////////////////////////////////////////////////////////////////////
	//  static Integer Constants:
	////////////////////////////////////////////////////////////////////////////

	/** The integer 0. */
	final static public int  ZERO=  0;
	/** The integer 1. */
	final static public int  ONE =  1;
	/** The integer -1. */
	final static public int _ONE = -1;
	/** The integer 2. */
	final static public int TWO  =  2;
	/** The integer 3. */
	final static public int THREE=  3;
	/** The integer 4. */
	final static public int FOUR =  4;
	/** The integer 5. */
	final static public int FIVE =  5;
	/** The integer 10. */
	final static public int TEN      = 10;
	/** The integer 100. */
	final static public int HUNDRED  = 100;
	/** The integer 1000. */
	final static public int THOUSAND = 1000;

	////////////////////////////////////////////////////////////////////////////
	//  static Integer Constant Objects:
	////////////////////////////////////////////////////////////////////////////

	/** Boxed form of {@link #ZERO}. */
	final static public Integer Zero   = new Integer((int) ZERO);
	/** Boxed form of {@link #ONE}. */
	final static public Integer  One   = new Integer((int) ONE);
	/** Boxed form of {@link #_ONE}. */
	final static public Integer _One   = new Integer((int)-ONE);
	/** Boxed form of {@link #TWO}. */
	final static public Integer Two    = new Integer((int) TWO);
	/** Boxed form of {@link #THREE}. */
	final static public Integer Three  = new Integer((int) THREE);
	/** Boxed form of {@link #FOUR}. */
	final static public Integer Four   = new Integer((int) FOUR);
	/** Boxed form of {@link #FIVE}. */
	final static public Integer Five   = new Integer((int) FIVE);
	/** Boxed form of {@link #TEN}. */
	final static public Integer Ten    = new Integer((int) TEN);

/** String used for Encoding: Roman Numbers 1, 5, 10, 50, 100, 500, 1000  */
	final static public String cstrDigits = "IVXLCDM";

}
