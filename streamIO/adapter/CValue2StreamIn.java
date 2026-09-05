package streamIO.adapter;

import graphs.ICValue;
import streamIO.object.AStreamIn;

/**
  * Adapter from the ICValue Interface to the StreamIn Interface.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  *	similar Classes: 
  * @see streamIO.adapter.Value2Pipe
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-23-2002, 09:45 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:37:34Z
  * digest: 38875256f51d504aec572627cba2398fd746af67debf140541a6f8cf5f07f287
  * stale: false
  * tags: [code/adapter_pattern, code/stream_abstraction]
  * concepts: [Adapter Pattern]
  * facets: {layer: infrastructure, status: broken, complexity: low}
  * -->
  */
public class CValue2StreamIn
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the getter Class:	 */
	protected ICValue cValue;
	
	/** Reference to the Item last returned by nextItem:	 */
	protected Object currItem;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	// TODO: LOGIC: never assigns the `cValue` field, and there is no other constructor
	// or setter to do so - every sibling adapter in this package (Value2Pipe,
	// ValueSetter2StreamOut, StreamOut2ValueSetter) instead takes and assigns its wrapped
	// dependency in its constructor. As written, `cValue` stays null and nextItem() below
	// throws NullPointerException on first use.
	/** Empty Constructor	 */
	protected CValue2StreamIn() { }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Item returned by the last {@link #nextItem()} call.
	  * @return the Item returned by the last nextItem() */
	public Object currItem() { return currItem; }

	/** Reads the next Item from the wrapped ICValue getter.
	  * @return the next Item from the getter */
	public Object nextItem() { return currItem = cValue.getVal(); }

	/** A getter always has an Item available.
	  * @return the Number of Items available from the getter */
	public long availAble() { return 1; }

	/** Returns the largest Mark ever supported: this Adapter has no size limit.
	  * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }

	/** Position is not tracked by this Adapter.
	  * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
}

