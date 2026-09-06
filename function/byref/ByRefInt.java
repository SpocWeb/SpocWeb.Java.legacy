package function.byref;

import java.security.InvalidParameterException;

import streamIO.Assert;
import function.AOrderAble;
import function.ICountAble;
import function.IMeasurAble;

/**
  * Title: ByRefInt<p>
  * Description:
  * This class is for transporting an int back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  * The Range of a 32 Bit Java Integer is
  * -2^31	= -2147483648 to
  *  2^31-1	=  2147483647
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:52:42Z
  * digest: 690c39a915bdefe96e7d6fdff284da279c82fc0f5bf421b9b9904c2b653f7a36
  * stale: false
  * tags: [code/function_wrapper, code/mathematical_constants]
  * concepts: [By-Reference Primitive Wrapper]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
final public class ByRefInt
extends AOrderAble 
implements ICountAble {
	
	////////////////////////////////////////////////////////////////////////////
	//  static Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Maximum Number of Iterations of Algorithms
	 * If an Algorithm is expected to converge slow or fast,
	 * it should use a derived Number of Iterations.	 */
	public static int MAX_ITER = IMeasurAble.MAX_ITER;
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for scalar (1Dim) Values:
	////////////////////////////////////////////////////////////////////////////////
	
	/** Rotates the low {@code octave} bits of x left by 1.
	 * @return the Value rotated left by 1  */
	final static public int ROL(int x, final int octave) {
		final int maxVal = 1 << octave;
		if((x <<= 1) > maxVal)
			x -= maxVal-1;
		return x;
	}

	/** Rotates the low {@code octave} bits of x right by 1.
	 * @return the Value rotated right by 1  */
	final static public int ROR(final int x, final int octave) {
		final int corr = (x &  1) << (octave-1);
		return    corr + (x >> 1);
	}

	/** Reverses the order of the low {@code octave} bits of x.
	 * @return the Value with it's Bit Sequence reverted */
	final static public int REVERT(int x, final int octave) {
		int ret = 0; 
		for(int i = octave; --i >= 0;) {
			ret <<= 1; 
			if(0 != (x & 1))
				++ret;
			x >>= 1; 
		}
		return ret; }
	
	/** Returns the relative Position of x to y: +1 if x&lt;y, -1 if y&lt;x, 0 if equal.
	 * @return the Sign of x-y  */
	final static public byte POSITION(final int x, final int y) {
		return (byte) (
			(x < y) ?  1 :
			(y < x) ? -1 : 0); }

	/** Returns the smaller of x and y.
	 * @return the Minimum of both Values */
	final static public int MIN(final int x, final int y) { return (x < y) ? x : y; }

	/** Returns the larger of x and y.
	 * @return the Maximum of both Values */
	final static public int MAX(final int x, final int y) { return (x > y) ? x : y; }
	
	/**Defines the Sqr for simple Types	 */
	final static public int SQR(final int x) { return x * x; }
	
	/**Defines the Cbc for simple Types	 */
	final static public int CBC(final int x) { return x * x * x; }
	
	/**Defines the binary Log Function for int	 */
	final static public byte LB(int x) {
		if (x <= 1) { //nest the Checks to minimize Execution Time
			if (x <= 0) { //or perform the most frequent Test first. 
				if (x == 0)
					return Byte.MIN_VALUE; 
				throw new InvalidParameterException("Logarithm not defined for negative Values like "+x); 
			} 
			return 0; 
		} 
		byte ret = 0; 
		while((x >>= 1) > 0) 
			 ++ret; 
		return ret; 
	}
	
	/**Defines the general Log Function Cbc for int	 */
	final static public byte LOG(int x, final int base) {
		if (x <= 1) { //nest the Checks to minimize Execution Time
			if (x <= 0) { //or perform the most frequent Test first. 
				if (x == 0)
					return Byte.MIN_VALUE; 
				throw new InvalidParameterException("Logarithm not defined for negative Values like "+x); 
			} 
			return 0; 
		} 
		byte ret = 0; 
		while((x /= base) > 0)  
			 ++ret; 
		return ret; 
	}
	
	/**Defines the general Power Function Cbc for int	 */
	final static public int BXP(final byte pow) { return BXP(pow, 1); }
	
	/**Defines the general Power Function Cbc for int	 */
	final static public int BXP(final byte pow, final int factor) { return factor << pow; }
	
	/**Defines the general Power Function Cbc for int	 */
	final static public int POW(final int x, final byte pow) { return POW(x, pow, 1); }
	
	/**Defines the general Power Function for int	 */
	final static public int POW(final int x, byte pow, final int factor) {
		/* simple, slow Implementation: O(pow)
		if (pow < 0) 
			return factor/POW(x, -pow, 1); 
		int ret  = factor; 
		while(--pow >= 0) 
			ret *= x; 
		*/
		//fast Implementation: O(log(pow))
		if (pow <= 1) { //special Cases...
			if (pow == 1) return factor*x;
			if (pow == 0) return factor;
			return factor/POW(x, (byte)-pow, 1); 
		}
		int prod = ((pow &  1) != 0)? x*factor : factor;	//could save one Multiplication in the loop
		int x_2n = x;
		//First Implementation: Use the Horner Scheme in the Exponent.
		for(;;){
			x_2n *= x_2n;	//you can save a SQR in the end by skipping this
			if (((pow >>= 1) & 1) != 0)	//(N1.odd())
				prod *= x_2n;	//you could save a multiplication in the beginning here
			else if (pow == 0)
				break; 
		} //(! N1.halfAt().IntAt().equals(mZERO))
		return prod; }

	/**Returns the Sign of x as an integer, i.e.
	 * -1 for negative x
	 *  0 for x == 0
	 * +1 for positive x	 */
	final static public int SIGN(final int x) {
		return (x > 0) ? 1 : (x < 0) ? -1 : 0;
	}
	
	/**Checks if the Interval contains x.
	 * This Implementation is unsymmetric, always a[0] < a[1] assumed
	 * there is no fast correct Solution, only Compromises !}	 */
	final static public boolean CONTAINS(final int x, final int left, final int right) {
		return (left <= x) ^ (right < x); }

	/**Returns true when n is odd	 */
	final static public boolean IS_ODD (final int n){ return (n & 1) == 1; }

	/**Returns true when n is odd	 */
	final static public boolean IS_EVEN(final int n){ return (n & 1) == 0; }

	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 */
	final static public int TO_INT(final Object arg) {
		return  arg instanceof ICountAble ?
				((ICountAble)arg). getInt() :
				arg instanceof Character ?  //Character not derived from Number
				((Character)arg).charValue() :
				((Number)	arg).intValue();	}

	///////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/**This is the Value of the Object	 */
	public int Value;
	
	///////////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor */
	public ByRefInt() { super(null); self = this; }
	
	/**Initializing Constructor, just comfortable	 */
	public ByRefInt(int Value_) { this(); Value = Value_; }
	
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
	public short getShort() { //return Value; }
		short Val = (short) Value;
		if (Value != Val) throw new IllegalArgumentException();
		return Val; }
	
	/** Returns the Object Value represented by a 32 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public int    getInt() { return Value; }
	
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
	public String toString() { return Integer.toString(Value); }

	//////////////////////
	//	IOrderable	
	//////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg){
		if ((arg == null) ||
			(arg == this)) return false;
		return Value < ByRefInt.TO_INT(arg); }//((ByRefInt)arg).Value;}

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() {return Value;}

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return  true;
		return Value == ByRefInt.TO_INT(obj); }
	
	///////////////////////////////////////////////////////////////////////////////
	// Static Testing & Main Methods
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * tests all Methods of this Class
	 */
	final static public void testIt() {
		final int x =5; 
		for(byte i = 10; --i >= 0;) {
			Assert.EQUALS(i, LOG(POW(x, i),x)); 
			Assert.EQUALS(i, LB(BXP(i))); 
		}
	}
	
	/** The main entry point for the application; runs {@link #testIt()}. */
	final static public void main(final String[] args) {
		testIt();
	}
	
}
