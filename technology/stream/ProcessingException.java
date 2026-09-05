/*
 * File Name: ProcessingException.java
 * Created on: 02.12.2003
 *
 */
package technology.stream;

import streamIO.exception.ChainedException;

/**
 * Signals that an attributed stream's {@code process(...)} step could not process the given
 * data.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:12:09Z
 * digest: 266ca5ce053d190ac02c622f854223ee2a41ecc0f42528ea9018e90b11c5f35d
 * stale: false
 * tags: [code/stream_adapter]
 * concepts: [Custom Exception Type]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class ProcessingException extends ChainedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception with no message and no cause.
	 */
	public ProcessingException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates an exception with the given message.
	 * @param inMessage
	 */
	public ProcessingException(String inMessage) {
		super(inMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates an exception wrapping the given cause.
	 * @param inThrowable
	 */
	public ProcessingException(Throwable inThrowable) {
		super(inThrowable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates an exception with the given message, wrapping the given cause.
	 * @param inMessage
	 * @param inThrowable
	 */
	public ProcessingException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
		// TODO Auto-generated constructor stub
	}

}
