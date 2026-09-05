package streamIO.integer.filter;

import java.io.IOException;
import java.io.OutputStream;

import streamIO.IIStreamOut;
import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamOutByte;

/**
  * Title: FilterOutByte<p>
  * Description:
  * Provides a Default Delegation of an Input streamIO Filter
  * Additional Value is provided by converting from the Class OutputStream
  * to the Interface IStreamOutByte.
  *
  * Rather than wrapping an OutputStream with this Adapter,
  * exploit the Java Behavior of overwriting Implementations with identical Signatures
  * in Subclasses declaring a different Interface without asking or delegating!
  *
  * Design Decisions: 
  * for Performance Reasons the Decision between the OutputStream and IStreamOutByte 
  * takes place everywhere, rather than the more modular Wrapping 
  * of an OutputStream by an IStreamOutByte 
  * 
  * Known SubClasses:
  * @see FilterOutLookup
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;30;01<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:45:34Z
  * digest: 270ee960a4911e868692f8afa81d2b37884d1da0767b7afc5ffbd659aa43b50f
  * stale: false
  * tags: [code/stream_filter]
  * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterOutByte
extends AStreamOutByte
//implements AStreamOutByte //rather use the AStreamOutByte Implementations! They use the read() Method!
{

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the actual IStreamOutByte being delegated to. 	 */
	protected IStreamOutByte streamOut;

	/** Reference to the actual IStreamOutByte being delegated to. 	 */
	protected OutputStream outStream;

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor taking a IStreamOutByte	 */
	public FilterOutByte(final IStreamOutByte _streamOut) {
		this.streamOut = _streamOut; }
	
	/**
	 * Initializing Constructor delegating to an OutputStream
	 * This is usually not necessary,
	 * because the OutputStream can be directly subclassed
	 * and declared to implement IStreamIn_Byte!
	 */
	public FilterOutByte(final OutputStream _streamOut) {
		this.outStream = _streamOut; }
	
	/**
	 * Initializing empty Constructor 
	 */
	public FilterOutByte() { }
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Writes the string representation of the given object to this stream.
	 * @see streamIO.AStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final Object _arg) {
		final String arg = String.valueOf(_arg); 
		AStreamOutByte.WRITE_SAFE(this, arg, arg.length(), 0); //this dispatches automatically (see below) 
		/*
		if (streamOut != null) 
			AStreamOutByte.WRITE_SAFE(streamOut, arg, arg.length(), 0); 
		else
			AStreamOutByte.WRITE_SAFE(outStream, arg, arg.length(), 0);
		*/ 
		return this; }
	
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
	public void write(final int b) throws IOException {
		if (streamOut != null) {
			streamOut.write(b); return; }
			outStream.write(b); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
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
	public void flush() throws IOException {
		if (streamOut != null) 
			streamOut.flush(); 
		else
			outStream.flush(); }

	/**
	  * Closes this output stream and releases any system resources associated with this stream.
	  * The general contract of close is that it closes the output stream.
	  * A closed stream cannot perform output operations and cannot be reopened.
	  *
	  * The close method of OutputStream does nothing.
	  * @throws IOException - if an I/O error occurs.
	  */
	public void close() throws IOException {
		if (streamOut != null) 
			streamOut.close(); 
		else
			outStream.close(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Optimizations, but these circumvent the write Method! 
	////////////////////////////////////////////////////////////////////////////////
	
	/** writes the whole Buffer to the Stream
	 */
	public void write(final byte[] buf, final int start, final int len) throws IOException {
		if (streamOut != null) {
			//streamOut.write(buf, start, len); //this circumvents the write(int) Method! 
			final int stop = start+len; 
			for(int i = start-1; ++i < stop;)
				streamOut.write(buf[i]); 
		} else {
			//outStream.write(buf, start, len); //this circumvents the write(int) Method!
			final int stop = start+len; 
			for(int i = start-1; ++i < stop;)
				outStream.write(buf[i]); 
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		System.out.println("Testing " + FilterOutByte.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}
