package function;

/**This class does not extend Number, because not every Group maps to numeric Values.
 * Instead it presents the conversion Routine to convert from Number Types.
 * This is unfortunately necessary, because Number does not implement an Interface.
 *
 * Class with only static Methods for Number Classes:
 * All countable Classes can be converted to these Integer Types.
 * usually implemented together with the Integer Interface.
 * The Problem is that static Methods cannot be declared in Interfaces
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

	final static public int  ZERO=  0;
	final static public int  ONE =  1;
	final static public int _ONE = -1;
	final static public int TWO  =  2;
	final static public int THREE=  3;
	final static public int FOUR =  4;
	final static public int FIVE =  5;
	final static public int TEN      = 10;
	final static public int HUNDRED  = 100;
	final static public int THOUSAND = 1000;

	////////////////////////////////////////////////////////////////////////////
	//  static Integer Constant Objects:
	////////////////////////////////////////////////////////////////////////////

	final static public Integer Zero   = new Integer((int) ZERO);
	final static public Integer  One   = new Integer((int) ONE);
	final static public Integer _One   = new Integer((int)-ONE);
	final static public Integer Two    = new Integer((int) TWO);
	final static public Integer Three  = new Integer((int) THREE);
	final static public Integer Four   = new Integer((int) FOUR);
	final static public Integer Five   = new Integer((int) FIVE);
	final static public Integer Ten    = new Integer((int) TEN);

/** String used for Encoding: Roman Numbers 1, 5, 10, 50, 100, 500, 1000  */
	final static public String cstrDigits = "IVXLCDM";

}
