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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:49:08Z
 * digest: 73c21cd37d4d8fe65d4f7e2e9999b42d8132b91fce4f23566eaaecd76c2183ac
 * stale: false
 * tags: [code/message_queue, code/sequence_processor]
 * concepts: [Asynchronous Messaging]
 * facets: {layer: infrastructure, status: stable, complexity: low}
 * -->
 */
public class MessageInSequence 
extends MessageReceiver {

	/** contains the ID of the next expected Message. 
	 * All Messages with lower ID were successfully processed.  */
	protected long currId; 
	
	/** Starts expecting Messages from {@link IMessageReceiver#START_ID}.
	 * @param _processor the Processor to handle the Messages.
	 */
	public MessageInSequence(final IIStreamOut _processor) {
		this(_processor, START_ID);
	}

	/** Starts expecting Messages from the given ID.
	 * @param _processor the Processor to handle the Messages.
	 * @param _currId the ID of the next expected Message
	 */
	public MessageInSequence(final IIStreamOut _processor, final long _currId) {
		super(_processor);
		this.currId = _currId;
	}

	/** Accepts the given Message only if its ID matches the expected one (or this Receiver has not
	 * started yet); any other Message is rejected without caching, so the Sender must retry.
	 * @see streamIO.asyncMessage.IMessageReceiver#addItem(long, java.lang.Object)
	 */
	public long addItem(final long id, final Object value) {
		if ((id != currId) && (currId != START_ID)) //accept Messages only in Sequence
			return currId; //except when starting off new...
		final long nextId = currId+1; 
		if (super.addItem(id, value) == nextId) 
			this.currId = nextId; 
		return currId; 
	}

	/** unused entry point; kept for the ad-hoc Test Convention used across this Package. */
	public static void main(String[] args) {
	}
}
