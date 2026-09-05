package streamIO.exception;


/**
  * Exception thrown in Methods that may or may not be implemented.
  * 				 This is to reduce the Number of Interfaces by providing
  * 				 optional Functionality.
  * 				 The Use of Exceptions is very good here to point Developers
  * 				 to the Fact that this Operation is optional.
  * 				 It should not be used to implement regular Functionality
  * 				 because processing Exceptions is very expensive (>10*)! <p>
  * 				 Since this is a Runtime Exception, it needn't be declared
  * 				 and can be used to wrap Exceptions.
  * Copyright:    Copyright (c) Matthias Heuer<p>
  * Company:      personal<p>
  * @author 		 Matthias Heuer
  * @version 1.0
  * @stereotype exception
  * @see InvalidState
  *
  * For signaling an invalid Parameter declare
  * @see java.security.InvalidAlgorithmParameterException
  * which can be wrapped with
  * @see java.security.InvalidParameterException
  * @see java.security.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:28:14Z
  * digest: 584dc44d30f083f784c353ffba46b944a0d0f560d5515ef550f35058ddef44b8
  * stale: false
  * tags: [code/custom_exception]
  * concepts: [Error Handling]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public class OperationNotSupported
extends BaseException {

	/** Serialization version identifier. */
	private static final long serialVersionUID = 1L;

	/**Empty Constructor, no Error Message	 */
	public OperationNotSupported() { super(null, null); }
	
	/**Constructor, taking an Error Message	 */
    public OperationNotSupported(final String s) { super(s, null); }
    
	/**Constructor, taking a Class	 */
    public OperationNotSupported(final Class s) { super(s.toString(), null); }
    
	/**Constructor, taking an Object Instance	 */
    public OperationNotSupported(final Object s) { super(s.toString(), null); }
    
	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 * Declared 'protected' to enforce the Use of Message that fits the current Semantics.
	 */
	protected OperationNotSupported(Throwable inThrowable) { super(inThrowable); }

	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 */
	public OperationNotSupported(String inMessage, Throwable inThrowable) {
		super (inMessage, inThrowable); }

}
