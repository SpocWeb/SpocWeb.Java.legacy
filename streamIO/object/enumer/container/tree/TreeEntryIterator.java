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
 * -->
 */
public class TreeEntryIterator 
implements Iterator {
	
	/** Reference to the TreeMap iterated over 	*/
	protected final TreeMap map;
	
	/** Modification Count at Creation	 */
	protected int expectedModCount;
	
	protected TreeMapEntry lastReturned = null;
	
	protected TreeMapEntry next;
	
	///////////////////////////////////////////////////////////////////////////
	
	TreeEntryIterator(final TreeMap _map) {
		this(_map, _map.firstEntry());
	}

	// Used by SubMapEntryIterator
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