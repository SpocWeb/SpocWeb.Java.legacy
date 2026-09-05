/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.NoSuchElementException;

/**
 * Iterates over the entries of a {@link TreeMap} bounded above by an excluded key,
 * as used by {@link SubTreeMap} to expose a key-range view.
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
class SubTreeMapEntryIterator
extends TreeEntryIterator {
	private final Object firstExcludedKey;

	/**
	 * Creates an iterator over the given map's entries, starting at {@code first} and
	 * stopping before the entry keyed by {@code firstExcluded}.
	 *
	 * @param map the map whose entries are iterated
	 * @param first the entry to start iteration from
	 * @param firstExcluded the entry to stop before, or null when the range is unbounded above
	 */
	SubTreeMapEntryIterator(TreeMap map, TreeMapEntry first, TreeMapEntry firstExcluded) {
		super(map, first);
		firstExcludedKey = (firstExcluded == null ?
							firstExcluded : firstExcluded.key);
	}

	/**
	 * Tests whether a next in-range entry is available.
	 * @return true when a next entry exists and its key is not the excluded upper bound
	 */
	public boolean hasNext() {
		return next != null && next.key != firstExcludedKey;
	}

	/**
	 * Returns the next in-range entry in iteration order.
	 * @return the next entry in iteration order
	 * @throws NoSuchElementException when no entry remains before the excluded upper bound
	 */
	public Object next() {
		if (next == null || next.key == firstExcludedKey)
			throw new NoSuchElementException();
		return nextEntry();
	}
}