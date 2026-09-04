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
	
	public boolean hasNext() { return next != null; }
	
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