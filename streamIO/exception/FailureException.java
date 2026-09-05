package streamIO.exception;

/**
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:28:04Z
  * digest: 0e7dad8609df69d5479422467a6b6e776ffff62ca0a85975bb39fe066baa46bb
  * stale: false
  * tags: [code/custom_exception]
  * concepts: [Error Handling, Testing]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
final public class FailureException
extends BaseException { //RuntimeException {

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Serialization version identifier. */
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
