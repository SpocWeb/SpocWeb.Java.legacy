package streamIO.adapter;

import graphs.ICValue;
import streamIO.object.AStreamIn;

/**
  * Title: CValue2StreamIn<p>
  * Description:
  * Purpose:
  * Adapter from the ICValue Interface to the StreamIn Interface
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
  *	similar Classes: 
  * @see streamIO.adapter.Value2Pipe
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-23-2002, 09:45 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
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
	
	/** Empty Constructor	 */
	protected CValue2StreamIn() { }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the Item returned by the last nextItem() */
	public Object currItem() { return currItem; }
	
	/** @return the next Item from the getter */
	public Object nextItem() { return currItem = cValue.getVal(); }
	
	/** @return the Number of Items available from the getter */
	public long availAble() { return 1; }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
}

