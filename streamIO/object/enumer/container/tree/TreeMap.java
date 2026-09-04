/*
 * Created on 07.05.2005
 *
 */
package streamIO.object.enumer.container.tree;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import function.index.Indexer;

import streamIO.Assert;
import tester.OrderatorComparable;

/**
 * Red-Black tree based implementation of the <tt>SortedMap</tt> interface.
 * This class guarantees that the map will be in ascending key order, 
 * sorted according to the <i>natural order</i> for the key's class 
 * (see <tt>Comparable</tt>), or by the comparator provided to the Constructor. 
 * The HashCode Value can also be used to Order the Elements, 
 * provided that it partitions the Set well enough! 
 * Only the Tree O(log N) Overhead for Sorting will be forfeit then, 
 * because the HashCode typically does not reflect any meaningful Order Relation, 
 * thus it is better to use a HashMap for this! 
 * 
 * This implementation provides guaranteed log(n) time cost for the
 * <tt>containsKey</tt>, <tt>get</tt>, <tt>put</tt> and <tt>remove</tt> operations.  
 * Algorithms are adaptations of those in Cormen, Leiserson, and
 * Rivest's <I>Introduction to Algorithms</I>.<p>
 * 
 * Unlike the Heap which is always a complete binary Tree 
 * and thus can be implemented with a simple Array, 
 * the general Red-Black Tree is not a complete Tree 
 * and thus requires to use intermediate TreeEntry Objects. 
 * 
 * Red-Black Trees are an Implementation of Top-Down-2,3,4 Trees, 
 * i.e. Trees which allow 2, 3 or 4 Child Nodes (split by 1, 2 or 3 Keys in sorted Order). 
 * The Clou is that 4-Nodes can be locally split up 
 * into two 2-Child Nodes with the left and right Key. 
 * The middle Key goes to the Parent Node, increasing it's Degree 
 * without modifying anything else; especially the Height of the Tree is unchanged! 
 * If the Parent Node was a 3-Node and becomes a 4-Node it should be split up too. 
 * To avoid the Overhead of navigating up again (or stacking a recursive Call), 
 * 4-Nodes are automatically split up on the Search Path down the Tree, 
 * so that Parents never have more than 3-Nodes. 
 * The Height of the Tree changes only if the Root Node is split up 
 * and this enlarges the Height of the total Tree, making it completely balanced at any Time! 
 * (Apart from the Fact that sometimes 2 Comparisons are required to decide the Path)
 * 
 * Now 2-3-4-Trees can be represented with binary Nodes having an additional Flag / Color
 * for the Edge to their Parent: 
 * "black" Edges are the ordinary 2-3-4-Tree Edges
 * "red" Edges construct 2-3-4-Vertices from 1, 2 or 3 binary Vertices. 
 * 
 * Note that the ordering maintained by a sorted map (whether or not an
 * explicit comparator is provided) must be <i>consistent with equals</i> if
 * this sorted map is to correctly implement the <tt>Map</tt> interface.  
 * (See <tt>Comparable</tt> or <tt>Comparator</tt> for a precise definition of
 * <i>consistent with equals</i>.)  This is so because the <tt>Map</tt>
 * interface is defined in terms of the equals operation, but a map performs
 * all key comparisons using its <tt>compareTo</tt> (or <tt>compare</tt>)
 * method, so two keys that are deemed equal by this method are, from the
 * standpoint of the sorted map, equal.  The behavior of a sorted map
 * <i>is</i> well-defined even if its ordering is inconsistent with equals; it
 * just fails to obey the general contract of the <tt>Map</tt> interface.<p>
 * 
 * <b>Note that this implementation is not synchronized.</b> If multiple
 * threads access a map concurrently, and at least one of the threads modifies
 * the map structurally, it <i>must</i> be synchronized externally.  (A
 * structural modification is any operation that adds or deletes one or more
 * mappings; merely changing the value associated with an existing key is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.  If no
 * such object exists, the map should be "wrapped" using the
 * <tt>Collections.synchronizedMap</tt> method.  This is best done at creation
 * time, to prevent accidental unsynchronized access to the map: 
 * <pre>
 *	 Map m = Collections.synchronizedMap(new TreeMap(...));
 * </pre><p>
 *
 * The iterators returned by all of this class's "collection view methods" are
 * <i>fail-fast</i>: if the map is structurally modified at any time after the
 * iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> or <tt>add</tt> methods, the iterator throws a
 * <tt>ConcurrentModificationException</tt>.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis. 
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   
 * <i>the fail-fast behavior of iterators should be used only to detect bugs.</i><p>
 *
 * @author  mHeuer
 * @version 1.56, 01/23/03
 * @see Map
 * @see HashMap
 * @see Hashtable
 * @see Comparable
 * @see Comparator
 * @see Collection
 * @see Collections#synchronizedMap(Map)
 * @since 1.2
 */
