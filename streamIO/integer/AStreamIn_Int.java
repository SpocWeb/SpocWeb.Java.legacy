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
 */
public abstract class AStreamIn_Int 
extends AAStreamIn_Int {
	
	/** Long has enough Capacity to hold any integer Item 	 */
	final public ByRefLong	currItem	= new ByRefLong();
	
	/** @see streamIO.real.AAStreamIn_Float#currItem() */
	final public Object currItem() { return currItem; }
	
	/** @see streamIO.real.AAStreamIn_Float#nextItem() */
	final public Object nextItem() { nextLong(); return currItem; }
	
	/** @see Stream.Float.IStreamIn_Int#nextInt() */
	final public long nextLong() { return currItem.Value = nextLongInternal(); }
	
	/** @see Stream.Float.IStreamIn_Float#nextDouble() */
	public double nextDouble() { return nextLong(); }
	
	/** @see streamIO.integer.AAStreamIn_Int#nextInt() */
	final public int nextInt() { return (int) nextLong(); }
	
	/** @see streamIO.real.IStreamIn_Float#currDouble()	 */
	public double currDouble() { return currItem.Value; }
	
	/** @see streamIO.real.IStreamIn_Float#currFloat()	 */
	public float currFloat() { return currItem.Value; }
	
	/** @see streamIO.real.IStreamIn_Float#currDouble()	 */
	public long currLong() { return currItem.Value; }
	
	/** @see streamIO.real.IStreamIn_Float#currFloat()	 */
	public int currInt() { return (int) currItem.Value; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// abstract Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** @see Stream.Float.IStreamIn_Int#nextInt() */
	abstract protected long nextLongInternal();

	/** @see streamIO.real.AAStreamIn_Float#getMinDouble() */
	abstract public double getMinDouble();

	/** @see streamIO.integer.AAStreamIn_Int#getOrder() */
	abstract public byte getOrder();

	/** @see streamIO.real.AAStreamIn_Float#availAble() */
	abstract public long availAble();

	/** @see streamIO.real.AAStreamIn_Float#getMaxMarkSize() */
	abstract public long getMaxMarkSize();

	/** @see streamIO.real.AAStreamIn_Float#getPosition() */
	abstract public long getPosition();
	
}