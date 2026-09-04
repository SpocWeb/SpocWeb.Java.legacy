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
	
	/** @see streamIO.Byte.IStreamIn_Byte#read()	 */
	public int read() throws IOException {
		return reader.read(); } //rather plug in a Filter that also logs! 
	/*	final int ret = reader.read(); 
		System.out.print((char)ret); 
		return ret; }
	*/
	
	/** @see streamIO.Float.IStreamIn_Int#nextLong()	 */
	public long nextLong() { 
		try { return reader.read(); 
		} catch(final IOException x) {
			throw new IOError(x); }
	}
	
	/** @see streamIO.Byte.IStreamIn_Byte#read(byte[])	 */
	public int read(final byte[] b) throws IOException { return read(b, 0, b.length); }
	
	/** @see streamIO.Byte.IStreamIn_Byte#read(byte[], int, int)	 */
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
	
	/** @see streamIO.Byte.IStreamIn_Byte#read(int)	 */
	public StringBuffer read(final int sep) throws IOException {
		return read(sep, new StringBuffer()); }
	
	/** @see streamIO.Byte.IStreamIn_Byte#read(char[])	 */
	public int read(final char[] b) throws IOException { return reader.read(b); }
	
	/** @see streamIO.Byte.IStreamIn_Byte#read(char[], int, int)	 */
	public int read(final char[] b, final int off, final int len) throws IOException {
		return reader.read(b, off, len); }
	
	/** @see streamIO.Byte.IStreamIn_Byte#read(int[])	 */
	public int read(final int[] b) throws IOException { return read(b, 0, b.length); }
	
	/** @see streamIO.Byte.IStreamIn_Byte#read(int[], int, int)	 */
	public int read(final int[] b, final int off, final int len) throws IOException {
		final int stop = off+len; 
		for (int i = off-1; ++i < stop;) {
			int val;
			if ((val = reader.read()) < 0) {
				return i-off; }
			b[i] = (byte) val;
		}
		return len; }
	
	/** @see streamIO.Byte.IStreamIn_Byte#jump(long)	 */
	public long jump(final long n) { 
	    try { return reader.skip(n); 
	    } catch (final IOException x) {
	        throw new IOError(x); 
	    }
	}
	
	/** @see streamIO.Byte.IStreamIn_Byte#available()	 */
	public int available() throws IOException { 
		//reader.mark(1);  
		final int ret = reader.read() < 0 ? 0 : 1;  
		//reader.reset(); 
		pushBack(); 
		return ret; }
	
	/** @see streamIO.Byte.IStreamIn_Byte#close()	 */
	public void close() throws IOException { reader.close(); }
	
	/** @see streamIO.Byte.IStreamIn_Byte#mark(int)	 */
	public void mark(final int readLimit)                  { 
		try { reader.mark(readLimit); 
		} catch (IOException x) {
			throw new IOError(x); }
	}
	
	/** @see streamIO.Byte.IStreamIn_Byte#mark()	 */
	public IMarkAble mark() { 
		try {reader.mark(Integer.MAX_VALUE); return this; 
		} catch (IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** @see streamIO.Byte.IStreamIn_Byte#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
	    try {
	        reader.reset();
	    } catch (final IOException x) {
	        throw new IOError(x); 
	    }
	    return this; 
	}
	
	/** @see streamIO.Byte.IStreamIn_Byte#reSet(long)	 */
	public long reSet(final long position) { //throws IOException {
	    try {
	        reader.reset(); 
	        return reader.skip(position); 
	    } catch (final IOException x) {
	        throw new IOError(x); 
	    }
	}
	
	/** @see streamIO.Byte.IStreamIn_Byte#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return reader.markSupported() ? Integer.MAX_VALUE : -1; }
	
	/** @see streamIO.Float.IStreamIn_Int#nextInt()	 */
	public int nextInt() { 
		try { return reader.read(); 
		} catch (IOException x) {
			throw new IOError(x); }
	}
	
	/** @see streamIO.Float.IStreamIn_Int#getOrder()	 */
	public byte getOrder() { return IOrdered.ORDER_NONE; }
	
	/** @see streamIO.integer.AStreamIn_Byte#getPosition()	 */
	public long getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
