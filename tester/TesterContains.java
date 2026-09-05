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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:10:22Z
 * digest: 5bcb84fe5fc624e364d300e0b25c8f7bbac52039ef929b485ce85cde23dd1a12
 * stale: false
 * tags: [code/predicate_filter]
 * concepts: [Containment Tester]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
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

	/** Returns whether {@link #str} contains the character carried by an {@link ICountAble} or an {@code int[]}, false for any other argument type.
	 * @see tester.ITester#test(java.lang.Object)	 */
	public boolean test(final Object arg) {
		if(arg instanceof ICountAble)
			return test ((ICountAble) arg);
		if(arg instanceof int[])
			return test(((int[]) arg)[0]);
		return false;
	}

	/** Returns whether {@link #str} contains the character given by arg's {@link ICountAble#getInt()}.
	 * @see tester.ITester#test(java.lang.Object)	 */
	public boolean test(final ICountAble arg) { return str.indexOf(arg.getInt()) >= 0; }

	/** Returns whether {@link #str} contains the given character code.
	 * @see tester.ITester#test(java.lang.Object)	 */
	public boolean test(final int arg) { return str.indexOf(arg) >= 0; }
	
}
