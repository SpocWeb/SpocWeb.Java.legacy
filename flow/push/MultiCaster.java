package flow.push;

import graphs.ICopy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
  * Title: MultiCaster<p>
  * Description:
  * Purpose:
  * Multicasts an Object and its Copy to both Successors concurrently 
  * by creating a new Thread.
  * Multicasting to more than two Channels is possible 
  * by concatenating several MultiCasters.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 11:53 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:24:17Z
  * digest: f9ee69d0abe929b2173cffe4402baf1401768b48ed2bc3d312aed87e3d14d9c1
  * stale: false
  * tags: [code/multicast]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: broken, complexity: medium}
  * -->
  */
public class MultiCaster
extends DualOutputPushStage
implements IPushStage {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Flag to switch cloning on.
	  * Unfortunately cloning cannot be separated from Multicasting
	  * because later Concurrency requires to do the Clone first!
	  * The only Alternative is to put Cloners on both Ends of the MultiCaster,
	  * but that is an unnecessary Overhead!
	  */
	public boolean doClone;

	/** Bounded Pool of daemon Worker Threads shared by all MultiCasters.
	  * A fixed Pool with a bounded Queue and a caller-runs Policy applies Backpressure
	  * instead of creating one unpooled Thread per pushed Item, which used to exhaust
	  * the OS Thread Resources under sustained high-throughput Input.
	  */
	private static final Executor POOL = newPool();

	/** Creates the shared, bounded, daemon-threaded Worker Pool. */
	private static Executor newPool() {
		int size = Math.max(2, Runtime.getRuntime().availableProcessors());
		ThreadPoolExecutor pool = new ThreadPoolExecutor(size, size
			, 60L, TimeUnit.SECONDS
			, new ArrayBlockingQueue<Runnable>(1024)
			, new ThreadFactory() {
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "MultiCaster");
					t.setDaemon(true);
					return t; }
			}
			, new ThreadPoolExecutor.CallerRunsPolicy());
		pool.allowCoreThreadTimeOut(true);
		return pool; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public MultiCaster(IPushStage next1_, IPushStage next2_) {
		super(next1_, next2_); }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** dispatches the Object to both Successors.
	  * It is important that the Element is cloned first
	  * before any of them being dispatched to the Successors,
	  * because these might modify them!
	  * Thus copying can NOT be delegated to a Cloner!
	  */
	public IPushStage putA(final Object A) {
		final Object B = (doClone ? ((ICopy)A).Copy() : A); //clone();
		POOL.execute(new Runnable() {
			public void run() { next2.putA(B); } //if you want to clone, put a Cloner in between!
		});
		next1.putA(A);
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface DualInputPushStage: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + MultiCaster.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

