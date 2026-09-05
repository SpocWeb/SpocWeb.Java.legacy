/*
 * Created on 05.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.asyncMessage;

import streamIO.IIStreamOut;
import streamIO.Log;

/**
 * Implements the Interface for the least Service Level Agreement (SLA): 
 * reliable Transport. 
 * Actually this is implemented on the Client Side Adapter.   
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:48:44Z
 * digest: bbd65fad70b09bee67c64c7a0f202b633a515c3ec3cb1ba127cd7e83a960a451
 * stale: false
 * tags: [code/message_queue]
 * concepts: [Asynchronous Messaging]
 * facets: {layer: infrastructure, status: stable, complexity: low}
 * -->
 */
public class MessageReceiver 
implements IMessageReceiver {

	/** Reference to the Processor
	 * Any thrown Exception or return of null 
	 * is interpreted as non-processing. 
	 * The first Processor should be a persistent Store. 
	 */
	private IIStreamOut processor; 
	
	/** Wraps the given downstream Processor.
	 * @param processor the Processor to forward accepted Messages to.
	 */
	public MessageReceiver(final IIStreamOut processor) {
		this.processor = processor;
	}

	/** Does nothing; there is no Buffer to clear at this Service Level.
	 * @see streamIO.IStreamOut#flush()	 */
	public void flush() {}
	
	/**
	 * Tries to process any incoming Message.  
	 * @see streamIO.asyncMessage.IMessageReceiver#addItem(long, java.lang.Object)
	 */
	public long addItem(final long id, final Object value) {
		IIStreamOut result = null; 
		try { //ignores the id
			result = processor.addItem(value); 
		} catch (Exception x) {
			Log.N(x); 
		}
		if (result == null) 
			return id; 
		return id+1;
	}

	/** unused entry point; kept for the ad-hoc Test Convention used across this Package. */
	public static void main(String[] args) {
	}

}
