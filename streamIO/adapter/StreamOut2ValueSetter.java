package streamIO.adapter;

import graphs.IValueSetter;
import streamIO.IIStreamOut;

/**
  * Title: StreamOut2ValueSetter<p>
  * Description:
  * Purpose:
  * Adapter from the StreamOut Interface to the ValueSetter Interface
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
  * Created on	09-23-2002, 09:36 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StreamOut2ValueSetter
//extends FilterOut
implements IValueSetter {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Output streamIO:	 */
	protected IIStreamOut streamOut;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IValueSetter: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** @return TODO:   */
//	get();

	/** adds the Value to the Output streamIO: 	 */
	public void setVal(Object arg) { streamOut.addItem(arg); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public StreamOut2ValueSetter(IIStreamOut streamOut_) { this.streamOut = streamOut_; }

}

