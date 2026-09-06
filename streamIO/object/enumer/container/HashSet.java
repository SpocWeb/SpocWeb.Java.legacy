package streamIO.object.enumer.container;

import graphs.ICopy;
import math.vector.VectorObject;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IReSetAble;
import streamIO.StreamOutPrimitive;
import streamIO.copy.ICopyAble;
import streamIO.copy.boole.Boole;
import streamIO.copy.group.ISemiGroup;
import streamIO.exception.OperationNotSupported;
import streamIO.object.IStreamIn;
import streamIO.object.StringStreamIn;
import streamIO.object.enumer.AEnumerator;
import tester.IEquivalence;

/**
 * Implements a Hashed Container as a Vector of dynamic Object Vectors (Rows) indexed by
 * HashCode Modulus; superseded by the linked-List-based {@link HashContainer}.
 * @deprecated use {@link streamIO.object.enumer.container.HashContainer} instead
 * @see streamIO.object.enumer.container.HashContainer
  * which is a very fast Storage for Objects.
  * The Objects are stored and retrieved by their HashCode,
  * which again is computed from their Contents,<
  * so the Items needn't be searched for, when their HashCode is unique. 
  * 
  * This Implementation is uses a dynamic Vector of dynamic Object Vectors. 
  * This incurs Memory and Initialization Overhead though 
  * due to pre-sized Object Vectors. 
  * Also the HashCode cannot be cached (implement this in the Object itself), 
  * and Mappings, Counts etc. cannot be stored without creating additional Objects. 
  * On the other Hand it is not necessary to create List Elements 
  * for every added Item (an O(1) Operation). 
  * Unfortunately, a HashSet should be rehashed when several Items chain, 
  * so using a Vector does not help so much anyway!  
  * 
  * Known uses:
  * @see ContainerSet      is best implemented using a HashSet
  * @see Relation is best implemented using a HashSet
  * 
  * Double Hashing can be implemented on two Strategies: 
  * Nested Hashing (expensive!): every Hash Entry contains another Hash Map 
  * Combined Keys: the key of every Entry is a (symmetric or antisymmetric) 
  * Combination (Pair) of the individual Keys. 
  *
  * In Contrast to the java.util.HashSet,  
  * this HashSet has the Advantage that 
  * 1) it implements the Container Interface directly and
  * 2) you can supply a custom HashCode and Equals Method.
  * 3) you can stack identical Elements and Mappings for 'local' Overrides! 
  * 4) Arraylists are more efficient for simple Set Usage
  *
  * This allows for very efficient Implementations of findFirst(),
  * which results in efficient Set Operations.
  * <p>
  * To successfully store and retrieve objects from a HashSet, the
  * Objects must implement the <code>hashCode</code> Method
  * and the <code>equals</code> Method.
  * Alternatively a HashCode Function will be used, if it is supplied (!= null).
  * <p>
  * The HashSet can act both as a Set and as a Container
  * depending on the Methods used:
  *   add() and   addItem() will add all Items
  * union() and unionItem() will check whether these exist before adding them
  * <p>
  * An instance of <code>HashSet</code> has two Parameters
  * that affect its Efficiency:
  * its <i>Capacity</i> and its <i>Load Factor</i>.
  * The load factor is the Number of Items per HashCode
  * and should be between 0.0 and 1.0.
  * When the number of entries in the HashSet exceeds
  * the Product of the Load Factor and the Current Capacity,
  * the Capacity is increased by calling the <code>rehash</code> Method.
  * Larger Load Factors use Memory more efficiently,
  * at the Expense of larger expected Time per Lookup.
  * <p>
  * If many entries are to be made into a <code>HashSet</code>,
  * creating it with a sufficiently large Capacity may allow the
  * Entries to be inserted more efficiently than letting it perform
  * automatic Rehashing as needed to grow the Table.
  * <p>
  * This example creates a HashSet of Numbers. It uses the Names of
  * the Numbers as Keys:
  * <p><blockquote><pre>
  *     HashSet numbers = new HashSet();
  *     numbers.addItem("one", new Integer(1));
  *     numbers.addItem("two", new Integer(2));
  *     numbers.addItem("three", new Integer(3));
  * </pre></blockquote>
  * <p>
  * To retrieve a Number, use the following code:
  * <p><blockquote><pre>
  *     Integer n = (Integer)numbers.get("two");
  *     if (n != null) {
  *         System.out.println("two = " + n);
  *     }
  * </pre></blockquote>
  *
  * Methods are:
  * addAt
  * subAt
  * setAt
  * replaceAt
  * flipAt
  * contains
  *
  * added in Container are
  * removeAt
  *
  * This Implementation mixes the HashCode structure
  * with the simply linked List structure.
  * A cleaner Approach would be to reuse the linked List structure.
  * But using a dedicated HashEntry Object saves a lot of Casting
  * and re-evaluating the HashCode Function;
  * together with inline List Processing this
  * leads to optimized Performance!
  *
  * @author  Matthias Heuer
  * @version 21/11/98
  * @see     java.lang.Object#equals(java.lang.Object)
  * @see     java.lang.Object#hashCode()
  * @see     java.util.HashSet#rehash()
  *
  * @stereotype container
  *
  *
  * With an inefficient Hash Function the Number of Bins with
  * more than 80% of all Elements should not be less than 20%!
  * The Characterizing Number would be #Elements exceeding 0 or 1 Element per Bin
  * scaled by the total #Elements, which should be as small as possible (near 0)!
  * With a bad Hash Function it is better to use a sorted Tree.
  * A Number that is easier to derive is the #Bins with Element Count exceeding 1.
  * 
  * TODO: add an Array of Element Counters per HashBin
  * to determine the Efficiency of the Hash Function.
  * 
  * Design Decisions: 
  * using an ArrayList instead of a Linked List is possible, 
  * but since there should be enough bins 
  * and removing an Item would be a more complex (and expensive) 
  * Operation, the Linked List is retained.  
  * Another Alternative is to jump ahead when the Bin is already blocked 
  * by an Item, until a free Slot is encountered. 
  * The Problem is when to stop jumping ahead on Searching!  
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: 790b2a4466e4029bf19095f0e665a20b4d215106da69780122ed7d18ca5c4774
  * stale: false
  * -->
  */
