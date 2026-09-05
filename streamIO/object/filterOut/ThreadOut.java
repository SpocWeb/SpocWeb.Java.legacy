package streamIO.object.filterOut;

import streamIO.FilterOut;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;

/**
  * Filter that offloads each incoming item's forwarding onto a new thread for concurrent
  * downstream processing.
  * <p>
  * Title: ThreadOut<p>
  * Description:
  * Creates a new thread for processing each incoming Message.
  * Errors are not propagated back, since addItem returns immediately.
  * Instead they terminate the Thread.
  * To avoid this, a TryCatchOut Element can be appended to ThreadOut
  * which allows to define an Error Output streamIO.
  *
  * This allows to add concurrency to Message Processing.
  * Another Object can be used to synchronize parallel Processing
  * back into one streamIO.
  *
  * Known SubClasses: <none>
  *
  * @see ThrowOut for a Node that converts the Error streamIO into an Exception (reThrow)
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:49:21Z
  * digest: 008cac9523c09257208f7375a2725ccb572913b46da8478094d67a4815878bdb
  * stale: false
  * tags: [code/stream_filter, code/decorator_pattern]
  * concepts: [Stream Filter (Output)]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class ThreadOut
	extends FilterOut {

	/** Initializing Constructor */
	public ThreadOut(IStreamOut store) {
		super(store); }

	/** Hands the Argument to a new Thread for asynchronous forwarding down the Chain. */
	// TODO: LOGIC: the Runnable's run() calls `addItem(arg)`, which resolves to this same
	// ThreadOut.addItem() rather than the wrapped output's `out.addItem(arg)` (or `super.addItem`).
	// Every invocation therefore spawns a new thread that immediately spawns another, forever,
	// exhausting threads/memory instead of ever forwarding the item to `out`. Should be
	// `out.addItem(arg)`.
	public synchronized IIStreamOut addItem(final Object arg) {
		//this is an excellent Use for local Classes!
		new Thread(new Runnable(){ //create a new Thread for adding / handling the Item
			public void run() {
				addItem(arg); }
		} ).start(); //starts the new Thread
		return this; }

}
