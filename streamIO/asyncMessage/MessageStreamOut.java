/*
 * Created on 05.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.asyncMessage;

import streamIO.AStreamOut;
import streamIO.IIStreamOut;
import streamIO.Log;

/**
 *
 * This is the Drain for Messages, here Messages can be deposited. 
 * These are stored permanently and transmitted in a transactional Manner.  
 * On the Receiver Side different Service Levels can be requested: 
 * -Delivery guaranteed 
 * -Delivery exactly once 
 * -Delivery in Sequence 
 * 
 * To undo Transactions you have to send a compensating Transaction, a Storno.
 * TODO: a fail-safe Message Queue directly writes into a persistent Store 
 * and tries to send it in parallel. 
 * @author heuerm
 */
public class MessageStreamOut 
extends AStreamOut {

	/** Default for the Minimum Sleep Time in ms between Retries 	*/
	final static public long DEFAULT_MIN_SLEEP_TIME = 10; 
	
	/** Default for the Maximum Sleep Time in ms between Retries 	*/
	final static public long DEFAULT_MAX_SLEEP_TIME = 30*60*1000; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** Minimum Sleep Time in ms between Retries 	*/
	final long minSleepTime; 
	
	/** Maximum Sleep Time in ms between Retries 	*/
	final long maxSleepTime; 
	
	/** Counter for a unique and strictly monotonous ID. 
	 * When several Senders are connected to a Receiver, 
	 * a Multiplexer has to be used, that tracks the IDs of each Sender individually. 
	 */
	private long currId = Long.MIN_VALUE; 
	
	/** the Message Receiver 	*/
	private final IMessageReceiver receiver; 
	
	/**
	 * @param receiver the Message Receiver 
	 */
	public MessageStreamOut(final IMessageReceiver receiver) {
		this(receiver, IMessageReceiver.START_ID); 
	}
	
	/**
	 * @param receiver the Message Receiver 
	 * @param currId the Start ID of this Connection 
	 */
	public MessageStreamOut(final IMessageReceiver receiver, final long currId) {
		this(receiver, currId, DEFAULT_MAX_SLEEP_TIME); 
	}
	
	/**
	 * @param receiver the Message Receiver 
	 * @param currId the Start ID of this Connection 
	 * @param maxSleepTime maximum Sleep Time between Retries 
	 */
	public MessageStreamOut(final IMessageReceiver receiver, final long currId
			, final long maxSleepTime) {
		this(receiver, currId, maxSleepTime, DEFAULT_MIN_SLEEP_TIME); 
	}
	
	/**
	 * @param receiver the Message Receiver 
	 * @param currId the Start ID of this Connection 
	 * @param maxSleepTime maximum Sleep Time between Retries 
	 * @param minSleepTime minimum Sleep Time between Retries (initial) 
	 */
	public MessageStreamOut(final IMessageReceiver receiver, final long currId
			, final long maxSleepTime, final long minSleepTime) {
		if (receiver == null) 
			throw new NullPointerException("Early Warning! The Receiver must not be null!"); 
		this.receiver = receiver;
		this.currId = currId;
		this.maxSleepTime = maxSleepTime;
		this.minSleepTime = minSleepTime;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Interface IStreamOut
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.IStreamOut#flush()	 */
	public void flush() {
		this.receiver.flush();
	}
	
	/** All Exceptions are caught
	 * Blocks until the Message could be delivered. 
	 * @return null when a remote Exception or a Sleep Interruption happened.  
	 * @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final Object arg) {
		final long nextId = currId+1; 
		try {
			long sleepTime = minSleepTime; 
			while (receiver.addItem(currId, arg) != nextId) { //try again!
				Thread.sleep(sleepTime);
				if((sleepTime+=sleepTime) > maxSleepTime) 
					sleepTime = maxSleepTime;
			}
			currId = nextId; 
			return this;
		} catch (final Throwable x) {
			Log.N(x); 
			return null; //signal the Failure
		}
	}

	public static void main(String[] args) {
	}
	
}
