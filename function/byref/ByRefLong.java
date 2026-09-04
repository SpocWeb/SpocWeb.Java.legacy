package function.byref;

import java.security.InvalidParameterException;

import streamIO.Assert;
import streamIO.Log;
import function.AOrderAble;
import function.ICountAble;

/**
  * Title: ByRefShort<p>
  * Description:
  * This class is for transporting a long back from a Method Call.
  * It can also be used for generic Sorting Algorithms or as a Function,
  * since it implements OrderAble and ICountAble.
  * The Range of a 64 Bit Java Long is
  * -2^63	= -9223372036854775808 to
  *  2^63-1	=  9223372036854775807
  *
  * You can also simply use long[] to return Values from Method Calls.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-12-12, 01;52;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class ByRefLong
//	extends ConstCount //not possible in Java to make a protected Variable ('Value') public
//couldn't easily implement orderAble when derived from ConstCount!
extends AOrderAble// AOpLong
implements ICountAble {

	/** Logger Instance for this Class	*/
	private static final Log L = new Log(ByRefLong.class, 0); 

	///////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor */
	public ByRefLong(){ super(null); self = this; L.warn("Called empty Constructor"); }

	/**Initializing Constructor, just comfortable	 */
	public ByRefLong(long Value_) { super(null); self = this; Value = Value_; }

	/**This is the Value of the Object	 */
	public long Value;

	///////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public byte	 getByte() { //return Value; }
		byte Val  = (byte) Value;
		if  (Val !=        Value) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 16 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public short getShort() { //return Value; }
		short Val  = (short) Value;
		if   (Val !=         Value) throw new IllegalArgumentException();
		return Val; }

	/** Returns the Object Value represented by a 32 Bit Integer
	  * Unfortunately Java does not raise an Error on exceeding the Range	 */
	public int	 getInt() { //return Value; }
		int Val  = (int) Value;
		if (Val !=       Value) throw new IllegalArgumentException();
		return Val; }

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
	public String toString() { return Long.toString(Value); }
	
	//////////////////////////////////////////////////////
	//	Replication of the Methods in absMetricIRing	//
	//	to break recursive Referece.					//
	//////////////////////////////////////////////////////
	
	/**Calculates the Variation(n,k) = n!/(n-k)!
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * with considering Sequence.	 */
	final static public long Variation(final long n, final long k) {
		return VariCombi(n, k, null); }

	/**Recursive Calculation of the Combination kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1 for the Factorial
	 * and calculates the Variation and the Factorial of k.
	 * The Combination can be calculated by dividing the Variation
	 * by the Factorial of k.	 */
	final static public long VariCombi(final long n, long k, final ByRefLong faculty) {
		long prod = n;
		long vari = n;
		if (k == 0) {
			if (faculty != null) faculty.Value = 1;
			return 1; }
		if (faculty != null) faculty.Value = k;
		while (--k > 0) {
			prod*=--vari;				//multiplication with 1 unnecessary
			if (faculty != null) faculty.Value*=k;//multiplication with 1 unnecessary also!
		}
		return prod; }

	/**Calculates the Combination(n,k) = n!/(k!*(n-k)!)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITHOUT considering the Sequence and
	 * WITHOUT repeating Elements.
	 *
	 * This Calculation is optimized, because Comb(n, k) == Comb (n, n-k).	 */
	final static public long Combination(final long n, final long k) {
		long variation;
		ByRefLong Fact = new ByRefLong();
		if (n-k < k) variation = VariCombi(n, n - k, Fact);
		else		 variation = VariCombi(n,	  k, Fact);
		return variation/Fact.Value; }	//put here, because Java divided before the result in Fact came back! (Optimization?)
	
	/**Recursive Calculation of Factorial kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1	 */
	final static public long fact(final byte n) { return VariCombi(n, n, null); }
	
	//alternative Implementations:
/*	{
		long Prod = n;
		long Fact = n;
		while ((--Fact) > 0) Prod*=Fact;	//multiplication with 1 unnecessary
		return Prod;
	}

	{return  n*fact(n-1);}	//Recursive Solution
*/
	
	/**Recursive Calculation of Double Factorial kills the Stack.
	 * Iterative Solution, gives 0!! = 0, 1!! = 1	 */
	final static public long dblFact(final byte n)	{
		long factor  = n;
		long product = n;
		while ((factor-=2) > 0) 
			product*=factor;	//multiplication with 1 unnecessary
		return product; }
