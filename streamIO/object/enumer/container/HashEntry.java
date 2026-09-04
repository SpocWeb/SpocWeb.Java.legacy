package streamIO.object.enumer.container;

import graphs.ICPair;
import graphs.ICopy;
import graphs.ILinkAble;
import graphs.ILinked;
import graphs.IValue;

import java.sql.ResultSet;
import java.sql.SQLException;

import streamIO.object.IStreamIn;
import streamIO.object.enumer.Enumerator;
import streamIO.object.enumer.ListItem;
import synch.ValidationRule;
import tester.IEquivalence;

/** 
 * Value-added Elements for the linked collision list for the Hashtable.
 * 
 * Design Decisions: 
 * Instead of using an Array of 'List' Items as Hashtable, I rather re-implement the List,
 * because it has special Contents and creates less overhead. 
 * A HashContainer using a linked List is a quite lightweight Implementation 
 * compared to the HashSet, although it requires to create a List Entry for each added Object. 
 * To compensate for this, the Entry Elements are final, lightweight (fast Construction) 
 * and packed with Attributes and implement many Interfaces. 
 * 
 * Storage Strategies for Graphs using a HashContainer: 
 * Indexing by Key only is sufficient and necessary for most Graph Algorithms 
 * to be able to quickly iterate over all Edges originating from a single Node. 
 * Indexing by Key and Value (and Type) is necessary only for dense Graphs, 
 * where you would use an Adjacency Matrix anyway, 
 * since a short sweep over all Edges from the same Key will find the Node.    
 * 
 * similar Classes:
 * @see Forest.IEquivalence which works with integer Numbers.
 * @see streamIO.Object.IPair
 * @see streamIO.Object.Pair
 * @see streamIO.Object.Enumerator.ILinked
 * @see streamIO.Object.Enumerator.ListItem
 * @see streamIO.Copy.IMonoid.Pair
 * @see streamIO.Copy.IMonoid.Association
 * 
 * @see streamIO.object.enumer.container.tree.TreeMapEntry which also extends IndexAssociation 
 * and adds Relations to a previous and a Parent Element. 
 */
