package streamIO.adapter;

import graphs.IValue;
import streamIO.IIStreamOut;
import streamIO.object.IPipe;
import streamIO.object.enumer.APipe;

/**
  * Title: Value2Pipe<p>
  * Description:
  * Purpose:
  * Adapter from the IValue get/set Interface to the Pipe Interface (bidirectional streamIO)
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
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
	
	/** @return the Number of Items available from the getter */
	public long availAble() { return 1; }

	/** @return the Item returned by the last nextItem() */
	public Object currItem() { return currItem; }

	/** @return the next Item from the getter */
	public Object nextItem() { return currItem = value.getVal(); }

	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOut: Methods
	////////////////////////////////////////////////////////////////////////////

	/** adds this Item to the Store in Place: +=
	 * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public IIStreamOut addItem (Object arg) { value.setVal(arg); return this; }
	
}

