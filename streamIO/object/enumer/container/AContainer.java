package streamIO.object.enumer.container;

import java.security.InvalidParameterException;
import java.util.Comparator;

import streamIO.AReSetAble;
import streamIO.AStreamOut;
import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IIterAble;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.StreamOutPrimitive;
import streamIO.StringBufferOutputStream;
import streamIO.copy.ICopyAble;
import streamIO.copy.boole.Boole;
import streamIO.copy.boole.Lattice;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.monoid.Association;
import streamIO.exception.ReadOnlyException;
import streamIO.integer.filter.LimitedSizeOutputStream;
import streamIO.object.AStreamIn;
import streamIO.object.AStreamSet;
import streamIO.object.CopyStreamIn;
import streamIO.object.CopyStreamOut;
import streamIO.object.DIFF;
import streamIO.object.IStreamIn;
import streamIO.object.ModificationException;
import streamIO.object.Product;
import streamIO.object.StringStreamIn;
import streamIO.object.backTrack.BackTracker;
import streamIO.object.enumer.ArrayEnum;
import streamIO.object.enumer.ChangeIterator;
import streamIO.object.enumer.Enumerator;
import streamIO.object.filterIn.FilterInPair;
import tester.IEquivalence;
import tester.IScalarMetric;
import tester.ITester;
import tester.process.StreamProcessor;
import function.IIOrderAble;
import function.IProcessor;
import graphs.IPair;
import graphs.KeyValuePair;

/**
  * Abstract Implementation of a Container.
  * @stereotype container
  * @see streamIO.Object.Enumerator.Container.Container for a description of it's specifics.
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public abstract class AContainer
extends AStreamSet
implements Container {
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Number of Items in this Container
	  * This is slightly redundant, since it could be calculated in an O(N) Operation,
	  * but O(N) is expensive and should be performed together with other O(N) Operations.
	  */
	protected int itemCount; // =0;
	
	/** Major Version of the Container. 
	 * Must be updated on any structural Change of the Container (e.g. Deletions, rehash)
	 * to trigger fast-fail Enumerators.
	 * At Creation Time a fast-fail Enumerator should read this
	 * and throw a ConcurrentModificationException 
	 * instead of synchronizing all the Container Methods
	 * and thus blocking the Container.
	 */
	protected int major;
	
	/**
	 * Minor Version of the Container. 
	 * Counts the Number of 'simple' Data Changes of this Container (e.g. Additions). 
	 * At Creation a fast-fail Iterator should read this.
	 * and throw a ConcurrentModificationException instead of synchronizing
	 * all the Container Methods and thus blocking the Iterators.
	 * Must be updated on any Change of the Container to trigger fast-fail Enumerators.
	 * Can also be used to keep the Version if this is a Container.
	 */
	protected int minor;
	
	/** Reference to the HashCode and Equivalence Function.
	  * Used to determine the Equivalence of Elements and the HashCode if not null
	  */
	protected IEquivalence hashFn; // = null; //rather than checking for null, 
	//use a Default Equivalence Implementation that delegates to the Object. 
	
	/** Reference to the single direct, synchronized Enumerator for this Container 	*/
	protected Enumerator enm; 
	
	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'false'.
	 * For Sets: The Set of ALL Elements (only posssible for known finite SuperSets) */
	protected Container True; //TODO: can this be a StreamSet?
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Reference to the HashCode and Equivalence Function
	  * used to determine the Equivalence of Elements and the HashCode (if not null)
	  */
	public IEquivalence getHashCode() { return hashFn; }

	/** Returns the current Version of the Container to support fast-fail Enumerators
	 * Should be incremented on each change of the Container
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Enumerator.
	 * Using int should be relatively safe,
	 * because Containers will at most contain about |int| Elements.
	 * Calling this Method additionally to nextItem is quite expensive,
	 * so the Enumerator should try to access the Field directly.
	 */
	public int getMinor() { return minor; } //return 0; }

	/**Increments and returns the current Version of the Container
	 * to indicate Modification to fast-fail Iterators.
	 * The Version should be incremented on each change of the Container
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Iterator.
	 * Using int should be large enough,
	 * because Containers will at most contain about |int| Elements.
	 */
	public int incMinor() { return ++minor; }
	
	/** Returns the current Major Version of the Container to support fast-fail Enumerators
	 * Should be incremented on each structural change of the Container
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Enumerator.
	 * Using int should be relatively safe,
	 * because Containers will at most contain about |int| Elements.
	 * Calling this Method additionally to nextItem is quite expensive,
	 * so the Enumerator should try to access the Field directly.
	 */
	public int getMajor() { return major; }
	
	/**Increments and returns the current Major Version of the Container
	 * to indicate Modification to fast-fail Iterators.
	 * The Version should be incremented on each structural change of the Container
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Iterator.
	 * Using int should be large enough,
	 * because Containers will at most contain about |int| Elements.
	 */
	public int incMajor() { return ++major; }
	
	/** Delegates to the single Iterator's own Position.
	 * @see streamIO.object.enumer.container.ARAContainer#getPosition()	 */
	public long getPosition() { return enm.getPosition(); }

	/** Returns the total Item Count, since the whole Container can be replayed.
	 * @see streamIO.object.enumer.container.ARAContainer#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return itemCount; }

	/** Resets this Container, throwing with the given Message on failure.
	 * @see streamIO.object.enumer.container.ARAContainer#reSet(java.lang.String)	 */
	public IReSetAble reSet(final String failureExceptionMessage) {
		return AReSetAble.RESET(this, failureExceptionMessage);	}
	
	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor TODO: should be removed to enforce Version Check!	 */
	protected AContainer(final int Version) { }
	
	/**Empty Constructor TODO: should be removed to enforce Version Check!	 */
	protected AContainer() { }
	
	////////////////////////////////////////////////////////////////////////////
	//	Methods by Interface
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	// Interface ChangeEventSource Operations:
	////////////////////////////////////////////////////////////////////////////
	
	protected ChangeObserver mListener;
	
	/** adds or removes the given Listener to this ChangeEventSource	*/
	public ChangeEventSource addChangeListener(ChangeObserver Listener, boolean add) {
		if(mListener instanceof ChangeMultiCaster) {
			((ChangeMultiCaster) mListener).addChangeListener(Listener, add);
		} else { //no MultiCaster (yet)
			if (mListener == null) { //no Observer yet
				if (add) {
					mListener =  Listener;
				} else { } //ignore this Removal..., although it is usually a Design Fault!
			} else { //normal Observer
				if (add) {
					ChangeMultiCaster multi = new ChangeMultiCaster();
					multi.addChangeListener(mListener, true);
					multi.addChangeListener( Listener, true);
					mListener = multi;
				} else {
					if (mListener == Listener) {
						mListener =  null;
					} else { } //ignore this Removal..., although it is usually a Design Fault!
				}
			}
		} return this; }
	
	////////////////////////////////////////////////////////////////////////////
	// Interface StreamIn Operations:
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Delegates to the single Iterator's own Filter.
	 * @return the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()! */
	public Object getFilter() {
		return enm.getFilter(); }
	
	/** Sets the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()!
	  * This allows for Optimizations on hashed and sorted Containers
	  * because the Result Set can be decreased dramatically. */
	public void setFilter(Object Value) {
		enm.setFilter(Value); }
	
	/** Performs the Operation of the Operator on each Item in the Collection
	  * that equals this Item. The generic Solution is slow
	  * and can be highly optimized in concrete Implementations.
	  * In a HashContainer all Items that are equal (and some other) must be in a row,
	  * so they can be quickly located and operated on.
	  * @return the Number of equal Objects found. */
	public int forEachThatEquals(Object Item, IProcessor op) {
		return enm.forEachThatEquals(Item, op); } //allows for the Iterator to implement a faster Version
