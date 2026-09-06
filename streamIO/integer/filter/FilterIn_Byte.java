package streamIO.integer.filter;

import java.io.IOException;
import java.io.InputStream;

import streamIO.IReSetAble;
import streamIO.integer.AStreamIn_Byte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.adapter.InputStreamToStreamIn_Byte;
import streamIO.object.IStreamIn;
import function.IIntFunction;

/**
  * Title: FilterIn_Byte<p>
  * Description:
  * Provides a Default Delegation of an Input streamIO Filter
  * Additional Value is provided by converting from the Class InputStream
  * to the Interface IStreamIn_Byte.
  *
  * Rather than wrapping an InputStream with this Adapter,
  * exploit the Java Behavior of overwriting Implementations with identical Signatures
  * in Subclasses declaring a different Interface without asking or delegating!
  *
  * Design Decisions: 
  * for Performance Reasons the Decision between the InputStream and IStreamIn_Byte 
  * takes place everywhere, rather than the more modular Wrapping 
  * of an OutputStream by an IStreamOutByte 
  * 
  * Known SubClasses:
  * @see FilterIn_Base64
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;30;01<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:44:23Z
  * digest: 3707c3143dd44249ef5d719ec6aefee0e04ef1b20f923fc520de549f724204a3
  * stale: false
  * tags: [code/stream_filter]
  * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterIn_Byte
extends AStreamIn_Byte
//implements AStreamIn_Byte //rather use the AStreamIn_Byte Implementations! They use the read() Method!
{

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws IOException {
		System.out.println("Testing " + FilterIn_Byte.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(args); }

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the actual IStreamIn_Byte being delegated to. 
	 * made protected to speed up Access in Child Classes. 
	 * Since the inStream is not visible anymore, 
	 * there is no Danger of misusing streamIn anymore!
	 */
	protected IStreamIn_Byte streamIn; //

	/** Reference to the actual InputStream being delegated to. 	 */
//	private InputStream inStream;

	/** the actual stateful Mapping Function;  
	 * if null, the Identical Mapping is performed. 
	 */
	protected IIntFunction mapper;

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor 	 */
	protected FilterIn_Byte() { }
	
	/** Initializing Constructor taking a IStreamIn_Byte 	 */
	public FilterIn_Byte(final IStreamIn_Byte streamIn_) {
		this.streamIn = streamIn_; }
	
	/**
	 * Initializing Constructor delegating to an InputStream
	 * This is usually not necessary,
	 * because the InputStream can be directly subclassed
	 * and declared to implement IStreamIn_Byte!
	 */
	public FilterIn_Byte(final InputStream streamIn_) {
		this.streamIn = new InputStreamToStreamIn_Byte(streamIn_); }

	/** Initializing Constructor taking a IStreamIn_Byte 	 */
	public FilterIn_Byte(final IStreamIn_Byte streamIn_, final IIntFunction mapper_) {
		this.streamIn = streamIn_; 
		this.mapper = mapper_; }

	/**
	 * Initializing Constructor delegating to an InputStream
	 * This is usually not necessary,
	 * because the InputStream can be directly subclassed
	 * and declared to implement IStreamIn_Byte!
	 */
	public FilterIn_Byte(final InputStream streamIn_, final IIntFunction mapper_) {
		this.streamIn = new InputStreamToStreamIn_Byte(streamIn_); 
		this.mapper = mapper_; }

	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the byte order of the wrapped stream, or {@link IStreamIn#ORDER_NONE} when none is wrapped.
	 * @return the Order of the Data in the File  */
	public byte getOrder() {
		if (streamIn != null) {
			return streamIn.getOrder(); }
			return IStreamIn.ORDER_NONE; }

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
	  */
	public int read() throws IOException {
		if (mapper == null) {
			return streamIn.read(); }
		return mapper.Map(streamIn.read()); }

	/**
	  * Reads the next long Value of data from the input stream.
	  * The value byte is returned as a long in the range MinLong to MaxLong .
	  * If no byte is available because the end of the stream has been reached,
	  * the value -1 is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or -1 if the end of the stream is reached.
	  * @throws IOException - if an I/O error occurs.
	  */
	public long nextLong() { 
		if (mapper == null) {
			return streamIn.nextLong(); }
		return mapper.Map(streamIn.nextLong()); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
    /** Returns the current read position of the wrapped stream.
     * @see streamIO.integer.AStreamIn_Byte#getPosition()     */
    public long getPosition() { return streamIn.getPosition(); }

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
	  * @throws IOException - if an I/O error occurs.
	  */
	public int available() throws IOException { return streamIn.available(); }

	/**
	  * Closes this input stream and releases any system resources associated with the stream.
	  * The close method of InputStream does nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  */
	public void close() throws IOException { streamIn.close(); }

	/**
	  * Marks the current position in this input stream.
	  * A subsequent call to the reset method repositions this stream
	  * at the last marked position so that subsequent reads re-read the same bytes.
	  * The readlimit arguments tells this input stream
	  * to allow that many bytes to be read before the mark position gets invalidated.
	  *
	  * The general contract of mark is that,
	  * if the method markSupported returns true,
	  * the stream somehow remembers all the bytes read after the call to mark
	  * and stands ready to supply those same bytes again
	  * if and whenever the method reset is called.
	  * However, the stream is not required to remember any data at all
	  * if more than readlimit bytes are read from the stream before reset is called.
	  *
	  * The mark method of InputStream does nothing.
	  * @param readlimit - the maximum limit of bytes that can be read
	  * 	before the mark position becomes invalid.
	  * @see reset()
	  */
	public void mark(int readlimit) { streamIn.mark(readlimit); }
	
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
	  */
	public IReSetAble reSet() { return streamIn.reSet(); }

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
	  */
	public long reSet(final long position) { return streamIn.reSet(position); }

	/**
	  * Skips over and discards n bytes of data from this input stream.
	  * The skip method may, for a variety of reasons,
	  * end up skipping over some smaller number of bytes, possibly 0.
	  * This may result from any of a number of conditions;
	  * reaching end of file before n bytes have been skipped is only one possibility.
	  * The actual number of bytes skipped is returned.
	  * If n is negative, no bytes are skipped.
	  * The skip method of InputStream creates a byte array
	  * and then repeatedly reads into it until n bytes have been read
	  * or the end of the stream has been reached.
	  * Subclasses are encouraged to provide a more efficient implementation of this method.
	  *
	  * @param n - the number of bytes to be skipped.
	  * @return the actual number of bytes skipped.
	  * @throws IOException - if an I/O error occurs.
	  */
	public long jump(final long len) { return streamIn.jump(len); }

	/**
	  * Returns how many bytes ahead of the mark this stream can still guarantee a reset.
	  * @return the maximum readlimit this stream honors for {@link #mark(int)}.
	  * @see mark(int), reset()
	  */
	public long getMaxMarkSize() { return streamIn.getMaxMarkSize(); }

	/** Reads bytes into the given range, applying the mapper (if any) to each byte read.
	 * @see streamIO.Byte.IStreamIn_Byte#read(byte[], int, int)	 */
	public int read(final byte[] b, final int off, final int len) throws IOException {
		int length = streamIn.read(b, off, len);
		if (mapper != null) {
			for(int i = length; --i >= 0;) {
				b[i] = (byte) mapper.Map(b[i] & 0xFF);
			}
		}
		return length; }

}
