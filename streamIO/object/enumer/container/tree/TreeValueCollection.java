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


final class TreeValueCollection 
extends AbstractCollection {
	private final TreeMap map;

	/**
	 * @param map
	 */
	TreeValueCollection(final TreeMap map) {
		this.map = map;
	}

	public Iterator iterator() {
		return new TreeValueIterator(map);
	}

	public int size() {
		return this.map.size();
	}

	public boolean contains(Object o) {
		for (TreeMapEntry e = this.map.firstEntry(); e != null; e = e.succ())
			if (ValidationRule.EQUALS(e.getVal(), o))
				return true;
		return false;
	}

	public boolean remove(Object o) {
		for (TreeMapEntry e = this.map.firstEntry(); e != null; e = e.succ()) {
			if (ValidationRule.EQUALS(e.getVal(), o)) {
				this.map.deleteEntry(e);
				return true;
			}
		}
		return false;
	}

	public void clear() {
		this.map.clear();
	}
}