public class HashSet
extends AContainer {
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	/** Serialization version UID. */
	private static final long serialVersionUID = 1L;

	/** This Mask brings the HashCode to positive Values	*/
	final static public int HASH_MASK = Integer.MAX_VALUE;
	
	/** The default initial Capacity on instantiating an Array	 */
	public static int DEFAULT_CAPACITY = 11;
	
	/** The default load factor, i.e. Items per Bins, on instantiating a HashSet.	 */
	public static float DEFAULT_LOAD_FACTOR = 0.75f;
	
	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** The hash table data.	 */
	protected transient VectorObject[] mTable;
	
	/** Rehashes the table when count exceeds this Capacity.
	  * Pre-Calculated Helper Value */
	protected transient int mCapacity;
	
	/** Reference to the Iterator, identical to mEnum, but already cast */
	protected transient HashSetIterator iter;
	
	/** The load factor for the HashSet.	 */
	protected float mLoadFactor;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Default Value for the initial Number of Slots per Row	 */
	public static char DEFAULT_ROW_SIZE = 5; 
	/** Default Value for the Increment in Slots per Row	 */
	public static char DEFAULT_ROW_INCR = 3;
	
	/**Constructs a new, empty HashSet with the specified initial
	 * capacity and the specified load factor.
	 *
	 * @param	  initialCapacity   the initial capacity of the HashSet.
	 * @param	  loadFactor		a number between 0.0 and 1.0.
	 * @exception  IllegalArgumentException  if the initial capacity is less
	 *			   than or equal to zero, or if the load factor is less than
	 *			   or equal to zero.
	 */
	public HashSet(final int initialCapacity, final float loadFactor, final IEquivalence HashCode) {
		if ((loadFactor <= 0) || (initialCapacity <= 0) ||
			(loadFactor >= 1))
			throw new IllegalArgumentException();
		this.hashFn = HashCode;
		this.mLoadFactor = loadFactor;
		this.mCapacity = initialCapacity; //the individual Vectors should never be filled with more than a few Items.  
		this.mTable = VectorObject.FILLED_ARRAY((int) (initialCapacity / loadFactor), DEFAULT_ROW_SIZE, DEFAULT_ROW_INCR); //already enlarge the Table according to the Load Factor
		this.enm  = this.iter = new HashSetIterator(this); //, null);
	}

	/**Constructs a new, empty HashSet with the specified initial
	 * capacity and the specified load factor.
	 *
	 * @param	  initialCapacity   the initial capacity of the HashSet.
	 * @param	  loadFactor		a number between 0.0 and 1.0.
	 * @exception  IllegalArgumentException  if the initial capacity is less
	 *			   than or equal to zero, or if the load factor is less than
	 *			   or equal to zero.
	 */
	public HashSet(final int initialCapacity, final float loadFactor) {
		this(initialCapacity, loadFactor, null); }

	/**Constructs a new, empty HashSet with the specified Load Factor
	 * and defaults the initial Capacity factor to 'defaultCapacity'.
	 *
	 * @param	  loadFactor		a number between 0.0 and 1.0.
	 */
	public HashSet(final float loadFactor) {
		this(DEFAULT_CAPACITY, loadFactor, null); }

	/**Constructs a new, empty HashSet with the specified initial capacity
	 * and defaults the load factor to 'defaultLoadFactor'.
	 *
	 * @param   initialCapacity   the initial capacity of the HashSet.
	 */
	public HashSet(final int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR, null); }

	/**Constructs a new, empty HashSet with a default capacity and load factor.	 */
	public HashSet() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, null); }

	/**Constructs a new, empty HashSet with a default capacity and load factor.	 */
	public HashSet(final IEquivalence HashCode) {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, HashCode); }

	/**Constructs a new, empty HashSet with a default capacity and load factor.	 */
	public HashSet(final int initialCapacity, final IEquivalence HashCode) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR, HashCode); }

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		return new HashSet(mTable.length, mLoadFactor, hashFn); }

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** This is the basic Operation which has to be redefined in Bag and Relation	 */
	protected Object removeItemAt(final Object key, final int index)	{
		final VectorObject row = mTable[index];
		final int pos = row.find(key); 
		if (pos < 0)
			return null; 
		++major;
		--itemCount;
		return row.removeAt(pos); }

	/** Adds the Item to the HashSet at the Position 'index' with HashCode 'hash'
	  * given by the associated internal Iterator.
	  * This Optimization has to be implemented by all Containers,
	  * but currently only by HashSet.
	  * This is a potentially dangerous Operation, 
	  * because Item may not match the current Iterator Position!
	  */
	/*public synchronized Container addItemAtCurrPos (final Object _item) {
		return addItemAt(_item, iter.index); } //
	*/
	/**Adds the Item to the HashSet at Position 'index' with HashCode 'hash'.
	 * This is the basic Routine, which has to be redefined in Bag and Dictionary	 */
	protected synchronized Container addItemAt (final Object _item, final int index) {
		mTable[index].addItem(_item);
		if (++itemCount >= mCapacity) 
			rehash();  // Rehash the table if the Capacity is exceeded
//		++mMinor; //minor is the same as Major, since there is no 'In Place' Modification of a HashSet
		++major;
		return this; }

	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * Default Operation is addNext, which is easy to implement for Lists,
	  * as well as it ensures that the Item will be picked up by the current Iterator,
	  * which is frequently used e.g. by LL(1) Parsers.
	  * @see Order()
	  * @see nextItem()	 */
	public IIStreamOut addItem(final Object arg) { addAt(arg); return this; }

	////////////////////////////////////////////////////////////////////////////
	// Optimizations
	////////////////////////////////////////////////////////////////////////////

	/**Returns an Iterator of the values in this HashSet.
	 * Use the Iterator methods on the returned object to fetch the elements
	 * sequentially.
	 *
	 * @return  an Iterator of the values in this HashSet.
	 * @see	 java.util.Enumeration
	 * @see	 java.util.HashSet#keys()	 */
