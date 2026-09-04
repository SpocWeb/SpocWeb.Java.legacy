package streamIO.adapter;

import graphs.IValue;
import streamIO.object.IPipe;

/**
  * Title: Pipe2Value<p>
  * Description:
  * Purpose:
  * Adapter from the IValue Interface to a bidirectional streamIO Interface.
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
  * Created on	09-23-2002, 05:05 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Pipe2Value
implements IValue {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Pipe being used:	 */
	protected IPipe pipe;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return the Value :   */
	public Object getVal() { return pipe.nextItem(); }

	/** sets the TODO: 	 */
	public void setVal(Object arg) { pipe.addItem(arg); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected Pipe2Value(IPipe pipe_) { this.pipe = pipe_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IValue: Implementation
////////////////////////////////////////////////////////////////////////////////

}

