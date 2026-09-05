package streamIO.object.enumer;

import graphs.ILinkAble;
import graphs.ILinked;
import graphs.IPair;
import streamIO.exception.OperationNotSupported;
import streamIO.object.ModificationException;
import streamIO.object.json.JSONTest;
import tester.Discrete;

/** This is the Class of the List Items for singly linked Lists
  * and upward navigable Trees (Hierarchies),
  * although you can also build Cycles in the Object Graph!
  * It should never be visble to the User directly,
  * because it is always hidden by the List Object.
  * This is a Specialization of the Association or Pair where the next Item
  * is always assumed to be a ListItem too.
  *
  * It is used to construct singly linked Lists and upward linked Trees,
  * as Base for DblListItem and for building up Disjoint Set Structures.
  * When changing the List by inserting Items is not allowed,
  * the ListItems could be reused by different Lists sharing their ends (Trees)!
  * The HashCode() and equals() Methods run along the whole List
  * and don't lose Information because the HashCode is rotated left (ROL).
  *
  * I could have extended streamIO.Association, but then I would have to cast nextItem.
  * This should be a lightweight Class to allow fast Con- and De-struction
  * when adding or removing Items from the List.
  * This is much less performant than an Array,
  * which has to be resized only when full and prevents Cache Swapping.
  *
  * The ListItem should be used only internally to speed up Creation and Destruction.
  * Both Container and Iterator use the ListItem only as a Container
  * by not exposing it and accessing it's Properties directly.
  *
  * ILinkAble to improve the Algorithms for finding and for comparing
  * Elements in disjoint Sets (defining Equivalence Relations).
  * Non-disjoint Sets must be represented using several Containers
  *
  * similar Classes:
  * @see Forest.IEquivalence which works with integer Numbers.
  * @see streamIO.Object.IPair
  * @see streamIO.Object.Pair
  * @see streamIO.Object.Enumerator.ILinkAble
  * @see streamIO.Object.Enumerator.ListItem
  * @see streamIO.Copy.IMonoid.Pair
  * @see streamIO.Copy.IMonoid.Association
  *
  * <!-- docstate
  * tags: [code/enumerator, code/iterator_adapter]
  * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class ListItem
extends AEnumerator  //leads to expensive Constructor Calls which slows down adding / removing Items.
implements ILinkAble, IPair //, CopyAble //for Trees and upward Lists to fill up with correct Elements
{
	
	///////////////////////////////////////////////////////////////////////////
	// static Methods for handling Disjoint Sets:
	///////////////////////////////////////////////////////////////////////////
	
	/** Returns the (current) Root of this Element.
	  * Does not reduces the Height of the Tree,
	  * because there is no Method available to do that.
	  * Uses Method calls, so it is not very fast!
	  * For fast Performance use direct Member access like in
	  * @see Discrete.Forest.IEquivalence	 */
	// TODO: LOGIC: the loop condition is inverted. It should stop when getPrnt() returns
	// null (x is the root) and otherwise keep climbing; as written it loops only while
	// getPrnt() returns null, so a root node (p == null on the first call) sets x = null
	// and then throws a NullPointerException on the next x.getPrnt() call, while a non-root
	// node exits immediately and returns just its direct parent instead of the true root.
	final static public ILinked getRootSimple(ILinked x) {
		ILinked p;
		while((p  = x.getPrnt()) == null)
			x = p;
		return p; }
	
	/** Returns the (current) Root of this Element.
	  * Reduces the Height of the Tree by one on every Level.
	  * Uses Method calls, so it is not very fast!
	  * For fast Performance use direct Member access like in
	  * @see Discrete.Forest.IEquivalence	 */
	final static public ILinked getRoot(ILinkAble x) {
		ILinkAble pp, p;
		if   ((p  = (ILinkAble) x.getPrnt()) == null) return x;
		while((pp = (ILinkAble) p.getPrnt()) != null) { //&& (pp != x)) {	//this test is for both the Root poFixLinkeding to itself and weighted Roots!
			x.setPrnt(pp); x = p; p = pp; }
		return p; }
	
	/**Returns the (current) Root of this Element.
	 * Reduces the Height of the Tree to one on EVERY Element of the Search Tree.
	 * Uses Recursion, so it is slower than getRoot on the first Operation,
	 * because these Array Operations are relatively cheap.	 */
	final static public ILinkAble getRootTotal(final ILinkAble x) {
		ILinkAble pp, p;
		if ((p =  (ILinkAble) x.getPrnt()) == null) return x;
		x.setPrnt(pp = getRootTotal(p)); //find the root and set it to all Elements on the way back.
		return pp; }
	
	/** Inserts this Object between the Parent and this one.
	  * This also happens in the Constructor for the ListItem.   */
	final static public void insertNext(final ILinkAble x, final ILinkAble Item) {
		(Item).setPrnt(x.getPrnt()); x.setPrnt(Item); }
	
	/** Removes the next Object from the Set and Iteration,
	  * @return the removed Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  */
	public static Object removeNext(final ILinkAble arg) {
		ILinked ret = arg.getPrnt(); arg.setPrnt(ret.getPrnt());
		return ret; }
	
	/** Checks both Elements for Equivalence by comparing their Roots.
	  * After this they are optionally united using the gathered Information.
	  * @return true, when both ListItems point to the same Root. 	 */
	final static public boolean equals(ILinkAble x, ILinkAble y, boolean union) {
		boolean ret = ((x == y) ||
			(x = (ILinkAble) x.getRoot()) ==	//x = getRootFast(x);
			(y = (ILinkAble) y.getRoot())); 	//y = getRootFast(y);
		if ((union) &&  !ret)
			x.setPrnt(y); //or y.setParent(x);
		return ret; }
	
	///////////////////////////////////////////////////////////////////////////
	//  Variables
	///////////////////////////////////////////////////////////////////////////
	
	/**This it the Reference to the Data of the current Item	 */
	protected Object currItem;
	
	/**This it the Reference (Pointer) to the next Item	 */
	protected ListItem nextItem;  //this is normally declared private and accessible via Method
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	//associates the Key with the Pointer to the next Item
	//and the Value with the Pointer to the current Item
	
	/** Accessor Method
	  * @return key */
	public Object getKey() { return nextItem; }
	
	/** Accessor Method
	  * @return Value */
	public Object getVal() { return currItem; }
	
	/** Accessor Method
	  * @param sets the key of the Pair */
	public void setKey(final Object Key) { this.nextItem = (ListItem) Key; }
	
	/** Accessor Method
	  * @param sets Value of the Pair */
	public void setVal(final Object Value) { this.currItem = Value; }
	
	/** Accessor Method
	  * @return Parent */
	public ILinked getPrnt() { return nextItem; }
	
	/** Accessor Method
	  * @param sets the Parent of the Pair */
	public void setPrnt(final ILinked parent) { nextItem = (ListItem) parent; }

	/** Accessor Method
	  * @return the final Parent == Root
	  * getRoot().getKey() is equivalent to StreamIn.lastItem()  */
	public ILinked getRoot() { //return getRoot(this); } //slow Implementation due to Method Calls
		ListItem pp, p, x = this; //fastest Implementation due to direct Member Access
		if   ((p  = x.nextItem) == null) return x;
		while((pp = p.nextItem) != null) { //&& (pp != x)) {	//this test is for both the Root poFixLinkeding to itself and weighted Roots!
			x.nextItem = pp; x = p; p = pp; }
		return p; }
	
	/** Removes the next Object from the Set and Iteration
	  * This has to be done explicitly, because the Destructor
	  * is not able to resolve the leading Connection.
	  * @return the removed Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  */
	public Object removeNext() throws ModificationException {
//		ListItem ret = nextItem; nextItem = ret.nextItem; //the ListItem should not be visible!
		final Object ret = nextItem.currItem; nextItem = nextItem.nextItem;
		return ret; }
	
	/** Replaces the current Object in the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * In this Case the Exception is thrown.
	  * That is why this Method should throw an Exception if replacing is not allowed.
	  * It should also update the Minor Version (or let the Container update it)
	  * to announce the Change to other Iterators.
	  * @param  The Item to replace the current Item (returned by the latest nextItem())
	  * @return the Object replaced by the Item
	  */
	public Object replaceCurr(final Object Item) { //throws ModificationException {
		Object ret = currItem; currItem = Item; return ret; }
	
	/**Replaces the current Object from the Container with this Item.
	 * The remaining Problem is other Enumerators that concurrently work through this. */
	public Object replaceNext(final Object Item) { //throws ModificationException { //ByRefLong available) {
		Object ret = nextItem.currItem; nextItem.currItem = Item;
		return ret; }
	
	/** Adds the Item to the Container (after this one) with this Enumerator knowing it.
	  * The remaining Problem is other Enumerators that concurrently work through this.
	  * When no Sequence is defined, use addItem() or add().
	  * Relies on the Constructor tieing the Nodes together!  */
	public Enumerator addNext(final Object Item) throws ModificationException { //
		return nextItem = new ListItem(Item, this); }
	
	///////////////////////////////////////////////////////////////////////////
	//  Interface Iterator:
	///////////////////////////////////////////////////////////////////////////
	
	/**This it the Iterator Method of the List Item	 */
	public Object nextItem() { return nextItem.currItem; }
	
	/**Returns the (least) Number of Items available	  */
	public long availAble() { return (nextItem != EOI) ? 1 : 0; }
	
	///////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor, defaults all Elements to null	 */
	protected ListItem() { super(null); }
