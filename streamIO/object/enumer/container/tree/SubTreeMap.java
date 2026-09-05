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
 * <!-- docstate
 * tags: [code/red_black_tree, code/iterator_pattern]
 * concepts: [Red-Black Tree Backed Sorted Map Implementation]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
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
	
	// TODO: LOGIC: this.map is read (via compare()) before it is assigned a few lines
	// below, so this validation reads the not-yet-initialized final field. javac requires
	// definite assignment for a blank final read, so this either fails to compile as
	// written or (if map is not actually blank-final at build time) throws a
	// NullPointerException on every call with a non-empty range.
	SubTreeMap(TreeMap map, Object fromKey, Object toKey) {
		if (this.map.compare(fromKey, toKey) > 0)
			throw new IllegalArgumentException("fromKey > toKey");
		this.map = map;
		this.fromKey = fromKey;
		this.toKey = toKey;
	}

	// TODO: LOGIC: same as the constructor above - this.map is read (via compare()) before
	// being assigned two lines below.
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

	/** Returns a view over this sub-map's entries.
	 * @return a view over this sub-map's entries, backed by the underlying {@link TreeMap} */
	public Set entrySet() { return entrySet; }

	/** Tests whether this sub-map's key range is empty.
	 * @return true when this sub-map's key range contains no entries */
	public boolean isEmpty() { return entrySet.isEmpty(); }

	/**
	 * Tests whether the key is in range and present in the backing map.
	 * @param key the key to look up
	 * @return true when the key is within this sub-map's range and present in the backing map
	 */
	public boolean containsKey(final Object key) {
		return inRange(key) && this.map.containsKey(key);
	}

	/**
	 * Looks up the value mapped to an in-range key.
	 * @param key the key to look up
	 * @return the mapped value, or null when the key is out of range or absent
	 */
	public Object get(Object key) {
		if (!inRange(key))
			return null;
		return this.map.get(key);
	}

	/**
	 * Associates a value with a key, requiring the key to be in range.
	 * @param key the key to associate, must be within this sub-map's range
	 * @param value the value to associate with the key
	 * @return the previous value mapped to the key, or null if there was none
	 * @throws IllegalArgumentException when the key is out of range
	 */
	public Object put(final Object key, final Object value) {
		if (!inRange(key))
			throw new IllegalArgumentException("key out of range");
		return this.map.put(key, value);
	}

	/** Returns the underlying map's Comparator.
	 * @return the comparator used by the underlying {@link TreeMap}, or null for natural order */
	public Comparator comparator() {
		return this.map.comparator();
	}

	/**
	 * Returns the lowest key in this sub-map's range.
	 * @return the lowest key in this sub-map's range
	 * @throws NoSuchElementException when this sub-map is empty
	 */
	public Object firstKey() {
		final Object first = (fromStart ? this.map.firstEntry():this.map.getCeilEntry(fromKey)).key;
		if (!toEnd && this.map.compare(first, toKey) >= 0)
			throw(new NoSuchElementException());
		return first;
	}
	
	/**
	 * Returns the highest key in this sub-map's range.
	 * @return the highest key in this sub-map's range
	 * @throws NoSuchElementException when this sub-map is empty
	 */
	public Object lastKey() {
		final Object last = (toEnd ? this.map.lastEntry() : this.map.getPrecedingEntry(toKey)).key;
		if (!fromStart && this.map.compare(last, fromKey) < 0)
			throw(new NoSuchElementException());
		return last;
	}

	/**
	 * Narrows this sub-map to a tighter key range.
	 * @param fromKey the inclusive lower bound, must be within this sub-map's range
	 * @param toKey the exclusive upper bound, must be within this sub-map's range
	 * @return a view over the narrower key range, backed by the same underlying map
	 * @throws IllegalArgumentException when either bound is out of range
	 */
	public SortedMap subMap(Object fromKey, Object toKey) {
		if (!inRange2(fromKey))
			throw new IllegalArgumentException("fromKey out of range");
		if (!inRange2(toKey))
			throw new IllegalArgumentException("toKey out of range");
		return new SubTreeMap(this.map, fromKey, toKey);
	}

	/**
	 * Narrows this sub-map to the keys below a bound.
	 * @param toKey the exclusive upper bound, must be within this sub-map's range
	 * @return a view over the keys below {@code toKey}, backed by the same underlying map
	 * @throws IllegalArgumentException when {@code toKey} is out of range
	 */
	public SortedMap headMap(final Object toKey) {
		if (!inRange2(toKey))
			throw new IllegalArgumentException("toKey out of range");
		return new SubTreeMap(map, fromStart, fromKey, false, toKey);
	}

	/**
	 * Narrows this sub-map to the keys at or above a bound.
	 * @param fromKey the inclusive lower bound, must be within this sub-map's range
	 * @return a view over the keys at or above {@code fromKey}, backed by the same underlying map
	 * @throws IllegalArgumentException when {@code fromKey} is out of range
	 */
	public SortedMap tailMap(final Object fromKey) {
		if (!inRange2(fromKey))
			throw new IllegalArgumentException("fromKey out of range");
		return new SubTreeMap(this.map, false, fromKey, toEnd, toKey);
	}

	/**
	 * Tests whether a key falls within this sub-map's range.
	 * @param key the key to test
	 * @return true when the key falls within this sub-map's [fromKey, toKey) range
	 */
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