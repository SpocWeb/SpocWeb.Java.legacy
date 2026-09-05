package asynch;

/**
  * Synchronization of several Threads by waiting until all arrive at this Barrier.
  * No Thread can leave it until all others have entered.
  * Threads are not identified individually, but only counted
  * Spurious Threads using this Barrier may ruin its Functionality!
  *
  * Since the Barrier is synchronized,
  * it also serves as a Memory Barrier to ensure flushing of concurrent Caches.
  *
  * This is similar to a Transaction, only that no Thread can vote 'no',
  * thus no Rollback is possible.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-15-2002, 01:03 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:22Z
  * digest: e19f4ebd02b8756a64b4c79e332701c6759923795c5f77bebf08605e8715e0f8
  * stale: false
  * tags: [code/concurrency_primitive]
  * concepts: [Thread Barrier]
  * facets: {layer: infrastructure, status: broken, complexity: medium}
  * -->
  */
public class Barrier {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Number of Parties participating in the Barrier	 */
	protected final int numParties;

	/** Counter for the Number of Parties being waited for	 */
	protected int count;

	/** Number of Resets, i.e. times the Barrier has been tripped.	 */
	protected int numResets;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public Barrier(int numParties_) { this.numParties = numParties_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** Enter the Barrier.
	  * No Thread can leave it until all others have entered.
	  * Threads are not identified individually, but only counted
	  * Spurious Threads using this Barrier may ruin its Functionality!
	  *
	  * Since the Barrier is synchronized,
	  * it also serves as a Memory Barrier to ensure flushing of concurrent Caches.
	  *
	  * @return the Number of Threads the Barrier is (was) still waiting for
	  * this can be used to trigger a regular Action on (barrier() == 0)
	  */
	public synchronized int barrier() throws InterruptedException {
		// TODO: LOGIC: 'count' is never initialized to 'numParties' (constructor only sets numParties;
		// count defaults to 0), so the very first call decrements 0 to -1, which is not > 0 and thus
		// takes the "last party" branch immediately instead of waiting for the other parties to arrive.
		int index = --count;
		if (index > 0) { //still have to wait for some...
//			int r = numResets; //wait until the next Reset
//			do { //not necessary to perform a Loop!
				wait();
//			} while(numResets == r); //because this Instance holds the Monitor to notify all Threads
		} else { //Reset...
			count = numParties;
			++numResets; 
			this.notifyAll(); //cause all Parties to resume Processing
		}
		return index; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Barrier.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

