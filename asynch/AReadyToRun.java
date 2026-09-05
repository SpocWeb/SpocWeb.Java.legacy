package asynch;

/**
  * Title: AReadyToRun<p>
  * Description:
  * Abstract base implementation of {@link ReadyToRun} that always reports itself as not dirty,
  * i.e. always ready to run; subclasses that need a real readiness condition override isDirty().
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-14-2002, 10:58 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:20Z
  * digest: b5c17cbbd2460b0c2cf5b59e422c7f0a17b4817a37f8e1639a56ae928876f97d
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Runnable Task Base Class]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public abstract class AReadyToRun
implements ReadyToRun {

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface ReadyToRun: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Always ready: this default implementation never reports itself as dirty. */
	public boolean isDirty() { return false; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + AReadyToRun.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

