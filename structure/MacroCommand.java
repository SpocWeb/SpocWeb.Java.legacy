package structure; //

/**
  * Title: MacroCommand.java<p>
  * Description:
  * The Command History can be undone / redone Step by Step.
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-20-2002, 09:01 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class MacroCommand
//extends Command
implements Runnable {

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** Reference to the HistoryList	 */
	protected HistoryList list;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected MacroCommand() {
		list = new HistoryList(); }

	/** Constructor taking the Number of Items to expect	 */
	protected MacroCommand(int numItems) {
		list = new HistoryList(numItems); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** returns the Position in the History List
	  */
	public int getPosition() {
		return list.getPosition(); }

	/** returns the Number of Items in the History List
	  */
	public int getSize() {
		return list.getSize(); }

	/**
	  * It is not clear whether the Commands added to this Macro
	  * are already performed or still have to be performed.
	  * If it has to be called, the Instance should be cloned before
	  * (Prototype Pattern)	 */
	public void addItem(Command item) {
		list.addItem(item); }

	/**
	  * Redoes the next Command	 */
	public void redo() {
		Command cmd;
		if (null == (cmd = (Command) list.nextItem())) {
			return; }
		cmd.run(); }

	/**
	  * 	 */
	public void undo() {
		UndoAble cmd;
		if (null == (cmd = (UndoAble) list.prevItem())) {
			return; }
		cmd.undo(); }

////////////////////////////////////////////////////////////////////////////////
//  Interface Runnable: Implementation
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Runs all Commands until the List is finished.	 */
	public void run() {
		Command cmd;
		while (true) {
			if (null == (cmd = (Command) list.nextItem())) {
				return; }
			cmd.run(); }
	}

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + MacroCommand.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
