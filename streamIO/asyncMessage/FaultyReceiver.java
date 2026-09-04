/*
 * Created on 05.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.asyncMessage;

import java.util.ArrayList;

import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.FilterOut;
import streamIO.IIStreamOut;

/**
 * @author heuerm
 * 
 * This Receiver throws random Exceptions 
 * and is used to test the reliability of the Message Transfer System. 
 */
public class FaultyReceiver 
extends AStreamOut {

	////////////////////////////////////////////////////////////////////////////
	//	static Methods
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//	Member Variables & Constructors 
	////////////////////////////////////////////////////////////////////////////
	
	/** this Store is used to collect items
	 * made public to be able to test the Result.  	*/
	final public ArrayList store = new ArrayList(); 
	
	/** @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final Object arg) {
		if (Math.random() > .5)
			throw new RuntimeException("random Error from "+this+" when receiving "+arg); 
		store.add(arg); 
		return this;
	}
	
	/** tests the implementations of this Package.	 */
	public static void testIt(final Class receiver) {
		final FaultyReceiver  fault = new FaultyReceiver(); 
		final IMessageReceiver recv = (IMessageReceiver) FilterOut.CREATE_OBJECT(receiver, fault); // 
		final MessageStreamOut  out = new MessageStreamOut(recv);
		final int maxVal = 99; 
		for (int i = -1; ++i < maxVal; ) 
			out.addItem(new Integer(i)); 
		out.flush(); 
		//test the Result
		for (int i = -1; ++i < maxVal; ) 
			Assert.EQUALS(fault.store.get(i), new Integer(i)); 
	}
	
	public static void main(final String[] args) {
		testIt(MessageReceiver.class); 
		testIt(MessageOnlyOnce.class); 
		testIt(MessageInSequence.class); 
		testIt(MessageOptimized.class); 
	}
	
}
