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


class TreeEntrySetView 
extends AbstractSet {
	
	private final SubTreeMap map;

	/**
	 * @param map
	 */
	TreeEntrySetView(SubTreeMap map) {
		this.map = map;
		// TODO Auto-generated constructor stub
	}

	private transient int size = -1, sizeModCount;

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

	public boolean isEmpty() {
		return !iterator().hasNext();
	}

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

	public Iterator iterator() {
		return new SubTreeMapEntryIterator(this.map.map,  
			(this.map.fromStart ? this.map.map.firstEntry() : this.map.map.getCeilEntry(this.map.fromKey)),
			(this.map.toEnd	    ? null                      : this.map.map.getCeilEntry(this.map.toKey)));
	}
}