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
 * Throws a random Exception on about half of its Calls instead of ever processing a Message,
 * to exercise the Retry Behaviour of {@link MessageStreamOut} and the Duplicate/Sequence
 * Guarantees of the {@link IMessageReceiver} implementations under Test.
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:50:11Z
 * digest: fe07a64020aa732a04d6ca99907ed386d5b2bacc16dbfaa04a46541e616bd5fe
 * stale: false
 * tags: [code/test_harness]
 * concepts: [Reliability Testing]
 * facets: {layer: infrastructure, status: stable, complexity: low}
 * -->
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
	
	/** Randomly throws instead of accepting the given Item, to simulate an unreliable downstream Stage.
	 * @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
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
	
	/** Runs {@link #testIt(Class)} against every Receiver implementation in this Package. */
	public static void main(final String[] args) {
		testIt(MessageReceiver.class); 
		testIt(MessageOnlyOnce.class); 
		testIt(MessageInSequence.class); 
		testIt(MessageOptimized.class); 
	}
	
}
