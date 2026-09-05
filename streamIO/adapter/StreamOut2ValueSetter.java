package streamIO.adapter;

import graphs.IValueSetter;
import streamIO.IIStreamOut;

/**
  * Adapter from the StreamOut Interface to the ValueSetter Interface.
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:38:01Z
  * digest: 1662926b308f25f6cc96e14093c9162487703fb0b98c594d3dcbf02d53a5afcd
  * stale: false
  * tags: [code/adapter_pattern, code/stream_abstraction]
  * concepts: [Adapter Pattern]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
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

