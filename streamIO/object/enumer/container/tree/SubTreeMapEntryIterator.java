/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.NoSuchElementException;


class SubTreeMapEntryIterator 
extends TreeEntryIterator {
	private final Object firstExcludedKey;

	SubTreeMapEntryIterator(TreeMap map, TreeMapEntry first, TreeMapEntry firstExcluded) {
		super(map, first);
		firstExcludedKey = (firstExcluded == null ?
							firstExcluded : firstExcluded.key);
	}

	public boolean hasNext() {
		return next != null && next.key != firstExcludedKey;
	}

	public Object next() {
		if (next == null || next.key == firstExcludedKey)
			throw new NoSuchElementException();
		return nextEntry();
	}
}