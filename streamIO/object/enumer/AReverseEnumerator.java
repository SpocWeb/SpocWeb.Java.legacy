package streamIO.object.enumer;

import streamIO.IReSetAble;
import streamIO.exception.ReadOnlyException;
import streamIO.object.ModificationException;

/**Implements the possible abstract reverse Enumerator Operations
 * @stereotype enumeration
 */
public abstract class AReverseEnumerator
extends AEnumerator
implements ReverseEnumerator {

	/**
	 * @param _container a versioned container backing this Enumerator. Null allowed
	 */
	public AReverseEnumerator(final IAlterAble _container) {
		super(_container);
	}
	
	/**
	  * sets the Iterator behind the last Position.
	  * This is the Opposite to reset()
	  * just like previous() the Opposite to next()
	  */
	public ReverseEnumerator preset() {
		jump(availAble () + 1);
		return this; }

	/**Resets the Enumerator to the last marked Position.
	 * Very ineffective Implementation!	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		while ((prevItem() != Enumerator.SOI) || (availableBefore() >= 0)); //do nothing!
		return this; } //

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException when removing the Item is not allowed
	 */
	public Object replacePrev(Object Item) { //throws ModificationException {
		try {
			Object ret = removePrev();
			addPrev(Item);
			return ret;
		} catch (ModificationException x) { throw new ReadOnlyException(x); } }

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException when removing the Item is not allowed
	 */
	public Object replaceCurr(Object Item) { //throws ModificationException {
		try {
			Object ret = removeCurr();
			addCurr(Item);
			return ret; 
		} catch (ModificationException x) { throw new ReadOnlyException(x); } }

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * @throws ModificationException when removing the Item is not allowed
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not */
//	public Object removePrev() throws ModificationException {
//		throw new ModificationException(); }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException when removing the Item is not allowed
	 * Could also return a boolean whether the Method is supported or not */
//	public ReverseEnumerator addPrev(Object Item) throws ModificationException {
//		throw new ModificationException(); }

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * @throws ModificationException when removing the Item is not allowed
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not */
//	public Object removeCurr() throws ModificationException {
//		throw new ModificationException(); }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * @throws ModificationException when removing the Item is not allowed
	 * Could also return a boolean whether the Method is supported or not */
//	public ReverseEnumerator addCurr(Object Item) throws ModificationException {
//		throw new ModificationException(); }

////////////////////////////////////////////////////////////////////////////
//  Interface ReverseEnumerator: abstract Methods
////////////////////////////////////////////////////////////////////////////

	/**Returns the minimum Number of Items (in the Buffer)
	 * that are to be reached by previousItem.
	 * Should be called again on reaching this Number,
	 * because the Buffer may be filled up.
	 */
	public abstract long availableBefore(); // { return 0; }

	/**Returns the previous Object:	 */
	public abstract Object prevItem(); // { return null; }

}
