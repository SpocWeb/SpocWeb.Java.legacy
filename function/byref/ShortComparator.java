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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:54:55Z
 * digest: f322d5e656d85a1d4e644c3ca172bd5cbd1060b8aedf37a7bdbfd2901aad7b8e
 * stale: false
 * tags: [code/numeric_comparison]
 * concepts: [Comparator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class ShortComparator implements Comparator {

	/** Index into each short[] array at which the compared elements are read. */
	final public int index;

	/** Constructor
	 *
	 * @param index_
	 */
	public ShortComparator(final int index_) {
		this.index = index_;
	}

	/** Compares two {@code short[]} arrays by the element at {@link #index}.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
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
