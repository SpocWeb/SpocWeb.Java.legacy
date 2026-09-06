package streamIO.object.enumer.container;

import streamIO.IIStreamIn;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.ISemiGroup;
import streamIO.object.ModificationException;
import streamIO.object.enumer.IndexEnumerator;
import streamIO.object.enumer.ReverseEnumerator;

/**
  * Implements the Operations of a Random Access Container
  * based on it't abstract Operations.
  *
  * The Items can be accessed by an integer Index ranging from 0 to getInt()-1.
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: a81df4bb0e8b81584163459a9187946540682555507ecc5264ac01c1e288c9f7
  * stale: false
  * -->
  * The Iterator Methods are delegated to mEnum, THE (single) Iterator for this Container. */
public abstract class ARAContainer
extends AContainer
implements RAContainer {
	
	/** Removes and returns the Item at the given Position, to be implemented by subclasses.
	 * @see streamIO.object.enumer.IndexEnumerator#removeAt(int)	 */
	abstract public Object removeAt(final int _index);

	/** Inserts an Item at the given Position, to be implemented by subclasses.
	 * @see streamIO.object.enumer.IndexEnumerator#addAt(int, java.lang.Object)	 */
	abstract public IndexEnumerator addAt(final int _index, final Object _item);

	/** Returns the current allocated Capacity, to be implemented by subclasses.
	 * @see streamIO.object.enumer.container.Container#getCapacity()	 */
	abstract public int getCapacity();

	/** Ensures at least the given minimum Capacity, to be implemented by subclasses.
	 * @see streamIO.object.enumer.container.Container#setCapacity(int)	 */
	abstract public int setCapacity(final int minCapacity);

	/** Creates a new, empty Instance of the concrete Subclass, to be implemented by subclasses.
	 * @see streamIO.copy.IICopyAble#newInstance()	 */
	abstract public ICopyAble newInstance();

	/** Returns the Item at the given Position, to be implemented by subclasses.
	 * @return the Object at the given Position in this Enumeration
	  * The Result depends on whether the Iterator is deterministic
	  * and supports these Operations
	  * Resolves the Conflict between AStreamSet.getAt(int)
	  * and IndexEnumerator.getAt(int) throwing no Exception. */
	abstract public Object getAt(final int _position); //throws ModificationException {
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the index of the first occurrence of the specified object in
	 * this Array.
	 *
	 * @param   elem   the desired component.
	 * @return  the index of the last occurrence of the specified object in
	 *		  this Array; returns <code>-1</code> if the object is not found.	 */
	public int firstIndexOf(final Object elem) {
		return firstIndexOf(elem, -1);}
	
	/**Returns the index of the last occurrence of the specified object in
	 * this Array.
	 *
	 * @param   elem   the desired component.
	 * @return  the index of the last occurrence of the specified object in
	 *		  this Array; returns <code>-1</code> if the object is not found.	 */
	public int lastIndexOf(final Object elem) {
		return lastIndexOf(elem, getInt());}

	/**Searches forwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   lower   the index to start searching from.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>-1</code> if the object is not found.	 */
	public int firstIndexOf(final Object elem, final int lower) {
		return firstIndexOf(elem, lower, getInt());}

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>-1</code> if the object is not found.	 */
	public int lastIndexOf(final Object elem, final int upper) {
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
	public int firstIndexOf(final Object elem, final int lower, final int upper) {
//		try {
			for (int i = lower-1; ++i < upper;)
				if (elem.equals(getAt(i))) 
					return i; 
//		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); }
		return upper; }

	/**Searches backwards for the specified object, starting from the
	 * specified index, and returns an index to it.
	 *
	 * @param   elem	the desired component.
	 * @param   upper   the index to start searching from.
	 * @param   lower   the index to stop  searching at.
	 * @return  the index of the last occurrence of the specified object in this
	 *		  Array at position less than <code>index</code> in the Array;
	 *		  <code>stop-1</code> if the object is not found.	 */
	public int lastIndexOf(final Object elem, final int lower, int upper) {
//		try {
			for (int i = upper; --i >= lower;)
			if (elem.equals(getAt(i))) 
				return i; 
//		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); }
		return lower-1; }
	
	/**Sets (adds or replaces) the component at the specified index.
	 * All other components in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * Replaces the component at the specified index.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  Item	the component to set (add or replace).
	 * @param	  index   the index of the object to remove.
	 * @return	 the component replaced by 'Item'.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public Object setAt(final int index, final Object Item) {
		final Object tmp; 
		if ((tmp = removeAt(index)) != IIStreamIn.EOI)
			addAt(index, Item);
		return tmp; }

	/**Deletes the component at the specified index. Each component in
	 * this Array with an index greater or equal to the specified
	 * <code>index</code> is shifted downward to have an index one
	 * smaller than the value it had previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Array.
	 *
	 * @param	  index   the index of the object to remove.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
/*	public RAContainer subAt(final int index) {
		removeAt(index); return this; } */

	/**Adds the specified component to the end of this Array,
	 * increasing its size by one. The capacity of this Array is
	 * increased if its size becomes greater than its capacity.
	 *
	 * @param   obj   the component to be added.	 */
	public synchronized ISemiGroup addAt(final Object obj) {
		addAt(getInt(), obj); return this; } //adds it at the End (getInt())

	/** Delegates to the single Iterator's own availableBefore() Count.
	  * @return the minimum Number of Items (in the Buffer)
	  * that are to be reached by previousItem.
	  * Should be called again on reaching this Number,
	  * because the Buffer may be filled up by then.
	  */
	public long availableBefore() {
		return ((IndexEnumerator) enm).availableBefore(); }

	/**Returns the previous Object:	 */
	public Object prevItem() {
		return ((IndexEnumerator) enm).prevItem(); }

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not
	 * @return the previous Item in the Container
	 * @throws ModificationException when the Container is read only
	 */
	public Object removePrev() throws ModificationException {
		return ((IndexEnumerator) enm).removePrev(); }

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * @param Object to replace the previous one in the Container
	 * @return the previous Object for later use
	 * @throws ModificationException when the Container is sorted or read only
	 */
	public Object replacePrev(final Object Item) { //throws ModificationException {
		return ((IndexEnumerator) enm).replacePrev(Item); }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * Could also return a boolean whether the Method is supported or not
	 * @param Object to be added at the current Position
	 * @return the Enumerator to allow for concatenated Adding
	 * @throws ModificationException when the Container is sorted or read only
	 */
	public ReverseEnumerator addPrev(final Object Item) throws ModificationException {
		return ((IndexEnumerator) enm).addPrev(Item); }

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not
	 * @return the current Object (returned by the latest nextItem())
	 * @throws ModificationException when the Container is read only
	public Object removeCurr() throws ModificationException;

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * @param Object to be replaced at the current Position
	 * @return the Object at the current Position
	 * @throws ReadOnlyException when the Container is read only
	 * @throws NoSuchMethodException when the Container is sorted
	 */
//	public Object replaceCurr(final Object Item) { // throws NoSuchMethodException {

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that adding the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if adding is not allowed.
	 * Could also return a boolean whether the Method is supported or not
	 * @param Object to be added at the current Position
	 * @return the Enumerator to allow for concatenated Adding
	 * @throws ModificationException when the Container is sorted or read only
	 * @throws NoSuchMethodException when the Container is sorted or read only
	 */
	public ReverseEnumerator addCurr(final Object Item) throws ModificationException {
		return ((IndexEnumerator) enm).addCurr(Item); }

	/**Returns the Item at the given absolute Position
	 * While this is possible in principle for all Enumerators,
	 * it is too ineffective to loop through the whole Enumerator
	 * @param Index of the Item to be retrieved
	 * @return the Item at Position Index, null, if beyond the Borders
	 */
//	public Object getAt(final int Index);

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not
	 * @param Index of the Item to be removed
	 * @return the Item removed, null, if beyond the Borders
	 * @throws ModificationException if the Container is read only
	 */
//	public Object removeAt(final int Index) throws ModificationException;

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @param Index of the Item to be replaced
	 * @return the Item replaced, null, if beyond the Borders
	 * @throws ModificationException if the Container is sorted or read only
	 * @throws NoSuchMethodException if the Container is sorted or read only
	 */
//	public Object replaceAt(int Index, Object Item) { //throws NoSuchMethodException {

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Could also return a boolean whether the Method is supported or not
	 * @param Index of the Item to be added
	 * @return the Enumerator to enable concatenated adding.
	 * @throws NoSuchMethodException if the Container is sorted or read only
	 * @throws ModificationException if the Container is sorted or read only
	 */
//	public IndexEnumerator addAt(int Index, Object Item) throws NoSuchMethodException;

	/** Returns the Item at the given relative Position
	  * @param Relative Index of the Item to be retrieved
	  * @return the Item at the relative Position, null, if beyond the Borders
	  */
	public Object getRel(int Index) {
		return ((IndexEnumerator) enm).getRel(Index); }

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not
	 * @param Relative Index of the Item to be removed
	 * @return the Item removed, null, if beyond the Borders
	 * @throws ModificationException if the Container is read only
	 */
	public Object removeRel(int Index) throws ModificationException {
		return ((IndexEnumerator) enm).removeRel(Index); }

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @param relative Index of the Item to be replaced
	 * @return the Item replaced, null, if beyond the Borders
	 * @throws NoSuchMethodException if the Container is sorted or read only
	 * @throws ModificationException if the Container is sorted or read only
	 */
	public Object replaceRel(int Index, Object Item) throws ModificationException {
		return ((IndexEnumerator) enm).replaceRel(Index, Item); }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Could also return a boolean whether the Method is supported or not
	 * @param relative Index of the Item to be added
	 * @return the Enumerator to enable concatenated adding.
	 * @throws NoSuchMethodException if the Container is sorted or read only
	 * @throws ModificationException if the Container is sorted or read only
	 */
	public IndexEnumerator addRel(int Index, Object Item) throws ModificationException {
		return ((IndexEnumerator) enm).addRel(Index, Item); }

	/**
	  * Sets the Iterator behind the last Position.
	  * This is the Opposite to reset()
	  * just like previous() the Opposite to next()
	  */
	public ReverseEnumerator preset() {//throws NoSuchMethodException {
		 ((ReverseEnumerator)enm).preset();
		 return this; }

	//the following Methods assume a connex Order on the Keys, i.e. int

	/**Returns the index of the first occurrence of the specified object in
     * this Array.
     *
     * @param   elem   the desired component.
     * @return  the index of the last occurrence of the specified object in
     *          this Array; returns <code>-1</code> if the object is not found.
     */
//	public int firstIndexOf(Object elem);

    /**Returns the index of the last occurrence of the specified object in
     * this Array.
     *
     * @param   elem   the desired component.
     * @return  the index of the last occurrence of the specified object in
     *          this Array; returns <code>-1</code> if the object is not found.
     */
//	public int lastIndexOf(Object elem);

    /**Searches forwards for the specified object, starting from the
     * specified index, and returns an index to it.
     *
     * @param   elem    the desired component.
     * @param   lower   the index to start searching from.
     * @return  the index of the last occurrence of the specified object in this
     *          Array at position less than <code>index</code> in the Array;
     *          <code>-1</code> if the object is not found.
     */
//	public int firstIndexOf(Object elem, int lower);

    /**Searches backwards for the specified object, starting from the
     * specified index, and returns an index to it.
     *
     * @param   elem    the desired component.
     * @param   upper   the index to start searching from.
     * @return  the index of the last occurrence of the specified object in this
     *          Array at position less than <code>index</code> in the Array;
     *          <code>-1</code> if the object is not found.
     */
//	public int lastIndexOf(Object elem, int upper);

    /**Searches forwards for the specified object, starting from the
     * specified index, and returns an index to it.
     *
     * @param   elem    the desired component.
     * @param   lower   the index to start searching from.
     * @param   upper   the index to stop  searching at.
     * @return  the index of the last occurrence of the specified object in this
     *          Array at position less than <code>index</code> in the Array;
     *          <code>stop+1</code> if the object is not found.
     */
//	public int firstIndexOf(Object elem, int lower, int upper);

    /**Searches backwards for the specified object, starting from the
     * specified index, and returns an index to it.
     *
     * @param   elem    the desired component.
     * @param   upper   the index to start searching from.
     * @param   lower   the index to stop  searching at.
     * @return  the index of the last occurrence of the specified object in this
     *          Array at position less than <code>index</code> in the Array;
     *          <code>stop-1</code> if the object is not found.
     */
//    public int lastIndexOf(Object elem, int lower, int upper);

}
