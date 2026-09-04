package tools.threads;

/**Helper Class that watches over an existing Tread handed over to it
 * and interrupts it after the given TimeOut.
 * The InterruptedException has to be caught (see testIt() Method).
 * This Instance stays alive until either the Thread to be monitored stops
 * or it is stopped by calling the stop() Method because it creates a new Thread.
 * <!-- docstate
 * pass: 2
 * mtime: 2005-07-25T11:45:12Z
 * digest: b99f8625892167499680b0a8fb181e450a0b044c39708dd8d4abd783f8ad21e4
 * stale: false
 * -->
 */
public class TimeOuter 
implements Runnable {
	
	//Time until this Thread is interrupted.
	private long sleepTime;
	
	//local Thread that monitors the other one.
	private Thread taskThread;
	
	//Thread that is to be monitored...
	private Thread monitoredThread;
	
	/**to halt Interruptions temporarily
	 * to stop them generally, call the stop() Method
	 */
	public boolean doInterrupt = true;
	
	/**Initializing Constructor,
	 * takes the Thread to be monitored and the Timeout before Interruption
	 */
	TimeOuter(Thread currentThread, long TimeOut) {
		monitoredThread = currentThread;
		sleepTime = TimeOut;
		taskThread = new Thread(this);
		taskThread.start(); //this keeps the TimeOuter running until the
	}

	/**Method that is started to monitor the thread. */
	public void run() {
		try {
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
