/*
 * Created on 29.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.filterIn;

import streamIO.IIStreamIn;
import streamIO.IPushBackAble;
import streamIO.object.AFilterIn;

/**
 * Title: <p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
public class FilterIn_PushBack 
extends AFilterIn {

	/**
	 * @param enum_
	 */
	public FilterIn_PushBack(final IIStreamIn enum_) {
		super(enum_);
	}
	
	/** @see streamIO.object.AFilterIn#nextItemInternal()	 */
	protected Object nextItemInternal() {
		if (filter == currItem) { //indicator that this Stream has been pushed back!
			filter = null; return currItem; }
		return currItem = nextItemInternal(); }
	
	/**
	 * returns this Stream if another pushBack() is allowed. 
	 * @param value the Value to be pushed back. 
	 * @return this Stream if another pushBack() is allowed. 
	 */
	public FilterIn_PushBack pushBack(final Object value) {
		if (filter == currItem) //indicator that this Stream has been pushed back! 
			return null; //only allows a single PushBack! 
		filter = currItem = value;  
		return this; 
	}
	
	/**
	 * returns this Stream if another pushBack() is allowed. 
	 * @return this Stream if another pushBack() is allowed. 
	 */
	public IPushBackAble pushBack() { return pushBack(currItem); }
	
}