//	public synchronized Iterator Iterator() { return new HashIterator(this); }

	/**Increases the capacity of this HashSet, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument, without exceeding the LoadFactor.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	public int setCapacity(final int minCapacity) {
		if (mCapacity  <  minCapacity) 
			return rehash(minCapacity);
		return mCapacity; } //this.mTable.length * loadFactor; }

	/** Returns the current minimum capacity of this Array.
	  *
	  * @return  the current capacity of this Array.	 */
	public int getCapacity() { return mCapacity; }

	/**Rehashes the contents of the HashSet into a HashSet with a larger capacity.
	 * This method plainly doubles this HashSet's capacity.
	 */
	protected int rehash() { return rehash (mTable.length + mTable.length + 1);}	//double the Capacity!

	/**Rehashes the contents of the HashSet into a HashSet with a larger capacity.
	 * This method is called automatically when the number of keys in the HashSet
	 * exceeds this HashSet's capacity times load factor.
	 * This is a quite expensive Operation, because ALL Elements 
	 * need to be evaluated, so rather create your HashSet large enough.	  */
	protected int rehash(final int newCapacity) {
		final VectorObject[] oldTable = mTable; //cannot reuse the old Table!
		mTable = VectorObject.FILLED_ARRAY((int) (newCapacity / mLoadFactor), DEFAULT_ROW_SIZE, DEFAULT_ROW_INCR);
		mCapacity = newCapacity;
		//System.out.println("rehash old=" + oldCapacity + ", new=" + newCapacity + ", thresh=" + mCapacity + ", count=" + count);
		for (int i = oldTable.length; --i >= 0 ;) {
			final VectorObject old = oldTable[i]; 
			for (int j = old.getInt(); --j >= 0; ){	//Modulo Operation is really expensive!
				final Object o = old.getAt(j);	//
				final int index = (o.hashCode() & HASH_MASK) % mTable.length;	//need to recalculate HashValues!
				mTable[index].addItem(o); 
			}
		}
		iter.reSet();
//		iter.reStart(null);
		return newCapacity; }

	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
