package function.byref;

import streamIO.Assert;
import function.AOrderAble;
import function.ICountAble;
import function.IMeasurAble;

/**
  * Title: ByRefFloat<p>
  * Description:
  * This class is for transporting a float back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  *
  * Float-Properties:
  * Bits:     32 = 8*4Byte
  * Mantissa: 23 ~ 8*3Byte  => 53 Bits ^ 7 Digits Accuracy
  * Exponent:  8 = 8*1Byte  => 11 Bits ^ +/- 38 Exponent
  * Sign:      1 =   1 Bit
  * abs.Range: 1.5e-45 to 3.4028235e+38
  *
  * @see Function.IMeasurAble
  *
  * You can also simply use float[] to return Values from Method Calls!
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
  * mtime: 2026-09-05T20:51:58Z
  * digest: 3a0fe22df57a6fde789287e94fdd755b89b500315c41793aa99c519dbfad0717
  * stale: false
  * tags: [code/function_wrapper, code/mathematical_constants]
  * concepts: [By-Reference Primitive Wrapper]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
final public class ByRefFloat
extends AOrderAble
implements ICountAble { //IMeasurAble { //

	/**Empty Constructor */
	public ByRefFloat() { super(null); self = this; }

	/**Initializing Constructor, just comfortable	 */
	public ByRefFloat(float Value_) { this(); Value = Value_; }

	/**This is the Value of the Object	 */
	public float Value;

	/** Accuracy used as Criterion for Convergence	*/
	public static float  FloatAccuracy =  IMeasurAble.FLOAT_ACCURACY;

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public byte	 getByte() {
		byte Val  = (byte) Value;
		if  (Val !=        Value) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 16 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public short getShort() {
		short Val  = (short) Value;
		if   (Val !=         Value) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 32 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public int	 getInt() {
		int Val  = (int) Value;
		if (Val !=       Value) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 64 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public long  getLong() {
		long Val  = (long) Value;
		if  (Val !=        Value) throw new IllegalArgumentException();
		return Val; }

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
	public String toString() { return Float.toString(Value); }

	//////////////////////
	//  IOrderable
	//////////////////////

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg) {
		if ((arg == null) ||
			(arg == this)) return false;
		return Value < ByRefFloat.getFloat(arg); } //((ByRefFloat)arg).Value;}

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() { return Float.floatToIntBits(Value); }

	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1
	 */
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return  true;
		return Value == ByRefFloat.getFloat(obj); }

	/**Absolute Value Multiplication by the Accuracy.
	 * Should be parameterized by an adjustale Accuracy. 	 */
	final static public float  mulAbsAccuracy(float  arg) { return Math.abs(arg) * ByRefFloat.FloatAccuracy; }

	/**Multiplication by the Accuracy.
	 * Should be parameterized by an adjustale Accuracy. 	 */
	final static public float  mulAccuracy(float  arg) { return arg * ByRefFloat.FloatAccuracy; }

	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 * @throws NumberFormatException if a String was to be converted!
	 */
	final static public float getFloat(final Object arg)
		throws NumberFormatException {
		if (arg == null) {
			return Float.NaN; }
		if (arg instanceof IMeasurAble) {
			return ((IMeasurAble)arg).getFloat(); }
		if (arg instanceof String) {
			if (((String) arg).length() == 0) {
			return Float.NaN; }
			return Float.parseFloat((String) arg); } //throws NumberFormatException
			return ((Number)    arg).floatValue(); }

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods for scalar (1Dim) Values:
	////////////////////////////////////////////////////////////////////////////////
	
	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	final static public boolean EQUALS(final float value, final Object arg) {
		if (arg == null)
			return false;
		return EQUALS(value, ByRefFloat.getFloat(arg));
	}

	/** Tests whether the given value is zero 
	 * compared to the specified value 
	 *
	 * @param value the value to test
	 * @param comparedTo the value to compare to
	 * @return 		true value is considerably smaller in Magnitude than comparedTo; false otherwise.
	 */
	final static public boolean IS_ZERO(final float value, final float comparedTo) {
		if (Math.abs(value/comparedTo) < IMeasurAble.FLOAT_ACCURACY) {
			//if Value and argVal are similar, they have the same Sign, 
			//i.e. |v-a|/|v+a|=||v|-|a||/||v|+|a|| < 1 with single Minimum  
			//if they are unsimilar, they may have even oposite Sign
			//i.e. |v-a|/|v+a|=||v|+|a||/||v|-|a|| > 1 
			return true; 
		} else { //check the (not so rare) 0/0 case
			return value == 0;
		}
	}
	
	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1	 */
	final static public boolean EQUALS(final float value, final float argVal) {
		return IS_ZERO(value - argVal, value + argVal); }
	
	/** Returns the relative Position of x to y: +1 if x&lt;y, -1 if y&lt;x, 0 if equal.
	 * @return the Sign of x-y  */
	final static public byte POSITION(final float x, final float y) {
		return (byte) (
			(x < y) ?  1 :
			(y < x) ? -1 : 0); }

	/** Returns the smaller of x and y.
	 * @return the Minimum of both Values */
	final static public float MIN(final float x, final float y) {
		return (x < y) ? x : y; }

	/** Returns the larger of x and y.
	 * @return the Maximum of both Values */
	final static public float MAX(final float x, final float y) {
		return (x > y) ? x : y; 
	}

	/**Defines the Sqr for simple Types	 */
	final static public float SQR(final float x) {
		return x*x;
	}

	/**Defines the Cbc for simple Types	 */
	final static public float Cbc(float x) {
		return x*x*x;
	}

	/**Returns the Sign of x as an integer, i.e.
	 * -1 for negative x
	 *  0 for x == 0
	 * +1 for positive x	 */
	final static public int Sign(float x) {
		return (x > 0) ? 1 : (x < 0) ? -1 : 0;
	}

	/**Checks if the Interval contains x.
	 * This Implementation is unsymmetric, always a[0] < a[1] assumed
	 * there is no fast correct Solution, only Compromises !}	 */
	final static public boolean contains(float x, float left, float right) {
		return (left <= x) ^ (right < x);
	}

	/** Combines the magnitude of a with the sign of b.
	 * @param a
	 * @param b
	 * @return a with the Sign of b
	 */
	final static public float assignSign(final float a, final float b) {
		final boolean aIsPositive = (a >= 0); 
		return (b >= 0) ? aIsPositive?a:-a : aIsPositive?-a:a; }

	/** 
	 * calculate (a^2+b^2)^{1/2} without overflow (2.6)
	 * Helper calculates the euklidean Norm of a 2Dim Vector, Chapter 
	 * 
	 * @param a
	 * @param b
	 * @return (a^2+b^2)^{1/2} 
	 */	
	final static public float Norm(final float a, final float b) {
		return (float) Math.sqrt(a*a+b*b);
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Writes the cosine and sine of x into CosSin[0] and CosSin[1].
	 * @return the Sinus and Cosinus of x in Place	 */
	final static public void CosSin(final double x, final float[] CosSin) {
		CosSin(x, CosSin, 0, 1);
	}

	/** Reduces x into [-Pi,Pi] and writes its cosine/sine into CosSin at the given indexes.
	 * @return the Sinus and Cosinus of x in Place	 */
	final static public void CosSin(double x, final float[] CosSin, final int cos, final int sin) {
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
		ByRefFloat.CosSinSafe(x, CosSin, cos, sin);
		if (!neg) {
			return; 
		}
		CosSin[sin] = -CosSin[sin];
	}
	
	/** Writes the cosine and sine of x (already in [-Pi,Pi]) into CosSin[0] and CosSin[1].
	 * @param SinCos the Sinus and Cosinus of x in Place
	 * @param x is expected to be in the Range of -Pi..Pi
	 */
	final static public void CosSinSafe(final double x, final float[] CosSin) {
		CosSinSafe(x, CosSin, 0, 1);
	}

	/** Writes the cosine and sine of x (already in [-Pi,Pi]) into CosSin at the given indexes.
	 * @param SinCos the Sinus and Cosinus of x in Place
	 * @param x is expected to be in the Range of -Pi..Pi
	 */
	final static public void CosSinSafe(final double x, final float[] CosSin, final int cos, final int sin) {
		final double c; 
		CosSin[cos] = (float) (c = Math.cos(x));
		if (x > 0) {
			CosSin[sin] = (float) +Math.sqrt(ICountAble.ONE - c * c);
		} else {
			CosSin[sin] = (float) -Math.sqrt(ICountAble.ONE - c * c);
		}
	}	

	/////////////////////////////////////////////////////////////////////////////////////
		
	/**Returns a random Number Offset <= x < Offset + Range	 */
	final static public float random(double Offset, double Range) {
		return (float) (Offset + Math.random() * Range); }

	/**Returns a random Number 0 <= x < Range	 */
	final static public float random(double Range) {
		return (float) (Math.random() * Range); }

	/** Returns a random Number between 1 and -1
	  * Used in @see randomizeWeights()
	  */
	public static float Random1_1() {
		final double tmp = Math.random();
		return (float) (tmp + tmp - 1);
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws Exception {
		System.out.println("Testing " + ByRefFloat.class.getName());
		Assert.IS_TRUE(IS_ZERO(0,1));
		Assert.IS_TRUE(IS_ZERO(0,0));
		Assert.IS_TRUE(!IS_ZERO(1e-20f,0));
		Assert.IS_TRUE(!EQUALS(1,.8f));
		Assert.IS_TRUE(EQUALS(1,1));
		Assert.IS_TRUE(EQUALS(0,0));
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(args); }
	
}
