package streamIO.integer.pipe;

import java.io.IOException;
import java.security.InvalidParameterException;

import math.vector.AVector;
import streamIO.Assert;
import streamIO.Log;
import streamIO.object.IStreamIn;

/**Implementation of a fast DeQueue for int Values using an Array in Memory.
 * The Client can allocate new Space.
 * It does a Wrap-around on the Beginning and End.
 * 
 * Similar Classes: 
 * @see streamIO.integer.pipe.MemoryPipe which does the same as this Class!
 * 
 * Subclasses:
 * @see streamIO.Byte.Encoding.SynchPipeByte
 * which synchronizes reading and writing,
 * automatically enlarges the Array
 * optionally calls Trigger Methods to synchronize Reading and Writing
 * waits for a TimeOut to pass.
 *
 * Design Decisions:
 * Using a regular Array for storing the Items
 * plus two Pointers: a QueuePointer QP and a StackPointer SP
 *
 * In regular     Order, the Positions of these Pointers are:
 * ...Q123S... and the Number of Items is SP-QP
 * In wrap-around Order, the Positions of these Pointers are:
 * ...S...Q... and the Number of Items is SP-QP+IFO.length
 *
 * The Buffer can be mark()ed and reset() by restoring the QP
 * or the SP (depending on whether it works as a Queue or as a Stack).
 *
 * The Array is empty when SP == QP, especially at Start S==Q....
 * The Array is full  when SP == QP-1 (mod IFO.length), especially Q....S
 * so the Capacity is only IFO.length-1
 * to be able to distinguish the full Case from the empty Case!
 *
 * Similar Classes:
 * @see stringOp.DeQueueInt sporting only a fixed Size Buffer.
 * @see streamIO.Object.Enumerator.Container.DeQueueArr
 */
