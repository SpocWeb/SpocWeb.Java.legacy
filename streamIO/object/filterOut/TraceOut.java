package streamIO.object.filterOut;

import streamIO.FilterOut;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;
import streamIO.Log;
/**
  * Title: TraceOut<p>
  * Description:
  * Simple Filter handing on the Message unchanged.
  * Writes out the Message to a Logger.
  *
  * While this is a trivial Task and could easily be incorporated
  * into any of the other Filters, the Separation of Granularity by defining
  * Configuration vs. Programming allows to work on a higher Level.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class TraceOut
extends FilterOut
{
	/** Reference to the Logger */
	Log L;

	/** (Unique) Name of this Logging Node to identify it
	  * if the Log is used for other sources too. */
	String NodeID;

	/** Initializing Constructor */
	public TraceOut(IStreamOut store, Log L_, String NodeID_) {
		super(store);
		this.NodeID = NodeID_;
		this.L = L_; }

	/** Initializing Constructor */
	public TraceOut(IStreamOut store, Log L_) {
		this(store, L_, ""); }

	/** Accepts the Argument, logs it and sends it further down the Chain */
	public IIStreamOut addItem(Object arg) {
		if (L != null) {
			L.n("Passed").l(NodeID).l("Item").l(arg); }
		out.addItem(arg);
		return this; }

}
