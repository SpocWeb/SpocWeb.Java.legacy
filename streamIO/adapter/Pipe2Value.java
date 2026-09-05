package streamIO.adapter;

import graphs.IValue;
import streamIO.object.IPipe;

/**
  * Adapter from the IValue Interface to a bidirectional streamIO Interface.
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:37:06Z
  * digest: 30205c66afabcda37c97046914520afc0f181722e1e10f3fd23f8153164a808b
  * stale: false
  * tags: [code/adapter_pattern, code/stream_abstraction]
  * concepts: [Adapter Pattern]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
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

	/** Reads the next Value from the wrapped Pipe.
	  * @return the Value :   */
	public Object getVal() { return pipe.nextItem(); }

	/** Writes the given Value to the wrapped Pipe. 	 */
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

