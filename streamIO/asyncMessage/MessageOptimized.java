/*
 * Created on 05.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.asyncMessage;

import java.util.HashMap;

import streamIO.IIStreamOut;
import streamIO.Log;

/**
 * Processes Messages in Sequence and only once, optimizing on {@link MessageInSequence}
 * by caching out-of-sequence incoming Messages instead of rejecting them, and replaying
 * the Cache forward as soon as the gap is filled.
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:49:30Z
 * digest: ec34bd3baa44b79eda0bddc9da34f90e49b61b1366933e3c9fd1690e6fdebd51
 * stale: false
 * tags: [code/message_queue, code/sequence_processor]
 * concepts: [Asynchronous Messaging]
 * facets: {layer: infrastructure, status: stable, complexity: medium}
 * -->
 */
public class MessageOptimized
extends MessageInSequence {

	/** initial Capacity for the not yet processible Messages	 */
	final static public int INITIAL_CAPACITY = 100; 
	
	/** Member Variable to construct the Key for the HashMap, 
	 * cannot be reused, since used in the HashMap!  */
	//private int[] key; 
	
	/** Cache for the not yet processed Messages */
	private HashMap messages = new HashMap(INITIAL_CAPACITY);
	
	/** Starts expecting Messages from {@link IMessageReceiver#START_ID}.
	 * @param _processor the Processor to handle the Messages.
	 */
	public MessageOptimized(final IIStreamOut _processor) {
		this(_processor, IMessageReceiver.START_ID);
	}

	/** Starts expecting Messages from the given ID.
	 * @param _processor the Processor to handle the Messages.
	 * @param _currId the ID of the next expected Message
	 */
	public MessageOptimized(final IIStreamOut _processor, final long _currId) {
		super(_processor, _currId);
	}

	/** Caches the given Message if it arrives out of Sequence or its immediate Processing fails,
	 * then replays as many contiguous cached Messages as now possible.
	 * @see streamIO.asyncMessage.IMessageReceiver#addItem(long, java.lang.Object)
	 */
	public long addItem(final long id, final Object value) {
		final Long key = new Long(id); 
		if (messages.containsKey(key)) 
			return Math.max(currId, id+1); 
		//process this Message 
		final long nextId = currId+1; 
		if ((id  != currId) || super.addItem(id, value) != nextId) { //
			messages.put(key, value); //cache this Message
		}
		return processSubSequent(true); 
	}
	
	
	/** Replays as many contiguous cached Messages as possible, but never blocks or retries -
	 * unlike {@link #processSubSequent(boolean)} called from {@link #addItem(long, Object)}.
	 * @see streamIO.asyncMessage.IMessageReceiver#flush()	 */
	public void flush() {
		processSubSequent(false); //
	}
	
	/** 
	 * processes as many Objects in the Cache as possible 
	 * @param breakOnAddFail Optimization: stop when any Problems occur until the next Messages come in. 
	 * @return currId, the last 
	 */
	private long processSubSequent(boolean breakOnAddFail) {
		Object value; 
		//process all subsequent cached Messages.
		for (long nextId; null != (value = messages.get(new Long(currId))); ) {
			if((nextId=currId+1) == super.addItem(currId, value))
				currId=nextId; //already in super.addItem()
			else
				if (breakOnAddFail)
					break; 
				else
					try {
						Thread.sleep(MessageStreamOut.DEFAULT_MIN_SLEEP_TIME); //force writing, despite further Exceptions
					} catch (InterruptedException x) {
						Log.N(x); 
					}
		}
		return currId;
	}
	
	/** unused entry point; kept for the ad-hoc Test Convention used across this Package. */
	public static void main(String[] args) {
	}
}
