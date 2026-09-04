package streamIO.exception;

import streamIO.IStreamOut;
import streamIO.copy.ICopyAble;
import streamIO.object.ModificationException;
import streamIO.object.enumer.ChangeIterator;
import streamIO.object.enumer.Enumerator;

/** This Exception Type is thrown when a modifying Operation is applied
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
  * Making this Exception a RuntimeException makes the Declaration obsolete.
  * It should be used to encapsulate caught
  * @see ModificationException thrown by Objects that mustn't be modified in structure.
  */
public class ReadOnlyException
extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Reference to the read only Object that caused the Exception */
	protected Object mThis;

	/** Empty Constructor, defaulting the Error Message!  */
	public ReadOnlyException() {}

	/** Constructor taking a Message   */
	public ReadOnlyException(String inMessage) { super(inMessage, null); }

	/** Constructor taking a Message Object  */
	public ReadOnlyException(Object ths) {
		mThis = ths; }

	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because Errors indicates a serious failure.
	 * The Message is defaulted to the original one.
	 * Declared 'protected' to enforce the Use of Message that fits the current Semantics.
	 */
	protected ReadOnlyException(Throwable inThrowable) {
		super(inThrowable.getMessage(), inThrowable); }

	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 */
	public ReadOnlyException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable); }

	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 */
	public ReadOnlyException(Object ths, String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
		mThis = ths; }

	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 */
	public ReadOnlyException(Object ths, Throwable inThrowable) {
		super(inThrowable);
		mThis = ths; }

}
