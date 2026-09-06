package streamIO.object.enumer;

import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.object.ModificationException;

/**
 * Title:        AIndexEnumerator<p>
 * Description:  Implements some Methods that are defined inherently<p>
 * Base Class for several Array and ResultSet Enumerators. 
 * Copyright:    Copyright (c) Matthias Heuer<p>
 * Company:      personal<p>
 * @author 		 Matthias Heuer
 * @version 1.0
 * @stereotype enumeration
 * <!-- docstate
 * tags: [code/enumerator, code/iterator_adapter]
 * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public abstract class AIndexEnumerator
extends AReverseEnumerator
implements IndexEnumerator {
	
	/** Creates an index-based Enumerator over the given versioned Container.
	 * @param _container a versioned container backing this Enumerator. Null allowed
	 */
	public AIndexEnumerator(final IAlterAble _container) { super(_container); }
	
	///////////////////////////////////////////////////////////////////////////
	//	Members
	///////////////////////////////////////////////////////////////////////////
	
	/** Counter for the current Position,
	  * since a PreIncrement is used on nextItem()	 */
	protected int curr = -1;
	
	/** Index for the mark()ed Position	 */
	protected int mark = -1;
	
	///////////////////////////////////////////////////////////////////////////
	//	abstract Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Inserts Item at the given absolute Index, to be implemented by subclasses.
	 * @see streamIO.object.enumer.IndexEnumerator#addAt(int, java.lang.Object)	 */
	abstract public IndexEnumerator addAt(final int Index, final Object Item)
			throws ModificationException;

	/** Removes and returns the Item at the given absolute Index, to be implemented by subclasses.
	 * @see streamIO.object.enumer.IndexEnumerator#removeAt(int)	 */
	abstract public Object removeAt(final int Index) throws ModificationException;

	/** Replaces the Item at the given absolute Index, to be implemented by subclasses.
	 * @see streamIO.object.enumer.IndexEnumerator#setAt(int, java.lang.Object)	 */
	abstract public Object setAt(final int Index, final Object Item);
	
	/**Returns the Item at the given absolute Position
	 * While this is possible in principle for all Enumerators,
	 * it is too ineffective to loop through the whole Enumerator
	 */
	abstract public Object getAt(final int Index); // { return null; }
	
	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * That is why this Method should throw an exception if removing is not allowed.   */
	//abstract public Object removeAt(); // { return null; }; //
	
	/** Returns the total Size of the underlying Array or Store, to be implemented by subclasses.
	  * @return the total Number of Objects in this Enumerator / Container
	  * For Random Access Stores this is definitely limited and can thus be returned.
	  */
	abstract public int getInt(); // { return 0; }

	///////////////////////////////////////////////////////////////////////////
	//	Methods
	///////////////////////////////////////////////////////////////////////////

	/** Returns the current Position relative to the last mark()ed Position.
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return curr-mark; }
	
	/** Computes the remaining Items from the total Size and the current Position.
	  * @return the Number of minimum available Objects
	  * Since this is dependent on the total Number of Items,
	  * it cannot be programmed here,
	  * except by introducing the abstract Function
	  * that returns the total Number of Items.   */
	public long availAble() { return getInt() - curr -1; }
	
	/** Returns the total Size, since the whole underlying Array or Store can be replayed.
	 * @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return getInt(); }
	
	/** Returns the current Object:
	  * Returning the cached currItem is faster! 	*/
	public Object currItem() { return getAt(curr); }
	
	/**Resets the Enumerator to the last marked Position,
	 * done automatically on Instantiation	 */
	public long reSet(final long Position) { //throws NoSuchMethodException {
		curr = mark + (int) Position; return Position; }
	
	/**Resets the Enumerator to the last marked Position,
	 * done automatically on Instantiation	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		curr = mark; return this; }
	
	/**Marks the current position in this Enumerator.
	 * A subsequent call to the reset method repositions this Enumerator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources but ignored here	 */
	public IMarkAble mark(final long ReadLimit) { //throws NoSuchMethodException {
		mark = curr; return this; }
	
	/** Returns the current Position, since that many Items precede it.
	  * @return the number of Items that are to be reached by previousItem.	 */
	public long availableBefore() { return curr; }

	/**Returns the next Object: 	 */ //prevent incrementing currItem above Limit
	public Object nextItem() { return getAt( ++curr); }

	/**Returns the next Object: 	 */ //prevent decrementing currItem below 0
	public Object prevItem() { return getAt( --curr); }

	/**Returns the Item at the given relative Position	 */
	public Object getRel(int Index) { return getAt(curr + Index); }

	/**Skips over and discards n Items from this Enumerator.
	 * Returns the actual number of Items skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(long Position) {
		curr += Position;
		return Position; }

	///////////////////////////////////////////////////////////////////////////
	//	Methods
	///////////////////////////////////////////////////////////////////////////

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 * @throws ModificationException if the Container is sorted or read only
	 */
	public Object replaceNext(Object arg) { //throws ModificationException {
		return setAt(curr+1, arg); }

	/** Replaces the previous Object in the Set and Iteration with the given one,
	  * and returns the replaced Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  * @throws ModificationException if the Container is sorted or read only
	  */
	public Object replacePrev(Object arg) { //throws ModificationException {
		return setAt(curr-1, arg); }

	/** Replaces the previous Object in the Set and Iteration with the given one,
	  * and returns the replaced Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  * @throws ModificationException if the Container is sorted or read only
	  */
	public Object replaceCurr(Object arg) { //throws ModificationException {
		return setAt(curr, arg); }

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 * @throws ModificationException if the Container is read only
	 */
	public Object removeNext() throws ModificationException {
		return removeAt(curr+1); }

	/**Removes the next Object from the Set and Iteration,
	 * returns the removed Item,
	 * this makes it necessary to define it separately,
	 * because it returns more Information: whether the Item was found or not!
	 * @throws ModificationException if the Container is read only
	 */
	public Object removePrev() throws ModificationException {
		return removeAt(curr-1); }

	/** Removes the current Object from the Container with this Enumerator knowing it.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Therefore the Version of the Container is updated.
	  * @throws ModificationException if the Container is read only
	  * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	  * Could also return a boolean whether the Method is supported or not */
	public Object removeCurr() throws ModificationException {
		return removeAt(curr); }

	/** Adds the Object at the current Object from the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * @throws ModificationException if the Container is sorted or read only
	  * Could also return a boolean whether the Method is supported or not */
	public ReverseEnumerator addCurr(Object Item) throws ModificationException {
		return addAt(curr, Item); }

	/** Adds the Object after the current Object from the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * @throws ModificationException if the Container is sorted or read only
	  * Could also return a boolean whether the Method is supported or not */
	public Enumerator addNext(Object Item) throws ModificationException {
		return addAt(curr+1, Item); }

	/** Adds the Object before the current Object from the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * @throws ModificationException if the Container is sorted or read only
	  * Could also return a boolean whether the Method is supported or not */
	public ReverseEnumerator addPrev(Object Item) throws ModificationException {
		return addAt(curr-1, Item); }

	///////////////////////////////////////////////////////////////////////////
	//  relative Operations
	///////////////////////////////////////////////////////////////////////////

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * @throws ModificationException if the Container is sorted or read only
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not */
	public Object removeRel(int Index) throws ModificationException {
		return removeAt(curr + Index); }

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException if the Container is sorted or read only
	 */
	public Object replaceRel(int Index, Object Item) throws ModificationException {
		return setAt(curr + Index, Item); }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException if the Container is sorted or read only
	 * Could also return a boolean whether the Method is supported or not */
	public IndexEnumerator addRel(int Index, Object Item) throws ModificationException {
		return addAt(curr + Index, Item); }

	/**Returns the index of the first occurrence of the specified object in
	 * this Array.
	 *
	 * @param   elem   the desired component.
	 * @return  the index of the last occurrence of the specified object in
	 *		  this Array; returns <code>-1</code> if the object is not found.	 */
	public int firstIndexOf(Object elem) {
		return firstIndexOf(elem, -1);}

	/**Returns the index of the last occurrence of the specified object in
	 * this Array.
	 *
	 * @param   elem   the desired component.
	 * @return  the index of the last occurrence of the specified object in
	 *		  this Array; returns <code>-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem) {
		return lastIndexOf(elem, (int) availAble ()); } // getInt()); }

	/**Searches forwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   lower   the index to start searching from.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>-1</code> if the object is not found.	 */
	public int firstIndexOf(Object elem, int lower) {
		return firstIndexOf(elem, lower, (int) availAble ()); } // getInt()); }

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem, int upper) {
		return lastIndexOf(elem, -1, upper);}

	/**Searches forwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   lower   the index to start searching from.
	 * @param   upper   the index to stop  searching at.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>stop+1</code> if the object is not found.	 */
	public int firstIndexOf(Object elem, int lower, int upper) {
		int i = lower;
		while (++i < upper)
			if (elem.equals(getAt(i))) break;
		return i; }

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from.
	 * @param   lower   the index to stop  searching at.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>stop-1</code> if the object is not found.	 */
	public int lastIndexOf(Object elem, int lower, int upper) {
		int i = upper;
		while (--i > lower)
			if (elem.equals(getAt(i))) return i; //break; //equivalent...
		return i; }

}
