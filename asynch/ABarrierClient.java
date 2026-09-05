package asynch;

/**
  * Abstract sample client for {@link Barrier}: drives a loop of update() / barrier() /
  * isConverged() so several threads can iterate a shared computation in lockstep and let a
  * single elected thread (the one for which barrier() returns 0) decide when it has converged.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-15-2002, 09:33 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:17Z
  * digest: ec46a371abea5e0d2b9980e1067d67c02d435c3b12a97391bc50789f7243ba82
  * stale: false
  * tags: [code/concurrency_primitive]
  * concepts: [Barrier Client Callback]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public abstract class ABarrierClient
implements Runnable {

	/** holds the Barrier this Client is supposed to wait for */
	protected Barrier barrier;

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor */
	protected ABarrierClient(Barrier barrier_) { this.barrier = barrier_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** update and process the internal Data */
	protected abstract void update();

	/** check the internal Data for Convergence
	  * This Method is called after all Barriers have left it.
	  */
	protected abstract boolean isConverged();

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method called by its Thread */
	public void run() {
		boolean converged = false;
		try {
			while (! converged) {
				update();
				if (barrier.barrier() == 0) {
					converged = isConverged(); }
				barrier.barrier(); //let ALL Threads wait for the Convergence Check to finish!
			} //this is slightly less effective, because Threads have to wait a second Time!
		} catch (RuntimeException x) {
		} catch (InterruptedException x) {
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ABarrierClient.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

