/*
 * Created on 31.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object;

import streamIO.AMarkAble;
import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.exception.BaseException;
import tester.ITester;
import function.IProcessor;

/**
  * Abstract base class for a {@link IStreamIn} filter that wraps a delegate stream and forwards
  * every optional capability ({@link IMarkAble}, {@link IReSetAble}, {@link IAvailAble}) to it.
  * <p>
  * Title: FilterByFunction.java<p>
  * Description:
  * Abstract Base Class for StreamIn Filters.
  *
  * Design Decisions / Implementation Details:
  *
  * Known SubClasses: 
  * @see streamIO.object.FilterIn implements the nextItemInternal 
  * by simply delegating to the Input Stream, so it realizes the identical Filter. 
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-03, 06;44;48<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:29:28Z
  * digest: 6dded80cbc0c761f478d3a018ae45baf2f0c0292abcd132c27bae4671008e840
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
 */
public abstract class AFilterIn 
extends AStreamIn 
implements IStreamIn {

	////////////////////////////////////////////////////////////////////////////
	//	static Methods
	////////////////////////////////////////////////////////////////////////////
	
	private static final Class[] CONSTRUCTOR_FILTER_IN = { IIStreamIn.class }; 
	
	/**
	 * returns a new FilterIn Instance of the given Class, if nothing fails
	 * useful to set up Tests with differing Filter Components
	 * or to parameterize Filter Structures using textual Descriptions. 
	 * @param _class the Class to instantiate 
	 * @param arg the stream to append
	 * @return null otherwise
	 */
	final static public AFilterIn CREATE_FILTER( final Class _class, final IIStreamIn arg)
	//throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException 
	{ return (AFilterIn) CREATE_OBJECT(_class, arg); }
	
	/**
	 * Returns a new FilterIn Instance of the given Class, if nothing fails. 
	 * Useful to set up Tests with differing Filter Components
	 * or to parameterize Filter Structures using textual Descriptions. 
	 * @param _class the Class to instantiate 
	 * @param arg the stream to append
	 * @return null otherwise
	 */
	final static public Object CREATE_OBJECT( final Class _class, final IIStreamIn arg)
	//throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException 
	{
		try {
			return _class.getConstructor(CONSTRUCTOR_FILTER_IN).newInstance(new Object[] {arg});
		} catch (final Exception x) {
			return null; 
		}
	}
	
	/**Checks both Elements for Equivalence by comparing their Roots. 	 */
	/*final static public boolean equals(StreamIn x, StreamIn y) {
		return  ((x == y) ||
				(x.lastItem()) ==	//x.lastItemFast();
				(y.lastItem()));	//y.lastItemFast();
	}
	*/

	////////////////////////////////////////////////////////////////////////////
	//	Members
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Delegatee / next Filter 	*/
	protected IIStreamIn in;
	
	/** Current Item, cached for iterated Retrieval, cached here also for parsing
	  * Could be removed here, because most Iterators have fast Access to the current Item	 */
	protected Object currItem;
	
    /**
     * Delegates to the wrapped stream's own maximum mark size.
     *
     * @return the largest {@code readLimit} the wrapped stream accepts for {@link #mark(long)}
     * @see streamIO.object.AStreamIn#getMaxMarkSize()
     */
    public long getMaxMarkSize() { return AMarkAble.GET_MAX_MARK(in); }

    /**
     * Delegates to the wrapped stream's own current position.
     *
     * @return the wrapped stream's position
     * @see streamIO.object.AStreamIn#getPosition()
     */
    public long getPosition() { return ((IStreamIn) in).getPosition(); }
	
	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/** Creates new FilterStreamIn delegating to the given Stream */
	public AFilterIn (final IIStreamIn enum_) { this.in = enum_; }
	
	/** Creates new FilterStreamIn for late Initialization */
	//private AFilterIn () { }
	
	////////////////////////////////////////////////////////////////////////////
	//	abstract Methods (for abstract Classes should be noted here!)
	////////////////////////////////////////////////////////////////////////////
	
	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator
	  */
	public IIStreamIn Iterator() {
		final AFilterIn ret; //first create the (outer) Filter...
		try { ret = (AFilterIn) clone(); }
		catch (final CloneNotSupportedException x) { 
			throw new BaseException("Should never happen!", x); 
		}
		ret.in = ((IStreamIn) in).Iterator(); //...then the inner Stream
		return ret; }
	
	/**Returns true, when the Object has more Constituents.
	 * If you mark the End by returning 'null',
	 * you can never have 'null' in the Collection!
	 * But in a Bag it has to be possible!?!
	 * Of course there is not much you can do with a 'null' Object,
	 * because any Operation raises a 'nullPointerObject' exception
	 * and it is of Type Object and has only Identity, nothing else.
	 * The only application is as a PlaceHolder to indicate NULL in Data.
	 *
	 * Nearly equivalent is currItem != null
	 * (when the Container does not contain null Entries, like e.g. HashTables)
	 *
	 * TODO:It would be best, if currItem, nextItem and previousItem
	 * had another Boolean Parameter denoting whether there are more Items.
	 * That would be possible, since all these Routines
	 * check exactly this condition. It is only hard to evaluate it,
	 * because it is not the Function Result.	 */
	public long availAble() { return ((IAvailAble)in).availAble(); }
	
	/** Switches on signaling inValid when null occurs 
	 * This is necessary for Streams that return intermediate nulls 
	 * to indicate the End of a SubStructure. 
	 */
	public boolean inValidOnNull;
	
	/** Switches on signaling inValid when null occurs */
	public boolean isValid() { 
		if ((currItem == null) && inValidOnNull) {
			return false; }
		return (in != null) && in.isValid(); }
	
	/**
	 * Returns the item cached by the most recent {@link #nextItem()} call.
	 *
	 * @return the current Object of this streamIO.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	public Object currItem() { return currItem; }

	/**
	 * Advances to and returns the next item, delegating to {@link #nextItemInternal()}.
	 *
	 * @return the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	final public Object nextItem() { return currItem = nextItemInternal(); }
	
	/** this is the abstract Template Method 	*/
	protected abstract Object nextItemInternal(); 
	
	/**
	 * Delegates to the wrapped stream's order when it implements {@link IStreamIn}, otherwise
	 * reports no known order.
	 *
	 * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() {
		if (in instanceof IStreamIn) { //delegate
			return ((IStreamIn) in).getOrder(); }
			return ORDER_NONE; } //otherwise you don't know!
	
	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
	//public boolean isMonotonous() { return ((StreamIn) Enum).isMonotonous(); }
	
	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public long reSet(long Position) { //throws NoSuchMethodException {
		return ((IStreamIn) in).reSet(Position); }
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(final long readLimit) { //throws NoSuchMethodException {
		((IStreamIn) in).mark(readLimit); return this; }
	
	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**Returns and moves to the last (Root) Object of this one.
	  * StreamIns are possibly infinitely long and thus lastXXX Operations are not well defined. */
	//public Object lastItem() { return lastItem(this); }
	
	/**Checks both Elements for Equivalence by comparing their Roots. 	 */
	/*public boolean equals(StreamIn y) {
		return  ((this == y) ||
				(  lastItem()) ==	//x = lastItemFast(x);
				(y.lastItem()));	//y = lastItemFast(y);
	}
	
	/**Checks both Elements for Equivalence by comparing their Roots. 	 */
	//public boolean equals(Object arg) { return equals((StreamIn) arg); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Optimizations (necessary, so that nextItem() is not always delegated!!!)
	////////////////////////////////////////////////////////////////////////////
	
	/**Resets the Iterator to the last marked Position.	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		((IStreamIn) in).reSet();
		currItem = SOI; return this; }
	
	/**Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.	 */
	public IMarkAble mark() { //throws NoSuchMethodException {
		((IStreamIn) in).mark(); return this; }
	
	/**Tests each Object with the given Test.
	 * Stops on the first Object that returns true.
	 * Returns it, otherwise returns streamIO.Enumerator.EOL	 */
	public Object nextThat (ITester tst) { return NEXT_THAT(in, tst); }
	
	/**Performs an Operation for each Object in this Iteration.
	 * Returns the Number of Operations performed.	 */
	public int forEach (IProcessor op) { return FOR_EACH(in, op); }
	
	/**Performs the Operation of the Operator on each Item in the Collection
	 * that equals this Item. The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public int forEachThatEquals(Object Item, IProcessor op) {
		return FOR_EACH_THAT_EQUALS (in, Item, op); }
	
	/**Returns the first Item of those Item in the Collection that equals this Item,
	 * that also fulfills the Test of the ITester Object.
	 * The generic Solution is slow
	 * and can be highly optimized in concrete Implementations. */
	public Object nextOfEachThatEqualsThat(Object Item, ITester Test) {
		return NEXT_OF_EACH_THAT_EQUALS_THAT(in, Item, Test); }
	
	/**Tests, whether this Object exists in the Set,
	 * Returns it, when found, otherwise returns streamIO.Enumerator.EOL	 */
	public Object findNext (Object Item) { return FIND_NEXT(in, Item, null); }
	
	/** Returns as many Items as are available() currently 	 */
	public Object[] nextItems() { return NEXT_ITEMS(in); }
	
	/** Returns as many Items as possible, but maximum numItems stored in Items from Index Begin 	 */
	public int nextItems(Object[] Items, int numItems, int Begin) { return NEXT_ITEMS(in, Items, numItems, Begin); }
	
	/** Returns as many Items as possible, but maximum numItems stored in Items
	  * small Optimization in using the Default Parameters right away */
	public int nextItems(Object[] Items, int numItems) { return NEXT_ITEMS(in, Items, numItems, -1); }
	
	/** Returns as many Items as possible, but maximum Items.length in Items 	 */
	public int nextItems(Object[] Items) { return NEXT_ITEMS(in, Items, Items.length, -1); }
	
}
