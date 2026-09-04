package structure; //

/**
  * Title: HistoryList<p>
  * AKA: NavStack
  *
  * Description:
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

	/**
	  * 	 */
	//add an Object at the current position
	public void addItem(Object item) {
		stackPtr = ++currPtr; //limit the Stack to this last Position!
		if (currPtr > stack.length) {
			Object[] tmp = new Object[stack.length + stack.length];
			System.arraycopy(stack, 0, tmp, 0, stack.length);
			stack = tmp;
		}
		stack[currPtr] = item; }

	/**
	  * 	 */
	//increases the Position and returns the next Item
	public Object nextItem() {
		if (stackPtr > currPtr) {
			return stack[++currPtr]; }
			return null; }

	/**
	  * 	 */
	//decreases the Position and returns the previous Item
	public Object prevItem() {
		if (currPtr > 0) {
			return stack[--currPtr]; }
			return null; }

	/**
	  * 	 */
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
