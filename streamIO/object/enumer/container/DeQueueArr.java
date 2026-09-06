package streamIO.object.enumer.container;

import java.security.InvalidParameterException;

import math.vector.AVector;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.exception.OperationNotSupported;
import streamIO.object.IPipe;

/** Implementation of a DeQueue (double ended Queue) using an Array.
  * Usually a doubly linke List is used,
  * but Arrays are faster because they are not distributed in Memory
  * and unlike other dynamic Structures they don't need to create and destroy
  * Container Objects.
  *
  * The Metaphor of calling nextItem() and testing available() afterwards
  * requires either a rewrite of addItem and removeItem
  * or an available() that is dependent on the Order chosen (Stack or Queue)
  * just like the nextItem().
  *
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
  * An additional use of this DeQueue as a Queue is
  * to add mark() and reset() to otherwise not resettable Streams.
  *
  * Similar Classes:
  * @see stringOp.DeQueueInt  sporting only a fixed Size Buffer.
  * @see streamIO.Byte.PipeByte which supports fast, unsynchronized and unchecked Access
  *
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: cb69c9610927006d2ac5e9baae815e636e02f141726d11a0040d9d4a8741e861
  * stale: false
  * -->
  */
public class DeQueueArr
extends AContainer {
	
	///////////////////////////////////////////////////////////////////////////////
	//  static Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	/** Serialization version UID. */
	private static final long serialVersionUID = 1L;

	/**Default Value, if the Parameter 'Capacity' is not provided	 */
	public static int  DEFAULT_CAPACITY = 12;
	
	/**Default Value, if the Parameter 'Stack' is not provided	 */
	public static byte DEFAULT_ORDER = ORDER_STACK;
	
	///////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/** Stack (LIFO) or Queue (FIFO) for the current Operation,
	  * since the Space is limited to the Number of Vertices.	 */
	protected Object[] IFO;
	
	/** StackPointer,  last Element (Tail) of IFO	 */	protected int SP = 0;
	/** QueuePointer, first Element (Head) of IFO	 */	protected int QP = 0;
	
	/** Increment when the Space runs out.
	  * positive Numbers give linear Growth
	  * negative Numbers exponential Growth
	  * Zero inhibits Growth	*/
	protected int increment = -1;
	
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
		if ((Order != ORDER_QUEUE) &&
			(Order != ORDER_STACK)) {
			throw new InvalidParameterException(); }
		this.mOrder = Order; }
	
	/** Returns the Flag that indicates, whether the Operations above
	  * work in LIFO (Stack) Fashion (true )
	  * or	in FIFO (Queue) Fashion (false)	 */
	public byte getOrder()	{ return mOrder; }
	
	/** Clears the Queue	 */
	public IGroup zeroAt()	{ SP = QP = 0; return this; }
	
	/** Returns the number of Items stored in the Store	 */
	public int getInt() { 
		return (SP >= QP) ? SP-QP : SP-QP+IFO.length; }
	
	///////////////////////////////////////////////////////////////////////////////
	//	Constructors
	///////////////////////////////////////////////////////////////////////////////
	
	/** Constructor allocating the Space	 */
	public DeQueueArr(final int Capacity, final byte Order) {
		IFO = new Object [Capacity];
		setOrder(Order);
//		mEnum = new ArrayEnumerator(IFO);
	}
	
	/** Constructor allocating the Space	 */
	public DeQueueArr(final int Capacity)	{ this(Capacity, DEFAULT_ORDER); }
	
	/** Constructor allocating the Space	 */
	public DeQueueArr(final byte Order) { this(DEFAULT_CAPACITY, Order); }
	
	/** Constructor allocating the Space	 */
	public DeQueueArr() { this(DEFAULT_CAPACITY, DEFAULT_ORDER); }
	
	/** Creates a new, empty DeQueueArr with the default Capacity and Order.
	 * @return a new (uninitialized) Instance of this Class	  */
	public ICopyAble newInstance() { return new DeQueueArr(); }
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Increases the Space for the DeQueue.
	  * Only possible, if no wrap-around has taken place 
	  */
	public int setCapacity(final int newCap) {
		if (newCap   <  IFO.length) 
			return IFO.length;  //length sufficient
		final Object[] tmp = new Object[newCap];
		if (SP > QP) 	//isZero() (SP == QP) SP > QP usually
			System.arraycopy(IFO, QP+1, tmp, QP+1, SP-QP+1);	//not necessary to copy the whole Array
		else { //if (SP <= QP) {	//Split copying up around the End (wrap-around)
			++QP;
			final int  L2 = IFO.length-QP;
			int QP2 = newCap-L2; 	//IFO.length-QP == N-QP2 == L2
			if (QP2 >= newCap) 
				QP2  = 0; 
			System.arraycopy(IFO,  0, tmp,   0, SP+1);	//copy the Beginning part
			System.arraycopy(IFO, QP, tmp, QP2, L2);	//copy the End part
			QP = QP2-1;
		}//else System.arraycopy(IFO, 0, tmp, 0, IFO.length);	//not necessary to copy the whole Array
		IFO = tmp;
		return newCap; }
	
	/** Returns the current Length of the backing Array.
	 * @return the minimum Number of Items fitting into this Buffer.
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number. */
	public int getCapacity() { return IFO.length; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests whether an Item is stored the Store	 */
	public boolean contains(final Object Item) {
		for(int i  = QP; i != SP; )
			if (Item.equals(IFO[(++i < IFO.length) ? i : (i = 0)]))
			return true;
			return false; }
	
	/** Returns the minimum Number of Items left (in the Buffer).
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number. */
	public long availAble() {
		if (SP == QP) 
			return -1; //jumps from 1 to -1 this is not really correct,
		return getInt(); } //but since nextItem() does not reduce SP or QP, it has to work like this!
	
	/** Gets an Item from the Store.
	  * The Flag determines whether it works like a Stack or like a Queue.
	  */
	public Object nextItem() {	//Wrap-around order
		if (SP == QP) 
			return IIStreamIn.EOI; //
		return IFO[(mOrder == IPipe.ORDER_STACK)? (SP > 0) ? SP-- : ((SP = IFO.length-1) & 0):
						 (++QP < IFO.length) ? QP : (QP = 0)]; }	//pop() / get()
	
	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * Default Operation is addNext, which is easy to implement for Lists,
	  * as well as it ensures that the Item will be picked up by the current Iterator,
	  * which is frequently used e.g. by LL(1) Parsers.
	  * @see Order()
	  * @see nextItem()	 */
	public IIStreamOut addItem(Object Item) {
		IFO[(++SP >= IFO.length)?(SP=0):SP] = Item;
		if (SP == QP)
			setCapacity(AVector.ENLARGED_CAPACITY(IFO.length, increment));	//throw new AbstractMethodError("Store overrun!");
        return this; }
	
	/** Puts an Item at the Head of the Queue, i.e. the Bottom of the Stack
	  * This makes a Queue work like a Stack and vice versa. */
	public DeQueueArr putHead (Object Item) {
       IFO[(QP > 0) ? QP-- : ((QP = IFO.length-1) & 0)] = Item;
		if (SP == QP)
			setCapacity(AVector.ENLARGED_CAPACITY(IFO.length, increment));	//throw new AbstractMethodError("Store overrun!");
       return this; }

	/** Replaces the current Item at the Iterator     */
	public Object replaceCurr(final Object Item) {
		int current = (mOrder == IPipe.ORDER_STACK) ? SP : (QP+1 < IFO.length) ? QP+1 : 0;
		Object ret = IFO[current]; IFO[current] = Item;
        return ret; }
	
	/** Returns the next Item of the Store without removing it.	 */
	public Object peekItem() {
		if (SP == QP) 
			return null; //
		return IFO[(mOrder == IPipe.ORDER_STACK) ? SP : (QP+1 >= IFO.length)? 0 : QP+1]; }
	
	/** Returns true, if the Store is empty.	 */
	public boolean isZero() { return (SP == QP); }
	
	/** Returns true, if the Store is full.	 */
	public boolean isFull() { return (((SP+1 >= IFO.length) ? 0 : SP+1) == QP); }
	
	//////////////////////////////
	//	interface intDeQueue	//
	//////////////////////////////
	
	/** Truncates the Queue at the given absolute Position
	  * counted from the Start.
	  */
	public DeQueueArr truncateAt(int index) { //throws NoSuchMethodException {
		if (index <  0) 
			throw new OperationNotSupported(DeQueueArr.class); 
//		if (index > available()) return EOI;
		index += QP;
		if (index >= IFO.length)
			index -= IFO.length;
		if (index > SP) 
			throw new OperationNotSupported(DeQueueArr.class); 
		QP = index;
		return this; }

	/** Returns the Item at the given absolute Position
	  * counted from the Queue Start.
	  * Returns SOI for negative Indices and EOI for Indices larger than the Size
	  * Could also use try/catch Block, but that is much more expensive!	 */
	public Object getAt(int index) {
		if (index <  0) return SOI;
//		if (index > available()) return EOI;
		index += QP;
		if (index >= IFO.length)
			index -= IFO.length;
		if (index > SP) 
			return EOI;
		return IFO[(int) index]; }
	
	/** Renders every slot of the backing Array with 'Q'/'S' markers at the Queue/Stack
	 * Pointers, for Debugging.
	 * @return an alternative String Representation of this DeQueue
	  * listing the whole Array indicating the Pointers and all Elements.
	  * used for Debugging only
	  */
	public String toDetailedString() {
		StringBuffer B = new StringBuffer();
		int i = -1;
		while(++i < IFO.length) {
			if (i == QP) B.append('Q'); B.append(IFO[i]).append(',');
			if (i == SP) B.append('S');
		}
		return B.toString(); }

	//////////////////////////////
	//	Interface Container
	//////////////////////////////

	/** The Iterator must only run over the defined Items of the DeQueue! */
//    public Iterator Iterator() { return new DQArrayIterator(this); }

}
