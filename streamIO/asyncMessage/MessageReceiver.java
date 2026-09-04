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
 */
public class MessageReceiver 
implements IMessageReceiver {

	/** Reference to the Processor
	 * Any thrown Exception or return of null 
	 * is interpreted as non-processing. 
	 * The first Processor should be a persistent Store. 
	 */
	private IIStreamOut processor; 
	
	/**
	 * @param processor
	 */
	public MessageReceiver(final IIStreamOut processor) {
		this.processor = processor;
	}
	
	/** @see streamIO.IStreamOut#flush()	 */
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

	public static void main(String[] args) {
	}

}
