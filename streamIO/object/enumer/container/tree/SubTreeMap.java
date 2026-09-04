/*
 * Created on 07.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object.enumer.container.tree;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

/**
 * 
 * Title: <p>
 * Description:
 * Purpose:
 * This Class represents a sorted Sub-Map, i.e. Subset of a sorted Map 
 * including the 'left' Value, but excluding the 'right' Value. 
 * 
 * Design Decisions / Implementation Details:
 * This Map is backed by a larger TreeMap. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
class SubTreeMap 
extends AbstractMap 
implements SortedMap, java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Reference to the Super Map 	 */
	final TreeMap map;

	/**
	 * fromKey is significant only if fromStart is false.  Similarly,
	 * toKey is significant only if toStart is false.
	 */
	boolean fromStart = false, toEnd = false;
	Object  fromKey,		   toKey;
	
	SubTreeMap(TreeMap map, Object fromKey, Object toKey) {
		if (this.map.compare(fromKey, toKey) > 0)
			throw new IllegalArgumentException("fromKey > toKey");
		this.map = map;
		this.fromKey = fromKey;
		this.toKey = toKey;
	}

	SubTreeMap(TreeMap map, Object key, boolean headMap) {
		this.map.compare(key, key); // Type-check key
		this.map = map;

		if (headMap) {
			fromStart = true;
			toKey = key;
		} else {
			toEnd = true;
			fromKey = key;
		}
	}

	SubTreeMap(TreeMap map, boolean fromStart, Object fromKey, boolean toEnd, Object toKey){
		this.fromStart = fromStart;
		this.map = map;
		this.fromKey= fromKey;
		this.toEnd = toEnd;
		this.toKey = toKey;
	}
	
	private transient Set entrySet = new TreeEntrySetView(this);
	
	public Set entrySet() { return entrySet; }

	public boolean isEmpty() { return entrySet.isEmpty(); }
	
	public boolean containsKey(final Object key) {
		return inRange(key) && this.map.containsKey(key);
	}
	
	public Object get(Object key) {
		if (!inRange(key))
			return null;
		return this.map.get(key);
	}
	
	public Object put(final Object key, final Object value) {
		if (!inRange(key))
			throw new IllegalArgumentException("key out of range");
		return this.map.put(key, value);
	}
	
	public Comparator comparator() {
		return this.map.comparator();
	}
	
	public Object firstKey() {
		final Object first = (fromStart ? this.map.firstEntry():this.map.getCeilEntry(fromKey)).key;
		if (!toEnd && this.map.compare(first, toKey) >= 0)
			throw(new NoSuchElementException());
		return first;
	}
	
	public Object lastKey() {
		final Object last = (toEnd ? this.map.lastEntry() : this.map.getPrecedingEntry(toKey)).key;
		if (!fromStart && this.map.compare(last, fromKey) < 0)
			throw(new NoSuchElementException());
		return last;
	}
	
	public SortedMap subMap(Object fromKey, Object toKey) {
		if (!inRange2(fromKey))
			throw new IllegalArgumentException("fromKey out of range");
		if (!inRange2(toKey))
			throw new IllegalArgumentException("toKey out of range");
		return new SubTreeMap(this.map, fromKey, toKey);
	}
	
	public SortedMap headMap(final Object toKey) {
		if (!inRange2(toKey))
			throw new IllegalArgumentException("toKey out of range");
		return new SubTreeMap(map, fromStart, fromKey, false, toKey);
	}
	
	public SortedMap tailMap(final Object fromKey) {
		if (!inRange2(fromKey))
			throw new IllegalArgumentException("fromKey out of range");
		return new SubTreeMap(this.map, false, fromKey, toEnd, toKey);
	}
	
	public boolean inRange(final Object key) {
		return (fromStart || map.compare(key, fromKey) >= 0) &&
			   (toEnd	  || map.compare(key,   toKey) <  0);
	}
	
	// This form allows the high endpoint (as well as all legit keys)
	private boolean inRange2(final Object key) {
		return (fromStart || map.compare(key, fromKey) >= 0) &&
			   (toEnd	  || map.compare(key,   toKey) <= 0);
	}
	
}