//	{return n*(dblFact(n-2));}	//Recursive Solution
	
	//////////////////////
	//	IOrderable	
	//////////////////////
	
	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (final Object arg) {
		if ((arg == null) ||
			(arg == this)) return false;
		return Value < ((ByRefLong)arg).Value; }

	/**Returns a hashcode for this Byte.	 */
	public int hashCode() { return (int)(Value ^ (Value >> 32)); }
	
	/**Compares this object to the specified object.
	 *
	 * @param obj	the object to compare with
	 * @return 		true if the objects are equivalent; false otherwise.
	 * @since   JDK1.1
	 */
	public boolean equals(final Object obj) {
		if (obj == null) return false; 
		if (obj == this) return  true; 
		return Value == ByRefLong.TO_LONG(obj); }
	
	final static public long CMPL(final long  ths, final int module) {
		if (module > 0) 
			return  ths - module; 
			return ~ths; }

	public long UPPER(final long mask, final long modByPeriod) {
		long ret;
		if (modByPeriod < 0) {
            ret = Value >> -modByPeriod; Value &=(mask-1); return ret; }
			ret = Value / mask;	         Value %= mask   ; return ret; }

	final static public long LOWER(final long value, final long mask, final long modByPeriod) {
		if (modByPeriod < 0) {
			return (value &(mask-1)) <<(-modByPeriod-1); } 	//this is exactly ModAtDivAt()!!!
			return (value % mask)     *  modByPeriod   ; }

	final static public long SHL (final long  ths){return ths << 1; }
	final static public long SHR (final long  ths){return ths >> 1; }
	final static public long SHL (final long  ths, final int module) { if (module < 0) return ths << -module; return ths* module; }
	final static public long SHR (final long  ths, final int module) { if (module < 0) return ths >> -module; return ths/ module; }
	final static public long ROR (long  ths, final long Mask, final long modByPeriod, int module){
		ths += LOWER(ths, Mask, modByPeriod); return SHR(ths, module); }
	
	public ByRefLong ROL_AT (final long mask, final long modByPeriod, final int module){
		Value = SHL(Value, module); Value += UPPER(mask, modByPeriod); return this; }
	
	/** @return the Value rotated left by 1  */
	final static public long ROL(long x, final int octave) { 
		final long maxVal = 1l << octave; 
		if((x <<= 1) > maxVal) 
			x -= maxVal-1;
		return x; 
	}
	
	/** @return the Value rotated right by 1  */
	final static public long ROR(final long x, final int octave) { 
		final long corr = (x &  1) << octave; 
		return    corr + (x >> 1); 
	}
	
	/** @return the Value with it's Bit Sequence reverted */
	final static public long REVERT(long x, final int octave) { 
		long ret = 0; 
		for(int i = octave; --i >= 0;) {
			ret <<= 1; 
			if(0 != (x & 1))
				++ret;
			x >>= 1; 
		}
		return ret; }
	
	////////////////////////////////////////////////////////////////////////////////
	//	ggT and kgV Algorithms
	////////////////////////////////////////////////////////////////////////////////
	
	/** Test for even integer Numbers x  */
	final static public boolean IS_EVEN (final long ths) { return (ths & 1) == 0; }
	
	/** Test for even integer Numbers x  */
	final static public boolean IS_ODD (final long ths) { return (ths & 1) == 1; }
	
	/**Calculates the smallest common Multiple of two numbers.
	 * kgV(a,b)=a*b/ggT(a,b)	 */
	final static public long KGV(final long ths, final long K2) {
		return ths*K2/ByRefLong.GGT_CLASSIC(ths, K2); 
	}	//don't need to use IntAt here, because the ggT divides
	
	/**Calculates the greatest common Divisor of two numbers,
	 * as well as the two factors a and b so that ggT(x, y) = a*x + b*y
	 * Uses the Archimedean Algorithm for this.	 */
	final static public long GGT_X(final long x, final long y, final long[] factors) {
		return GGT_X(x, y, factors, 0); 
	}	//don't need to use IntAt here, because the ggT divides
	
	/**Calculates the greatest common Divisor of two numbers,
	 * as well as the two factors a and b so that ggT(x, y) = a*x + b*y
	 * Uses the Archimedean Algorithm for this.	 */
	final static public long GGT_X(final long x, final long y, final long[] factors, final int index) {
		if  (y == 0) { 
			if (factors != null){
				factors[  index] = 1; 
				factors[1-index] = 0; } 
			return x; }
		final long q = x / y; 
		final long d = GGT_X(y, x - y*q, factors, 1-index);
		if (factors != null) 
			factors[1-index] -= q * factors[index]; //this prevents iterative Solution unless you introduce a Stack for the q-s
		return d; }
	
	/**Calculates the greatest common Divisor of two numbers.
	 * Uses the recursive Archimedean Algorithm for this,
	 * because that is apted for integer as well as for float Values.
	 * For integer Values the binary ggT is much faster.	 */
	final static public long GGT_CLASSIC(long x, long y) {
		while(y != 0) {
			final long mod = x % y; x = y; y = mod;
		}
		return x; 
		/* //recursive Implementations: 
		if  (y == 0)
			return x;
			return GGT_CLASSIC(y, x % y); 
		 */
	}
	
	/** binary O(lb N) ggT-Algorithm,
	  * On average faster than the regular euklidean Algorithm,
	  * but only usable for integer Numbers x and y  */
	final static public long GGT(long x, long y) {
		if (x < 0) x = -x; //{damit der ungerade-ungerade-Fall zu KLEINEREN Zahlen fuehrt}
		if (y < 0) y = -y; 
		int shift = 0; //1;
		while (x != y) {
			if (ByRefLong.IS_ODD(x))
				if (ByRefLong.IS_ODD(y)) { //both odd, i.e. x-y is even 
					final long diff = Math.abs(x-y) >> 1; //ggt(x,y) == ggt(x-y,y) == ggt(x, x-y)
					if (x > diff)
						x = diff; 
					else 
						y = diff; 
				} else
					y >>= 1; //ggt(x,y) == ggt(x, y/2) wenn x ungerade! 
			else { //x is even
				x >>= 1;
				if (ByRefLong.IS_EVEN(y)){
					y >>= 1; ++shift; // <<= 1; //ggt(x/2, y/2) = ggt(x,y)/2
				}
			}
		}
		return x << shift; }
	
	/**Returns the integer binary exponential function: 2^n = 1 << n	 */
	final static public long BXP(final byte n) { return BXP(n, 1); }

	/**Returns the integer binary exponential function: 2^n = 1 << n	 */
	final static public long BXP(final byte n, final long factor) { return factor << n; }

	/**Returns the integer binary logarithmic function: (int) log(2)n
	 * To receive the bounding binary Power, use xp2(lg2(n  )+1)
	 * If you expect binary Powers for n, use    xp2(lg2(n-1)+1)   */
	final static public byte LB(long n) {
		if (n <= 0) {
			if (n == 0) 
				return Byte.MIN_VALUE; 
			n = -n; }
		byte lg = 0;
		while ((n >>= 1) != 0) 
			 ++lg;
		return lg; }
	
	/**Defines the general integer Log Function for long Numbers	
	 * 
	 * @param x the Value to calculate the integer Logarithm of
	 * @param base the Base of the Logarithm
	 * @return the Logarithm for the given Base
	 */
	final static public byte LOG(long x, final int base) {
		if (x <= 1) { //nest the Checks to minimize Execution Time
			if (x <= 0) { //or perform the most frequent Test first. 
				if (x == 0) {
					return Byte.MIN_VALUE + 1;
				}
				throw new InvalidParameterException("Logarithm not defined for negative Values like "+x); 
			} 
			return 0; 
		} 
		byte ret = 0; 
		while((x /= base) > 0) {
			 ++ret; 
		}
		return ret; 
	}
	
	/**Defines the general Power Function Cbc for int	 */
	final static public long POW(final long x, final byte pow) { return POW(x, pow, 1); }
	
	/**Defines the general Power Function for int	 */
	final static public long POW(final long x, byte pow, final long factor) {
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
		long prod = ((pow &  1) != 0)? x*factor : factor;	//could save one Multiplication in the loop
		long x_2n = x;
		//First Implementation: Use the Horner Scheme in the Exponent.
		for(;;){
			x_2n *= x_2n;	//you can save a SQR in the end by skipping this
			if (((pow >>= 1) & 1) != 0)	//(N1.odd())
				prod *= x_2n;	//you could save a multiplication in the beginning here
			else if (pow == 0)
				break; 
		} //(! N1.halfAt().IntAt().equals(mZERO))
		return prod; }

	///////////////////////////////////////////////////////////////////////////
	/// Conversion & Parsing Methods
	///////////////////////////////////////////////////////////////////////////
	
	/**This class does not extend Number, because not every Group maps to numeric Values.
	 * Instead it presents the conversion Routine to convert from Number Types.
	 */
	final static public long TO_LONG(final Object arg) {
		//The following also works, but is clumsier.
//		return arg.getClass().isAssignableFrom(countable.class)?
		return  arg instanceof ICountAble?
				((ICountAble)arg).getLong():
				arg instanceof Character ?
				((Character)arg).charValue() :
				((Number)	arg).longValue(); }
	
	/** 
	 * tries to parse the given String into a Long Number 
	 * @param arg the String to parse
	 * @return the value read, Long.MIN_VALUE if it could not be read. 
	 */
	final static public long TRY_PARSE(final String arg) {
		final long[] defaultValue = { Long.MIN_VALUE }; 
		TRY_PARSE(arg, 0, 10, defaultValue);
		return defaultValue[0]; 
	}
	
	/** 
	 * tries to parse the given String into a Long Number 
	 * @param arg the String to parse
	 * @param radix the Radix of the Number Representation 
	 * @param defaultValue the Default Value, also used to return the Result  
	 * @return the first invalid Character Position 
	 */
	final static public int TRY_PARSE(final String arg, final long[] defaultValue) {
		return TRY_PARSE(arg, 0, 10, defaultValue); 
	}
	
	/** 
	 * tries to parse the given String into a Long Number 
	 * @param arg the String to parse
	 * @param radix the Radix of the Number Representation 
	 * @param defaultValue the Default Value, also used to return the Result  
	 * @return the first invalid Character Position 
	 */
	final static public int TRY_PARSE(final String arg, final int radix, final long[] defaultValue) {
		return TRY_PARSE(arg, 0, radix, defaultValue); 
	}
	
	/** 
	 * tries to parse the given String into a Long Number 
	 * @param arg the String to parse
	 * @param pos the Position in the String to parse from
	 * @param radix the Radix of the Number Representation 
	 * @param defaultValue the Default Value, also used to return the Result  
	 * @return the first invalid Character Position 
	 */
	final static public int TRY_PARSE(final String arg, final int init, final int radix, final long[] defaultValue) {
		if (arg == null)
			return -1; 
		int pos = init; 
		long result = 0; 
		//skip initial White Space
		for(;ByRefChar.WHITESPACE.indexOf(arg.charAt(pos)) > 0; ++pos);
		//read the Sign
		boolean positive; 
		switch (ByRefChar.PLUS_MINUS.indexOf(arg.charAt(pos))) {
		case -1: --pos; //positive = true; break; //falls through on purpose!
		case  0: positive = true; break;
		case  1: positive = false; break;
		default: throw new RuntimeException("Should never happen!"); 
		}
		final int start = pos; 
		final int stopp = arg.length(); 
		for(; ++pos < stopp; ) {
			final int val = ByRefChar.VALUE_OF(arg.charAt(pos)); 
			if ((val < 0) || (val >= radix)) 
				break; 
			result = result*radix + val; //use Horner Schema 
		}
		if (!positive)
			result = -result; 
		if (start+1 < pos) {
			defaultValue[0] = result; 
			return pos; 
		}
		return init; //don't skip WhiteSpace! 
	}
	
	///////////////////////////////////////////////////////////////////////////////
	// Static Testing & Main Methods
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * tests all Methods of this Class
	 */
	final static public void testLogExp() {
		final int x =5; 
		for(byte i = 10; --i >= 0;) {
			Assert.EQUALS(i, LOG(POW(x, i),x)); 
			Assert.EQUALS(i, LB(BXP(i))); 
		}
	}
	
	/** tests all Methods of this Class	 */
	public static void testIt() {
		testGgt(); 
		testLogExp(); 
	}
	
	/** tests all Methods of this Class	 */
	public static void testGgt() {
		final long[] factors = new long[2]; 
		for(int i = 1000; --i >= 0;) {
			final int x = (int) (1 + 100*Math.random()); 
			final int y = (int) (1 + 100*Math.random());
			final long ggt = GGT_CLASSIC(x, y); 
			Assert.EQUALS(ggt, GGT  (x, y)); 
			Assert.EQUALS(ggt, GGT_X(x, y, null)); 
			Assert.EQUALS(ggt, GGT_X(x, y, factors)); 
			Assert.EQUALS(ggt, x*factors[0]+y*factors[1]); 
		}
	}
	
	/** Main Method 
	 * 
	 */
	public static void main(final String[] args) {
		if (args.length == 0)
			testIt(); 
	}
	
}
