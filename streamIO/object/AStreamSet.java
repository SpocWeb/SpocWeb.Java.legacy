package streamIO.object;

import java.util.Comparator;

import streamIO.AReSetAble;
import streamIO.IIStreamIn;
import streamIO.IMarkAble;
import streamIO.IPushBackAble;
import streamIO.IReSetAble;
import streamIO.copy.boole.Boole;
import streamIO.copy.boole.Lattice;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.ABoolRing;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.exception.OperationNotSupported;
import streamIO.object.enumer.container.AContainer;
import tester.IEquivalence;
import tester.ITester;
import function.IProcessor;

/**
  * Abstract Base Class of both StreamSet and AContainer
  * Implements all common Operations of these two Classes.
  * Merges the StreamIn Interface with the Boolean and IntegrityRing Interfaces
  * to work on (possibly streaming) individual and integer Objects.
  *
  * Subclasses:
  * @see StreamSet
  * @see AContainer
  */
public abstract class AStreamSet
extends ABoolRing
implements IStreamSet { 
	
	/** @see streamIO.object.IStreamIn#currItem()	 */
	abstract public Object currItem(); 
	
	/** @see streamIO.object.IStreamIn#getComparator()	 */
	abstract public Comparator getComparator(); 
	
	/** @see streamIO.object.IStreamIn#getFilter()	 */
	abstract public Object getFilter(); 
	
	/** @see streamIO.object.IStreamIn#setFilter(java.lang.Object)	 */
	abstract public void setFilter(Object Value); 
	
	/** @see streamIO.IAvailAble#availAble()	 */
	abstract public long availAble(); 
	
	/** @see streamIO.IAvailAble#getPosition()	 */
	abstract public long getPosition(); 
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	abstract public long getMaxMarkSize(); 
	
	/** @see streamIO.IOrdered#getOrder()	 */
	abstract public byte getOrder(); 
	
	/** @see streamIO.IFactory#nextItem()	 */
	abstract public Object nextItem(); 
	
	/** @see streamIO.IReSetAble#reSet(java.lang.String)	 */
	abstract public IReSetAble reSet(String failureExceptionMessage); 
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.copy.group.IISemiGroup#addAt(java.lang.Object)	 */
	abstract public ISemiGroup addAt(Object arg); 
	
	/** @see streamIO.copy.groupM.IISemiGroupM#mulAt(java.lang.Object)	 */
	abstract public ISemiGroupM mulAt(Object arg); 
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.copy.boole.ILattice#ANDat(java.lang.Object)	 */
	abstract public Lattice ANDat(Object arg); 
	
	/** @see streamIO.copy.boole.IBoole#NOTat()	 */
	abstract public Boole NOTat(); 
	
	/** @see streamIO.copy.boole.ILattice#ORat(java.lang.Object)	 */
	abstract public Lattice ORat(Object arg); 
	
	/** @see streamIO.copy.boole.IBoole#FalseAt()	 */
	abstract public Boole FalseAt(); 
	
	/** @see streamIO.copy.boole.Boole#TrueAt()	 */
	abstract public Boole TrueAt(); 
	
	////////////////////////////////////////////////////////////////////////////
	// Interface StreamIn Operations:
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see streamIO.IIStreamIn#isValid()
	 */
	public boolean isValid() { return availAble() >= 0; }

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator     */
	public IIStreamIn Iterator() {
		try { return (IIStreamIn) clone();
		} catch (CloneNotSupportedException x) {
			throw new OperationNotSupported("Should not happen!", x); }
	}

	/**Skips over and discards n Items from this Iterator.
	 * Returns the actual number of bytes skipped.
	 * This dumb Implementation just reads all Elements and discards them.	 */
	public long jump(final long Position) { return 
		//AReSetAble.JUMP(this, Position);
		AStreamIn.JUMP((IIStreamIn) this, Position); }
	
	/** @see streamIO.object.IStreamIn#jump()     */
	public IReSetAble jump() { return AReSetAble.JUMP(this); }
    
	/** 
	 * Jumps a single Position back in this Iterator.
     * equivalent to jump(-1); 
	 * @see streamIO.IReSetAble#pushBack()  
	 * @return this Stream if jumping worked, null otherwise. 
	 */
    public IPushBackAble pushBack() { return AReSetAble.PUSH_BACK(this); }
	
	/** @return the Object at the given Position in this Enumeration
	  * The Result depends on whether the Iterator is deterministic
	  * and supports these Operations */
	public Object getAt(final int Position) throws NoSuchMethodException {
		reSet (Position); return nextItem(); }

	/** Returns and moves to the last (Root) Object of this one.
	  * This should be used with Care, because it could result in Blocking
	  * or infinite Loops with infinite Streams. */
	public Object lastItem() { return AStreamIn.LAST_ITEM(this); }

	//Convenience Array Read Methods

	/** Returns as many Items as are available() currently 	 */
	public Object[] nextItems() { return AStreamIn.NEXT_ITEMS(this); }

	/** Returns as many Items as possible, but maximum numItems stored in Items from Index Begin 	 */
	public int nextItems(final Object[] Items, final int numItems, final int Begin) {
		return AStreamIn.NEXT_ITEMS(this, Items, numItems, Begin); }

	/** Returns as many Items as possible, but maximum numItems stored in Items
	  * small Optimization in using the Default Parameters right away */
	public int nextItems(final Object[] Items, final int numItems) {
		return AStreamIn.NEXT_ITEMS(this, Items, numItems, -1); }

	/** Returns as many Items as possible, but maximum Items.length in Items 	 */
	public int nextItems(final Object[] Items) {
		return AStreamIn.NEXT_ITEMS(this, Items, Items.length, -1); }

	//Convenience Bulk Tests and Operations

	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOL	 */
	public Object firstThat (final ITester tst) throws NoSuchMethodException {
		reSet (0); return AStreamIn.NEXT_THAT(this, tst); }

	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Iterator.EOL	 */
	public Object nextThat (final ITester tst) {
		return AStreamIn.NEXT_THAT(this, tst); }

	/**Performs an Operation for each Object in this Iteration.
	 * Returns the Number of Operations performed.	 */
	public int forEach (final IProcessor op) {
		return AStreamIn.FOR_EACH(this, op); }

	/**Performs the Operation of the Operator on each Item in the Collection
	 * that equals this Item. The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public int forEachThatEquals(final Object Item, final IProcessor op) {
		return AStreamIn.FOR_EACH_THAT_EQUALS (this, Item, op); }

	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object firstOfEachThatEqualsThat(final Object Item, final ITester Test) throws NoSuchMethodException {
		reSet (0); return AStreamIn.NEXT_OF_EACH_THAT_EQUALS_THAT(this, Item, Test); }

	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object nextOfEachThatEqualsThat(final Object Item, final ITester Test) {
		return AStreamIn.NEXT_OF_EACH_THAT_EQUALS_THAT(this, Item, Test); }

	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findNext (final Object Item) {
		return AStreamIn.FIND_NEXT(this, Item, null); }

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (final Object Item, final IEquivalence EQ) {
		return AStreamIn.FIND_NEXT (this, Item, EQ); }

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (final Object Item, final int index, final IEquivalence EQ) {
		return AStreamIn.FIND_NEXT (this, Item, index, EQ); }

	/** Tests, whether this Object exists in the Set,
	  * @return the next Item found that equals Item, otherwise IStreamIn.EOI
	  * Can be used iteratively to find all Occurrences. 	 */
	public Object findNext (final Object Item, final int index) {
		return AStreamIn.FIND_NEXT (this, Item, index); }

	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findFirst (final Object Item) throws NoSuchMethodException {
		reSet (0); return AStreamIn.FIND_NEXT(this, Item, null); }

	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	
	 * Returns any found Instance, not necessarily the first or the last one!  */
	public Object find(final Object Item) {
		try {
			return findFirst(Item); 
		} catch(final NoSuchMethodException x) {
			return findNext(Item);
		}
	}

	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Iterator.EOL	 */
	public Object findFirst (final Object Item, final IEquivalence EQ) throws NoSuchMethodException {
		reSet (0); return AStreamIn.FIND_NEXT(this, Item, EQ); }

	/** @return true when this Object is contained in this Container
	  * This is the same Operation as (findFirst() != EOI) || (available() >= 0)
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name contains() is only to be used for single Elements
	  */
	public boolean contains(final Object item) { //throws NoSuchMethodException {
		try {
			return (findFirst(item) != EOI) || isValid();
		} catch (NoSuchMethodException x) { //choose the alternative Strategy and search only forwards!
			return (findNext (item) != EOI) || isValid();
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
	public boolean SubEq(IIStreamIn arg, boolean Sequence) {
		return AStreamIn.SUB_EQ(this, arg, Sequence); }

	/**Returns the next Item without moving to it.	 */
	//public Object peekItem() { //throws    NoSuchMethodException {
	//						 throw new NoSuchMethodException(); }

	//Marking and Resetting a Stream (for re-Processing, if supported)

	/**Resets the Iterator to the last marked Position,
	 * done automatically on Instantiation
	 * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() { //throws NoSuchMethodException{
		reSet (0); return this; }

	/**Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(long Position) { //throws    NoSuchMethodException {
	    //reset(); if (Position == 0) return 0; return skip(Position);  }
		throw new OperationNotSupported(AStreamIn.class); 
	}

	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.	 */
	public IMarkAble mark() { //throws NoSuchMethodException {
		return mark(Long.MAX_VALUE); }

	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(long ReadLimit) { //throws    NoSuchMethodException {
	    throw new OperationNotSupported(AStreamIn.class); }

	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
//	public boolean isMonotonous() { return false; }

}
