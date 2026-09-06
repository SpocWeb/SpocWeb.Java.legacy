/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;



/**
 * TreeMap Iterator.
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: 72f97cbf280b793065fc7ac0ea2d998ad7aa4f92acf6a3b1559cab2eddf89e2b
 * stale: false
 * -->
 */
public class TreeEntryIterator 
implements Iterator {
	
	/** Reference to the TreeMap iterated over 	*/
	protected final TreeMap map;
	
	/** Modification Count at Creation	 */
	protected int expectedModCount;
	
	/** The entry last returned by {@link #next()}, or null if none has been returned yet or it was already removed. */
	protected TreeMapEntry lastReturned = null;

	/** The entry {@link #next()} will return next, or null when iteration is exhausted. */
	protected TreeMapEntry next;

	///////////////////////////////////////////////////////////////////////////

	/** Creates an iterator over the whole map, starting at its first entry.
	 * @param _map the map to iterate over */
	TreeEntryIterator(final TreeMap _map) {
		this(_map, _map.firstEntry());
	}

	/** Creates an iterator starting at a given entry; used by {@link SubTreeMapEntryIterator} to iterate a bounded range.
	 * @param _map the map to iterate over
	 * @param _first the entry to start from */
	TreeEntryIterator(final TreeMap _map, final TreeMapEntry _first) {
		this.map = _map;
		this.next = _first;
		this.expectedModCount = _map.modCount; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Tests whether a next entry is available.
	 * @return true when a next entry is available
	 */
	public boolean hasNext() { return next != null; }

	/**
	 * Returns the next entry in iteration order.
	 * @return the next entry in iteration order
	 * @throws java.util.NoSuchElementException when no next entry remains
	 * @throws ConcurrentModificationException when the map changed since this iterator was created
	 */
	public Object next() { return nextEntry(); }

	/** for typesafe Iteration	 */
	final TreeMapEntry nextEntry() {
		if (next == null)
			throw new NoSuchElementException();
		if (this.map.modCount != expectedModCount)
			throw new ConcurrentModificationException();
		lastReturned = next;
		next = next.succ();
		return lastReturned;
	}
	
	/**
	 * Removes the entry last returned by {@link #next()} from the backing map.
	 *
	 * @throws IllegalStateException when {@link #next()} was never called, or was already
	 *         followed by a {@code remove()}
	 * @throws ConcurrentModificationException when the map changed since this iterator was created
	 */
	public void remove() {
		if (lastReturned == null)
			throw new IllegalStateException();
		if (this.map.modCount != expectedModCount)
			throw new ConcurrentModificationException();
		if (lastReturned.prev != null && lastReturned.next != null) 
			next = lastReturned; 
		this.map.deleteEntry(lastReturned);
		expectedModCount++;
		lastReturned = null;
	}
	
}