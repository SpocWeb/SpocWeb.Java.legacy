package streamIO.object.filterOut;

import streamIO.FilterOut;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;

/**
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
  */
public class ThreadOut
	extends FilterOut {

	/** Initializing Constructor */
	public ThreadOut(IStreamOut store) {
		super(store); }

	/** Accepts the Argument, logs it and sends it further down the Chain */
	public synchronized IIStreamOut addItem(final Object arg) {
		//this is an excellent Use for local Classes!
		new Thread(new Runnable(){ //create a new Thread for adding / handling the Item
			public void run() {
				addItem(arg); }
		} ).start(); //starts the new Thread
		return this; }

}
