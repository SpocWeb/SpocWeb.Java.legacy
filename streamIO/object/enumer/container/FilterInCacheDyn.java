package streamIO.object.enumer.container;

import streamIO.IAvailAble;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.object.AFilterIn;
import streamIO.object.IPipe;
import streamIO.object.IStreamIn;

/**
  * Title: FilterInCacheDyn<p>
  * Description:
  * Provides for Cacheing the Elements of an IStreamIn
  * to enable the mark() and reset() Operations over a limited Number of Elements.
  *
  * There are generally two frequent Scenarios for a Cache:
  * a fixed Size Cache, implemented by @see FilterInCache
  * (which is also supported by the streamIO Interface)
  * and a dynamic Cache, implemented by @see FilterInCacheDyn
  *
  * When placing a mark() while replaying Elements,
  * the Elements would have to be moved, except when using a circular Array (Queue).
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-12-2002, 05:17 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FilterInCacheDyn
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
	protected DeQueueArr cache;

	/** current Position in the Cache	 */
	protected int Pos; // = 0;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected FilterInCacheDyn(IStreamIn Delegate) { this(Delegate, DefaultCapacity); }

	/** Constructor defining an initial Cache Capacity	 */
	protected FilterInCacheDyn(IStreamIn Delegate, int Capacity) {
		super(Delegate);
		cache = new DeQueueArr(Capacity, IPipe.ORDER_QUEUE); }

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
	public IMarkAble mark(final long readLimit) { //throws NoSuchMethodException {
	    cache.truncateAt(Pos); //need to move the Cache when marking while replaying
		Pos = 0;
		return this; }

	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * Returns the Number of Positions actually moved (e.g. due to EOF)	 */
	public long reSet(long _position) { //throws NoSuchMethodException {
		long Top = cache.availAble();
		if (_position <= Top) {
			return Pos = (int)_position; } 
		Pos = (int) Top;
		return Top + jump(_position - Top); }

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
		if (++Pos < cache.availAble()) { //increment Pos in any case,
			return cache.getAt(Pos-1); }
		currItem = in.nextItem();
		cache.addItem(currItem);
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
		return cache.availAble() - Pos + ((IAvailAble)in).availAble(); }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterInCacheDyn.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
