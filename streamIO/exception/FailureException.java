package streamIO.exception;

/**
  * Title: FailureException<p>
  * Description:
  * Unchecked Runtime Exception solely for the Purpose of the Assert Class
  * Thrown wenn an Assertion fails.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-08-2002, 12:39 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class FailureException
extends BaseException { //RuntimeException {

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Empty Constructor	 */
	public FailureException() { super(); }

	/** Constructor taking a Description	 */
	public FailureException(String message) { super('\n'+message); }

	/** Constructor taking a nested Exception	 */
	public FailureException(Throwable x) { super(x); }

	/** Constructor taking a Description and a nested Exception	 */
	public FailureException(String message, Throwable x) { super('\n'+message, x); }

}