/*	public long reset(long Position) throws    NoSuchMethodException {
		iter.reStart(null); if (Position == 0) return 0; return iter.skip(Position); } //no external caller cares for a marked Object
//		return mEnum.reset(Position); } //

	/** Maps the specified <code>Item</code> to the specified
	  * <code>Item</code> in this HashSet. Neither the Item nor the
	  * Item can be <code>null</code>.
	  * <p>
	  * The Item can be retrieved by calling the <code>get</code> method
	  * with a Item that is equal to the original Item.
	  *
	  * @param	  Item	 the HashSet Item.
	  * @param	  Item   the Item.
	  * @return	 the previous Item of the specified Item in this HashSet,
	  *			 or <code>null</code> if it did not have one.
	  * @exception  NullPointerException  if the Item or Item is
	  *			   <code>null</code>.
	  * @see	 java.lang.Object#equals(java.lang.Object)
	  * @see	 java.util.HashSet#get(java.lang.Object)	 */
	/** Adds the Item, or flattens and adds its Contents when it is itself a streamIO.
	 * @param Item the Item to add; must not be null
	 * @return this HashSet
	 * @throws NullPointerException when Item is null */
	public synchronized ISemiGroup addAt(final Object Item) {
		if (Item == null)
			throw new NullPointerException();
		if (Item instanceof IIStreamIn) {
			addItems((IIStreamIn) Item); return this; }
		final int hash = (hashFn == null) ? Item.hashCode() : hashFn.HashCode(Item);
		final int index = (hash & HASH_MASK) % mTable.length;	//Modulo Operation is really expensive!
		return addItemAt(Item, index); }

	/**Flips the Item, i.e. when it is contained, remove it, otherwise add it.
	  * @return the removed Item, otherwise 'null'
	  * This corresponds to the XOR Operation.
	  * This Operation is optimized in this Class,
	  * because 'iter' still points to the correct Location */
	public Object flipItem(final Object Item) {
		if (iter.find(Item) != IStreamIn.EOI)
			return iter.removeCurr();
		addItemAt(Item, iter.index); //using 'mFilterHash' is an Optimization
		return null; }
	
	/** Replaces the given <code>Item</code> by the one specified.
	  * The Item can not be <code>null</code>.
	  * <p>
	  *
	  * @param	  Item	 the HashSet Item.
	  * @return	 the previous Item of the specified Item in this HashSet,
	  *			 or <code>null</code> if it did not have one.
	  * @exception  NullPointerException  if the Item or Item is
	  *			   <code>null</code>.
	  * @see	 java.lang.Object#equals(java.lang.Object)
	  * This Operation is optimized in this Class,
	  * because 'iter' still points to the correct Location */
	public Object replaceItem(final Object Item) {
		Object ret = null;
		if (iter.find(Item) != IStreamIn.EOI) {
			ret = iter.removeCurr();
			addItemAt(Item, iter.index); } //using 'mFilterHash' is an Optimization
		return ret; }

	/** Adds or replaces the given <code>Item</code> by the one specified.
	  * The Item can not be <code>null</code>.
	  * This is equivalent to the OR Operation
	  * <p>
	  *
	  * @param	  Item	 the HashSet Item.
	  * @return	 the previous Item of the specified Item in this HashSet,
	  *			 or <code>null</code> if it did not have one.
	  * @exception  NullPointerException  if the Item or Item is
	  *			   <code>null</code>.
	  * @see	 java.lang.Object#equals(java.lang.Object)
	  * @see flipItem() which removes the Item, when it exists (XOR Operation)
	  * @see replaceItem() which only replaces it, when it existed before (Id)
	  * @see removeItem() which only removes the Item (- Operation)
	  * @see addItem() which only adds the Item (+ Operation)
	  * This Operation is optimized in this Class,
	  * because 'iter' still points to the correct Location */
	public Object setItem(final Object Item, final boolean replace) {
		Object ret; //optimized setItem Method
		if ((ret = iter.find(Item)) != IStreamIn.EOI) {
			if (replace) {
				ret = iter.removeCurr();
				addItemAt(Item, iter.index); } } //using 'mFilterHash' is an Optimization

		//alternative setItem Method, reusing findFirst implicitly in remove()
/*		if (replace) ret = this.remove(Item); //replaces the Item
		else if ((ret = iter.findFirst(Item)) != StreamIn.EOI) return ret; //doesn't replace
		addItemAt(Item, iter.index, iter.mFilterHash);  //using 'mFilterHash' and 'index' is an Optimization

		//generic setItem Method
/*		Object ret = null;
		if (iter.findFirst(Item) != StreamIn.EOI) {
			ret = iter.removeCurr(); }
		addItem(Item);  //using 'mFilterHash' is an Optimization
*/		return ret; }


	/** Clears every Row of the backing Table.
	 * @return this, set to the Boolean Constant for the Representation of 'false' = 0
	  * i.e. not 'true'.
	  * For Conatainers this is equivalent to zeroAt() and clear()
	  * @see zeroAt()	 */
	public Boole FalseAt() {
		++major; 
		itemCount = 0;
		//mTable = new HashEntry[mTable.length];
		//this is slower but leaves the Array on the Heap
		for (int index = mTable.length; --index >= 0; )
			mTable[index].clear();	//This ripples the Garbage Collection along the linked List.
		return this; }

	/** Creates a shallow copy of this HashSet.
	  * The keys and values themselves are not cloned.
	  * This is a relatively expensive operation,
	  * but still cheaper than the Default Implementation,
	  * because the HashCodes and Capacities don't need to be re-calculated.
	  *
	  * @return  a clone of the HashSet.	 */
	public ICopy Copy() {
	try	{
		final HashSet t = (HashSet)super.clone();
		t.enm = t.iter = new HashSetIterator(t); //, null);
		t.mTable = new VectorObject[mTable.length];
		for (int i = mTable.length ; i-- > 0 ; )
			t.mTable[i] = mTable[i].copyVector();
		return t;
	} catch (final CloneNotSupportedException e) {	// this shouldn't happen, since the HashSet is Cloneable
		throw new OperationNotSupported(e.toString(), e); }
	}

	/**WriteObject is called to save the state of the HashSet to a stream.
	 * Only the keys and values are serialized since the hash values may be
	 * different when the contents are restored.
	 * iterate over the contents and write out the keys and values.	 */
