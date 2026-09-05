package streamIO.object.filterOut;

import streamIO.FilterOut;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;
import streamIO.Log;
/**
  * Filter that forwards each item downstream, catching any exception the downstream chain
  * throws and rerouting the original item to a configured error output instead.
  * <p>
  * Title: TryCatchOut<p>
  * Description:
  * Converts an Exception/Escalation into an Error streamIO/Processing.
  * This is a Simple Filter handing on the Message unchanged
  * but catching any downstream Exception, optionally logs it
  * and passes the Message on to the catch Path.
  * Using TryCatchOut, Context Information acquired on the way to the Error is lost.
  * To avoid this, don't use the ThrowOut, but the Error streamIO of the Node.
  *
  * While this is a trivial Task and could easily be incorporated
  * into any of the other Filters, the Separation of Granularity by defining
  * Configuration vs. Programming allows to work on a higher Level.
  *
  * Working with Exceptions or Error Streams is a Decision
  * that can be made only in concrete Implementations.
  * Exceptions are frequently used in Programming, they correspond to Escalations.
  * Error Streams are used in Business Modelling, but it may be tedious
  * to define Error Streams at any Level of Processing.
  *
  * Using this Node Type, Error Streams can be introduced
  * at any Position in the Chain to prevent Escalation and trigger Error Processing.
  * Adding a TryCatchOut Element before each processing Element
  * saves defining an Error Output streamIO for it and allows to throw Exceptions
  * at the Cost of 'expensive' Exception handling.
  * The alternative is to incorporate the Output streamIO in each processing Element
  * and processing all Exceptions internally.
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
  * mtime: 2026-09-05T20:49:28Z
  * digest: 219324289a8c514306fe97a35586c326c2d3b4bb771c21800b41719333ea3bc8
  * stale: false
  * tags: [code/stream_filter, code/decorator_pattern]
  * concepts: [Stream Filter (Output)]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class TryCatchOut
	extends FilterOut
{
	/** Reference to the Error Output streamIO
	  * Any Exception happening downstream
	  * will be caught and the Original Message will be passed on to this streamIO. */
	IStreamOut Err;

	/** Reference to the Logger */
	Log L;

	/** Initializing Constructor */
	public TryCatchOut(IStreamOut store, IStreamOut Error) {
		this(store, Error, null); }

	/** Initializing Constructor */
	public TryCatchOut(IStreamOut store, IStreamOut Error, Log L_) {
		super(store);
		this.Err = Error;
		this.L = L_; }

	/** Accepts the Argument, logs it and sends it further down the Chain */
	public IIStreamOut addItem(Object arg) {
		try {
			out.addItem(arg);
		} catch(Throwable x) {
			if (L != null) {
				L.n("Exception").l(x).l("with Item").l(arg); }
			Err.addItem(arg);
		}
		return this; }

}
