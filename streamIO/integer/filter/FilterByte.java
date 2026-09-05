package streamIO.integer.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.adapter.OutputStreamToStreamOutByte;
import function.IIntFunction;

/**
 * Title: FilterByte
 * <p>
 * Description: Provides a Default Delegation of an Input streamIO Filter
 * Additional Value is provided by converting from the Class OutputStream to the
 * Interface IStreamOutByte.
 * 
 * Rather than wrapping an Input- or OutputStream with this Adapter, exploit the
 * Java Behavior of overwriting Implementations with identical Signatures in
 * Subclasses declaring a different Interface without asking or delegating!
 * 
 * Design Decisions: for Performance Reasons the Decision between the
 * OutputStream and IStreamOutByte takes place everywhere, rather than the more
 * modular Wrapping of an OutputStream by an IStreamOutByte
 * 
 * Known SubClasses:
 * 
 * Copyright: Copyright (c) Matthias Heuer
 * <p>
 * Company: personal
 * <p>
 * Created on 2001-02-24, 12;58;57
 * <p>
 * 
 * @author Matthias Heuer
 * @version 1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:43:40Z
 * digest: f5296f0680b88413e9ed7b56479996f17feaef5e2d7adeecf9a15458629f663f
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FilterByte 
extends FilterIn_Byte 
implements IStreamOutByte {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the actual IStreamIn_Byte being delegated to. */
	//	protected IStreamIn_Byte streamIn;
	
	/** Reference to the actual InputStream being delegated to. */
	//	protected InputStream inStream;
	
	/** Reference to the actual IStreamOutByte being delegated to. */
	protected IStreamOutByte streamOut;

	/** Reference to the actual IStreamOutByte being delegated to. */
	//	private OutputStream outStream;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor taking a IStreamOutByte */
	public FilterByte(final IStreamOutByte streamOut, final IIntFunction _mapper) {
		this.streamOut = streamOut;
		this.mapper = _mapper; 
	}
	
	/**
	 * Initializing Constructor delegating to an OutputStream This is usually
	 * not necessary, because the OutputStream can be directly subclassed and
	 * declared to implement IStreamIn_Byte!
	 */
	public FilterByte(final OutputStream streamOut, final IIntFunction _mapper) {
		this.streamOut = new OutputStreamToStreamOutByte(streamOut);
		this.mapper = _mapper; 
	}
	
	/** Initializing Constructor taking a IStreamOutByte */
	public FilterByte(final IStreamOutByte _streamOut) { this(_streamOut, null); }
	
	/**
	 * Initializing Constructor delegating to an OutputStream This is usually
	 * not necessary, because the OutputStream can be directly subclassed and
	 * declared to implement IStreamIn_Byte!
	 */
	public FilterByte(final OutputStream _streamOut) { this(_streamOut, null); }
	
	/** Initializing Constructor taking a IStreamIn_Byte */
	public FilterByte(final IStreamIn_Byte _streamIn) { super(_streamIn); }
	
	/**
	 * Initializing Constructor delegating to an InputStream This is usually not
	 * necessary, because the InputStream can be directly subclassed and
	 * declared to implement IStreamIn_Byte!
	 */
	public FilterByte(final InputStream _streamIn) { super(_streamIn); }
	
	/**
	 * Creates a filter delegating to the given Input Stream, mapping each byte read
	 * through the given function.
	 * @param _streamIn
	 * @param _mapper
	 */
	public FilterByte(final InputStream _streamIn, final IIntFunction _mapper) {
		super(_streamIn, _mapper); }

	/**
	 * Creates a filter delegating to the given Input Stream, mapping each byte read
	 * through the given function.
	 * @param _streamIn
	 * @param _mapper
	 */
	public FilterByte(final IStreamIn_Byte _streamIn, final IIntFunction _mapper) {
		super(_streamIn, _mapper); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes the specified byte to this output stream. The general contract for
	 * write is that one byte is written to the output stream. The byte to be
	 * written is the eight low-order bits of the argument b. The 24 high-order
	 * bits of b are ignored.
	 * 
	 * Subclasses of OutputStream must provide an implementation for this
	 * method.
	 * 
	 * @param b -
	 *            the byte.
	 * @throws IOException -
	 *             if an I/O error occurs. In particular, an IOException may be
	 *             thrown if the output stream has been closed.
	 */
	public void write(int b) throws IOException {
		if (mapper != null) 
			b = mapper.Map(b); 
		streamOut.write(b); 
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Flushes this output stream and forces any buffered output bytes to be
	 * written out. The general contract of flush is that calling it is an
	 * indication that, if any bytes previously written have been buffered by
	 * the implementation of the output stream, such bytes should immediately be
	 * written to their intended destination.
	 * 
	 * The flush method of OutputStream does nothing.
	 * 
	 * @throws IOException -
	 *             if an I/O error occurs.
	 */
	public void flush() throws IOException {
		if (streamOut != null) 
			streamOut.flush();
	}

	/**
	 * Closes this output stream and releases any system resources associated
	 * with this stream. The general contract of close is that it closes the
	 * output stream. A closed stream cannot perform output operations and
	 * cannot be reopened.
	 * 
	 * The close method of OutputStream does nothing.
	 * 
	 * @throws IOException -
	 *             if an I/O error occurs.
	 */
	public void close() throws IOException {
		flush(); //in case the flush does some additional Operations.
		if (streamOut != null)
			streamOut.close();
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes len bytes from the specified byte array starting at offset off to
	 * this output stream. The general contract for write(b, off, len) is that
	 * some of the bytes in the array b are written to the output stream in
	 * order; element b[off] is the first byte written and b[off+len-1] is the
	 * last byte written by this operation. The write method of OutputStream
	 * calls the write method of one argument on each of the bytes to be written
	 * out. Subclasses are encouraged to override this method and provide a more
	 * efficient implementation.
	 * 
	 * If b is null, a NullPointerException is thrown.
	 * 
	 * If off is negative, or len is negative, or off+len is greater than the
	 * length of the array b, then an IndexOutOfBoundsException is thrown.
	 * 
	 * @param b -
	 *            the data.
	 * @param off -
	 *            the start offset in the data.
	 * @param len -
	 *            the number of bytes to write.
	 * @throws IOException -
	 *             if an I/O error occurs. In particular, an IOException is
	 *             thrown if the output stream is closed.
	 */
	public void write(final byte[] b, final int off, final int len) throws IOException {
		//streamOut.write(b, off, len); //fast, but incorrect, what would a Filter be good for, if it didn't perform Filtering at all?
		//super.write(b, off, len); 
		for(int i = -1; ++i < len;) {
			write(b[off + i]); }
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: Methods handling Characters and Strings
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes b.length Characters from the specified byte array to this output
	 * stream. The general contract for write(b) is that it should have exactly
	 * the same effect as the call write(b, 0, b.length).
	 * 
	 * @param b -
	 *            the data.
	 * @see write(byte[], int, int)
	 */
	public void write(final char[] b) throws IOException { write(b, 0, b.length); }

	/**
	 * Writes len Characters from the specified byte array starting at offset
	 * off to this output stream. The general contract for write(b, off, len) is
	 * that some of the Characters in the array b are written to the output
	 * stream in order; element b[off ] is the first Character written and
	 * b[off+len-1] is the last Character written by this operation. The write
	 * method of OutputStream calls the write method of one argument on each of
	 * the bytes to be written out. Subclasses are encouraged to override this
	 * method and provide a more efficient implementation.
	 * 
	 * If b is null, a NullPointerException is thrown.
	 * 
	 * If off is negative, or len is negative, or off+len is greater than the
	 * length of the array b, then an IndexOutOfBoundsException is thrown.
	 * 
	 * @param b -
	 *            the data.
	 * @param off -
	 *            the start offset in the data.
	 * @param len -
	 *            the number of bytes to write.
	 * @throws IOException -
	 *             if an I/O error occurs. In particular, an IOException is
	 *             thrown if the output stream is closed.
	 */
	public void write(final char[] b, final int off, final int len) throws IOException {
		AStreamOutByte.WRITE(this.streamOut, b, off, len); }

	/**
	 * Writes b.length Characters from the specified byte array to this output
	 * stream. The general contract for write(b) is that it should have exactly
	 * the same effect as the call write(b, 0, b.length).
	 * 
	 * @param b -
	 *            the data.
	 * @throws IOException -
	 *             if an I/O error occurs.
	 * @see write(byte[], int, int)
	public IStreamOutByte addItem(final int[] b) {
		return addItem(b, 0, b.length); }
	 */

	/**
	 * Writes len Characters from the specified byte array starting at offset
	 * off to this output stream. The general contract for write(b, off, len) is
	 * that some of the Characters in the array b are written to the output
	 * stream in order; element b[off ] is the first Character written and
	 * b[off+len-1] is the last Character written by this operation. The write
	 * method of OutputStream calls the write method of one argument on each of
	 * the bytes to be written out. Subclasses are encouraged to override this
	 * method and provide a more efficient implementation.
	 * 
	 * If b is null, a NullPointerException is thrown.
	 * 
	 * If off is negative, or len is negative, or off+len is greater than the
	 * length of the array b, then an IndexOutOfBoundsException is thrown.
	 * 
	 * @param b -
	 *            the data.
	 * @param off -
	 *            the start offset in the data.
	 * @param len -
	 *            the number of bytes to write.
	 * @throws IOException -
	 *             if an I/O error occurs. In particular, an IOException is
	 *             thrown if the output stream is closed.
	public void addItem(final int[] b, final int off, final int len) {
		AStreamOutByte.WRITE_SAFE(this.streamOut, b, off, len);
	}
	 */

	/**
	 * Writes b.length Characters from the specified String to this Output
	 * streamIO. The general contract for write(b) is that it should have
	 * exactly the same effect as the call write(b, 0, b.length).
	 * 
	 * @param b -
	 *            the data.
	 * @throws IOException -
	 *             if an I/O error occurs.
	 * @see write(byte[], int, int)
	 */
	public void write(final String b) throws IOException {
		write(b, 0, b.length());
	}

	/**
	 * Writes len Characters from the specified String starting at Offset off to
	 * this Output streamIO. The general contract for write(b, off, len) is that
	 * some of the Characters in the array b are written to the output stream in
	 * order; element b[off ] is the first Character written and b[off+len-1] is
	 * the last Character written by this operation. The write method of
	 * OutputStream calls the write method of one argument on each of the bytes
	 * to be written out. Subclasses are encouraged to override this method and
	 * provide a more efficient implementation.
	 * 
	 * If b is null, a NullPointerException is thrown.
	 * 
	 * If off is negative, or len is negative, or off+len is greater than the
	 * length of the array b, then an IndexOutOfBoundsException is thrown.
	 * 
	 * @param b -
	 *            the data.
	 * @param off -
	 *            the start offset in the data.
	 * @param len -
	 *            the number of bytes to write.
	 * @throws IOException -
	 *             if an I/O error occurs. In particular, an IOException is
	 *             thrown if the output stream is closed.
	 */
	public void write(String b, int off, int len) throws IOException {
		AStreamOutByte.WRITE(this, b, off, len);
	}
	
	/**
	 * Writes b.length Characters from the specified String to this Output
	 * streamIO. The general contract for write(b) is that it should have
	 * exactly the same effect as the call write(b, 0, b.length).
	 * 
	 * @param b -
	 *            the data.
	 * @throws IOException -
	 *             if an I/O error occurs.
	 * @see write(byte[], int, int)
	 */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop) { 
		return addBuffer(b, stop, 0); }
	
	/**
	 * Writes b.length Characters from the specified String to this Output
	 * streamIO. The general contract for write(b) is that it should have
	 * exactly the same effect as the call write(b, 0, b.length).
	 * 
	 * @param b -
	 *            the data.
	 * @throws IOException -
	 *             if an I/O error occurs.
	 * @see write(byte[], int, int)
	 */
	public IStreamOutByte addBuffer(final StringBuffer b) { 
		return addBuffer(b, b.length()); }
	
	/**
	 * Writes len Characters from the specified String starting at Offset off to
	 * this Output streamIO. The general contract for write(b, off, len) is that
	 * some of the Characters in the array b are written to the output stream in
	 * order; element b[off ] is the first Character written and b[off+len-1] is
	 * the last Character written by this operation. The write method of
	 * OutputStream calls the write method of one argument on each of the bytes
	 * to be written out. Subclasses are encouraged to override this method and
	 * provide a more efficient implementation.
	 * 
	 * If b is null, a NullPointerException is thrown.
	 * 
	 * If off is negative, or len is negative, or off+len is greater than the
	 * length of the array b, then an IndexOutOfBoundsException is thrown.
	 * 
	 * @param b -
	 *            the data.
	 * @param off -
	 *            the start offset in the data.
	 * @param len -
	 *            the number of bytes to write.
	 * @throws IOException -
	 *             if an I/O error occurs. In particular, an IOException is
	 *             thrown if the output stream is closed.
	 */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop, final int start) { 
		AStreamOutByte.WRITE_SAFE(this.streamOut, b, stop, start);
		return this; }
	
	/** Writes the whole string to this stream.
	 * @see streamIO.integer.IStreamOutByte#addString(java.lang.String)	 */
	public IStreamOutByte addString(final String b) { return addString(b, b.length()); }

	/** Writes the string up to the given stop index to this stream.
	 * @see streamIO.integer.IStreamOutByte#addString(java.lang.String, int)	 */
	public IStreamOutByte addString(final String b, final int stop) {
		return addString(b, stop, 0); }

	/** Writes the given range of the string to this stream.
	 * @see streamIO.integer.IStreamOutByte#addString(java.lang.String, int, int)	 */
	public IStreamOutByte addString(final String b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; }
	
	/**
	 * Writes b.length bytes from the specified byte array to this output
	 * stream. The general contract for write(b) is that it should have exactly
	 * the same effect as the call write(b, 0, b.length).
	 * 
	 * @param b -
	 *            the data.
	 * @throws IOException -
	 *             if an I/O error occurs.
	 * @see write(byte[], int, int)
	 */
	public void write(final byte[] b) throws IOException { write(b, 0, b.length); }

	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public IStreamOutByte escapeString(final String str, final String separators) {
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
	public IStreamOutByte escapeString(final StringBuffer str, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, str, separators);
		return this; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class */
	public static void testIt(final String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterByte.class.getName());
	}
	
	/**
	 * The main entry point for the application.
	 * 
	 * @param args
	 *            Array of parameters passed to the application via the command
	 *            line.
	 */
	public static void main(final String[] args) throws Exception {
		testIt(args);
	}

}