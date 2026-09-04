/*
 * Created on 08.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.AbstractSet;
import java.util.Iterator;



final class TreeKeySet 
extends AbstractSet {
	private final TreeMap map;

	/**
	 * @param map
	 */
	TreeKeySet(final TreeMap map) {
		this.map = map;
	}

	public Iterator iterator() {
		return new TreeKeyIterator(map);
	}

	public int size() {
		return this.map.size();
	}

	public boolean contains(Object o) {
		return this.map.containsKey(o);
	}

	public boolean remove(Object o) {
		int oldSize = this.map.size();
		this.map.remove(o);
		return this.map.size() != oldSize;
	}

	public void clear() {
		this.map.clear();
	}
}