public class PipeByte
extends APipeByte {

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Default Value for the Initial Capacity 	*/
	static int INITIAL_CAPACITY = 10;

	/** Default Value for the Maximum Capacity 	*/
	static int MAX_CAPACITY = 1000000;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Stack (LIFO) or Queue (FIFO) for the current Operation,
	 * since the Space is limited to the Number of Vertices.
	 */
	protected int [] IFO;
	
	/**StackPointer,  last Element (Tail) of IFO	 */ protected int SP = 0;
	/**QueuePointer, first Element (Head) of IFO	 */ protected int QP = 0;
	
	/** Maximum Capacity allowed, before writing blocks (to prevent Memory Overflow) */
	protected int maxCapacity;
	
	/** Increment when the Space runs out.
	  * positive Numbers give linear Growth
	  * negative Numbers exponential Growth
	  * Zero inhibits Growth	*/
	protected int increment = -1;
	
	/**Flag that determines, whether the Store Operations
	 * work in LIFO (Stack) Fashion (true )
	 * or	in FIFO (Queue) Fashion (false)	 */
	protected boolean stack = false;
	
	/** Stet the Flag that controls Operation */
	public void setStack(boolean stack_) { this.stack = stack_; }
	
	/**Returns the Flag that indicates, whether the Operations above
	 * work in LIFO (Stack) Fashion (true )
	 * or	in FIFO (Queue) Fashion (false)	 */
	public boolean isStack() { return stack; }
	
	/**Clears the Queue	 */
	public void clear() { SP = QP = 0; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Constructor allocating the Space	 */
	public PipeByte() {
		this(false, INITIAL_CAPACITY, MAX_CAPACITY); }

	/**Constructor allocating the Space	 */
	public PipeByte(boolean stack_) {
		this(stack_, INITIAL_CAPACITY, MAX_CAPACITY); }
	
	/**Constructor allocating the Space	 */
	public PipeByte(boolean stack_, int initialCapacity_) {
		this(stack_, initialCapacity_, MAX_CAPACITY); }
	
	/**Constructor allocating the Space	 */
	public PipeByte(boolean stack_, int initialCapacity_, int maxCapacity_) {
		this.maxCapacity = maxCapacity_;
		this.stack = stack_;
		IFO = new int [initialCapacity_];
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// public Methods
	////////////////////////////////////////////////////////////////////////////
	
	public int getCapacity() { return IFO.length-1; }
	
	/**Increases the Space for the DeQueue. */
	public int setCapacity(int newCap) {
		if (newCap   <  IFO.length) 
			return IFO.length; 
		if (newCap > maxCapacity) 
			newCap = maxCapacity; 
		final int[] tmp = new int[++newCap]; //+1 because the Capacity is always one less!
		if (SP > QP) { 	//isZero() (SP == QP) SP > QP usually
			System.arraycopy(IFO, QP+1, tmp, QP+1, SP-QP+1);	//not necessary to copy the whole Array, only the used part
		} else {	//copy the first part
			++QP;
			final int  L2 = IFO.length-QP;
			int QP2 = newCap-L2; 	//IFO.length-QP == N-QP2 == L2
			if (QP2 >= newCap) 
				QP2  = 0; 
			System.arraycopy(IFO,  0, tmp,   0, SP+1);	//copy the Beginning part
			System.arraycopy(IFO, QP, tmp, QP2, L2);	//copy the End part
			QP = QP2-1;
		}
		IFO = tmp;
		return newCap; }
	
	//////////////////////////////////////////////////////////////////////////////
	//	Interface IStack
	//////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////////
	//	Interface IDeQueue
	////////////////////////////////////////////////////////////////////////////////////
	
	/** encapsulates Health Checks 	 */
	final private void checkCapacity() {
		if (SP == QP) //must never be the same, because then you cannot distinguish between full and empty! 
			setCapacity(AVector.ENLARGED_CAPACITY(IFO.length, increment));	
	}
	
	/** Puts an Item at the Head of the Queue, resp. the Bottom of the Stack	 */
	public void putHead (final int Item) {
		IFO[(QP > 0) ? QP-- : ((QP = IFO.length-1) & 0)] = Item;
		if (increment != 0)
			checkCapacity(); 
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//	Interface IStore
	//////////////////////////////////////////////////////////////////////////////
	
	/** Puts an Item into the Store.
	 * When the Increment is not 0 it is automatically enlarged.
	 * When the store runs over, it loses all Contents,
	 * because the Pointers suggest an empty Store!
	 */
	public void write(final int _item) {
		IFO[(++SP >= IFO.length) ? SP = 0 : SP] = _item; 
		if (increment != 0)
			checkCapacity(); 
	}	//push(); 
	
	/** Gets an Item from the Store
	 * When the Store is empty, it underruns and simulates a full Store!
	 */
	final public int pop() { //Wrap-around order
		return IFO[(  SP > 0) ? SP-- : ((SP = IFO.length-1) & 0)]; }	//push(); 
	
	/** Gets an Item from the Store
	 * When the Store is empty, it underruns and simulates a full Store!
	 */
	final public int get() { //Wrap-around order
		return IFO[(++QP < IFO.length) ? QP : (QP = 0)]; }	//push(); 
	
	/** Gets an Item from the Store
	 * When the Store is empty, it underruns and simulates a full Store!
	 */
	public int read() { if (stack) return pop(); return get(); }	
	
	/**@return the next Item of the Store without removing it.	 */
	public int peek() { return IFO[stack ? SP : (QP+1 >= IFO.length)? 0 : QP+1]; }
	
	/**@return the Number of Elements in this Store.	 */
	public int available() { return getInt(); }
	
	/**@return the Number of Elements in this Store.	 */
	public int getInt() {
		final int ret = SP - QP;
		if (ret >= 0) 
			return ret; 
		return ret + IFO.length; }
	
	/**@return true, if the Store is empty.	 */
	public boolean isZero() { return (SP == QP); } //available() == 0); }
	
	/**@return true, if the Store is empty.	 */
	//public boolean isEmpty() {
	//	return available() == 0; }
	
	/**@return true, if the Store is full.	 */
	public boolean isFull() {  return available() == IFO.length-1; }
	
	//////////////////////////////////////////////////////////////////////////////
	/// Interface IStreamIn_Byte
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Closing an IStreamByte prevents adding to and reading from it.
	 * @see streamIO.Byte.IStreamIn_Byte#close()
	 */
	public void close() throws IOException {}
	
	/**
	 * Flushing an IStreamOutByte writes all cached Data to the persistent Store.
	 * @see streamIO.Byte.IStreamOutByte#flush()
	 */
	public void flush() throws IOException {}
	
	/** Sets the Flag that determines, whether the Store Operations
	  * work in LIFO (Stack) Fashion (true )
	  * or	in FIFO (Queue) Fashion (false)	 */
	public void setOrder(final byte Order) {
		if ((Order != IStreamIn.ORDER_QUEUE) &&
			(Order != IStreamIn.ORDER_STACK)) {
			throw new InvalidParameterException("Only OrderQueue or OrderStack are allowed for "+this.getClass().getName()); }
		stack = (Order == IStreamIn.ORDER_STACK); }
	
	/** @see streamIO.Float.IStreamIn_Int#getOrder()	 */
	public byte getOrder() { return stack ? IStreamIn.ORDER_STACK : IStreamIn.ORDER_QUEUE; }
	
	////////////////////////////////////////////////////////////////////////////////////
	/// mark()ing and reset()ing
	////////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.Byte.IStreamIn_Byte#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
	
	/** Mark for resetting the IStreamIn_Byte */
	protected int mark;
	
	/**
	 * Marking involves storing the QP or the SP
	 * @see streamIO.Byte.IStreamIn_Byte#mark(int)
	 */
	public void mark(int readLimit) {
		setCapacity(readLimit);
		mark = (stack ? SP : QP); }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; } //stream.getPosition(); }
	
	/**
	 * Resetting involves restoring the QP or the SP
	 * @see streamIO.Byte.IStreamIn_Byte#reSet(long)
	 */
	public long reSet(final long position) { //throws IOException {
		if (stack) {
			SP = mark + (int) position;
			if (SP >  IFO.length) {
				SP -= IFO.length; }
		} else {
			QP = mark + (int) position;
			if (QP >  IFO.length) {
				QP -= IFO.length; }
		}
		return position; }
	
	public String toString() {
		StringBuffer B = new StringBuffer();
		for (int i = -1; ++i < IFO.length; ) {
			if (i == QP) B.append('Q'); B.append(IFO[i]).append(',');
			if (i == SP) B.append('S');
		}
		return B.toString();
		//Code for giving out only the real contents
	/*	if (SP > QP) 	//isZero() (SP == QP) SP > QP usually
			System.arraycopy(IFO, QP, tmp, QP, SP-QP);	//not necessary to copy the whole Array
		else if (SP < QP)
		{	//copy the first part
			System.arraycopy(IFO, 0, tmp, 0, SP);	//not necessary to copy the whole Array
			System.arraycopy(IFO, QP, tmp, N-QP, IFO.length-QP);	//not necessary to copy the whole Array
		}
	*/}
	
	/** Reference to the Logger Instance for this Class	 */
	private static final Log L = new Log(PipeByte.class); 
	
	/**Tests all Methods of this Class	 */
	public static void testStack() throws Exception {
		L.enter(); 
		final PipeByte XQ = new PipeByte(true, 5);
		L.n("empty:" + XQ);
		Assert.IS_TRUE( XQ.isZero(), "empty");
		Assert.IS_TRUE(!XQ.isFull(), "not full");
		int i = 0; 
		for(; ++i < 8;) {
			XQ.write(i);	L.n("+"+i + XQ); 
			Assert.EQUALS(i, XQ.getInt()); 
			Assert.IS_TRUE(!XQ.isZero(), "empty");
			//Assert.IS_TRUE(!XQ.isFull(), "not full");
		}
		//XQ.setCapacity(9); //not necessary!
		L.readString("Stack filled"); 
		for(; XQ.available() > 0;) {
			Assert.EQUALS(--i, XQ.read(), "pop()"); 
		} //Note that the Rollover is not detected!
		Assert.IS_TRUE(XQ.isZero(), "empty");
		L.readString("Stack depleted"); 
	}
	
	/**Tests all Methods of this Class	 */
	public static void testQueue() throws Exception {
		L.enter(); 
		final PipeByte XQ = new PipeByte(false, 5);
		L.n("empty:" + XQ);
		Assert.IS_TRUE( XQ.isZero(), "empty");
		Assert.IS_TRUE(!XQ.isFull(), "not full");
		int i = 0; 
		for(; ++i < 8;) {
			XQ.write(i);	L.n("+"+i + XQ); 
			Assert.EQUALS(i, XQ.getInt()); 
			Assert.IS_TRUE(!XQ.isZero(), "empty");
			//Assert.IS_TRUE(!XQ.isFull(), "not full");
		}
		//XQ.setCapacity(9); //not necessary!
		L.readString("Queue filled");
		for(i = 0; XQ.available() > 0;) {
			Assert.EQUALS(++i, XQ.read(), "get()"); 
		} //Note that the Rollover is not detected!
		Assert.IS_TRUE(XQ.isZero(), "empty");
		L.readString("Queue depleted");
	}
	
	/**Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		testStack(); 
		testQueue(); 
	}
	
	/**Tests all Methods of this Class	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
