/**
 * File  Name: InputStreamToStreamIn_Byte.java
 * Created on: 23.02.2003
 */
package streamIO.integer.adapter;

import java.io.IOException;
import java.io.InputStream;

import streamIO.IMarkAble;
import streamIO.integer.AStreamIn_Byte;
import streamIO.integer.IStreamIn_Byte;
import tools.IOError;

/**
 * Title: enclosing_type<p>
 * Description:
 * A faster Alternative is to derive from the given InputStream directly, 
 * since both Interfaces have the same Signatures. 
 * Adapter Design is slightly slower, but cleaner and safer 
 * than the former if() Swirch in the Filter Classes
 * @see streamIO.Byte.FilterIn_Byte
 * @see streamIO.Byte.FilterOutByte
 * @see streamIO.Byte.FilterByte
 * 
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * 
 * See also: 
 * @see streamIO.Byte.StreamIn_ByteToInputStream which has nearly identical Code 
 * @see streamIO.Byte.InputStreamToStreamIn_Byte which has nearly identical Code 
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
public class InputStreamToStreamIn_Byte 
extends AStreamIn_Byte 
implements IStreamIn_Byte {
	
	/** Reference to the actual streamIO delegated to */
	protected InputStream stream; 
	
	/**
	 * Constructor for InputStreamToStreamIn_Byte.
	 */
	public InputStreamToStreamIn_Byte(final InputStream stream_) {
		this.stream = stream_;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamIn_Byte 
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Reads the next byte of data from the input stream.
	  * The value byte is returned as an int in the range 0 to 255.
	  * If no byte is available because the end of the stream has been reached,
	  * the value EOF is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or EOF if the end of the stream is reached.
	  * @throws IOException - if an I/O error occurs.
	  * @see streamIO.Byte.IStreamIn_Byte#read()	 
	  */
	public int read() throws IOException { return stream.read(); }
	
	/**
	  * Returns the number of bytes that can be read (or skipped over)
	  * from this input stream without blocking
	  * by the next caller of a method for this input stream.
	  * The next caller might be the same thread or or another thread.
	  *
	  * This Default Implementation assumes that the Number of Bytes stays the same.
	  * When using Compression or Encoding this method should be overridden.
	  *
	  * @return the number of bytes that can be read from this input stream without blocking.
	  * 	Since the last Byte and even the EOF Sign has already been read, 
	  * 	available() must return negative Numbers, 
	  * 	which is not guaranteed for common InputStreams.  
	  * @throws IOException - if an I/O error occurs.
	  * @see streamIO.Byte.IStreamIn_Byte#available()
	  */
	public int available() throws IOException { return stream.available()-1; }
	
	/**
	  * Closes this input stream and releases any system resources associated with the stream.
	  * The close method of InputStream does nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  * @see streamIO.Byte.IStreamIn_Byte#close()
	  */
	public void close() throws IOException { stream.close(); }
	
	/**
	  * Repositions this stream to the position
	  * at the time the mark method was last called on this input stream.
	  *
	  * The general contract of reset is:
	  * If the method markSupported returns true, then:
	  * If the method mark has not been called since the stream was created,
	  * or the number of bytes read from the stream since mark was last called
	  * is larger than the argument to mark at that last call,
	  * then an IOException might be thrown.
	  * If such an IOException is not thrown,
	  * then the stream is reset to a state such that all the bytes read
	  * since the most recent call to mark (or since the start of the file,
	  * if mark has not been called) will be resupplied to subsequent callers
	  * of the read method, followed by any bytes that otherwise would have been
	  * the next input data as of the time of the call to reset.
	  *
	  * If the method markSupported returns false, then:
	  * The call to reset may throw an IOException.
	  * If an IOException is not thrown, then the stream is reset to a fixed state
	  * that depends on the particular type of the input stream and how it was created.
	  * The bytes that will be supplied to subsequent callers of the read method
	  * depend on the particular type of the input stream.
	  * The method reset for class InputStream does nothing and always throws an IOException.
	  *
	  * @throws IOException - if this stream has not been marked or if the mark has been invalidated.
	  * @see mark(int)
	  * @see IOException
	  * @see streamIO.Byte.IStreamIn_Byte#reSet()
	  */
	public long reSet(final long position) { 
		try {
			stream.reset(); 
		    return stream.skip(position); 
		} catch (final IOException x) {
		    throw new IOError(x); 
		}
	}
	
	/**
	  * Tests if this input stream supports the mark and reset methods.
	  * The markSupported method of InputStream returns false.
	  * @return true if this true type supports the mark and reset method; false otherwise.
	  * @see mark(int), reset()
	  * @see streamIO.Byte.IStreamIn_Byte#getMaxMarkSize()	 
	  */
	public long getMaxMarkSize() { return stream.markSupported() ? Integer.MAX_VALUE : -1; }
	
	/** Marks the current position in the wrapped InputStream.
	 * @see streamIO.Byte.IStreamIn_Byte#mark(int)	 */
	public IMarkAble mark(long readLimit) { stream.mark((int) readLimit); return this; }

	/** Not tracked by the wrapped InputStream; always returns 0.
	 * @see streamIO.Float.IStreamIn_Int#getOrder()	 */
	public byte getOrder() { return 0; }

	/** Not tracked by the wrapped InputStream; always returns 0.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; } //stream.getPosition(); }
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Optimizations 
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Delegates to the wrapped InputStream's {@code read(byte[], int, int)}.
	 * @see streamIO.Byte.IStreamIn_Byte#read(byte[], int, int)	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len); }

	/** Delegates to the wrapped InputStream's {@code read(byte[])}.
	 * @see streamIO.Byte.IStreamIn_Byte#read(byte[])	 */
	public int read(byte[] b) throws IOException { return stream.read(b); }

	/** Skips {@code n} bytes on the wrapped InputStream, wrapping any IOException as an unchecked IOError.
	 * @see streamIO.Byte.IStreamIn_Byte#jump(long)	 */
	public long jump(final long n) {
	    try { return stream.skip(n); 
	    } catch(final IOException x) {
	        throw new IOError(x);
	    }
	}
	
}
