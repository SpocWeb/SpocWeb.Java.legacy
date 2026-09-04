package stringOp;

//import

/**Implementation of a DeQueue with Integers using an Array.
 * When the Capacity is depleted, an Error occurs.
 * It can only run once through it's Store,
 * so only N put() and get() Operations in Queue Style are possible.
 * 
 * @see streamIO.integer.pipe.PipeByte which implements a DeQueue with dynamically growing Capacity. 
 * @see streamIO.object.enumer.container.DeQueueArr which grows for Objects. 
 */
public class DeQueueInt
//	implements intDeQueue	//intDeQueue uses Objects instead of int.
{
	/**Stack (LIFO) or Queue (FIFO) for the current Operation,
	 * since the Space is limited to the Number of Vertices.	 */
	int [] IFO;

	/**StackPointer,  last Element (Tail) of IFO	 */	int SP = -1;
	/**QueuePointer, first Element (Head) of IFO	 */	int QP = -1;

	/**Flag that determines, whether the Store Operations
	 * work in LIFO (Stack) Fashion (true )
	 * or	in FIFO (Queue) Fashion (false)	 */
	protected boolean Stack = false;

	/**Sets the Flag that determines, whether the Store Operations
	 * work in LIFO (Stack) Fashion (true )
	 * or	in FIFO (Queue) Fashion (false)	 */
	public void setStack(boolean Stack)
	{this.Stack = Stack;}

	/**Returns the Flag that indicates, whether the Operations above
	 * work in LIFO (Stack) Fashion (true )
	 * or	in FIFO (Queue) Fashion (false)	 */
	public boolean isStack()
	{return Stack;}

	//////////////////////////////
	//	interface intDeQueue	//
	//////////////////////////////

	/**Constructor allocating the Space	 */
	public DeQueueInt(final int Capacity, final boolean Stack)
	{
		this.Stack = Stack;
		IFO = new int [Capacity];
	}

	/**returns the Space for the DeQueue.
	 * @return the Space for the DeQueue.	 
	 */
	public int getCapacity() { return IFO.length; }

	/**Increases the Space for the DeQueue.
	 * Only possible, if no wrap-around has taken place	 */
	public int setCapacity(final int N)
	{
		if (N < IFO.length) 
			return IFO.length;
		if (SP < QP) return IFO.length;	//no increase possible now, because wrap-around
		int[] tmp = new int[N];
		System.arraycopy(IFO, 0, tmp, 0, IFO.length);	//not necessary to copy the whole Array
		IFO = tmp;
		return N;
	}

	/**Gets an Item from the End of the Queue	 */
//	public int getTail ()
//	{return IFO[++QP];}

	//////////////////////////
	//	interface intStack	//
	//////////////////////////

	//////////////////////////
	//	interface intStore	//
	//////////////////////////

	/**Puts an Item into the Store	 */
	public void put (int Item)
	{IFO[++SP] = Item;}	//push(Item);

	/**Gets an Item from the Store	 */
	public int get ()
	{return IFO[Stack?SP--:++QP];}	//pop() / get()

	/**Returns the next Item of the Store without removing it.	 */
	public int peek()
	{return IFO[Stack ? SP : QP+1];}

	/**Returns true, if the Store is empty.	 */
	public boolean isZero()
	{return (SP == QP);}

	//////////////////////////////
	//	interface intDeQueue	//
	//////////////////////////////

	/**Puts an Item at the Head of the Queue, i.e. the Bottom of the Stack	 */
	public void putHead (int Item)
	{IFO[QP--] = Item;}

}
