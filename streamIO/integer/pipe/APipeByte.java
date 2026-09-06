package streamIO.integer.pipe;

import java.io.IOException;

import streamIO.integer.AStreamByte;
import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamByte;
import streamIO.integer.IStreamOutByte;

/**
  * Title: APipeByte<p>
  * Description:
  * Purpose:
  * abstract Base Class for a Pipe processing Bytes and Integers
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
  * Created on	12-29-2002, 02:58 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/pipe_abstraction, code/pipe_implementation]
  * concepts: [In-Memory Producer-Consumer Byte Pipes]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public abstract class APipeByte
extends AStreamByte
implements IStreamByte {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamOutByte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Writes the byte range [off, off+len) to this Pipe.
	 * @see streamIO.Byte.IStreamOutByte#addString(byte[], int, int)	 */
	public void write(byte[] b, int off, int len) throws IOException {
		AStreamOutByte.WRITE(this, b, off, len); }

	/** Writes the whole byte array to this Pipe.
	 * @see streamIO.Byte.IStreamOutByte#addString(byte[]) */
	public void write(byte[] b) throws IOException { write(b, 0, b.length); }

	/** Writes the char range [off, off+len) as bytes to this Pipe, swallowing any IOException.
	 * @see streamIO.Byte.IStreamOutByte#addString(char[], int, int)	 */
	public void write(final char[] b, final int off, final int len) {
		AStreamOutByte.WRITE_SAFE(this, b, off, len); }

	/** Writes the whole char array as bytes to this Pipe.
	 * @see streamIO.Byte.IStreamOutByte#addString(char[]) */
	public void write(final char[] b) { write(b, 0, b.length); }

	/** @see streamIO.Byte.IStreamOutByte#addItem(int[], int, int)	 
	public void addItem(final int[] b, final int off, final int len) {
		AStreamOutByte.WRITE_SAFE(this, b, off, len); }
	 */

	/** @see streamIO.Byte.IStreamOutByte#addItem(int[])	 
	public void addItem(int[] b) throws IOException { addItem(b, 0, b.length); }
	 */

	/** Writes the substring [off, off+len) as bytes to this Pipe.
	 * @see streamIO.Byte.IStreamOutByte#addString(String, int, int)	 */
	public void write(final String b, final int off, final int len) throws IOException {
		AStreamOutByte.WRITE(this, b, off, len); }

	/** Writes the whole String as bytes to this Pipe.
	 * @see streamIO.Byte.IStreamOutByte#addString(String) */
	public void write(final String b) throws IOException { write(b, 0, b.length()); }

	/** Writes the StringBuffer range [start, stop) as bytes to this Pipe.
	 * @see streamIO.Byte.IStreamOutByte#addString(StringBuffer, int, int)	 */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start);
		return this; }

	/** Writes the leading StringBuffer range [0, stop) as bytes to this Pipe.
	 * @see streamIO.Byte.IStreamOutByte#addString(StringBuffer)	 */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop) {
		return addBuffer(b, stop, 0); }

	/** Writes the whole StringBuffer as bytes to this Pipe.
	 * @see streamIO.Byte.IStreamOutByte#addString(StringBuffer)	 */
	public IStreamOutByte addBuffer(final StringBuffer b) {
		return addBuffer(b, b.length()); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public abstract void write(int b) throws IOException; // {}

	/**
	  * Flushes this output stream and forces any buffered output bytes to be written out.
	  * The general contract of flush is that calling it is an indication that,
	  * if any bytes previously written have been buffered
	  * by the implementation of the output stream,
	  * such bytes should immediately be written to their intended destination.
	  *
	  * The flush method of OutputStream does nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract void flush() throws IOException; // {}

	/**
	  * Closes this output stream and releases any system resources associated with this stream.
	  * The general contract of close is that it closes the output stream.
	  * A closed stream cannot perform output operations and cannot be reopened.
	  *
	  * The close method of OutputStream does nothing.
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract void close() throws IOException; // {}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent IStreamOutByte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the byte order of this Pipe's Data.
	 * @see streamIO.Float.IStreamIn_Int#getOrder()	 */
	public abstract byte getOrder(); // { return 0; }

	/** Returns the number of bytes available to read without blocking.
	 * @see streamIO.Byte.IStreamIn_Byte#available()	 */
	public abstract int available() throws IOException; // { return 0; }

	/** Marks the current Position, allowing up to {@code readLimit} bytes to be read before invalidation.
	 * @see streamIO.Byte.IStreamIn_Byte#mark(int) */
	public abstract void mark(int readLimit); // {}

	/** Returns the maximum number of bytes that can be marked and reset.
	 * @see streamIO.Byte.IStreamIn_Byte#getMaxMarkSize()	 */
	public abstract long getMaxMarkSize(); // { return false; }

	/** Reads and returns the next byte, or EOF at end of Pipe.
	 * @see streamIO.Byte.IStreamIn_Byte#read()	 */
	public abstract int read() throws IOException; //{ return 0; }

	/** Resets this Pipe to the given Position relative to the last mark.
	 * @see streamIO.Byte.IStreamIn_Byte#reSet(long)	 */
	public abstract long reSet(long position); // { return 0; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception { //
		System.out.println("Testing " + APipeByte.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(); }

}