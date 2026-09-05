/*
 * Created on 06.02.2005
 *
 * Memory (dynamic Array)- backed Stream, which can work in FIFo and LIFO Mode 
 * and can synchronize read/write Access between Threads. 
 */
package streamIO.integer.pipe;

import java.io.IOException;
import java.security.InvalidParameterException;

import math.vector.AVector;
import streamIO.integer.AStreamByte;
import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamOutByte;
import streamIO.object.IPipe;
import streamIO.object.IStreamIn;

/**
 * Memory (dynamic Array)- backed Stream, which can work in FIFo and LIFO Mode 
 * and can synchronize read/write Access between Threads. 
 * This Class does not provide an Iterator, because it is typically used
 * only by a single Client or as a Communicator between two Threads.
 * When the Capacity is depleted, it automatically allocates new Space.
 * Valid Elements are between QP (QueuePointer) and SP (StackPointer),
 * so usually SP > QP, except if there has been a RollOver/Wrap-Around.
 * ...QP]xxxxSP]... or xxxSP]...QP]xxx
 * This DeQeue can act as both a Queue and/or a Stack.
 * Due to it's specialized use, it should not be used as a RAContainer,
 * instead use the Array.Array Class.
 *
 * @author heuerm
 * 
 * similar Classes: 
 * @see streamIO.integer.pipe.PipeByte which does the same as this Class! 
 * @see streamIO.object.enumer.container.DeQueueArr 
 * 		which implements the same for Object Streams
 * <!-- docstate
 * tags: [code/pipe_abstraction, code/pipe_implementation]
 * concepts: [In-Memory Producer-Consumer Byte Pipes]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class MemoryPipe 
extends AStreamByte {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : testing & Main Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty smoke-test entry point; performs no action. */
	public static void main(final String[] args) {
	}

	/** Empty test stub; performs no action. */
	public static void testIt(final String[] args) {
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//  static Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/**Default Value, if the Parameter 'Capacity' is not provided	 */
	public static int  DEFAULT_CAPACITY = 12;
	
	/**Default Value, if the Parameter 'Stack' is not provided	 */
	public static byte DEFAULT_ORDER = IStreamIn.ORDER_STACK;
	
	/**Default Value, if the Parameter 'capacityIncrement' is not provided	 */
	public static byte DEFAULT_INCREMENT = -1;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/** Stack (LIFO) or Queue (FIFO) for the current Operation,
	  * since the Space is limited to the Number of Vertices.	 */
	protected int[] IFO;
	
	/** StackPointer,  last Element (Tail) of IFO	 */	protected int SP = 0;
	/** QueuePointer, first Element (Head) of IFO	 */	protected int QP = 0;
	
	/** marked StackPointer,  last Element (Tail) of IFO	 */	protected int markSP = SP;
	// TODO: LOGIC: initialized from SP instead of QP (copy-paste from the line above) - the
	// marked Queue Pointer should mirror QP, not SP; as written it starts wrong whenever SP != QP.
	/** marked QueuePointer, first Element (Head) of IFO	 */	protected int markQP = SP;
	
	/** Increment when the Space runs out.
	  * positive Numbers give linear Growth
	  * negative Numbers exponential Growth
	  * Zero inhibits Growth	*/
	protected int mIncrement = -1;
	
	/**Flag that determines, whether the Store Operations
	 * work in LIFO (Stack) Fashion (true )
	 * or	in FIFO (Queue) Fashion (false)	 */
	protected byte mOrder = DEFAULT_ORDER; //Pipe.OrderQueue;
	
	///////////////////////////////////////////////////////////////////////////////
	//  Accessor (get/set/is) Methods
	///////////////////////////////////////////////////////////////////////////////
	
	/** Sets the Flag that determines, whether the Store Operations
	  * work in LIFO (Stack) Fashion (true )
	  * or	in FIFO (Queue) Fashion (false)	 */
	public void setOrder(final byte Order)	{
		if ((Order != IStreamIn.ORDER_QUEUE) &&
			(Order != IStreamIn.ORDER_STACK)) {
			throw new InvalidParameterException(); }
		this.mOrder = Order; }
	
	/** Returns the Flag that indicates, whether the Operations above
	  * work in LIFO (Stack) Fashion (true )
	  * or	in FIFO (Queue) Fashion (false)	 */
	public byte getOrder()	{ return mOrder; }
	
	/** Clears the Queue	 */
	//public Group zeroAt()	{ SP = QP = 0; return this; }
	
	/** Returns the number of Item stored in the Store	 */
	public int getInt() { return (SP >= QP) ? SP-QP : SP-QP+IFO.length; }
	
	///////////////////////////////////////////////////////////////////////////////
	//	Constructors
	///////////////////////////////////////////////////////////////////////////////
	
	/** Constructor allocating the Space	 */
	public MemoryPipe(final int _initialCapacity, final int _capacityIncrement, final byte _Order) {
		IFO = new int[_initialCapacity];
		this.mIncrement = _capacityIncrement; 
		setOrder(_Order);
//		mEnum = new ArrayEnumerator(IFO);
	}
	
	/** Constructor allocating the Space	 */
	public MemoryPipe(final int _initialCapacity, final int _capacityIncrement)	{ this(_initialCapacity, _capacityIncrement, DEFAULT_ORDER); }
	
	/** Constructor allocating the Space	 */
	public MemoryPipe(final int _capacityIncrement, final byte _Order) { this(DEFAULT_CAPACITY, _capacityIncrement, _Order); }
	
	/** Constructor allocating the Space	 */
	public MemoryPipe(final int _initialCapacity)	{ this(_initialCapacity, DEFAULT_CAPACITY, DEFAULT_ORDER); }
	
	/** Constructor allocating the Space	 */
	public MemoryPipe(final byte _Order) { this(DEFAULT_CAPACITY, DEFAULT_INCREMENT, _Order); }
	
	/** Constructor allocating the Space	 */
	public MemoryPipe() { this(DEFAULT_CAPACITY, DEFAULT_INCREMENT, DEFAULT_ORDER); }
	
	/** @return a new (uninitialized) Instance of this Class	  */
	//public CopyAble newInstance() { return new MemoryStream(); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Increases the Space for the DeQueue. 
	 * 
	 * @param N new minimum Capacity 
	 * @return the actual new Capacity 
	 */
	public int setCapacity(final int minCapacity) {
		if (minCapacity   <  IFO.length) {
			return IFO.length; } //length sufficient
		final int[] tmp = new int[AVector.ENLARGED_CAPACITY(IFO.length, mIncrement, minCapacity)];
		if (SP > QP) 	//isZero() when no Wrap-Around has happened.  
			System.arraycopy(IFO, QP+1, tmp, QP+1, SP-QP+1);	//keep Location to preserve Mark
		else if (SP <= QP) {	//wrap-around: Split copying up around the End 
			++QP;
			int  L2  = IFO.length-QP;
			int QP2  = tmp.length-L2; 	//IFO.length-QP == N-QP2 == L2
			if (QP2 >= tmp.length) 
				QP2  = 0; 
			System.arraycopy(IFO,  0, tmp,   0, SP+1);	//copy the Beginning part
			System.arraycopy(IFO, QP, tmp, QP2, L2);	//copy the End part
			QP = QP2-1;
		}//else System.arraycopy(IFO, 0, tmp, 0, IFO.length);	//not necessary to copy the whole Array
		IFO = tmp;
		return tmp.length; 
	}
	
	/** Returns the current backing Array's Capacity.
	 * @return the minimum Number of Items fitting into this Buffer.
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number. */
	public int getCapacity() { return IFO.length; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** adds this Item to the Store in Place: +=
	 * The Type of Item is not analyzed, i.e. Containers are added as is.
	 * Default Operation is addNext, which is easy to implement for Lists,
	 * as well as it ensures that the Item will be picked up by the current Iterator,
	 * which is frequently used e.g. by LL(1) Parsers.
	 * @see Order()
	 * @see streamIO.integer.IStreamOutByte#write(int)	 
	 * @see nextItem()	 
	 */
	public void write(final int _value) throws IOException {
		IFO[(++SP >= IFO.length)?(SP=0):SP] = _value;
		if (SP == QP)
			setCapacity(IFO.length+1);	//throw new AbstractMethodError("Store overrun!");
	}
	
	/**very effective Bulk Methods 
	 * @see streamIO.integer.IStreamOutByte#addItem(int[], int, int)
	 */
	public IStreamOutByte addItem(final int[] tmp, int off, int len) {
		if (mOrder == IStreamIn.ORDER_STACK) { 
			AStreamOutByte.WRITE_SAFE(this, tmp, off, len);
			return this; }
		//fast Track Copy
		int newSP = SP+len;
		setCapacity(newSP); //...otherwise enlarge (requires an additional Copy
		System.arraycopy(IFO, SP+1, tmp, off, len);	//not necessary to copy the whole Array
		SP = newSP;
		//} else { //if space is sufficient, split...
		return this; 
	}
	
	/**very effective Bulk Methods 
	 * @see streamIO.integer.IStreamIn_Byte#read(int[], int, int)
	 */
	public int read(final int[] tmp, int off, int len) throws IOException {
		if (mOrder == IStreamIn.ORDER_STACK) 
			return super.read(tmp, off, len); 
		//fast track Copy
		final int avail = available(); 
		if (len > tmp.length - off)
			len = tmp.length - off; 
		if (len > avail) //avoid over-length
			len = avail; 
		if (SP > QP) { 	//isZero() when no Wrap-Around has happened.  
			System.arraycopy(IFO, QP+1, tmp, off, len);	//not necessary to copy the whole Array
			QP += len;
		} else { //if (SP <= QP) {	//wrap-around: Split copying up around the End
			int left = IFO.length-QP; 
			if (len <= left) {
				System.arraycopy(IFO, QP+1, tmp, off, len);	//copy the End part
				QP += len; 
			} else {
				int len2 = len-left; 
				System.arraycopy(IFO, QP+1, tmp, off, left);	//copy the End part
				System.arraycopy(IFO,    0, tmp,   0, len2);	//copy the Beginning part
				QP = len2; 
			}
		}//
		return len;
	}
	
	/** Gets an Item from the Store.
	  * The Flag determines whether it works like a Stack or like a Queue. 
	  * @see streamIO.integer.IStreamIn_Byte#read()	 
	  */
	public int read() throws IOException {
		if (SP == QP) 
			return EOF; //
		return IFO[(mOrder == IPipe.ORDER_STACK)? 
				(SP > 0) 			? SP-- 	: ((SP = IFO.length-1) & 0):
				(++QP < IFO.length) ? QP 	:  (QP = 0)]; }	//pop() / get()
	
	/** Returns the next Item of the Store without removing it.	 */
	public int peekInt() {
		if (SP == QP) 
			return EOF; //
		return IFO[(mOrder == IPipe.ORDER_STACK) ? SP 
				: (QP+1 >= IFO.length)? 0 : QP+1]; }
	
	/** Returns the minimum Number of Items left (in the Buffer).
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number. */
	public int available() {
		if (SP == QP) 
			return -1; //jumps from 1 to -1 this is not really correct,
		return getInt(); } //but since nextItem() does not reduce SP or QP, it has to work like this!
	
	/** Not implemented; always returns 0.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; } //stream.getPosition(); }

	/** Not implemented; always returns -1.
	 * @see streamIO.integer.IStreamIn_Byte#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; } //Long.MAX_VALUE; }
	
	/** Does not work properly, since the Contents is overwritten 
	 * especially with a Stack, any Item added immediately overwrites the previous one! 
	 * @see streamIO.integer.IStreamIn_Byte#mark(int)	 */
	public void mark(final int readlimit) {
		markSP = SP; 
		markQP = QP; 
	}
	
	/** Resets to the last marked SP/QP, then jumps forward by the given Position.
	 * @see streamIO.integer.IStreamIn_Byte#reSet(long)	 */
	public long reSet(final long _position) { //throws IOException {
		SP = markSP;
		QP = markQP;
		return jump(_position);
	}

	/** Discards the backing Array, making this Pipe unusable.
	 * @see streamIO.integer.IStreamIn_Byte#close()	 */
	public void close() { //throws IOException {
		IFO = null; 
	}
	
	/** Always flushed, caching is inherent 
	 * @see streamIO.integer.IStreamOutByte#flush()	 */
	public void flush() { } //throws IOException { }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Convenience Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Returns true, if the Store is empty.	 */
	public boolean isZero() { return (SP == QP); }
	
	/** Returns true, if the Store is full.	 */
	public boolean isFull() { return (SP == QP-1); }
	
	/** Returns the Item at the given absolute Position
	  * counted from the Queue Start.
	  * Returns SOI for negative Indices and EOI for Indices larger than the Size
	  * Could also use try/catch Block, but that is much more expensive!	 */
	public int getIntAt(int index) {
		if (index <  0) 
			return EOF; 
//		if (index > available()) return EOI;
		index += QP;
		if (index >= IFO.length)
			index -= IFO.length;
		if (index > SP) 
			return EOF;
		return IFO[(int) index]; }
	
	/** Puts an Item at the Head of the Queue, i.e. the Bottom of the Stack
	  * This makes a Queue work like a Stack and vice versa. */
	public MemoryPipe putHead (final int Item) {
       IFO[(QP > 0) ? QP-- : ((QP = IFO.length-1) & 0)] = Item;
       return this; }
	
}
