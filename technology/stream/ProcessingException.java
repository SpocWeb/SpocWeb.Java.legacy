/*
 * File Name: ProcessingException.java
 * Created on: 02.12.2003
 *
 */
package technology.stream;

import streamIO.exception.ChainedException;

/**
 * Title: ProcessingException<p>
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
public class ProcessingException extends ChainedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ProcessingException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inMessage
	 */
	public ProcessingException(String inMessage) {
		super(inMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inThrowable
	 */
	public ProcessingException(Throwable inThrowable) {
		super(inThrowable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param inMessage
	 * @param inThrowable
	 */
	public ProcessingException(String inMessage, Throwable inThrowable) {
		super(inMessage, inThrowable);
		// TODO Auto-generated constructor stub
	}

}
