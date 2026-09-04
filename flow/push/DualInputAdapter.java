package flow.push;

/**
  * Title: DualPutAdapter<p>
  * Description:
  * Purpose:
  * Adapter to put between any Stage and the second Input of a DualStage
  * Purpose / Responsibilities of this Class
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
  * Created on	09-11-2002, 11:33 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
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
	
	/** adds an Item to this Stage 	 */
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

