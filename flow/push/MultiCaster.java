package flow.push;

import graphs.ICopy;

/**
  * Title: Multicaster<p>
  * Description:
  * Purpose:
  * Multicasts an Object and its Copy to both Successors concurrently 
  * by creating a new Thread.
  * Multicasting to more than two Channels is possible 
  * by concatenating several MultiCasters.
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
public class MultiCaster
extends DualOutputPushStage
implements IPushStage {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Flag to switch cloning on.
	  * Unfortunately cloning cannot be separated from Multicasting
	  * because later Concurrency requires to do the Clone first!
	  * The only Alternative is to put Cloners on both Ends of the MultiCaster,
	  * but that is an unnecessary Overhead!
	  */
	public boolean doClone;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public MultiCaster(IPushStage next1_, IPushStage next2_) {
		super(next1_, next2_); }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** dispatches the Object to both Successors.
	  * It is important that the Element is cloned first
	  * before any of them being dispatched to the Successors,
	  * because these might modify them!
	  * Thus copying can NOT be delegated to a Cloner!
	  */
	public IPushStage putA(final Object A) {
		final Object B = (doClone ? ((ICopy)A).Copy() : A); //clone();
		new Thread(new Runnable() {
			public void run() { next2.putA(B); } //if you want to clone, put a Cloner in between!
		}).start();
		next1.putA(A);
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface DualInputPushStage: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + MultiCaster.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

