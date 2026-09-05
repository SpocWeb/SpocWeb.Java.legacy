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
 * <p>An Instance is created by {@link #monitor(Thread, long)} and is live from the moment
 * that Method returns; the Constructor itself starts nothing. That split is deliberate: a
 * Constructor that starts a Thread hands it a half-built Object, and no marking of the
 * Fields can make that safe. The Fields the monitoring Thread reads are final, so the
 * finished Object publishes safely to it.
 *
 * <p>It owns exactly one Thread, interrupts the monitored Thread at most once, and no
 * Instance is reusable after {@link #stop()} or after its single Interruption has fired.
 * {@link #doInterrupt} is read by that Thread without Synchronization, so clearing it
 * cancels the pending Interruption on a best-effort Basis rather than at a guaranteed
 * Point in Time.
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
 * digest: c9ab89ab14f263ad026115f71f670bd8f16294f7e6962e48e6820d132ff1113a
 * stale: false
 * tags: [code/watchdog_thread, code/timeout_handling, code/thread_interruption]
 * concepts: [Concurrency]
 * facets: {layer: infrastructure, status: experimental, complexity: low}
 * -->
 */
public class TimeOuter 
implements Runnable {
	
	/**Time in Milliseconds until the monitored Thread is interrupted. */
	private final long sleepTime;

	/**local Thread that monitors the other one. */
	private final Thread taskThread;

	/**Thread that is to be monitored, interrupted once the Timeout elapses. */
	private final Thread monitoredThread;
	
	/**to cancel the pending Interruption before its Timeout elapses;
	 * to stop the monitoring Thread outright, call the stop() Method
	 */
	public boolean doInterrupt = true;
	
	/**Builds a watchdog over the given Thread without starting it.
	 *
	 * <p>Private because an Instance is of no use until its Thread runs, and
	 * {@link #monitor(Thread, long)} is the only thing that may start it. Starting it here
	 * would hand the new Thread a reference to an Object whose Constructor has not returned.
	 *
	 * @param currentThread the Thread to interrupt once the Timeout elapses
	 * @param TimeOut Milliseconds to wait before the Interruption
	 */
	private TimeOuter(Thread currentThread, long TimeOut) {
		monitoredThread = currentThread;
		sleepTime = TimeOut;
		taskThread = new Thread(this);
	}

	/**Starts monitoring the given Thread, interrupting it once the Timeout has elapsed.
	 *
	 * <p>The monitoring Thread is started only after this Instance is fully constructed,
	 * which is what makes the final Fields it reads safely visible to it. Returns the
	 * watchdog so the caller can {@link #stop()} it, or clear {@link #doInterrupt} before it
	 * fires.
	 *
	 * @param monitored the Thread to interrupt once the Timeout elapses
	 * @param timeOutMillis Milliseconds to wait before interrupting
	 * @return the running watchdog, never null
	 */
	public static TimeOuter monitor(Thread monitored, long timeOutMillis) {
		TimeOuter watchdog = new TimeOuter(monitored, timeOutMillis);
		watchdog.taskThread.start(); //safe: this Instance is fully built by now
		return watchdog;
	}

	/**Waits out the Timeout once and then interrupts the monitored Thread, if it still lives.
	 *
	 * <p>Method that is started to monitor the thread.
	 * Returns after that single Interruption, or as soon as this monitoring Thread is itself
	 * interrupted by {@link #stop()}. It used to loop, re-interrupting every Timeout for as
	 * long as the monitored Thread lived, which contradicted this Class's own contract and
	 * forced its caller to clear {@link #doInterrupt} by hand to obtain a Timer.
	 */
	public void run() {
		try {
			Thread.sleep(sleepTime);
			//You cannot put a diffferent Thread to sleep!!!
			if (doInterrupt && monitoredThread.isAlive()) {
				monitoredThread.interrupt();
			}
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
	 * <p>Keeps the calling Thread busy for about five Seconds while a TimeOuter interrupts
	 * it once, after 1.4 Seconds. No {@link #doInterrupt} juggling is needed for that: the
	 * Watchdog fires once by contract.
	 */
	public static void testIt() {
		long timeOut = 1400;
		long workTime = 5000;
		//work for a long while..., could also be an IO Process

		// Create the monitoring thread. It times out after timeOut
		TimeOuter task = TimeOuter.monitor(Thread.currentThread(), timeOut);
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < workTime) {
			try {
				Thread.sleep(500);
				System.out.print(
					"The working thread has worked for "
						+ (System.currentTimeMillis() - start)
						+ " milliseconds\n");
			} catch (InterruptedException e) {
				System.out.print(
					"The working thread Main has been interrupted after "
						+ (System.currentTimeMillis() - start)
						+ " milliseconds\n");
			}
		}
		task.stop(); //harmless once it has fired; needed if it has not
	}

}
