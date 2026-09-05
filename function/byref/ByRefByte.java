package function.byref;

import function.AOrderAble;
import function.ICountAble;

/**
  * Title: ByRefByte<p>
  * Description:
  * This class is for transporting a byte back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  *
  * You can also simply use byte[] to return Values from Method Calls.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-12-12, 01;52;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: 42a89f81a7f30f24f951cac77179d8bc1a26b4038486694d02c514414deaebca
  * stale: false
  * tags: [code/function_wrapper, code/mathematical_constants]
  * concepts: [By-Reference Primitive Wrapper]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
final public class ByRefByte
extends AOrderAble
implements ICountAble {

	/**Empty Constructor */
	public ByRefByte(){super(null); self = this;}

	/**Initializing Constructor, just comfortable	 */
	public ByRefByte(byte Value_){this(); Value = Value_;}

	/**Constructs a Byte object initialized to the Value specified by the
	 * String parameter.  The radix is assumed to be 10.
	 *
	 * @param s		the String to be converted to a Byte
	 * @exception	NumberFormatException If the String does not
	 *			contain a parsable byte.
	 * @since   JDK1.1
	 */
	public ByRefByte(String s) throws NumberFormatException {
		this(); this.Value = parseByte(s); }

	/**This is the Value of the Object	 */
	public byte Value;

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public byte	 getByte() { return Value; }

	/** Returns the Object Value represented by a 16 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public short getShort() { return Value; }

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
	public String toString() {return Byte.toString(Value);}

	//////////////////////
	//	OrderAble
	//////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (final Object arg) { return Value < ((ByRefByte)arg).Value; }

	//	static Members

	/**The minimum Value a Byte can have.     */
	final static public byte   MIN_VALUE = -128;

	/**The maximum Value a Byte can have.     */
	final static public byte   MAX_VALUE = 127;

	//	static Methods

	/**Returns a new String object representing the specified Byte. The radix
	 * is assumed to be 10.
	 *
	 * @param b	the byte to be converted
	 */
	public static String TO_STRING(final byte b) { return Integer.toString(b, 10); }

	/**Assuming the specified String represents a byte, returns
	 * that byte's Value. Throws an exception if the String cannot
	 * be parsed as a byte.  The radix is assumed to be 10.
	 *
	 * @param s		the String containing the byte
	 * @exception	NumberFormatException If the string does not
	 *			contain a parsable byte.
	 */
	public static byte parseByte(final String s) throws NumberFormatException {
		return parseByte(s, 10); }

	/**Assuming the specified String represents a byte, returns
	 * that byte's Value. Throws an exception if the String cannot
	 * be parsed as a byte.
	 *
	 * @param s		the String containing the byte
	 * @param radix	the radix to be used
	 * @exception	NumberFormatException If the String does not
	 *			contain a parsable byte.
	 * @since   JDK1.1
	 */
	public static byte parseByte(final String s, final int radix) 
	throws NumberFormatException {
		int i = Integer.parseInt(s, radix);
		if (i < MIN_VALUE || i > MAX_VALUE) {
            throw new NumberFormatException(); }
		return (byte)i; }

	/**Assuming the specified String represents a byte, returns a
	 * new Byte object initialized to that Value.  Throws an
	 * exception if the String cannot be parsed as a byte.
	 *
	 * @param s		the String containing the integer
	 * @param radix 	the radix to be used
	 * @exception	NumberFormatException If the String does not
	 *			contain a parsable byte.
	 * @since   JDK1.1
	 */
	final static public Byte valueOf(final String s, final int radix) 
	throws NumberFormatException {
		return new Byte(parseByte(s, radix)); }

	/**Assuming the specified String represents a byte, returns a
	 * new Byte object initialized to that Value.  Throws an
	 * exception if the String cannot be parsed as a byte.
	 * The radix is assumed to be 10.
	 *
	 * @param s		the String containing the integer
	 * @exception	NumberFormatException If the String does not
	 *			contain a parsable byte.
	 * @since   JDK1.1
	 */
	final static public Byte valueOf(final String s) throws NumberFormatException {
		return valueOf(s, 10); }

	/**Decodes a String into a Byte.  The String may represent
	 * decimal, hexadecimal, and octal numbers.
	 *
	 * @param nm the string to decode
	 * @since   JDK1.1
	 */
	final static public Byte PARSE(final String nm) throws NumberFormatException {
		if (nm.startsWith("0x")) return Byte.valueOf(nm.substring(2), 16); 
		if (nm.startsWith("#" )) return Byte.valueOf(nm.substring(1), 16); 
		if (nm.startsWith("0" )
            && nm.length() > 1)  return Byte.valueOf(nm.substring(1),  8); 
								 return Byte.valueOf(nm);
	}

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() { return Value; }

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1     */
	public boolean equals(final Object obj) {
		if (obj == null) return false; 
		if (obj == this) return  true; 
		return Value == ByRefInt.TO_INT(obj); }

	/**Returns the integer Power of the Argument.
	 * Can only be used in a certain Range of x and n.
	 * Frequently used Helper Function	 */
	final static public long POW(final byte x, byte n) {
		if((n == 0) ||
		   (x == 1))return 1;
		if((n == 1) ||
		   (x == 0))return x;
		if (n <  0) throw new AbstractMethodError();	//x = 1.0/x;
		if (n > 60) throw new AbstractMethodError();	//avoid Overflow with 2^63
		long Prod = ((n &  1) != 0)? x : 1;	//could save one Multiplication in the loop
		long Factor = x;
		//First Implementation: Use the Horner Scheme in the Exponent.
		do{
			Factor *= Factor; //if ((Factor *= Factor) >= Integer.MAX_VALUE) throw new AbstractMethodError();	//you can save a SQR in the end by skipping this
			if (((n >>= 1) & 1) != 0)	//(N1.odd())
				Prod *= Factor;	//you could save a Multiplication in the beginning here
		}while (n != 0);	//(! N1.halfAt().IntAt().equals(mZERO))
		return Prod; }

	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 */
	final static public byte getByte(Object arg) {
		return  arg instanceof ICountAble?
				((ICountAble)arg).getByte() :
				arg instanceof Character ? (byte)
				((Character)arg).charValue() :
				((Number)	arg).byteValue() ; }

}
