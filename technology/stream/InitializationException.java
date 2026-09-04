/*
 * File Name: InitializationException.java
 * Created on: 02.12.2003
 *
 */
package technology.stream;

import streamIO.exception.ChainedException;

/**
 * Title: InitializationException<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
 */
public class InitializationException extends ChainedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InitializationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inMessage
	 */
	public InitializationException(String inMessage) {
		super(inMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inThrowable
	 */
	public InitializationException(Throwable inThrowable) {
		super(inThrowable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inMessage
	 * @param inThrowable
	 */
	public InitializationException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
		// TODO Auto-generated constructor stub
	}

}