//		currItem = SOI;
//		nextItem = EOI; }
	
	/**Initializing Constructor,
	 * sets the Value and prepends it into the List	 */
	public ListItem(final Object Item_, final ListItem nextItem_) {
		super(null); 
		currItem = Item_;
		nextItem = nextItem_; }
	
	/**Initializing Constructor,
	 * sets the Value and inserts it into the List so that it is correctly linked.	 */
	public ListItem(final ListItem prevItem_, final Object Item_) {
		super(null); 
		currItem= Item_;
		if (prevItem_ == null) return; //this does not set the nextItem if there is no root!
		nextItem= prevItem_.nextItem; //but fortunately this is not necessary in an unordered List, since you append at the beginning.
		prevItem_.nextItem = this; }
	
	/**Initializing Constructor,
	 * sets the Value but doesn't insert it into a List	 */
	public ListItem(final Object Item_) {
		super(null); 
	//	nextItem = EOI;
		currItem = Item_; }
	
	////////////////////////////////////////////////////////////////////////////////
	//	Interface AlterAble
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Intstance of a Pipe or Enumerator ,
	  * which allows for changing the Data and structure concurrently. */
	public Enumerator Enumerator() {
		return new ListItem(this.currItem, this.nextItem); }
	
	/** Returns a new Intstance of a ChangeIterator,
	  * which allows for changing the Data concurrently. */
	public ChangeIterator ChangeIterator() {
		return new ListItem(this.currItem, this.nextItem); }
	
	/** Returns the Order in which Elements are returned or processed.	 */
	public byte getOrder() { return ORDER_QUEUE; }
	
	/** removes the current Item (returned by the latest nextItem())
	  * @return the current Item	  */
	public Object removeCurr() { throw new OperationNotSupported(); }
	
	/** Returns the current Object without moving.
	  * This is just a caching Functionality and should be done
	  * at the Client Process, for faster Access.	 */
	public Object currItem() { return currItem; }
	
	////////////////////////////////////////////////////////////////////////////////
	//	Interface ICopyAble
	////////////////////////////////////////////////////////////////////////////////
	
	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
