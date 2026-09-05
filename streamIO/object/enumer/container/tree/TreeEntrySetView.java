/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

import synch.ValidationRule;


/**
 * An {@link java.util.Set} view over the entries of a {@link SubTreeMap}'s key range,
 * backed by the underlying {@link TreeMap} so mutations on either side are reflected
 * in the other.
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
class TreeEntrySetView
extends AbstractSet {

	private final SubTreeMap map;

	/**
	 * Creates an entry-set view over the given key-range map.
	 *
	 * @param map the sub-map whose entries this set exposes
	 */
	TreeEntrySetView(SubTreeMap map) {
		this.map = map;
	}

	private transient int size = -1, sizeModCount;

	/**
	 * Returns the sub-map's current Size.
	 * @return the number of entries within the sub-map's key range, recomputed by a full
	 *         scan when the backing map has been structurally modified since the last call
	 */
	public int size() {
		if (size == -1 || sizeModCount != this.map.map.modCount) {
			size = 0;  sizeModCount = this.map.map.modCount;
			Iterator i = iterator();
			while (i.hasNext()) {
				size++;
				i.next();
			}
		}
		return size;
	}

	/**
	 * Tests whether the sub-map's key range is empty.
	 * @return true when the sub-map's key range contains no entries
	 */
	public boolean isEmpty() {
		return !iterator().hasNext();
	}

	/**
	 * Tests whether the sub-map holds a matching in-range Entry.
	 * @param o the {@link Map.Entry} to look up
	 * @return true when the entry's key is within range and the backing map holds an
	 *         entry with the same key and value
	 */
	public boolean contains(final Object o) {
		if (!(o instanceof Map.Entry))
			return false;
		Map.Entry entry = (Map.Entry)o;
		Object key = entry.getKey();
		if (!this.map.inRange(key))
			return false;
		TreeMapEntry node = this.map.map.getEntry(key);
		return node != null &&
			ValidationRule.EQUALS(node.getVal(), entry.getValue());
	}

	/**
	 * Removes the backing map's entry matching {@code o}'s key and value, when the key
	 * is within this view's range.
	 *
	 * @param o the {@link Map.Entry} to remove
	 * @return true when a matching in-range entry was found and removed
	 */
	public boolean remove(Object o) {
		if (!(o instanceof Map.Entry))
			return false;
		Map.Entry entry = (Map.Entry)o;
		Object key = entry.getKey();
		if (!this.map.inRange(key))
			return false;
		TreeMapEntry node = this.map.map.getEntry(key);
		if (node!=null && ValidationRule.EQUALS(node.getVal(),entry.getValue())){
			this.map.map.deleteEntry(node);
			return true;
		}
		return false;
	}

	/**
	 * Iterates over the entries in the sub-map's key range.
	 * @return an iterator over the entries in the sub-map's key range, in ascending
	 *         key order
	 */
	public Iterator iterator() {
		return new SubTreeMapEntryIterator(this.map.map,
			(this.map.fromStart ? this.map.map.firstEntry() : this.map.map.getCeilEntry(this.map.fromKey)),
			(this.map.toEnd	    ? null                      : this.map.map.getCeilEntry(this.map.toKey)));
	}
}