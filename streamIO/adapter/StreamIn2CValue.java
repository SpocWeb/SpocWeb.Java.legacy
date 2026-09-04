package streamIO.adapter;

import graphs.ICValue;
import streamIO.IIStreamIn;

/**
  * Title: StreamIn2CValue<p>
  * Description:
  * Purpose:
  * Adapter from the StreamIn Interface to the CValue Interface
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
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-23-2002, 09:32 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StreamIn2CValue
//extends FilterIn
implements ICValue {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Input streamIO:	 */
	protected IIStreamIn streamIn;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface ICValue: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** @return the next Item from the Input streamIO.   */
	public Object getVal() { return streamIn.nextItem(); }

	/** sets the TODO: 	 */
//	void set();

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	protected StreamIn2CValue(IIStreamIn streamIn_) { this.streamIn = streamIn_; }

}

