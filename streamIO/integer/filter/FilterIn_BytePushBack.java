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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:45:00Z
 * digest: 87288a792d46e7fcff6f209553c1c5dad7ce6a7f00c0af6dd9aecafc3bd25f03
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FilterIn_BytePushBack 
extends FilterIn_Byte {
	
	/** Creates a push-back filter delegating to the given Input Stream.
	 * @param streamIn_
	 */
	public FilterIn_BytePushBack(IStreamIn_Byte streamIn_) {
		super(streamIn_);
	}

	/** Creates a push-back filter delegating to the given Input Stream.
	 * @param streamIn_
	 */
	public FilterIn_BytePushBack(InputStream streamIn_) {
		super(streamIn_);
	}

	/** Creates a push-back filter delegating to the given Input Stream and mapper.
	 * @param streamIn_
	 * @param mapper_
	 */
	public FilterIn_BytePushBack(IStreamIn_Byte streamIn_, IIntFunction mapper_) {
		super(streamIn_, mapper_);
	}

	/** Creates a push-back filter delegating to the given Input Stream and mapper.
	 * @param streamIn_
	 * @param mapper_
	 */
	public FilterIn_BytePushBack(InputStream streamIn_, IIntFunction mapper_) {
		super(streamIn_, mapper_);
	}
	
	/**
	 * Pushes the given value back so the next {@link #nextInt()} returns it again.
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
	 * Pushes the most recently read value back, per {@link #pushBack(int)}.
	 * @return this Stream if another pushBack() is allowed.
	 */
	public IPushBackAble pushBack() { return pushBack(currItem.Value); }

	/**
	  * Returns the pushed-back value if one is pending, otherwise reads the next value.
	  * @return the next byte of data, or -1 if the end of the stream is reached.
	  */
	public int nextInt() {
		if (filter == currItem) { //indicator that this Stream has been pushed back!
			filter = null; return currItem.Value; }
		return currItem.Value = super.nextInt(); }
	
}
