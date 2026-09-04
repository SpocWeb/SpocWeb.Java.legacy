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

	public Iterator iterator() {
		return new TreeEntryIterator(map);
	}

	public boolean contains(final Object o) {
		if (!(o instanceof TreeMapEntry))
			return false;
		TreeMapEntry entry = (TreeMapEntry)o;
		final Object value = entry.getVal();
		TreeMapEntry p = this.map.getEntry(entry.getKey());
		return p != null && ValidationRule.EQUALS(p.getVal(), value);
	}

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

	public int size() {
		return this.map.size();
	}

	public void clear() {
		this.map.clear();
	}
}