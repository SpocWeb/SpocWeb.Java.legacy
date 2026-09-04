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
 * @author heuerm
 * Processes Messages in Sequence and only once.  
 * Optimizes the Operation of it's Parent Class 
 * by cacheing incoming Messages that cannot be processed currently. 
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
	
	/**
	 * @param _currId
	 * @param _processor
	 */
	public MessageOptimized(final IIStreamOut _processor) {
		this(_processor, IMessageReceiver.START_ID);
	}
	
	/**
	 * @param _currId
	 * @param _processor
	 */
	public MessageOptimized(final IIStreamOut _processor, final long _currId) {
		super(_processor, _currId);
	}
	
	/** @see streamIO.asyncMessage.IMessageReceiver#addItem(long, java.lang.Object)
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
	
	
	/** @see streamIO.asyncMessage.IMessageReceiver#flush()	 */
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
	
	public static void main(String[] args) {
	}
}
