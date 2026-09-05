package streamIO.adapter;

import graphs.ICValue;
import streamIO.IIStreamIn;

/**
  * Adapter from the StreamIn Interface to the read-only CValue Interface.
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:37:15Z
  * digest: 53818c34bd531717398b336791fee75693cb95cda32cc2f233f927f3b21aa3ce
  * stale: false
  * tags: [code/adapter_pattern, code/stream_abstraction]
  * concepts: [Adapter Pattern]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
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

	/** Reads the next Item from the wrapped Input streamIO.
	  * @return the next Item from the Input streamIO.   */
	public Object getVal() { return streamIn.nextItem(); }

	/** sets the TODO: 	 */
//	void set();

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	protected StreamIn2CValue(IIStreamIn streamIn_) { this.streamIn = streamIn_; }

}

