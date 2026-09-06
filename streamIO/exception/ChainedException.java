/*
 * ChainedException.java
 *
 * Created on 7. Oktober 2002, 16:00
 */

package streamIO.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * Unchecked Error Class to contain a nested Exception, used to tunnel fatal Errors
 * (that should never happen) through Method Declarations.
 *
 * Generische Wrapper Klasse f�r Exceptions
 *
 * Grund f�r die Einf�hrung des ExceptionChainers war:
 * Die Konfiguration oder das Deployment ist so korrupt,
 * dass das System nicht arbeiten kann.
 * Beispiele: Defekter Deploymentdescriptor oder Komponentenkonfiguration
 * NegativBeispiel: 
 * Datenbank nicht verf�gbar - dies ist nur eine Resource die zur Zeit nicht verf�gbar ist 
 * und sollte �ber entsprechend konkrete Exceptions behandelt werden. 
 Sofern m�glich sollten bestehende Standardexception hierf�r genutzt werden (SQLException).
 * --> Rote Lampe auf der Konsole mu� leuchten.
 *
 * The WebLogic 7 Server catches all Throwables and rolls back the Transaction,
 * so that Messages will be processed over and over again if they trigger an Error! 
 *
 * Purpose of this Class: Keeping Track of the original Exception and it's Origin.
 * In MS.NET this is done by the command throw; without Parameter issued in a catch Block!
 * It works exactly like @see InvocationTargetException, but unlike
 * @see InvocationTargetException it is a @see RuntimeException and needn't be declared,
 * so it can be used to catch and tunnel Exceptions in Interface Methods.
 * It is better though to declare a generic Exception in each Interface Method though,
 * when Exceptions are expected in regular Operation. (which should not happen too often!)
 * The BaseException class should be used as the base class for all Exceptions.
 * @see Throwable is the Base Class for both Exception and Error
 * @see Error is an abnormal Exception that should never occur, be handled or declared.
 * @see RuntimeException is a child of Exception that doesn't need to be declared.
 * You can use it to throw Exceptions without having to declare them at any Level.
 * It should only be used within Applications, not for a Framework,
 * because it hides Information!
 *
 * For signaling an optionally supported declare @see NoSuchMethodException
 * which can be wrapped with @see NoSuchMethodError
 * @see UnsupportedOperationException is a @see RuntimeException
 *
 * For signaling an invalid Parameter declare
 * @see java.security.InvalidAlgorithmParameterException
 * which can be wrapped with
 * @see java.security.InvalidParameterException
 * @see InvocationTargetException which is also a Wrapper around Exceptions,
 * but derived from a checked Exception and thus has to be declared!
 *
 * @see backtrace Object[] containing Line Numbers and Classes of the current Line
 * unfortunately in reverse Order:
 * backtrace[0] contains an int[] with the Line Numbers
 * backtrace[1] the class Object of the innermost Procedure etc...
 *
 * @stereotype exception
 * @author  DirkSlootz
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:27:54Z
 * digest: 188c5f35f22b891125d0dcd6d0d271ea516fffc5ec8794919a5e63d4cac07b4e
 * stale: false
 * tags: [code/custom_exception, code/exception_wrapping]
 * concepts: [Error Handling]
 * facets: {layer: infrastructure, status: broken, complexity: low}
 * -->
 */
public class ChainedException extends Exception { //

	/** Serialization version identifier. */
	private static final long serialVersionUID = 1L;
	/**
	 * By giving <code>BaseException</code> a reference to a Throwable object,
	 * exception chaining can be enforced easily.
	 */
	private Throwable innerThrowable = null;

	/** Empty Constructor, made protected to enforce the use of Messages!  */
	protected ChainedException() {
	}

	/** Constructor taking a Message  
	 * @param inMessage message
	 */
	public ChainedException(String inMessage) {
		this(inMessage, null);
	}

	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 * Declared 'protected' to enforce the Use of Message that fits the current Semantics.
	 */
	protected ChainedException(Throwable inThrowable) {
		this(inThrowable.getMessage(), inThrowable);
	}

	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 * Also logs a fatal Message together with the complete Stack Trace
	 * @param inMessage message
	 * @param inThrowable original exception
	 */
	public ChainedException(String inMessage, Throwable inThrowable) {
		super(inMessage);
		this.innerThrowable = inThrowable;
	}

	/** Returns the original Exception	
	 * @return original exception
	 * @see Throwable#getCause() introduced in 1.4 which does the same 
	 */
	public Throwable getInnerException() {
		return innerThrowable;
	}

	/** Prints the Stack Trace of this and the Parent Exception  */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/** Prints the Stack Trace of this and the Parent Exception
	 * @param inPrintStream stream for printing stacktrace
	 */
	public void printStackTrace(PrintStream inPrintStream) {
		final java.io.PrintWriter pw = new java.io.PrintWriter(inPrintStream);
		this.printStackTrace(pw);
		pw.close(); //important to flush! finalize() might come later or never!!!
	}

	/** Prints the Stack Trace of this and the Parent Exception  
	 * @param inPrintWriter Writer for printing stacktrace 
	 */
	public void printStackTrace(PrintWriter inPrintWriter) {
		synchronized (inPrintWriter) {
			super.printStackTrace(inPrintWriter);
			if (this.innerThrowable != null) {
				inPrintWriter.println(
					"wrapping the following inner Exception:");
				//print the Message of the Constructor			
				this.innerThrowable.printStackTrace(inPrintWriter);
			}
		}
	}

}
