package streamIO.adapter;

import graphs.IValue;
import streamIO.IIStreamOut;
import streamIO.object.IPipe;
import streamIO.object.enumer.APipe;

/**
  * Adapter from the IValue get/set Interface to the Pipe Interface (bidirectional streamIO).
  *
  * Known SubClasses: <none>
  *	
  *	similar Classes: 
  * @see streamIO.adapter.CValue2StreamIn
  *
  * Known Uses: <none>
  *	
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-23-2002, 05:39 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:37:46Z
  * digest: 0e83d3a82518dd563cd88cec8a733d192f6a0a7cbb555aca091abbe0091093c0
  * stale: false
  * tags: [code/adapter_pattern, code/stream_abstraction]
  * concepts: [Adapter Pattern]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public class Value2Pipe
extends APipe
implements IPipe {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the value being set/read	 */
	protected IValue value;
	
	/** Reference to the Object returned by the last nextItem() */
	protected Object currItem;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	protected Value2Pipe(IValue value_) { this.value = value_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Pipe: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** A getter always has an Item available.
	  * @return the Number of Items available from the getter */
	public long availAble() { return 1; }

	/** Returns the Item returned by the last {@link #nextItem()} call.
	  * @return the Item returned by the last nextItem() */
	public Object currItem() { return currItem; }

	/** Reads the next Item from the wrapped IValue getter.
	  * @return the next Item from the getter */
	public Object nextItem() { return currItem = value.getVal(); }

	/** Returns the largest Mark ever supported: this Adapter has no size limit.
	  * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }

	/** Position is not tracked by this Adapter.
	  * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOut: Methods
	////////////////////////////////////////////////////////////////////////////

	/** adds this Item to the Store in Place: +=
	 * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public IIStreamOut addItem (Object arg) { value.setVal(arg); return this; }
	
}

