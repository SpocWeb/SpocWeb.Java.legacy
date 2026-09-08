/*
 * File Name: AStreamIn_Int.java
 * Created on: 16.02.2004
 *
 */
package streamIO.integer;

import streamIO.real.AAStreamIn_Float;

/**
 * Abstract base class that adapts the real-valued stream hierarchy to integer semantics,
 * adding peek and bulk array-fill operations shared by all {@code int}/{@code long}
 * stream implementations.
 *
 * ## Collaborators
 * | Type | Relationship |
 * |---|---|
 * | {@link streamIO.real.AAStreamIn_Float} | Superclass supplying the generic real-valued
 * stream machinery (positioning, mark/reset) that this class narrows to integer semantics. |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see streamIO.real.AAStreamIn_Float real-valued stream superclass narrowed here to integers
 *
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: 4}
 * -->
 */
public abstract class AAStreamIn_Int 
extends AAStreamIn_Float
implements IStreamIn_Int {
	
	/** Reads and returns the next {@code long} value from this stream.
	 * @see streamIO.integer.IStreamIn_Int#nextLong()	 */
	public abstract long nextLong();

	/** Reads and returns the next {@code int} value from this stream.
	 * @see streamIO.integer.IStreamIn_Int#nextInt()	 */
	public abstract int nextInt();

	/** Returns the byte order this stream reads values in.
	 * @see streamIO.integer.IStreamIn_Int#getOrder()	 */
	public abstract byte getOrder();
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Implementations
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Reads the next {@code long} value without advancing the stream position.
	 * @return the next Value without moving to it.	 */
	public long peekLong() { //throws    NoSuchMethodException {
		//throw new NoSuchMethodException("No generic Implementation!");
		final long ret = nextLong(); 
		pushBack(); 
		return ret; 
	}
	
	/** Reads the next {@code int} value without advancing the stream position.
	 * @return the next Value without moving to it.	 */
	public int peekInt() { //throws    NoSuchMethodException {
		//throw new NoSuchMethodException("No generic Implementation!");
		final int ret = nextInt(); 
		pushBack(); 
		return ret; 
	}
	
	/**Cloning creates only a shallow Copy.  
	 * @see streamIO.integer.IStreamIn_Int#Iterator()	 */
	public IStreamIn_Int IntIterator() {
		try { return (IStreamIn_Int) clone(); 
		} catch (final CloneNotSupportedException x) {
			return null;
		}
	}
	
	/** fills the Array with the Data from this Stream 
	 * @param arr
	 * @return the Number of Items filled. 
	 */ 
	public int fill(final int[] arr) {
		return fill(arr, 0, arr.length); }

	/** fills the Array with the Data from this Stream 
	 * @param arr
	 * @return the Number of Items filled. 
	 */ 
	public int fill(final int[] arr, final int start, final int stop) {
		for (int i = start-1; ++i < stop; ) {
			arr[i] = nextInt(); //TODO: test for NaNs and stop   
		}
		return stop-start; 
	}

	/** fills the Array with the Data from this Stream 
	 * @param arr
	 * @return the Number of Items filled. 
	 */ 
	public int fill(final long[] arr) {
		return fill(arr, 0, arr.length); }

	/** fills the Array with the Data from this Stream 
	 * @param arr
	 * @return the Number of Items filled. 
	 */ 
	public int fill(final long[] arr, final int start, final int stop) {
		for (int i = start-1; ++i < stop; ) {
			arr[i] = nextLong(); //TODO: test for NaNs and stop   
		}
		return stop-start; 
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public void fillArray(final long[] arr, final int start, final int stop) {
		for (int i = stop; --i >= start;) {
			arr[i] = nextLong(); }
	}
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */ 	
	public void fillArray(final long[] arr) { fillArray(arr, 0, arr.length); }
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public void fillArray(final int[] arr, final int start, final int stop) {
		for (int i = stop; --i >= start;) {
			arr[i] = nextInt(); }
	}
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */ 	
	public void fillArray(final int[] arr) { fillArray(arr, 0, arr.length); }
	
}