/*	public CopyAble copyAt(Object arg, int Depth) {
		ListItem arg_ = (ListItem) arg;
		Item	 = arg_.Item;
		if (--Depth > 0)
			 nextItem = (arg_.nextItem).copy(Depth);	//copy the Copy
		else nextItem = arg_.nextItem;	//copy only the Value
		return this; }
		
	/** Creates an uninitalized new Instance of it's class.
	  * When overriding, use newInstance on all Components.
	  * Not necessary, clone() is sufficient! 	 */
	//public CopyAble newInstance() { return new ListItem(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//	Interface Object
	////////////////////////////////////////////////////////////////////////////////
	
	/** Checks both Elements for Equivalence by comparing their Roots.
	  * After this they are optionally united using the gathered Information.
	  * @return true, when both ListItems point to the same Root. 	 */
	public boolean equals(final ILinkAble y, final boolean union) {
		return equals(y, this, union); }
	
	/** Compares only the Value of this object to the specified object.
	  * conformant to hashCode() analogous to Association
	  * @see JSONTest
	  *
	  * @param obj	the object to compare with
	  * @return 		true if the objects are equivalent; false otherwise.
	  * @since   JDK1.1	 */
	public boolean equals(final Object arg) {
		if (arg == null) return false;
		if (arg == this) return  true;
		if (arg instanceof ListItem) {
			final ListItem arg_ = (ListItem) arg;
			return ( //for Symmetry, also the reverse Combination has to be checked!
				(currItem   ==   arg_.currItem) ||
				(currItem.equals(arg_.currItem)) ) && (
				(nextItem   ==   arg_.nextItem) ||
				(nextItem.equals(arg_.nextItem)) );
		} else { return false; } }
	
	/** Returns a hash code Value for the object
	  * conformant to equals() analogous to Association.
	  * With the Association the HashCode is exactly the key's HashCode!
	  * This has to be redefined if the Association is used to (recursively)
	  * cluster two Arguments which is done in String.DynTransByFunction.
	  *
	  * This method is supported for the benefit of hashtables
	  * such as those provided by <code>java.util.Hashtable</code>.
	  * <p>
	  * The general contract of <code>hashCode</code> is:
	  * <ul>
	  * <li>Whenever it is invoked on the same object more than once during
	  * an execution of a Java application, the <code>hashCode</code> method
	  * must consistently return the same integer. This integer need not
	  * remain consistent from one execution of an application to another
	  * execution of the same application.
	  * <li>If two objects are equal according to the <code>equals</code>
	  * method, then calling the <code>hashCode</code> method on each of the
	  * two objects must produce the same integer result.
	  * </ul>
	  *
	  * @return  a hash code Value for this object.
	  * @see     Object#equals(Object)
	  * @see     java.util.Hashtable
	  * @see     graphs.Pair for the same Implementation
	  * @since   JDK1.0	 */
	public int hashCode() {
		boolean neg;
		int HC = 0; //ROL the next Items HashCode to not lose any Information!
		if (nextItem != null) { HC  = nextItem.hashCode(); neg = (HC < 0); HC <<= 1; if (neg) ++HC; }
		if (currItem != null) { HC ^= currItem.hashCode(); } //make this conformant to the equals() Method
		return HC; }

	/** Renders this Item as "(currItem@nextItem)".
	 * @return  A string representation of this Association.     */
	public synchronized String toString() { return "(" + currItem + "@" + nextItem + ")"; }

	/** This List Item does not support marking.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }

	/** This List Item has no meaningful Position of its own.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
}
