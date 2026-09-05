package asynch;

import function.IProcessor;
import graphs.IValueSetter;

/**
  * Title: ProcessorRef<p>
  * Description:
  * Purpose:
  * Holds a Reference to an IValueSetter and implements the Runnable Interface
  * Thus it acts as a Bridge between those two Interfaces.
  * This THE single universal Bridge Class
  * between the Runnable and the Processor Interface whose Result is stored in the Future.
  * It acts as the Reference for a Callback that is notified asynchronously.
  *
  * Unfortunately the Callback Function has no direct way to determine
  * what the Origin of this Call is when SEVERAL Threads could call it back!
  * The Solution is to hand over this ProcessorRef Object as a Cookie during the Call,
  * which is returned on the Callback!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 10:15 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:46Z
  * digest: ca5a3e84d0b7fa44703999b21435e78e841a59c32e5404edd87cf28198dbf4af
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Task Processor Reference]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class ProcessorRef
extends AReadyToRun {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Object to retrieve the Result from	 */
	protected IProcessor processor;

	/** Reference to the Object to pass to the Processor	 */
	protected Object arg;

	/** Reference to the Object to temporarily store the Result to	 */
	protected IValueSetter valueSetter;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public ProcessorRef(IProcessor processor_, Object arg_, IValueSetter valueSetter_) {
		this.arg = arg_;
		this.processor = processor_;
		this.valueSetter = valueSetter_;}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method to execute by the Thread.
	  * Unfortunately only the Result is returned,
	  * but the Target has no Way to identify the Origin of this Call!
	  */
	public void run() {
		try {
			valueSetter.setVal(processor.MapAt(arg));
		} catch (RuntimeException x) {
			if (valueSetter instanceof IFuture) {
				((IFuture) valueSetter).setException(x); }
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ProcessorRef.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

