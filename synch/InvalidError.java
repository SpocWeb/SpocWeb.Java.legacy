package synch;

import streamIO.exception.BaseException;

/**
  * This Exception Type is a Wrapper to InvalidException
  *
  * Used in
  *
  * Design Decisions:
  */
public class InvalidError
extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for InvalidError.
	 */
	public InvalidError() {
		super();
	}

	/**
	 * Constructor for InvalidError.
	 * @param inMessage
	 */
	public InvalidError(String inMessage) {
		super(inMessage);
	}

	/**
	 * Constructor for InvalidError.
	 * @param inThrowable
	 */
	public InvalidError(Throwable inThrowable) {
		super(inThrowable);
	}

	/**
	 * Constructor for InvalidError.
	 * @param inMessage
	 * @param inThrowable
	 */
	public InvalidError(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
	}

	/** Initializing Constructor
	  * @param Msg The Message to be displayed
	  */
	public InvalidError(String Msg, InvalidException base) {
		super(Msg, base); }

	/** Initializing Constructor  */
//	public InvalidError() { super(); }

}
