package flow.push;

import graphs.ICopy;

/**
  * Title: Cloner<p>
  * Description:
  * Purpose:
  * Clones / Copies the Message Object and sends it on.
  *
  * Design Decisions / Implementation Details:
  * deprecated
  * Originally created to separate Cloning from the MultiCaster,
  * but to be Thread safe, both Exits of the MultiCaster
  * would have to be decorated with Cloners, which is too much Overhead.
  *
  * A more lightweight Implementation is done in MultiCaster
  * by combining Cloning with the Creation of a new Thread.
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
  * @deprecated
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:23:51Z
  * digest: 5b7bfbf0b6dc3be0da21e2d9d149ef47bb0d0ca4baf297f5ee924fa0b4cd6a6f
  * stale: false
  * tags: [code/adapter_pattern]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public class Cloner
extends SingleOutputPushStage
implements IPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public Cloner(IPushStage next1_) {
		super(next1_); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

/** Copies A via ICopy.Copy() and forwards the copy to next1.
  * @return this
  */
public IPushStage putA(final Object A) {
	next1.putA(((ICopy)A).Copy());
	return this; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface DualInputPushStage: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Cloner.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

