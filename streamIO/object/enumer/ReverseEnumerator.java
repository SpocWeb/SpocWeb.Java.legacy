package streamIO.object.enumer;

import streamIO.exception.ReadOnlyException;
import streamIO.object.ModificationException;

/**Interface for an iterator through a Collection
 *
 * Design Decisions:
 * The Enumerator Object is singled out from the Container Classes,
 * because it is very probable, that you have several Enumerators
 * running over the same Object at the same time!
 * Therefore the Interface is renamed from 'iterAble' to 'Enumerator'.
 *
 * The skip Method should now also accept negative Values, moving backwards.
 * <!-- docstate
 * tags: [code/enumerator, code/iterator_adapter]
 * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public interface ReverseEnumerator
extends Enumerator
{

	/**Returns the minimum Number of Items (in the Buffer)
	 * that are to be reached by previousItem.
	 * Should be called again on reaching this Number,
	 * because the Buffer may be filled up.
	 */
	public long availableBefore();

	/**Returns the previous Object:	 */
	public Object prevItem();

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not
	 * @return the previous Item in the Container
	 * @throws ReadOnlyException when the Container is read only
	 * @throws ModificationException when the Container is read only
	 */
	public Object removePrev() throws ModificationException;

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * @param Object to replace the previous one in the Container
	 * @return the previous Object for later use
	 * @throws ReadOnlyException when the Container is read only
	 * @throws ModificationException when the Container is sorted
	 */
	public Object replacePrev(Object Item); // throws ModificationException;

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * Could also return a boolean whether the Method is supported or not
	 * @param Object to be added at the current Position
	 * @return the Enumerator to allow for concatenated Adding
	 * @throws ReadOnlyException when the Container is read only
	 * @throws ModificationException when the Container is sorted
	 */
	public ReverseEnumerator addPrev(Object Item) throws ModificationException;

	// TODO: LOGIC: this Javadoc block (starting with "Removes the current Object...")
	// is missing its closing "*/", so the "removeCurr()" declaration below is swallowed
	// as comment text instead of being an active method declaration. ReverseEnumerator
	// therefore does not explicitly redeclare removeCurr() here; only the inherited
	// declaration from Enumerator applies, silently losing this comment's own contract.
	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not
	 * @return the current Object (returned by the latest nextItem())
	 * @throws ReadOnlyException when the Container is read only
	 * @throws ModificationException when the Container is sorted
	public Object removeCurr() throws ModificationException;

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * @param Object to be replaced at the current Position
	 * @return the Object at the current Position
	 * @throws ReadOnlyException when the Container is read only
	 * @throws ModificationException when the Container is sorted
	 */
	public Object replaceCurr(Object Item); // throws ModificationException;

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that adding the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if adding is not allowed.
	 * Could also return a boolean whether the Method is supported or not
	 * @param Object to be added at the current Position
	 * @return the Enumerator to allow for concatenated Adding
	 * @throws ReadOnlyException when the Container is read only
	 * @throws ModificationException when the Container is sorted
	 */
	public ReverseEnumerator addCurr(Object Item) throws ModificationException;

	/**
	  * sets the Iterator behind the last Position.
	  * This is the Opposite to reset()
	  * just like previous() the Opposite to next()
	  */
	public ReverseEnumerator preset();

}
