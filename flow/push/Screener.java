package flow.push;

import tester.ITester;

/**
  * Title: Screener<p>
  * Description:
  * Purpose:
  * Joins two Streams by waiting for an Input from both Channels. 
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
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

