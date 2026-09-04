package streamIO.object.filterIn;

import streamIO.IAvailAble;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.exception.OperationNotSupported;
import streamIO.object.AFilterIn;
import streamIO.object.IStreamIn;

/**
  * Title: FilterInCache<p>
  * Description:
  * Provides for Caching the Elements of an IStreamIn
  * to enable the mark() and reset() Operations over a limited Number of Elements.
  *
  * There are generally two frequent Scenarios for a Cache:
  * a fixed Size Cache, implemented by @see FilterInCache
  * (which is also supported by the streamIO Interface)
  * and a dynamic Cache, implemented by @see FilterInCacheDyn
  *
  * The limited Number of Elements is used to presize the Array.
  * When placing a mark() while replaying Elements,
  * the Elements have to be moved, except when using a circular Array (Queue).
  *
  * When the Limit for the Cache is exceeded,
  * the Reset Method throws an Exception.
  *
  * Design Decisions / Implementation Details:
  * Made it final, because that speeds up Operation
  * when declared explicitly and not polymorphic.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-12-2002, 05:17 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class FilterInCache
extends AFilterIn {

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Default Capacity for the Cache.
	  */
	public static int DefaultCapacity = 20;

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Cache for the Elements of the streamIO	 */
	protected Object[] cache;

	/** current Position in the Cache	 */
	protected int pos; // = 0;

	/** current Top of the Queue	 */
	protected int top; // = 0;

	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	protected FilterInCache(IStreamIn Delegate) {
		this(Delegate, DefaultCapacity); }

	/** Constructor defining an initial Cache Capacity	 */
	protected FilterInCache(IStreamIn Delegate, int Capacity) {
		super(Delegate);
		cache = new Object[Capacity]; }

	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Marking and Resetting a Stream (for re-Processing in Parsing, if supported)
	////////////////////////////////////////////////////////////////////////////////

	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation
	  * Reset throws an Exception if it is not supported,
	  * since it requires either a Cache or an Algorithm. 	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		reSet (0); return this; }

	/** Marks the current position in this Iterator.
	  * A subsequent call to the reset method repositions this Iterator
	  * to the last marked position.
	  * Marking and Resetting requires either a Cache
	  * or an Algorithm to replay the Values.
	  * The Default ReadLimit of this Class is used here. 	 */
	public IMarkAble mark() { //throws NoSuchMethodException {
		return mark(DefaultCapacity); }

	/** Marks the current position in this Iterator.
	  * A subsequent call to the reset method repositions this Iterator
	  * at the last marked position.
	  * The readlimit arguments tells this input stream to allow that many Items
	  * to be read before the mark position gets invalidated.
	  * This is to limit the Blocking of System Ressources	 */
	public IMarkAble mark(long ReadLimit) { //throws NoSuchMethodException {
		top = pos = 0;
		if (cache.length < ReadLimit) { //resize the Cache
			Object[] tmp = new Object[(int) ReadLimit];
			if (top > pos) {
				System.arraycopy(cache, pos, tmp, 0, top - pos); }
			cache = tmp; //need to move the Cache when marking while replaying
		} return this; }

	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * Returns the Number of Positions actually moved (e.g. due to EOF)	 */
	public long reSet(final long Position) { //throws NoSuchMethodException {
		if (top > cache.length) 
			throw new OperationNotSupported("Top ="+top+" > cacheSize="+cache.length); 
		if (Position <= top) {
			return pos = (int)Position; } pos = top;
			return top + jump(Position - top); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	protected Object nextItemInternal() {
		if (++pos < top) {
			return cache[pos-1]; } //postIncrement is ineffective...
		currItem = in.nextItem();
		if (top < cache.length) {
			cache[top++] = currItem; } //but it makes other Methods easier!
		return currItem; }

	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble() {
		return top - pos + ((IAvailAble)in).availAble(); }

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() {
		System.out.println("Testing " + FilterInCache.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(); }

}
