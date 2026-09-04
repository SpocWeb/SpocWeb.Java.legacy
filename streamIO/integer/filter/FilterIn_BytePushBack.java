/*
 * Created on 29.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.filter;

import java.io.InputStream;

import streamIO.IPushBackAble;
import streamIO.integer.IStreamIn_Byte;
import function.IIntFunction;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Adds the PushBack Functionality to any Byte Stream. 
 * This puts the Parsing State completely on the Stream, 
 * so the LL(1) Parsing Class can become easier 
 * and doesn't need to handle the current Character. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public class FilterIn_BytePushBack 
extends FilterIn_Byte {
	
	/**
	 * @param streamIn_
	 */
	public FilterIn_BytePushBack(IStreamIn_Byte streamIn_) {
		super(streamIn_);
	}
	
	/**
	 * @param streamIn_
	 */
	public FilterIn_BytePushBack(InputStream streamIn_) {
		super(streamIn_);
	}
	
	/**
	 * @param streamIn_
	 * @param mapper_
	 */
	public FilterIn_BytePushBack(IStreamIn_Byte streamIn_, IIntFunction mapper_) {
		super(streamIn_, mapper_);
	}
	
	/**
	 * @param streamIn_
	 * @param mapper_
	 */
	public FilterIn_BytePushBack(InputStream streamIn_, IIntFunction mapper_) {
		super(streamIn_, mapper_);
	}
	
	/**
	 *  returns this Stream if another pushBack() is allowed. 
	 * @param value the Value to be pushed back. 
	 * @return this Stream if another pushBack() is allowed. 
	 */
	public FilterIn_BytePushBack pushBack(final int value) {
		if (this.filter == currItem) //indicator that this Stream has been pushed back! 
			return null; //only allows a single PushBack! 
		currItem.Value = (byte) value; this.filter = currItem; 
		return this; 
	}
	
	/**
	 * 
	 * @return this Stream if another pushBack() is allowed. 
	 */
	public IPushBackAble pushBack() { return pushBack(currItem.Value); }

	/**
	  * @return the next byte of data, or -1 if the end of the stream is reached.
	  */
	public int nextInt() { 
		if (filter == currItem) { //indicator that this Stream has been pushed back!
			filter = null; return currItem.Value; }
		return currItem.Value = super.nextInt(); }
	
}
