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
 * Filter supporting a single-slot push-back: the last item read can be pushed back once and
 * will be replayed by the next {@code nextItem()} call before the wrapped stream is advanced.
 *
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:47:17Z
 * digest: e841a0db365e857fe1dbc90608a07af77bc56ac3572b84539f56eb65925b1b4a
 * stale: false
 * tags: [code/stream_filter, code/decorator_pattern]
 * concepts: [Stream Filter (Input)]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class FilterIn_PushBack
extends AFilterIn {

	/**
	 * Creates a push-back filter over the given input.
	 *
	 * @param enum_ the stream to delegate to
	 */
	public FilterIn_PushBack(final IIStreamIn enum_) {
		super(enum_);
	}

	/** Replays the pushed-back item if one is pending, otherwise advances the wrapped stream.
	 * @see streamIO.object.AFilterIn#nextItemInternal()	 */
	protected Object nextItemInternal() {
		if (filter == currItem) { //indicator that this Stream has been pushed back!
			filter = null; return currItem; }
		return currItem = in.nextItem(); }
	
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
