package streamIO.object.enumer.container;

import java.util.ConcurrentModificationException;

import streamIO.IReSetAble;
import streamIO.exception.OperationNotSupported;
import streamIO.object.IStreamIn;
import streamIO.object.enumer.AEnumerator;
import synch.ValidationRule;
import tester.ITester;
import function.IProcessor;

/** A HashTable enumerator class.
  * This Iterator can either iterate through all Elements of this HashContainer
  * or only through those equivalent to Object 'only'.
  * This class should remain opaque to the client.
  * It will use the Enumeration interface.
  *
  * Since HashContainer is almost always used for unique Storage of Items,
  * Filtering in this Iterator could be simplified by relying on the Fact
  * that there is always at most one equivalent Instance in the Container.
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
final public class HashIterator
extends AEnumerator {
	
	////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the HashContainer	 */		protected HashContainer hashTable;
	/** Current Number of Items found	 */		protected int numFound;
	/** Next	 Entry	 */						protected HashEntry next;
	/** Current  Entry	 */						protected HashEntry curr;
	/** Previous Entry	 */						protected HashEntry prev;
	/** Item of the current  Entry	 */			//protected Object currItem;
	/** current Position in the HashArray */	protected int currRow;
	
	///////////////////////////////////////////////////////////////////////////
	
	/** full HashCode of the Item searched*/	protected int filterHash;
	/** Index of the Item searched */			protected int filterBin;
	/** Type of the Item searched */			protected Object filterTyp; 
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/**initializing Constructor	 */
	public HashIterator(final HashContainer _table) {
		this(_table, null, null); }
	
	/**initializing Constructor	 */
	public HashIterator(final HashContainer _table, final Object _filter, final Object _typ) {
		super(_table); 
		hashTable = _table; setFilter(_filter, _typ); }
	
	////////////////////////////////////////////////////////////////////////////
	// Operations
	////////////////////////////////////////////////////////////////////////////
	
	/** This Iterator returns Items unsorted, by HashCode Bucket order.
	 * @return the Sort Order this Iterator returns the Items with
	  * This HashIterator returns the Items sorted by a Modulus of their HashCode
	  * which basically means 'unsorted'	*/
	public byte getOrder() { return ORDER_NONE; }
	
	/** Restart the Iterator, done automatically on Instantiation
	  * If mFilter == null, loops over all Items,
	  * otherwise only over those that equal 'mFilter'
	  */
	public void setFilter(final Object _filter) { setFilter(_filter, null); }
	
	/** Restart the Iterator, done automatically on Instantiation
	  * If mFilter == null, loops over all Items,
	  * otherwise only over those that equal 'mFilter'
	  */
	public void setFilter(final Object _filter, final Object _typ) {
		if ((this.filter = _filter) == null) { 
			reSet(); return; } // this; }
		filterHash  = (hashTable.hashFn == null) ? filter.hashCode() : hashTable.hashFn.HashCode(filter);
		filterBin = (filterHash & HashContainer.HASH_MASK) % hashTable.bins.length;	//
		filterTyp = _typ; 
		setFilter(); }
	
	/** Restarts the Iterator, using the same filter. 
	 *  Loops only over those that equal 'filter'
	 */
	public void setFilter() {
		resetVersion(hashTable); 
		numFound = 0;
		curr = null;
		next = hashTable.bins[currRow = filterBin]; //
//		return this; 
	}
	
	/**Restart the Iterator, done automatically on Instantiation	 */
	public IReSetAble reSet(){ //throws OperationNotSupported{
		resetVersion(hashTable);
		numFound = 0; 
		currRow = hashTable.bins.length;
		filter = next = curr = null; //unmark the Restriction!
		return this; }
	
	/**Returns the number of Items left in the HashContainer
	 * Since you know the number of Items in the HashContainer, this is reliable, 
	 * but only if no 'mFilter' Object is given!	 */
	public long availAble() {
		if (filter == null) 
			return hashTable.itemCount - numFound;
		return (next != IStreamIn.EOI) ? 1 :
			   (curr != IStreamIn.EOI) ? 0 : -1; } //only a single List!
	
	/** Returns the current Bin/Row Index within the HashContainer's Bucket Array.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return currRow; }

	/** Returns the HashContainer's total Item Count, since replay covers the whole Table.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return hashTable.getInt(); }

	/** Returns the current Item without advancing.
	 * @return  the current Item without Increment	 */
	public Object currItem() { return curr; }

	/** Returns the current Entry without advancing.
	 * @return  the current Entry without Increment	 */
	public IndexAssociation currEntry() { return curr; }

	/** Advances to and returns the next Item, delegating to {@link #nextEntry()}.
	 * @return  the next Item with PostIncrement
	  * If an mFilter Item was specified, the nextItem() is automatically filtered! */
	public Object nextItem() { return  nextEntry(); }

	/** Advances to and returns the next Entry, honoring the active filter (if any).
	 * @return  the next Entry with PostIncrement
	  * If an mFilter Item was specified, the nextEntry() is automatically filtered! */
	public IndexAssociation nextEntry() { //ByRefLong moreItems) {
		if (hashTable.major != this.major)
			throw new ConcurrentModificationException("HashTable has been modified in Structure "+(hashTable.major - this.major)+"-times!");
		if (filter == null) {  //don't change the Index, when only a certain Object is wanted!
			while ((next == null) && (--currRow >= 0)) { //Jump over empty Rows in the Table
				curr = null; next = hashTable.bins[currRow]; } 
		}
		for(;;) { //loop within a Row
			++numFound; //increment Number of found Items
			if (next == null) 
				return curr = (HashEntry) IStreamIn.EOI; // null;//	throw new NoSuchElementException("HashtableEnumerator");
			prev = curr; curr = next; next = next.next; //currItem = curr.key;
			//split it up, too complicated otherwise:
			if (filter == null) break; //faster Check!
			if (filterHash != curr.hash) continue;
			if(!ValidationRule.EQUALS(filterTyp, curr.typ, hashTable.hashFn)) continue; //maybe the Type has to be identical, not only equal! 
			if (filter == curr.key) break;
			if (hashTable.hashFn == null) {
				if (ValidationRule.EQUALS(filter, curr.key)) break; //possibly the Key does not support the equals() Operation on the Value
				//if (curr.key.equals(filter)) break; //so also try it vice versa, ...
			} else { //...although the Convention is that equals() returns false, when the Classes differ...
				if (hashTable.hashFn.equals(filter, curr.key)) break;
			}
		}
		return curr; 
	}

	/** Returns the first occurence of the specified Item in this HashContainer.
	  *
	  * @param   _key   possible Item.
	  * @return  <code>true</code> if the specified object is a Item in this
	  *		  HashContainer; <code>false</code> otherwise.
	  * @see	 java.util.HashContainer#contains(java.lang.Object)	 */
	public Object findNext(final Object _key) { return findNextEntry(_key); }

	/** Returns the first occurence of the specified Item in this HashContainer.
	  *
	  * @param   _key   possible Item.
	  * @return  <code>true</code> if the specified object is a Item in this
	  *		  HashContainer; <code>false</code> otherwise.
	  * @see	 java.util.HashContainer#contains(java.lang.Object)	 */
	public IndexAssociation findNextEntry(final Object _key) { return findNext(_key, (Object) null); }

	/** Returns the first occurence of the specified Item in this HashContainer.
	  *
	  * @param   _key   possible Item.
	  * @return  <code>true</code> if the specified object is a Item in this
	  *		  HashContainer; <code>false</code> otherwise.
	  * @see	 java.util.HashContainer#contains(java.lang.Object)	 */
	public IndexAssociation findNext(final Object _key, final Object _typ) {
		if (filter != _key)
			setFilter(_key, _typ);
		return nextEntry(); 
	}
	
	/**Tests, whether this Object exists in the Set,
	 * @return the Object, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findFirst (final Object _key) { return findFirstEntry(_key); }
	
	/**Tests, whether this Object exists in the Set,
	 * @return the Object, when found, otherwise returns streamIO.Iterator.EOL	 */
	public IndexAssociation findFirstEntry (final Object _key) { return findFirst (_key, (Object) null); }
	
	/**Tests, whether this Object exists in the Set,
	 * @return the Object, when found, otherwise returns streamIO.Iterator.EOL	 */
	public IndexAssociation findFirst (final Object _key, final Object _typ) { //throws NoSuchMethodException {
		if (filter != _key) {
			setFilter(_key);
		} else {
			curr = null; next = hashTable.bins[currRow]; 
		}
		return nextEntry(); }
	
	/**Replaces the current Item in the Iterator with the given one	 */
	public Object replaceCurr(final Object _value) throws OperationNotSupported {
		//throw new OperationNotSupported("Keys in a HashMap cannot be modified, only deleted.");
		final Object ret = curr.val; curr.val = _value; 
		return ret; 
	}
	
	/** Removes the current Object from the Container with this Iterator knowing it.
	  * @return the removed Object
	  * The remaining Problem is other Iterators that concurrently work through this. */
	public Object removeCurr() { //ByRefLong moreItems) {
		final HashEntry prv = prev;
		final Object ret = hashTable.removeItemAt(curr, prev, currRow);
		curr = prv; //prevent missing Items on Removal from the Front!
		return ret; }
	
	/** Performs the Operation of the Operator on each Item in the Collection
	  * that equals this Item. The generic Solution is slow
	  * and can be highly optimized in concrete Implementations.
	  * In a HashContainer all Items that are equal (and some other) must be in a row,
	  * so they can be quickly located and operated on. */
	public int forEachThatEquals(final Object _key, final Object _typ, final IProcessor op) {
		if (op   == null) return 0;
		if (_key == null) return 0;
		int ret = 0;
		findFirst(_key, _typ);
		while(nextEntry() != IStreamIn.EOI) {
			op.MapAt(curr); ++ret; }
		return ret; }
	
	/** Returns the first Item of the Collection that equals this Item,
	  * that also fulfills the Test of the ITester Object.
	  * In a HashContainer all Items that are equal (and some more) must be in a row,
	  * so they can be quickly located and operated on.
	  * It is faster and easier to use the HashTableIterator!!! */
	public Object firstOfEachThatEqualsThat(final Object _key, final Object _typ, final ITester tester) {
		if (tester == null) return null;
		if (_key   == null) return null;
		findFirst(_key, _typ);
		while(nextItem() != IStreamIn.EOI) {
			if (tester.test(curr))
				return curr;	//The last Test is neccessary, but not sufficient and also not faster!
//		  else return null; //don't return before tested all Elements!!!
		} return curr; } //Iterator.EOI; }
	
	/** Workaround to avoid the Evaluation of the Iterator during Debugging */
	public String toString() {
		return Integer.toString(this.numFound); 
	}
	
}
