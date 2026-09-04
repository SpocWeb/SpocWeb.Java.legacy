package flow.push;

import tester.ITester;

/**
  * Title: Alternator<p>
  * Description:
  * Purpose:
  * Directs the Objects of the streamIO either to one 
  * or to the other streamIO.
  * depending on the Result of the ITester Routine.
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * @see the Screener uses the same Logic, only employing a Function.
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
  */
public class Alternator
extends DualOutputPushStage
implements IPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** ITester to switch between the Outputs	 */
	protected final ITester predicate;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public Alternator(IPushStage next1_, IPushStage next2_, ITester predicate_) {
		super(next1_, next2_);
		this.predicate = predicate_;}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface DualInputPushStage: Implementation
////////////////////////////////////////////////////////////////////////////////

public IPushStage putA(final Object A) {
	if (predicate.test(A)) {
		next1.putA(A);
	} else {
//		new Thread(new Runnable() {
//			public void run() {
				next2.putA(A); }
//		}).start(); }
	return this; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Alternator.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

