/*
 * Created on 31.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object;

import streamIO.IIStreamIn;
import streamIO.IIStreamOut;

/**
 * Concrete identity filter that passes every item through unchanged on both the input and
 * output side.
 *
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:40:03Z
 * digest: e60b7c4cd294eb8b2458d7980126e71e44e9f933a24163802153c5c141bd9df8
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Filter
extends AFilter {

	/**
	 * Creates an identity filter reading from the given input.
	 *
	 * @param enum_ the stream to delegate to
	 */
	public Filter(final IIStreamIn enum_) { super(enum_); }

	/**
	 * Creates an identity filter writing to the given output.
	 *
	 * @param out_ the stream to delegate to
	 */
	public Filter(final IIStreamOut out_) { super(out_); }

	/**
	 * Returns the next item from the wrapped input, unchanged.
	 */
	public Object nextItemInternal() { return in.nextItem(); }

	/**
	 * Passes {@code arg} through to the wrapped output, unchanged.
	 *
	 * TODO: use a Mapper to control the Filter just like with float Streams!
	 * @see streamIO.IIStreamOut#addItem(Object)
	 */
	public IIStreamOut addItem(final Object arg) {
		out.addItem(arg); 
		return this; }
	
}
