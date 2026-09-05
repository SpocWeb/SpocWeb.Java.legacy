/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

/**
 * Iterates over the keys of a {@link TreeMap}, in ascending key order.
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class TreeKeyIterator
extends TreeEntryIterator {
	/**
	 * Creates an iterator over all keys of the given map, starting at its first entry.
	 *
	 * @param map the map whose keys are iterated
	 */
	public TreeKeyIterator(final TreeMap map) { super(map); }

	/**
	 * Creates an iterator over the given map's keys, starting at a specific entry.
	 *
	 * @param map the map whose keys are iterated
	 * @param first the entry to start iteration from
	 */
	public TreeKeyIterator(final TreeMap map, final TreeMapEntry first) {
		super(map, first);
	}

	/**
	 * Returns the key of the next entry in iteration order.
	 * @return the key of the next entry in iteration order
	 */
	public Object next() { return nextEntry().key; }
}