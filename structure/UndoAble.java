package structure; //

/**
  * Title: UndoAble<p>
  * Description:
  * Defines the Interface for undoable Operations
  * Implementors of this Interface must support an Undo Operation
  * and possibly store the previous State of the Operand to do this.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-20-2002, 09:16 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface UndoAble
extends Runnable {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Inverse Operation to the run() Method.
	  * Possibly store the previous State of the Operand to enable this 	 */
	void undo();

}
