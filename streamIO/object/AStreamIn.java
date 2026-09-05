package streamIO.object;

import java.util.ArrayList;
import java.util.Comparator;

import streamIO.AMarkAble;
import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IReSetAble;
import streamIO.exception.OperationNotSupported;
import streamIO.object.enumer.IChangeAble;
import tester.IComparator;
import tester.IEquivalence;
import tester.IScalarMetric;
import tester.ITester;
import function.IIOrderAble;
import function.IOrderAble;
import function.IProcessor;
import graphs.ICopy;

/**
  * Abstract base for a {@link IStreamIn} implementation, hoisting every generic streaming
  * algorithm (find, forEach, ordering, containment) into reusable static helper methods so
  * a concrete subclass only has to supply {@link #nextItem()}, {@link #availAble()},
  * {@link #getMaxMarkSize()}, {@link #currItem()} and {@link #getPosition()}.
  * <p>
  * AStreamIn
  * Design Decisions:
  * Since most Methods involve a Loop, it pays off to move them to static Methods
  * and call these, because that allows to use IStreamIn without embedding it
  * or deriving it from this Class.
  *
  * If the Collection contains 'null', you have to test for both
  *  'null' AND (available() <= 0), which is nearly as fast due to shortCut Evaluation:
  *
  * while ((null != (currItem = Iter.nextItem())) || Iter.isValid()) {
  * while ((EOI  != (currItem = Iter.nextItem())) || Iter.isValid()) {
  *
  * Created on 26. Mai 2001, 22:34
  *
  * @author  Matthias Heuer
  * @version
  * @stereotype enumeration
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:32:18Z
  * digest: c9aca17b6134ec52cbd175fb397263843e0dd552aa7044ae4d3e9498543253f6
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class AStreamIn
extends AMarkAble
implements IStreamIn, Cloneable {
	
	////////////////////////////////////////////////////////////////////////////
	// Interface StreamIn: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Only the concrete Implementation can retrieve Items 
	 *  
	 * @return  the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  */
	abstract public Object nextItem (); // { }
	
	/** 
	 * Only the concrete Implementation can know how many Items are available. 
	 * @return the minimum Number of Items left (in the Buffer).
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
    abstract public long availAble (); // { }
	
    /** 
	 * Only the concrete Implementation can know how big the maximum Mark Size is 
	 * or whether Marking is supported at all. 
     * @see streamIO.IMarkAble#getMaxMarkSize()     */
	abstract public long getMaxMarkSize(); // { return availAble(); }
	
	
	/**
	 * Returns the item cached by the concrete implementation's own {@link #nextItem()} call.
	 *
	 * @return the current Object.
	  * Could be implemented by redirecting the nextItem Calls to assign to the currItem Object,
	  * but most Iterators have fast Access to the current Item anyway...
	  * More interesting is the Addition of a lastItem, or even better, 
	  * a PeekItem which can be used to feed a (deterministic) LL(1) Parser. 
	  */
	abstract public Object currItem(); // { return currItem; }
	
    /** 
	  * Could be implemented by counting the nextItem Calls, 
	  * Only the concrete Implementation can know how many Items are available. 
      * @see streamIO.IAvailAble#getPosition()     */
    abstract public long getPosition(); 
    
	////////////////////////////////////////////////////////////////////////////
	//	Members
	////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * The String of Separator Characters (re-used in ResultSetSep)
	 * starting with the lesser significant Separator Characters:
	 * Escape Character, (Col Delimiter) and  most significant (Row Delimiter).
	 * Cached for writing out the cleaned up Data
	 * instead, use the public Separator String of the Parent Class AStreamIn
	 * @see #toString() also uses this Separator String 
	 */
	public String Separator = ",";
	
	/** Filter Object: only Items that are equal to this Object are returned!
	  * This supports findFirstThatEquals(), findFirstThatEqualsThat() */
	protected Object filter;
	
	/** The current Position in the streamIO.
	  * Reset to 0 on Construction and on reset() Commands
	  * Introduced to support reset() Methods also for Iterators that are not resettable
	  * and as a ShortCut for Iterators that have started,
	  * but not reached the given Position!
	  * Makes only sense when concurrently implementing the nextItem() Method
	  * to increase this Position!
	  * This can be enforced by introducing a new protected Delegation Method
	  * and making the nextItem() Method final. */
	//protected long Position = 0;
	
	/** Current Item, cached for iterated Retrieval, cached here also for parsing
	  * Could be removed here, because most Iterators have fast Access to the current Item	 */
