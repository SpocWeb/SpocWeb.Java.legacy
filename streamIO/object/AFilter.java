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
	
	/** @see streamIO.IStreamOut#flush()	 */
	public void flush() throws IOException {
		if ((out != null) && (out instanceof IStreamOut))
			((IStreamOut)out).flush(); 
	}
	
	/**
	 * @see streamIO.IStreamOut#addItems(IStreamIn)
	 */
	public long addItems(final IIStreamIn arg) {
		return AStreamOut.STREAM(arg, this); }
	
	/**
	 * @see streamIO.IStreamOut#addItems(Object, int)
	 */
	public long addItems(final Object arg, final int flatDepth) {
		return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }
	
	/**
	 * @see streamIO.IStreamOut#addItems(Object)
	 */
	public long addItems(final Object arg) {
		return AStreamOut.ADD_ITEMS(this, arg, 1); }
	
	/**
	 * @see streamIO.IStreamOut#addItems(Object[])
	 */
	public long addItems(final Object[] arg) {
		return AStreamOut.ADD_ITEMS(this, arg); }
	
	//public static void main(final String[] args) throws Exception { }
	
}
