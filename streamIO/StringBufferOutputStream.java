package streamIO;

import java.io.IOException;
import java.io.OutputStream;

import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.IStreamOutChar;
import streamIO.integer.IStreamOutInt;
import streamIO.integer.IStreamOutPrimitive;
import streamIO.real.IStreamOutFloat;

/**
  * Title: StringBufferOutputStream.java<p>
  * Description:
  * OutputStream, also used for counting and filtering Characters from the streamIO
  * and for assembling the Data into a String. 
  * Assembling starts only when Counter is >= 0
  * @see streamIO.Log uses this Class to assemble the Stack Trace without CR/LFs
  * For just assembling you could also use
  * @see java.io.ByteArrayOutputStream which grows automatically or 
  * @see streamIO.StringBufferStreamOut
  *
  * Known Uses:
  * @see streamIO.Log 
  * 
  * Known SubClasses:
  *
  * Similar Classes: 
  * @see java.io.StringBufferInputStream which does not implement all these Interfaces 
  * and thus must be wrapped by a StreamInChar. 
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-19, 11;59;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StringBufferOutputStream
extends OutputStream
implements IStreamOutByte, IStreamOut, IStreamOutFloat, IStreamOutInt, IStreamOutPrimitive {
	
	/** Default initial Buffer Size for the empty Constructor */
	public static char INITIAL_SIZE_DEFAULT = 12;
	
	////////////////////////////////////////////////////////////////////////////////
	/// Static Helper Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** return the last Character and shrinks the Buffer by it
	 * analogous to the pushBack Method of InputStreams
	 * @return the last Character or 0 if the given Buffer is null
	 * @param buf the Buffer to trim by the last Character
	 */
	final static public char PULL_BACK(final StringBuffer buf) {
		if (buf == null)
			return 0; 
		final int  pos = buf.length()-1;
		final char ret = buf.charAt(pos); 
		buf.setLength(pos); 
		return ret; 
	}
	
	/** return the last Character and shrinks the Buffer by it
	 * @return the last Character or 0 if the given Buffer is null
	 * @param buf the Buffer to trim by the last Character
	 */
	final static public char REPLACE_LAST(final StringBuffer buf, final char chr) {
		if (buf == null)
			return 0; 
		final int  pos = buf.length()-1;
		final char ret = buf.charAt(pos); 
		buf.setCharAt(pos, chr); 
		return ret; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Counts the Occurrence of the Filter Characters
	  * Assembling starts only when Counter is >= 0 	 */
	public int counter;
	
	/** String cotaining all Characters to be replaced or filtered out
	  * No Filtering if set to 'null'	*/
	public String filter;
	
	/** String cotaining all Characters to replace the ones found in 'filter'
	  * No Replace leaves the Characters empty	*/
	public String replace;
	
	/** Contains the assembled String	*/
	protected StringBuffer buffer; //
	
	/////////////////////////////////////////////////////////////////////////////////
	// Interface Object
	/////////////////////////////////////////////////////////////////////////////////

	/** @see java.lang.Object#toString()	 */
	public String toString() { return buffer.toString(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the assembled String	*/
	public StringBuffer getBuffer() { return buffer; }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public StringBufferOutputStream() {
		this(INITIAL_SIZE_DEFAULT);
	}

	/** Initializing Constructor	 */
	public StringBufferOutputStream(final int initialSize) {
		this(new StringBuffer(initialSize));
	}

	/** Initializing Constructor	 */
	public StringBufferOutputStream(final StringBuffer buffer_) {
		this.buffer = buffer_;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Interface OutputStream: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	*/
	//	public StackFilter() { super(null); }

	/** Writes the specified byte to this byte array output stream. 	 */
	public void write(final int b) throws IOException {
		int pos;
		if ((filter != null) && ((pos = filter.indexOf(b)) >= 0)) {
			++counter;
			if ((replace != null) && (replace.length() >= pos) && (counter >= 0)) {
				buffer.append(replace.charAt(pos));
			}
		} else if (counter >= 0)
			buffer.append((char) b);
	} // out.write(b); }

	/////////////////////////////////////////////////////////////////////////////////
	// Interface IStreamOutByte
	/////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.Byte.IStreamOutByte#addString(char[], int, int)	 */
	public void write(final char[] b, final int off, final int len) {
		buffer.append(b, off, len); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(char[])	 */
	public void write(final char[] b) { buffer.append(b); }
	
	/** @see streamIO.Byte.IStreamOutByte#addItem(int[])	 */
	//public void addItem(final int[] b) { buffer.append(b); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(String)	 */
	public void write(final String b) { buffer.append(b); }
	
	/** @see streamIO.Byte.IStreamOutByte#addItem(int[], int, int)	 */
	/* rather add the Elements individually! 
	public void addItem(final int[] b, final int off, int len) throws IOException {
		len += off; 
		for (int i = off-1; ++i < len;) {
			buffer.append((char) b[i]); //encoding?
		}
	}
	*/
	/** @see streamIO.Byte.IStreamOutByte#addString(String, int, int)	 */
	public void write(final String b, final int off, int len) throws IOException {
		len += off; 
		for (int i = off-1; ++i < len;) 
			buffer.append(b.charAt(i)); //encoding?
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// Interface IStreamOutChar
	/////////////////////////////////////////////////////////////////////////////////

	/** @see streamIO.integer.IStreamOutChar#getStreamOutByte()	 */
	public IStreamOutByte getStreamOutByte() { return this;	} //.out; }

	/** @see IStreamOutPrimitive#addChar(char)	 */
	public IStreamOutChar addChar(final char value) {
		buffer.append(value); 
		return this; }

	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer, int, int)	 */
	public IStreamOutChar addBuffer(final StringBuffer b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer)	 */
	public IStreamOutChar addBuffer(final StringBuffer b, final int stop) {
		return addBuffer(b, 0, stop); }
	
	/** @see streamIO.Byte.IStreamOutByte#addString(StringBuffer)	 */
	public IStreamOutChar addBuffer(final StringBuffer b) {
		return addBuffer(b, b.length()); }
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public IStreamOutChar escapeString(final String str, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, str, separators);
		return this; 
	}
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public IStreamOutChar escapeString(final StringBuffer str, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, str, separators);
		return this; 
	}
	
	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String)	 */
	public IStreamOutChar addString(final String b) { return addString(b, b.length()); } 

	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String, int)	 */
	public IStreamOutChar addString(final String b, final int stop) {
		return addString(b, stop, 0); } 

	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String, int, int)	 */
	public IStreamOutChar addString(final String b, final int stop, final int start) {
		for (int i = start-1; ++i < stop;)
			buffer.append(b.charAt(i)); 
		return this; }
	
	/////////////////////////////////////////////////////////////////////////////////
	// Interface IStreamOut
	/////////////////////////////////////////////////////////////////////////////////

	/** @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(Object arg) { this.buffer.append(arg); return this; }

	/** @see streamIO.IStreamOut#addItems(java.lang.Object)	 */
	public long addItems(final Object arg) { return AStreamOut.ADD_ITEMS(this, arg, 1); }
	
	/** @see streamIO.IStreamOut#addItems(java.lang.Object, int)	 */
	public long addItems(final Object arg, final int flatDepth) { 
		return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }

	/** @see streamIO.IStreamOut#addItems(java.lang.Object[])	 */
	public long addItems(final Object[] arg) { return AStreamOut.ADD_ITEMS(this, arg); }
	
	/** @see streamIO.IStreamOut#addItems(streamIO.IIStreamIn)	 */
	public long addItems(final IIStreamIn arg) { return AStreamOut.STREAM(arg, this); }

	/////////////////////////////////////////////////////////////////////////////////
	// Interface IStreamOutFloat
	/////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public IStreamOutFloat addFloat(final float value) {
		buffer.append(value); 
		return this; }
	
	/** @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		buffer.append(value); 
		return this; }
	
	/////////////////////////////////////////////////////////////////////////////////
	// Interface IStreamOutInt
	/////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutInt#addInt(int)	 */
	public IStreamOutInt addInt(final int value) {
		buffer.append(value); 
		return this; }
	
	/** @see streamIO.integer.IStreamOutInt#addLong(long)	 */
	public IStreamOutInt addLong(final long value) {
		buffer.append(value); 
		return this; }
	
	/** @see streamIO.integer.IStreamOutPrimitive#addBool(boolean)	 */
	public IStreamOutPrimitive addBool(final boolean value) {
		buffer.append(value); 
		return this; }
	
}
