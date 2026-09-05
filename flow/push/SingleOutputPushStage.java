package flow.push;

/**
  * Title: SingleOutputPushStage<p>
  * Description:
  * Purpose:
  * Base class for a pipeline Stage that holds a reference to a single
  * following Stage (next1) and forwards Items to it.
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
  * mtime: 2026-09-05T10:24:25Z
  * digest: d5b76c17bf6cb740bdef7df5557eb622873534cef9d9a4f542486f037c99df6f
  * stale: false
  * tags: [code/producer_consumer]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class SingleOutputPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the second following Stage	 */
	protected IPushStage next1;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the single following Stage.
	  * @return the next Stage:   */
	public IPushStage getNext1() { return next1; }

	/** sets the next Stage: 	 */
	public void setNext1(IPushStage next) { next1 = next; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public SingleOutputPushStage(IPushStage next) { next1 = next; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + SingleOutputPushStage.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

