package structure; //

/**
  * Declares the {@link #undo} Operation inverse to {@link Runnable#run()}, implemented by
  * anything that can reverse its own previously performed Effect.
  *
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:17:58Z
  * digest: f045299ac366c5ba9329db78b3a226eef680892e353d0ae215637eb15650981a
  * stale: false
  * tags: [code/undo_redo]
  * concepts: [Undoable Interface]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
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
