/**
 * File  Name: FilterBuffer.java
 * Created on: 13.02.2003
 */
package streamIO.integer.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.Assert;
import streamIO.StringBufferOutputStream;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.pipe.ByteStreamerThread;

/**
 * Title: FilterBuffer<p>
 * Purpose:
 * Implements a dynamically growing asynchronous Buffer / Cache / Queue 
 * between an InputStream and an OutputStream. 
 *
 * Design Decisions / Implementation Details:
 * Buffered Streams are very effective for Reading and / or Writing, 
 * if only using the Bulk read/write Routine
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * 
 * FIFO (or LIFO) RAM Buffer with Wrap-Around Logic similar to 
 * @see the C# Class MemoryStream which implements only a FIFO
 * @see graphs.SparseMatrix which uses an integer Array in the same Manner
 * 
 * @author mheuer
 * @version	1.0
 *
 */
public class FilterBuffer 
extends FilterByte {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Buffer / Cache to read ahead the Streams, should be encapsulated in a byte Queue */
	private final byte[] buffer; 

	/** current Position to separate the Streams */
	private int bufferPosition = 0; 

	/** Length of the used Buffer when Reading */
	private int length; 

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor for FilterBuffer.
	 * @param streamOut
	 */
	public FilterBuffer(final IStreamOutByte streamOut, final byte[] buffer_) { 
		super(streamOut); 
		this.buffer = buffer_;
	}

	/**
	 * Constructor for FilterBuffer.
	 * @param streamOut
	 */
	public FilterBuffer(final IStreamOutByte streamOut, final int bufferSize_) { 
		this(streamOut, new byte[bufferSize_]); }

	/**
	 * Constructor for FilterBuffer.
	 * @param streamOut
	 */
	public FilterBuffer(final OutputStream streamOut, final int bufferSize_) { 
		this(streamOut, new byte[bufferSize_]); }

	/**
	 * Constructor for FilterBuffer.
	 * @param streamOut
	 */
	public FilterBuffer(final OutputStream streamOut, final byte[] buffer_) { 
		super(streamOut); 
		this.buffer = buffer_;
	}

	/**
	 * Constructor for FilterBuffer.
	 * @param streamOut
	 */
	public FilterBuffer(final IStreamIn_Byte streamOut, final byte[] buffer_) { 
		super(streamOut); 
		this.buffer = buffer_;
	}

	/**
	 * Constructor for FilterBuffer.
	 * @param streamOut
	 */
	public FilterBuffer(final InputStream streamIn_, final byte[] buffer_) { 
		super(streamIn_); 
		this.buffer = buffer_;
	}

	/**
	 * Constructor for FilterBuffer.
	 * @param streamOut
	 */
	public FilterBuffer(final IStreamIn_Byte streamIn_, final int bufferSize_) { 
		this(streamIn_, new byte[bufferSize_]); }

	/**
	 * Constructor for FilterBuffer.
	 * @param streamOut
	 */
	public FilterBuffer(final InputStream streamIn_, final int bufferSize_) { 
		this(streamIn_, new byte[bufferSize_]); }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent AFilter: Implementation / Overrides
	////////////////////////////////////////////////////////////////////////////////
	
	public void close() throws IOException {
		if (bufferPosition >= 0){
			streamOut.write(buffer, 0, bufferPosition); }
	}

	/**
	 * Defines a new Protocol Interpretation: -1 closes the File / streamIO 
	 * and triggers the Creation of a new one! 
	 * @see streamIO.Byte.IStreamOutByte#addString(int)
	 */
	public void write(final int val) throws IOException {
		buffer[bufferPosition] = (byte) val;
		if (++bufferPosition == buffer.length) { //write early!
			bufferPosition = 0;
			super.write(buffer); //writes the whole Buffer to the Stream in one sweep
		}
	}

	/** @see streamIO.Byte.IStreamIn_Byte#read()	 */
	public int read() throws IOException {
		if (++bufferPosition >= length) { //read late
			bufferPosition = 0;
			if (0 >= (length = super.read(buffer))) {
				return -1; 
			}
		}
		return buffer[bufferPosition]; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterBuffer.class.getName());
		String value = "12345678901";
		StringBufferOutputStream buffer = new StringBufferOutputStream();
		IStreamIn_Byte in_ = new FilterBuffer(new ByteArrayInputStream(value.getBytes()), 3);
		IStreamOutByte out = new FilterBuffer((IStreamOutByte) buffer, 2); 
		ByteStreamerThread.STREAM(in_, out); out.close(); 
		Assert.EQUALS(value, buffer.toString()); 
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 
	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
