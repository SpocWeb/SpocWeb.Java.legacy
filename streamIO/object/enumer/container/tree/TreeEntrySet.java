/*
 * Created on 09.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.AbstractSet;
import java.util.Iterator;

import synch.ValidationRule;

/**
 * Set implementation based on a sorted Tree. 
 * @author heuerm
 *
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
final class TreeEntrySet 
extends AbstractSet {
	
	private final TreeMap map;

	/**
	 * @param map
	 */
	TreeEntrySet(TreeMap map) {
		this.map = map;
	}

	/**
	 * Iterates over the backing map's entries.
	 * @return an iterator over the backing map's entries, in ascending key order
	 */
	public Iterator iterator() {
		return new TreeEntryIterator(map);
	}

	/**
	 * Tests whether the backing map holds a matching Entry.
	 * @param o the {@link TreeMapEntry} to look up
	 * @return true when the backing map holds an entry with the same key and value
	 */
	public boolean contains(final Object o) {
		if (!(o instanceof TreeMapEntry))
			return false;
		TreeMapEntry entry = (TreeMapEntry)o;
		final Object value = entry.getVal();
		TreeMapEntry p = this.map.getEntry(entry.getKey());
		return p != null && ValidationRule.EQUALS(p.getVal(), value);
	}

	/**
	 * Removes the backing map's entry matching {@code o}'s key and value.
	 *
	 * @param o the {@link TreeMapEntry} to remove
	 * @return true when a matching entry was found and removed
	 */
	public boolean remove(final Object o) {
		if (!(o instanceof TreeMapEntry))
			return false;
		TreeMapEntry entry = (TreeMapEntry) o;
		Object value = entry.getVal();
		TreeMapEntry p = this.map.getEntry(entry.getKey());
		if (p != null && ValidationRule.EQUALS(p.getVal(), value)) {
			this.map.deleteEntry(p);
			return true;
		}
		return false;
	}

	/**
	 * Returns the backing map's current Size.
	 * @return the number of entries in the backing map
	 */
	public int size() {
		return this.map.size();
	}

	/** Removes all entries from the backing map. */
	public void clear() {
		this.map.clear();
	}
}