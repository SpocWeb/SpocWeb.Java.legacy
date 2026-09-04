/*
 * Created on 05.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.asyncMessage;

import java.util.BitSet;

import streamIO.IIStreamOut;

/**
 * Receives Messages only once and thus prevents double Processing. 
 * Out of Sequence Processing is allowed for Optimizations: 
 * -either due to Messages coming in out of Sequence 
 * -or due to differing asynchronous Processing Times. 
 * 
 * @author heuerm
 * Design Decisions: 
 * The actual Messages don't have to be cached, unless inSequence is required. 
 * Since you have to access the Vector directly, 
 * a (dynamic) VectorInt is not effective to store the IDs!  
 * 
 * TODO: a periodic Cleanup should shrink the BitVector and increase the Offset. 
 * TODO: If you only store a single Number, you cannot shut down this Receiver 
 * unless all intermediate Messages have been processed. 
 * Thus a Shutdown has to be initiated 
 * which waits for all Messages that have not been processed yet 
 * and only accepts continguous Messages. 
 * Alternatively all not processed Messages have to be written out 
 * into a persistent Store on shutdown. 
 */
public class MessageOnlyOnce 
extends MessageReceiver {

	/** The initial Capacity for the already received Message Numbers. 
	 * The optimum Value is determined by 
	 * how many intermediate Messages are received / processed
	 * while a Message is still missing.  
	 */
	final static public int INITIAL_CAPACITY = 1000; 
	
	/** contains the ID of the next expected Message. 
	 * All Messages with lower ID were successfully processed.  */
	protected long idOffset; 
	
	/** dynamic Cache for the already received Message Numbers. 	*/
	final BitSet receivedIDs; // = new BitSet(INITIAL_CAPACITY); 
	//VectorInt vector = new VectorInt(); 
	
	/**
	 * @param currId the next Message to expect. 
	 * @param processor
	 */
	public MessageOnlyOnce(final IIStreamOut processor) {
		super(processor);
		this.idOffset = START_ID; 
		this.receivedIDs = new BitSet(INITIAL_CAPACITY); 
	}

	/**
	 * @param currId the next Message to expect. 
	 * @param processor
	 */
	public MessageOnlyOnce(final IIStreamOut processor, final int currId) {
		super(processor);
		this.idOffset = currId; 
		this.receivedIDs = new BitSet(INITIAL_CAPACITY); 
	}

	/**
	 * @param currId the next Message to expect. 
	 * @param processor
	 */
	public MessageOnlyOnce(final IIStreamOut processor, final int currId, final BitSet _receivedIDs) {
		super(processor);
		this.idOffset = currId; 
		this.receivedIDs = (BitSet) _receivedIDs.clone(); 
	}
	
	/** @see streamIO.asyncMessage.IMessageReceiver#addItem(long, java.lang.Object)
	 */
	public long addItem(final long id, final Object value) {
		final long nextId = id+1;
		final int currId = (int) (id - this.idOffset);
		if (receivedIDs.get(currId)) 
			return nextId; //Duplicate, ignore this Message.
		if (nextId != super.addItem(id, value))  //signal Failure
			return id; 
		receivedIDs.set(currId);
		return nextId; 
	}
	
	public static void main(String[] args) {
	}
}