/*	protected synchronized void writeObject(java.io.ObjectOutputStream s)
	throws IOException {	// Write out the length, Capacity, loadfactor
		s.defaultWriteObject();

		// Write out length, count of elements and then the Item/Item objects
		s.writeInt(mTable.length);
		s.writeInt(count);
		for (int index = mTable.length-1; index >= 0; index--) {
			HashEntry entry = mTable[index];
			while (entry != null) {
				s.writeObject(entry.Item);
				s.writeObject(entry.Item);
				entry = entry.next;
			}
		}
	}

	/**readObject is called to restore the state of the HashSet from
	 * a stream.  Only the keys and values are serialized since the
	 * hash values may be different when the contents are restored.
	 * Read count elements and insert into the HashSet.	  */
/*	protected synchronized void readObject(java.io.ObjectInputStream s)
		 throws IOException, ClassNotFoundException {	//
		s.defaultReadObject(); //Read in the length, Capacity, and loadfactor

		// Read the original length of the array and number of elements
		int origlength = s.readInt();
		int elements   = s.readInt();

		// Compute new size with a bit of room 5% to grow,
		// but no larger than the original size.  Make the length
		// odd if it's large enough, this helps distribute the entries.
		// Guard against the length ending up zero, that's not valid.
		int length = (int)(elements / loadFactor) + (elements / 20) + 3;
		if (length > elements && (length & 1) == 0) length--;
		if (origlength > 0 && length > origlength)  length = origlength;

		table = new HashEntry[length];
		count = 0;

		// Read the number of elements and then all the Item/Item objects
		for (; elements > 0; elements--) {
			java.lang.Object Item = s.readObject(); addAt(Item); }
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Optimizations
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests, whether this Object exists in the Set,
	  * @return the Object, when found, otherwise returns Stream.Iterator.EOL
	  * actually it is not necessary to override this Method,
	  * because you have to iterate over all List Members with identical Hash Modulus
	  * and this is very well restricted by the Method mEnum.setFilter() */
