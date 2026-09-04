/*
 * ResidiumDispatcher.java
 *
 * Created on 19. März 2003, 20:54
 */

package asynch;

import java.util.TooManyListenersException;

//import org.apache.log4j.Logger;

/**
 * Dispatches Residuum Values to Clients and sets according Mutexes 
 * also watches Timeouts of these Mutexes. 
 * Contains a static Method to return Residuum Values. 
 * @see de.bahn.zkdb.dblayer.delta.EBDeltaBean uses the static Method
 * @see  
 * @author mheuer
 */
public class ResidiumDispatcher {
	
	/** Holen des Loggers */
	//private static Logger logger = Logger.getLogger(ResidiumDispatcher.class);

    /** Block for at most 10 Minutes */
    final static public int MAX_BLOCK_IN_SECONDS = 600; 
    
    /**
     * increment the given residium value with respect of given modbase
     * uses no static Variables, only Parameters, thus Thread safe. 
     * 
     * @param residium input Value being incremented
     * @param module maximum Value where Rollover happens
     * @return [residium+1] % module
     */
    final static public int INC_RESIDUUM( int residium, int module ) {
        if ((residium >= module) || (residium < 0)) {
            String errorMessage = "Residium Value out of Bounds"; //de.bahn.zkdb.common.error.ErrorDescriptor.retrieveLogMessage(de.bahn.zkdb.common.error.ErrorDescriptor.DB_DELTA_MODRESIDIUM_OUTOFBOUNDS);
            //logger.error("ZKDBError: "+errorMessage);
            throw new ArrayIndexOutOfBoundsException(errorMessage);
        }
        ++residium;
        if (residium == module) {
            residium = 0; }
        return residium;
    }

	/////////////////////////////////////////////////////////////////////////////
	
	/**
	 * holds Locks and TimeStamps from 0..ENV_MODULE
	 * and coordinates different Instances within the same AppServer Instance
	 * contains <=0 for free Instances
	 * >0 for busy Instances (the TimeStamp)
	 */
	private final long[] mutex;

	/** Module */
	private final int module;
	
	/** current Value: between 0 and module-1 */
	private int value; 
	
	/** Creates a new instance of ResidiumDispatcher 
	 * 
	 * @param module_ the Module to use
	 * @param startVal the Starting Value to use
	 */
	public ResidiumDispatcher(int module_, int startVal) {
		this.module = module_;
		this.value = startVal;
		mutex = new long[module]; 
	}
	
	/** Creates a new instance of ResidiumDispatcher 
	 * Start Value is defaulted to 0
	 * @param module the Module to use
	 */
	public ResidiumDispatcher(int module) {	this(module, 0); }
	
	/** Returns the next Lock without waiting
	 * 
	 * @return the ID of the Lock acquired (needed for freeing up the Lock again)
	 * @throws TooManyListenersException if no Lock could be acquired
	 */
	public synchronized int getLock() throws TooManyListenersException {
        return getLock(MAX_BLOCK_IN_SECONDS); }
	
	/** Returns the next Lock that is not blocked or blocked for at least maxBlockSeconds 
	 * @param maxBlockSeconds Number of Seconds to block at maximum
	 * @return the Number of the acquired Lock 
	 * @throws TooManyListenersException if no Lock could be acquired
	 */
	public synchronized int getLock(int maxBlockSeconds) throws TooManyListenersException {
        int start = value;
        int residium = start;
        long now = System.currentTimeMillis(); 
        while (mutex[residium = INC_RESIDUUM(residium, module)] > (now - maxBlockSeconds * 1000) /* is blocked */)  {
            if (residium == start) { //
				String errorMessage = "all Mutexes blocked!"; //de.bahn.zkdb.common.error.ErrorDescriptor.retrieveLogMessage(de.bahn.zkdb.common.error.ErrorDescriptor.DB_DELTA_MODRESIDIUM_BLOCKED);
				//if (logger.isDebugEnabled()) { logger.debug("mutex["+residium+"]="+mutex[residium]+"; now ="+now); }
                //logger.error("ZKDBError: "+errorMessage);
                throw new TooManyListenersException(errorMessage);
            }
        }
        // found an available Mutex
        //if (logger.isDebugEnabled()) { logger.debug("returning "+value+" [module="+module+"]"); }
		value = residium;
		mutex[value] = System.currentTimeMillis(); 
		return value;
	}
	
	/** releasing the Mutex Lock 
	 * 
	 * @param lock ID of the Lock to release
	 */
	public synchronized void releaseLock(int lock) {
		mutex[lock] = 0; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	/// Testing Code
	/////////////////////////////////////////////////////////////////////////////////////////
	
    /**Test Code,  
     * acquires locks and releases them after n Seconds 
     */
	static final void dumpValues(final String thread, final ResidiumDispatcher rd) {
		try {
			for (int i = 100; --i >= 0;) {
				int lock; 
				System.out.println(thread+i+":"+(lock = rd.getLock(1))+"lock="+lock); 
				Thread.sleep(10); 
//				rd.releaseLock(lock); //for testing, don't release the Locks
			}
		} catch(Exception x) {
			throw new RuntimeException(x.toString()); 
		}
	}
	
	final static public void testIt() {
		final ResidiumDispatcher rd = new ResidiumDispatcher(73); 
		Thread thread = new Thread() {
			public void run() { 
				dumpValues("inner", rd); 
			};
		};
		thread.start(); 
		dumpValues("outer", rd); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	final static public void main(String[] args) {
		testIt(); 
	}
	
}
