package streamIO.object.enumer;

import java.util.ArrayList;

import streamIO.IIStreamOut;

/**This Class models an asynchronous Pipe which allows more flexible,
 * asynchronous Communication between two Threads than 'streamIO.Monitor':
 * To facilitate continuous Operation on both Sides without Memory Overload,
 * this Class also has a MaxCapacity.
 *
 * Thus neither the Sender nor the Receiver are blocked in any way,
 * except if the Queue is empty.
 * TODO: Introduce a Timeout for the Blocking.
 * @todo: enhance the Class according to @see PipeByte
 * @stereotype enumeration
 */
public class CachePipe
extends APipe //has more virtual Methods
//implements Pipe
{
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Default Value for the Initial Capacity 	*/
	static int INITIAL_CAPACITY = 10;

	/** Default Value for the Capacity Increment
	 * when 0 no Increment happens
	 * when negative, shifts the Size
	 */
	static int CAPACITY_INCREMENT = 10;

	/** Default Value for the Maximum Capacity 	*/
	static int MAX_CAPACITY = 1000000;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** The actual maximum allowed Capacity. 	 */
	protected int MaxCapacity;

	/** The current Item. 	 */
	protected Object currItem;

    /**Buffer, if the Producer is faster than the Consumer	 */
	protected ArrayList Buffer = new ArrayList(INITIAL_CAPACITY);

	//protected Vector Buffer = new Vector(); //very ineffective, because synchronized!
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor defaulting all Values	 */
	public CachePipe(int initialCapacity, int MaxCapacity) {
		Buffer = new ArrayList(initialCapacity); // Vector(initialCapacity, CapacityIncrement);
		this.MaxCapacity = MaxCapacity; }

	/**Empty Constructor defaulting all Values	 */
	public CachePipe() {
		this (INITIAL_CAPACITY, MAX_CAPACITY); }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Reads a single Value from the Cache.
	 * When no Value is available, the requesting Thread is put to sleep.
	 * Alternatively SOI could be returned or an exception be thrown not to block the Thread.	 */
	public synchronized Object nextItem() {
		while (Buffer.size() == 0) //use 'while', because several Threads could 'notify()' this Class
			try { wait(); // until notifiy()
			} catch (InterruptedException e) {}
		notify();
		return currItem =  Buffer.remove(0); }	//Buffer.removeElementAt(0); //ineffective, because of Array Moving...
	//this is quite ineffective! Use a dynamic Array with SP and QP instead!

	/**Reads a single Value from the streamIO and caches it.
	 * When the Cache is full, the writing Thread is put to sleep. 	 */
	public synchronized IIStreamOut addItem(final Object value) {
		while (Buffer.size() > MaxCapacity) //use 'while', because several Threads could 'notify()' this Class
			try { wait(); // until notifiy()
			} catch (InterruptedException e) {}
		Buffer.add(value); // addElement(value);
		notify();
		return this; }
	
	/** Returns the minimum Number of Items available. 	 */
	public long availAble() { return Buffer.size(); }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }
	
	/**
	 * @return the Order in which Elements are returned or processed.
	 * @see streamIO.Float.IStreamIn_Int#getOrder()
	 */
	public byte getOrder() { return
		//stack ? OrderStack :
		ORDER_QUEUE; }

	/** Returns the current Item. 	 */
	public Object currItem() { return currItem; }

}
