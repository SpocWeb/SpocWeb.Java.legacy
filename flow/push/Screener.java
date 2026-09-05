package flow.push;

import tester.ITester;

/**
  * Title: Screener<p>
  * Description:
  * Purpose:
  * Routes each Object to one of two following Stages, depending on the
  * Result of an ITester Predicate. Functionally identical to Alternator,
  * which the same ITester-based routing Logic duplicates.
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
  * mtime: 2026-09-05T10:24:21Z
  * digest: a34f1f6078e4da1c357850394a54c27fb64312d3cb34640646fc0f2190d66470
  * stale: false
  * tags: [code/adapter_pattern]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class Screener
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
	public Screener(IPushStage next1_, IPushStage next2_, ITester predicate_) {
		super(next1_, next2_);
		this.predicate = predicate_;}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface DualInputPushStage: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Routes A to next1 if the predicate accepts it, otherwise to next2.
	  * @return this
	  */
	public IPushStage putA(final Object A) {
		if (predicate.test(A)) {
			next1.putA(A);
		} else {
	//		new Thread(new Runnable() {
	//			public void run()  {
					next2.putA(A); }
	//		}).start(); }
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Screener.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

