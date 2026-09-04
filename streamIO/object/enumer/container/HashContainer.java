package streamIO.object.enumer.container;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;

import math.vector.VectorObject;
import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.copy.boole.Boole;
import streamIO.copy.group.ISemiGroup;
import streamIO.exception.OperationNotSupported;
import streamIO.integer.jdbc.ResultSetArray;
import streamIO.object.IStreamIn;
import streamIO.object.StringStreamIn;
import streamIO.object.parser.jdbc.ResultSetSep;
import synch.ValidationRule;
import tester.EquivalenceIdentity;
import tester.IEquivalence;
import function.derive.Enum;
import graphic.math2D.Map2DModel;
import graphic.math2D.Map2DPainter;
import graphic.mvc.BaseApplet;
import graphs.ICopy;
import graphs.IEdgeStreamIn;
import graphs.IGraph;
import graphs.SparseGraph;

/**
 * Enum Class to switch HashContainer Operation Modes this is better than using different
 * Methods to add items to the HashContainer. The Functionality of a Bag is already
 * present in HashContainer and selected by the OperationMode 'BAG_SET_FUNCTION'. A Bag
 * (MultiSet or MonoGraph) can contain any number of Items, also duplicates and null
 * Values. As an Optimization it doesn't store Duplicates, but counts their Occurrence!
 * This reduces the LoadFactor of the HashTable used and avoids Distortions and long
 * Searches due to repeating Elements. This Bag cannot contain null Values. Bags can be
 * used to collect Statistics about a Stream, like the Frequency of Elements, not taking
 * their Sequence into Consideration.
 * @see streamIO.integer.filter.stats.FilterByteBag which implements a Bag for counting
 *      the Bytes in a Stream.
 * @see streamIO.integer.filter.stats.FilterDiGraphCounter
 * @see streamIO.integer.filter.stats.FilterTriGraphCounter
 * @see streamIO.object.enumer.container.DebitContainer Like DebitContainer, this Bag
 *      Class is capable of storing Debits for Objects as well as Credits (if they cannot
 *      be stored in the Object itself!). Unlike DebitContainer it doesn't keep the
 *      Balances separate. It does NOT store Fractions of Objects, since Objects are
 *      considered integer. It can also be used to model ternary or n-ary Relations which
 *      are needed in Knowledge Management, where Assertions can be TRUE, FALSE or
 *      UNKNOWN. Design Decisions: This Bag is implemented with a HashContainer of
 *      (Object,Counter) Associations for Performance Reasons. It could be implemented
 *      with any Container Type, but that would require delegating or reimplementing all
 *      Methods of Container. Since a HashTable can find it's mapped Objects very quickly,
 *      this Map is used to count the number of items and thus save Space (e.g. count
 *      occurences). Uses an Association with a ByRefInt to save the Item Count. It is
 *      better to use a dedicated Class 'Bag' with an Object Reference and an int Member.
 *      An Alternative Design stores the Counters in a (dynamic) Array and only stores the
 *      Index into this Array in the Bag. Thus only a HashMap with automatic Counter is
 *      needed and the Items maintained are stored in a dynamic Array, whose indices are
 *      dynamically created and thus unpredictable and unusable for arithmetic Operations.
 *      A more structured Alternative is to use an Array with fixed Meanings/Dimensions
 *      for it's Indices. With this Vector regular Arithmetics can be performed! This
 *      Container is not typed. Typically you would like to have a compile Time (i.e. add)
 *      typed or a Runtime typed (highest common Class or Interface) Container
 * @author heuerm
 */
final class HashOperationMode extends Enum {

	// //////////////////////////////////////////////////////////////////////////////
	// / #region : static Constants and Variables
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Constant denoting Operation as a simple Collection resp. Relation Supports fastest
	 * insert.
	 */
	final static public String STR_COLLECTION_RELATION = "COLLECTION_RELATION";

	/**
	 * Constant denoting Operation as a Set resp. Function with additional Bag Operations
	 * of aggregating the Weights and replacing the Value for repeated Keys/Duplicates
	 */
	final static public String STR_BAG_SET_FUNCTION = "BAG_SET_FUNCTION";

	/**
	 * Constant denoting Operation as a Collection, but assigns unique Indices to repeated
	 * Keys/Duplicates.
	 */
	final static public String STR_INDEX_COLLECTION = "INDEX_COLLECTION";

	// /////////////////////////////////////////////////////////////////////////

	/** Constant denoting FALSE */
	final static public byte BYTE_COLLECTION_RELATION = 0;

	/** Constant denoting NULL or Unknown */
	final static public byte BYTE_BAG_SET_FUNCTION = 1;

	/** Constant denoting TRUE */
	final static public byte BYTE_INDEX_COLLECTION = 2;

	// /////////////////////////////////////////////////////////////////////////

	/** List of Names for the Enums */
	protected static final String[] NAMES = {STR_COLLECTION_RELATION,
			STR_BAG_SET_FUNCTION, STR_INDEX_COLLECTION};

	/** List of Names for the Enums */
	// protected static final byte[] VALUES = { BYTE_COLLECTION_RELATION,
	// BYTE_BAG_SET_FUNCTION, BYTE_INDEX_COLLECTION};
	/** Constant denoting TRUE */
	private static final Enum[] LIST = CREATE_LIST(NAMES, 0, new HashOperationMode());

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * HashContainer simply collects Items and Mappings, also accepts duplicate Keys this
	 * has the fastest insert Operation
	 */
	final static public HashOperationMode COLLECTION_RELATION = (HashOperationMode) LIST[0];

	/**
	 * HashContainer sums up the Weights and replaces the Value for repeated
	 * Keys/Duplicates
	 */
	final static public HashOperationMode BAG_SET_FUNCTION = (HashOperationMode) LIST[1];

	/**
	 * HashContainer collects repeated Keys/Duplicates, but assigns unique Indices to
	 * them. Well usable to create a bijective Mapping ('Index') between Objects and int
	 */
	final static public HashOperationMode INDEX_COLLECTION = (HashOperationMode) LIST[2];

	// //////////////////////////////////////////////////////////////////////////
	// / #region : Interface Enum: Implementation
	// //////////////////////////////////////////////////////////////////////////

