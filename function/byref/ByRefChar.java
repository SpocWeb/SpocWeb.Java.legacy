package function.byref;

import function.AOrderAble;
import function.ICountAble;

/**
  * Title: ByRefShort<p>
  * Description:
  * This class is for transporting an Integer back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  * The Range of a 16 Bit Java Character is
  * o to 2^16-1 = 65535
  *
  * You can also simply use int[] to return Values from Method Calls.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-12-12, 01;52;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class ByRefChar
extends AOrderAble
implements ICountAble {
	
	///////////////////////////////////////////////////////////////////////////
	/// Static Constants and Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Character Constant */
	final static public char CHR_PERCENT = '%'; 
	
	/** String Constant */
	final static public String WHITESPACE= " \t\n\r"; 
	
	/** String Constant */
	final static public String PLUS_MINUS= "+-"; 
	
	/** String Constant */
	final static public String NUMBERS ="0123456789"; 
    
	/** String Constant */
	final static public String CAPITAL ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
    
	/** String Constant */
	final static public String SMALL =CAPITAL.toLowerCase(); 
    
	/** String Constant */
	final static public String IDENTIFIER = NUMBERS + CAPITAL + SMALL;

	private static final char CHR_0 = '0';
	
	private static final char CHR_A = 'A'-'0'-10;
	
	private static final char CHR_a = 'a'-'A'-10;
	
	/** 
	 * returns negative Values if no numeric Value can be found. 
	 * @param chr
	 * @return the numeric Value of the given Character with max. Radix of 37 
	 */
	final static public int VALUE_OF(char chr) {
		//slow Implementation
		//return IDENTIFIER.indexOf(Character.toUpperCase(chr)); 
		if ((chr-= CHR_0) < 10) return chr; 
		if ((chr-= CHR_A) < 27) return chr; 
		if ((chr-= CHR_a) < 27) return chr; 
		return -1; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors 
	///////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor */
	public ByRefChar(){ super(null); self = this;}

	/**Initializing Constructor, just comfortable	 */
	public ByRefChar(final char Value_){ this(); Value = Value_;}

	/**This is the Value of the Object	 */
	public char Value;
	
	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public byte	 getByte() { //return Value; }
		byte Val = (byte) Value;
		if (Value != Val) { throw new IllegalArgumentException(); }
		return Val; }

	/** Returns the Object Value represented by a 16 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public short getShort() { //return Value; }
		short Val = (short) Value;
		if (Value != Val) { throw new IllegalArgumentException(); }
		return Val; }

	/** Returns the Object Value represented by a 32 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public int	 getInt() { return Value; }

	/** Returns the Object Value represented by a 64 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public long  getLong() { return Value; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IMeasurAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign */
	public double getDouble() { return Value; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float  getFloat() { return Value; }

	///////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////

	/**Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommendedthat all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object.
	 *
	 * @return  a string representation of the object.
	 * @since   JDK1.0
	 */
	public String toString() { return String.valueOf(Value); }

	//////////////////////
	//	intOrderable	//
	//////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (final Object arg) { 
		if (arg == this) //null must stay not comparable! 
			return false;  //it is neither less nor more nor equal
		if (arg == null) //null should not be comparable! 
			return false;  //it is neither less nor more nor equal
		return Value < ((ByRefChar)arg).Value; }

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() { return Value; }

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return  true; }
		return Value == ByRefInt.TO_INT(obj); }

	/**Converts the String into an Array of ByRefChars	 */
	public static ByRefChar[] String2ByRefChar(String str) {
		return char2ByRefChar(str.toCharArray()); }

	/**Converts the String into an Array of ByRefChars	 */
	public static ByRefChar[] char2ByRefChar(char[] str) {
		int i = str.length;
		ByRefChar[] Return = new ByRefChar[i];
		while (--i >= 0) {
            Return[i] = new ByRefChar(str[i]); }
		return Return; }

}
