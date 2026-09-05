package flow.push;

/**
  * Title: DualOutputPushStage<p>
  * Description:
  * Purpose:
  * Base class for a Stage that fans out to two following Stages (next1, next2).
  * Extends SingleOutputPushStage with a second output reference; subclasses
  * (Alternator, Screener, MultiCaster) decide how Items are distributed
  * between the two.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-12-2002, 12:06 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:24:08Z
  * digest: aeb828c3fa325ffad855910125a0e64f737ff759a06359b14338bc3de42c71db
  * stale: false
  * tags: [code/producer_consumer]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class DualOutputPushStage
extends SingleOutputPushStage {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the second following Stage	 */
	protected IPushStage next2;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the second following Stage.
	  * @return the next Stage:   */
	public IPushStage getNext2() { return next2; }

	/** sets the next Stage: 	 */
	public void setNext2(IPushStage next) { next2 = next; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public DualOutputPushStage(IPushStage next1_, IPushStage next2_) {
		super(next1_);
		next2 = next2_; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + DualOutputPushStage.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

