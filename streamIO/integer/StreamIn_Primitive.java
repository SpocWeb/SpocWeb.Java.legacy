/*
 * Created on 12.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;
import java.io.StringReader;

import streamIO.Assert;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.integer.adapter.ReaderToStreamIn_Byte;
import tools.IOError;
import function.byref.ByRefInt;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Class for StreamIn_Char Implementations. 
 * Provides static Methods to read Numbers in arbitrary Notation. 
 * 
 * Design Decisions / Implementation Details:
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
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class StreamIn_Primitive 
extends AStreamIn_Char
implements IStreamIn_Primitive {
	
	/** Reference to the Logger Instance for this Class	 */
	private static final Log L = new Log(StreamOutPrimitive.class); 
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Locale used to format primitive Types.	 */
	public LocalePrimitive locale = LocalePrimitive.DEFAULT_LOCALE;  
	
	/** Separator String, 
	 * since variable-Length Encodings require this for being parseable! 
	 * Adding this here saves overwriting in the SubClass AStreamOutStruct
	 * On the other Hand, adding a Separator is only necessary for Lists; 
	 * for Name/Value Pairs a different Delimiter must be set 
	 * and it is more direct write it in the Child Class.  
	 */
	//public char separator;// = 0; //null; 
	
	/**	Most direct Reference to the Input Stream giving the next Characters. 
	 * Since this Class is capable of Escaping and Quoting, 
	 * no further PreProcessing is necessary and the Input can directly be taken 
	 * from any InputStream or IStreamIn_Byte without Delegation Overhead! 	*/
	final public IStreamIn_Byte streamByte; 
	
	/** Returns the underlying byte Stream this Reader is wrapping. */
	final public IStreamIn_Byte getStreamIn_Byte() { return streamByte; }

	/** Wraps the given byte Stream to read primitive Values from it.
	 * @param _stream the underlying byte input Stream to read from */
	public StreamIn_Primitive(final IStreamIn_Byte _stream) {
		this.streamByte = _stream; 
		L.debug(_stream); 
	}
	
	/** Default Implementation that serializes the Contents of the wrapped Stream	 */
	final public String toString() { return streamByte.toString(); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Char: abstract Methods
	//  not effective due to Delegation, fortunately they are not called often! 
	////////////////////////////////////////////////////////////////////////////
	
	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() {
		resetVersion(null); 
		return streamByte.reSet(); }
	
	/** Closes the underlying byte Stream.
	 * @see streamIO.integer.IStreamIn_Byte#close()	 */
	public void close() throws IOException { streamByte.close(); }

	/** Delegates to the underlying byte Stream's {@code available()}.
	 * @see streamIO.integer.IStreamIn_Byte#available()	 */
	public int available() throws IOException { return streamByte.available(); }

	/** Delegates to the underlying byte Stream's current read position.
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return streamByte.getPosition(); }

	/** Delegates to the underlying byte Stream's maximum mark/reset size.
	 * @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return streamByte.getMaxMarkSize(); }

	/** Delegates to the underlying byte Stream's byte order.
	 * @see streamIO.IOrdered#getOrder()	 */
	public byte getOrder() { return streamByte.getOrder(); }
	
	/** return the next Byte read, -1 for EOF (End Of File) 
	 * @see streamIO.integer.IStreamIn_Byte#read()	 */
	public int read() throws IOException { return streamByte.read(); }
	
	////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamOutChar
	////////////////////////////////////////////////////////////////////////////
	
	/** Reads and returns the next Character from the underlying byte Stream.
	 * @see streamIO.integer.IStreamIn_Primitive#nextChar()	 */
	public char nextChar() {
		try { return (char) streamByte.read();
		} catch (final IOException x) {
			throw new IOError(x);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamIn_Primitive
	////////////////////////////////////////////////////////////////////////////
	
	/**Since the Stream may still be unstructured, 
	 * we cannot apply a Tokenizer, read up to the next Separator 
	 * and compare the result afterwards!  
	 * @see #nextEnum(String[]) allows to choose between up to 64 Values 
	 * @return true when the next Value read is the locale Representation of 'true' 
	 * @throws RuntimeException when the value read is neither 'true' nor 'false' 
	 */
	public boolean nextBool() {
		final int val = nextEnum(locale.strBool);
		if (val < 0)
			throw new RuntimeException("Could not parse to Boolean: stopped at Character "+-val); 
		return (val == 1); }
	
	/**Since the Stream may still be unstructured, 
	 * we cannot apply a Tokenizer, read up to the next Separator 
	 * and compare the result afterwards!  
	 * Allows to choose between up to 64 String Values.  
	 * 
	 * @param names 
	 * @return the Index of the found Name 
	 * or the negative Number of Characters read without finding a Match.  
	 */
	public int nextEnum(final String[] names) {
		//using a long saves creating and deleting a boolean[names.length] on the Heap
		long all  = 1L << names.length;
		long bits = all-1; //Assume that there are less than 64 Values in an enm.
		int i = -1; 
		do {
			char chr = nextChar(); 
			long mask = all; 
			for(int j = names.length; --j >= 0;) {
				if (0 == (bits & (mask >>= 1))) 
					continue; 
				if (i >= names[j].length())
					return i; 
				if (chr != names[j].charAt(i)) 
					bits &= ~mask; 
			}
		} while(bits != 0); 
		return -i; 
	}
	
	/** Reads and returns the next value narrowed from a {@code double} to a {@code long}.
	 * @see IStreamIn_Int#nextLong()	 */
	public long nextLong() { return (long) nextDouble(); } /*
		final double dbl = nextDouble(); 
		if (dbl != dbl)//by Default NaN is cast to 0!
			return Long.MIN_VALUE; 
		if ((dbl < Long.MIN_VALUE) || (dbl != dbl) )//
			return Long.MIN_VALUE; 
		if (dbl  > Long.MAX_VALUE)
			return Long.MAX_VALUE; 
		return (long) dbl; }
	*/ 
	
	/** Reads and returns the next {@code double} value, using this Reader's Locale, swallowing any IOException.
	 * @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	public double nextDouble() { return READ_DOUBLE_FROM_SAFE(this, locale, currItem); }

	/** Reads the next {@code double} value from the given Stream/Locale, swallowing any IOException as an unchecked IOError.
	 * @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	final static public double READ_DOUBLE_FROM_SAFE(final IStreamIn_Byte stream
			, final LocalePrimitive locale, final ByRefInt currItem) {
		try { return READ_DOUBLE_FROM(stream, locale, currItem);
		} catch(final IOException x) {
			throw new IOError(x);
		}
	}

	/** Reads the next {@code double} value from the given Stream, using the default Locale.
	 * @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	final static public double READ_DOUBLE_FROM(
			final IStreamIn_Byte stream) throws IOException {
		return READ_DOUBLE_FROM(stream, LocalePrimitive.DEFAULT_LOCALE);
	}

	/** Reads the next {@code double} value from the given Stream, using the given Locale.
	 * @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	final static public double READ_DOUBLE_FROM(final IStreamIn_Byte stream
			, final LocalePrimitive locale) throws IOException {
		return READ_DOUBLE_FROM(stream, locale, null);
	}
	
	/** return the next double Value read from the stream using the given locale 
	 * @return the next double Value read from the stream using the given locale
	 * @see streamIO.real.IStreamIn_Float#nextDouble() 
	 * @param stream the Stream to read from 
	 * a PushBack() has to be performed to retrieve the current Character! 
	 * @param locale the Locale to use for parsing 
	 * @param currItem the optional Container for the current Character to avoid a pushBack 
	 * @throws IOException
	 */
	final static public int READ_INT_FROM(final IStreamIn_Byte stream
			, final int base, final int numChars) throws IOException {
		return (int) READ_LONG_FROM(stream, base, numChars); }
	
	/** return the next double Value read from the stream using the given locale 
	 * @return the next double Value read from the stream using the given locale
	 * @see streamIO.real.IStreamIn_Float#nextDouble() 
	 * @param stream the Stream to read from 
	 * a PushBack() has to be performed to retrieve the current Character! 
	 * @param locale the Locale to use for parsing 
	 * @param currItem the optional Container for the current Character to avoid a pushBack 
	 * @throws IOException
	 */
	final static public long READ_LONG_FROM(final IStreamIn_Byte stream
			, final int base, final int numChars) throws IOException {
		long value = 0; 
		for(int i = numChars; --i >= 0; )
			value = value*base+LocalePrimitive.GET_DIGIT(stream.read()); 
		return value; 
	}
	
	/** return the next double Value read from the stream using the given locale 
	 * @return the next double Value read from the stream using the given locale
	 * @see streamIO.real.IStreamIn_Float#nextDouble() 
	 * @param stream the Stream to read from 
	 * a PushBack() has to be performed to retrieve the current Character! 
	 * @param locale the Locale to use for parsing 
	 * @param currItem the optional Container for the current Character to avoid a pushBack
	 * @param numDigits if not null, is incremented by the Number of valid Digits read.  
	 * @throws IOException
	final static public long READ_LONG_FROM(final IStreamIn_Byte stream
			, final LocalePrimitive locale, ByRefInt currItem, final int[] numDigits) throws IOException {
		if (currItem == null)
			currItem  = new ByRefInt(stream.read()); 
		else 
			if (0 > locale.getDigitOrNeg(currItem.Value)) 
				currItem.Value = stream.read(); 
		int currValue = currItem.Value; 
		long lng = locale.getDigitOrNeg(currValue);
		if  (lng < 0)
			return 0; //does not even start with a Number
		int power = 1; 
		for(int val; 0 <= (currValue = stream.read());) {
			if (currValue == locale.chrGroup)
				currValue  = stream.read(); //ignore Grouping Characters
			if (0 > (val = locale.getDigitOrNeg(currValue))) //super.nextInt()) 
				break; 
			lng = lng*locale.base+val; 
			++power; 
		}
		if ((numDigits != null) &&
			(numDigits.length > 0))
			 numDigits[0] += power; 
		currItem.Value = currValue;  
		return lng; 
	}
	
	final static public double READ_DOUBLE_FROM(final IStreamIn_Byte stream
			, final LocalePrimitive locale, final ByRefInt currItem) throws IOException {
		return READ_DOUBLE_FROM(stream, locale, currItem, null); }
	
	final static public double READ_DOUBLE_FROM(final IStreamIn_Byte stream
			, final LocalePrimitive locale, ByRefInt currItem, int[] numDigits) throws IOException {
		if (currItem == null)
			currItem  = new ByRefInt(stream.read()); 
		if (numDigits == null)
			numDigits  = new int[1]; 
		int oldDigits  = numDigits[0]; 
		final long lng = READ_LONG_FROM(stream, locale, currItem, numDigits); 
		if (oldDigits == numDigits[0])
			return Double.NaN; //no valid Digits found! 
		if (currItem.Value == locale.chrDot) {
			currItem.Value  = stream.read(); 
		} else 
			return lng; 
		oldDigits  = numDigits[0]; 
		final long frac = READ_LONG_FROM(stream, locale, currItem, numDigits); //using 2 long Variables allows to process longer Numbers 
		final int pow = numDigits[0] - oldDigits; 
		if (pow != 0)
			return lng*Math.exp(pow*locale.lnBase); 
		return lng; //loss of Precision with long Integers! Maybe write a distinct READ_LONG Method. 
	}
	 */
	
	final static public double READ_DOUBLE_FROM(final IStreamIn_Byte stream
			, final LocalePrimitive locale, ByRefInt currItem) throws IOException {
		if (currItem == null)
			currItem  = new ByRefInt(stream.read()); 
		else 
			if (0 > locale.getDigitOrZero(currItem.Value)) //not usable...
				currItem.Value = stream.read(); //read next char to tolerantly skip a single leading Delimiter! 
		boolean neg = false; 
		int power = Integer.MIN_VALUE; 
		int      currValue  = currItem.Value; 
		while   (currValue == locale.chrPad)
			     currValue  = stream.read(); //skip White Padding Space
		if     ((currValue == locale.chrPlus) || 
				(currValue == LocalePrimitive.DEFAULT_CHR_PLUS))
			     currValue  = stream.read(); 
		else if (currValue == locale.chrMinus) {
				 currValue  = stream.read(); neg = true; 
		}
		if      (currValue == locale.chrDot) {
				 currValue  = stream.read(); power = 1; }
		long lng = locale.getDigitOrNeg(currValue); 
		if  (lng < 0)
			return Double.NaN; //does not even start with a Number
		for(int val; 0 <= (currValue = stream.read());) {
			if  ((currValue == locale.chrGroup) ||
				 (currValue == LocalePrimitive.DEFAULT_CHR_GROUP))
				if (power < 0)
					continue; //ignore Grouping Characters
				else 
					break; //only allowed BEFORE the Decimal Point!
			if   (currValue == locale.chrDot) {
				if (power < 0)
					power = 0; 
				else
					throw new IOException("Two Decimal Separators in same Number:"+locale.chrDot); 
				continue; 
			}
			if (0 > (val = locale.getDigitOrNeg(currValue))) //super.nextInt()) 
				break; 
			lng = lng*locale.base+val; 
			++power; 
		}
		double pow = (power < 0) ? 0 : -power; 
		if (currValue != locale.chrExp) {
			currItem.Value = currValue; //PushBack
		} else {
			currItem.Value = stream.read(); 
			final double exp = READ_DOUBLE_FROM(stream, locale, currItem); 
			pow += exp; 
		}
		if (neg)
			lng = -lng; 
		if (pow != 0)
			return lng*Math.exp(pow*locale.lnBase); 
		return lng; //loss of Precision with long Integers! Maybe write a distinct READ_LONG Method. 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Default Implementations 
	///////////////////////////////////////////////////////////////////////////
	
	/** Reads and returns the next value narrowed from a {@code long} to an {@code int}.
	 * @see stringOp.parser.IIStreamIn_Int#nextInt()	 */
	public int nextInt() { return (int) nextLong(); }

	/** Reads and returns the next value narrowed from a {@code double} to a {@code float}.
	 * @see streamIO.real.IStreamIn_Float#nextFloat()	 */
	public float nextFloat() { return (float) nextDouble(); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Empty smoke-test entry point; performs no action. */
	public static void main(final String[] args) throws Exception {
		testReadDouble(Math.PI*1e12); 
		testReadDouble(.23456, ".23456"); 
		testReadDouble(-.23456, "-.23456"); 
		testReadDouble(-.23456, "  -00.23456"); 
		testReadDouble(3.4, "  3.4-00.23456"); 
		testReadDouble(Double.NaN, "  .-0"); 
		try { testReadDouble(Double.NaN, ".0.-0"); Assert.FAIL("should throw an Exception"); 
		} catch(final IOException x) {}
	}

	/**
	 * @param value
	 * @throws IOException
	 */
	private static void testReadDouble(final double value) throws IOException {
		testReadDouble(value, Double.toString(value)); 
	}

	/**
	 * @param value
	 * @throws IOException
	 */
	private static void testReadDouble(final double value, final String input) throws IOException {
		final StringReader sr = new StringReader(input);
		final IStreamIn_Byte inStream = new ReaderToStreamIn_Byte(sr); 
		final double ret = READ_DOUBLE_FROM(inStream); 
		Assert.EQUALS(value, ret);
	}

}
