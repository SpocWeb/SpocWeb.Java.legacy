package streamIO.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


/**
  * Keeps Track of an original wrapped Exception and its Origin, with Space to collect
  * local Variables and Parameters to aid Post Mortem Debugging.
  *
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
  * SubClasses: 
  * @see streamIO.exception.OperationNotSupported
  *
  * @stereotype exception
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:27:21Z
  * digest: 9770483bd989e8e4ec48f076f13965278f6bca37d2df308ddcfe9829fcb06e2f
  * stale: false
  * tags: [code/custom_exception, code/exception_wrapping]
  * concepts: [Error Handling]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public class BaseException
extends RuntimeException {
	
	/** Serialization version identifier. */
	private static final long serialVersionUID = 1L;

	/**
	 * By giving <code>BaseException</code> a reference to a Throwable object,
	 * exception chaining can be enforced easily.
	 */
	private Throwable previousThrowable = null;

	/**
	 * This Variable can contain local Variables by their Names
	 * to enable Post Mortem Debugging.
	 */
	private Map localVariables = new HashMap();

	/** Records a local Variable's Value under the given Name for Post Mortem Debugging. */
	public void addLocalVariable(final String name, final Object value) {
		localVariables.put(name, value); }
	
	/** Empty Constructor, made protected to enforce the use of Messages!  */
	protected BaseException() {}
	
	/** Constructor taking a Message   */
	public BaseException(final String inMessage) { this(inMessage, null); }
	
	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 */
	public BaseException(final Throwable inThrowable) {
		this(inThrowable.getMessage(), inThrowable); }

	/**
	 * Constructor taking a Throwable and not an Exception to be able to even catch Errors
	 * although this is not recommended, because it indicates a serious failure.
	 * The Message is defaulted to the original one.
	 */
	public BaseException(final String inMessage, final Throwable inThrowable) {
		super(inMessage);
		this.previousThrowable = inThrowable; }

	/** Returns the original Exception	 */
	public Throwable getBaseException() { return previousThrowable; }

	/** Prints the Stack Trace of this and the Parent Exception  */
	public void printStackTrace() {
		printStackTrace(System.err); }

	/** Prints the Stack Trace of this and the Parent Exception  */
	public void printStackTrace(final PrintStream printStream) {
		synchronized(printStream) { //synchronizing on the actual Stream...
			final PrintWriter pw = new PrintWriter(printStream);
			this.printStackTrace(pw);
			pw.close(); //important to flush! finalize() might come later or never!!!
		}
	}

	/** Prints the Stack Trace of this and the Parent Exception  */
	public void printStackTrace(final PrintWriter printWriter) {
		synchronized(printWriter) {
			super.printStackTrace(printWriter);
			if (this.previousThrowable != null) {
				printWriter.println(" wrapping the following inner Exception: ");
	//			printWriter.println(getClass().getName() + ": " + super.getMessage() + " wrapping: ");
				this.previousThrowable.printStackTrace(printWriter);
	//		} else { super.printStackTrace(printWriter);
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * tests whether the Stack Trace of the inner Exception
	 * is retained on a rethrow
	 */
	public static void testStackTraceIsRetainedOnRethrow() {
		System.out.println("Demonstrate that an Exception does not lose its Stack Trace on rethow!");
		try {
			throwException();
		} catch (RuntimeException y) {
	//		y.fillInStackTrace(); //fillInStackTrace() overwrites the previous Stack Trace with the current!
			throw y;
		}
	}
	
	/**
	 * tests whether the Stack Trace is filled
	 * on Creation
	 * or on throwing
	 * Result: already on Creation, but refilled later!
	 */
	public static void testStackTraceFilledOnCreation() {
		System.out.println("Demonstrate that the Stack Trace is automatically filled on Creation:");
		Exception x = new Exception("Message");
		System.out.println("After Creation:"); x.printStackTrace(); x.fillInStackTrace(); //happens automatically on Creation!
		System.out.println("After Filling :"); x.printStackTrace();
		System.out.println("should be no Difference (except for Line Numbers)!");
	}
	
	/**
	 * tests whether the Stack Trace Formatting is complete.
	 * Result: printStackTrace contains both
	 * the Message and
	 * the Stack
	 */
	public static void testStackTraceFormattingIsComplete() {
		try {
			throwException();
		} catch (RuntimeException y) {
			BaseException baseX = new BaseException("outer Message", y);
			baseX.printStackTrace();
	//		throw baseX;
		}
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + BaseException.class.getName());
		testStackTraceFilledOnCreation();
		testStackTraceFormattingIsComplete();
	}
	
	/** static test Method to throw a new Runtime Exception */
	protected static void throwException() throws RuntimeException {
		throw new RuntimeException("inner Message"); }
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }
	
}
