package tools.threads;

/**Interrupts another, already running Thread once its Timeout has elapsed.
 *
 * <p>Helper Class that watches over an existing Tread handed over to it
 * and interrupts it after the given TimeOut.
 * The InterruptedException has to be caught (see {@link #testIt()} Method).
 * This Instance stays alive until either the Thread to be monitored stops
 * or it is stopped by calling the {@link #stop()} Method because it creates a new Thread.
 *
 * <h2>Invariants</h2>
 *
 * <p>The monitoring Thread is created and started by the Constructor, so an Instance is
 * live from the moment it exists; it owns exactly one Thread and no Instance is reusable
 * after {@link #stop()}. {@link #doInterrupt} is read by that Thread without
 * Synchronization, so clearing it suppresses further Interruptions on a best-effort Basis
 * rather than at a guaranteed Point in Time.
 *
 * <h2>Collaborators</h2>
 *
 * <table>
 * <caption>Types this Class works with</caption>
 * <tr><th>Type</th><th>Relationship</th></tr>
 * <tr><td>{@link Runnable}</td>
 *     <td>Implemented so the Instance itself is the Body of its own monitoring Thread.</td></tr>
 * <tr><td>{@link Thread}</td>
 *     <td>Both the monitored Thread passed in, and the monitoring Thread this Class starts.</td></tr>
 * </table>
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: 662207cbd8bb355598443b3878c17635b3ad73e6a6cafc68254d6def88b4d01e
 * stale: false
 * tags: [code/watchdog_thread, code/timeout_handling, code/thread_interruption]
 * concepts: [Concurrency]
 * facets: {layer: infrastructure, status: experimental, complexity: low}
 * -->
 */
public class TimeOuter 
implements Runnable {
	
	/**Time in Milliseconds until the monitored Thread is interrupted. */
	private long sleepTime;

	/**local Thread that monitors the other one. */
	private Thread taskThread;

	/**Thread that is to be monitored, interrupted once the Timeout elapses. */
	private Thread monitoredThread;
	
	/**to halt Interruptions temporarily
	 * to stop them generally, call the stop() Method
	 */
	public boolean doInterrupt = true;
	
	/**Starts monitoring the given Thread immediately, interrupting it after the Timeout.
	 *
	 * <p>Initializing Constructor,
	 * takes the Thread to be monitored and the Timeout before Interruption.
	 *
	 * @param currentThread the Thread to interrupt once the Timeout elapses
	 * @param TimeOut Milliseconds to wait before each Interruption Attempt
	 */
	// TODO: LOGIC: `this` is published to a new Thread from inside the Constructor, so the
	// monitoring Thread can start before construction completes; the fields it reads are
	// non-final and unsynchronized, so a subclass Constructor or a reordered write could
	// let it observe sleepTime == 0 or monitoredThread == null.
	TimeOuter(Thread currentThread, long TimeOut) {
		monitoredThread = currentThread;
		sleepTime = TimeOut;
		taskThread = new Thread(this);
		taskThread.start(); //this keeps the TimeOuter running until the
	}

	/**Repeatedly waits out the Timeout and interrupts the monitored Thread while it lives.
	 *
	 * <p>Method that is started to monitor the thread.
	 * Returns as soon as the monitored Thread has died, or as soon as this monitoring
	 * Thread is itself interrupted by {@link #stop()}.
	 */
	public void run() {
		try {
			// TODO: LOGIC: the class contract says the monitored Thread is interrupted "after
			// the given TimeOut" (one shot), but this loop re-interrupts it every sleepTime
			// for as long as it stays alive; testIt() has to clear doInterrupt by hand to get
			// the documented one-shot behaviour.
			do {
				Thread.sleep(sleepTime);
				//You cannot put a diffferent Thread to sleep!!!
				if (doInterrupt) {
					monitoredThread.interrupt();
				}
			} while (monitoredThread.isAlive()); // true); //doInterrupt);
		} catch (InterruptedException e) {
		}
	}

	/** Stops the Task immediately and thus also kills this Instance.  */
	public void stop() {
		taskThread.interrupt();
	}

	/**Demonstrates the Class by timing out the calling Thread, and shows how to catch the
	 * resulting InterruptedException.
	 *
	 * <p>Blocks the calling Thread for about ten Seconds, letting a TimeOuter interrupt it
	 * repeatedly until roughly five Seconds have passed, then clears
	 * {@link #doInterrupt} to reduce the Watchdog to a single-shot Timer.
	 */
	public static void testIt() {
		long sleepTime1 = 1400;
		long sleepTime2 = 10000;
		//sleep for very long..., could also be IO Process

		// Create the monitoring thread. It times out after sleepTime1
		TimeOuter task = new TimeOuter(Thread.currentThread(), sleepTime1);
		try {
			Thread.sleep(sleepTime2);
		} catch (InterruptedException e) {
			System.out.print("The working thread Main has been interrupted\n");
		}
		long sleptTime = 0;
		while ((sleptTime += 500) < sleepTime2) {
			try {
				Thread.sleep(500);
				System.out.print(
					"The working thread has slept for "
						+ sleptTime
						+ " milliseconds\n");
			} catch (InterruptedException e) {
				System.out.print(
					"The working thread Main has been interrupted\n");
			}
			if (sleptTime > 5000)
				task.doInterrupt = false;
			//stop interrupting, one Shot Timer...
		}
		//	task.stop(); //If you forget to stop it, it will live on to the End of this Thread (but not longer)
	}

}
