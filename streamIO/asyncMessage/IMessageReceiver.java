/*
 * Created on 05.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.asyncMessage;

/**
 * Receives Messages identified by a strictly ascending {@code long} ID, the asynchronous
 * counterpart to an {@link streamIO.IIStreamOut}.
 * Implementors are free to choose their own Service Level Agreement for out-of-sequence
 * or duplicate Messages - see {@link MessageReceiver}, {@link MessageOnlyOnce} and
 * {@link MessageInSequence} for increasingly strict Guarantees.
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:48:35Z
 * digest: 9a9ea3dd27cae651f99d6d6e5da5df017b75c5c97c92433335823aac3e17943c
 * stale: false
 * tags: [code/message_queue]
 * concepts: [Asynchronous Messaging]
 * facets: {layer: infrastructure, status: stable, complexity: low}
 * -->
 */
public interface IMessageReceiver {

	/** Initial Value for Message Senders and Receivers 	*/
	final static public long START_ID = Long.MIN_VALUE; 
	
	/**
	 * add a Message to the Processing 
	 * 
	 * @param id the unique, ascending ID of the Message 
	 * @param value the actual Message Content
	 * @return the id of the next expected Message
	 * the given id, if Processing was accepted but failed 
	 * a previous id, if resending is necessary  
	 */
	long addItem(final long id, final Object value); 
	
	/** clears any Buffers downstream, 
	 * substitute for the close() Method 
	 * which is irreversible and not necessary 
	 * but left to Garbage Collection (for freeing Resources). 
	 * @author heuerm
	 */ 
	void flush(); 
}
