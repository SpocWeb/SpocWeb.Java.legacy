/*
 * File Name: ShortComparator.java
 * Created on: 27.12.2003
 *
 */
package function.byref;

import java.util.Comparator;

/**
 * Title: ShortComparator<p>
 * Description:
 * Allows to compare two Arrays of short[] 
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class ShortComparator implements Comparator {

	final public int index; 
	
	/** Constructor 
	 * 
	 * @param index_
	 */
	public ShortComparator(final int index_) {
		this.index = index_;
	}

	/** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(Object o1, Object o2) {
		final short s1 = ((short[]) o1)[index];
		final short s2 = ((short[]) o2)[index];
		if (s1 > s2) {
			return 1;
		}
		if (s1 < s2) {
			return -1;
		}
		return 0;
	}

}