//	protected Object currItem; // = IStreamIn.SOI;
//	unnecessary, it is as fast for Iterators to directly access the internal Structure of a Container
//	only for Streams this could be necessary.
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the filter object that restricts which items {@link #nextItem()} yields.
	 *
	 * @return the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()! */
	public Object getFilter() {
		return filter; }
	
	/** Sets the Filter Object
	  * only Items that are equal to this Object are returned by nextItem()!
	  * This allows for Optimizations on hashed and sorted Containers
	  * because the Result Set can be decreased dramatically. */
	public void setFilter(final Object _value) {
		filter = _value; }
	
	/**
	 * Reports no known ordering; a subclass overrides this when it can guarantee one.
	 *
	 * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return ORDER_NONE; } //Source.getOrder(); }
	
	////////////////////////////////////////////////////////////////////////////
	// Interface StreamIn Operations:
	////////////////////////////////////////////////////////////////////////////
	
	/**usually tested when 'null' Elements are encountered. 
	 * @see streamIO.IIStreamIn#isValid()
	 * @return true when the current Item is valid and at the Stream's End
	 */
	public boolean isValid() { return availAble() >= 0; }
	
	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	public IIStreamIn Iterator() {
		try { 
			final IIStreamIn ret = (IIStreamIn) clone();
			//ret.reset(); 
			return ret; 
		} catch (final CloneNotSupportedException x) { 
			throw new OperationNotSupported("Should not happen!", x); 
		}
	}
	
	/** Skips over and discards n Items from this Iterator.
	  * @return the actual number of bytes skipped.
	  * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(final long Position) { return JUMP((IIStreamIn) this, Position); }
	
	/** Returns and moves to the last (Root) Object of this one.
	  * This should be used with Care, because it could result in Blocking
	  * or infinite Loops with infinite Streams. */
	public Object lastItem() { return LAST_ITEM(this); }
	
	/**
	 * Repositions this stream to {@code Position} and returns the item found there.
	 *
	 * @return the Object at the given Position in this Enumeration
	  * The Result depends on whether the Iterator is deterministic
	  * and supports these Operations */
	public Object getAt(final int Position) { //throws NoSuchMethodException {
		reSet (Position);
		return nextItem(); }
	
	//Convenience Array Read Methods
	
	/** Returns as many Items as are available() currently 	 */
	public Object[] nextItems() { return NEXT_ITEMS(this); }
	
	/** Returns as many Items as possible, but maximum numItems stored in Items from Index Begin 	 */
	public int nextItems(Object[] Items, int numItems, int Begin) { 
		return NEXT_ITEMS(this, Items, numItems, Begin); }
	
	/** Returns as many Items as possible, but maximum numItems stored in Items
	  * small Optimization in using the Default Parameters right away */
	public int nextItems(Object[] Items, int numItems) { return NEXT_ITEMS(this, Items, numItems, -1); }
	
	/** Returns as many Items as possible, but maximum Items.length in Items 	 */
	public int nextItems(Object[] Items) { return NEXT_ITEMS(this, Items, Items.length, -1); }
	
	//Convenience Bulk Tests and Operations
	
	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOL	 */
	public Object firstThat (ITester tst) {
		reSet (0); return NEXT_THAT(this, tst); }
	
	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOL	 */
	public Object nextThat (ITester tst) { return NEXT_THAT(this, tst); }
	
	/**Performs an Operation for each Object in this Iteration.
	 * Returns the Number of Operations performed.	 */
	public int forEach (IProcessor op) { return FOR_EACH(this, op); }
	
	/**Performs the Operation of the Operator on each Item in the Collection
	 * that equals this Item. The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public int forEachThatEquals(Object Item, IProcessor op) {
		return FOR_EACH_THAT_EQUALS (this, Item, op); }
	
	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object firstOfEachThatEqualsThat(Object Item, ITester Test) throws NoSuchMethodException {
		reSet (0); return NEXT_OF_EACH_THAT_EQUALS_THAT(this, Item, Test); }
	
	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object nextOfEachThatEqualsThat(final Object Item, final ITester Test) {
		return NEXT_OF_EACH_THAT_EQUALS_THAT(this, Item, Test); }
	
	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (final Object Item, final IEquivalence EQ) {
		return FIND_NEXT (this, Item, EQ); }
	
	/** Tests, whether this Object exists in the Set,
	  * @return the first Item found that equals Item, otherwise IStreamIn.EOI
	  * @param EQ Equivalence Relation used to test Equality, instead of equals()
	  * Cannot be used iteratively, because it resets the streamIO. 	 */
	public Object findFirst (final Object Item, final IEquivalence EQ) throws NoSuchMethodException {
		reSet (0); return FIND_NEXT(this, Item, EQ); }
	
	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findNext (final Object Item) { return FIND_NEXT(this, Item, null); }
	
	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findFirst (final Object Item) throws NoSuchMethodException {
		reSet (0); return FIND_NEXT(this, Item, null); }
	
	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object find(final Object Item) { //throws {
		try {
			return findFirst(Item);
		} catch (NoSuchMethodException x) {
			return findNext(Item);
		}
	}
	
	/**
	 * Tests whether {@code Item} occurs in this stream, searching forward when a restart via
	 * {@link #findFirst(Object)} is not supported.
	 *
	 * @return true when this Object is contained in this Container
	  * This is the same Operation as (findFirst() != EOI) || (available() >= 0)
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name contains() is only to be used for single Elements
	  */
	public boolean contains(Object Item) { //throws NoSuchMethodException {
		try {
			return (findFirst(Item) != EOI) || this.isValid();
		} catch (NoSuchMethodException x) { //choose the alternative Strategy and search only forwards!
			return (findNext (Item) != EOI) || this.isValid();
//			throw new OperationNotSupported(x.toString(), x);
		} }
	
	/**Tests, whether all Objects of streamIO arg exists in this streamIO,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL
	 * Requires this streamIO to be restartAble.
	 * There are more restrictive and thus 'cheaper' Searches:
	 * -finding all Elements of arg in Sequence
	 * -finding all Elements of arg in Sequence with intermittent Objects
	 * Uses the Monotony Criterion (for infinite Streams)
	 * This corresponds to the contains() Method.	 */
	public boolean SubEq(final IIStreamIn arg, final boolean Sequence) {
		return SUB_EQ(this, arg, Sequence); }
	
	/**
	 * Looks ahead one item by advancing then pushing back, without consuming it.
	 *
	 * @return the next Item without moving to it.	 */
	public Object peekItem() { //throws    NoSuchMethodException {
		//throw new NoSuchMethodException("No generic Implementation!");
		final Object ret = nextItem(); 
		pushBack(); 
		return ret; 
	}
	
	//Marking and Resetting a Stream (for re-Processing, if supported)
	
	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() {
		resetVersion(null); 
		return super.reSet(); }
	
	/** intended to reset the Version on versioned iterators 	 */
	protected void resetVersion(final IChangeAble _container) { } 
	
	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(final long position) {
		resetVersion(null); 
		return super.reSet(position); 
	}
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.	 */
	//public IMarkAble mark() { return mark(Long.MAX_VALUE); }
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	//public IMarkAble mark(final long readLimit) { 
	//    throw new OperationNotSupported("No Default Implementation"); }
	
	/** Streams the whole Iterator to the String using the toString() Methods
	  * of the Elements of this streamIO.
	  * Does not try to reset this streamIO!
	  * This is now obsolete by using
	  * @see PrintStreamOut.add(Iter), but shorter!
	  * Should not be used on blocking or infinite Streams. 	 */
	public String toString() {
		return super.toString(); //TODO: anything else results in Errors during Debugging! 
		/*
	    //Streaming out the Content disturbs the regular Stream Operation!
	    final long maxReadLimit = getMaxMarkSize(); 
	    if (maxReadLimit <= 0)
	        return ""; 
	    mark(maxReadLimit); //even this could disturb previous mark()s
		final StringBufferStreamOut ret = new StringBufferStreamOut();
		AStreamOut.STREAM(this, ret, Integer.MAX_VALUE, false, false, Separator, maxReadLimit);
		reSet(); 
		return ret.toString(); 
		*/
	}

	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
//	public byte getOrder() { return OrderUnDef; }

	/**
	 * Reports no explicit comparator; a {@code null} result means elements are assumed to
	 * implement one of the ordering interfaces listed below.
	 *
	 * @return The Comparator being used to compare Elements.
	  * If 'null', the Elements are assumed to implement
	  * @see IScalarMetric or
	  * @see java.lang.Comparable  or
	  * @see IIOrderAble	 */
	public Comparator getComparator () { return null; }

	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
//	public boolean isMonotonous() { return getOrder() == OrderSortAsc; }

	////////////////////////////////////////////////////////////////////////////
	// static StreamIn Operations: directly callable here, since not possible in the Interface!
	////////////////////////////////////////////////////////////////////////////

	//A StreamIn is possibly infinitely long (like a Stream),
	//thus lastXxx Operations are not well defined!

	/**
	 * Scans {@code str} to its end and returns its smallest element by {@link IIOrderAble}
	 * comparison.
	 *
	 * @return the Minimum of all Elements in this streamIO Min(i, x[i])
	  * This Implementation reuses the Elements of the streamIO */
	final static public IIOrderAble MIN(final IIStreamIn str) {
		if (str instanceof IStreamIn) 
		    ((IStreamIn) str).reSet(); 
		IIOrderAble Min = (IIOrderAble) ((ICopy) str.nextItem()).Copy(); //
		IIOrderAble xi;
		while (EOI != (xi = (IIOrderAble) str.nextItem()) || str.isValid()) {
			if (  xi.isLessThan(Min)) Min = xi; }
		return Min;	}

	/**
	 * Scans {@code str} to its end and returns its largest element by {@link IIOrderAble}
	 * comparison.
	 *
	 * @return the Minimum of all Elements in this streamIO Min(i, x[i])
	  * This Implementation reuses the Elements of the streamIO */
	final static public IIOrderAble MAX(final IIStreamIn str) {
		if (str instanceof IStreamIn) 
		    ((IStreamIn) str).reSet(); 
		IIOrderAble Max = (IIOrderAble) str.nextItem(); //
		IIOrderAble xi;
		while (EOI != (xi = (IIOrderAble) str.nextItem()) || str.isValid()) {
			if (! xi.isLessThan(Max)) Max = xi; }
		return Max;	}

	/** Tests, if the Iteration, generated from the Container, is sorted.
	  * Returns a float Number between [-1,+1] that indicates the Degree of
	  * ascending/descending Sort Order and 0 for no Sorting.
	  * It detects strict Monotony by returning exact (+/-1)
	  * but it can not distinguish between no sorting and total Equality. */
	final static public float ORDERED_DEGREE(final IIStreamIn iter) {
		int   asc = 0;
		int  desc = 0;
		int count = 1;
		IIOrderAble Item,      LastItem = (IIOrderAble) iter.nextItem();
		while ((EOI != (Item = (IIOrderAble) iter.nextItem())) || iter.isValid()) {
			     if ( asc > 0) { if (LastItem.isLessThan(    Item)) ++ asc; else if (    Item.isLessThan(LastItem)) return 0; } //this Layout saves the second Call to less()
			else if (desc > 0) { if ( Item	 .isLessThan(LastItem)) ++desc; else if (LastItem.isLessThan(    Item)) return 0; } //as soon as a Direction is defined!
			else {//no Direction yet...
					 if (LastItem.isLessThan(    Item)) ++ asc;  //
				else if (Item	 .isLessThan(LastItem)) ++desc; } //
			LastItem = Item;
			count++; }
		return ((float)(asc - desc))/count; }

	/** Tests, if the Iteration, generated from the Container, is sorted.
	  * Returns a float Number between [-1,+1] that indicates the Degree of
	  * ascending/descending Sort Order and 0 for no Sorting.
	  * It detects strict Monotony by returning exact (+/-1)
	  * but it can not distinguish between no sorting and total Equality. */
	final static public float ORDERED_DEGREE(final IIStreamIn iter, final IComparator ord) {
		int   tmp;
		int   ret = 0;
		int count = 1;
		IIOrderAble Item,      LastItem = (IIOrderAble) iter.nextItem();
		while ((EOI != (Item = (IIOrderAble) iter.nextItem())) || iter.isValid()) {
			tmp = ord.compare(LastItem, Item);
			if ((tmp != 0) && (ret != 0)) { //if Directions are defined...
			if ((tmp >  0) != (ret >  0)) { //if Inconsistencies are found...
					return 0; } }
//				 if (tmp > 0) {	if (ret < 0) return 0; } //this is
//			else if (tmp < 0) {	if (ret > 0) return 0; } //equivalent!
			LastItem = Item;
			ret += tmp;
			count++; }
		return ((float)ret)/count; }

	/** Returns and moves to the last (Root) Object of this one.
	  * This should be used with Care, because it could result in Blocking
	  * or infinite Loops with infinite Streams.
	  * This is the fastest way, but it does not reduce the needed time
	  * for the next Search like other (modifying) Implementations do. 	 */
	final static public Object LAST_ITEM(final IIStreamIn iter) {
		Object last = EOI;
		for(Object curr; EOI != (curr = iter.nextItem()) || iter.isValid();) {
			last =  curr; }
		return last; 
	}

	/** Tests, whether this Object exists in the streamIO.
	  * Uses the Monotony Criterion if possible (for infinite Streams).
	  * @param item Object to be searched in the streamIO
	  * @param iter Iterator to be searched for the Item
	  * Iter is not restarted, so the previous state is preserved
	  * and this Function can be used for both findFirst() an findNext()!
	  * @return an Object that is equal to this one, when found, otherwise EOI
	  * therefore if searching for "null", you have to test available() too
	  */
	final static public Object FIND (final IStreamIn iter, final Object item) {
		iter.reSet(); return FIND_NEXT(iter, item); 
	}

	/** Tests, whether this Object exists in the streamIO.
	  * Uses the Monotony Criterion if possible (for infinite Streams).
	  * @param item Object to be searched in the streamIO
	  * @param iter Iterator to be searched for the Item
	  * Iter is not restarted, so the previous state is preserved
	  * and this Function can be used for both findFirst() an findNext()!
	  * @return an Object that is equal to this one, when found, otherwise EOI
	  * therefore if searching for "null", you have to test available() too
	  */
	final static public Object FIND_NEXT (final IIStreamIn iter, final Object item) {
		return FIND_NEXT(iter, item, null); }

	/** Tests, whether this Object exists in the streamIO.
	  * Uses the Monotony Criterion if possible (for infinite Streams).
	  * @param item Object to be searched in the streamIO
	  * @param iter Iterator to be searched for the Item
	  * Iter is not restarted, so the previous state is preserved
	  * and this Function can be used for both findFirst() an findNext()!
	  * @return an Object that is equal to this one, when found, otherwise EOI
	  * therefore if searching for "null", you have to test available() too
	  */
	final static public Object FIND_NEXT (final IIStreamIn iter, final Object item, final IEquivalence eq) {
		int monotony = IPipe.ORDER_NONE; //redundant Flag to speed up Evaluation
		IOrderAble orderAble = null; //for the Monotony Criterion!
		Object curr;
		if (iter instanceof IStreamIn) {
			if (1 == Math.abs(monotony = ((IStreamIn) iter).getOrder())) {
				orderAble = (IOrderAble) item; } }
		while ((EOI != (curr = iter.nextItem())) || iter.isValid()) {
			if ((item   ==   curr) ||
				((eq != null) ? eq.equals(curr) : item.equals(curr))) return curr;
			if  ((orderAble != null) && 
				((orderAble.Position(curr)) != monotony)) {
				return EOI; }
		} return curr; } //EOI; } // null; }

	/** Tests, whether this Object exists in the Set,
	  * @param index The Index of the Object interpreted as an Array or StreamIn
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  */
	public Object findNext (Object Item, int index) {
		return FIND_NEXT (this, Item, index, null); }

	/** Tests, whether this Object exists in the Set,
	  * @param index The Index of the Object interpreted as an Array or StreamIn
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  */
	public Object findNext (Object Item, int index, IEquivalence EQ) {
		return FIND_NEXT (this, Item, index, EQ); }

	/** Tests, whether this Object[index] exists in the Set,
	  * @param index The Index of the Object interpreted as an Array or StreamIn
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  * Implementation should allow for both Object[index] and StreamIn.getAt(index)
	  *
	  * Advanced Implementations for Databases can make use of Index Tables
	  * to find the next Match, instead of scanning the whole Table.
	  */
	final static public Object FIND_NEXT(IIStreamIn Iter, Object Item, int index) {
		return FIND_NEXT (Iter, Item, index, null); }

	/** Tests, whether this Object[index] exists in the Set,
	  * @param index The Index of the Object interpreted as an Array or StreamIn
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences.
	  * Implementation should allow for both Object[index] and StreamIn.getAt(index)
	  *
	  * Advanced Implementations for Databases can make use of Index Tables
	  * to find the next Match, instead of scanning the whole Table.
	  */
	final static public Object FIND_NEXT(IIStreamIn Iter, Object Item, int index, IEquivalence EQ) {
		int Mon = IPipe.ORDER_NONE; //redundant Flag to speed up Evaluation
		IOrderAble Item_ = null; //for the Monotony Criterion!
		Object tst, curr;
		if (Iter instanceof IStreamIn) {
			if (1 == Math.abs(Mon = ((IStreamIn) Iter).getOrder())) {
				Item_ = (IOrderAble) Item; } }
		while ((EOI != (curr = Iter.nextItem())) || Iter.isValid()) {
			try {
				tst = (curr instanceof Object[]) ?
					((Object[]) curr)      [index]:
					((IStreamIn) curr).getAt(index);
			} catch      (NoSuchMethodException x) {
				throw new NoSuchMethodError(    x.toString());
			}
			if ((Item   ==   tst) || //shortCut Evaluation!
				((EQ != null) ? EQ.equals(tst) : Item.equals(tst))) {
				return curr; }
			if (Mon == (Item_.Position(curr))) {
				return EOI; }
		} return curr; } //EOI; } // null; }


	/**Performs the Operation of the Operator on each Item in the Collection
	 * that equals this Item. The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	final static public int FOR_EACH_THAT_EQUALS(IIStreamIn Iter, Object Item, IProcessor op) {
		int ret = 0;
		for(Object tst; (EOI != (tst = Iter.nextItem())) || Iter.isValid();) {
			if ((Item   ==   tst) ||
				(Item.equals(tst))) { ++ret; op.MapAt(tst); }
		} return ret; }

	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
//	final static public Object firstOfEachThatEqualsThat(StreamIn Iter, Object Item, ITester Test) throws NoSuchMethodException {
//		Iter.reset(); return nextOfEachThatEqualsThat(Iter, tst); }

	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	final static public Object NEXT_OF_EACH_THAT_EQUALS_THAT(IIStreamIn Iter, Object Item, ITester Test) {
		for(Object tst; (EOI != (tst = Iter.nextItem())) || Iter.isValid();) {
			if ((Item   ==   tst) ||
				 Item.equals(tst))
				if(Test.test(tst))
					return   tst;
		} return IStreamIn.EOI; } // null; }

	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.StreamIn.EOL	 */
//	final static public Object firstThat (StreamIn Iter, ITester tst) throws NoSuchMethodException {
//		Iter.reset(); return nextThat(Iter, tst); }

	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.StreamIn.EOL	 */
	final static public Object NEXT_THAT(IIStreamIn Iter, ITester tst) {
		for(Object curr; (EOI != (curr = Iter.nextItem())) || Iter.isValid();) {
			if (tst.test(curr)) {
				return curr; }
		} return EOI; } //null; }

	/**Performs an Operation for each Object in this Iteration.
	 * Returns the Number of Operations performed.	 */
	final static public int FOR_EACH(IIStreamIn Iter, IProcessor op) {
		int ret = 0;
		for (Object curr; (EOI != (curr = Iter.nextItem())) || Iter.isValid();) {
			++ret; op.MapAt(curr); }
		return ret; }

	/** Returns as many Items as are available() currently 	 */
	final static public ArrayList NEXT_ITEMS_LIST(IIStreamIn iter) {
		ArrayList ret = new ArrayList();
		for(Object curr; ((curr = iter.nextItem()) != EOI) || iter.isValid();) {
			ret.add(curr); }
		return ret; }

	/** Returns as many Items as are available() currently 	 */
	final static public Object[] NEXT_ITEMS(IIStreamIn iter) {
		if (!(iter instanceof IStreamIn)) {
			return NEXT_ITEMS_LIST(iter).toArray(); }
		int i = -1, len = (int) ((IAvailAble) iter).availAble();
		Object[] ret = new Object[len];
		while (++i < len) { //preserve the Order
			ret[i] =  iter.nextItem(); }
		return ret; }

	/** Returns as many Items as possible, but maximum numItems stored in Items from Index Begin+1	 */
	final static public int NEXT_ITEMS(IIStreamIn Iter, Object[] Items, int numItems, int Begin) {
		int len = Begin + numItems;
		while (++Begin < len) //preserve the Order
			if (EOI == (Items[Begin] = Iter.nextItem()) && !Iter.isValid()) {
				break; }
		return numItems + len - Begin; }

	/** Returns as many Items as possible, but maximum numItems stored in Items 	 */
	final static public int NEXT_ITEMS(IIStreamIn Iter, Object[] Items, int numItems) {
		return NEXT_ITEMS(Iter, Items, numItems, -1); }

	/** Returns as many Items as possible, but maximum Items.length in Items 	 */
	final static public int NEXT_ITEMS(IIStreamIn Iter, Object[] Items) {
		return NEXT_ITEMS(Iter, Items, Items.length, -1); }

	/**Skips over and discards n Items from this Iterator.
	 * Returns the actual number of bytes skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 
	 * @see streamIO.AReSetAble#JUMP(IReSetAble, long) does the same, 
	 * but cannot test using the isValid() Method.  
	 */
	final static public long JUMP(final IIStreamIn iter, final long position) {
		//iter.reSet(); //no reSet Method available! 
		long i = -1; //use shortCut Evaluation and order the Expressions
		while ((++i < position) &&
			  ((EOI != iter.nextItem()) || iter.isValid()));
		return i; }
	
	/**
	 * Tests whether {@code arg} occurs anywhere in {@code Enum}.
	 *
	 * @return true, when this Container contains arg, false otherwise
	  * Returns it, when found, otherwise returns streamIO.Iterator.EOL
	  * This corresponds to the contains() and SubEq() Method.	 */
	final static public boolean CONTAINS(IIStreamIn Enum, Object arg) {
		return (FIND_NEXT(Enum, arg, null) != EOI) || Enum.isValid(); }

	/**
	 * @return true, when all Objects of IStreamIn Enum exist in IStreamIn arg,
	 * i.e. Enum <= arg
	 * Requires this Enum to be restartAble.
	 * More restrictive and thus 'cheaper' Searches exist:
	 * -finding all Elements of arg in Sequence with intermittent Objects
	 * -finding all Elements of arg in Sequence w/o intermittent Objects (not supported)
	 * Uses the Monotony Criterion (for infinite Streams) of findFirst()
	 * @param Sequence if true, does not reset() the streamIO
	 * and thus requires the Items to appear in the Order of arg.
	 */
	/**
	 * Tests whether every element of {@code Enum} also occurs in {@code arg}, i.e.
	 * {@code Enum <= arg}.
	 *
	 * @return true, when all Objects of IStreamIn Enum exist in IStreamIn arg,
	 * i.e. Enum <= arg
	 * Requires this Enum to be restartAble.
	 * More restrictive and thus 'cheaper' Searches exist:
	 * -finding all Elements of arg in Sequence with intermittent Objects
	 * -finding all Elements of arg in Sequence w/o intermittent Objects (not supported)
	 * Uses the Monotony Criterion (for infinite Streams) of findFirst()
	 * @param Sequence if true, does not reset() the streamIO
	 * and thus requires the Items to appear in the Order of arg.
	 */
	final static public boolean SUB_EQ(IIStreamIn arg, IIStreamIn Enum, boolean Sequence) { //throws NoSuchMethodException {
		Object curr;
		IStreamIn Enum_ = null;
		if (!Sequence) {
			Enum_ = (IStreamIn) Enum; }
		while ((EOI != (curr = arg.nextItem())) || arg.isValid()) {
			if ((Enum_ != null) && !Sequence) { //only reset when wanted!
				Enum_.reSet(); }
			if((EOI == AStreamIn.FIND_NEXT(Enum, curr, null)) && !Enum.isValid()) {
				return false; }
		}
		return true; }

	/** Tests, whether all Objects of streamIO arg exists in this streamIO AND vice versa,
	  * @return true, when all Elements appear in exactly the same Order
	  * -finding all Elements of arg in Sequence w/o intermittent Objects (not supported)
	  * Uses the Monotony Criterion (for infinite Streams) of findFirst()
	  * This could be called endsWith(), if Enum1 was checked for available() = 0 in the end.
	  */
	final static public boolean IDENTICAL(IIStreamIn Enum1, IIStreamIn Enum2) {
		Object curr1, curr2;
		while  (   EOI  !=   (curr2 = Enum2.nextItem()) || Enum2.isValid()) {
			if ((curr2  !=   (curr1 = Enum1.nextItem())) &&
				!curr2.equals(curr1))
				return false; }
		return !Enum1.isValid(); } //true when Enum1 contains no more Elements

}
