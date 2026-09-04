package function.byref;

import streamIO.Assert;
import math.vector.VectorDouble;
import math.vector.VectorInt;
import math.vector.VectorString;
import function.ACountAble;
import function.ICountAble;
import function.IMeasurAble;
import function.derive.ring.body.Exponential;

/**
  * Title: ByRefDouble<p>
  * Description:
  * This class is for transporting a double back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  *
  * You can also simply use double[] to return Values from Method Calls.
  *
  * Special Values:
  * NaN = 0/0
  * -Infinity = -1/0
  * +Infinity = +1/0
  * +0 = +1/Infinity =  0
  * -0 = -1/Infinity = -0
  *
  * Double-Properties:
  * Bits:     64 = 8*8Byte
  * Mantissa: 52 = 8*6Byte + 4 Bit  => 53 Bits ^ 16 Digits Accuracy
  * Exponent: 11 = 8*1Byte + 3 Bit  => 11 Bits ^ +/- 307 Exponent
  * Sign:      1 =           1 Bit
  * abs.Range: 4.9e-324 to 1.7976931348623157e+308
  * @see Function.IMeasurAble
  *
  * Known SubClasses:
  * CachedDouble
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-12-12, 01;52;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class ByRefDouble
//extends CMeasurAble //not possible in Java to make a protected Variable ('Value') public
//couldn't easily implement orderAble when derived from CMeasurAble!
extends ACountAble //AOpMeasurAble //AOpDouble
implements IInteger, IFloat {

	/* actual Accuracy used as Criterion for Convergence	*/
	public static double DoubleAccuracy = IMeasurAble.DOUBLE_ACCURACY;

	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**This is the Value of the Object	 */
	public double Value;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**Empty Constructor */
	public ByRefDouble() {} //super(null); self = this; } //performed automatically!

	/**Initializing Constructor, just comfortable	 */
	public ByRefDouble(double Value_) { //super(null); //performed automatically!
		Value = Value_;
	}

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public byte getByte() { //return Value; }
		byte Val = (byte) Value;
		if (Val != Value) {
			throw new IllegalArgumentException();
		}
		return Val;
	}

	/** Returns the Object Value represented by a 16 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public short getShort() { //return Value; }
		short Val = (short) Value;
		//		if  (Math.abs(Val - Value) > Value*DoubleAccuracy) throw new IllegalArgumentException();
		if (Val != Value) {
			throw new IllegalArgumentException();
		}
		return Val;
	}

	/** Returns the Object Value represented by a 32 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public int getInt() { //return Value; }
		int Val = (int) Value;
		if (Val != Value) {
			throw new IllegalArgumentException();
		}
		return Val;
	}

	/** Returns the Object Value represented by a 64 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public long getLong() { //return Value; }
		long Val = (long) Value;
		if (Val != Value) {
			throw new IllegalArgumentException();
		}
		return Val;
	}

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IMeasurAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign */
	public double getDouble() {
		return Value;
	}
	/*		double Val = (double) Value;
			if (Value != Val) {
				throw new IllegalArgumentException(); }
			return Val; }

		/**Returns the Object Value represented by a scalar Variable of Type float.
		 * It consists of an IEEE Number with 32 Bit (4 Byte):
		 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float getFloat() { //return Value; }
		float Val = (float) Value;
		if (Val != Value) {
			throw new IllegalArgumentException();
		}
		return Val;
	}

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IAdjustAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/**
	 * @see function.ByRef.IAdjustAble#setDouble(double)
	 */
	public void setDouble(double val) {
		Value = val;
	}

	/**
	 * @see function.ByRef.IAdjustAble#setFloat(float)
	 */
	public void setFloat(float val) {
		Value = val;
	}

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICategorizeAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** gives this Object the given Category
	 *  @param val the Category to set this Object to
	 */
	public void setByte(byte val) {
		Value = val;
	}

	/** gives this Object the given Category
	 *  @param val the Category to set this Object to
	 */
	public void setShort(short val) {
		Value = val;
	}

	/** gives this Object the given Category
	 *  @param val the Category to set this Object to
	 */
	public void setInt(int val) {
		Value = val;
	}

	/** gives this Object the given Category
	 *  @param val the Category to set this Object to
	 */
	public void setLong(long val) {
		Value = val;
	}

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
	public String toString() {
		return Double.toString(Value);
	}

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IOrderAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan(Object arg) {
		if ((arg == null) || (arg == this))
			return false;
		return Value < ByRefDouble.GET_DOUBLE(arg);
	} //((ByRefDouble)arg).Value;}

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() {
		return (int) Double.doubleToLongBits(Value);
	}
	//		long bits = Double.doubleToLongBits(Value);
	//		return (int)(bits ^ (bits >> 32)); }
	//		return new Double(Value).hashCode(); }

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	public boolean equals(Object arg) {
		if (arg == this)
			return true;
		return EQUALS(Value, arg); 
	}

	////////////////////////////////////////////////////////////////////////////
	//  static Methods (could not be defined in IMeasurAble)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param arg
	 * @return NaN if the String could not be parsed
	 */
	final static public double TRY_PARSE(final String arg) {
		double[] ret = { Double.NaN }; 
		TRY_PARSE(arg, 0, ret);
		return ret[0]; 
	}
	
	/**
	 * @param arg
	 * @return NaN if the String could not be parsed
	 */
	final static public int TRY_PARSE(final String arg, final double[] defaultValue) {
		return TRY_PARSE(arg, 0, defaultValue); 
	}
	
	/**
	 * @param arg
	 * @param pos Position to start Parsing from 
	 * @return NaN if the String could not be parsed
	 */
	final static public int TRY_PARSE(final String arg, int pos, final double[] defaultValue) {
		if (arg == null) return -1;
		final int len = arg.length(); 
		final long[] value = {0};
 		int newPos = ByRefLong.TRY_PARSE(arg, pos, 10, value);
		if (newPos <= pos)
			return pos; //could not be parsed
		pos = newPos;
		double val = value[0]; 
		if ((pos < len) && (arg.charAt(pos)=='.')) {
			newPos = ByRefLong.TRY_PARSE(arg, ++pos, 10, value);
			if (newPos > pos) {
				final long fraction = value[0]; 
				val += fraction / ByRefDouble.POW(10, newPos - pos);
				pos = newPos;
			}
		}
		if ((pos < len) && (Character.toUpperCase(arg.charAt(pos))=='E')) {
			newPos = ByRefLong.TRY_PARSE(arg, ++pos, 10, value);
			if (newPos > pos) {
				val *= ByRefDouble.POW(10, (int) value[0]);
				pos = newPos;
			}
		}
		defaultValue[0] = val; 
		return pos; 
	}
	
	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 * @throws NumberFormatException if a String was to be converted!
	 */
	final static public double GET_DOUBLE(final Object arg)
		throws NumberFormatException {
		if (arg == null) {		          return Double.NaN; }
		if (arg instanceof IMeasurAble) { return ((IMeasurAble)arg).getDouble(); }
		if (arg instanceof Number     ) { return ((Number)     arg).doubleValue(); }
		//if (arg instanceof String) {
			//String strArg = arg.toString();
			return Double.parseDouble(arg.toString()); //throws NumberFormatException
		//return Double.NaN; 
	} //
	
	////////////////////////////////////////////////////////////////////////////////
	//	Accuracy and Testing Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**Multiplication by the Accuracy.
	 * Should be parameterized by an adjustale Accuracy. 	 */
	final static public double MUL_ACCURACY(final double arg) { return arg * ByRefDouble.DoubleAccuracy; }

	/**Absolute Value Multiplication by the Accuracy.
	 * Should be parameterized by an adjustale Accuracy. 	 */
	final static public double MUL_ABS_ACCURACY(double arg) { return Math.abs(arg) * ByRefDouble.DoubleAccuracy; }

	/**Precision for Rounding	 */
	protected static int PRECISION = 4;	//4 Positions before the '.'
		
	/**Precision Factor for Rounding	 */
	protected static float PRECISION_FACTOR = 10000;
		
	/**Retrieves Formatting of float Point Numbers	 */
	final static public int GET_FORMAT_PRECISION() { return PRECISION; }
		
	/**Procedure to change Formatting of float Point Numbers	 */
	final static public void SET_FORMAT_PRECISION(final int Digits) {
		if((PRECISION <-18) ||
		   (PRECISION > 18)) throw new AbstractMethodError();//	return;
		if (PRECISION == Digits)return;
			PRECISION =  Digits;
			PRECISION_FACTOR = (float) Math.pow(10, Digits);
	}
		
	/**Rounds this Number to the given Number of Digits
	 * Negative Digits round to full 10s, 100s etc.
	 * @see BodyDouble#RoundedDigits(double, int)
	 */
	final static public double ROUND(final double x, final byte Digits)	{
		final double Factor;
		if (Digits != PRECISION)
			 Factor = PRECISION_FACTOR;
		else Factor = Math.pow(10, Digits);
		return Math.rint(x*Factor)/Factor; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Rounds and formats this Number in fixed Point Notation to the Number of Digits
	 * Negative Digits round to full 10s, 100s etc.	 */
	final static public String FORMAT(final double x,
	final int lengthBeforeDot,
	final int lengthAfter_Dot,
	final boolean alwaysSign) {
		long tmp;
	//	boolean small = (Math.abs(x) < 1);	//not needed!
		if (lengthAfter_Dot == PRECISION)
			 tmp = Math.round(x * PRECISION_FACTOR);
		else tmp = Math.round(x * Math.pow(10, lengthAfter_Dot));
		int OverallLength = lengthBeforeDot +  lengthAfter_Dot + 2;	//for the Dot and the Sign
		if ((tmp < Long.MAX_VALUE) &&
			(tmp > Long.MIN_VALUE))// && (tmp != 0.0)) //this last part makes small Items to be displayed in full accuracy
		{
			String str;		//Length for Padding with Spaces
			boolean neg;
			int PadLength = lengthBeforeDot + Math.max(0, lengthAfter_Dot);
			if (neg = (tmp <  0)) tmp = -tmp;
			if		  (tmp == 0) {
				if (lengthAfter_Dot <= 0)
					return VectorString.FORMAT("0", -PadLength); //right aligned
				str = VectorDouble.STR_ZEROS.substring(0, lengthAfter_Dot + 1);
			}
			else str = String.valueOf(tmp);
			String Sign = (neg ? "-" : (alwaysSign ? "+" : ""));
			int len = str.length()-lengthAfter_Dot;
			if (len <= 0) {str = VectorDouble.STR_ZEROS.substring(0, 1-len).concat(str); len = 1; }	//leading Zeroes
			if (lengthAfter_Dot > 0) //Space left to insert the '.'
				return	VectorString.FORMAT( Sign + //right aligned!
						VectorString.FORMAT //right aligned!
								(str.substring(0, len), Sign.length() - lengthBeforeDot) + "." +
								 str.substring(   len), 1-OverallLength);	//padding with Spaces on the left
				return	VectorString.FORMAT(	Sign + //left aligned!
						VectorString.FORMAT  //right aligned!
								(str.concat	//pad with "0" before the "."
								(VectorDouble.STR_ZEROS.substring(0, Sign.length() - lengthAfter_Dot)), -lengthBeforeDot)
								 , PadLength);	//padding with Spaces on the left
		}	//Goal is to format so that the Dot is always at the same Position
		return ByRefDouble.FORMAT(x, lengthAfter_Dot, -OverallLength); } //right aligned
	
	/**Formats the given Number to the Number of Digits
	 * Negative Digits round to full 10s, 100s etc.	 */
	final static public char[] FORMAT(final char[] arr, final double x
	, final int startPos
	, final int dot__Pos
	, final int stop_Pos, boolean alwaysSign) {
		return FORMAT(arr, Math.round(x*Math.pow(10, stop_Pos - dot__Pos)), startPos, dot__Pos, stop_Pos, alwaysSign); 
	}
	
	/** Formats the given Integer Number to the Number of Digits
	 * 
	 * @param arr the Array to be filled 
	 * @param y the Value to write into the Array 
	 * @param alwaysSign Flag whether to display a Sign only for negative Numbers 
	 * @return the given Array or a new one if it was null
	 */
	final static public char[] FORMAT(final char[] arr, final long y) { return FORMAT(arr, y, false); }
	
	/** Formats the given Integer Number to the Number of Digits
	 * 
	 * @param arr the Array to be filled 
	 * @param y the Value to write into the Array 
	 * @param alwaysSign Flag whether to display a Sign only for negative Numbers 
	 * @return the given Array or a new one if it was null
	 */
	final static public char[] FORMAT(final char[] arr, final long y, final boolean alwaysSign) {
		return FORMAT(arr, y, 0, arr.length, arr.length-1, alwaysSign); }
	
	/** Formats the given Integer Number with Fixed Point Position 
	 * to the Number of Digits
	 * 
	 * @param arr the Array to be filled 
	 * @param y the Value to write into the Array 
	 * @param startPos inclusive first Position to place Characters
	 * @param dot__Pos if between startPos and stop_Pos, places a '.' there
	 * @param stop_Pos inclusive last  Position to place Characters
	 * @param alwaysSign
	 * @return
	 */
	final static public char[] FORMAT(char[] arr, long y
	, final int startPos
	, final int dot__Pos
	, final int stop_Pos, boolean alwaysSign) {
		if (arr == null) 
			arr = new char[stop_Pos+1-startPos]; 
		boolean neg = (y < 0); 
		if (neg) {
			y = -y; }
		for (int i = stop_Pos+1; --i >= startPos; ) { //starting from the End makes the Algorithm considerably easier! 
			if (i == dot__Pos) { //the dot can be outside the Range ;-)
				arr[i] = '.'; y*=10;
				continue; }
			if ((y == 0) && (i < dot__Pos)) {
				//if (neg || alwaysSign)
				arr[i] = neg ? '-' : (alwaysSign ? '+' : ' ');
				neg = alwaysSign = false; 
				continue; }
			//(y > 0) || (i > dot_Pos), fill with 0s
			long tmp = y / 10; 
			final long digit = y - tmp * 10; y = tmp;  
			arr[i]=(char) ('0'+digit);
		}
		return arr; 
	}
	
	/**Formats this Number to the set Number of Digits
	 * additionally a (larger) OverallLength is defined to format empty Space.
	 * Negative LengthAfter_Dot rounds to full 10s, 100s etc.	 */
	final static public String FORMAT(double x,
									  int lengthBeforeDot,
									  int lengthAfter_Dot,
									  int lengthOverall,
									  boolean alwaysSign) {
	//	if (OverallLength < LengthBeforeDot + Math.max(0, Precision)) throw new AbstractMethodError();
			return VectorString.FORMAT(ByRefDouble.FORMAT(x, lengthBeforeDot, lengthAfter_Dot, alwaysSign), lengthOverall); }

	/**Formats the String to the given Length and Precision (left or right aligned)	 */
	final static public String FORMAT(double x, int Length, int Precision) {
		return VectorString.FORMAT(String.valueOf(ROUND(x, (byte) Precision)), Length); }

	/**Formats the String to the given Length (left or right aligned)	 */
	final static public String FORMAT(final double x, int Length) {
		return VectorString.FORMAT(String.valueOf(x), Length); }

	/////////////////////////////////////////////////////////////////////////////////////

	/**Returns a random Number Offset <= x < Offset + Range	 */
	final static public double RANDOM(double Offset, double Range) {
		return Offset + Math.random() * Range; }

	/**Returns a random Number 0 <= x < Range	 */
	final static public double RANDOM(double Range) {
		return Math.random() * Range; }

	/** Returns a random Number between 1 and -1
	  * Used in @see randomizeWeights()
	  */
	public static double RANDOM_1_1() {
		final double tmp = Math.random();
		return tmp + tmp - 1;
	}
	
	/**Defines the Sqr for simple Types	 */
	final static public double SQR(final double x) { return x * x; }
	
	/**Defines the Cbc for simple Types	 */
	final static public double CBC(final double x) { return x * x * x; }
	
	/**Returns the integer Power of the Argument.
	 * Efficient Implementation: O(log(n))
	 */
	final static public double POW(double x, int n) {
		if (n <= 1) { //special Cases...
			if (n == 1) return x;
			if (n == 0) return 1;
			x = 1/x; n = -n; 
		}
		double Prod = ((n &  1) != 0)? x : 1;	//could save one Multiplication in the loop
		double Factor = x;
		//First Implementation: Use the Horner Scheme in the Exponent.
		for(;;){
			Factor *= Factor;	//you can save a SQR in the end by skipping this
			if (((n >>= 1) & 1) != 0)	//(N1.odd())
				Prod *= Factor;	//you could save a multiplication in the beginning here
			else if (n == 0)
				break; 
		} //(! N1.halfAt().IntAt().equals(mZERO))
		return Prod; }

	/**Returns the Sign of x as an integer, i.e.
	 * -1 for negative x
	 *  0 for x == 0
	 * +1 for positive x	 */
	final static public int SIGN(final double x) {
		//return (x > 0) ? 1 : (x < 0) ? -1 : 0;
		if (x > 0)	return  1;
		if (x < 0)	return -1;
					return  0; }

	/**Returns the Zchn of the Argument.
	 * Frequently used Helper Function	 */
	final static public int ZCHN(double arg) {
		if (arg >=0) {
			return  1; } 
			return -1; }

	/**Returns y multiplied by the Sign of x.	 */
	final static public double MUL_BY_SIGN(double x, double y, boolean withZero) {
		if (x <  0) return -y;
		if(withZero &&	//first test whether you want to consider 0!
		   (x == 0))return  x;
					return  y; 
	}
		
	/**
	 * @param x determines the absolute Value
	 * @param y determines the Sign
	 * @return x with the Sign of y
	 */
	final static public double ASSIGN_SIGN(final double x, final double y) {
		return (y >= 0) ? Math.abs(x) : -Math.abs(x); }
			
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for scalar (1Dim) Values:
	////////////////////////////////////////////////////////////////////////////////
	
	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	final static public boolean EQUALS(final double value, final Object arg) {
		if (arg == null)
			return false;
		return EQUALS(value, ByRefDouble.GET_DOUBLE(arg));
	}
	
	/** Tests whether the given value is zero 
	 * compared to the specified value 
	 *
	 * @param value the value to test
	 * @param comparedTo the value to compare to
	 * @return 		true value is considerably smaller in Magnitude than comparedTo; false otherwise.
	 */
	final static public boolean IS_ZERO(final double value, final double magnitude) {
		return IS_SMALL(value, magnitude, IMeasurAble.DOUBLE_ACCURACY);
	}
	
	/** Tests whether the given value is zero 
	 * compared to the specified value 
	 *
	 * @param value the value to test
	 * @param comparedTo the value to compare to
	 * @return 		true value is considerably smaller in Magnitude than comparedTo; false otherwise.
	 */
	final static public boolean IS_SMALL(final double value, final double magnitude, final double rel) {
		if (Math.abs(value/magnitude) < rel) {
			//if Value and argVal are similar, they have the same Sign, 
			//i.e. |v-a|/|v+a|=||v|-|a||/||v|+|a|| < 1 with single Minimum  
			//if they are unsimilar, they may have even oposite Sign
			//i.e. |v-a|/|v+a|=||v|+|a||/||v|-|a|| > 1 
			return true; }
		//check the (not so rare) 0/0 case
			return value == 0;
	}
		
	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	final static public boolean EQUALS(final double value, final double argVal) {
		return IS_SMALL(value - argVal, value + argVal, IMeasurAble.DOUBLE_ACCURACY);
	}
		
	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	final static public boolean EQUALS(final double value, final double argVal, final double rel) {
		return IS_SMALL(value - argVal, value + argVal, rel); 
	}
		
	/** validates whether the given Value exceeds the given minimum Value	 */
	final static public boolean EQUALS(
	final double expected,
	final double actual,
	final double rel,
	final double abs) {
		if (Math.abs(expected - actual) <= Math.abs(expected * rel) + abs) {
		//if (Math.abs(expected - actual) <= Math.abs((expected+actual) * rel) + abs) {
			//not necessary to add the absolute Values of both!
			return true;
		}
		return false;
	}
	
	/** @return the Minimum of both Values */
	final static public byte POSITION(final double x, final double y) {
		return (byte) (
			(x < y) ?  1 : 
			(y < x) ? -1 : 0); }

	/**@see Math#min(double, double) 
	 * @return the Minimum of both Values */
	final static public double MIN(final double x, final double y) {
		return (x < y) ? x : y;
	}

	/** @return the Maximum of both Values */
	final static public double MAX(final double x, final double y) {
		return (x > y) ? x : y;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////

	/**Calculates the full Arcus Tangens for two coordinates x and y.
	 * The Return Value is in the range of -pi to pi. 	 */
	final static public double ArcTg(final double x, final double y) {
		return Math.atan2(x, y);
	}

	/**Checks if the Interval contains x.
	 * This Implementation is unsymmetric, always a[0] < a[1] assumed
	 * there is no fast correct Solution, only Compromises !}	 */
	final static public boolean CONTAINS(double x, double left, double right) {
		return (left <= x) ^ (right < x);
	}
	
	/** 
	 * calculate (a^2+b^2)^{1/2} without overflow (2.6)
	 * Helper calculates the euklidean Norm of a 2Dim Vector, Chapter 
	 * 
	 * @param a
	 * @param b
	 * @return (a^2+b^2)^{1/2} 
	 */	
	final static public double NORM(final double a, final double b) {
		final double aAbs=Math.abs(a);
		final double bAbs=Math.abs(b);
		if (aAbs > bAbs) return aAbs*Math.sqrt(1+ByRefDouble.SQR(bAbs/aAbs));
		else return (bAbs == 0 ? 0 : bAbs*Math.sqrt(1+ByRefDouble.SQR(aAbs/bAbs)));
	}
		
	///////////////////////////////////////////////////////////////////////////////////
	// Trigonometric Calculations for Scalars (only in Double)
	///////////////////////////////////////////////////////////////////////////////////
	
	/**Returns both the Sinus and Cosinus Hyperbolicus.
	 * This is more efficient, because CosH^2-SinH^2=1
	 */
	final static public double SINH_COSH(double x, ByRefDouble CosH) {
		double ExpM1 = Exponential.ExpM1(x);
		double Exp = ExpM1 + ONE;
		CosH.Value = (Exp + ONE / Exp) * HALF;
		return (ExpM1 + ExpM1 / Exp) * HALF;
	} //
		
	/** @return the Sinus and Cosinus of x in Place	 */
	final static public double SIN_COS(double x, ByRefDouble Cos) {
		boolean neg;
		if (neg = (x < 0)) {
			x = -x;
		}
		if (x > TWO_PI) {
			x %= TWO_PI;
		}
		//		if (x < -PI) { x += TWO_PI; } else
		if (x > PI) {
			x -= TWO_PI;
		}
		if (neg) {
			return -SIN_COS_SAFE(x, Cos);
		}
		return SIN_COS_SAFE(x, Cos);
		//		Sin.Value = Math.cos(x); return Math.sin(x);
	}
	
	/** @return the Sinus and Cosinus of x in Place	 */
	final static public void COS_SIN(final double x, final double[] CosSin) {
		COS_SIN(x, CosSin, 0, 1);
	}
	
	/** @return the Sinus and Cosinus of x in Place	 */
	final static public void COS_SIN(double x, final double[] CosSin
			, final int cosIndex, final int sinIndex) {
		//		Sin.Value = Math.cos(x); return Math.sin(x);
		boolean neg;
		if (neg = (x < 0)) {
			x = -x;
		}
		if (x > TWO_PI) {
			x %= TWO_PI;
		}
		//		if (x < -PI) { x += TWO_PI; } else
		if (x > PI) {
			x -= TWO_PI;
		}
		ByRefDouble.COS_SIN_SAFE(x, CosSin, cosIndex, sinIndex);
		if (!neg) {
			return; 
		}
		CosSin[sinIndex] = -CosSin[sinIndex];
	}
	
	/**
	 * @param SinCos the Sinus and Cosinus of x in Place
	 * @param x is expected to be in the Range of -Pi..Pi
	 */
	final static public void COS_SIN_SAFE(final double x, final double[] CosSin) {
		COS_SIN_SAFE(x, CosSin, 0, 1); 
	}
	
	/**
	 * @param SinCos the Sinus and Cosinus of x in Place
	 * @param x is expected to be in the Range of -Pi..Pi
	 */
	final static public void COS_SIN_SAFE(final double x, final double[] CosSin
			, final int cosIndex, final int sinIndex) {
		final double c = CosSin[cosIndex] = Math.cos(x);
		if (x > 0) {
			CosSin[sinIndex] = +Math.sqrt(ICountAble.ONE - c * c);
		} else {
			CosSin[sinIndex] = -Math.sqrt(ICountAble.ONE - c * c);
		}
	}	
		
	/**
	 * @return the Sinus and Cosinus of x in Place
	 * @param x is expected to be in the Range of -PI..Pi
	 */
	final static public double SIN_COS_SAFE(double x, ByRefDouble Cos) {
		Cos.Value = Math.cos(x);
		if (x > 0)
			return +Math.sqrt(ONE - Cos.Value * Cos.Value);
		else
			return -Math.sqrt(ONE - Cos.Value * Cos.Value);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Formats this Number to the Number of Digits
	 * Negative Digits round to full 10s, 100s etc.	 */
	private static final void testFormat2() {
		System.out.println("\nTesting fixed Length Formatting on a char[]:");
		final int startPos = 0;
		final int dot__Pos = 5;
		final int stop_Pos = 10;
		System.out.println(FORMAT(null, +Math.PI, startPos, dot__Pos, stop_Pos, true)); 
		System.out.println(FORMAT(null, +Math.PI, startPos, dot__Pos, stop_Pos, false)); 
		System.out.println(FORMAT(null, -Math.PI, startPos, dot__Pos, stop_Pos, true)); 
		System.out.println(FORMAT(null, -Math.PI, startPos, dot__Pos, stop_Pos, false)); 
		System.out.println(FORMAT(null, -0., startPos, dot__Pos, stop_Pos, false)); 
		System.out.println(FORMAT(null, +0., startPos, dot__Pos, stop_Pos, false)); 
		System.out.println(FORMAT(null, -0.01, startPos, dot__Pos, stop_Pos, false)); 
		System.out.println(FORMAT(null, +0.01, startPos, dot__Pos, stop_Pos, false)); 
	}
	
	/**Tests the Formatting Routine	 */
	private static final void testParsing() throws Exception {
		Assert.EQUALS(PI, TRY_PARSE(Double.toString(PI)));
	}
	
	/**Tests the Formatting Routine	 */
	private static final void testFormat() throws Exception {
		System.out.println("\nTesting left and right Alignment on Formatting:");
		String testStr = "Hello World";
		int l = testStr.length() + 3;
		int i = -l;
		while (++i <= l)
			System.out.println(VectorString.FORMAT(testStr, i));
		double x = -IMeasurAble.PI*1000 + IMeasurAble.PI/100;
		do {
			int p = -4;
			while (++p < 4) {
	//			setFormatPrecision(p);
				System.out.println(VectorInt.FORMAT(p, -3) + //right aligned!
								   ByRefDouble.FORMAT(x, 7, p, 12, false) + "***");
			}
			x += IMeasurAble.PI*1000;
		} while (x < 4000);
		System.in.read(); System.in.read(); 
	}
	
	/**Tests the Formatting Routine	 */
	final static public void testIt() throws Exception {
		testParsing(); 
		testFormat2();
		testFormat();
	}
	
	/**Tests the Formatting Routine	 */
	final static public void main(final String[] args) throws Exception {
		testIt(); 
	}

}
