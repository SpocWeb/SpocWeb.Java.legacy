package flow.push;

/**
  * Title: Joiner<p>
  * Description:
  * Purpose:
  * Joins two Streams by waiting for an Input from both Channels.
  * A streamIO is blocked until the current Object of the other streamIO
  * matches the one from the first streamIO.
  * null Objects in the streamIO are ignored, because they are used as Flags.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 11:53 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:24:12Z
  * digest: 98bb6c7baedcfa0ef16f1c514b5dd8cb85bd0c7a73a175f7b6b85c5b4f80648f
  * stale: false
  * tags: [code/producer_consumer]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: medium}
  * -->
  */
public abstract class Joiner
extends SingleOutputPushStage
implements IDualInputPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Objects put into this Joiner	 */
	protected Object a;

	/** Reference to the Objects put into this Joiner	 */
	protected Object b;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public Joiner(IPushStage next) { super(next); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface DualInputPushStage: Implementation
////////////////////////////////////////////////////////////////////////////////

/** Joins this Object and propagates the Result, when successful
  * @return this
  */
public IPushStage putA(Object A) {
	Object j = joinFromA(A);
	if (j != null) {
		next1.putA(j); }
	return this; }

/** Wait for Consumption of the old Value in A, then store this new Value.
  * Notified by tryJoin().
  * @return the joint Object, null on InterruptException
  */
protected synchronized Object joinFromA(Object A) {
	while(a != null) {
		try { wait();
		} catch (InterruptedException x) {
			return null; }
	} a = A;
	return tryJoin(); }

/** Joins this Object and propagates the Result, when successful
  * @return this
  */
public IDualInputPushStage putB(Object B) {
	Object j = joinFromB(B);
	if (j != null) {
		next1.putA(j); }
	return this; }

/** Wait for Consumption of the old Value in B, then store this new Value
  * Notified by tryJoin().
  * @return the joint Object, null on InterruptException
  */
protected synchronized Object joinFromB(Object B) {
	while(b != null) {
		try { wait();
		} catch (InterruptedException x) {
			return null; }
	} b = B;
	return tryJoin(); }

/** tries to join both Channels.
  * Wakes up the Threads waiting for Consumption.
  * @return the joint Object or null, when one has not arrived yet.
  */
protected synchronized Object tryJoin() {
	if  ((a == null) ||
		 (b == null)) {
		return null;  }
	Object joined = join(a, b); //create the combined Object
	a = b = null;
	notify(); //notifyAll();
	return joined; }

////////////////////////////////////////////////////////////////////////////
/// #region : abstract Methods
////////////////////////////////////////////////////////////////////////////

/** Creation of the Joint Result Object
  * Use an abstract Class to keep this virtual Method protected!
  */
protected abstract Object join(Object A, Object B);

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Joiner.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

