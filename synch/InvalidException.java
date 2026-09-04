package synch;

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
public class InvalidException
extends Exception {

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Source' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** holds The Source of the Change that was invalidated   */
	protected Object source;

	/** @return The Source of the Change that was invalidated  */
	public Object getSource() {
		return source; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Value' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/** holds The Value of the Change that was invalidated   */
	protected Object value;

	/** @return The Value of the Change that was invalidated  */
	public Object getValue() {
		return value; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Severity' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/** holds The Value of the Severity that was invalidated   */
	protected int severity;

	/** @return The Value of the Severity that was invalidated  */
	public int getSeverity() {
		return severity; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Value' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor
	  * @param ths The Object that caused the Exception on being modified, usually 'this'
	  */
	public InvalidException(Object Source_, Object Value_) {
		this(Source_, Value_, null, 0); }

	/** Initializing Constructor
	  * @param Source The Object that caused the Exception on being modified, usually 'this'
	  * @param Value  The Value  that caused the Exception
	  */
	public InvalidException(Object Source_, Object Value_, String Msg) {
		this(Source_, Value_, Msg, 0); }

	/** Initializing Constructor
	  * @param Source The Object that caused the Exception on being modified, usually 'this'
	  * @param Value  The Value  that caused the Exception
	  * @param Severity The Severity of the Exception
	  */
	public InvalidException(Object Source_, Object Value_, String Msg, int Severity_) {
		super(Msg);
		this.source   = Source_;
		this.severity = Severity_;
		this.value    = Value_ ; 
	}

}
