package asynch;

/**
  * Abstract Sample Client for the Barrier
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
  * Created on	09-15-2002, 09:33 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
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

