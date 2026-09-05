/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.AbstractSet;
import java.util.Iterator;



/**
 * A {@link java.util.Set} view over the keys of a {@link TreeMap}, backed by it so
 * mutations on either side are reflected in the other.
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
final class TreeKeySet
extends AbstractSet {
	private final TreeMap map;

	/**
	 * Creates a key-set view backed by the given map.
	 *
	 * @param map the map whose keys this set exposes
	 */
	TreeKeySet(final TreeMap map) {
		this.map = map;
	}

	/**
	 * Iterates over the backing map's keys.
	 * @return an iterator over the backing map's keys, in ascending order
	 */
	public Iterator iterator() {
		return new TreeKeyIterator(map);
	}

	/**
	 * Returns the backing map's current Size.
	 * @return the number of keys in the backing map
	 */
	public int size() {
		return this.map.size();
	}

	/**
	 * Tests whether the backing map contains the given key.
	 * @param o the key to look up
	 * @return true when the backing map contains this key
	 */
	public boolean contains(Object o) {
		return this.map.containsKey(o);
	}

	/**
	 * Removes the entry keyed by {@code o} from the backing map.
	 *
	 * @param o the key to remove
	 * @return true when the backing map's size changed as a result
	 */
	public boolean remove(Object o) {
		int oldSize = this.map.size();
		this.map.remove(o);
		return this.map.size() != oldSize;
	}

	/** Removes all entries from the backing map. */
	public void clear() {
		this.map.clear();
	}
}