/*
 * Created on 14.09.2005
 *
 * Defines the Interface and Order Values for Streams and Stores. 
 */
package streamIO;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines the Interface and Order Values for Streams and Stores. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public interface IOrdered {

	/** @return true, when the Items returned support the OrderAble Interface
	  * and they are returned in (strictly) ascending or descending Order.
	  * This is used as an additional criterion for Search Operations like findFirst()
	  * It is replaced by the @see Pipe.getOrder() Method: ordered, random, sorted
	  * Monotonous is implicitly sorted!	 */
	//	public boolean isMonotonous();
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder();

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants for the Sequence of Elements in the Iterator
	////////////////////////////////////////////////////////////////////////////////
	
	///Types of Random Numbers
	
	/** Constant denoting the Order "undefined" (Default)
	 * The Value matches the Result of Comparable#compareTo() and OrderAble#Position()
	 * @see HashIterator for an Iterator with (quasi) undefined Sequence	 */
	final static public byte ORDER_NONE = Byte.MIN_VALUE;
	
	/** Constant denoting the Order "Random", 
	 * which is typically fed into the Algorithm externally, 
	 * since it requires a multi-parametric System to generate really random "Noise"
	 */
	final static public byte ORDER_RANDOM = Byte.MIN_VALUE+1;
	
	/** Constant denoting the Order "Pseudo-Random", 
	 * a typical Sequence of a computed Random Number Generator, 
	 * which should lead to an Error Estimate of 1/SqRt(N) but has a definite Period 
	 * and likely Correlations in the lower Bits. 
	 */
	final static public byte ORDER_RANDOM_PSEUDO = Byte.MIN_VALUE+2;
	
	/** Constant denoting the Order "Sub-Random", 
	 * a typical Sequence of a stateful Random Number Generator, 
	 * which should lead to an Error Estimate of 1/N. 
	 */
	final static public byte ORDER_RANDOM_SUB = Byte.MIN_VALUE+3;
	
	/** Constant denoting an alternating Order, 
	 * i.e. up, down, up, down, ...
	 */
	final static public byte ORDER_ALTERNATING = -3;
	
	///Order Relation
	
	/** Constant denoting the Order "SortDesc", i.e. sorted descending
	 * The Value matches the Result of Comparable#compareTo() and OrderAble#Position()
	 * @see ArrayIterator for an Iterator with descending Order	 */
	final static public byte ORDER_DESC_STRICT = -2;
	
	/** Constant denoting the Order "SortDesc", i.e. sorted descending
	 * The Value matches the Result of Comparable#compareTo() and OrderAble#Position()
	 * @see ArrayIterator for an Iterator with descending Order	 */
	final static public byte ORDER_DESC = -1;
	
	/** Constant denoting the Order "Constant", i.e. all Members are the same. 
	 * The Value matches the Result of 
	 * @see Comparable#compareTo(java.lang.Object) and 
	 * @see function.IOrderAble#Position(Object)
	 * @see function.byref.ByRefFloat#Sign(float)
	 * @see ArrayIterator for an Iterator with descending Order	 */
	final static public byte ORDER_CONST = 0;
	
	/** Constant denoting the Order "SortAsc", i.e. sorted ascending
	  * The Value matches the Result of Comparable#compareTo() and OrderAble#Position()
	  * @see ArrayIterator for an Iterator with ascending Order	 */
	final static public byte ORDER_ASC = +1;
	
	/** Constant denoting the Order "SortAsc", i.e. sorted ascending
	  * The Value matches the Result of Comparable#compareTo() and OrderAble#Position()
	  * @see ArrayIterator for an Iterator with ascending Order	 */
	final static public byte ORDER_ASC_STRICT = +2;
	
	///Array or Buffer Traversal
	
	/** Constant denoting the "Queue"-Order, i.e. LIFO or Online Processing
	 * also user for ascending Array Traversal
	 * @see ListIterator for an Iterator with LIFO Ordering	 */
	final static public byte ORDER_QUEUE = +19;
	
	/** Constant denoting the "Priority"-Order, i.e. Priority Queue Processing
	  * Elements are ordered according to a (calculated) Priority.
	  * @see TODO: for an Iterator with Priority Order like a Heap	 */
	final static public byte ORDER_PRIORITY = +20;
	
	/** Constant denoting the "Stack"-Order, i.e. FIFO Processing
	 * also user for descending Array Traversal
	 * @see ListIterator for an Iterator with FIFO Ordering	 */
	final static public byte ORDER_STACK = +21;
	
	///Tree Traversal
	
	/** Constant denoting the Pre-Order TreeWalk, i.e. this, left Child, right Child
	  * @see ListIterator for an Iterator with FIFO Ordering	 */
	final static public byte ORDER_PRE = +29;
	
	/** Constant denoting the In-Order TreeWalk, i.e. left Child, this, right Child
	  * @see ListIterator for an Iterator with FIFO Ordering	 */
	final static public byte ORDER_IN = +30;
	
	/** Constant denoting the Post-Order TreeWalk, i.e. left Child, right Child, this
	  * @see ListIterator for an Iterator with FIFO Ordering	 */
	final static public byte ORDER_POST = +31;
	
}