	/** Used by the createList Method to create Instances for the List */
	protected Enum newEnum(long val_, long Offset_, Enum[] list_, String[] names_,
			Hashtable EnumsByName_) {
		return new HashOperationMode(val_, Offset_, list_, names_, EnumsByName_);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// / #region : Constructors, calling each other using this()/super()
	// //////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor */
	private HashOperationMode() {}

	/**
	 * Initializing Constructor
	 * @param val the Value for this Enum
	 * @param list the Enumeration this Enum belongs to
	 */
	private HashOperationMode(final long val_, final long Offset_, final Enum[] list_,
			final String[] names_, final Hashtable EnumsByName_) {
		super(val_, Offset_, list_, names_, EnumsByName_);
	}

}

/**
 * This class implements a Hashed Container, which is a very fast Storage for Objects. The
 * Objects are stored and retrieved in O(1) by their HashCode, which again is computed
 * from their Contents, so the Items needn't be searched for, when their HashCode is
 * unique. The Elements are unordered though; for sorted Containers,
 * @see streamIO.object.enumer.container.tree.TreeMap which stores and retrieves Objects
 *      in O(log(n))
 * @see streamIO.object.enumer.container.SortedArray which retrieves Objects at most in
 *      O(log(n)) and stores them in O(n), but with a very small Factor. Elements with
 *      equal HashCode are chained and thus it allows to stack identical Elements,
 *      especially for local Overrides of Mappings, which is not possible with a HashMap
 *      (Function). This is needed e.g. for local Variables or Namespace Declarations
 *      during SAX Parsing. Known uses:
 * @see ContainerSet is best implemented using a HashContainer
 * @see Relation is best implemented using a HashContainer Double Hashing can be
 *      implemented on two Strategies: Nested Hashing (expensive!): every Hash Entry
 *      contains another Hash Map Combined Keys: the key of every Entry is a (symmetric or
 *      antisymmetric) Combination (Pair) of the individual Keys. In Contrast to the
 *      java.util.HashSet, this HashContainer has the Advantage that 1) it implements the
 *      Container Interface directly and 2) you can supply a custom HashCode and Equals
 *      Method. 3) you can stack identical Elements and Mappings for 'local' Overrides!
 *      This allows for very efficient Implementations of findFirst(), which results in
 *      efficient Set Operations.
 *      <p>
 *      To successfully store and retrieve objects from a HashContainer, the Objects must
 *      implement the <code>hashCode</code> Method and the <code>equals</code> Method.
 *      Alternatively a HashCode Function will be used, if it is supplied (!= null).
 *      <p>
 *      The HashContainer can act both as a Set and as a Container depending on the
 *      Methods used: add() and addItem() will add all Items unconditionally union() and
 *      unionItem() will check whether these exist before adding them
 *      <p>
 *      An instance of <code>HashContainer</code> has two Parameters that affect its
 *      Efficiency: its <i>Capacity</i> and its <i>Load Factor</i>. The load factor is
 *      the Number of Items per HashCode and should be between 0.0 and 1.0. When the
 *      number of entries in the HashContainer exceeds the Product of the Load Factor and
 *      the Current Capacity, the Capacity is increased by calling the <code>rehash</code>
 *      Method. Larger Load Factors use Memory more efficiently, at the Expense of larger
 *      expected Time per Lookup.
 *      <p>
 *      If many entries are to be made into a <code>HashContainer</code>, creating it
 *      with a sufficiently large Capacity may allow the Entries to be inserted more
 *      efficiently than letting it perform automatic Rehashing as needed to grow the
 *      Table.
 *      <p>
 *      This example creates a HashContainer of Numbers. It uses the Names of the Numbers
 *      as Keys:
 *      <p>
 *      <blockquote>
 * 
 * <pre>
 * HashContainer numbers = new HashContainer();
 * numbers.addItem(&quot;one&quot;, new Integer(1));
 * numbers.addItem(&quot;two&quot;, new Integer(2));
 * numbers.addItem(&quot;three&quot;, new Integer(3));
 * </pre>
 * 
 * </blockquote>
 *      <p>
 *      To retrieve a Number, use the following code:
 *      <p>
 *      <blockquote>
 * 
 * <pre>
 * Integer n = (Integer) numbers.get(&quot;two&quot;);
 * if (n != null) {
 * 	System.out.println(&quot;two = &quot; + n);
 * }
 * </pre>
 * 
 * </blockquote> Methods are: addAt subAt setAt replaceAt flipAt contains added in
 *      Container are removeAt This Implementation mixes the HashCode structure with the
 *      simply linked List structure. A cleaner Approach would be to reuse the linked List
 *      structure. But using a dedicated HashEntry Object saves a lot of Casting and
 *      re-evaluating the HashCode Function; together with inline List Processing this
 *      leads to optimized Performance!
 * @author Matthias Heuer
 * @version 21/11/98
 * @see java.lang.Object#equals(java.lang.Object)
 * @see java.lang.Object#hashCode()
 * @see java.util.HashContainer#rehash()
 * @see streamIO.object.enumer.container.HashIterator
 * @stereotype container With an inefficient Hash Function the Number of Bins with more
 *             than 80% of all Elements should not be less than 20%! The Characterizing
 *             Number would be #Elements exceeding 0 or 1 Element per Bin scaled by the
 *             total #Elements, which should be as small as possible (near 0)! With a bad
 *             Hash Function it is better to use a sorted Tree. A Number that is easier to
 *             derive is the #Bins with Element Count exceeding 1. TODO: add an Array of
 *             Element Counters per HashBin to determine the Efficiency of the Hash
 *             Function. Design Decisions:
 * @see streamIO.object.enumer.container.HashSet uses ArrayLists instead of Linked Lists,
 *      but since there should be enough bins and removing an Item would be a more complex
 *      (and expensive) Operation, this Linked List Version is retained. A dynamic Vector
 *      of dynamic Object Vectors is used there. The HashCode cannot be cached then, all
 *      Vectors have to be initialized and Mappings cannot be stored without creating
 *      additional Objects. On the other Hand it is not necessary to create List Elements
 *      for every added Item.
 */
final public class HashContainer extends AContainer {

	// //////////////////////////////////////////////////////////////////////////
	// static Constants and Variables
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for this Class */
	final static public Log L = new Log(HashContainer.class);

	/** This Mask brings the HashCode to positive Values */
	final static public int HASH_MASK = Integer.MAX_VALUE;

	/** The default initial Capacity on instantiating an Array */
	public static int DEFAULT_CAPACITY = 11;

	/** The default load factor, i.e. Items per Bins, on instantiating a HashSet. */
	public static float DEFAULT_LOAD_FACTOR = 0.75f;

