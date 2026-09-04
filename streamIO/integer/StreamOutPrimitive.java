/*
 * Created on 07.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;

import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.Log;
import streamIO.StringBufferOutputStream;
import streamIO.real.IStreamOutFloat;
import tools.IOError;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Complete StreamOutPrimitive Implementations. 
 * Provides static Methods to stream out Numbers in arbitrary Notation 
 * as well als all primitive Types including Strings. 
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
 */
public class StreamOutPrimitive 
extends AStreamOutChar //clutters the Interface 
implements IStreamOutPrimitive {
	
	/** Reference to the Logger Instance for this Class	 */
	private static final Log L = new Log(StreamOutPrimitive.class); 
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer to calculate Integer Number Representations 
	 * (sufficient for long Numbers in binary Representation)	*/
	protected char[] buf = new char[65]; 
	
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
	
	final public IStreamOutByte streamByte; 
	
	final public IStreamOutByte getStreamOutByte() { return streamByte; }
	
	public StreamOutPrimitive(final IStreamOutByte _stream) {
		this.streamByte = _stream; 
	}
	
	/** Default Implementation that serializes the Contents of the wrapped Stream	 */
	final public String toString() { return streamByte.toString(); }
	
	////////////////////////////////////////////////////////////////////////////
	/// List Separation is necessary, due to variable-Length Encoding! 
	////////////////////////////////////////////////////////////////////////////
	
	/** Character to indicate a missing Character or no Markup	 */
	public static final char CHR_IGNORE = 0; 
	
	/** Default Character to separate Lists. 
	 * Asserted that it doesn't need Escaping	 */
	public static final char CHR_COL=','; // = CHR_IGNORE; 
	
	/** Character to separate Lists. 
	 * Asserted that it doesn't need Escaping	 */
	public char chrCol = CHR_COL;// = CHR_IGNORE; 
	
	/** Flag when in a List. Needs to be reset to CHR_IGNORE when a new List or Structure starts!	 */
	public char listChr;
	
	/** Optionally writes the Character to separate Lists. 
	 * Asserted that it doesn't need Escaping	 */
	final protected void listChr() throws IOException {
		if ((listChr != CHR_IGNORE) && (streamByte != null))
			streamByte.write(listChr); 
		else
			listChr = chrCol; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamOutChar: abstract Methods; 
	//  not effective due to Delegation, fortunately they are not called often! 
	////////////////////////////////////////////////////////////////////////////
	
	/** Overwritten in StreamOutStruct and StreamOutXml  
	 * @see streamIO.integer.IStreamOutByte#write(int)	 */
	public void write(final int b) throws IOException { 
		//if (streamByte == this)
		//	super.write(b); 
		//else
			streamByte.write(b); }
	
	/** @see streamIO.integer.IStreamOutByte#flush()	 */
	public void flush() throws IOException { streamByte.flush(); }
	
	/** @see streamIO.integer.IStreamOutByte#close()	 */
	public void close() throws IOException { streamByte.close(); }
	
	////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamOutChar
	////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutPrimitive#addChar(char)	 */
	public IStreamOutChar addChar(final char chr) {
		try { listChr(); 
			write(chr);
		} catch(final IOException x) {
			throw new IOError(x);
		}
		return this; }
	
	/** @see streamIO.integer.IStreamOutChar#addString(java.lang.String, int, int) 	 */
	public IStreamOutChar addString(final String b, final int stop, final int start) {
		try { listChr(); 
			WRITE_UNSAFE(this, b, stop, start); //super.addString(b, stop, start); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamOutPrimitive
	////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	public IStreamOutInt addLong(final long b) { //no Encoding
		try { listChr(); 
			locale.addLong(streamByte, b, buf); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this;  }
	
	/** @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) { //no Encoding
		try { listChr(); 
			locale.addDouble(streamByte, value, buf); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** @see streamIO.integer.IStreamOutPrimitive#addBool(boolean)	 */
	public IStreamOutPrimitive addBool(final boolean value) {
		try { listChr(); 
			//addString(locale.strBool[value?1:0]); //applies Encoding
			WRITE_UNSAFE(streamByte, locale.strBool[value?1:0]); //Assumption: both Strings don't need to be encoded! 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutInt addInt(final int b) { return addLong(b); }
	
	/** @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public IStreamOutFloat addFloat(final float value) {
		return addDouble(value); }
	
	/** @see streamIO.IStreamOut#addItems(java.lang.Object)	 */
	public long addItems(final Object arg) {
		return addItems(arg, Integer.MAX_VALUE); }
	
	/** @see streamIO.IStreamOut#addItems(java.lang.Object, int)	 */
	public long addItems(final Object arg, final int flatDepth) {
		return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }
	
	/** @see streamIO.IStreamOut#addItems(java.lang.Object[])	 */
	public long addItems(final Object[] arg) {
		return AStreamOut.ADD_ITEMS(this, arg); }
	
	/** @see streamIO.IStreamOut#addItems(streamIO.IIStreamIn)	 */
	public long addItems(final IIStreamIn arg) {
		return AStreamOut.STREAM(arg, this); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Testing and main() Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	public static void TEST_DOUBLE() throws Exception {
		final StringBufferOutputStream stream = new StringBufferOutputStream();
		byte precision = (byte) 5; 
		double value = -14.56789; 
		LocalePrimitive.ADD_DOUBLE(stream, value, precision);
		String result = stream.toString(); 
		Assert.EQUALS(Double.toString(value), result);
		
		stream.getBuffer().setLength(0); 
		LocalePrimitive.ADD_DOUBLE(stream, 1e-12, precision);
		result = stream.toString(); 
		L.n(result); 
		
		stream.getBuffer().setLength(0); 
		LocalePrimitive.ADD_DOUBLE(stream, 1e6, precision, LocalePrimitive.DEFAULT_CHR_DOT, (char) 1, LocalePrimitive.DEFAULT_CHR_PAD, (char) 2);
		result = stream.toString(); 
		L.n(result); 
		
		TEST_SCIENTIFIC(stream, 0.9e-6); 
		TEST_SCIENTIFIC(stream, 1e-6); 
		TEST_SCIENTIFIC(stream, 1.1e-6); 
		TEST_SCIENTIFIC(stream, -0.9e-6); 
		TEST_SCIENTIFIC(stream, -1e-6); 
		TEST_SCIENTIFIC(stream, -1.1e-6); 
		TEST_SCIENTIFIC(stream, 0.9e+12); 
		TEST_SCIENTIFIC(stream, 1e+12); 
		TEST_SCIENTIFIC(stream, 1.1e+12); 
		TEST_SCIENTIFIC(stream, -0.9e+12); 
		TEST_SCIENTIFIC(stream, -1e+12); 
		TEST_SCIENTIFIC(stream, -1.1e+12); 
	}
	
	/**
	 * @param stream
	 * @param precision
	 * @throws IOException
	 */
	private static void TEST_SCIENTIFIC(final StringBufferOutputStream stream, double value) throws IOException {
		stream.getBuffer().setLength(0); 
		LocalePrimitive.ADD_DOUBLE(stream, value, (byte) 5, LocalePrimitive.DEFAULT_CHR_DOT, (char) 8);
		final String result = stream.toString(); 
		L.n(result);
	}

	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	public static void TEST_LONG() throws Exception {
		final StringBufferOutputStream stream = new StringBufferOutputStream();
		long value = 123456789; 
		char radix = LocalePrimitive.DEFAULT_BASE; 
		LocalePrimitive.ADD_LONG(stream, value); 
		Assert.EQUALS(Long.toString(value, radix), stream.toString()); 
		stream.getBuffer().setLength(0); 
	}
	
	public static void main(final String[] args) throws Exception {
		TEST_DOUBLE(); 
		TEST_LONG(); 
	}
	
}
