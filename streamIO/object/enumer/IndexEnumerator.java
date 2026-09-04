package streamIO.object.enumer;

import streamIO.exception.ReadOnlyException;
import streamIO.object.IStreamIn;
import streamIO.object.ModificationException;

/**Abstract Class for an indexed Access.
 * This extends the abstract (Reverse-) Enumerator
 * Only currItem not implemented yet! */
public interface IndexEnumerator
extends ReverseEnumerator {

	/**Returns the Item at the given absolute Position
	 * While this is possible in principle for all Enumerators,
	 * it is too ineffective to loop through the whole Enumerator
	 * @param Index of the Item to be retrieved
	 * @return the Item at Position Index, null, if beyond the Borders
	 *
	 * this was already defined in
	 * @see IStreamIn but throws an Exception there.
	 */
	Object getAt(int Index);

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not
	 * @param Index of the Item to be removed
	 * @return the Item removed, null, if beyond the Borders
	 * @throws ModificationException if the Container is read only
	 */
	Object removeAt(int Index) throws ModificationException;

	/** Replaces the current Object in the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * <p>
	  * The index must be a value greater than or equal to <code>0</code>
	  * and less than the current size of the Array.
	  *
	  * This could also be named setAt()
	  * @param Index of the Item to be replaced
	  * @return the Item replaced, null, if beyond the Borders
	  * @throws ReadOnlyException if the Container is read only
	  * @throws ModificationException if the Container is sorted
	  * @throws ArrayIndexOutOfBoundsException  if the index was invalid.
	  * @see java.util.Array#size()
	  */
	Object setAt(int Index, Object Item); // throws ModificationException;

	/** Sets (adds or replaces) the component at the specified index.
	  * All other components in this Container keep their <code>index</code>.
	  * <p>
	  *
	  * @param	  Item	the component to set (add or replace).
	  * @param	  index   the index of the object to remove.
	  * @return	 the component replaced by 'Item'.
	  * @see		java.util.Array#size()
	  * @see #replaceAt(int, Object)
	  */
	//void setAt(int index, Object Item);

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Could also return a boolean whether the Method is supported or not
	 * @param Index of the Item to be added
	 * @return the Enumerator to enable concatenated adding.
	 * @throws ReadOnlyException if the Container is read only
	 * @throws ModificationException if the Container is sorted
	 */
	IndexEnumerator addAt(int Index, Object Item) throws ModificationException;

	/** Returns the Item at the given relative Position
	  * @param Relative Index of the Item to be retrieved
	  * @return the Item at the relative Position, null, if beyond the Borders
	  */
	Object getRel(int Index);

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not
	 * @param Relative Index of the Item to be removed
	 * @return the Item removed, null, if beyond the Borders
	 * @throws ReadOnlyException if the Container is read only
	 * @throws ModificationException if the Container is read only
	 */
	Object removeRel(int Index) throws ModificationException;

	/** Replaces the current Object in the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * @param relative Index of the Item to be replaced
	  * @return the Item replaced, null, if beyond the Borders
	  * @throws ReadOnlyException if the Container is read only
	  * @throws ModificationException if the Container is sorted
	  */
	Object replaceRel(int Index, Object Item) throws ModificationException;

	/** Adds the Object after the current Object from the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Could also return a boolean whether the Method is supported or not
	  * @param relative Index of the Item to be added
	  * @return the Enumerator to enable concatenated adding.
	  * @throws ReadOnlyException if the Container is read only
	  * @throws ModificationException if the Container is sorted or read only
	  */
	IndexEnumerator addRel(int Index, Object Item) throws ModificationException;

    //the following Methods assume a connex Order on the Keys, i.e. int

	/** Returns the index of the first occurrence of the specified object in
	  * this Array.
	  *
	  * @param   elem   the desired component.
	  * @return  the index of the last occurrence of the specified object in
	  *		  this Array; returns <code>-1</code> if the object is not found.
	  */
	public int firstIndexOf(Object elem);

	/** Returns the index of the last occurrence of the specified object in
	  * this Array.
	  *
	  * @param   elem   the desired component.
	  * @return  the index of the last occurrence of the specified object in
	  *		  this Array; returns <code>-1</code> if the object is not found.
	  */
	public int lastIndexOf(Object elem);

	/** Searches forwards for the specified object, starting from the
	  * specified index, and returns an index to it.
	  *
	  * @param   elem	the desired component.
	  * @param   lower   the index to start searching from.
	  * @return  the index of the last occurrence of the specified object in this
	  *		  Array at position less than <code>index</code> in the Array;
	  *		  <code>-1</code> if the object is not found.
	  */
	public int firstIndexOf(Object elem, int lower);

	/** Searches backwards for the specified object, starting from the
	  * specified index, and returns an index to it.
	  *
	  * @param   elem	the desired component.
	  * @param   upper   the index to start searching from.
	  * @return  the index of the last occurrence of the specified object in this
	  *		  Array at position less than <code>index</code> in the Array;
	  *		  <code>-1</code> if the object is not found.
	  */
	public int lastIndexOf(Object elem, int upper);

	/** Searches forwards for the specified object, starting from the
	  * specified index, and returns an index to it.
	  *
	  * @param   elem	the desired component.
	  * @param   lower   the index to start searching from.
	  * @param   upper   the index to stop  searching at.
	  * @return  the index of the last occurrence of the specified object in this
	  *		   Array at position less than <code>index</code> in the Array;
	  *		  <code>stop+1</code> if the object is not found.
	  */
	public int firstIndexOf(Object elem, int lower, int upper);

	/** Searches backwards for the specified object, starting from the
	  * specified index, and returns an index to it.
	  *
	  * @param   elem	the desired component.
	  * @param   upper   the index to start searching from.
	  * @param   lower   the index to stop  searching at.
	  * @return  the index of the last occurrence of the specified object in this
	  *		  Array at position less than <code>index</code> in the Array;
	  *		  <code>stop-1</code> if the object is not found.
	  */
	public int lastIndexOf(Object elem, int lower, int upper);

}
