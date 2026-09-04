package streamIO.object;

import streamIO.IStreamOut;
import streamIO.copy.ICopyAble;
import streamIO.exception.ReadOnlyException;
import streamIO.object.enumer.ChangeIterator;
import streamIO.object.enumer.Enumerator;

/** This Exception Type is thrown when a structurally modifying Operation is applied
  * to a read only Object.
  * In order to prevent Class Proliferation by always providing two Types of Objects
  * a modifyable and a read only Type,
  * all Interfaces contain the modifying Operations
  * but throw this Exception when marked as read only.
  *
  * Two different Types of Modification exist:
  * -informational : only the Contents changes, the structure stays the same.
  * -structural    : both structure and Contents change.
  * The Interfaces
  * @see ChangeIterator and
  * @see Enumerator distinguish these Types of Change.
  *
  * Used in
  * @see IStreamOut
  * @see ICopyAble
  * 
  * Design Decisions:
  * Not making this Exception a RuntimeException makes the Declaration mandatory.
  * @see ReadOnlyException should be used to encapsulate this Exception
  */
public class ModificationException
extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Object that caused the Exception on being modified	*/
	protected Object mThis;

	/** Initializing Constructor
	  * @param ths The Object that caused the Exception on being modified, usually 'this'
	  */
	public ModificationException(Object ths) {
		super();
		mThis = ths; }

	/** Initializing Constructor
	  * @param ths The Object that caused the Exception on being modified, usually 'this'
	  */
	public ModificationException(String Msg, Object ths) {
		super(Msg);
		mThis = ths; }

	/** Initializing Constructor
	  * @param ths The Object that caused the Exception on being modified, usually 'this'
	  */
	public ModificationException(String Msg) {
		super(Msg); }

}
