/*
 * Created on 05.03.2005
 *
 */
package streamIO.asyncMessage;

import streamIO.IIStreamOut;

/**
 * This simple Implementation of a Receiver can support only the 'in Sequence' Model; 
 * It is quite ineffective because it doesn't cache out of Sequence Messages. 
 * 
 * @author heuerm
 */
public class MessageInSequence 
extends MessageReceiver {

	/** contains the ID of the next expected Message. 
	 * All Messages with lower ID were successfully processed.  */
	protected long currId; 
	
	/**
	 * @param _currId the ID of the next expected Message
	 * @param _processor the Processor to handle the Messages. 
	 */
	public MessageInSequence(final IIStreamOut _processor) {
		this(_processor, START_ID); 
	}
	
	/**
	 * @param _currId the ID of the next expected Message
	 * @param _processor the Processor to handle the Messages. 
	 */
	public MessageInSequence(final IIStreamOut _processor, final long _currId) {
		super(_processor);
		this.currId = _currId;
	}
	
	/** @see streamIO.asyncMessage.IMessageReceiver#addItem(long, java.lang.Object)
	 */
	public long addItem(final long id, final Object value) {
		if ((id != currId) && (currId != START_ID)) //accept Messages only in Sequence
			return currId; //except when starting off new...
		final long nextId = currId+1; 
		if (super.addItem(id, value) == nextId) 
			this.currId = nextId; 
		return currId; 
	}

	public static void main(String[] args) {
	}
}
