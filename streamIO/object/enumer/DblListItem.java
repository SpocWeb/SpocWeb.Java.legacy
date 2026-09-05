package streamIO.object.enumer;

//import Stream.Object.*;//
//import Stream.OperationNotSupported;
import streamIO.IReSetAble;
import streamIO.object.ModificationException;

/**This is the Class of the List Items for single linked Lists.
 * Different from singly linked Lists, these cannot be used for Tree Structures,
 * except if the prevItem is modeled as a nested Structrue itself.
 * It should never be visble to the User directly,
 * because it is always hidden by the List Object.
 * <!-- docstate
 * tags: [code/enumerator, code/iterator_adapter]
 * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class DblListItem
extends ListItem
implements ReverseEnumerator {

	///////////////////////////////////////////////////////////////////////////
	//  Variables
	///////////////////////////////////////////////////////////////////////////

	/**This it the Reference (Pointer) to the previous Item	 */
	public DblListItem prevItem;

	///////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////

	/**Empty Constructor	 */
	protected DblListItem() { super (); }

	/**Initializing Constructor,
	 * sets the Value and inserts it into the List
	 * the other Pointers are updated automatically	 */
	public DblListItem(DblListItem prevItem_, Object Item_, DblListItem nextItem_)	{
		super(Item_,nextItem_);
		if (EOI !=  nextItem_)             nextItem_.prevItem = this;
		if (EOI != (prevItem = prevItem_)) prevItem_.nextItem = this;
	}

	/**Initializing Constructor,
	 * sets the Value but doesn't insert it into the List	 */
	public DblListItem(Object Item_) { super(Item_); }


	///////////////////////////////////////////////////////////////////////////
	//  Interface ReverseEnumerator
	///////////////////////////////////////////////////////////////////////////

	/**This it the Iterator Method of the List Item	 */
	public Object prevItem() { return prevItem.currItem; }

	/**Returns the (least) Number of Items available before this one.	  */
	public long availableBefore() { return (prevItem != EOI) ? 1 : 0; }

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not */
	public Object removePrev() throws ModificationException {
		Object ret = prevItem.currItem;
		prevItem = prevItem.prevItem;
		prevItem.nextItem = this;
		return ret; }

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 */
	public Object replacePrev(Object Item) { //throws ModificationException {
		Object ret = prevItem.currItem; prevItem.currItem = Item;
		return ret; }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * Could also return a boolean whether the Method is supported or not */
	public ReverseEnumerator addPrev(Object Item) throws ModificationException {
//		prevItem = //not necessary, updated automatically
		new DblListItem(prevItem.prevItem, Item, this);
		return this; }

	/**Removes the current Object from the Container with this Enumerator knowing it.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Therefore the Version of the Container is updated.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * After removing currItem() is set to SOI (nextItem is not triggered automatically!)
	 * Could also return a boolean whether the Method is supported or not */
	public Object removeCurr() {
		Object ret = nextItem(); //.currItem; //cannot access protected Field currItem!?!
		nextItem = nextItem.nextItem;
		((DblListItem) nextItem).prevItem = this;
		return ret; }

	/**Replaces the current Object in the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 */
	// TODO: LOGIC: this method never uses the Item parameter and never writes any state -
	// it only reads prevItem.currItem (not even this item's own currItem) and returns it,
	// so calling replaceCurr() silently performs no replacement at all, unlike replacePrev()
	// just above which does assign prevItem.currItem = Item.
	public Object replaceCurr(Object Item) { //throws ModificationException {
		Object ret = prevItem.currItem;
		return ret; }

	/**Adds the Object after the current Object from the Container with the given Item.
	 * One Problem is other Enumerators that concurrently work through this Container.
	 * Another Problem is that removing the Item may not be possible at all.
	 * In this Case the Exception is thrown.
	 * That is why this Method should throw an Exception if removing is not allowed.
	 * Could also return a boolean whether the Method is supported or not */
	public ReverseEnumerator addCurr(Object Item) throws ModificationException {
		return new DblListItem(prevItem, Item, (DblListItem) nextItem); }

	/**
	  * sets the Iterator behind the last Position.
	  * This is the Opposite to reset()
	  * just like previous() the Opposite to next()
	  */
	public ReverseEnumerator preset() {//throws NoSuchMethodException {
		return (DblListItem) nextItem; }

	/**
	  * resets the Iterator before the first Position.
	  * This is the Opposite to preset()
	  * just like previous() the Opposite to next()
	  */
	public IReSetAble reSet() {//throws NoSuchMethodException {
		return prevItem; }

	///////////////////////////////////////////////////////////////////////////
	//  Interface ICopyAble
	///////////////////////////////////////////////////////////////////////////

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
/*	public copyAble copyAt(Object arg, int Depth) {
		super.copyAt(arg);
//		ListItem arg_ = (ListItem) arg;
//		prevItem = arg_.prevItem;	//copy only the Value
		return this; }

	/**Creates an uninitalized new Instance of it's class.
	 * When overriding, use newInstance on all Components.	 */
//	public copyAble newInstance() { return new dblListItem(); }

}
