/*
 * Created on 05.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.asyncMessage;

/**
 * @author heuerm
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
