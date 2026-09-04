package function.byref;

import function.AOrderAble;
import function.ICountAble;

/**
  * Title: ByRefShort<p>
  * Description:
  * This class is for transporting a short back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  *
  * You can also simply use short[] to return Values from Method Calls.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-12-12, 01;52;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class ByRefShort
extends AOrderAble
implements ICountAble {

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for scalar (1Dim) Values:
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the Minimum of both Values */
	final static public short Min(short x, short y) {
		return (x < y) ? x : y;
	}

	/** @return the Maximum of both Values */
	final static public short Max(short x, short y) {
		return (x > y) ? x : y;
	}

	/**Defines the Sqr for simple Types	 */
	final static public short Sqr(short x) {
		return x *= x;
	}

	/**Defines the Cbc for simple Types	 */
	final static public short Cbc(short x) {
		return x *= x * x;
	}

	/**Returns the Sign of x as an integer, i.e.
	 * -1 for negative x
	 *  0 for x == 0
	 * +1 for positive x	 */
	final static public short Sign(short x) {
		if (x > 0) {
			return 1;
		}
		if (x < 0) {
			return -1; 
		}
		return 0;
	}

	/**Checks if the Interval contains x.
	 * This Implementation is unsymmetric, always a[0] < a[1] assumed
	 * there is no fast correct Solution, only Compromises !}	 */
	final static public boolean contains(short x, short left, short right) {
		return (left <= x) ^ (right < x);
	}

	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 */
	final static public short getShort(Object arg) {
		return  arg instanceof ICountAble?
				((ICountAble)arg).getShort() :
				arg instanceof Character ? (short) //Character not derived from Number
				((Character) arg).charValue() :
				((Number)	 arg).shortValue(); }

	///////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////////

	/**This is the Value of the Object	 */
	public short Value;

	///////////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////////

	/**Empty Constructor */
	public ByRefShort() { super(null); self = this; }

	/**Initializing Constructor, just comfortable	 */
	public ByRefShort(short Value_) { this(); Value = Value_; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public byte	 getByte() { //return Value; }
		byte Val = (byte) Value;
		if (Value != Val) throw new IllegalArgumentException();
		return Val; }

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
	public String toString() { return Short.toString(Value); }

	//////////////////////
	//	IOrderable
	//////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg) {
		if ((arg == null) ||
			(arg == this)) return false;
		return Value < ByRefShort.getShort(arg); } //((ByRefShort)arg).Value; }

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() { return Value; }

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1
	 */
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return  true;
		return Value == ByRefShort.getShort(obj); }

}
