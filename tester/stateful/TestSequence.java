/*
 * Created on 05.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tester.stateful;

import synch.ValidationRule;
import tester.ITester;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Stateful Tester returning true when the Items in a Test Sequence are equal or identical. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public class TestSequence 
implements ITester {

	/** counts the Items in a Sequence
	 * works differently for #addItem() and #nextItem()
	 */
	public int numItemsInSequence; //= 0; 
	
	protected Object last; //= null; 
	
	/** Flag to switch Polarity of the Output without having to pipe TesterNOT.	 */
	public boolean falseOnSequence; 
	
    /** Empty Constructor defaulting the last Object to null and the Polarity to true on Sequence.     */
    public TestSequence() { }
    
    /**
     * 
     * @param _falseOnSequence
     */
    public TestSequence(final boolean _falseOnSequence) { falseOnSequence = _falseOnSequence; }
    
    /**
     * 
     * @param _falseOnSequence
     * @param initial the initial Sequence Value to use for Testing.
     */
    public TestSequence(boolean _falseOnSequence, final Object initial) { 
        falseOnSequence = _falseOnSequence; 
        last = initial; 
    }
    
    /** @see tester.ITester#test(java.lang.Object)     */
    public boolean test(final Object arg) { 
        final boolean equals = ValidationRule.EQUALS(last, arg); last = arg;
        if (equals)
            ++numItemsInSequence; 
        else
            numItemsInSequence = 0; 
        return falseOnSequence != equals; 
    }

    public static void main(final String[] args) {
    }
    
}
