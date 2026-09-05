package synch;

import streamIO.exception.BaseException;

/**
  * This Exception Type is a Wrapper to InvalidException
  *
  * Used in
  *
  * Design Decisions:
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: 678715e69a95b83dea200923d97d55f23f71cee4620cf64dcf0f4d7e3b3f0492
  * stale: false
  * tags: [code/validation]
  * concepts: [Custom Exception Type]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
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
