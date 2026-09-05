/*
 * Created on 21.09.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import function.byref.ByRefLong;

/**
 * Title:
 * <p>
 * Description: Purpose: Abstract Base Class for Streams of Integers or Longs
 * (not Bytes; use AAStreamIn__Int for this)
 * 
 * Design Decisions / Implementation Details: If similar Classes exist (e.g.
 * Polymorphism), characterize the specific Differences to compare these.
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * Copyright: Copyright (c) Matthias Heuer
 * <p>
 * Company: personal
 * <p>
 * Created on 10-26-2002, 12:47 PM
 * <p>
 * 
 * @author heuerm
 * @version 1.0
 * <!-- docstate
 * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
 * concepts: [Primitive and Structured Stream I/O Core Abstractions]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public abstract class AStreamIn_Int 
extends AAStreamIn_Int {
	
	/** Long has enough Capacity to hold any integer Item 	 */
	final public ByRefLong	currItem	= new ByRefLong();
	
	/** Returns the current Item, boxed as a {@link ByRefLong}.
	 * @see streamIO.real.AAStreamIn_Float#currItem() */
	final public Object currItem() { return currItem; }

	/** Advances to and returns the next Item, boxed as a {@link ByRefLong}.
	 * @see streamIO.real.AAStreamIn_Float#nextItem() */
	final public Object nextItem() { nextLong(); return currItem; }

	/** Reads and returns the next {@code long} value, updating {@link #currItem}.
	 * @see Stream.Float.IStreamIn_Int#nextInt() */
	final public long nextLong() { return currItem.Value = nextLongInternal(); }

	/** Returns the next value widened to a {@code double}.
	 * @see Stream.Float.IStreamIn_Float#nextDouble() */
	public double nextDouble() { return nextLong(); }

	/** Returns the next value narrowed to an {@code int}.
	 * @see streamIO.integer.AAStreamIn_Int#nextInt() */
	final public int nextInt() { return (int) nextLong(); }

	/** Returns the current Item widened to a {@code double}, without advancing.
	 * @see streamIO.real.IStreamIn_Float#currDouble()	 */
	public double currDouble() { return currItem.Value; }

	/** Returns the current Item narrowed to a {@code float}, without advancing.
	 * @see streamIO.real.IStreamIn_Float#currFloat()	 */
	public float currFloat() { return currItem.Value; }

	/** Returns the current Item as a {@code long}, without advancing.
	 * @see streamIO.real.IStreamIn_Float#currDouble()	 */
	public long currLong() { return currItem.Value; }

	/** Returns the current Item narrowed to an {@code int}, without advancing.
	 * @see streamIO.real.IStreamIn_Float#currFloat()	 */
	public int currInt() { return (int) currItem.Value; }

	/////////////////////////////////////////////////////////////////////////////////////
	/// abstract Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** Reads and returns the next raw {@code long} value from the underlying source.
	 * @see Stream.Float.IStreamIn_Int#nextInt() */
	abstract protected long nextLongInternal();

	/** Returns the smallest value this stream can produce, as a double.
	 * @see streamIO.real.AAStreamIn_Float#getMinDouble() */
	abstract public double getMinDouble();

	/** Returns the byte order this stream reads values in.
	 * @see streamIO.integer.AAStreamIn_Int#getOrder() */
	abstract public byte getOrder();

	/** Returns the number of values still available from this stream.
	 * @see streamIO.real.AAStreamIn_Float#availAble() */
	abstract public long availAble();

	/** Returns the maximum number of Values that can be marked and reset.
	 * @see streamIO.real.AAStreamIn_Float#getMaxMarkSize() */
	abstract public long getMaxMarkSize();

	/** Returns the current read position within this stream.
	 * @see streamIO.real.AAStreamIn_Float#getPosition() */
	abstract public long getPosition();
	
}