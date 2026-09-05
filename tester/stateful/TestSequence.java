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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:14:33Z
 * digest: c8c104f256b08298f76a8d0634b8af8f4951c8ac063aea31b0bc00e915d7a0ba
 * stale: false
 * tags: [code/stateful_algorithm]
 * concepts: [Test Sequence Runner]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class TestSequence 
implements ITester {

	/** counts the Items in a Sequence
	 * works differently for #addItem() and #nextItem()
	 */
	public int numItemsInSequence; //= 0; 
	
	/** The last Object seen by {@link #test(Object)}, compared against on the next call. */
	protected Object last; //= null;
	
	/** Flag to switch Polarity of the Output without having to pipe TesterNOT.	 */
	public boolean falseOnSequence; 
	
    /** Empty Constructor defaulting the last Object to null and the Polarity to true on Sequence.     */
    public TestSequence() { }
    
    /**
     * Creates a TestSequence with the last Object defaulted to null.
     * @param _falseOnSequence whether to invert the result returned by {@link #test(Object)}
     */
    public TestSequence(final boolean _falseOnSequence) { falseOnSequence = _falseOnSequence; }

    /**
     * Creates a TestSequence starting from a given initial sequence value.
     * @param _falseOnSequence whether to invert the result returned by {@link #test(Object)}
     * @param initial the initial Sequence Value to use for Testing.
     */
    public TestSequence(boolean _falseOnSequence, final Object initial) {
        falseOnSequence = _falseOnSequence; 
        last = initial; 
    }
    
    /** Tests whether arg continues the run of equal/identical items, updating {@link #numItemsInSequence} accordingly.
     * @see tester.ITester#test(java.lang.Object)     */
    public boolean test(final Object arg) {
        final boolean equals = ValidationRule.EQUALS(last, arg); last = arg;
        if (equals)
            ++numItemsInSequence; 
        else
            numItemsInSequence = 0; 
        return falseOnSequence != equals; 
    }

    /** Empty entry point stub; kept for consistency with the other classes in this package.
     * @param args unused */
    public static void main(final String[] args) {
    }
    
}
