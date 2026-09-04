/*
 * Created on 11.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;

import streamIO.Log;
import tools.IOError;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;

/**
 * Title: <p>
 * Description:
 * Collects all Locale-specific Settings for primitive Types. 
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public class LocalePrimitive {
	
	/** Offset to correct for Rounding Errors in the Log Function 	 */
	final static double ROUND_OFFSET = 4*ByRefDouble.DOUBLE_FULL_ACCURACY; 
	
	/** the Default Radix for Number Representations 	 */
	final static public char DEFAULT_BASE = (char) 10; 
	
	/** Natural Logarithm of the Default Base	 */
	final static public double DEFAULT_LN_BASE = ByRefDouble.LN10; //not initialized yet... 
	
	/** Character to indicate no Output! 	 */
	final static public char CHR_NONE = 0; 
	
	/** Default Character to fill the Output for fixed Size 
	 * The Problem with using ' ' is that it has a different Length in proportional Fonts,
	 * so using '_' for padding is preferred.	 */
	final static public char DEFAULT_CHR_PAD	= ' ';
	
	/** Default Character to separate the Integer from the Fraction	
	 * The Problem with using ',' is that it is mistaken for a List Separator.	 */
	final static public char DEFAULT_CHR_DOT	= '.';
	
	/** Default Character to separate the Mantissa from the Exponent	 */
	final static public char DEFAULT_CHR_EXP = 'E'; 
	
	/** Default Character to indicate non-negative Values, only used if not 0 	 */
	final static public char DEFAULT_CHR_PLUS = '+'; //0; 
	
	/** Default Character to indicate negative Values 	 */
	final static public char DEFAULT_CHR_MINUS = '-';
	
	/** Default Character to structure the leading Digits of the Mantissa
	 * The Problem with using ',' is that it is mistaken for a List Separator.	 */
	final static public char DEFAULT_CHR_GROUP = ' '; //0; //','; 
	
	/** Allows to represent 0 with an empty String when 0 	 */
	final static public char DEFAULT_MIN_LENGTH = (char) 1; 
	
	/** Pre-calculated Factor for full Precision with the Default Base.  	 */
	final static public byte DEFAULT_PRECISION = (byte) 
		(ByRefDouble.DOUBLE_MANTISSA_BITS*ByRefDouble.LN2/Math.log(DEFAULT_BASE)); 
	
	/** Pre-calculated Factor for full Precision with the Default Base.  	 */
	final static public long DEFAULT_PRECISION_FACTOR = ByRefLong.POW(DEFAULT_BASE, DEFAULT_PRECISION); 
	
	/** Default String Constant to represent 'true'	 */
	public static String DEFAULT_STR_TRUE  = "true";
	
	/** Default String Constant to represent 'false'	 */
	public static String DEFAULT_STR_FALSE = "false";
	
	///////////////////////////////////////////////////////////////////////////
	/// Handling Lists requires an Escape Mechanism
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Character Constant to represent 	 */
	public static char DEFAULT_CHR_ESCAPE = '\\';
	
	/** Default Character Constant to represent 	 */
	public static char DEFAULT_CHR_SEP = '\t';
	
	/** Default Character Constant to represent 	 */
	public static char DEFAULT_CHR_BRACKET_OPEN = '{';
	
	/** Default Character Constant to represent 	 */
	public static char DEFAULT_CHR_BRACKET_CLOSE = '}';
	
	///////////////////////////////////////////////////////////////////////////
	
	private static final Log L = new Log(LocalePrimitive.class); 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG_SAFE(final IStreamOutByte stream
			, final long b, final char base
			, final char minLen) {
		try { ADD_LONG(stream, b, base, 0, DEFAULT_CHR_DOT, minLen, '0'); 
		} catch (final IOException x) {
			throw new IOError(x);
		}
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG_SAFE(final IStreamOutByte stream
			, final long b, final char base
			, final int dotPos, final char chrDot
			, final char minLen, final char chrPad
			, final char chrMinus, final char chrPlus
			, final char chrGroup) {
		try { ADD_LONG(stream, b, base, dotPos, chrDot, minLen, chrPad
				, chrMinus, chrPlus, chrGroup); 
		} catch (final IOException x) {
			throw new IOError(x);
		}
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, DEFAULT_CHR_DOT); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, DEFAULT_PRECISION); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot 
				, minLen, DEFAULT_CHR_PAD); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot 
				, minLen, chrPad, DEFAULT_BASE); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot 
				, minLen, chrPad, base, DEFAULT_CHR_MINUS); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot 
				, minLen, chrPad, base, chrMinus, DEFAULT_CHR_PLUS); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus , final char chrPlus
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot 
				, minLen, chrPad, base, chrMinus, chrPlus, DEFAULT_CHR_EXP); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus , final char chrPlus
			, final char chrExp
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot 
				, minLen, chrPad, base, chrMinus, chrPlus, chrExp, DEFAULT_CHR_GROUP); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus , final char chrPlus
			, final char chrExp , final char chrGroup
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot 
				, minLen, chrPad, base, chrMinus, chrPlus, chrExp, chrGroup, Math.log(base)); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus , final char chrPlus
			, final char chrExp , final char chrGroup
			, final double lnBase
	) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot 
				, minLen, chrPad, base, chrMinus, chrPlus, chrExp, chrGroup, lnBase, null); }
	
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, final double x
			, final char base) throws IOException {
		final double lnBase = Math.log(base); 
		final byte precision = (byte) (ByRefDouble.DOUBLE_MANTISSA_BITS*ByRefDouble.LN2/lnBase); 
		ADD_DOUBLE_SCIENTIFIC(stream, x, precision, DEFAULT_CHR_DOT, DEFAULT_MIN_LENGTH
				, DEFAULT_CHR_PAD, base, DEFAULT_CHR_MINUS, DEFAULT_CHR_PLUS 
				, DEFAULT_CHR_EXP, DEFAULT_CHR_GROUP, lnBase, null);
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SCIENTIFIC(final IStreamOutByte stream, double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus , final char chrPlus
			, final char chrExp , final char chrGroup
			, final double lnBase, char[] buf
	) throws IOException {
		if (buf == null)
			buf = new char[64]; 
		final double xp = Math.log(Math.abs(value))/lnBase+ROUND_OFFSET; 
		int exp = ((int) xp);
		if (exp < 0)
			--exp; 
		//else ++exp; 
		/*
		while(x < base) {
			x*=base; ++exp; }
		while(x > base) {
			x/=base; ++exp; }
		*/	
		value*=Math.exp((precision-exp)*lnBase); //*precisionFactor; 
		ADD_LONG(stream, Math.round(value), base, precision, chrDot, minLen, chrPad, chrMinus, chrPlus, chrGroup, buf); 
		stream.write(chrExp); 
		ADD_LONG(stream, exp, base, -1, chrDot, (char) 1, chrPad, chrMinus, chrPlus, chrGroup, buf);  
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SAFE(final IStreamOutByte stream, final double value) {
		try { ADD_DOUBLE(stream, value, DEFAULT_PRECISION, DEFAULT_CHR_DOT
				, DEFAULT_MIN_LENGTH, DEFAULT_CHR_PAD, DEFAULT_BASE, DEFAULT_CHR_MINUS
				, DEFAULT_CHR_PLUS, DEFAULT_CHR_EXP, DEFAULT_CHR_GROUP, false, DEFAULT_PRECISION_FACTOR); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream, final double value) throws IOException {
		ADD_DOUBLE(stream, value, DEFAULT_PRECISION, DEFAULT_CHR_DOT, DEFAULT_MIN_LENGTH
				, DEFAULT_CHR_PAD, DEFAULT_BASE, DEFAULT_CHR_MINUS, DEFAULT_CHR_PLUS
				, DEFAULT_CHR_EXP, DEFAULT_CHR_GROUP, false, DEFAULT_PRECISION_FACTOR); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision //negative Precision rounds to integer Numbers, also determines the Dot Position!
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, DEFAULT_CHR_DOT); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, (char)1); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen 
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, DEFAULT_CHR_PAD); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, DEFAULT_BASE);
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, base
				, DEFAULT_CHR_MINUS);
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, base
				, chrMinus, DEFAULT_CHR_PLUS);
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus, final char chrPlus
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, base
				, chrMinus, chrPlus, DEFAULT_CHR_EXP);
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus, final char chrPlus
			, final char chrExp
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, base
				, chrMinus, chrPlus, chrExp, DEFAULT_CHR_GROUP);
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus, final char chrPlus
			, final char chrExp, final char chrGroup
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, base
				, chrMinus, chrPlus, chrExp, chrGroup, false);
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus, final char chrPlus
			, final char chrExp, final char chrGroup
			, final boolean zeroOnUnderflow 
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, base
				, chrMinus, chrPlus, chrExp, chrGroup, zeroOnUnderflow 
				, ByRefLong.POW(base, precision));
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE_SAFE(final IStreamOutByte stream
			, final double value 
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus, final char chrPlus
			, final char chrExp, final char chrGroup
			, final boolean zeroOnUnderflow  
			, final long precisionFactor 
			) {
		try { ADD_DOUBLE(stream, value , precision, chrDot, minLen, chrPad, base
				, chrMinus, chrPlus, chrExp, chrGroup
				, zeroOnUnderflow, precisionFactor); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value 
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus, final char chrPlus
			, final char chrExp, final char chrGroup
			, final boolean zeroOnUnderflow 
			, final long precisionFactor 
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, base
				, chrMinus, chrPlus, chrExp, chrGroup, zeroOnUnderflow, precisionFactor, null); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value 
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus, final char chrPlus
			, final char chrExp, final char chrGroup
			, final boolean zeroOnUnderflow 
			, final long precisionFactor 
			, char[] buf
			) throws IOException {
		ADD_DOUBLE(stream, value, precision, chrDot, minLen, chrPad, base
				, chrMinus, chrPlus, chrExp, chrGroup, zeroOnUnderflow, precisionFactor, buf, Math.log(base)); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_DOUBLE(final IStreamOutByte stream
			, final double value 
			, final byte precision, final char chrDot //negative Precision rounds to integer Numbers, also determines the Dot Position!
			, final char minLen, final char chrPad
			, final char base
			, final char chrMinus, final char chrPlus
			, final char chrExp, final char chrGroup
			, final boolean zeroOnUnderflow 
			, final long precisionFactor 
			, char[] buf
			, final double lnBase
			) throws IOException {
		if (buf == null)
			buf = new char[64]; 
		long b = Math.round(value*precisionFactor); 
		int dotPos = precision+2; 
		for(long q; (--dotPos > 0) && (b == (q = b/base)*base); ) 
			b = q; //remove trailing 0s 
		if  ((b == Long.MAX_VALUE) ||
			 (b == Long.MIN_VALUE) ||
			((b == 0) && (value != 0) && !zeroOnUnderflow) || //when a Number under- 
			((minLen > 1) && (Math.abs(b) > (1 << minLen))) //or over-flows the given Precision...
			) { ADD_DOUBLE_SCIENTIFIC(stream, value, precision, chrDot, minLen, chrPad, chrExp, base, chrMinus, chrPlus, chrGroup, lnBase, buf); //...switch to scientific Notation
			return; }
		ADD_LONG(stream, b, base, dotPos, chrDot, minLen, chrPad, chrMinus, chrPlus, chrGroup, buf); 
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG_SAFE(final IStreamOutByte stream, final long b) {
		try { ADD_LONG(stream, b);
		} catch(final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream, final long b) throws IOException {
		ADD_LONG(stream, b, DEFAULT_BASE); 
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, final long b, final char base) throws IOException {
		ADD_LONG(stream, b, base, -1); 
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, final long b, final char base
			, final int dotPos) throws IOException {
		ADD_LONG(stream, b, base, dotPos, DEFAULT_CHR_DOT); 
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, final long b, final char base
			, final int dotPos, final char chrDot) throws IOException {
		ADD_LONG(stream, b, base, dotPos, chrDot, DEFAULT_MIN_LENGTH); 
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, final long b, final char base
			, final int dotPos, final char chrDot
			, final char minLen) throws IOException {
		ADD_LONG(stream, b, base, dotPos, chrDot, minLen, DEFAULT_CHR_PAD); 
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, final long b, final char base
			, final int dotPos, final char chrDot
			, final char minLen, final char chrPad) throws IOException {
		ADD_LONG(stream, b, base, dotPos, chrDot, minLen, chrPad, DEFAULT_CHR_MINUS); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, final long b, final char base
			, final int dotPos, final char chrDot
			, final char minLen, final char chrPad
			, final char chrMinus) throws IOException {
		ADD_LONG(stream, b, base, dotPos, chrDot, minLen, chrPad, 
				chrMinus, DEFAULT_CHR_PLUS); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, final long b, final char base
			, final int dotPos, final char chrDot
			, final char minLen, final char chrPad
			, final char chrMinus, final char chrPlus) throws IOException {
		ADD_LONG(stream, b, base, dotPos, chrDot, minLen, chrPad, 
				chrMinus, chrPlus, DEFAULT_CHR_GROUP); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, final long b, final char base
			, final int dotPos, final char chrDot
			, final char minLen, final char chrPad
			, final char chrMinus, final char chrPlus
			, final char chrGroup) throws IOException {
		ADD_LONG(stream, b, base, dotPos, chrDot, minLen, chrPad, 
				chrMinus, chrPlus, chrGroup, null); }
	
	/** Flag whether to prepend the dot with 0
	 * replaced by the Criterion chrPad != 0	 */
	//public static boolean leadingZero; 
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final static public void ADD_LONG(final IStreamOutByte stream
			, long b, final char base
			, final int dotPos, final char chrDot
			, final char minLen, final char chrPad 
			, final char chrMinus, final char chrPlus
			, final char chrGroup, char[] arr) throws IOException {
		if (arr == null)
			arr = new char[65]; 
		int pos = arr.length-dotPos; //Position of the dot
		if ((b <  0)) { 
			stream.write(chrMinus); b = -b; 
		} else if(b == 0) {
			if (minLen != 0) 
				stream.write('0'); 
			return; 
		} else 
			if(chrPlus != 0)
				stream.write(chrPlus);
		int i = ADD_LONG_POS(arr, b, base)-1; //chrMinus, chrPlus, minLen == 0)-1; 
		for(int j = minLen-arr.length+i; --j >= 0;)
			stream.write(chrPad); 
		if (i > pos) {
			if (chrPad != 0) //also determines whether to use a leading Zero! 
				stream.write('0'); 
			stream.write(chrDot); 
			for(int pad = pos;i >= ++pad;)
				stream.write('0'); 
		}
		for(;++i < arr.length; ) {
			if (i == pos) 
				stream.write(chrDot); 
			else if((chrGroup != 0) &&
				(pos == i-3)) {
				 pos  = i; 
				stream.write(chrGroup);
			}
			stream.write(arr[i]); 
		}
	}
	
	/** 
	 * writes the given Number b backwards into the given Character Array arr 
	 * @param arr the Array to write into
	 * @param base the Base to write the Number in
	 * @param b the Number to write 
	 * @return the Starting Position of the Number in the Array
	 */
	final static public int ADD_LONG(final char[] arr, long b) {
		return ADD_LONG(arr, b, DEFAULT_BASE); }
	
	/** 
	 * writes the given Number b backwards into the given Character Array arr 
	 * @param arr the Array to write into
	 * @param base the Base to write the Number in
	 * @param b the Number to write 
	 * @return the Starting Position of the Number in the Array
	 */
	final static public int ADD_LONG(final char[] arr, long b, final char base) {
		return ADD_LONG(arr, b, base, DEFAULT_CHR_MINUS); }
	
	/** 
	 * writes the given Number b backwards into the given Character Array arr 
	 * @param arr the Array to write into
	 * @param base the Base to write the Number in
	 * @param b the Number to write 
	 * @return the Starting Position of the Number in the Array
	 */
	final static public int ADD_LONG(final char[] arr, long b
			, final char base, final char chrMinus) {
		return ADD_LONG(arr, b, base, chrMinus, DEFAULT_CHR_PLUS); }
	
	/** 
	 * writes the given Number b backwards into the given Character Array arr 
	 * @param arr the Array to write into
	 * @param base the Base to write the Number in
	 * @param b the Number to write 
	 * @return the Starting Position of the Number in the Array
	 */
	final static public int ADD_LONG(final char[] arr, long b
			, final char base, final char chrMinus, final char chrPlus) {
		return ADD_LONG(arr, b, base, chrMinus, chrPlus, false); }
	
	/** 
	 * writes the given Number b backwards into the given Character Array arr 
	 * @param arr the Array to write into
	 * @param base the Base to write the Number in
	 * @param b the Number to write 
	 * @return the Starting Position of the Number in the Array
	 */
	final static public int ADD_LONG(final char[] arr, long b
			, final char base, final char chrMinus, final char chrPlus, final boolean emptyOnZero) {
		return ADD_LONG(arr, b, base, chrMinus, chrPlus, emptyOnZero, arr.length); }
	
	/** 
	 * writes the given Number b backwards into the given Character Array arr 
	 * @param arr the Array to write into
	 * @param firstPos the Position to end writing into 
	 * @param b the Number to write 
	 * @param base the Base to write the Number in
	 * @return the Starting Position of the Number in the Array
	 */
	final static public int ADD_LONG(final char[] arr
			, long b, final char base
			, final char chrMinus, final char chrPlus
			, final boolean emptyOnZero, int firstPos) {
		final boolean neg;
		if (neg = (b <  0)) 
			b = -b; 
		if((b == 0) && !emptyOnZero) {
			arr [--firstPos] = '0'; 
			return firstPos; 
		}
		firstPos = ADD_LONG_POS(arr, b, base, firstPos);
		if (neg) 
			arr[--firstPos] = chrMinus; 
		else if(chrPlus != 0)
			arr[--firstPos] = chrPlus;
		return firstPos; 
	}
	
	/** adds a positive long Number to the given char Array.  
	 * @param arr the Array to write into
	 * @param firstPos the Position to end writing into 
	 * @param b the Number to write 
	 * @param base the Base to write the Number in
	 * @return the Starting Position of the Number in the Array
	 */
	private static int ADD_LONG_POS(final char[] arr, final long b, final char base) {
		return ADD_LONG_POS(arr, b, base, arr.length); }
	
	/** adds a positive long Number to the given char Array.  
	 * @param arr the Array to write into
	 * @param firstPos the Position to end writing into 
	 * @param b the Number to write 
	 * @param base the Base to write the Number in
	 * @return the Starting Position of the Number in the Array
	 */
	private static int ADD_LONG_POS(final char[] arr, long b, final char base, int firstPos) {
		for(long q; (q = b) > 0; ) {
			final char r = (char) (q -(b /= base)*base);
			arr[--firstPos] =  (char) (r + ((r < 10) ? '0' : 'A')); //use capital Chars... 
		} //...since they are smaller than 128 and thus unencoded.  
		return firstPos;
	}
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	
	/** The Number Base to represent the Numbers in	 */
	final public char base; 
	
	/**Precision for Rounding	 */
	final public double lnBase;	
	
	/**Precision for Rounding	 */
	final public byte precision;	
	
	/**Precision Factor for Rounding	 */
	final public long precisionFactor;
	
	/** Character for Padding Digits for fixed Size Output	
	 * The Problem with using ' ' is that it has a different Length in proportional Fonts.	 
	 * The Criterion chrPad != 0 is also used to control 
	 * whether Float Numbers between 0 and 1 have a leading 0 or start with '.' */
	public char chrPad = DEFAULT_CHR_PAD;
	
	/** Character to separate the Integer Part from the Fraction	
	 * The Problem with using ',' is that it is mistaken for a List Separator.	 */
	public char chrDot = DEFAULT_CHR_DOT;
	
	/** Character to separate the Mantissa from the Exponent	 */
	public char chrExp = DEFAULT_CHR_EXP; 
	
	/** Character to indicate negative Values 	 */
	public char chrMinus = DEFAULT_CHR_MINUS; 
	
	/** Character to indicate non-negative Values, only used if not 0 	 */
	public char chrPlus = CHR_NONE; //DEFAULT_CHR_PLUS;
	
	/** Character for Grouping Digits for better Readability 
	 * The Problem with using ',' is that it is mistaken for a List Separator.	 */
	public char chrGroup = CHR_NONE; //DEFAULT_CHR_GROUP;
	
	/** Allows to represent 0 with an empty String when 0 	 */
	public char minLength = DEFAULT_MIN_LENGTH; 
	
	/** Flag to output Signs also for non-negative Values	 */
	public boolean alwaysSign;
	
	/** Flag to output 0 for Fractions smaller than the desired Precision	 */
	public boolean zeroOnUnderflow;
	
	/** String Constants to represent 'false' and 'true'	 */
	final public String[] strBool = { DEFAULT_STR_FALSE, DEFAULT_STR_TRUE};
	
	///////////////////////////////////////////////////////////////////////////
	/// Handling Structures and Lists requires an Escape Mechanism
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Character Constant to represent 	 */
	public char chrEscape= DEFAULT_CHR_ESCAPE;
	
	/** Default Character Constant to represent 	 */
	public char chrSep = DEFAULT_CHR_SEP;
	
	/** Default Character Constant to represent 	 */
	public char chrBracketOpen = DEFAULT_CHR_BRACKET_OPEN;
	
	/** Default Character Constant to represent 	 */
	public char chrBracketClose = DEFAULT_CHR_BRACKET_CLOSE;
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Singleton with english Notation	 */
	final public static LocalePrimitive DEFAULT_LOCALE = new LocalePrimitive(); 
	
	/**	
	 * @param precision
	 */
	public LocalePrimitive() {
		L.debug("Empty Constructor"); 
		lnBase = DEFAULT_LN_BASE; 
		this.base = DEFAULT_BASE; 
		precision = DEFAULT_PRECISION;
		precisionFactor = DEFAULT_PRECISION_FACTOR; 
	}
	
	/**	
	 * 
	 * @param _base
	 * @param _precision
	 */
	public LocalePrimitive(final char _base) { 
		lnBase = Math.log(_base); 
		this.base = _base; 
		precision = (byte) 
		(ByRefDouble.DOUBLE_MANTISSA_BITS*ByRefDouble.LN2/lnBase); 
		precisionFactor = Math.round(Math.exp(lnBase*precision)); //ByRefLong.POW(base, precision); 
	}
	
	/**	
	 * 
	 * @param _base
	 * @param _precision
	 */
	public LocalePrimitive(final char _base, final byte _precision) {
		lnBase = Math.log(_base); 
		this.base = _base; 
		precision = _precision; 
		precisionFactor = Math.round(Math.exp(lnBase*precision)); //ByRefLong.POW(base, precision); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods for Writing
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final public void addLongSafe(final IStreamOutByte stream, final long b
			, final char[] arr) {
		try { addLong(stream, b, arr); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final public void addLong(final IStreamOutByte stream, final long b
			, final char[] arr) throws IOException {
		ADD_LONG(stream, b, this.base
				, -1, this.chrDot
				, this.minLength, this.chrPad
				, this.chrMinus, this.chrPlus
				, this.chrGroup, arr); 
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final public void addDoubleScientific(final IStreamOutByte stream, double value
			, final char[] buf) throws IOException {
		ADD_DOUBLE_SCIENTIFIC(stream, value, this.precision, this.chrDot 
				, this.minLength, this.chrPad, this.base, this.chrMinus
				, this.chrPlus, this.chrExp, this.chrGroup, this.lnBase, buf); }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final public void addDoubleScientificSafe(final IStreamOutByte stream
			, double value, final char[] buf) {
		try { addDoubleScientific(stream, value, buf); 
		} catch(final IOException x){ 
			throw new IOError(x); 
		}
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final public void addDoubleSafe(final IStreamOutByte stream, final double value 
			, final char[] buf) {
		try { addDouble(stream, value, buf); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	final public void addDouble(final IStreamOutByte stream, final double value 
			, final char[] buf) throws IOException {
		ADD_DOUBLE(stream, value, this.precision, this.chrDot, this.minLength
				, this.chrPad, this.base, this.chrMinus, this.chrPlus, this.chrExp
				, this.chrGroup, this.zeroOnUnderflow, this.precisionFactor, buf, this.lnBase); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods for Reading & Parsing
	///////////////////////////////////////////////////////////////////////////
	
	final public boolean isDigit(final int value) { return GET_DIGIT(value) >= 0; }
	
	/**
	 * decodes the given Character in any Base System 
	 * @param value the Character to convert into a Digit 
	 * @return -1 if the Character does not represent a Digit in the Base system of this Locale!  
	 */
	final public int getDigitOrZero(final int value) {
		if (((value == chrPad ) && (chrPad  != CHR_NONE)) ||
			((value == chrPlus) && (chrPlus != CHR_NONE)) ||
			((value == chrDot ) && (chrDot  != CHR_NONE)))
			return 0; 
		if  (value == chrMinus)
			return Integer.MAX_VALUE; 
		return getDigitOrNeg(value); 
	}
	
	/**
	 * decodes the given Character in any Base System 
	 * @param value the Character to convert into a Digit 
	 * @return -1 if the Character does not represent a Digit in the Base system of this Locale!  
	 */
	final public int getDigitOrNeg(final int value) {
		final int ret = GET_DIGIT(value); 
		if (ret >= base) 
			return -1; 
		return ret; 
	}
	
	/**
	 * decodes the given Character in any Base System 
	 * @param value the Character to convert into a Digit 
	 * @return -1 if the Character does not represent a Digit
	 */
	public static final int GET_DIGIT(final int value) {
		int tmp;
		if (value > 'z')
			return -1;
		if ( (tmp = value - 'a') > 0)
			return tmp+10; 
		if (value > 'Z')
			return -1;
		if ((tmp = value - 'A') > 0)
			return tmp+10; 
		if (value > '9')
			return -1;
		return value-'0'; 
	}
	
}