//	public Object findFirst(Object Item) { } //

	/**Returns the Degree of the given Object,
	 * i.e. how often it appears in the Container.
	 * This is done very fast for the Bag and used for the Relation / Tree
	 * to determine the (Out-)Degree of the Nodes.
	 */
	public int Degree(final Object _node) {
		int ret = 0;
		Object curr;
		for (final HashSetIterator iter = new HashSetIterator(this, _node);
			(curr = iter.nextItem()) != IStreamIn.EOI; ) {
			if ((_node == curr) || ((hashFn == null) ? _node.equals(curr) : hashFn.equals(_node, curr))) {
				++ret; } }
		return ret; }

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
//		try {
		System.out.println("Testing " + HashSet.class.getName());
		StreamOutPrimitive Out = new StreamOutPrimitive();
		HashSet A = new HashSet();
		HashSet B = new HashSet();
		AContainer.testIt(A);
		StringStreamIn Str1 = new StringStreamIn("ABCD");
		StringStreamIn Str2 = new StringStreamIn("BDEF");
		Out.println("Str1 = " + Str1);
		Out.println("Str2 = " + Str2);
		A.union(Str1);
		A.union(Str2);
		B.copyAt(A);
		Out.println("Str1 OR Str2 = " + B);
		Out.println("... DIFFat(Str1) = " + A.DIFFat(Str1));
		Out.println("... DIFFat(Str1) = " + A.DIFFat(Str1) + "(no Effect)");
		Out.println("... DIFFat(Str2) = " + A.DIFFat(Str2) + "(empty)");
//		Out.println("0 = " + A.zeroAt()); //no longer necessary...
		A.add  (Str1);
		A.add  (Str2);
		Out.println("Str1 + Str2 = " + A);
		Out.println("... subAt(Str1) = " + A.subAt(Str1));
		Out.println("... subAt(Str2) = " + A.subAt(Str2) + " (empty)");
//		Out.println("... subAt(Str2) = " + A.subAt(Str2) + " (results in an Exception)");
//		Str1.reset();
		A.copyAt(Str1);
