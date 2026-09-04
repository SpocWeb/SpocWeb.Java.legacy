package tools; //

import streamIO.exception.BaseException;

/**
  * Title: IOError.java<p>
  * Description:
  * Designed to wrap an IOException so that it needn't be declared.
  * This makes the Interface cleaner, but the Contract less explicit.
  * It is used e.g. in StreamIn and StreamOut
  * because these are used for both Memory and I/O Structures.
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
  * mtime: 2008-12-31T20:47:05Z
  * digest: cdd8920622f80bd498f3d6b75356da24efc0ad3db860089b745d4d892fa01a68
  * stale: false
  * -->
  */
public class IOError
extends BaseException {

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

/**
	 * 
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