	// //////////////////////////////////////////////////////////////////////////
	// static Methods
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * returns the weight associated with the Edge of Type typ from key to val.
	 * @param key
	 * @param val
	 * @param typ
	 * @return the weight associated with the Edge of Type typ from key to val. Infinity,
	 *         if there is no such edge.
	 */
	public double getWeight(final Object key, final Object val, final Object typ) {
		final IndexAssociation entry = getEntry(key, val, typ);
		if (entry != null) return entry.weight;
		return Double.POSITIVE_INFINITY;
	}

	/**
	 * returns the weight associated with the Edge of Type typ from key to val.
	 * @param key
	 * @param val
	 * @param typ
	 * @return the weight associated with the Edge of Type typ from key to val. Infinity,
	 *         if there is no such edge.
	 */
	public TypedAssociation getEdge(final Object key, final Object val, final Object typ) {
		final IndexAssociation entry = getEntry(key, val, typ);
		if (entry != null) return new TypedAssociation(key, val, entry.weight, typ);
		return null;
	}

	/**
	 * returns the weight associated with the Edge of Type typ from key to val.
	 * @param key
	 * @param val
	 * @param typ
	 * @return the weight associated with the Edge of Type typ from key to val. Infinity,
	 *         if there is no such edge.
	 */
	protected IndexAssociation getEntry(final Object key, final Object val,
			final Object typ) {
		iter.setFilter(key, typ);
		for (IndexAssociation entry; (entry = iter.nextEntry()) != null;)
			if (ValidationRule.EQUALS(entry.val, val, hashFn)) return entry;
		return null;
	}

	/**
	 * returns true when this is a subSet / subMap of the given Mapping
	 * @param sup the supposed SuperSet
	 * @return true when this is a subSet / subMap of the given Mapping
	 */
	public boolean isSubSet(final HashContainer sup) {
		this.iter.reSet();
		for (HashEntry subNode; (subNode = iter.next) != null;) {
			final double supWeight = sup.getWeight(subNode.key, subNode.val, subNode.typ);
			if (supWeight > subNode.weight) return false;
		}
		return true;
	}

	/**
	 * returns true when both Sets / Mappings are identical
	 * @param sup
	 * @return true when both Sets / Mappings are identical.
	 */
	public boolean equals(final HashContainer sup) {
		return this.isSubSet(sup) && sup.isSubSet(this);
	}

	// //////////////////////////////////////////////////////////////////////////
	// Variables
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Rehashes the table when count exceeds this Capacity. Pre-Calculated Helper Value
	 */
	protected transient int capacity;

	/** The load factor for the HashContainer. */
	protected float loadFactor;

	/** The hash table data. */
	protected transient HashEntry[] bins;

	/** Reference to the Iterator, identical to mEnum, but already cast to save Tests */
	protected transient HashIterator iter;

	/**
	 * Flag to switch between adding, or replacing and counting Duplicates The latter
	 * Model is used to implement a fast Bag Container.
	 */
	final public HashOperationMode operation; // boolean replaceAndSumUpDuplicates;

	/**
	 * Number of distinct Items in this Container. This is slightly redundant, since it
	 * could be calculated in an O(N) Operation, but O(N) is expensive and should only be
	 * performed together with other O(N) Operations. Post-Incremented.
	 */
	protected int distinctKeyCount; // =0;

	/**
	 * @return the Number of distinct Items in this Container, but only if operation !=
	 *         COLLECTION_RELATION
	 */
	public int getDistinctKeyCount() {
		switch (operation.getValue()) {
			case HashOperationMode.BYTE_BAG_SET_FUNCTION :
				return itemCount; // identical for Sets
			case HashOperationMode.BYTE_INDEX_COLLECTION :
				return distinctKeyCount;
			case HashOperationMode.BYTE_COLLECTION_RELATION :
				throw new RuntimeException("The simple " + operation
						+ " Model does not support Calculation of distinctKeyCount!");
			default :
				throw new ArrayIndexOutOfBoundsException("Value " + operation.getValue()
						+ " should never occur!");
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// Accessor Methods (getXXX/setXXX)
	// //////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////
	// Constructors, calling each other using this()/super() (not in Interfaces)
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a new, empty HashContainer with the specified initial capacity and the
	 * specified load factor.
	 * @param _initialCapacity the initial capacity of the HashContainer.
	 * @param _loadFactor a number between 0.0 and 1.0.
	 * @exception IllegalArgumentException if the initial capacity is less than or equal
	 *                to zero, or if the load factor is less than or equal to zero.
	 */
	public HashContainer(final int _initialCapacity, final float _loadFactor,
			final IEquivalence _hashCode, final HashOperationMode _operationMode) {
		if ((_loadFactor <= 0) || (_loadFactor >= 1))
			throw new IllegalArgumentException(
					"The Load Factor (Items per Bins) must be between 0 an 1, but is "
							+ _loadFactor);
		if (_initialCapacity < 0)
			throw new IllegalArgumentException(
					"The intitial Capacity must not be negative, but is "
							+ _initialCapacity);
		this.hashFn = _hashCode;
		this.loadFactor = _loadFactor;
		this.capacity = _initialCapacity;
		this.bins = new HashEntry[(int) (_initialCapacity / _loadFactor)]; // already
		// enlarge the
		// Table
		// according
		// to the Load
		// Factor
		this.enm = this.iter = new HashIterator(this);
		this.operation = _operationMode;
	}

	/**
	 * Constructs a new, empty HashContainer with the specified initial capacity and the
	 * specified load factor.
	 * @param initialCapacity the initial capacity of the HashContainer.
	 * @param loadFactor a number between 0.0 and 1.0.
	 * @exception IllegalArgumentException if the initial capacity is less than or equal
	 *                to zero, or if the load factor is less than or equal to zero.
	 */
	public HashContainer(final int initialCapacity, final float loadFactor) {
		this(initialCapacity, loadFactor, null, HashOperationMode.COLLECTION_RELATION);
	}

	/**
	 * Constructs a new, empty HashContainer with the specified Load Factor and defaults
	 * the initial Capacity factor to 'defaultCapacity'.
	 * @param loadFactor a number between 0.0 and 1.0.
	 */
	public HashContainer(final float loadFactor) {
		this(DEFAULT_CAPACITY, loadFactor, null, HashOperationMode.COLLECTION_RELATION);
	}

	/**
	 * Constructs a new, empty HashContainer with the specified initial capacity and
	 * defaults the load factor to 'defaultLoadFactor'.
	 * @param initialCapacity the initial capacity of the HashContainer.
	 */
	public HashContainer(final int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR, null,
				HashOperationMode.COLLECTION_RELATION);
	}

	/** Constructs a new, empty HashContainer with a default capacity and load factor. */
	public HashContainer(final IEquivalence HashCode) {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, HashCode,
				HashOperationMode.COLLECTION_RELATION);
	}

