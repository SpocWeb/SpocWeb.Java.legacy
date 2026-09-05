/**
 * File  Name: Filter.java
 * Created on: 26.12.2002
 */
package streamIO.object;

import java.io.IOException;

import streamIO.AStreamOut;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;

/**
 * Bidirectional filter base class that implements the {@link IStreamOut} write side once so
 * subclasses only need to supply their own filtering logic.
 * <p>
 * Title: Filter <p>
 * Description:
 * Purpose:
 *
 * bidirectional Filter for implementing a Filter Functionality only once.
 *
 * Design Decisions / Implementation Details:
 *
 * Known Uses: <none>
 *
 * Known SubClasses:
 * @see streamIO.object.filterInOut.FilterByFunction
 * @see streamIO.object.filterInOut.FilterByTester
 * @see streamIO.object.filterInOut.FilterFileToName
 * @see streamIO.object.filterInOut.FilterReflectionFunction
 * @see streamIO.object.filterInOut.FilterSeparator
 * @see streamIO.object.filterInOut.FilterString
 * @see streamIO.real.FilterVectorStatistic
 *
 * Similar Classes:
 * @see streamIO.Byte.FilterByte
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:28:41Z
 * digest: b9672b93b4433980f9965fb384344e031581c691bec9a38c181447debabe8ef0
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public abstract class AFilter 
extends AFilterIn 
implements IStreamOut {
	
	///////////////////////////////////////////////////////////////////////////
	// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** Output streamIO */
	protected IIStreamOut out; 
	
	///////////////////////////////////////////////////////////////////////////
	// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor for Filter.
	 * @param Enum
	 */
	public AFilter(final IIStreamOut out_) {
		super(null); 
		this.out = out_; 
	}
	
	/**
	 * Constructor for Filter.
	 * @param Enum
	 */
	public AFilter(final IIStreamIn enum_) {
		super(enum_);
		out = null;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	// Interface IStreamOut
	///////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Flushes the underlying output stream when it supports flushing; otherwise this is a no-op.
	 *
	 * @throws IOException when the underlying stream fails to flush
	 * @see streamIO.IStreamOut#flush()
	 */
	public void flush() throws IOException {
		if ((out != null) && (out instanceof IStreamOut))
			((IStreamOut)out).flush(); 
	}
	
	/**
	 * Streams every item from {@code arg} into this filter's output.
	 *
	 * @return the number of items written
	 * @see streamIO.IStreamOut#addItems(IStreamIn)
	 */
	public long addItems(final IIStreamIn arg) {
		return AStreamOut.STREAM(arg, this); }

	/**
	 * Adds {@code arg} to this filter's output, flattening nested arrays or collections up to
	 * {@code flatDepth} levels deep.
	 *
	 * @return the number of items written
	 * @see streamIO.IStreamOut#addItems(Object, int)
	 */
	public long addItems(final Object arg, final int flatDepth) {
		return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }

	/**
	 * Adds {@code arg} to this filter's output, flattening one level of nested arrays or
	 * collections.
	 *
	 * @return the number of items written
	 * @see streamIO.IStreamOut#addItems(Object)
	 */
	public long addItems(final Object arg) {
		return AStreamOut.ADD_ITEMS(this, arg, 1); }

	/**
	 * Adds every element of {@code arg} to this filter's output.
	 *
	 * @return the number of items written
	 * @see streamIO.IStreamOut#addItems(Object[])
	 */
	public long addItems(final Object[] arg) {
		return AStreamOut.ADD_ITEMS(this, arg); }
	
	//public static void main(final String[] args) throws Exception { }
	
}