//		return AStreamIn.forEachThatEquals(this, Item, op); }
	
	/**Returns the first Item of the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations.
	 * In a HashContainer all Items that are equal (and some more) must be in a row and
	 * in a sorted List all Items that are equal are following each other,
	 * so they can be quickly located and operated on.
	 * It is faster and easier to use the HashTableIterator!!! */
	public Object firstOfEachThatEqualsThat(Object Item, ITester Test) {
//	throws NoSuchMethodException {
		try { return enm.firstOfEachThatEqualsThat(Item, Test);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); }} //allows for the Iterator to implement a faster Version
//		reset(0); return AStreamIn.nextOfEachThatEqualsThat(this, Item, Test); } //generic Solution
	
	/** Tests, whether this Object exists in the Set,
	  * @return the Object, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findNext(Object Item) {
		return enm.findNext(Item); }	//when the Enumerator can Implement a better search Strategy
//		return AStreamIn.findNext(this, Item); } //when the Container implements the Search Strategy
	// The Iterator should implement the Search and Enumeration Strategy
	// The Container should implement the Storage Strategy
	
	/** Tests, whether this Object exists in the Set,
	  * @return the Object, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findFirst(final Object item) { //throws NoSuchMethodException {
		try { return enm.findFirst(item);
		} catch (final NoSuchMethodException x) { 
			throw new NoSuchMethodError(x.toString()); 
		}
	}	//
//		reset(0); return AStreamIn.findNext(this, Item); }
	
	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
//	public boolean isMonotonous() { return false; }
	
	////////////////////////////////////////////////////////////////////////////
	//	Method Implementations
	////////////////////////////////////////////////////////////////////////////
	
	/** Division of the Container Elements by the given Equivalence Relation
	  * which compares Objects for Equality
	  * This is more common than a Division of a Product by first or second Factor,
	  * which can be done without specifiying an Equivalence Relation by the following Methods:
	  * @see getKeys()
	  * @see getValues()
	  */
	public Container div(IEquivalence EQ) {
		HashContainer ret = new HashContainer(EQ); //newInstance(); //create a Container with the given Equivalence Relation
		ret.ORat(this); //fill it with Elements of this Container using ORat()
		return ret; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface OrderAble: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns the Position of this Number relative to arg:
	 * -1 for smaller, otherwise +1
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return -1 when this.less(arg)
	 *         +1 else
	 */
	public int Position(Object arg)	{
		if (this.isLessThan(arg))	return -1;
		else				return +1; }
	
	/** Returns the exact Position of this Number relative to arg:
	  * -1 for smaller, 0 for equal, otherwise +1
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return -1 when this.less  (arg)
	  *          0 when this.equals(arg)
	  *         +1 else
	  */
	public int compareTo(Object arg) {
		if		(this.isLessThan	(arg))	return -1;
		else if (this.equals(arg))	return  0;
		else						return +1; }
	
	/** between
	  * @param arg1 : first Border to compare to <CODE>this</CODE>
	  * @param arg2 : second Border to compare to <CODE>this</CODE>
	  * @return true, when 'Self' is between arg1 and arg2
	  */
	public boolean isBetween (Object arg1, Object arg2) {
		return this.isLessThan(arg1) ^ this.isLessThan (arg2);}
	
	/** greater: '>'
		 * @param arg  : Object to compare to <CODE>this</CODE>
		 * @return true, when 'Self' > arg
		 */
	public boolean isMoreThan (Object arg) { //do the most probable Test first
		return !(this.isLessThan(arg) || this.equals(arg)); }
	//	return !this.lessEq(arg); }
	
	/** greater or equal: '>='
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return true, when 'Self' >= arg
	  */
	public boolean notLessThan (Object arg) { return !(this.isLessThan(arg)); }
	
	/** less or equal: '<='
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return true, when 'Self' <= arg
	 */
	public boolean notMoreThan (Object arg) { return (this.isLessThan(arg) || this.equals(arg)); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IMeasurAble:
	////////////////////////////////////////////////////////////////////////////
	
	//Optimization: no Delegation to getInt() here, prevents reuse in @see DebitContainer
	
	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble() { return itemCount; }
	
	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float   getFloat() { return itemCount; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface ICountAble:
	////////////////////////////////////////////////////////////////////////////

	/** Narrows the Item Count to a byte.
	 * @return  the Number of Items in the Collection represented by an 8 Bit Integer
	 * @throws IllegalArgumentException when the Count does not fit into a byte	 */
	public byte   getByte() {
		byte ret  = (byte) itemCount;
		if  (ret !=        itemCount) throw new IllegalArgumentException();
		return ret; }

	/** Narrows the Item Count to a short.
	 * @return  the Number of Items in the Collection represented by a 16 Bit Integer
	 * @throws IllegalArgumentException when the Count does not fit into a short	 */
	public short getShort() { //return mNumItems; }
		short ret  = (short) itemCount;
		if   (ret !=         itemCount) throw new IllegalArgumentException();
		return ret; }

	/** Returns the current Item Count directly.
	 * @return  the Number of Items in the Collection represented by a 32 Bit Integer
	 * Since conventional Memory is usually limited to 4 GByte on 32 Bit Machines,
	 * 'int' is being used instead of 'long'.	 */
	public int     getInt() { return itemCount; }
/*		int Counter = 0;
		Iterator iter = this.Iterator();
//		ByRefLong moreItems = new ByRefLong();
		while (iter.nextItem() != Iterator.EOI) //  moreItems) != null)
			Counter++;
		return Counter;	} */

	/** Returns the Collection's Item Count as a 64 Bit Integer.
	 * @return  the Number of Items in the Collection represented by a 64 Bit Integer	 */
	public long   getLong() { return itemCount; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface OrderAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Virtual Method!
	 * less: '<' Returns True, when 'Self' < arg
	 * Implemented only to make this class concrete for delegation.
	 * Should be overwritten!
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean isLessThan (Object arg) { return this.isLessThan(arg); } // ! this.grtrEq(arg); }


	////////////////////////////////////////////////////////////////////////////
	//	Interface Container
	////////////////////////////////////////////////////////////////////////////

	/**Returns the Degree of the given Object,
	 * i.e. how often it appears in the Container.
	 * This is done very fast for the Bag and used for the Relation / Tree
	 * to determine the (Out-)Degree of the Nodes.
	 */
	public int Degree(Object Item) {
		int ret = 0;
		Object curr;
//		try {
			reSet ();
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x); }
		while ((EOI != (curr = findNext(Item))) && isValid()) {
			if ((Item == curr) || Item.equals(curr)) {
				++ret; } }
		return ret; }

	/** Sets, adds, replaces or flips the Item if it already existed,
	  * as specified by the second Parameter 	*/
	public Object set(Object Item, int ifExists) {
		Object ret = IStreamIn.EOI;
		boolean found = false;
		try {
			if (ifExists != IF_EXISTS_ADD) { //not necessary
				found = (EOI != (ret = findFirst(Item))) || isValid(); }
			switch (ifExists) {
				case IF_EXISTS_ADD:                                 addItem(Item);   break;
				case IF_EXISTS_UNION:   if (!found) {               addItem(Item); } break; //ret = Item; break;
				case IF_EXISTS_REPLACE: if ( found) { removeCurr(); addItem(Item); } break;
				case IF_EXISTS_REMOVE:  if ( found) { removeCurr();                } break;
				case IF_EXISTS_FLIP:    if ( found) {               addItem(Item); } break;
			default: throw new InvalidParameterException(); }
		} catch (ModificationException e) { throw new ReadOnlyException(e);
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x);
		} return ret; }

	/** Adds or replaces the given <code>Item</code> by the one specified.
	  * The Item can not be <code>null</code>.
	  * <p>
	  *
	  * @param	  Item	 the HashContainer Item.
	  * @return	 the Item replaced in this HashContainer,
	  *			 or <code>null</code> if it did not exist before.
	  * @exception  NullPointerException  if the Item or Item is
	  *			   <code>null</code>.
	  * @see	 java.lang.Object#equals(java.lang.Object)
	  * This Operation is optimized in this Class,
	  * because 'iter' still points to the correct Location */
	public Object setItem(Object Item) {
		Object ret = IStreamIn.EOI;
		try {
			if ((IStreamIn.EOI != findFirst(Item)) || (availAble() > 0)) {
				ret = removeCurr(); }  //this is an Optimization
		} catch (ModificationException e) { throw new ReadOnlyException(e);
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x);
		} addItem(Item);
		return ret; }

	/** Replaces the given <code>Item</code> by the one specified.
	  * The Item can not be <code>null</code>.
	  * <p>
	  *
	  * @param	  Item	 the HashContainer Item.
	  * @return	 the previous Item of the specified Item in this HashContainer,
	  *			 or <code>null</code> if it did not have one.
	  * @exception  NullPointerException  if the Item or Item is
	  *			   <code>null</code>.
	  * @see	 java.lang.Object#equals(java.lang.Object)
	  * This Operation is optimized in this Class,
	  * because 'iter' still points to the correct Location */
	public Object replaceItem(Object Item) {
		Object ret = IStreamIn.EOI;
		try {
			if ((IStreamIn.EOI != findFirst(Item)) || (availAble() > 0)) {
				ret = removeCurr();  //this is an Optimization
				addItem(Item); } //
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x); }
		} catch (ModificationException e) { throw new ReadOnlyException(e);
		} return ret; }

	/** Replaces <code>Item1</code> by  <code>Item2</code>.
	  *  <code>Item1</code> can not be <code>null</code>.
	  * <p>
	  *
	  * @param	  Item1	the Item searched.
	  * @return	  the first Instance equivalent to the specified Item in this Container,
	  *			 or <code>null</code> if it there is none.
	  * @exception  NullPointerException  if the Item or Item is
	  *			   <code>null</code>.
	  * @see	 java.lang.Object#equals(java.lang.Object)	 */
	public Object replaceItem(Object Item1, Object Item2) {
		Object obj = IStreamIn.EOI;
//		try {
			if ((EOI != findFirst(Item1)) || isValid())
				obj = replaceCurr(Item2); //this is an Optimization
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x); }
		return obj; }

	/** Replaces the Items from Item with the ones from Item2 in this Container
	  * @return true, if found any, i.e. Container is changed, otherwise false	 */
	public boolean replace(Object Item1, Object Item2) {
		boolean ret = false;
		Object obj1, obj2;
		IIStreamIn Iter1 = null;
		IIStreamIn Iter2 = null;
		if (Item1 instanceof IIStreamIn) Iter1 = (IIStreamIn) Item1;
		if (Item2 instanceof IIStreamIn) Iter2 = (IIStreamIn) Item2;
		if (Item1 instanceof IIterAble ) Iter1 = ((IIterAble) Item1).Iterator();
		if (Item2 instanceof IIterAble ) Iter2 = ((IIterAble) Item2).Iterator();
		while(((EOI  != (obj1 = Iter1.nextItem())) || Iter1.isValid()) &&
			  ((EOI  != (obj2 = Iter2.nextItem())) || Iter2.isValid())) {
			if (replaceItem(obj1, obj2) != EOI) ret = true;
		} return ret; }

	/** Removes this Item from the Container
	  * This method does nothing if the Item is not in the HashContainer.
	  * Corresponds to subAt(), but retained, because it also returns Information
	  * whether the Container was changed.
	  * @param   Item   the Item that needs to be removed.
	  * @return the Item, if found, otherwise 'null' resp 'EOI'	 */
	public Object removeItem(Object Item) { //throws ModificationException {
		try {
			if ((EOI != enm.findFirst(Item)) || enm.isValid())
				return enm.removeCurr(); //this is an Optimization
		} catch (ModificationException x) { throw new ReadOnlyException(x);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); }
		return IStreamIn.EOI; }

	/** Removes these Items from the Container
	  * Corresponds to subAt(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return true, if found any, i.e. Container is changed, otherwise false	 */
	public boolean remove(Object Item) { //throws ModificationException {
		return subAtDIFFat(Item, false); }

	/** Unites this Item (the set consisting of only this Item) with the Container
	  * Similar to addAt(), this is used for Sets, which don't accept duplicate Objects
	  * @see addAt()
	  * @see  ORat()
	  * @return true, when the Item was added	 */
	public boolean unionItem(final Object Item) {
//		this.reset();	//this must be restartAble for this.
//		try {
			if ((findFirst(Item) == EOI) && !isValid()) {
				addItem(Item); return true; }
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x); }
		return false; }

	/** Unites this Item (the set consisting of only this Item) with the Container
	  * Similar to addAt(), this is used for Sets, which don't accept duplicate Objects
	  * To support alternative Unions an additional ITester can be supplied.
	  * @see addAt()
	  * @see  ORat()
	  * @return true, when the Item was added	 */
	public boolean unionItem(Object Item, ITester tst) {
//		this.reset();	//this must be restartAble for this.
//		try {
			if ((firstOfEachThatEqualsThat(Item, tst) == EOI) && !isValid()) {
				addItem(Item); return true; }
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x); }
		return false; }

	/** Adds or removes this Item from the Container
	  * Corresponds to XORat(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return the Item, if found, otherwise 'null' resp 'EOI'
	  * TODO: should throw a NoSuchMethodException when the Container is read only! */
	public Object flipItem(Object Item) {
		try {
			if ((findFirst(Item) != EOI) || isValid())
				return removeCurr();
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x); }
		} catch (ModificationException e) { throw new ReadOnlyException(e);
		} addItem(Item); return null; }

	/** Adds or removes these Items from the Container
	  * Corresponds to XORat(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return the Item, if found, otherwise 'null' resp 'EOI'
	  */
	public boolean flip(Object Item) {
		boolean ret = false;
		Object obj;
		IIStreamIn Iter = null;
		if (Item instanceof IIStreamIn) Iter = (IIStreamIn) Item;
		if (Item instanceof IIterAble ) Iter = ((IIterAble) Item).Iterator();
		if (Iter instanceof IStreamIn) {
			setCapacity(getInt () + (int) ((IAvailAble)Iter).availAble()); }
		while((EOI  != (obj = Iter.nextItem())) || Iter.isValid()) {
			ret |= (flipItem(obj) != null);
		} return ret; }

	/** Removes all Objects from the Container except for this one.
	  * Corresponds to ANDat(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return true, if the Item existed, otherwise false	 */
	public boolean retainItem(Object Item) {
		boolean ret = contains(Item); FalseAt(); //zeroAt();
		if (ret) addItem(Item);
		return ret; }

	/** Removes all Objects from the Container except for the ones from this streamIO.
	  * Corresponds to ANDat(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return true, if the Container is changed, otherwise false	 */
	public boolean retain(Object arg) {
		boolean ret = false;
		IStreamIn arg_ = null;
		Object currItem;
		try {
			if (arg instanceof IStreamIn) arg_ = (IStreamIn) arg;
			if (arg instanceof IIterAble) arg_ = (IStreamIn) ((IIterAble) arg).Iterator();
			this.reSet(0); //check all Elements whether they can stay in this Container
			while ((EOI != (currItem = this.nextItem())) || isValid()) {
				arg_.reSet();	//arg must be restartAble for this.
				if (EOI == arg_.findNext(currItem) && !arg_.isValid()) { //when not found...
					this.removeCurr(); ret = true; } } //remove all Items that don't appear in arg.
		} catch (ModificationException e) { throw new ReadOnlyException(e);
		} return ret; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface CopyAble : Implementation
	////////////////////////////////////////////////////////////////////////////////

	/** Integrates deepCopyAt() and shallopCopyAt().
	  * @param  Object to deep copy the Contents from.
	  * @return 'this' but with the Contents or arg
	  *
	  * @throws NoSuchMethodError when the reset() Method is not supported
	  *
	  * Does a Copy to a certain Level
	  * i.e. also inner Components are copied up to the Depth.
	  *
	  * Returns the itself for further use.
	  *
	  * Depth is only valid >= 0, for 0 only copy() is valid and returns itself.
	  * Does not flatten the Items, but creates Copies of the inner Items.
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see CopyStreamIn
	  * @see CopyStreamOut
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening 	 */
	public ICopyAble copyAt(Object arg, int Depth) {
		FalseAt(); //zeroAt();
		if (--Depth == 0) { addItems(arg); return this; } //that would be a shallow Copy
		IIStreamIn arg_ = null; //code nearly identical to ORat()
		if (arg instanceof IIterAble ) { arg_ = ( IStreamIn) ((IIterAble) arg).Iterator(); } else
		if (arg instanceof IIStreamIn) { arg_ = (IIStreamIn) arg; } else
		if (arg.getClass().isArray()) {	arg_ = new ArrayEnum((Object[])arg); }
		//TODO: is Sequence important here?!?
		if (arg_ instanceof IStreamIn)
			((IStreamIn) arg_).reSet();
		CopyStreamOut.stream(arg_, this, Depth);
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IGroupM: Implementation
	//	Multiplication cannot be done in Place!
	////////////////////////////////////////////////////////////////////////////////

	/** Multiplication in Place: *=
	  * @return the Product of this and arg in Place: this*=arg
	  * This Product is ordered and thus not commutative!!!	 */
	public ISemiGroupM mulAt(Object arg) {
		copyAt(mul(arg)); return this; }  //TODO: rather copy from the Stream than to create a new Container!

	/** Multiplication : *
	  * @return the Product of this and arg: this*arg
	  * Multiplies the Items of arg to this Container (Cross Product).
	  * That is the (ordered) Combination (Pair, 2-Tupel) of Items from both Containers
	  * The Number of these Pairs is |A*B| = |A|*|B|
	  * Two Containers are joined, creating the cross Product of both tables
	  * -with the Sum of both Columns as Columns and ...
	  * -Pairs of the first and the second rows as Rows (flattened).
	  *
	  * n-Tupel are created by iteratively creating 2-Tupel.
	  * This Product is ordered and thus not commutative!!!
	  * The Items in the two Containers are associated.
	  *
	  * @see flatten()
	  * @see flattenAt()
	  *
	  * Choosing the Cartesian Product, not the (more complicated) Cantor Product,
	  * because it is more common and we are dealing with finite Sets here!
	  * Reuses the streamIO Implementation in...
	  * @see Product  */
	public ISemiGroupM mul(Object arg) {
		IStreamIn arg_ = null;
		if (arg instanceof IStreamIn) arg_ = (IStreamIn) arg;
		if (arg instanceof IIterAble) arg_ = (IStreamIn) ((IIterAble) arg).Iterator();
		Container ret = (Container) newInstance(); //empty Container
		ret.setCapacity(getInt () * (int)arg_.availAble()); //reserve Space... I could even return an Array here, because the Size is well known with Containers!
		Product Enum;
		try { Enum = new Product(this, arg_);
		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); }
		ret.copyAt(Enum); //this Command streams the whole Contents from the Enumeration into the Container
		return ret; }

	/**	Join Operations are just Multiplications filtered by the WHERE Criteria!
	  * The Criteria can be given by a ITester applied on the Pair
	  * or an Equivalence applied to both Items of the Pair.
	  * or by plainly comparing certain Items of the flattened Pair.
	  * all this is implemented in the classes
	  * @see JoinStreamByTest
	  * @see JoinStreamByCols
	  * @see JoinStreamByEquivalence
	  */

	/** Joins a Table with Equality Equation between Column i of this Collection
	  * and Column j of arg. Therefore both this and arg have to be Tables
	  * (i.e. Collections of Collections)
	  * Since a new Container would be created, a streamIO is being used. */
/*	public Container joinByCols (int i, boolean[] Cols, Container arg, int j, boolean[] ColsArg) {
		Container res = (Container)  newInstance();
		Container o1; Iterator i1 =		Iterator();
		Container o2; Iterator i2 = arg.Iterator();
		res.ensureCapacity(getInt() + arg.getInt());	//the typical Join creates as many rows as the larger table has
		while ((o1 = (Container)i1.nextItem()) != Iterator.EOI) { // moreItems)) != null) {
			Object Test = o1.getAt(i);
			try { i2.reset(); } catch (OperationNotSupported e) { throw new AbstractMethodError(e.toString()); }
			while ((o2 = (Container) i2.nextItem()) != Iterator.EOI) // moreItems)) != null)
				if (o2.getAt(j).equals (Test))
					res.addAt (((Container) newInstance()).addColsAt(o1, Cols).addColsAt(o2, ColsArg));
		}
		return res; }

	/** Joins a Table with another and the Criterion given by 'Condition'.
	  * Therefore both this and arg have to be Tables (i.e. Collections of Collections)
	  * Since a new Container would be created, a Stream is being used. */
/*	public Container join (Container arg, ITester Condition) {
		Container res = (Container)  newInstance();
		Container o1; Iterator i1 =		Iterator();
		Container o2; Iterator i2 = arg.Iterator();
		res.ensureCapacity(getInt() + arg.getInt());	//the typical Join creates as many rows as the larger table has
		while ((o1 = (Container)i1.nextItem()) != Iterator.EOI) { // moreItems)) != null) {
			Container tmp;
			try { i2.reset(); } catch (OperationNotSupported e) { throw new AbstractMethodError(e.toString()); } //moreItems);
			while ((o2 = (Container) i2.nextItem()) != Iterator.EOI) // moreItems)) != null)
				if (Condition.Test (tmp = (Container)((Container)o1.copy()).addAt(o2)))
					res.addAt (tmp);
		}
		return res; }

*/
	/** Creates the Power Set of a Container: P(A)
	  * This is the Set of all SubSets of A, inclusive the empty Set and A itself.
	  * The Empty Set should be unique, e.g. the Null Pointer!
	  * The Number of Subsets is |P(A)| = 2^|A|
	  * because each Bit decides on the existence of an Element to the Set or not.
	  *
	  * Application of this Power Set is e.g. to create the Set of all Relations A->B
	  * by first creating all Mappings A->B resulting in |A->B| = |A*B| = |A|*|B|.
	  * The Cardinality of this Power Set would then be 2^|A|^|B| = 2^|B|^|A|	 */
	public Container[] Potency () {
		int n = 1 << getInt ();	//can only create for Sets with |A| <= 32
		Container[] A = new Container[n];
		while (--n >= 0)
			A[n] = (Container) newInstance(); A[n].addItems(this, n);
		return A; }

	/** Creates the Power of both Containers: A^X
	  * This is the set of all possible Mappings X->A
	  *
	  * The Items in the two Containers are associated.
	  * The Number of Associations is |A^X| = |A| ^ |X| = |A| * |A| * ... * |A|
	  *
	  * with Elements like (a@x,b@x,c@x...)*(a@y,b@y,c@y,...)*...
	  * giving Elements like ((a@x)^(a@y)^(a@z)^...),((b@x)^(a@y)^(a@z)^...),(...
	  *
	  * The Power with an Integer Number is already defined in SemiGroup:
	  * a^(n+1) = a * a^n
	  * giving Associations like a@a@b@a@c@a
	  *
	  * In contrast to this is the Potency Set which is the set of SubSets and it's Size is
	  * |Potency(A)| = 2 ^ |A|
	  *
	  * Instead of just Creating the n-th Power of A,
	  * which you can also accomplish using the Pow Method from SemiGroupM,
	  * this Method creates Associations, allowing to interpret the Elements
	  * as Relations (in fact: Functions).
	  *
	  * Design Decisions:
	  * The resulting Elements are chained Pairs which have to be flattened
	  * into a Container to be used as a Relation
	  * (in fact if you view the chained Pairs as a linked List, this is just an addAt() of a Container.)
	  * Similarly joins have to be flattened. */
	public Container pow (Container arg) {
		Container prod = null;	//Contains the accumulated Product
		Container fact = (Container) newInstance();	//Contains the Factor
		int Cap;  fact.setCapacity(Cap = getInt ());

		Object argItem, item;
		IStreamIn argIter = arg .ChangeIterator();
		IStreamIn    iter = this.ChangeIterator();
		argIter.reSet();
		while (((argItem = argIter.nextItem()) != IIStreamIn.EOI) || (argIter.availAble()  >= 0)){ //Create the Pairs
			iter.reSet();
			while(((item =    iter.nextItem()) != IIStreamIn.EOI) || (   iter.availAble()  >= 0)){ //create a Set of Pairs with constant right side.
				fact.addAt(new Association (argItem, item)); }
			if (prod == null) { 	//Multiply this Set by the Set of Pairs
				prod =  fact; fact = (Container) newInstance(); fact.setCapacity(Cap);
		//	} else { prod.mulAt(fact); fact.zeroAt(); }	//this is slower!
			} else { prod = (Container) prod.mul(fact); fact.zeroAt(); }	//after this, the Capacity stays the same.
		}
		return prod; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface ILattice: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/** Delegates to {@link #FalseAt()}.
	 * @return this, set to 0 in Place:
	  * Can be implemented by subtracting any number from itself.
	  * A Standard Implementation. Should be overwritten by faster Implementations.	 */
	public IGroup zeroAt() { FalseAt(); return this; }

	/** Removes every Item by repeatedly calling removeCurr() over a full Iteration; a
	 * generic, slow fallback for subclasses without a faster clear Operation.
	 * @return this, set to the Boolean Constant for the Representation of 'false' = 0
	  * i.e. not 'true'.
	  * For Conatainers this is equivalent to zeroAt() and clear()
	  * @see zeroAt()	 */
	public Boole FalseAt() {
		try {
			reSet ();
			while (availAble() > 0) {
				while (nextItem() != EOI) {
					removeCurr(); }
	//			reset(); //reset again to bring the Cursor into a defined Position.
			}
		} catch (ModificationException e) { throw new ReadOnlyException(e);
//		} catch (NoSuchMethodException x) { throw new OperationNotSupported(x.toString(), x);
//		} catch (NoSuchMethodError     e) {
		} return this; }

	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'False'.
	 * For Sets: The Set of ALL Elements (only posssible for known finite SuperSets) */
	public Boole TrueAt() { copyAt(True); return this; }

	/**Boolean Constant for the Representation of 'true': 1
	 * i.e. NOT 'false'.
	 * For Sets: The Set of ALL Elements (only posssible for known finite SuperSets) */
	public Boole True() { return True; }

	/** Boolean NOT Operation in Place: ~=, != for single Bit
	  * @return the boolean Inverse of this in Place: =! this	*/
	public Boole NOTat	() { copyAt(NOT()); return this; } //TODO: rather copy from the Stream than to create a new Container!

	/** Boolean NOT Operation: ~=, != for single Bit
	  * @return the boolean Inverse of this: !this	*/
	public Boole NOT	() {
		Container ret = (Container) newInstance();
		DIFF Enum = new DIFF(True, this);
		ret.copyAt(Enum);
		return ret; }

	/** Boolean AND Operation in Place: &=, &&= for single Bit
	  * @return this &= arg
	  * @see retain()
	  * This can be done in place, which is cheaper than creating an AND streamIO.	 */
	public Lattice ANDat (Object arg) {
		retain(arg);
		return this; }

	/** Boolean OR Operation in Place: |=, ||= for single Bit
	  * can be calculated in place.
	  * @return this |= arg
	  * |A OR B| == |A| + |B| - |A AND B|
	  */
	public Lattice ORat  (Object arg) {
		union(arg); //the Set accepts only Items that don't exist
		return this; }

	/** Unites the Items of the streamIO or Container to this Container
	  * Corresponds to ORAt(), but retained, because it also returns Information
	  * whether the Container was changed.
	  * This is only important for Sets, which don't accept duplicate Objects
	  * @see addAt()
	  * @see  ORat()
	  * @return true, if found any, i.e. Container is changed, otherwise false	 */
	public boolean union(Object Item) { return union(Item, null); }

	/** Unites the Items of the streamIO or Container to this Container
	  * Corresponds to ORAt(), but retained, because it also returns Information
	  * whether the Container was changed.
	  * This is only important for Sets, which don't accept duplicate Objects
	  * @param EQ stricter Equivalence Relation than equals() used to test the Items
	  * @see addAt()
	  * @see  ORat()
	  * @return true, if found any, i.e. Container is changed, otherwise false	 */
	public boolean union(final Object Item, final IEquivalence EQ) {
		boolean ret = false;
		IIStreamIn Iter = null;
		if (Item instanceof IIStreamIn) Iter = (IIStreamIn) Item;
		if (Item instanceof IIterAble ) Iter = ((IIterAble) Item).Iterator();
		if (Iter instanceof IStreamIn) {
			setCapacity(getInt () + (int)((IAvailAble)Iter).availAble()); }
		AReSetAble.TRY_TO_RESET(Iter, AContainer.class.toString()); 
		for(Object obj; (EOI  != (obj = Iter.nextItem())) || Iter.isValid();) {
			this.reSet();	//this must be restartAble for this.
			if (EOI == findNext(obj, EQ) && !isValid()) { //when not found...
				this.addItem(obj); ret = true; } //remove all Items that don't appear in arg.
			//ret |= unionItem(obj); //equivalent, but slower!
		} return ret; }

	/** Boolean DIFF Operation in Place: -=
	  * can be calculated in place.
	  * A DIFF B <=> (A AND NOT B) <=> NOT IMP
	  * For Sets:	Difference Set ; can also be defined without NOT!
	  * Just like with Addition it is:
	  * |A DIFF B| == |A| - |A AND B| == |A OR B| - |B|
	  * |A  OR  B| == |A| + |B| - |A AND B|	 */
	public Lattice DIFFat(Object arg) {
		subAtDIFFat(arg, false); return this; }

	/** Boolean DIFF Operation in Place: -=
	  * or Subtraction in Place: -=
	  * can be calculated in place.
	  * DIFFat can take any Argument
	  * subAt accepts only Arguments that are contained in this Container.
	  * @return true if the Container was changed
	  * @throws InvalidParameterException when subt = true
	  * 	and not all Items could be removed from the Container */
	public boolean subAtDIFFat(Object arg, boolean subt) {
		boolean ret = false;
		IIStreamIn arg_ = null; //code nearly identical to ORat()
		Object currItem;
		if (arg instanceof IIterAble){arg_ = ((IIterAble) arg).Iterator(); ((IStreamIn) arg_).reSet(); } else
		if (arg instanceof IIStreamIn) arg_ = (IIStreamIn) arg; else
			return subAtDIFFatItem(arg, subt);
		while ((EOI != (currItem = arg_.nextItem())) || arg_.isValid()) {
			if (removeItem(currItem) != EOI) {
				ret = true;
			} else if (subt)
				throw new InvalidParameterException("'" + currItem.toString() + "' does not appear in the Container and cannot be subtracted. Use DIFFat() instead!");
		}
		return ret; }

	/** Boolean DIFF Operation in Place: -=
	  * or Subtraction in Place: -=
	  * can be calculated in place.
	  * DIFFat can take any Argument
	  * subAt accepts only Arguments that are contained in this Container.
	  * @return true if the Container was changed
	  * @throws InvalidParameterException when subt = true
	  * 	and not all Items could be removed from the Container */
	public boolean subAtDIFFatItem(Object arg, boolean subt) {
		if (removeItem(arg) != EOI) {
			return true;
		} else if (subt)
			throw new InvalidParameterException("'" + arg.toString() + "' does not appear in the Container and cannot be subtracted. Use DIFFat() instead!");
		return false; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IGroup : Implementation
	////////////////////////////////////////////////////////////////////////////////

	/** Addition in Place: +=
	  * @return ths Sum of both Containers.
	  * @see ORat()
	  * For MultiSets that allow multiple Occurrences of equal and even identical Objects
	  * this results in the following Homeophism: |A + B| = |A| + |B|	 */
	public ISemiGroup addAt(Object arg) {
		if (arg instanceof IIStreamIn) { //Containers and Streams...
			addItems(arg);
		} else { //"normal" Objects
			addItem (arg);
		}
		return this; }

	/** Subtraction in Place: -=
	  * This cannot be defined properly on Containers,
	  * especially when |arg| > |this|,
	  * because there is no Debit Set. */
	public IGroup subAt (Object arg) {
		subAtDIFFat(arg, true); return this; }

	////////////////////////////////////////////////////////////////////////////////
	//	Optimizations:
	////////////////////////////////////////////////////////////////////////////////


	/**Tests if this Array has no components.
	 * Returns true, when the Collection contains no Items.
	 *
	 * @return  <code>true</code> if this Array has no components;
	 *		  <code>false</code> otherwise.
	 *
	 * This generic Solution may be slow and should be redefined.
	 */
	public boolean isZero() {
		return 0 == itemCount; } //this.getInt(); }
//		return (this.first() != Iterator.EOI); } //much faster!


	////////////////////////////////////////////////////////////////////////////////
	//	optional Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Interface IterAble: optional Methods
	////////////////////////////////////////////////////////////////////////////

	/**Returns the next Item without moving to it.	 */
	public Object peekItem() { //throws NoSuchMethodException {
		return enm.peekItem(); }
//							 throw new NoSuchMethodException(); }// OperationNotSupported(); }

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     java.util.Enumeration
	  * @see     java.util.Iterator     */
	public IIStreamIn Iterator() {
		return enm.Iterator(); } //null; }
/*		try { return (IStreamIn) clone(); //usually it is sufficient to clone the current Iterator
		} catch (CloneNotSupportedException x) {
			throw new OperationNotSupported("Should not happen!", x); } }

	////////////////////////////////////////////////////////////////////////////
	//  Interface ChangeAble: optional Methods
	////////////////////////////////////////////////////////////////////////////

	/** Delegates to the single Iterator's own ChangeIterator.
	 * Returns a new Intstance of a ModStreamIn Iterator,
	  * which allows for changing the Data concurrently. */
	public ChangeIterator ChangeIterator() {
		return enm.ChangeIterator(); } // null; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface AlterAble: optional Methods
	////////////////////////////////////////////////////////////////////////////

	/** Returns a new Intstance of an alterable Object Input streamIO,
	  * which allows for changing the Data and structure concurrently.
	  * By Default return no new Iterators, except for the single one
	  * implemented implicitly in this Container. */
	public Enumerator Enumerator() {
		return enm.Enumerator(); } //null; }

	//Marking and Resetting a Stream (for re-Processing, if supported)

	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() {// throws NoSuchMethodException{
		reSet (0); return this; }

	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.	 */
	public long reSet(final long position) { //throws    NoSuchMethodException {
		return enm.reSet(position); 
	}
//	reset(); skip(Position); }

	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.	 */
	public IMarkAble mark() { //throws NoSuchMethodException { 
	    return mark (Long.MAX_VALUE); }

	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(long ReadLimit) { //throws    NoSuchMethodException {
		enm.mark(ReadLimit); return this; }
	//	throw new NoSuchMethodException(); }

	////////////////////////////////////////////////////////////////////////////////
	//	Interface Enumerator: optional Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Adds the given Item after the current Object to the Container.
	  * Returns the Container to allow for concatenated adding.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * In this Case the Exception is thrown.
	  * That is why this Method should throw an Exception if removing is not allowed.
	  * Could also return a boolean whether the Method is supported or not
	  * @param Object to be added at the next Position
	  * @return the Enumerator to allow for concatenated Adding
	  * @throws ModificationException when the Container is sorted or read only
	  */
	public Enumerator addNext(final Object Item) throws ModificationException {
		enm.addNext(Item); return this; }
//		if (Cont != null) Minor = Cont.getMinor(); //re-read the Minor, because this Enumerator is aware of the Change.
//		throw new ModificationException(); }

	/** Removes the next Object from the Set and Iteration,
	  * returns the removed Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  * @return the removed next Object, EOI when at the End of the streamIO
	  * @throws ModificationException when the Container is read only
	  */
	public Object removeNext() throws ModificationException {
		return enm.removeNext(); }
//		if (Cont != null) Minor = Cont.getMinor(); //re-read the Minor, because this Enumerator is aware of the Change.
//		throw new ModificationException(); }

	/**Replaces the next Object from the streamIO with this Item.
     * It should also update the Minor Version (or let the Container update it)
     * to announce the Change to other Iterators.
	 * This Operation can be used to e.g. influence Parsers concurrently.
	 * @param Object to replace the next Item in the Container
	 * @return the next Item in the Container
	 * @throws ModificationException when the Container is sorted or read only
     */
	public Object replaceNext(Object Item) { //throws ModificationException {
		return enm.replaceNext(Item); }
//		throw new ModificationException(); }

	////////////////////////////////////////////////////////////////////////////
	//	abstract Methods
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//  Interface ChangeStreamIn: abstract Methods
	////////////////////////////////////////////////////////////////////////////

    /**Replaces the current Object in the Container with the given Item.
     * One Problem is other Enumerators that concurrently work through this Container.
     * Another Problem is that removing the Item may not be possible at all.
     * In this Case the Exception is thrown.
     * That is why this Method should throw an Exception if replacing is not allowed.
     * It should also update the Minor Version (or let the Container update it)
     * to announce the Change to other Iterators.
     */
	public Object replaceCurr(Object Item) { //throws ModificationException { // { throw new OperationNotSupported(); }
		return enm.replaceCurr(Item); }

	////////////////////////////////////////////////////////////////////////////
	//  Interface AlterStreamIn: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Removes the current Object in the Container with the given Item.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * In this Case the Exception is thrown.
	  * That is why this Method should throw an Exception if replacing is not allowed.
	  * It should also update the Minor Version (or let the Container update it)
	  * to announce the Change to other Iterators.
	  */
	public Object removeCurr() throws ModificationException {
		return enm.removeCurr(); }
//		throw new ModificationException(); }

	////////////////////////////////////////////////////////////////////////////////
	//	Interface Pipe
	////////////////////////////////////////////////////////////////////////////////

	/** Delegates to the single Iterator's own Order.
	 * @return the Order in which Elements are returned or processed.
	  * @see  addItem() how the Objects are added to the Container
	  * @see nextItem() how the Objects are retrieved from the Container	 */
	public byte getOrder() { // { return Pipe.OrderUnDef; }
		return enm.getOrder(); }

	/** Delegates to the single Iterator's own Comparator.
	 * @return The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see Comparable  or
	  * @see IIOrderAble	 */
	public Comparator getComparator () {
		return enm.getComparator(); }

	////////////////////////////////////////////////////////////////////////////////
	//	Interface Enumerator
	////////////////////////////////////////////////////////////////////////////////

	/** Delegates to the single Iterator's own availability.
	 * @return the (minimum) Number of Items left (in the Buffer),
      * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble() {
		return enm.availAble(); }

	/** Delegates to the single Iterator's own nextItem().
	 * @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public Object nextItem() {
		return enm.nextItem(); }

	/** Returns the current Object.
	  * Could be removed here, because most Iterators have fast Access to the current Item	 */
	public Object currItem() {
		return enm.currItem(); }

	////////////////////////////////////////////////////////////////////////////////
	//	Interface Object
	////////////////////////////////////////////////////////////////////////////////

	/** Renders this Container's Items, comma-separated inside curly Braces.
	 * @return  a string representation of the object.
	  * In general, the toString method returns a string that "textually represents" this object.
	  * The result should be a concise but informative representation that is easy for a person to read.
	  * It is recommended that all subclasses override this method.
	  *
	  * This replaces the Implementation in
	  * @see AStreamIn where no reset() takes place.
	  */
	public String toString() {
		StringBufferOutputStream SBOS = new StringBufferOutputStream();
		StreamOutPrimitive Out = new StreamOutPrimitive(SBOS);
		enm.reSet();
		Out.print('{'); AStreamOut.STREAM(this, Out);
		Out.print('}');
		return SBOS.getBuffer().toString();	}

	//////////
	// 	not used...

	/** Flattens this Container filled with Pairs (e.g. from a Multiplication or Join),
	  * i.e. stores the key and the Value in one (new) Container.
	  * @return a new Container filled with Containers of this Type containing the flattened Pairs contained in this one.
	  * Flattening leads to a cross Product with no clear distinction
	  * between the Elements of the first and second Set.
	  * This makes sense only for Containers with an Order, like Arrays or Lists,
	  * or if the Items have an internal Order like Associations do.	 */
	public Container flatten() {	//Input : Container filled with Pairs
//		boolean rightIsContainer;
		Container tmp, ret = (Container) newInstance();
		final IStreamIn i1 = this.ChangeIterator();
		i1.reSet();
		for(Object o1; EOI != (o1 = i1.nextItem()) || i1.isValid();) { //
			tmp = (Container) newInstance();
			tmp.flattenItem((KeyValuePair) o1);
			ret.addAt(tmp);
		} return ret; }	//Output: Container filled with Containers

	/** Recursively flattens a Pair into this Container,
	  * i.e. stores the key and the Value in this Container next to each other.
	  * @return this Container after filling in the Items of the Pair.
	  * @param Item is split up recursively and the Items are added to this Container
	  * Flattening leads to a cross Product with no clear distinction
	  * between the Elements of the first and second Set.
	  * This makes sense only for Containers with an Order, like Arrays or Lists,
	  * or if the Items have an internal Order like Associations do.	 */
	public Container flattenItem(KeyValuePair Item) {	//
		addAt(Item.val);
		if (Item.key  instanceof  KeyValuePair) {
			flattenItem((KeyValuePair) Item.key);
		} else { addAt(Item.key); }
		return this; }

	/** Recursively flattens any IPair Chain into this Container,
	  * i.e. stores the key and the Value in this Container next to each other.
	  * @return this Container after filling in the Items of the Pair.
	  * @param Item is split up recursively and the Items are added to this Container
	  * Flattening leads to a cross Product with no clear distinction
	  * between the Elements of the first and second Set.
	  * This makes sense only for Containers with an Order, like Arrays or Lists,
	  * or if the Items have an internal Order like Associations do.	 */
	public Container flattenItem(IPair Item) {	//
		addAt(Item.getVal());
		Object Key = Item.getKey();
		if (Key instanceof IPair) {
			flattenItem((IPair) Key);
		} else { addAt(Key); }
		return this; }

	//	Any Object that implements ILinked could be flattened like this,
	//	but then I would also flatten an Association.

	///////////////////////////////////////////////////////////////////////////////
	//  relational Operations consider a Container as being
	//  either a ColumnSet, where the Elements are not Containers
	//  or a RowSet, where the Elements are Containers, being ColumnSets.
	//
	//  A Join is the Cross Product of two Container
	//  and then aggregating the Sum of the Rows into the Elements of a Container
	///////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////
	// similarly, Matrices are RowSets consisting of ColumnSets.
	// with Tensors the RowSets can again be Tensors of lower Degree,
	// but these are not used for Scalar Products
	//
	// A Scalar Product is the Cross Product of two Containers
	// and then aggregating the Sum of the Rows into a Vector
	///////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////
	//	Column Operations: adds only certain Items from an Iterator to the Container
	///////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////
	// Interface IStreamOut
	///////////////////////////////////////////////////////////////////////////////

	/** Does nothing; this Container has no buffered output to flush.
	  * @see streamIO.IStreamOut#flush()	 */
	public void flush() {}
	
	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * Default Operation is addNext, which is easy to implement for Lists,
	  * as well as it ensures that the Item will be picked up by the current Iterator,
	  * which is frequently used e.g. by LL(1) Parsers.
	  * @see Order()
	  * @see nextItem()	 */
	public IIStreamOut addItem(final Object arg) { 
		try { return addNext(arg); 
		} catch (final ModificationException x) { 
			throw new ReadOnlyException(x.toString(), x); 
		} 
	}

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers' Contents is added,
	  * but not recursively, only flattened by one Level (flatDepth == 1).
	  * Named with capital A, to distinguish it from streamIO.Copy.Group.add()
	  * @param arg Object to be added to this Container
	  * @return this Output streamIO to enable concatenated adding. */
	public long addItems(final Object arg) {
		return AStreamOut.ADD_ITEMS(this, arg, 1); }

	/** adds these Items to the Store in Place: +=
	  * @return the Output streamIO to allow for further Items to be added
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but only recursively, when flattened is true.	 */
	public long addItems(Object arg, int flatDepth) {
		return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }

	/** adds these Items to the Store in Place: +=
	  * @return the Output streamIO to allow for further Items to be added
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public long addItems(Object[] arg) {
		return AStreamOut.ADD_ITEMS(this, arg); }

	/** adds all Items from the Enumerator to the Store in Place: +=
	  * @return the Output streamIO to allow for further Items to be added
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public long addItems(IIStreamIn Iter) {
		return AStreamOut.STREAM(Iter, this); }

	/** Adds the Items with Cols.a[i] == true to this Container in Place. 	 */
	public Container addItems(IIStreamIn iter, boolean[] Cols) {
		setCapacity(getInt () + Cols.length); // iter.availAble());
//		AStreamOut.stream(new FilterInByBoolean(iter, Cols), this); return this; }
		int i = -1;
		Object TestItem = IIStreamIn.EOI;
		while ((++i < Cols.length) &&
			((TestItem = iter.nextItem()) != EOI) || iter.isValid()) {
			if (Cols[i]) {
				addAt(TestItem); }
		} return this; }

	/** Adds the Items with the Bits Cols[i] == 1 to this Container in Place. 	 */
	public Container addItems(IIStreamIn iter, int Cols) {
		if (iter instanceof IStreamIn) {
			setCapacity(getInt () + (int) ((IAvailAble)iter).availAble()); }
//		AStreamOut.stream(new FilterInByBitMask(iter, Cols), this); return this; }
		Object TestItem = IIStreamIn.EOI;
		while ((Cols != 0) &&
			(((TestItem = iter.nextItem()) != EOI) || iter.isValid())) {
			if ((Cols & 1) != 0)
				addAt(TestItem);
			Cols >>= 1;
		} return this; }

	/** Adds the Items to this Container in Place.
	  * @return the Output streamIO to allow for further Items to be added
	  * The Difference to addItems(IStreamIn) is that the Container is reset()! */
	public Container addItems(Container iter) {
		iter.reSet(); addItems((IIStreamIn) iter); return this; }

	/** Adds the Items with Cols.a[i] == true to this Container in Place.
	  * @return the Output streamIO to allow for further Items to be added
	  * The Difference to addItems(IStreamIn) is that the Container is reset()! */
	public Container addItems(Container iter, boolean[] Cols) {
		iter.reSet(); addItems((IIStreamIn) iter, Cols); return this; }

	/** Adds the Items with the Bits Cols[i] == 1 to this Container in Place.
	  * @return the Output streamIO to allow for further Items to be added
	  * The Difference to addItems(IStreamIn) is that the Container is reset()! */
	public Container addItems(Container iter, int Cols) {
		iter.reSet(); addItems((IIStreamIn) iter, Cols); return this; }

	////////////////////////////////////////////////////////////////////////////////
	//	Column Operations
	//	These are Optimizations, also possible using Stream Filters,
	//  but these incur a Call Overhead AND Copying!
	////////////////////////////////////////////////////////////////////////////////

	/** Table Operation: In all Rows of this Container,
	  * Filters the Columns by the given Permutation
	  * @return this Object
	  */
	public Container filterAllCols(boolean[] Cols) {
		Container c1; Enumerator iter = enm; //Enumerator();
		iter.reSet();
		while ((EOI != (c1 = (Container)iter.nextItem())) || iter.isValid()) //
			c1.filterCols(Cols);
		return this; }

	/** Table Operation: In all Rows of this Container,
	  * Filters the Columns by the Bits in the given long
	  * @return this Object
	  */
	public Container filterAllCols(long Cols) {
		Container c1; Enumerator iter = enm; //Enumerator();
		iter.reSet();
		while ((EOI != (c1 = (Container)iter.nextItem())) || iter.isValid()) //
			c1.filterCols(Cols);
		return this; }

	/** Removes the Items with Cols.a[i] == false from this Container in Place.
	  * @return this Object
	  */
	public Container filterCols(boolean[] Cols) {
		int i = 0;
		Enumerator iter = enm; //Enumerator();
		try {
			iter.reSet();
			while ((EOI != iter.nextItem()) || iter.isValid()) { //
				if ((i >= Cols.length) || (! Cols[i++])) {
					iter.removeCurr(); } }
		} catch (final ModificationException e) { 
		    throw new ReadOnlyException(e);
		}
		return this; }

	/** Removes the Items with Bit i set from this Container in Place. 	 */
	public Container filterCols(long Cols) {
//		Object TestItem;
		Enumerator iter = enm; //Enumerator();
		try {
			iter.reSet();
			while ((Cols != 0) && ((EOI != iter.nextItem()) || iter.isValid())) { //
				if ((Cols & 1) != 0) {
					iter.removeCurr(); }
				Cols >>= 1; }
		} catch (final ModificationException e) { 
		    throw new ReadOnlyException(e);
		}
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//	Record / Row Operations:
	//	These are Optimizations, also possible using Stream Filters,
	//  but these incur a Call Overhead AND Copying!
	////////////////////////////////////////////////////////////////////////////////

	/** Filters the Rows by the given Filter Function, that tests each Row
	  * This is typically used for Rows, because their Number is not limited
	  * and they are all alike, so the same ITester can be applied.
	  *
	  * For filtering(Projecting) Columns, use filterCols
	  *
	  * Streams can be filtered using pluggable Filters.
	  * @see streamIO.Object.FilterByTester and
	  * @see streamIO.Object.FilterOutByTester
	  * They are not used here, because they incur a single Call Overhead
	  * AND involve copying the Container, which is modified in Place here!  */
	public Container filterRows(ITester RowFilter) {
//		Stream.Object.FilterByTester Filter = new Stream.Object.FilterByTester(mEnum, RowFilter, false);
		Object o1;
		Enumerator iter = enm; //Enumerator();
		try {
			iter.reSet();
			while ((EOI != (o1 = iter.nextItem())) || iter.isValid()) { //
				if (! RowFilter.test (o1)) 
						iter.removeCurr(); }
		} catch (ModificationException e) { 
		    throw new ReadOnlyException(e);
		}
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  Division from the Right or from the Left is a Filter / Mapping
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the Keys of the Elements in this Container
	  * no matter whether it contains Associations, Pairs or IPairs
	  * which is useful to determine the left Factor of a Product
	  * or the Definition Set of a Relation or Function */
	public Container getKeys() {
		Container ret = (Container) newInstance();
		ret.union(new FilterInPair(this, true));
		return ret; }

	/** Returns the Values of the Elements in this Container
	  * no matter whether it contains Associations, Pairs or IPairs
	  * which is useful to determine the right Factor of a Product
	  * or the Value Set of a Relation or Function */
	public Container getValues() {
		Container ret = (Container) newInstance();
		ret.union(new FilterInPair(this, false));
		return ret; }

	////////////////////////////////////////////////////////////////////////////////
	//	abstract Methods:
	////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////
	//  Interface Copy: Implementation
	///////////////////////////////////////////////////////////////////////////////

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public abstract ICopyAble newInstance();

	////////////////////////////////////////////////////////////////////////////
	//  Interface SemiGroup: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Addition in Place: +=
	  * This virtual Operation has to be implemented by each subclass.	 */
//	public abstract SemiGroup addItem(Object arg);// { return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  public Set Methods these could also be added to the Boole Interface!
	////////////////////////////////////////////////////////////////////////////////

	/** Increases the capacity of this Array, if necessary, to ensure
	  * that it can hold at least the number of components specified by
	  * the minimum capacity argument.
	  *
	  * @param   minCapacity   the desired minimum Capacity.
	  * @return  the actual Capacity allocated for this Container */
	public abstract int setCapacity(int minCapacity);

	/** Returns the current minimum capacity of this Array.
	  *
	  * @return  the current capacity of this Array.	 */
	public abstract int getCapacity();

	/** @return true when this Object is contained in this Container
	  * This is the same Operation as (findFirst() != EOI) || (available() >= 0)
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name contains() is only to be used for single Elements
	  */
	//public boolean contains(Object Item); //already defined in StreamIn or Pipe

	/** Adds the Items to this Container in Place.
	  * Already defined in StreamOut	 */
	//public Container addItems(IStreamIn iter);

	////////////////////////////////////////////////////////////////////////////////
	//	Column Operations
	//	These are Optimizations, also possible using Stream Filters,
	//  but these incur a Call Overhead AND Copying!
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final AContainer C) throws Exception {
		System.out.println("Testing " + AContainer.class.getName());
		C.zeroAt();
		Container A = (Container) C.newInstance();
		StringStreamIn Str1 = new StringStreamIn("ABCD");
		StringStreamIn Str2 = new StringStreamIn("UXYZ");
		A.union(Str1);
		C.union(Str2);
		Container[] Pot = A.Potency();
		int i = Pot.length;
		while (--i >= 0) {
			System.out.println(Pot[i]); }
		Container Pow = A.pow(C);
		System.out.println(Pow);
		Pow = Pow.flatten();
		System.out.println(Pow);
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(); }
	
}