public class TreeMap 
extends AbstractMap
implements SortedMap, Cloneable, java.io.Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Find the level down to which to assign all nodes false.  This is the
	 * last `full' level of the complete binary tree produced by buildTree. 
	 * The remaining nodes are colored true. 
	 * (This makes a `nice' set of color assignments for future insertions.) 
	 * This level number is computed by 
	 * finding the number of splits needed to reach the zeroeth node.  
	 * (The answer is ~lg(N), but in any case must be computed by same quick O(lg(N)) loop.)
	 */
	private static final int COMPUTE_RED_LEVEL(final int sz) {
		int level = 0;
		for (int m = sz - 1; m >= 0; m = m / 2 - 1) 
			++level;
		return level;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * The Comparator used to maintain order in this TreeMap, or
	 * null if this TreeMap uses its elements natural ordering.
	 *
	 * @serial
	 */
	private Comparator comparator; // = null; //not necessary

	/**
	 * Returns the comparator used to order this map, or <tt>null</tt> if this
	 * map uses its keys' natural order.
	 *
	 * @return the comparator associated with this sorted map, or
	 *				<tt>null</tt> if it uses its keys' natural sort method.
	 */
	public Comparator comparator() { return comparator; }

	/**
	 * Compares two keys using the correct comparison method for this TreeMap.
	 */
	public int compare(final Object k1, final Object k2) {
		return (comparator==null ? ((Comparable)k1).compareTo(k2)
								 : comparator.compare(k1, k2));
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**The current Root of the Tree 
	 * redundant, needn't be serialized 
	 */
	private transient TreeMapEntry root = null;
	
	/**
	 * The number of entries in the tree
	 * redundant, needn't be serialized 
	 */
	private transient int size = 0;
	
	/**
	 * Returns the number of key-value mappings in this map.
	 * @return the number of key-value mappings in this map.
	 */
	public int size() { return size; }
	
	/**
	 * The number of structural modifications to the tree.
	 * redundant, needn't be serialized 
	 */
	transient int modCount = 0;
	
	protected void incrementSize()   { ++modCount; ++size; }
	protected void decrementSize()   { ++modCount; --size; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new, empty map, sorted according to the keys' natural
	 * order.  All keys inserted into the map must implement the
	 * <tt>Comparable</tt> interface.  Furthermore, all such keys must be
	 * <i>mutually comparable</i>: <tt>k1.compareTo(k2)</tt> must not throw a
	 * ClassCastException for any elements <tt>k1</tt> and <tt>k2</tt> in the
	 * map.  If the user attempts to put a key into the map that violates this
	 * constraint (for example, the user attempts to put a string key into a
	 * map whose keys are integers), the <tt>put(Object key, Object
	 * value)</tt> call will throw a <tt>ClassCastException</tt>.
	 *
	 * @see Comparable
	 */
	public TreeMap() {}

	/**
	 * Constructs a new, empty map, sorted according to the given comparator.
	 * All keys inserted into the map must be <i>mutually comparable</i> by
	 * the given comparator: <tt>comparator.compare(k1, k2)</tt> must not
	 * throw a <tt>ClassCastException</tt> for any keys <tt>k1</tt> and
	 * <tt>k2</tt> in the map.  If the user attempts to put a key into the
	 * map that violates this constraint, the <tt>put(Object key, Object
	 * value)</tt> call will throw a <tt>ClassCastException</tt>.
	 *
	 * @param c the comparator that will be used to sort this map.  A
	 *		<tt>null</tt> value indicates that the keys' <i>natural
	 *		ordering</i> should be used.
	 */
	public TreeMap(final Comparator c) {
		this.comparator = c;
	}

	/**
	 * Constructs a new map containing the same mappings as the given map,
	 * sorted according to the keys' <i>natural order</i>.  All keys inserted
	 * into the new map must implement the <tt>Comparable</tt> interface.
	 * Furthermore, all such keys must be <i>mutually comparable</i>:
	 * <tt>k1.compareTo(k2)</tt> must not throw a <tt>ClassCastException</tt>
	 * for any elements <tt>k1</tt> and <tt>k2</tt> in the map.  This method
	 * runs in n*log(n) time.
	 *
	 * @param  m the map whose mappings are to be placed in this map.
	 * @throws ClassCastException the keys in t are not Comparable, or
	 *		 are not mutually comparable.
	 * @throws NullPointerException if the specified map is null.
	 */
	public TreeMap(final Map m) {
		putAll(m);
	}

	/**
	 * Constructs a new map containing the same mappings as the given
	 * <tt>SortedMap</tt>, sorted according to the same ordering.  This method
	 * runs in linear time.
	 *
	 * @param  m the sorted map whose mappings are to be placed in this map,
	 *		 and whose comparator is to be used to sort this map.
	 * @throws NullPointerException if the specified sorted map is null.
	 */
	public TreeMap(final SortedMap m) {
		comparator = m.comparator();
		try {
			buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
		} catch (java.io.IOException cannotHappen) {
		} catch (ClassNotFoundException cannotHappen) {
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// Query Operations
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 *
	 * @param key key whose presence in this map is to be tested.
	 * 
	 * @return <tt>true</tt> if this map contains a mapping for the
	 *			specified key.
	 * @throws ClassCastException if the key cannot be compared with the keys
	 *				  currently in the map.
	 * @throws NullPointerException key is <tt>null</tt> and this map uses
	 *				  natural ordering, or its comparator does not tolerate
	 *			<tt>null</tt> keys.
	 */
	public boolean containsKey(final Object key) { 
		return getEntry(key) != null;
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the
	 * specified value.  More formally, returns <tt>true</tt> if and only if
	 * this map contains at least one mapping to a value <tt>v</tt> such
	 * that <tt>(value==null ? v==null : value.equals(v))</tt>.  This
	 * operation will probably require time linear in the Map size for most
	 * implementations of Map.
	 *
	 * @param value value whose presence in this Map is to be tested.
	 * @return  <tt>true</tt> if a mapping to <tt>value</tt> exists;
	 *		<tt>false</tt> otherwise.
	 * @since 1.2
	 */
	public boolean containsValue(final Object value) {
		return (root == null ? false : (null != root.valueSearch(value)));
	}
	
	/**
	 * Returns the value to which this map maps the specified key.  Returns
	 * <tt>null</tt> if the map contains no mapping for this key.  A return
	 * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
	 * map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
	 * operation may be used to distinguish these two cases.
	 *
	 * @param key key whose associated value is to be returned.
	 * @return the value to which this map maps the specified key, or
	 *			   <tt>null</tt> if the map contains no mapping for the key.
	 * @throws	ClassCastException key cannot be compared with the keys
	 *				  currently in the map.
	 * @throws NullPointerException key is <tt>null</tt> and this map uses
	 *				  natural ordering, or its comparator does not tolerate
	 *				  <tt>null</tt> keys.
	 * 
	 * @see #containsKey(Object)
	 */
	public Object get(final Object key) {
		final TreeMapEntry p = getEntry(key);
		return (p==null ? null : p.val);
	}

	/**
	 * Returns the value to which this map maps the specified key.  Returns
	 * <tt>null</tt> if the map contains no mapping for this key.  A return
	 * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
	 * map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
	 * operation may be used to distinguish these two cases.
	 *
	 * @param key key whose associated value is to be returned.
	 * @return the value to which this map maps the specified key, or
	 *			   <tt>null</tt> if the map contains no mapping for the key.
	 * @throws	ClassCastException key cannot be compared with the keys
	 *				  currently in the map.
	 * @throws NullPointerException key is <tt>null</tt> and this map uses
	 *				  natural ordering, or its comparator does not tolerate
	 *				  <tt>null</tt> keys.
	 * 
	 * @see #containsKey(Object)
	 */
	public int getNdx(final Object key) {
		final TreeMapEntry p = getEntry(key);
		return (p==null ? Integer.MIN_VALUE : p.ndx);
	}
	
	/**
	 * Returns this map's entry for the given key, or <tt>null</tt> if the map
	 * does not contain an entry for the key.
	 *
	 * @return this map's entry for the given key, or <tt>null</tt> if the map
	 *				does not contain an entry for the key.
	 * @throws ClassCastException if the key cannot be compared with the keys
	 *				  currently in the map.
	 * @throws NullPointerException key is <tt>null</tt> and this map uses
	 *				  natural order, or its comparator does not tolerate *
	 *				  <tt>null</tt> keys.
	 */
	TreeMapEntry getEntry(final Object key) {
		for (TreeMapEntry p = root; p != null;) {
			final int cmp = compare(key, p.key);
			if (cmp < 0)
				p = p.prev;
			else if (cmp > 0)
				p = p.next;
			else //if (cmp == 0)
				return p;
		}
		return null;
	}

	/**
	 * Gets the entry corresponding to the specified key; 
	 * if no such entry exists, 
	 * returns the entry for the least key greater than the specified key; 
	 * if no such entry exists (all smaller), returns <tt>null</tt>
	 * (i.e., the greatest key in the Tree is less than the specified key).
	 */
	TreeMapEntry getCeilEntry(final Object key) {
		TreeMapEntry p = root;
		if (p == null)
			return null;
		for(;;) {
			final int cmp = compare(key, p.key);
			if (cmp < 0) {
				if (p.prev != null)
					p = p.prev;
				else
					return p;
			} else if (cmp > 0) { //return the direct Successor
				if (p.next != null) {
					p = p.next;
				} else 
					return p.nextParent();
			} else //if (cmp == 0) { //equal
				return p;
		}
	}
	
	/**
	 * Returns the entry for the greatest key less than the specified key; 
	 * if no such entry exists (all larger), returns <tt>null</tt>.
	 * (i.e., the least key in the Tree is greater than the specified key)
	 */
	TreeMapEntry getPrecedingEntry(final Object key) {
		TreeMapEntry p = root;
		if (p==null)
			return null;
		while (true) {
			final int cmp = compare(key, p.key);
			if (cmp > 0) {
				if (p.next != null)
					p = p.next;
				else
					return p;
			} else {
				if (p.prev != null) {
					p = p.prev;
				} else 
					return p.prevParent();
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////
	/// find TreeMapEntries
	///////////////////////////////////////////////////////////////////////////
	
	final static public double DEFAULT_WEIGHT = 1; 
	
	/**
	 * TODO: rename to setAt
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for this key, the old
	 * value is replaced.
	 *
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * 
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *		 if there was no mapping for key.  A <tt>null</tt> return can
	 *		 also indicate that the map previously associated <tt>null</tt>
	 *		 with the specified key.
	 * @throws	ClassCastException key cannot be compared with the keys
	 *			currently in the map.
	 * @throws NullPointerException key is <tt>null</tt> and this map uses
	 *		 natural order, or its comparator does not tolerate
	 *		 <tt>null</tt> keys.
	 */
	public Object put(final Object key, final Object value) {
		return put(key, value, modCount, DEFAULT_WEIGHT); //modCount is monotonously ascending
	}
	
	/**
	 * TODO: rename to setAt
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for this key, the old
	 * value is replaced.
	 *
	 * @param key key with which the specified value is to be associated.
	 * @param _Value value to be associated with the specified key.
	 * 
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *		 if there was no mapping for key.  A <tt>null</tt> return can
	 *		 also indicate that the map previously associated <tt>null</tt>
	 *		 with the specified key.
	 * @throws	ClassCastException key cannot be compared with the keys
	 *			currently in the map.
	 * @throws NullPointerException key is <tt>null</tt> and this map uses
	 *		 natural order, or its comparator does not tolerate
	 *		 <tt>null</tt> keys.
	 */
	public Object put(final Object key, final int index) {
		return put(key, null, index, DEFAULT_WEIGHT); //
	}
	
	/**
	 * TODO: rename to setAt
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for this key, the old
	 * value is replaced.
	 *
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * 
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *		 if there was no mapping for key.  A <tt>null</tt> return can
	 *		 also indicate that the map previously associated <tt>null</tt>
	 *		 with the specified key.
	 * @throws	ClassCastException key cannot be compared with the keys
	 *			currently in the map.
	 * @throws NullPointerException key is <tt>null</tt> and this map uses
	 *		 natural order, or its comparator does not tolerate
	 *		 <tt>null</tt> keys.
	 */
	public Object put(final Object key, final Object value, final int index, final double weight) {
		TreeMapEntry t = root;
		
		if (t == null) { //inserted very first Node
			incrementSize();
			root = new TreeMapEntry(key, value, index, weight, null);
			return null; //no previous Mapping
		}
		
		for(;;) { //traverse down the Tree
			final int cmp = compare(key, t.key);
			if (cmp < 0) { //turn left or insert
				if (t.prev != null) {
					t = t.prev;
				} else {
					fixAfterInsertion(t.prev = new TreeMapEntry(key, value, index, weight, t));
					return null; //no previous Mapping
				}
			} else if (cmp > 0) { //turn right or insert
				if (t.next != null) {
					t = t.next;
				} else {
					fixAfterInsertion(t.next = new TreeMapEntry(key, value, index, weight, t));
					return null;
				}
			} else {//if (cmp == 0) { //found same Mapping
				final Object ret = t.getVal(); t.setVal(value);
				return ret; 
			}
		}
	}

	/**
	 * Removes the mapping for this key from this TreeMap if present.
	 *
	 * @param  key key for which mapping should be removed
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *		 if there was no mapping for key.  A <tt>null</tt> return can
	 *		 also indicate that the map previously associated
	 *		 <tt>null</tt> with the specified key.
	 * 
	 * @throws	ClassCastException key cannot be compared with the keys
	 *			currently in the map.
	 * @throws NullPointerException key is <tt>null</tt> and this map uses
	 *		 natural order, or its comparator does not tolerate
	 *		 <tt>null</tt> keys.
	 */
	public Object remove(final Object key) {
		final TreeMapEntry p = getEntry(key);
		if (p == null)
			return null; //not found
		
		final Object oldValue = p.val;
		deleteEntry(p);
		return oldValue;
	}

	/**
	 * Removes all mappings from this TreeMap.
	 */
	public void clear() {
		++modCount;
		size = 0;
		root = null;
	}
	
	///////////////////////////////////////////////////////////////////////////
	///
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a shallow copy of this <tt>TreeMap</tt> instance. (The keys and
	 * values themselves are not cloned.)
	 *
	 * @return a shallow copy of this Map.
	 */
	public Object clone() {
		TreeMap clone = null;
		try { 
			clone = (TreeMap)super.clone();
		} catch (CloneNotSupportedException e) { 
			throw new InternalError();
		}

		// Put clone into "virgin" state (except for comparator)
		clone.root = null;
		clone.size = 0;
		clone.modCount = 0;
		clone.entrySet = null;

		// Initialize clone with our mappings
		try {
			clone.buildFromSorted(size, entrySet().iterator(), null, null);
		} catch (final java.io.IOException cannotHappen) {
		} catch (final ClassNotFoundException cannotHappen) {
		}

		return clone;
	}


	// Views

	/**
	 * Each of these fields are initialized to contain an instance of the
	 * appropriate view the first time this view is requested.  The views are
	 * stateless, so there's no reason to create more than one of each.
	 */
	transient volatile Set		keySet = null;
	
	/**
	 * Returns a Set view of the keys contained in this map.  The set's
	 * iterator will return the keys in ascending order.  The map is backed by
	 * this <tt>TreeMap</tt> instance, so changes to this map are reflected in
	 * the Set, and vice-versa.  The Set supports element removal, which
	 * removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt>, and <tt>clear</tt> operations.  It does not support
	 * the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the keys contained in this TreeMap.
	 */
	public Set keySet() {
		if (keySet == null) 
			keySet =  new TreeKeySet(this);
		return keySet;
	}

	/**
	 * Each of these fields are initialized to contain an instance of the
	 * appropriate view the first time this view is requested.  The views are
	 * stateless, so there's no reason to create more than one of each.
	 */
	transient volatile Collection values = null;

	/**
	 * Returns a collection view of the values contained in this map.  The
	 * collection's iterator will return the values in the order that their
	 * corresponding keys appear in the tree.  The collection is backed by
	 * this <tt>TreeMap</tt> instance, so changes to this map are reflected in
	 * the collection, and vice-versa.  The collection supports element
	 * removal, which removes the corresponding mapping from the map through
	 * the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
	 * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a collection view of the values contained in this map.
	 */
	public Collection values() {
		if (values == null) 
			values =  new TreeValueCollection(this);
		return values;
	}

	/**
	 * This field is initialized to contain an instance of the entry set
	 * view the first time this view is requested.  The view is stateless,
	 * so there's no reason to create more than one.
	 */
	private transient volatile Set entrySet = null;

	/**
	 * Returns a set view of the mappings contained in this map.  The set's
	 * iterator returns the mappings in ascending key order.  Each element in
	 * the returned set is a <tt>Map.Entry</tt>.  The set is backed by this
	 * map, so changes to this map are reflected in the set, and vice-versa.
	 * The set supports element removal, which removes the corresponding
	 * mapping from the TreeMap, through the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
	 * <tt>clear</tt> operations.  It does not support the <tt>add</tt> or
	 * <tt>addAll</tt> operations.
	 *
	 * @return a set view of the mappings contained in this map.
	 * @see Map.Entry
	 */
	public Set entrySet() {
		if (entrySet == null) 
			entrySet = new TreeEntrySet(this);
		return entrySet;
	}
	
	/**
	 * Returns a view of the portion of this map whose keys range from
	 * <tt>fromKey</tt>, inclusive, to <tt>toKey</tt>, exclusive.  
	 * (If <tt>fromKey</tt> and <tt>toKey</tt> are equal, 
	 * the returned sorted map is empty.)  
	 * The returned sorted map is backed by this map, so changes
	 * in the returned sorted map are reflected in this map, and vice-versa.
	 * The returned sorted map supports all optional map operations.<p>
	 * 
	 * The sorted map returned by this method will throw an <tt>IllegalArgumentException</tt> 
	 * if the user attempts to insert a key less than <tt>fromKey</tt> 
	 * or greater than or equal to <tt>toKey</tt>.<p>
	 *
	 * Note: this method always returns a <i>half-open range</i> 
	 * (which includes its low endpoint but not its high endpoint).  
	 * If you need a <i>closed range</i> (which includes both endpoints), 
	 * and the key type allows for calculation of the successor a given key, 
	 * merely request the subrange from <tt>lowEndpoint</tt> to <tt>successor(highEndpoint)</tt>.
	 * 
	 * For example, suppose that <tt>m</tt> is a sorted map whose keys are
	 * strings.  The following idiom obtains a view containing all of the
	 * key-value mappings in <tt>m</tt> whose keys are between <tt>low</tt>
	 * and <tt>high</tt>, inclusive:
	 *			 <pre>	SortedMap sub = m.submap(low, high+"\0");</pre>
	 * A similar technique can be used to generate an <i>open range</i> (which
	 * contains neither endpoint).  The following idiom obtains a view
	 * containing all of the key-value mappings in <tt>m</tt> whose keys are
	 * between <tt>low</tt> and <tt>high</tt>, exclusive:
	 *			 <pre>	SortedMap sub = m.subMap(low+"\0", high);</pre>
	 *
	 * @param fromKey low endpoint (inclusive) of the subMap.
	 * @param toKey high endpoint (exclusive) of the subMap.
	 * 
	 * @return a view of the portion of this map whose keys range from
	 *				<tt>fromKey</tt>, inclusive, to <tt>toKey</tt>, exclusive.
	 * 
	 * @throws ClassCastException if <tt>fromKey</tt> and <tt>toKey</tt>
	 *		 cannot be compared to one another using this map's comparator
	 *		 (or, if the map has no comparator, using natural ordering).
	 * @throws IllegalArgumentException if <tt>fromKey</tt> is greater than
	 *		 <tt>toKey</tt>.
	 * @throws NullPointerException if <tt>fromKey</tt> or <tt>toKey</tt> is
	 *			   <tt>null</tt> and this map uses natural order, or its
	 *			   comparator does not tolerate <tt>null</tt> keys.
	 */
	public SortedMap subMap(final Object fromKey, final Object toKey) {
		return new SubTreeMap(this, fromKey, toKey);
	}
	
	/**
	 * Returns a view of the portion of this map 
	 * whose keys are strictly less than <tt>toKey</tt>.  
	 * The returned sorted map is backed by this map, 
	 * so changes in the returned sorted map are reflected in this map, and vice-versa.  
	 * The returned sorted map supports all optional map operations.<p>
	 *
	 * The sorted map returned by this method will throw an
	 * <tt>IllegalArgumentException</tt> if the user attempts to insert a key
	 * greater than or equal to <tt>toKey</tt>.<p>
	 *
	 * Note: this method always returns a view that does not contain its
	 * (high) endpoint.  If you need a view that does contain this endpoint,
	 * and the key type allows for calculation of the successor a given key,
	 * merely request a headMap bounded by <tt>successor(highEndpoint)</tt>.
	 * For example, suppose that suppose that <tt>m</tt> is a sorted map whose
	 * keys are strings.  The following idiom obtains a view containing all of
	 * the key-value mappings in <tt>m</tt> whose keys are less than or equal
	 * to <tt>high</tt>:
	 * <pre>
	 *	 SortedMap head = m.headMap(high+"\0");
	 * </pre>
	 *
	 * @param toKey high endpoint (exclusive) of the headMap.
	 * @return a view of the portion of this map whose keys are strictly
	 *				less than <tt>toKey</tt>.
	 *
	 * @throws ClassCastException if <tt>toKey</tt> is not compatible
	 *		 with this map's comparator (or, if the map has no comparator,
	 *		 if <tt>toKey</tt> does not implement <tt>Comparable</tt>).
	 * @throws IllegalArgumentException if this map is itself a subMap,
	 *		 headMap, or tailMap, and <tt>toKey</tt> is not within the
	 *		 specified range of the subMap, headMap, or tailMap.
	 * @throws NullPointerException if <tt>toKey</tt> is <tt>null</tt> and
	 *			   this map uses natural order, or its comparator does not
	 *			   tolerate <tt>null</tt> keys.
	 */
	public SortedMap headMap(final Object toKey) {
		return new SubTreeMap(this, toKey, true);
	}
	
	/**
	 * Returns a view of the portion of this map 
	 * whose keys are greater than or equal to <tt>fromKey</tt>.  
	 * The returned sorted map is backed by this map, 
	 * so changes in the returned sorted map are reflected in this map, and vice-versa.  
	 * The returned sorted map supports all optional map operations.<p>
	 *
	 * The sorted map returned by this method will throw an
	 * <tt>IllegalArgumentException</tt> if the user attempts to insert a key
	 * less than <tt>fromKey</tt>.<p>
	 *
	 * Note: this method always returns a view that contains its (low)
	 * endpoint.  If you need a view that does not contain this endpoint, and
	 * the element type allows for calculation of the successor a given value,
	 * merely request a tailMap bounded by <tt>successor(lowEndpoint)</tt>.
	 * For For example, suppose that suppose that <tt>m</tt> is a sorted map
	 * whose keys are strings.  The following idiom obtains a view containing
	 * all of the key-value mappings in <tt>m</tt> whose keys are strictly
	 * greater than <tt>low</tt>: <pre>
	 *	 SortedMap tail = m.tailMap(low+"\0");
	 * </pre>
	 *
	 * @param fromKey low endpoint (inclusive) of the tailMap.
	 * @return a view of the portion of this map whose keys are greater
	 *				than or equal to <tt>fromKey</tt>.
	 * @throws ClassCastException if <tt>fromKey</tt> is not compatible
	 *		 with this map's comparator (or, if the map has no comparator,
	 *		 if <tt>fromKey</tt> does not implement <tt>Comparable</tt>).
	 * @throws IllegalArgumentException if this map is itself a subMap,
	 *		 headMap, or tailMap, and <tt>fromKey</tt> is not within the
	 *		 specified range of the subMap, headMap, or tailMap.
	 * @throws NullPointerException if <tt>fromKey</tt> is <tt>null</tt> and
	 *			   this map uses natural order, or its comparator does not
	 *			   tolerate <tt>null</tt> keys.
	 */
	public SortedMap tailMap(final Object fromKey) {
		return new SubTreeMap(this, fromKey, false);
	}

	/**
	 * Returns the first (lowest) key currently in this sorted map.
	 *
	 * @return the first (lowest) key currently in this sorted map.
	 * @throws	NoSuchElementException Map is empty.
	 */
	public Object firstKey() { return firstEntry().key; }

	/**
	 * Returns the first Entry in the TreeMap 
	 * (according to the TreeMap's key-sort function).  
	 * @return null if the TreeMap is empty.
	 */
	public TreeMapEntry firstEntry() { return TreeMapEntry.FIRST_OF(root); }

	/**
	 * Returns the last (highest) key currently in this sorted map.
	 *
	 * @return the last (highest) key currently in this sorted map.
	 * @throws	NoSuchElementException Map is empty.
	 */
	public Object lastKey() { return lastEntry().key; }
	
	/**
	 * Returns the last Entry in the TreeMap 
	 * (according to the TreeMap's key-sort function).  
	 * @return null if the TreeMap is empty.
	 */
	TreeMapEntry lastEntry() { return TreeMapEntry.LAST_OF(root); }

	/** From CLR **/
	private void fixAfterInsertion(final TreeMapEntry x) {
		incrementSize();
		root = x.balanceAfterInsertion(root);
	}

	/**
	 * Delete node p, and then rebalance the tree.
	 */
	void deleteEntry(final TreeMapEntry p) {
		decrementSize();
		root = p.deleteEntry(root); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Save the state of the <tt>TreeMap</tt> instance to a stream (i.e.,
	 * serialize it).
	 *
	 * @serialData The <i>size</i> of the TreeMap (the number of key-value
	 *			 mappings) is emitted (int), followed by the key (Object)
	 *			 and value (Object) for each key-value mapping represented
	 *			 by the TreeMap. The key-value mappings are emitted in
	 *			 key-order (as determined by the TreeMap's Comparator,
	 *			 or by the keys' natural ordering if the TreeMap has no
	 *			 Comparator).
	 */
	private void writeObject(final java.io.ObjectOutputStream s)
		throws java.io.IOException {
		// Write out the Comparator and any hidden stuff
		s.defaultWriteObject();

		// Write out size (number of Mappings)
		s.writeInt(size);

		// Write out keys and values (alternating)
		for (final Iterator i = entrySet().iterator(); i.hasNext(); ) {
			TreeMapEntry e = (TreeMapEntry)i.next();
			s.writeObject(e.key);
			s.writeObject(e.val);
		}
	}

	/**
	 * Reconstitute the <tt>TreeMap</tt> instance from a stream (i.e.,
	 * deserialize it).
	 */
	private void readObject(final java.io.ObjectInputStream s)
		throws java.io.IOException, ClassNotFoundException {
		// Read in the Comparator and any hidden stuff
		s.defaultReadObject();

		// Read in size
		int size = s.readInt();

		buildFromSorted(size, null, s, null);
	}

	/** Intended to be called only from TreeSet.readObject **/
	void readTreeSet(final int size, final java.io.ObjectInputStream s, final Object defaultVal)
		throws java.io.IOException, ClassNotFoundException {
		buildFromSorted(size, null, s, defaultVal);
	}

	/**
	 * Copies all of the mappings from the specified map to this map.  These
	 * mappings replace any mappings that this map had for any of the keys
	 * currently in the specified map.
	 *
	 * @param	 map mappings to be stored in this map.
	 * @throws	ClassCastException class of a key or value in the specified
	 *				   map prevents it from being stored in this map.
	 * 
	 * @throws NullPointerException if the given map is <tt>null</tt> or
	 *		 this map does not permit <tt>null</tt> keys and a 
	 *		 key in the specified map is <tt>null</tt>.
	 */
	public void putAll(final Map map) {
		int mapSize = map.size();
		if (size==0 && mapSize!=0 && map instanceof SortedMap) {
			Comparator c = ((SortedMap)map).comparator();
			if (c == comparator || (c != null && c.equals(comparator))) {
			  ++modCount;
			  try {
				  buildFromSorted(mapSize, map.entrySet().iterator(), null, null);
			  } catch (java.io.IOException cannotHappen) {
			  } catch (ClassNotFoundException cannotHappen) {
			  }
			  return;
			}
		}
		super.putAll(map);
	}
	
	/** Intended to be called only from TreeSet.addAll **/
	void addAllForTreeSet(final SortedSet set, final Object defaultVal) {
	  try {
		  buildFromSorted(set.size(), set.iterator(), null, defaultVal);
	  } catch (java.io.IOException cannotHappen) {
	  } catch (ClassNotFoundException cannotHappen) {
	  }
	}


	/**
	 * Linear (!) time tree building algorithm from sorted (!) data.  
	 * Can accept keys and/or values from iterator or stream. 
	 * This leads to too many parameters, but seems better than alternatives.  
	 * The four formats that this method accepts are:
	 *
	 *	1) An iterator of Map.Entries.  (it != null, defaultVal == null).
	 *	2) An iterator of keys.		    (it != null, defaultVal != null).
	 *	3) A stream of alternating serialized keys and values.
	 *								    (it == null, defaultVal == null).
	 *	4) A stream of serialized keys. (it == null, defaultVal != null).
	 *
	 * It is assumed that the comparator of the TreeMap is already set prior
	 * to calling this method.
	 *
	 * @param size the number of keys (or key-value pairs) to be read from
	 *		the iterator or stream. 
	 * @param it If non-null, new entries are created from entries
	 *		or keys read from this iterator.
	 * @param it If non-null, new entries are created from keys and
	 *		possibly values read from this stream in serialized form.
	 *		Exactly one of it and str should be non-null.
	 * @param defaultVal if non-null, this default value is used for
	 *		each value in the map.  If null, each value is read from
	 *		iterator or stream, as described above.
	 * @throws IOException propagated from stream reads. This cannot
	 *		 occur if str is null.
	 * @throws ClassNotFoundException propagated from readObject. 
	 *		 This cannot occur if str is null.
	 */
	private void buildFromSorted(int size, Iterator it,
								  java.io.ObjectInputStream str,
								  Object defaultVal)
		throws  java.io.IOException, ClassNotFoundException {
		this.size = size;
		root = buildFromSorted(0, 0, size-1, COMPUTE_RED_LEVEL(size),
							   it, str, defaultVal);
	}

	/**
	 * Recursive "helper method" that actually reads a sorted Stream
	 * consisting of Key-Value Sequences. 
	 * Identically named parameters have identical definitions.  
	 * Additional parameters are documented below. 
	 * It is assumed that the comparator and size fields of the TreeMap are
	 * already set prior to calling this method.  (It ignores both fields.)
	 *
	 * @param level the current level of tree. Initial call should be 0.
	 * @param lo the first element index of this subtree. Initial should be 0.
	 * @param hi the last element index of this subtree.  Initial should be
	 *			  size-1.
	 * @param redLevel the level at which nodes should be red. 
	 *		Must be equal to computeRedLevel for tree of this size.
	 */
	private static final TreeMapEntry buildFromSorted(
			final int level, final int lo, final int hi, final int redLevel, 
			final Iterator it, final java.io.ObjectInputStream str, 
			final Object defaultVal) 
		throws  java.io.IOException, ClassNotFoundException //from readObject 
	{
		/*
		 * Strategy: The root is the middlemost element. 
		 * To get to it, we have to first recursively construct 
		 * the entire left subtree, so as to grab all of its elements. 
		 * We can then proceed with right subtree. 
		 *
		 * The lo and hi arguments are the minimum and maximum indices 
		 * to pull out of the iterator or stream for current subtree.
		 * They are not actually indexed, we just proceed sequentially,
		 * ensuring that items are extracted in corresponding order.
		 */

		if (hi < lo) 
			return null;

		final int mid = (lo + hi) / 2;
		
		//InOrder Traversal Algorithm
		TreeMapEntry left  = null;
		if (lo < mid) //build left Subtree
			left = buildFromSorted(level+1, lo, mid - 1, redLevel,
								   it, str, defaultVal);
		
		// extract key and/or value from iterator or stream
		final Object key;
		final Object value;
		double weight = DEFAULT_WEIGHT; 
		int index = mid; 
		if (it != null) { // use iterator
			if (defaultVal==null) {
				final Object nxt = it.next();
				if (nxt instanceof TreeMapEntry) {
					TreeMapEntry tme = (TreeMapEntry) nxt; 
					key = tme.key;
					value = tme.val;
					index = tme.ndx;
					weight = tme.weight;
				} else {
					final Map.Entry entry = (Map.Entry) nxt; 
					key = entry.getKey();
					value = entry.getValue();
				}
			} else { 
				key = it.next();
				value = defaultVal;
			}
		} else { //assume stream of Key-Value Pairs
			key = str.readObject();
			value = (defaultVal != null ? defaultVal : str.readObject());
		}

		final TreeMapEntry middle =  new TreeMapEntry(key, value, index, weight, null);
		
		// color nodes in non-full bottommost level red
		if (level == redLevel)
			middle.isRed = true;
		
		if (left != null) 
			middle.setPrev(left);  
		
		if (mid < hi) { //build right Subtree
			TreeMapEntry right = buildFromSorted(level+1, mid+1, hi, redLevel,
										  it, str, defaultVal);
			middle.setNext(right); 
		}
		
		return middle;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// static Testing and main Methods
	///////////////////////////////////////////////////////////////////////////
	
	public static void testIt() {
		final String[] args = Indexer.TEST_STRINGS; 
		main(args);
		testTreeMap(); 
		final TreeMap index = new TreeMap(OrderatorComparable.Orderator); 
		for (int i = args.length; --i >= 0; ) { 
			index.put(args[i], i); //sort Strings into Tree
		}
		testOrdered(index); 
		for (int i = args.length; --i >= 0; ) {
			index.remove(args[i]); //remove Strings from the Tree
			testOrdered(index); 
			index.put(args[i], i); //add Strings to the Tree
			testOrdered(index); 
		}
	}
	
	/**
	 * outputs the Indices of the sorted Strings to System.out separated by Tabs. 
	 * @param args a List of Strings to index
	 */
	public static void testOrdered(final TreeMap index) {
		final Iterator iter = index.keySet().iterator(); 
		Object next=iter.next(); 
		for (; iter.hasNext(); ) {
			final Object last = next; next = iter.next(); 
			Assert.IS_TRUE(index.comparator().compare(next, last) >= 0); 
		}
	}
	
	public static void testTreeMap() {
		final String[] args = Indexer.TEST_STRINGS; 
		final java.util.TreeMap index = new java.util.TreeMap(OrderatorComparable.Orderator); 
		for (int i = args.length; --i >= 0; ) { 
			index.put(args[i], new Integer(i)); //sort Strings into Tree
		}
		testOrdered(index); 
		for (int i = args.length; --i >= 0; ) {
			index.remove(args[i]); //remove Strings from the Tree
			testOrdered(index); 
			index.put(args[i], new Integer(i)); //add Strings to the Tree
			testOrdered(index); 
		}
	}
	
	/**
	 * outputs the Indices of the sorted Strings to System.out separated by Tabs. 
	 * @param args a List of Strings to index
	 */
	public static void testOrdered(final java.util.TreeMap index) {
		final Iterator iter = index.keySet().iterator(); 
		Object next=iter.next(); 
		for (; iter.hasNext(); ) {
			final Object last = next; next = iter.next(); 
			Assert.IS_TRUE(index.comparator().compare(next, last) >= 0); 
		}
	}
	
	/**
	 * outputs the Indices of the sorted Strings to System.out separated by Tabs. 
	 * @param args a List of Strings to index
	 */
	public static void main(final String[] args) {
		if (args.length == 0)
			testIt(); 
		else {
			final TreeMap index = new TreeMap(OrderatorComparable.Orderator); 
			for (int i = args.length; --i >= 0; ) 
				index.put(args[i], i); //sort Strings into Tree
			// Write out keys and values (alternating)
			final int[] positions = new int[args.length]; 
			for (int i = -1; ++i < args.length; ) {
				final int ndx = index.getNdx(args[i]);
				if (args == Indexer.TEST_STRINGS)
					Assert.EQUALS(ndx, i);
				else {
					System.out.print(ndx);
					System.out.print('\t'); 
				}
				positions[i] = ndx; 
			}
			/*
			 */
		}
	}
	
}
