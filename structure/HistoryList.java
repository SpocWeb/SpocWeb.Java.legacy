package structure; //

/**
  * Encapsulates a growable back/forward History List (a "NavStack") over an Object Array,
  * indexed by a current Position that {@link #nextItem()}/{@link #prevItem()} move.
  *
  * AKA: NavStack
  *
  * This Class encapsulates a History List (Stack)
  * similar to the one used in Wizards and Internet Explorer .
  * A similar Class is used for navigating the Knowledge DB in VB
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-11-2002, 08:33 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:16:22Z
  * digest: 542e058a03353762d869a7df350d9217a0ddd10028e09a4ab02aedd528ca26f6
  * stale: false
  * tags: [code/undo_redo]
  * concepts: [Undo/Redo History List]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class HistoryList
//extends TODO
//implements TODO
{

////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////

/** Constant denoting the Default Size of Stacks  */
public static int DefaultSize;

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** Actual List where the Objects are stored.
	  * Redimensioned dynamically	 */
	protected Object[] stack;

	/**
	  * Maximum Position of the Stack	 */
	private int stackPtr;

	/**
	  * current Position of the Stack	 */
	private int currPtr;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor
	  * Setting the Size to the given	 */
	protected HistoryList(int size) { stack = new Object[size]; }

	/** Empty Constructor
	  * defaulting the Size to the given static Default	 */
	protected HistoryList() { this(DefaultSize); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** returns the Position in the History List
	  */
	public int getPosition() {
		return currPtr; }

	/** returns the Number of Items in the History List
	  */
	public int getSize() {
		return stackPtr; }

	/** Appends an Item at the next Position, growing the backing Array first if it is full,
	  * and truncates any forward History past the current Position. */
	//add an Object at the current position
	public void addItem(Object item) {
		stackPtr = ++currPtr; //limit the Stack to this last Position!
		if (currPtr >= stack.length) {
			Object[] tmp = new Object[stack.length + stack.length];
			System.arraycopy(stack, 0, tmp, 0, stack.length);
			stack = tmp;
		}
		stack[currPtr] = item; }

	/** Moves one Position forward in the History and returns the Item there.
	  * @return the next Item, or {@code null} when already at the most recent Position */
	//increases the Position and returns the next Item
	public Object nextItem() {
		if (stackPtr > currPtr) {
			return stack[++currPtr]; }
			return null; }

	/** Moves one Position back in the History and returns the Item there.
	  * @return the previous Item, or {@code null} when already at the first Position */
	//decreases the Position and returns the previous Item
	public Object prevItem() {
		if (currPtr > 0) {
			return stack[--currPtr]; }
			return null; }

	/** Returns the Item at the current Position without moving it.
	  * @return the current Item, or {@code null} when the Position is still negative */
	//keeps the Position and returns the current Item
	public Object currItem() {
		if (currPtr >= 0) {
			return stack[currPtr]; }
			return null; }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + HistoryList.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
