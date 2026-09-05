/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.AbstractCollection;
import java.util.Iterator;

import synch.ValidationRule;


/**
 * A {@link java.util.Collection} view over the values of a {@link TreeMap}, backed by
 * it so mutations on either side are reflected in the other.
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
final class TreeValueCollection
extends AbstractCollection {
	private final TreeMap map;

	/**
	 * Creates a value-collection view backed by the given map.
	 *
	 * @param map the map whose values this collection exposes
	 */
	TreeValueCollection(final TreeMap map) {
		this.map = map;
	}

	/**
	 * Iterates over the backing map's values.
	 * @return an iterator over the backing map's values, in ascending key order
	 */
	public Iterator iterator() {
		return new TreeValueIterator(map);
	}

	/**
	 * Returns the backing map's current Size.
	 * @return the number of entries in the backing map
	 */
	public int size() {
		return this.map.size();
	}

	/**
	 * Tests whether any entry's value equals the given one.
	 * @param o the value to look up, compared via {@link ValidationRule#EQUALS}
	 * @return true when some entry's value equals {@code o}
	 */
	public boolean contains(Object o) {
		for (TreeMapEntry e = this.map.firstEntry(); e != null; e = e.succ())
			if (ValidationRule.EQUALS(e.getVal(), o))
				return true;
		return false;
	}

	/**
	 * Removes the first entry whose value equals {@code o} from the backing map.
	 *
	 * @param o the value to remove, compared via {@link ValidationRule#EQUALS}
	 * @return true when a matching entry was found and removed
	 */
	public boolean remove(Object o) {
		for (TreeMapEntry e = this.map.firstEntry(); e != null; e = e.succ()) {
			if (ValidationRule.EQUALS(e.getVal(), o)) {
				this.map.deleteEntry(e);
				return true;
			}
		}
		return false;
	}

	/** Removes all entries from the backing map. */
	public void clear() {
		this.map.clear();
	}
}