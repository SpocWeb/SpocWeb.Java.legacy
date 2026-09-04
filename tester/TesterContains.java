/*
 * Created on 29.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tester;

import function.ICountAble;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Implements a Test for Containment of a Character in a String. 
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
public class TesterContains 
implements ITester {
	
	/** the String containing all Characters to search for.  */
	final public String str; 
	
	/** 
	 * Initializing Constructor
	 * @param _str the String containing all Characters to search for. 
	 */
	public TesterContains(final String _str) {
		this.str = _str; 
	}

	/** @see tester.ITester#test(java.lang.Object)	 */
	public boolean test(final Object arg) {
		if(arg instanceof ICountAble)
			return test ((ICountAble) arg);
		if(arg instanceof int[])
			return test(((int[]) arg)[0]);
		return false; 
	}
	
	/** @see tester.ITester#test(java.lang.Object)	 */
	public boolean test(final ICountAble arg) { return str.indexOf(arg.getInt()) >= 0; }
	
	/** @see tester.ITester#test(java.lang.Object)	 */
	public boolean test(final int arg) { return str.indexOf(arg) >= 0; }
	
}