final public class HashEntry 
extends IndexAssociation  //leads to expensive Constructor Calls which slows down adding / removing Items.
implements ILinkAble, ICopy, IValue, ICPair //, IPair //, CopyAble //for Trees and upward Lists to fill up with correct Elements
{
	
	/** The HashCode is cached on creating this Entry, 
	 * because it is compared often and required for a Rehash. 
	 * This saves the repeated Function Call and Calculation Overhead. 
	 */
	protected final int hash;
	
	/** Reference to the next / parent HashTableEntry stored  */
	protected HashEntry next;
	
	///////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////

	/** Empty Constructor for fast Construction and external Initialization */
	//protected HashEntry() { }

	/**Initializing Constructor,
	 * sets the Value and prepends it into the List	 */
	public HashEntry(final Object _key, final Object _value, final Object _type, final double _weight, final int _index, final int _hash, final HashEntry _next) {
		super(_key); 
		this.val = _value; 
		this.typ = _type; 
		this.weight = _weight; 
		this.ndx = _index; 
		this.hash = _hash; 
		this.next = _next; 
	}
	
	/** reads all available Data from the ResultSet	 */
	public HashEntry(final ResultSet rs, final int[] cols, final IEquivalence hashFn) throws SQLException {
		super(        rs.getObject(cols[0])); 
		if (cols.length > 1) this.val    = rs.getObject(cols[1]) ; 
		if (cols.length > 3) this.typ    = (cols[3] >= 0 ? rs.getObject(cols[3]) : null); 
		if (cols.length > 2) this.weight = (cols[2] >= 0 ? rs.getFloat (cols[2]) : 1); 
		if (cols.length > 4) this.ndx    = (cols[4] >= 0 ? rs.getInt   (cols[4]) : 1); 
		this.hash   = (hashFn != null ? hashFn.HashCode(this.key) : this.key.hashCode()); 
	}
	
	/**Initializing Constructor,
	 * sets the Value and prepends it into the List	 */
	public HashEntry(final Object _item, final int _hash, final HashEntry _next) {
		this(_item, _item, null, 1, 1, _hash, _next); }
	
	/** Initializing Constructor,
	  * sets the Value and prepends it into the List
	  * No full Initialization, because the previous Item is not initialized. */
	public HashEntry(final Object _item, final HashEntry next_) {
		this(_item, _item.hashCode(), next_); }
	
	/** Constructor for full Initialization
	  * Like with ListItems this one maintains the Linked List automatically. */
/*	public HashEntry(final HashEntry prev_, final Object Item_) {
		this(prev_, Item_, Item_.hashCode()); }

	/**Initializing Constructor,
	 * sets the Value and inserts it into the List so that it is correctly linked.	 */
/*	public HashEntry(final HashEntry prev_, final Object Item_, final int hash_) {
		super(Item_); 
		hash = hash_; 
		if (prev_ == null) return; //this does not set the mNext if there is no root!
		next = prev_.next; //but fortunately this is not necessary in an unordered List, since you append at the beginning.
		prev_.next = this; }
	
	/** Copies the whole Object to one Level.	 */
	//public Object newInstance() {
	//	return new HashEntry(); }

	/** @return a new Enumerator with the same Position	 */
	public HashEntry Enumerator() {
		try { return (HashEntry) this.clone(); }
		catch (CloneNotSupportedException x) { return null; } } //throw new CloneNotSupportedError(x.toString); } }

	/** @return a new linked List with the same Elements
	  * Does a "shallow" Copy in the Sense of the Container,
	  * but the List is copied deeply.
	  * Slow Implementation due to Recursion! */
	public ICopy Copy() {
		super.Copy();
		Object Item;
		if(((Item = key) != null) &&
			 Item instanceof ICopy) {
			 Item = ((ICopy) Item).Copy(); }
		if (next == null)
			return new HashEntry(Item, hash, null);
		return new HashEntry(key, hash, (HashEntry) next.Copy());  }
	
	/** Checks both Elements for Equivalence by comparing their Data.
	  * @return true, when both ListItems point to the same Object. 	 */
	public boolean equals(final Object y) {
		if (y instanceof HashEntry)
			return equals((HashEntry) y); 
		return ValidationRule.EQUALS(key, y); }
	
	/** Checks both Elements for Equivalence by comparing their Data.
	  * @return true, when both ListItems point to the same Object. 	 */
	public boolean equals(final HashEntry y) {
		return 
		//ValidationRule.EQUALS(weight, y.weight) && //ignore the weight for now, because it has to be tested separately 
		ValidationRule.EQUALS(key, y.key) &&
		ValidationRule.EQUALS(val, y.val) &&
		ValidationRule.EQUALS(typ, y.typ); }
	
	/** Checks both Elements for Equivalence by comparing their Roots.
	  * After this they are optionally united using the gathered Information.
	  * @return true, when both ListItems point to the same Root. 	 */
	public boolean equals(final ILinkAble y, final boolean union) {
		return ListItem.equals(y, this, union); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	 * @see ILinked#getPrnt()
	 * @return Parent */
	public ILinked getPrnt() { return next; }

	/** Accessor Method
	 * @see ILinkAble#setPrnt(ILinked)
	 * @param sets the Parent of the Pair */
	public void setPrnt(final ILinked parent) { next = (HashEntry) parent; }

	/** Accessor Method
	 * @see ILinked#getRoot()
	 * @return the final Parent == Root
	 * getRoot().getKey() is equivalent to StreamIn.lastItem()
	 * fastest Implementation due to direct Member Access 	 */
	public ILinked getRoot() { //return getRoot(this); } //slow Implementation due to Method Calls
		HashEntry pp, p, x = this; //
		if   ((p  = x.next) == null) return x;
		while((pp = p.next) != null) { //&& (pp != x)) {	//this test is for both the Root poFixLinkeding to itself and weighted Roots!
			x.next = pp; x = p; p = pp; }
		return p; }

	///////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////

	/** Removes the next Object from the Set and Iteration
	  * This has to be done explicitly, because the Destructor
	  * is not able to resolve the leading Connection.
	  * @return the removed Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  */
	public Object removeNext() throws NoSuchMethodException {
//		ListItem ret = mNext; mNext = ret.mNext; //the ListItem should not be visible!
		Object ret = next.key; next = next.next;
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
	//public Object replaceCurr(final Object Item) throws NoSuchMethodException {
	//	Object ret = mItem; mItem = Item; return ret; }

	/**Replaces the current Object from the Container with this Item.
	 * The remaining Problem is other Enumerators that concurrently work through this. */
	/*public Object replaceNext(final Object Item) throws NoSuchMethodException { //ByRefLong available) {
		Object ret = mNext.mItem; mNext.mItem = Item;
		return ret; }
    */
	/** Adds the Item to the Container (after this one) with this Enumerator knowing it.
	  * The remaining Problem is other Enumerators that concurrently work through this.
	  * When no Sequence is defined, use addItem() or add().
	  * Relies on the Constructor tieing the Nodes together!  */
	public Enumerator addNext(final Object Item) throws NoSuchMethodException { //
//		if (this.mHash != Item.hashCode()) {
			throw new NoSuchMethodException(); }
//		mNext = new HashItem(Item, this); return this; }

	///////////////////////////////////////////////////////////////////////////
	//  Interface Iterator:
	///////////////////////////////////////////////////////////////////////////

	/**This it the Iterator Method of the List Item	 */
	public Object nextItem() { return next.key; }

	/**Returns the (least) Number of Items available	  */
	public long available() { return (next != IStreamIn.EOI) ? 1 : 0; }

}


