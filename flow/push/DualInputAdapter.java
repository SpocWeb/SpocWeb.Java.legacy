package flow.push;

/**
  * Title: DualInputAdapter<p>
  * Description:
  * Purpose:
  * Adapts a single-input IPushStage Producer to feed the second (B) Input
  * of a downstream IDualInputPushStage: every Item it receives via putA
  * is forwarded as putB on the wrapped Stage.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 11:33 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:24:05Z
  * digest: e05615e5dfc5124d9b0e68a768620125dd9a5bcd868d7dede69f438b7598967c
  * stale: false
  * tags: [code/adapter_pattern]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public class DualInputAdapter
implements IPushStage {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the next Stage	 */
	protected IDualInputPushStage nextStage;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public DualInputAdapter(IDualInputPushStage nextStage_) {
		this.nextStage = nextStage_; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IPushStage: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Forwards item to the wrapped Stage's putB, adapting it as the B Input.	 */
	public IPushStage putA(Object item) { nextStage.putB(item); return this; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + DualInputAdapter.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

