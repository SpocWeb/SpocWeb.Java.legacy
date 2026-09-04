package tools; //

import streamIO.exception.BaseException;

/**
  * Unchecked Wrapper around an IOException, so I/O Failures need not be declared.
  *
  * <p>Designed to wrap an IOException so that it needn't be declared.
  * This makes the Interface cleaner, but the Contract less explicit.
  * It is used e.g. in StreamIn and StreamOut
  * because these are used for both Memory and I/O Structures.
  *
  * <h2>Collaborators</h2>
  *
  * <table>
  * <caption>Types this Class works with</caption>
  * <tr><th>Type</th><th>Relationship</th></tr>
  * <tr><td>{@link streamIO.exception.BaseException}</td>
  *     <td>Base Class supplying the Message and Cause Chaining this Class only forwards to.</td></tr>
  * <tr><td>{@link java.io.IOException}</td>
  *     <td>The checked Exception this Class exists to carry without declaring it.</td></tr>
  * </table>
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-03, 05;34;45<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-04T16:35:47Z
  * digest: f667d180315172ddfb147afacef7da21d36565d20b00d3b62f0546e48dd041d9
  * stale: false
  * tags: [code/exception_wrapping, code/unchecked_exception]
  * concepts: [Error Handling]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public class IOError
extends BaseException {

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

/**
	 * Serialization Version of this Exception Class, never varied since its Introduction.
	 */
	private static final long serialVersionUID = 1L;

/** Empty Constructor, made protected to enforce the use of Messages!  */
protected IOError() { }

/** Constructor taking a Message   */
public IOError(String inMessage) { super(inMessage, null); }

/**
 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
 * although this is not recommended, because it indicates a serious failure.
 * The Message is defaulted to the original one.
 * Declared 'protected' to enforce the Use of Message that fits the current Semantics.
 */
public IOError(Throwable inThrowable) { super(inThrowable.getMessage(), inThrowable); }

/**
 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
 * although this is not recommended, because it indicates a serious failure.
 * The Message is defaulted to the original one.
 */
public IOError(String inMessage, Throwable inThrowable) { super(inMessage, inThrowable); }

}
