/**
 * File  Name: ReaderToStreamIn_Byte.java
 * Created on: 26.02.2003
 */
package streamIO.integer.adapter;

import java.io.IOException;
import java.io.Reader;

import streamIO.IMarkAble;
import streamIO.IOrdered;
import streamIO.IReSetAble;
import streamIO.integer.AStreamIn_Byte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamIn_Int;
import tools.IOError;

/**
 * Title: ReaderToStreamIn_Byte<p>
 * Description:
 * Adapter that wraps any Writer Interface into an IStreamIn_Byte Interface
 *
 * Design Decisions / Implementation Details:
 * Bytes are created simply by truncating the Words. 
 * Encodings are rather solved by Encoding Filters in streamIO.Byte.Encoding. 
 * 
 * Just like with InputStream and OutputStream, most Methods have the same Signature
 * als the Reader and Writer Methods, so a StreamIn_Byte 
 * can be quickly derived instead of being wrapped (slower but more flexible) 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * tags: [code/stream_adapter, code/stream_bridging, code/stream_wrapper]
 * concepts: [Bridges streamIO Interfaces to java.io and Arrays]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class ReaderToStreamIn_Byte 
extends AStreamIn_Byte
implements IStreamIn_Byte {
	
	/** Reference to the Writer Object being wrapped */
	protected Reader reader; 
	
	/** Constructor for ReaderToStreamIn_Byte.	 */
	public ReaderToStreamIn_Byte(final Reader reader_) { this.reader = reader_; }
	
	/** not possible to create a second Iterator from a Reader 
	 * @see streamIO.integer.IStreamIn_Int#IntIterator()	 */
	public IStreamIn_Int IntIterator() { return null; }
	
	/** Delegates to the wrapped Reader's read().
	 * @see streamIO.Byte.IStreamIn_Byte#read()	 */
	public int read() throws IOException {
		return reader.read(); } //rather plug in a Filter that also logs! 
	/*	final int ret = reader.read(); 
		System.out.print((char)ret); 
		return ret; }
	*/
	
	/** Reads and returns the next Character (widened to long) from the wrapped Reader.
	 * @see streamIO.Float.IStreamIn_Int#nextLong()	 */
	public long nextLong() {
		try { return reader.read(); 
		} catch(final IOException x) {
			throw new IOError(x); }
	}
	
	/** Fills the whole byte array by delegating to {@link #read(byte[], int, int)}.
	 * @see streamIO.Byte.IStreamIn_Byte#read(byte[])	 */
	public int read(final byte[] b) throws IOException { return read(b, 0, b.length); }

	/** Reads up to {@code len} Characters from the wrapped Reader, narrowed to bytes.
	 * @see streamIO.Byte.IStreamIn_Byte#read(byte[], int, int)	 */
	public int read(final byte[] b, final int off, final int len) throws IOException {
		final int stop = off+len; 
		for (int i = off-1; ++i < stop;) {
			int val;
			if ((val = reader.read()) < 0) {
				return i-off; }
			b[i] = (byte) val;
		}
		return len; }
	
	/**
	 * This Method makes Parsing by a SINGLE Separator an elementary Function of a streamIO. 
	 * Parsing Masses of Text by several Separators is less efficient: O(N*M)
	 * and requires an Inversion of the Separator String: O(N) + O(M)
	 * @see streamIO.Byte.IStreamIn_Byte#read(int, StringBuffer)
	 */
	public StringBuffer read(final int sep, final StringBuffer b) throws IOException {
		for (int val; (val = reader.read()) >= 0;) {
			if (val == sep) {
				break; }
			b.append((char) val); }
		return b; }
	
	/** Reads Characters up to (and excluding) {@code sep} into a fresh StringBuffer.
	 * @see streamIO.Byte.IStreamIn_Byte#read(int)	 */
	public StringBuffer read(final int sep) throws IOException {
		return read(sep, new StringBuffer()); }

	/** Delegates to the wrapped Reader's read(char[]).
	 * @see streamIO.Byte.IStreamIn_Byte#read(char[])	 */
	public int read(final char[] b) throws IOException { return reader.read(b); }

	/** Delegates to the wrapped Reader's read(char[], int, int).
	 * @see streamIO.Byte.IStreamIn_Byte#read(char[], int, int)	 */
	public int read(final char[] b, final int off, final int len) throws IOException {
		return reader.read(b, off, len); }

	/** Fills the whole int array by delegating to {@link #read(int[], int, int)}.
	 * @see streamIO.Byte.IStreamIn_Byte#read(int[])	 */
	public int read(final int[] b) throws IOException { return read(b, 0, b.length); }

	/** Reads up to {@code len} Characters from the wrapped Reader into the int array.
	 * @see streamIO.Byte.IStreamIn_Byte#read(int[], int, int)	 */
	public int read(final int[] b, final int off, final int len) throws IOException {
		final int stop = off+len;
		for (int i = off-1; ++i < stop;) {
			int val;
			if ((val = reader.read()) < 0) {
				return i-off; }
			// TODO: LOGIC: narrowing the Character to (byte) before storing into this int[]
			// truncates and sign-extends any code point outside -128..127 (e.g. Latin-1
			// supplement, or any char > 0x7F), corrupting the value that ends up in b[i] -
			// unlike the read(byte[], ...) overload above, where truncating to byte is the
			// intended behavior, this int[] overload should store "val" directly, unnarrowed.
			b[i] = (byte) val;
		}
		return len; }

	/** Skips {@code n} Characters on the wrapped Reader, wrapping any IOException as an unchecked IOError.
	 * @see streamIO.Byte.IStreamIn_Byte#jump(long)	 */
	public long jump(final long n) {
	    try { return reader.skip(n); 
	    } catch (final IOException x) {
	        throw new IOError(x); 
	    }
	}
	
	/** Tests availability by reading one Character ahead and pushing it back.
	 * @see streamIO.Byte.IStreamIn_Byte#available()	 */
	public int available() throws IOException {
		//reader.mark(1);  
		final int ret = reader.read() < 0 ? 0 : 1;  
		//reader.reset(); 
		pushBack(); 
		return ret; }
	
	/** Delegates to the wrapped Reader's close().
	 * @see streamIO.Byte.IStreamIn_Byte#close()	 */
	public void close() throws IOException { reader.close(); }

	/** Marks the current position in the wrapped Reader.
	 * @see streamIO.Byte.IStreamIn_Byte#mark(int)	 */
	public void mark(final int readLimit)                  {
		try { reader.mark(readLimit); 
		} catch (IOException x) {
			throw new IOError(x); }
	}
	
	/** Marks the current position in the wrapped Reader, with no read limit.
	 * @see streamIO.Byte.IStreamIn_Byte#mark()	 */
	public IMarkAble mark() {
		try {reader.mark(Integer.MAX_VALUE); return this; 
		} catch (IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** Resets the wrapped Reader to its last mark()ed position.
	 * @see streamIO.Byte.IStreamIn_Byte#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
	    try {
	        reader.reset();
	    } catch (final IOException x) {
	        throw new IOError(x); 
	    }
	    return this; 
	}
	
	/** Resets the wrapped Reader to its last mark, then skips forward {@code position} Characters.
	 * @see streamIO.Byte.IStreamIn_Byte#reSet(long)	 */
	public long reSet(final long position) { //throws IOException {
	    try {
	        reader.reset(); 
	        return reader.skip(position); 
	    } catch (final IOException x) {
	        throw new IOError(x); 
	    }
	}
	
	/** Returns Integer.MAX_VALUE when the wrapped Reader supports marking, -1 otherwise.
	 * @see streamIO.Byte.IStreamIn_Byte#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return reader.markSupported() ? Integer.MAX_VALUE : -1; }

	/** Reads and returns the next Character (widened to int) from the wrapped Reader.
	 * @see streamIO.Float.IStreamIn_Int#nextInt()	 */
	public int nextInt() {
		try { return reader.read(); 
		} catch (IOException x) {
			throw new IOError(x); }
	}
	
	/** Not tracked by the wrapped Reader; always returns {@link IOrdered#ORDER_NONE}.
	 * @see streamIO.Float.IStreamIn_Int#getOrder()	 */
	public byte getOrder() { return IOrdered.ORDER_NONE; }

	/** Not implemented; always returns 0.
	 * @see streamIO.integer.AStreamIn_Byte#getPosition()	 */
	public long getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