	/** Constructs a new, empty HashContainer with a default capacity and load factor. */
	public HashContainer(final int initialCapacity, final IEquivalence HashCode) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR, HashCode,
				HashOperationMode.COLLECTION_RELATION);
	}

	/** Constructs a new, empty HashContainer with a default capacity and load factor. */
	public HashContainer() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, null,
				HashOperationMode.COLLECTION_RELATION);
	}

	/** Constructs a new, empty HashContainer with a default capacity and load factor. */
	public HashContainer(final HashOperationMode _operation) {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, null, _operation);
	}

	/**
	 * Creates an uninitalized new Instance of it's class. This can in VB also be achieved
	 * by 'CreateObjectFromInstance', which may be slower. NewInstance also clones the
	 * Types, but does not initialize them! When overriding, use newInstance on all
	 * Components.
	 */
	public ICopyAble newInstance() {
		return new HashContainer(bins.length, loadFactor, hashFn, operation);
	}

	// //////////////////////////////////////////////////////////////////////////
	// Methods, public ones, then private ones (not in Interfaces)
	// //////////////////////////////////////////////////////////////////////////

	/** This is the basic Operation which has to be redefined in Bag and Relation */
	protected Object removeItemAt(final HashEntry e, final HashEntry prev, final int index) {
		// if (e == null) return null; //not necessary here, check it further up...
		if (prev != null) {
			prev.next = e.next; // skip this Item
		} else {
			bins[index] = e.next;
		} // Item has no predecessor, insert at Front
		++major; // may result in phantom and lost Elements
		--itemCount;
		return e.key;
	}

	/**
	 * Adds the Item to the HashContainer at the Position 'index' with HashCode 'hash'
	 * given by the associated internal Iterator. This Optimization has to be implemented
	 * by all Containers, but currently only by HashContainer. This is of course
	 * dangerous, because Item may not match the current Iterator Position!
	 */
	public Container addItemAtCurrPos(final Object _key, final Object _value,
			final Object _typ, final double _weight, final int _ndx) {
		addItemAt(_key, _value, _typ, _weight, _ndx, iter.currRow, iter.filterHash);
		return this;
	} //

	/**
	 * Adds the Item to the HashContainer at Position 'index' with HashCode 'hash'. This
	 * is the basic Routine, which has to be redefined in Bag and Dictionary
	 */
	protected HashEntry addItemAt(final Object _key, final Object _value,
			final Object _typ, final double _weight, final int _ndx) {
		return addItemAt(_key, _value, _typ, _weight, _ndx, hashFn != null ? hashFn
				.HashCode(_key) : _key.hashCode());
	} //

	/**
	 * Adds the Item to the HashContainer at Position 'index' with HashCode 'hash'. This
	 * is the basic Routine, which has to be redefined in Bag and Dictionary
	 */
	protected HashEntry addItemAt(final Object _key, final Object _value,
			final Object _typ, final double _weight, final int _ndx, final int _hash) {
		final int _bin = (_hash & HASH_MASK) % bins.length; // Modulo Operation is really
		// expensive!
		return addItemAt(_key, _value, _typ, _weight, _ndx, _bin, _hash);
	} //

	/**
	 * Adds the Item to the HashContainer at Position 'index' with HashCode 'hash'. This
	 * is the fundamental Routine, which has to be redefined in Bag and Dictionary
	 */
	protected synchronized HashEntry addItemAt(final Object _key, final Object _value,
			final Object _typ, final double _weight, int _ndx, final int _bin,
			final int _hash) {
		final HashEntry root;
		HashEntry curr = root = bins[_bin];
		if ((operation == HashOperationMode.BAG_SET_FUNCTION)
				|| (operation == HashOperationMode.INDEX_COLLECTION)) {
			while ((curr != null) && // search for the exact Match!
					((curr.hash != _hash)
							|| (!ValidationRule.EQUALS(curr.key, _key, hashFn)) || !ValidationRule
							.EQUALS(curr.typ, _typ, hashFn)))
				curr = curr.next;
			if (operation == HashOperationMode.INDEX_COLLECTION)
				if (curr == IIStreamIn.EOI) // starting new Bin...
					_ndx = distinctKeyCount++; // ...create a new Index
				// (Post-Incremented)
				else _ndx = curr.ndx; // use the same index
			else // (operation == HashOperationMode.BAG_SET)
			if (curr == IIStreamIn.EOI)
				_ndx = 1; // start Bag-counting at 1
			else { // sum up Weights
				++curr.ndx; // Increment the Bag-Counter for the #of Items
				curr.weight += _weight; // Sum up the Bag-weights for the Sum of Items
				curr.val = _value; // can be exchanged
				// curr.typ = _typ; //same anyway
				return curr;
			}
		} // else create a new entry right away
		final HashEntry ret = bins[_bin] = new HashEntry(_key, _value, _typ, _weight,
				_ndx, _hash, root); // Create a new entry at the Beginning.
		incCounter();
		return ret;
	} // using 'mFilterHash' is an Optimization

	/**
	 * Increments the Counter for Elements for this HashContainer Also modifies the
	 * Version. Has to be called on any Addition to the Container.
	 */
	protected void incCounter() {
		if (++itemCount >= capacity) rehash(); // Rehash the table if the Capacity is
		// exceeded
		++minor; // minor is the same as Major, since there is no 'In Place'
		// Modification of a HashContainer
	}

	/**
	 * adds this Item to the Store in Place: += The Type of Item is not analyzed, i.e.
	 * Containers are added as is. Default Operation is addNext, which is easy to
	 * implement for Lists, as well as it ensures that the Item will be picked up by the
	 * current Iterator, which is frequently used e.g. by LL(1) Parsers.
	 * @see Order()
	 * @see nextItem()
	 */
	public IIStreamOut addItem(final Object arg) {
		addAt(arg);
		return this;
	}

	// //////////////////////////////////////////////////////////////////////////
	// Optimizations
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an Iterator of the values in this HashContainer. Use the Iterator methods
	 * on the returned object to fetch the elements sequentially.
	 * @return an Iterator of the values in this HashContainer.
	 * @see java.util.Enumeration
	 * @see java.util.HashContainer#keys()
	 */
	// public synchronized Iterator Iterator() { return new HashIterator(this); }
	/**
	 * Increases the capacity of this HashContainer, if necessary, to ensure that it can
	 * hold at least the number of components specified by the minimum capacity argument,
	 * without exceeding the LoadFactor.
	 * @param minCapacity the desired minimum capacity.
	 */
	public int setCapacity(final int minCapacity) {
		if (capacity < minCapacity) return rehash(minCapacity);
		return capacity;
	} // this.mTable.length * loadFactor; }

	/**
	 * Returns the current minimum capacity of this Array.
	 * @return the current capacity of this Array.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Rehashes the contents of the HashContainer into a HashContainer with a larger
	 * capacity. This method plainly doubles this HashContainer's capacity.
	 */
	protected int rehash() {
		return rehash(bins.length + bins.length + 1);
	} // double the Capacity!

	/**
	 * Rehashes the contents of the HashContainer into a HashContainer with a larger
	 * capacity. This method is called automatically when the number of keys in the
	 * HashContainer exceeds this HashContainer's capacity times load factor.
	 */
	protected int rehash(final int newCapacity) {
		++major; // this is a major Reorganization that results in missing Elements
		// during Iteration!
		final HashEntry[] oldTable = bins;
		bins = new HashEntry[(int) (newCapacity / loadFactor)];
		capacity = newCapacity;
		// System.out.println("rehash old=" + oldCapacity + ", new=" + newCapacity + ",
		// thresh=" + mCapacity + ", count=" + count);
		for (int i = oldTable.length; --i >= 0;) {
			for (HashEntry old = oldTable[i]; old != null;) { // Modulo Operation is
				// really expensive!
				final HashEntry e = old;
				old = old.next; // Re-Use the Entries e
				final int index = (e.hash & HASH_MASK) % bins.length; // don't need
				// recalculate
				// HashValues!
				e.next = bins[index];
				bins[index] = e;
			}
		}
		iter.reSet();
		// iter.reStart(null);
		return newCapacity;
	}

	/**
	 * Resets the Iterator to the given Position counted from the last marked Position.
	 */
	/*
	 * public long reset(long Position) throws NoSuchMethodException { iter.reStart(null);
	 * if (Position == 0) return 0; return iter.skip(Position); } //no external caller
	 * cares for a marked Object // return mEnum.reset(Position); } // /** Maps the
	 * specified <code>Item</code> to the specified <code>Item</code> in this
	 * HashContainer. Neither the Item nor the Item can be <code>null</code>. <p> The
	 * Item can be retrieved by calling the <code>get</code> method with a Item that is
	 * equal to the original Item. @param Item the HashContainer Item. @param Item the
	 * Item. @return the previous Item of the specified Item in this HashContainer, or
	 * <code>null</code> if it did not have one. @exception NullPointerException if the
	 * Item or Item is <code>null</code>.
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @see java.util.HashContainer#get(java.lang.Object)
	 */
	public synchronized ISemiGroup addAt(final Object _key, final Object _value,
			final Object _typ, final double _weight, final int _ndx) {
		if (_key == null) throw new NullPointerException();
		if (_key instanceof IIStreamIn) {
			addItems((IIStreamIn) _key);
			return this;
		}
		int hash = (hashFn == null) ? _key.hashCode() : hashFn.HashCode(_key);
		addItemAt(_key, _value, _typ, _weight, _ndx, hash);
		return this;
	}

	/**
	 * Flips the Item, i.e. when it is contained, remove it, otherwise add it.
	 * @return the removed Item, otherwise 'null' This corresponds to the XOR Operation.
	 *         This Operation is optimized in this Class, because 'iter' still points to
	 *         the correct Location
	 */
	public Object flipItem(final Object _key, final Object _val, final Object _typ,
			final double _weight, final int _ndx) {
		if (iter.findFirst(_key, _typ) != IStreamIn.EOI) return iter.removeCurr();
		bins[iter.filterBin] = new HashEntry(_key, _val, _typ, _weight, _ndx,
				iter.filterHash, bins[iter.filterBin]);
		// addItemAt(_key, _val, _typ, _weight, _ndx, iter.currRow, iter.filterHash);
		// //using 'mFilterHash' is an Optimization
		return null;
	}

	/**
	 * @param key Object to find or add
	 * @return the HashEntry found or added
	 */
	public IndexAssociation findOrAdd(final Object _key, final Object _val,
			final Object _typ, final double _weight) {
		return findOrAdd(_key, _val, _typ, _weight, distinctKeyCount);
	}

	/**
	 * @param key Object to find or add
	 * @return the HashEntry found or added
	 */
	public IndexAssociation findOrAdd(final Object _key, final Object _val,
			final Object _typ, final double _weight, final int _ndx) {
		IndexAssociation value = iter.findFirstEntry(_key);
		if (value == null) { // Add Lookups for Nodes with 0 == FanOut
			value = bins[iter.filterBin] = new HashEntry(_key, _val, _typ, _weight, _ndx,
					iter.filterHash, bins[iter.filterBin]);
			// optimized by inserting the Entry directly, since we know, it is new and
			// have already calculated it's hash and bin.
			if (distinctKeyCount == _ndx) ++distinctKeyCount; // Post-Incremented
			incCounter();
		}
		return value;
	}

	/**
	 * Replaces the given <code>Item</code> by the one specified. The Item can not be
	 * <code>null</code>.
	 * <p>
	 * @param _key the HashContainer Item.
	 * @return the previous Item of the specified Item in this HashContainer, or
	 *         <code>null</code> if it did not have one.
	 * @exception NullPointerException if the Item or Item is <code>null</code>.
	 * @see java.lang.Object#equals(java.lang.Object) This Operation is optimized in this
	 *      Class, because 'iter' still points to the correct Location
	 */
	public Object replaceItem(final Object _key, final Object _value, final Object _typ,
			final double _weight, final int _ndx) {
		Object ret = null;
		if (iter.findFirst(_key, _typ) != IStreamIn.EOI) {
			ret = iter.removeCurr();
			bins[iter.filterBin] = new HashEntry(_key, _value, _typ, _weight, _ndx,
					iter.filterHash, bins[iter.filterBin]);
			// addItemAt(_key, _value, _typ, _weight, _ndx, iter.currRow,
			// iter.filterHash);
		} // using 'mFilterHash' is an Optimization
		return ret;
	}

	/**
	 * Adds or replaces the given <code>Item</code> by the one specified. The Item can
	 * not be <code>null</code>. This is equivalent to the OR Operation
	 * <p>
	 * @param _key the HashContainer Item.
	 * @return the previous Item of the specified Item in this HashContainer, or
	 *         <code>null</code> if it did not have one.
	 * @exception NullPointerException if the Item or Item is <code>null</code>.
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @see flipItem() which removes the Item, when it exists (XOR Operation)
	 * @see replaceItem() which only replaces it, when it existed before (Id)
	 * @see removeItem() which only removes the Item (- Operation)
	 * @see addItem() which only adds the Item (+ Operation) This Operation is optimized
	 *      in this Class, because 'iter' still points to the correct Location
	 */
	public Object setItem(final Object _key, final Object _value, final Object _typ,
			final double _weight, final int _ndx, final boolean replace) {
		Object ret; // optimized setItem Method
		if ((ret = iter.findFirst(_key, _typ)) != IStreamIn.EOI) {
			if (replace) {
				ret = iter.removeCurr();
				bins[iter.filterBin] = new HashEntry(_key, _value, _typ, _weight, _ndx,
						iter.filterHash, bins[iter.filterBin]);
				// addItemAt(_key, _value, _typ, _weight, _ndx, iter.currRow,
				// iter.filterHash);
			}
		} // using 'mFilterHash' is an Optimization

		// alternative setItem Method, reusing findFirst implicitly in remove()
		/*
		 * if (replace) ret = this.remove(Item); //replaces the Item else if ((ret =
		 * iter.findFirst(Item)) != StreamIn.EOI) return ret; //doesn't replace
		 * addItemAt(Item, iter.index, iter.mFilterHash); //using 'mFilterHash' and
		 * 'index' is an Optimization //generic setItem Method /* Object ret = null; if
		 * (iter.findFirst(Item) != StreamIn.EOI) { ret = iter.removeCurr(); }
		 * addItem(Item); //using 'mFilterHash' is an Optimization
		 */return ret;
	}

	/**
	 * @return this, set to the Boolean Constant for the Representation of 'false' = 0
	 *         i.e. not 'true'. For Conatainers this is equivalent to zeroAt() and clear()
	 * @see zeroAt()
	 */
	public Boole FalseAt() {
		major = itemCount = 0;
		bins = new HashEntry[bins.length];
		// HashEntry tab[] = table; //this is slower but leaves the Array on the Heap
		// for (int index = tab.length; --index >= 0; )
		// tab[index] = null; //This ripples the Garbage Collection along the linked List.
		return this;
	}

	/**
	 * Creates a shallow copy of this HashContainer. The keys and values themselves are
	 * not cloned. This is a relatively expensive operation, but still cheaper than the
	 * Default Implementation, because the HashCodes and Capacities don't need to be
	 * re-calculated.
	 * @return a clone of the HashContainer.
	 */
	public ICopy Copy() {
		try {
			final HashContainer t = (HashContainer) super.clone();
			t.enm = t.iter = new HashIterator(t);
			t.bins = new HashEntry[bins.length];
			for (int i = bins.length; i-- > 0;)
				if (bins[i] != null) t.bins[i] = (HashEntry) bins[i].Copy();
			return t;
		} catch (final CloneNotSupportedException e) { // this shouldn't happen, since
			// the HashContainer is Cloneable
			throw new OperationNotSupported(e.toString(), e);
		}
	}

	/**
	 * WriteObject is called to save the state of the HashContainer to a stream. Only the
	 * keys and values are serialized since the hash values may be different when the
	 * contents are restored. iterate over the contents and write out the keys and values.
	 */
	/*
	 * protected synchronized void writeObject(java.io.ObjectOutputStream s) throws
	 * IOException { // Write out the length, Capacity, loadfactor s.defaultWriteObject(); //
	 * Write out length, count of elements and then the Item/Item objects
	 * s.writeInt(mTable.length); s.writeInt(count); for (int index = mTable.length-1;
	 * index >= 0; index--) { HashEntry entry = mTable[index]; while (entry != null) {
	 * s.writeObject(entry.Item); s.writeObject(entry.Item); entry = entry.next; } } }
	 * /**readObject is called to restore the state of the HashContainer from a stream.
	 * Only the keys and values are serialized since the hash values may be different when
	 * the contents are restored. Read count elements and insert into the HashContainer.
	 */
	/*
	 * protected synchronized void readObject(java.io.ObjectInputStream s) throws
	 * IOException, ClassNotFoundException { // s.defaultReadObject(); //Read in the
	 * length, Capacity, and loadfactor // Read the original length of the array and
	 * number of elements int origlength = s.readInt(); int elements = s.readInt(); //
	 * Compute new size with a bit of room 5% to grow, // but no larger than the original
	 * size. Make the length // odd if it's large enough, this helps distribute the
	 * entries. // Guard against the length ending up zero, that's not valid. int length =
	 * (int)(elements / loadFactor) + (elements / 20) + 3; if (length > elements &&
	 * (length & 1) == 0) length--; if (origlength > 0 && length > origlength) length =
	 * origlength; table = new HashEntry[length]; count = 0; // Read the number of
	 * elements and then all the Item/Item objects for (; elements > 0; elements--) {
	 * java.lang.Object Item = s.readObject(); addAt(Item); } }
	 * //////////////////////////////////////////////////////////////////////////////// //
	 * Optimizations
	 * ////////////////////////////////////////////////////////////////////////////////
	 * /** Tests, whether this Object exists in the Set, @return the Object, when found,
	 * otherwise returns Stream.Iterator.EOL actually it is not necessary to override this
	 * Method, because you have to iterate over all List Members with identical Hash
	 * Modulus and this is very well restricted by the Method mEnum.setFilter()
	 */
	// public Object findFirst(Object Item) { } //
	/**
	 * Returns the Degree of the given Object, i.e. how often it appears in the Container.
	 * This is done very fast for the Bag and used for the Relation / Tree to determine
	 * the (Out-)Degree of the Nodes. For Maps this is the Fan-Out.
	 */
	public int Degree(final Object _key, final Object _typ) {
		int ret = 0;
		final HashIterator iter = new HashIterator(this, _key, _typ);
		while ((iter.nextItem()) != IStreamIn.EOI)
			// the Filter does the Rest
			++ret;
		return ret;
	}

	// //////////////////////////////////////////////////////////////////////////
	// high Level static Methods for loading an displaying Graphs
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the Edges from the given ResultSet to the Graph After this, a Mapping of
	 * Objects to integers exists, so that all Relations can be mapped to integer
	 * Relations and be directly processed by faster SparseMatrix Algorithms. The Edges
	 * can either be given in relational Format: Source/Target/directed/Weight or in
	 * sparse Format: Source/Target1/Target2/Target3/...
	 * @param rs
	 * @param directedDefault if false, also the inverse Edge is being added.
	 * @param weightDefault the Default Weight, if none is given
	 * @param reverse If reverse is true, the Direction of the Graph is reversed!
	 * @throws SQLException
	 */

	/** reads all available Data from the ResultSet */
	public void read(final ResultSet rs) throws SQLException {
		read(rs, 0);
	}

	/** reads all available Data from the ResultSet */
	public void read(final ResultSet rs, final int firstCol) throws SQLException {
		final ResultSetMetaData rsMeta = rs.getMetaData();
		final int maxCol = Math.min(rsMeta.getColumnCount(), firstCol + 5);
		final int[] cols = new int[maxCol - firstCol];
		for (int i = cols.length; --i >= 0;)
			cols[i] = firstCol + i;
		read(rs, cols);
	}

	/** reads all available Data from the ResultSet */
	public void read(final ResultSet rs, final int[] cols) throws SQLException {
		while (rs.next()) {
			final IndexAssociation entry = new IndexAssociation(rs, cols);
			if (cols.length < 5) // no explicit Index given => count Items
				entry.ndx = itemCount;
			addItemAt(entry.key, entry.val, entry.typ, entry.weight, entry.ndx);
			/*
			 * final HashEntry entry = new HashEntry (rs, cols, hashFn); final int bin =
			 * (entry.hash & HASH_MASK) % bins.length; //Modulo Operation is really
			 * expensive! entry.next = bins[bin]; bins[bin] = entry; incCounter();
			 */
		}
	}

	/** creates a SparseGraph Representation of this Relation */
	public VectorObject fillGraph(final IGraph graph) {
		this.setCapacity(itemCount << 1); // prevent a rehash due to added Target
		// Nodes!!!
		// final SparseGraph ret = new SparseGraph(this.itemCount, -1);
		// graph.setCapacity(itemCount); //distinctKeyCount is too small, since unknown
		// 'val' Objects haven't been added
		final VectorObject list = new VectorObject(itemCount); // Object[itemCount];
		// //itemCount is
		// oversized...
		final HashIterator iter = new HashIterator(this); // need a 2nd to be able to
		// search!
		// iter.reSet(); //not necessary!
		for (IndexAssociation entry; (entry = iter.nextEntry()) != null;) {
			int typ = -1;
			if (entry.val == null) continue;
			IndexAssociation value = findOrAdd(entry.val, null, null, 0, distinctKeyCount);
			if (entry.typ != null) {
				IndexAssociation type = findOrAdd(entry.typ, null, null, 0,
						distinctKeyCount);
				list.setAt(typ = type.ndx, type.key);
			}
			list.setAt(value.ndx, value.key); // since Graphs implicitly assume that Set
			// and Image are the same: G <= S*S
			list.setAt(entry.ndx, entry.key);
			graph.addEdge(entry.ndx, value.ndx, true, (float) entry.weight, typ);
		}
		return list; // TODO: return List not correctly filled!
	}

	/** Tests all Methods of this Class */
	final static public SparseGraph displayDataSet(final String rsFilePath)
			throws Exception {
		return displayDataSet(new File(rsFilePath));
	}

	/** displays the Graph of the DataSet in the given File */
	final static public SparseGraph displayDataSet(final File rsFile) throws Exception {
		return displayDataSet(new ResultSetSep(rsFile));
	}

	/** Displays the Graphic read from the given DataSet */
	final static public SparseGraph displayDataSet(final ResultSet rs) throws Exception {
		// read the DataSet into the HashContainer
		final HashContainer hc = new HashContainer(HashOperationMode.INDEX_COLLECTION);
		hc.read(rs);
		// fill a simplified Graph Model using int
		final SparseGraph graph = new SparseGraph(hc.itemCount); // itemCount is
		// oversized...
		final VectorObject list = hc.fillGraph(graph); // distinctKeyCount is too small,
		// since unknown 'val' Objects
		// haven't been added
		// display the simplified Model
		final BaseApplet applet = new BaseApplet();
		final Map2DPainter frame = new Map2DPainter(applet, new Map2DModel(list, graph,
				list));
		frame.addDefaultControllers(applet);
		frame.show(); //

		return graph;
	}

	/** Tests all Methods of this Class */
	final public HashContainer simplify() {
		// fill a simplified Graph Model using int
		final SparseGraph graph = new SparseGraph(itemCount); // itemCount is
		// oversized...
		final VectorObject list = fillGraph(graph); // distinctKeyCount is too small,
		// since unknown 'val' Objects haven't
		// been added

		// /Create a Graph with the SubGraphs as Nodes.
		// /Unfortunately the Information is lost,
		// /which SubNode connects to which SubNode of the other Component,
		// /since this typically also changes with each Edge!
		final int[] connComp = graph.stronglyConnectedComponents(); // creates a
		// simplified Graph,
		// but only for
		// integer Mappings
		final HashContainer[] subMaps = new HashContainer[graph.getNumVertices()];
		final int[] numItemsPerComponent = new int[subMaps.length];
		for (int i = subMaps.length; --i >= 0;)
			++numItemsPerComponent[connComp[i]];
		for (int i = subMaps.length; --i >= 0;) { //
			if (numItemsPerComponent[i] > 1) // only if it's a real SubGraph
				subMaps[i] = new HashContainer(); // to have only Graphs at this
			// Level...
		} // ...fill a SubGraph with a Dummy Relation : A->A
		final HashContainer ret = new HashContainer(HashOperationMode.INDEX_COLLECTION);
		ret.hashFn = EquivalenceIdentity.Identity; // since Containers have no
		// Identity...
		// ...it is problematic to use them as Keys!
		// ...loop over all Edges, map both Start and End to the new Connected Components
		final IEdgeStreamIn iterator = graph.EdgeIterator();
		for (graphs.Edge edge; (edge = iterator.nextEdge()) != null;) {
			// any Node represents a Connection, no matter which Weight.
			final Object typ = list.getAt(edge.typ);
			final int keyMap = connComp[edge.key];
			final int valMap = connComp[edge.val];
			if (keyMap == valMap) { // model Edges within same Component
				subMaps[keyMap].addItemAt(list.getAt(edge.key), list.getAt(edge.val),
						typ, edge.weight, 0);
			} else { // return only the shortest Connections in the Meta Graph...
				Object key = subMaps[keyMap];
				if (key == null) key = list.getAt(keyMap);
				Object val = subMaps[valMap];
				if (val == null) val = list.getAt(valMap); // no SubGraph, only simple
				// Node
				final IndexAssociation e = ret.getEntry(key, val, typ);
				if (e == null)
					ret.addItemAt(key, val, typ, edge.weight, 0);
				else if (e.weight > edge.weight) e.weight = edge.weight;
			}// ...because you only want the Distance to the Hull!
		}
		return ret;
	}

	// //////////////////////////////////////////////////////////////////////////
	// static Testing and main() Methods (not in Interfaces)
	// //////////////////////////////////////////////////////////////////////////

	static final String[][] Sedgewick_29_1 = {{ //
			"A", "B", "1"}, { // 13 Points
			"A", "C", "1"}, { // 13 Edges
			"A", "F", "2"}, { // taking out these two Edges...
			"A", "G", "4"}, { // taking out this isolates G completely!
			"E", "D", "2"}, {"F", "D", "1"}, {"F", "E", "2"}, {"G", "E", "1"}, { // ...breaks
			// it
					// up
					// into
					// 4
					// Components
					"H", "I", "2"}, {"J", "M", "2"}, { //
			"J", "L", "3"}, {"J", "K", "1"}, {"L", "M", "1"}};

	static final String[][] Sedgewick_31_1a = {
			{ // for un- and directed Graph
			"A", "B", "1"}, {"A", "F", "2"}, {"D", "F", "1"}, {"E", "D", "2"},
			{"F", "E", "2"}, {"G", "E", "1"}, {"G", "J", "1"}, {"H", "G", "3"},
			{"H", "I", "2"}, {"J", "K", "1"}, {"J", "L", "3"}, {"J", "M", "2"},
			{"L", "G", "5"}, {"M", "L", "1"}};

	static final String[][] Sedgewick_32_1 = {
			{ // additional Edges for the directed Graph
			"A", "G", "4"}, {"C", "A", "1"}, {"G", "C", "1"}, {"I", "H", "2"},
			{"L", "M", "1"}};

	static final String[][] pureDirectedLinks = {{ //
			"K", "N", "2"}, { // to demonstrate that they are retained on
			// simplification!
					"N", "O", "2"}};

	/** Tests all Methods of this Class */
	public static void testDataSet() throws Exception {
		final SparseGraph graph = displayDataSet(new ResultSetArray(Sedgewick_29_1));
		final int[] topSort = graph.getSortSequence(); // a large 0 < (distinctKeyCount /
		// itemCount) <= 1 is an indicator
		// to use a sparse Matrix!
	}

	/** Tests all Methods of this Class */
	final static public void testSimplify() throws Exception {
		// read the DataSet into the HashContainer
		final HashContainer hc = new HashContainer(HashOperationMode.INDEX_COLLECTION);
		hc.read(new ResultSetArray(Sedgewick_31_1a));
		hc.read(new ResultSetArray(Sedgewick_32_1));
		hc.read(new ResultSetArray(pureDirectedLinks));
		final HashContainer simple = hc.simplify();
		final int expectedNumKeyComponents = 4; //
		Assert.EQUALS(simple.getDistinctKeyCount(), expectedNumKeyComponents);
		final int expectedNumEdges = 6;
		Assert.EQUALS(simple.getInt(), expectedNumEdges);
	}

	/** Tests all Methods of this Class */
	public static void testIt() throws Exception {
		// try {
		L.n("Testing " + HashContainer.class.getName());
		testSimplify();
		testDataSet();
		// testContainer(args); //TODO: infinite loop on union()!!!
	}

	/** Tests all Methods of this Class */
	public static void testContainer() throws Exception {
		HashContainer A = new HashContainer();
		HashContainer B = new HashContainer();
		AContainer.testIt(A); // TODO: infinite loop on union()!!!
		StringStreamIn Str1 = new StringStreamIn("ABCD");
		StringStreamIn Str2 = new StringStreamIn("BDEF");
		L.n("Str1 = " + Str1);
		L.n("Str2 = " + Str2);
		A.union(Str1);
		A.union(Str2);
		B.copyAt(A);
		L.n("Str1 OR Str2 = " + B);
		L.n("... DIFFat(Str1) = " + A.DIFFat(Str1));
		L.n("... DIFFat(Str1) = " + A.DIFFat(Str1) + "(no Effect)");
		L.n("... DIFFat(Str2) = " + A.DIFFat(Str2) + "(empty)");
		// L.n("0 = " + A.zeroAt()); //no longer necessary...
		A.add(Str1);
		A.add(Str2);
		L.n("Str1 + Str2 = " + A);
		L.n("... subAt(Str1) = " + A.subAt(Str1));
		L.n("... subAt(Str2) = " + A.subAt(Str2) + " (empty)");
		// L.n("... subAt(Str2) = " + A.subAt(Str2) + " (results in an Exception)");
		// Str1.reset();
		A.copyAt(Str1);
		// Str2.reset();
		B.copyAt(Str2);
		L.n("(" + A + ") * (" + B + ") = " + A.mul(B));
		// How is a Division defined?
		// by an Equivalence Relation, i.e. a Forest / HashMap, i.e. a normal Mapping.
		// a Product is also a 'normal' mapping resulting in Pairs or Maps.
		// } catch (NoSuchMethodException x) {}
	}

	/**
	 * The main entry point for the application.
	 * @param args Array of parameters passed to the application via the command line.
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length == 0)
			testIt();
		else for (int i = args.length; --i >= 0;)
			displayDataSet(args[i]);
	}

	/**
	 * Workaround to avoid the Evaluation of the Iterator during Debugging, komment this
	 * out if no longer needed.
	 */
	// public String toString() { return Integer.toString(this.itemCount); }
}