//		Str2.reset();
		B.copyAt(Str2);
		Out.println("(" + A + ") * (" + B + ") = " + A.mul(B));
		//How is a Division defined?
		//by an Equivalence Relation, i.e. a Forest / HashMap, i.e. a normal Mapping.
		//a Product is also a 'normal' mapping resulting in Pairs or Maps.
//		} catch (NoSuchMethodException x) {}
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}

/** Iterator for a HashSet
 * actually this is a chained Iterator over individual Row Iterators.
 * @author heuerm
 *
 * <!-- docstate
 * tags: [code/container, code/hash_table, code/container_iteration]
 * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: 963d75fe1c2cccc4eac3dcf27cc66361770080963c4f1265f08cdc7551d6963a
 * stale: false
 * -->
 */
class HashSetIterator
extends AEnumerator {

	/** Reference to the HashSet iterated over	 */
	final HashSet set; 

	/** Index to the current Row	 */
	int index; 

	/** Index to the current Item within the current Row; -1 before the first one	 */
	int col = -1;

	/** Reference to the Item returned by the latest nextItem()	 */
	Object currItem = IStreamIn.EOI;

	/** Number of Items already returned by nextItem()	 */
	long position;
	
	/** Initializing Constructor	 */
	public HashSetIterator(final HashSet _set, final Object _node) {
		super(_set); 
		this.set = _set; 
		if (_node != null) 
			setFilter(_node); 
	}

	/** Initializing Constructor	 */
	public HashSetIterator(final HashSet _set) {
		this(_set, null); 
	}

	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(final long Position) {
		reSet(); 
		if (Position == 0) return 0; 
		return jump(Position);  }
	
	/** Reports 1 while another Item follows in this or a later Row, 0 once exhausted.
	 * @see streamIO.object.AStreamIn#availAble()	 */
	public long availAble() {
		for (int i = index, c = col; i < set.mTable.length; ++i, c = -1) {
			if (++c < set.mTable[i].getInt()) 
				return 1; }
		return 0;
	}

	/** Advances to the next Item of the backing Table, walking the Rows in Order.
	 * @see streamIO.object.AStreamIn#nextItem()	 */
	public Object nextItem() {
		while (index < set.mTable.length) {
			final VectorObject row = set.mTable[index];
			if (++col < row.getInt()) {
				++position;
				return currItem = row.getAt(col); }
			col = -1; ++index;
		}
		return currItem = IStreamIn.EOI;
	}

	/** Returns the Item returned by the latest nextItem().
	 * @see streamIO.object.AStreamIn#currItem()	 */
	public Object currItem() {
		return currItem;
	}

	/** Removes the Item returned by the latest nextItem() from its Row.
	 * @see streamIO.object.enumer.Enumerator#removeCurr()	 */
	public Object removeCurr() { //throws ModificationException {
		if ((col < 0) || (index >= set.mTable.length)) 
			return IStreamIn.EOI;
		final Object ret = set.mTable[index].removeAt(col--); //the Row shrinks, so step back
		--set.itemCount; ++set.major; ++major;
		currItem = IStreamIn.EOI;
		return ret;
	}

	/** Replaces the Item returned by the latest nextItem() within its Row.
	 * @see streamIO.object.enumer.ChangeIterator#replaceCurr(java.lang.Object)	 */
	public Object replaceCurr(Object Item) {
		if ((col < 0) || (index >= set.mTable.length)) 
			return IStreamIn.EOI;
		currItem = Item;
		return set.mTable[index].setAt(col, Item);
	}
	
	/** Resets the Iterator to the Start of the backing Table.
	  * @return this Iterator	 */
	public IReSetAble reSet(){
		index = 0; col = -1; position = 0;
		currItem = IStreamIn.EOI;
		return this;
	}
	
	
	/** Reports the Number of Items this Iterator can be reSet over.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()
	 */
	public long getMaxMarkSize() {
		return set.itemCount;
	}

	/** Reports the Number of Items already returned by nextItem().
	 * @see streamIO.object.AStreamIn#getPosition()
	 */
	public long getPosition() {
		return position;
	}

}