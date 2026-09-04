package streamIO;

import java.io.PrintStream;
import java.util.Date;

import streamIO.exception.FailureException;
import synch.ValidationRule;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;

/**
  * Title: Assert<p>
  * Each Test Case should instantiate its own Assert Object,
  * to avoid Side Effects by global Criteria Changes
  * (Defaults for relative and absolute Deviation in float and double).
  *
  * Description:
  * Assertions allow concise Formulation of the Condition
  * and throw a Runtime Exception (FailureException), documenting the Exception!
  * Whenever a Runtime Check is introduced, these Methods should be used!
  * In Production Code, the Log Property should be set to null
  * and maxFailures should be set to 0.
  *
  * Additionally provides static Methods to Log Assertions and their failures to a streamIO.
  * Thus it can also be used in Testing Code to check the Correctness.
  * For this, set the Log Property and maxFailures to the Value
  * at which you want to stop testing.
  * This is usually either 0 (log and stop on first Error)
  * or a relatively high value like 1000 or so.
  *
  * Returns the Test Status and optionally throws a FailureException
  * to abort the Test prematurely.
  * Additionally provides a Testing Framework calling the static testIt() Method
  * as well as calling all static Methods starting with "test..."
  * All Throwables are caught and logged (possibly to different Streams).
  *
  * TODO: add Methods to maintain Statistics on the current Run
  * e.g. Threshold for stopping the Test.
  *
  * Design Decisions:
  * Resisted to add Methods of the Order Interface,
  * because their Semantics is unclear due to the prefix Notation:
  * Assert.less  (a , b) will rarely be used and should be replaced by
  * Assert.isTrue(a < b)
  *
  * Known SubClasses:
  *
  * Dependencies:
  * uses @see Log to perform the actual Logging!
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-07-2002, 12:37 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Assert {

	////////////////////////////////////////////////////////////////////////////////
	// Heart Beat and Logging Mechanism
	////////////////////////////////////////////////////////////////////////////////

	/** Logger for Testing, modify Threshold for switching Logging */
	public PrintStream logStream;

	/** runs the Heart Beat and prints Logging Messages 	*/
	private void runHeartBeat() {
		runHeartBeat(null); }
	
	/** runs the Heart Beat and prints Logging Messages 	*/
	private void runHeartBeat(final String message) {
		if (HeartBeat != null) // Adds another testing Tag to the Output Stream to indicate Progress
			HeartBeat.run(); //This may be necessary during long Tests to indicate Progress
		if (logStream != null){
			if (message == null) {
				logStream.println(); 
			} else {
				logStream.println(message); 
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The String to print with each Heartbeat */
	public static String STR_HEARTBEAT = ".";

	final static public Runnable DEFAULT_HEARTBEAT = new RunnablePrinter(System.out, STR_HEARTBEAT);

	/** Static Instance to Assert and log Failures to...	 */
	public static Assert A = new Assert(DEFAULT_HEARTBEAT);

	/** Message to indicate wrong Parameters */
	final static public String NON_NEGATIVE_MSG = " was expected to be non-negative!";
	
	////////////////////////////////////////////////////////////////////////////////
	//  #region : static Methods; The Rest of the static Methods all forward to the static Member A!
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data is entered.
	 *
	 * It is designed to be used with the System.in Console Input.
	 */
	public static String GET_AVAILABLE() { //throws java.io.IOException {
		return GET_AVAILABLE("Press Enter..."); }

	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data is entered.
	 *
	 * It is designed to be used with the System.in Console Input.
	 */
	public static String GET_AVAILABLE(final String prompt) { //throws java.io.IOException {
		System.out.print(prompt);
		return GET_AVAILABLE(System.in); }

	/**
	 * Helper Method for manual key Press
	 * Since a streamIO synchronously waits for input,
	 * this Method blocks until all Data in a Line is entered.
	 *
	 * It is designed to be used with the System.in Console Input on all Platforms.
	 * It also catches any Exceptions and returns null instead.
	 */
	public static String GET_AVAILABLE(java.io.InputStream in) { //throws java.io.IOException {
		char b;
		StringBuffer SB = new StringBuffer();
		try {
			do {
				b = (char) in.read();
				SB.append(b);
			} while(in.available() > 0);
			return SB.toString();
		} catch(java.io.IOException x) { }
		return null; }

	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables and Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/** Default Value for the Failure Message:	 */
	public String strFailure = ""; //"Assertion has failed!";

	/** Procedure to indicate Liveness of the Testing as a HeartBeat.
	 * the run() Method is called with EACH Comparison performed!
	 */
	public Runnable HeartBeat; // = null;

	/** The Output streamIO to log the Assertion to, when it fails.
	  * By setting this Property to null,
	  * any Failure results in a Runtime Exception of Type FailureException
	  * otherwise the Argument is written to the FailureHandler and no Exception is raised.
	  *
	  * Alternatively the Return Value also indicates the Status of the Assertion,
	  * so it can be used as Part of the Production Code!
	  */
	public IIStreamOut FailureHandler; // = null;

	/**
	 * switches raising Exceptions on or off by setting FailureHandler 
	 * @param raise Flag whether to raise Exceptions or ignore them. 
	 */
	public void throwException(boolean raise) {
		FailureHandler = raise ? null : Log.L; // StreamOut.DevNullOut;
	}
	
	/** Default Value for the relative Accuracy of double Comparisons:	 */
	protected double relDoubleDefault = ByRefDouble.DOUBLE_ACCURACY;

	/** Default Value for the relative Accuracy of double Comparisons:	 */
	protected double relFloatDefault = ByRefFloat.FLOAT_ACCURACY;

	/** @return Default Value for the relative Accuracy of double Comparisons:	 */
	public double getRelDoubleDefault() {
		return relDoubleDefault; }

	/** Sets Default Value for the relative Accuracy of double Comparisons:	 */
	public void setRelDoubleDefault(final double value) {
		if (value < 0) {
			throw new IllegalArgumentException(value + NON_NEGATIVE_MSG); }
		relDoubleDefault = value; }

	/** @return Default Value for the relative Accuracy of double Comparisons:	 */
	public double getRelFloatDefault() {
		return relFloatDefault; }

	/** Sets Default Value for the relative Accuracy of double Comparisons:	 */
	public void setRelFloatDefault(final double value) {
		if (value < 0) {
			throw new IllegalArgumentException(value + NON_NEGATIVE_MSG); }
		relFloatDefault = (float) value; }

	/** Default Value for the absolute Accuracy of double Comparisons:	 */
	protected double absDoubleDefault = ByRefDouble.DOUBLE_ACCURACY;

	/** Default Value for the absolute Accuracy of double Comparisons:	 */
	protected float absFloatDefault = ByRefFloat.FLOAT_ACCURACY;

	/** @return Default Value for the absolute Accuracy of double Comparisons:	 */
	public double getAbsFloatDefault() {
		return absFloatDefault; }

	/** Sets Default Value for the absolute Accuracy of double Comparisons:	 */
	public void setAbsFloatDefault(final double value) {
		if (value < 0) {
			throw new IllegalArgumentException(value + NON_NEGATIVE_MSG); }
		absFloatDefault = (float) value; }

	/** @return Default Value for the absolute Accuracy of double Comparisons:	 */
	public double getAbsDoubleDefault() {
		return absDoubleDefault; }

	/** Sets Default Value for the absolute Accuracy of double Comparisons:	 */
	public void setAbsDoubleDefault(final double value) {
		if (value < 0) {
			throw new IllegalArgumentException(value + NON_NEGATIVE_MSG); }
		absDoubleDefault = value; }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor, no Logging	 */
//	public Assert() { this(true); }

	/** Empty Constructor, no Logging	 */
//	public Assert(boolean logStack_) {
//		log.logStack = logStack_; }

	/** Default Constructor	 */
	public Assert() { }

	/** @param Heatbeat_ Callback for logging the Tests	 */
	public Assert(Runnable HeartBeat_) {
		this.HeartBeat = HeartBeat_; }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isEmpty(final Object obj) {
		return isEmpty(obj, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isEmpty(final Object obj, final String Message) {
		return treatComparison(ValidationRule.IS_EMPTY(obj), "should be empty ", "'"+obj+"'", Message, false); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean conformsToRegExp(final String string, final String regExp) {
		return conformsToRegExp(string, regExp, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean conformsToRegExp(final String string, final String regExp, final String Message) {
		return treatComparison(ValidationRule.CONFORMS_TO_REG_EXP(string, regExp), "should conform to the Regular Expression '"+regExp+"' ", "'"+string+"'", Message, false); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean contains(final String container, final String contained) {
		return contains(container, contained, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean contains(final String container, final String contained, final String Message) {
		return treatComparison(ValidationRule.CONTAINS(contained, container), "should contain '"+contained+"' ", "'"+container+"'", Message, false); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isMaxLength(final int maxLength, final String value) {
		return isMaxLength(maxLength, value, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isMaxLength(final int maxLength, final String strVal, final String Message) {
		return treatComparison(ValidationRule.IS_MAX_LENGTH(maxLength, strVal), "shorter than "+maxLength, " Value: "+strVal+" with Length "+strVal.length(), Message, false); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isMinLength(final int minLength, final String value) {
		return isMinLength(minLength, value, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isMinLength(final int minLength, final String strVal, final String Message) {
		return treatComparison(ValidationRule.IS_MIN_LENGTH(minLength, strVal), "larger than "+minLength, " Value: "+strVal, Message, false); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isMaxValue(final double maxVal, final double value) {
		return isMaxValue(maxVal, value, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isMaxValue(final double maxVal, final double dblVal, final String Message) {
		return treatComparison(ValidationRule.IS_MAX_VALUE(maxVal, dblVal), "larger than "+maxVal, " Value: "+dblVal, Message, false); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isMinValue(final double minVal, final double value) {
		return isMinValue(minVal, value, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isMinValue(final double minVal, final double dblVal, final String Message) {
		return treatComparison(ValidationRule.IS_MIN_VALUE(minVal, dblVal), "larger than "+minVal, " Value: "+dblVal, Message, false); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isModule(final double module, final double value) {
		return isModule(module, value, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isModule(final double module, final double dblVal, final String Message) {
		return treatComparison(ValidationRule.IS_MODULE(module, dblVal), "multiple of "+module, " Value: "+dblVal, Message, false); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isTrue(final boolean Condition) {
		return isTrue(Condition, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isTrue(final boolean Condition, final String Message) {
		return treatComparison(Condition, "true", "false", Message, false); }

	/** Asserts that both boolean Values are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean equals(
		final boolean expected,
		final boolean actual) {
		return equals(expected, actual, strFailure); }

	/** Asserts that both boolean Values are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean equals(
		final boolean expected,
		final boolean actual,
		final String Message) {
		return treatComparison(
			expected == actual,
			new Boolean(expected),
			new Boolean(actual), Message, false); }

	/** Asserts that both integer Numbers are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean equals(final long expected, final long actual) {
		return equals(expected, actual, strFailure); }

	/** Asserts that both integer Numbers are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean equals(
		final long expected,
		final long actual,
		final String Message) {
		return treatComparison(
			expected == actual,
			Long.toString(expected),
			Long.toString(actual), Message, false); }

	///////////////////////////////////////////////////////////////////////////////////
	/// Vector Methods
	///////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual,
		final int stop, 
		final double rel,
		final double abs) {
		return equals(expected, actual, 0, stop, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float[] actual,
		final int stop, 
		final double rel,
		final double abs) {
		return equals(expected, actual, 0, stop, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual,
		final int stop, 
		final double rel) {
		return equals(expected, actual, 0, stop, rel, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float [] actual,
		final int stop, 
		final double rel) {
		return equals(expected, actual, 0, stop, rel, absFloatDefault, strFailure); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual, 
		final int stop) {
		return equals(expected, actual, 0, stop, relDoubleDefault, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float [] actual, 
		final int stop) {
		return equals(expected, actual, 0, stop, relDoubleDefault, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual,
		final double rel,
		final double abs) {
		return equals(expected, actual, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float[] actual,
		final double rel,
		final double abs) {
		return equals(expected, actual, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual,
		final double rel) {
		return equals(expected, actual, rel, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float[] actual,
		final double rel) {
		return equals(expected, actual, rel, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual) {
		return equals(expected, actual, relDoubleDefault, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float[] actual) {
		return equals(expected, actual, relFloatDefault, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual,
		final int stop, 
		final double rel,
		final double abs, 
		final String strFailure) {
		return equals(expected, actual, 0, stop, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float[] actual,
		final int stop, 
		final double rel,
		final double abs, 
		final String strFailure) {
		return equals(expected, actual, 0, stop, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual,
		final int stop, 
		final double rel, 
		final String strFailure) {
		return equals(expected, actual, 0, stop, rel, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float [] actual,
		final int stop, 
		final double rel,
		final String strFailure) {
		return equals(expected, actual, 0, stop, rel, absFloatDefault, strFailure); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual, 
		final int stop, 
		final String strFailure) {
		return equals(expected, actual, 0, stop, relDoubleDefault, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float [] actual, 
		final int stop, 
		final String strFailure) {
		return equals(expected, actual, 0, stop, relDoubleDefault, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual,
		final double rel,
		final double abs, 
		final String strFailure) {
		return equals(expected, actual, 0, expected.length, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float[] actual,
		final double rel,
		final double abs, 
		final String strFailure) {
		return equals(expected, actual, 0, expected.length, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual,
		final double rel, 
		final String strFailure) {
		return equals(expected, actual, rel, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float[] actual,
		final double rel, 
		final String strFailure) {
		return equals(expected, actual, rel, absFloatDefault, strFailure); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final float[] actual, 
		final String message) {
		if (!equals(expected.length, actual.length, "Dimensions don't match! " + message)) {
			return false; }
		return equals(expected, actual, 0, expected.length, relFloatDefault, absFloatDefault, message); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[] expected,
		final double[] actual, 
		final String message) {
		if (!equals(expected.length, actual.length, "Dimensions don't match! " + message)) {
			return false; }
		return equals(expected, actual, 0, expected.length, relFloatDefault, absFloatDefault, message); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals
	( final double[] expected
	, final double[] actual
	, final int start
	, final int stop
	, final double rel
	, final double abs
	, final String Message) {
		runHeartBeat(); 
		//if (!equals(expected.length, actual.length, "Dimensions don't match! " + Message)) {
		//	return false; }
		for (int i = stop; --i >= start;) {
			if (!equals(expected[i], actual[i] , rel, abs, Message+"["+i+"]")) {
				return false; }
		} return true; }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals
	( final double[] expected
	, final float[] actual
	, final int start
	, final int stop
	, final double rel
	, final double abs
	, final String Message) {
		runHeartBeat(); 
		//if (!equals(expected.length, actual.length, "Dimensions don't match! " + Message)) {
		//	return false; }
		for (int i = stop; --i >= start;) {
			if (!equals(expected[i], actual[i] , rel, abs, Message+"["+i+"]")) {
				return false; }
		} return true; }

	///////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals
	( final Object[] expected
	, final Object[] actual
	, final int stop
	, final int start
	, final String message) {
		runHeartBeat(); 
		//if (!equals(expected.length, actual.length, "Dimensions don't match! " + Message)) {
		//	return false; }
		for (int i = stop; --i >= start;) {
			if (!equals(expected[i], actual[i] , message+"["+i+"]")) {
				return false; }
		} return true; }
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final Object[] expected
	, final Object[] actual) {
		return equals(expected, actual, expected.length);
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final Object[] expected
	, final Object[] actual 
	, final String message) {
		return equals(expected, actual, expected.length, 0, message);
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final Object[] expected
	, final Object[] actual
	, final int stop) {
		return equals(expected, actual, stop, 0);
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final Object[] expected
	, final Object[] actual
	, final int stop
	, final String message) {
		return equals(expected, actual, stop, 0, message);
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final Object[] expected
	, final Object[] actual
	, final int stop
	, final int start) {
		return equals(expected, actual, stop, start, strFailure); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final float[] expected,
		final float[] actual,
		final double rel,
		final double abs) {
		return equals(expected, actual, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final float[] expected,
		final float[] actual,
		final double rel) {
		return equals(expected, actual, rel, absFloatDefault, strFailure); }
	
	/** Asserts that both float Point Numbers are equal
	 * considering both / either a given absolute and / or relative Tolerance!
	 * The relative Tolerance adjusts the allowed Difference
	 * to the Size of both expected and actual Value.
	 * The absolute Tolerance guarantees Convergence even with Values near Zero
	 * by being a lower bound to the desired and tested Accuracy.
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param rel      the maximum allowed relative Deviation
	 * @param abs      the maximum allowed absolute Deviation
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals(final float[] expected, final float[] actual) {
		return equals(expected, actual, relFloatDefault, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	 * considering both / either a given absolute and / or relative Tolerance!
	 * The relative Tolerance adjusts the allowed Difference
	 * to the Size of both expected and actual Value.
	 * The absolute Tolerance guarantees Convergence even with Values near Zero
	 * by being a lower bound to the desired and tested Accuracy.
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param rel      the maximum allowed relative Deviation
	 * @param abs      the maximum allowed absolute Deviation
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final float[] expected
	, final float[] actual
	, final double rel
	, final double abs
	, final String Message) {
		if (!equals(expected.length, actual.length, "Vector Dimensions don't match! " + Message)) {
			return false; 
		}
		return equals(expected, actual, 0, expected.length, rel, abs, Message); 
	}
	
	/** Asserts that both float Point Numbers are equal
	 * considering both / either a given absolute and / or relative Tolerance!
	 * The relative Tolerance adjusts the allowed Difference
	 * to the Size of both expected and actual Value.
	 * The absolute Tolerance guarantees Convergence even with Values near Zero
	 * by being a lower bound to the desired and tested Accuracy.
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param rel      the maximum allowed relative Deviation
	 * @param abs      the maximum allowed absolute Deviation
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final float[] expected
	, final float[] actual
	, final int stop
	, final double rel
	, final double abs
	, final String Message) {
		return equals(expected, actual, 0, stop, relFloatDefault, absFloatDefault, Message); }

	/** Asserts that both float Point Numbers are equal
	 * considering both / either a given absolute and / or relative Tolerance!
	 * The relative Tolerance adjusts the allowed Difference
	 * to the Size of both expected and actual Value.
	 * The absolute Tolerance guarantees Convergence even with Values near Zero
	 * by being a lower bound to the desired and tested Accuracy.
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param rel      the maximum allowed relative Deviation
	 * @param abs      the maximum allowed absolute Deviation
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final float[] expected
	, final float[] actual
	, final int stop
	, final double rel
	, final double abs) {
		return equals(expected, actual, 0, stop, relFloatDefault, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	 * considering both / either a given absolute and / or relative Tolerance!
	 * The relative Tolerance adjusts the allowed Difference
	 * to the Size of both expected and actual Value.
	 * The absolute Tolerance guarantees Convergence even with Values near Zero
	 * by being a lower bound to the desired and tested Accuracy.
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param rel      the maximum allowed relative Deviation
	 * @param abs      the maximum allowed absolute Deviation
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final float[] expected
	, final float[] actual
	, final int start
	, final int stop
	, final double rel
	, final double abs) {
		return equals(expected, actual, start, stop, relFloatDefault, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	 * considering both / either a given absolute and / or relative Tolerance!
	 * The relative Tolerance adjusts the allowed Difference
	 * to the Size of both expected and actual Value.
	 * The absolute Tolerance guarantees Convergence even with Values near Zero
	 * by being a lower bound to the desired and tested Accuracy.
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param rel      the maximum allowed relative Deviation
	 * @param abs      the maximum allowed absolute Deviation
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final float[] expected
	, final float[] actual
	, final int start
	, final int stop
	, final double rel
	, final double abs
	, final String Message) {
		runHeartBeat(); 
		//if (!equals(expected.length, actual.length, "Dimensions don't match! " + Message)) {
		//	return false; }
		for (int i = stop; --i >= start;) {
			if (!equals(expected[i], actual[i] , rel, abs, Message+"["+i+"]")) {
				return false; }
		} return true; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final long[] expected
	, final long[] actual
	, final String Message) {
		if (!equals(expected.length, actual.length, "Vector Dimensions don't match! " + Message)) {
			return false; }
		return equals(expected, actual, 0, expected.length, Message);
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final long[] expected
	, final long[] actual) {
		return equals(expected, actual, strFailure);
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final long[] expected
	, final long[] actual
	, final int stop) {
		return equals(expected, actual, 0, stop, strFailure);
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final long[] expected
	, final long[] actual
	, final int stop
	, final String Message) {
		return equals(expected, actual, 0, stop, Message);
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final long[] expected
	, final long[] actual
	, final int start
	, final int stop) {
		return equals(expected, actual, start, stop, strFailure); 
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for
	 * @param length   the length of the Area tested 
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final long[] expected
	, final long[] actual
	, final int start
	, final int stop
	, final String Message) {
		runHeartBeat(); 
		//if (!equals(expected.length, actual.length, "Vector Dimensions don't match! " + Message)) {
		//	return false; }
		for (int i = stop; --i >= start; ) {
			if (!equals(expected[i], actual[i], Message+"["+i+"]")) {
				return false; }
		} return true; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for is defaulted to 0 
	 * @param length   the length of the Area tested is defaulted to expected.length
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals(
		final int[] expected,
		final int[] actual) {
		return equals(expected, actual, strFailure); 
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for is defaulted to 0 
	 * @param length   the length of the Area tested is defaulted to expected.length
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final int[] expected
	, final int[] actual
	, final String Message) {
		if (!equals(expected.length, actual.length, "Vector Dimensions don't match! " + Message)) {
			return false; }
		return equals(expected, actual, 0, expected.length, Message); 
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for is defaulted to 0 
	 * @param length   the length of the Area tested is defaulted to expected.length
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final int[] expected
	, final int[] actual
	, final int stop
	, final String Message) {
		return equals(expected, actual, 0, stop, Message); 
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for is defaulted to 0 
	 * @param length   the length of the Area tested is defaulted to expected.length
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final int[] expected
	, final int[] actual
	, final int stop) {
		return equals(expected, actual, 0, stop, strFailure); 
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for is defaulted to 0 
	 * @param length   the length of the Area tested is defaulted to expected.length
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final int[] expected
	, final int[] actual
	, final int start
	, final int stop) {
		return equals(expected, actual, start, stop, strFailure); 
	}
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first index to compare Values for is defaulted to 0 
	 * @param length   the length of the Area tested is defaulted to expected.length
	 * @param Message  the Message sent when the Test fails
	 * @return true when both Arrays are equal in the given Range
	 */
	public boolean equals
	( final int[] expected
	, final int[] actual
	, final int start
	, final int stop
	, final String Message) {
		runHeartBeat(); 
		for (int i = stop; --i >= start; ) {
			if (!equals(expected[i], actual[i], Message+"["+i+"]")) {
				return false; }
		} return true; }

	///////////////////////////////////////////////////////////////////////////////////
	/// Matrix Methods
	///////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Matrices are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[][] expected,
		final double[][] actual,
		final double rel,
		final double abs) {
		return equals(expected, actual, rel, abs, strFailure); }

	/** Asserts that both float Point Matrices are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[][] expected,
		final float[][] actual,
		final double rel,
		final double abs) {
		return equals(expected, actual, rel, abs, strFailure); }

	/** Asserts that both float Point Matrices are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[][] expected,
		final double[][] actual,
		final double rel) {
		return equals(expected, actual, rel, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Matrices are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[][] expected,
		final float[][] actual,
		final double rel) {
		return equals(expected, actual, rel, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[][] expected,
		final double[][] actual) {
		return equals(expected, actual, relDoubleDefault, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[][] expected,
		final float[][] actual) {
		return equals(expected, actual, relFloatDefault, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[][] expected,
		final double[][] actual,
		final double rel,
		final double abs,
		final String Message) {
		if (!equals(expected.length, actual.length, "Matrix Dimensions don't match! " + Message)) {
			return false; }
		for (int i = expected.length; --i >= 0; ) {
			if (!equals(expected[i], actual[i] , rel, abs, Message+"["+i+"]")) {
				return false; }
		} return true; }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double[][] expected,
		final float[][] actual,
		final double rel,
		final double abs,
		final String Message) {
		if (!equals(expected.length, actual.length, "Matrix Dimensions don't match! " + Message)) {
			return false; }
		for (int i = expected.length; --i >= 0; ) {
			if (!equals(expected[i], actual[i] , rel, abs, Message+"["+i+"]")) {
				return false; }
		} return true; }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final float[][] expected,
		final float[][] actual,
		final double rel,
		final double abs) {
		return equals(expected, actual, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final float[][] expected,
		final float[][] actual,
		final double rel) {
		return equals(expected, actual, rel, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final float[][] expected,
		final float[][] actual) {
		return equals(expected, actual, relFloatDefault, absFloatDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final float[][] expected,
		final float[][] actual,
		final double rel,
		final double abs,
		final String Message) {
		if (!equals(expected.length, actual.length, "Matrix Dimensions don't match! " + Message)) {
			return false; }
		for (int i = expected.length; --i >= 0; ) {
			if (!equals(expected[i], actual[i] , rel, abs, Message+"["+i+"]")) {
				return false; }
		} return true; }

	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	public boolean equals(
		final long[][] expected,
		final long[][] actual) {
		return equals(expected, actual, strFailure); }

	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final long[][] expected,
		final long[][] actual,
		final String Message) {
		if (!equals(expected.length, actual.length, "Dimensions don't match! " + Message)) {
			return false; }
		for (int i = expected.length; --i >= 0; ) {
			if (!equals(expected[i], actual[i], Message+"["+i+"]")) {
				return false; }
		} return true; }

	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	public boolean equals(final int[][] expected, final int[][] actual) {
		return equals(expected, actual, strFailure); }

	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final int[][] expected,
		final int[][] actual,
		final String Message) {
		if (!equals(expected.length, actual.length, "Dimensions don't match! " + Message)) {
			return false; }
		for (int i = expected.length; --i >= 0; ) {
			if (!equals(expected[i], actual[i], Message+"["+i+"]")) {
				return false; }
		} return true; }

	///////////////////////////////////////////////////////////////////////////////////
	/// Scalar Tests
	///////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double expected,
		final double actual,
		final double rel,
		final double abs,
		final String Message) {
//		if (Double.isInfinite(expected) { //TODO: consider the affine Model: -Infinity == +Infinity
//		} //use the Inverse of 'abs' to check the Size of 'actual'
		return treatComparison(
			ByRefDouble.EQUALS(expected, actual, rel, abs) ||
			((expected != expected) && (actual != actual)),
			Double.toString(expected),
			Double.toString(actual  ), Message, false); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(
		final double expected,
		final double actual,
		final double rel,
		final String Message) {
//		if (Double.isInfinite(expected) { //TODO: consider the affine Model: -Infinity == +Infinity
//		} //use the Inverse of 'abs' to check the Size of 'actual'
		return treatComparison(
			ByRefDouble.EQUALS(expected, actual, rel),
			Double.toString(expected),
			Double.toString(actual  ), Message, false); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean equals(
		final double expected,
		final double actual,
		final double rel,
		final double abs) {
		return equals(expected, actual, rel, abs, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  * @param rel      the maximum allowed relative Deviation
	  */
	public boolean equals(final double expected, final double actual, final double rel) {
		return equals(expected, actual, rel, absDoubleDefault, strFailure); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  * @param rel      the maximum allowed relative Deviation
	  */
	public boolean equals(final float expected, final float actual, final double rel) {
		return equals(expected, actual, rel, strFailure); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  */
	public boolean equals(final double expected, final double actual) {
		return equals(expected, actual, relDoubleDefault, absDoubleDefault, strFailure); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  */
	public boolean equals(final float expected, final float actual) {
		return equals(expected, actual, relFloatDefault, absFloatDefault, strFailure); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  */
	public boolean equals(final double expected, final float actual) {
		return equals((float)expected, actual); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  */
	public boolean equals(final float expected, final double actual) {
		return equals(expected, (float)actual); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  * @param Message  the Message sent when the Test fails
	  */
	public boolean equals(final float expected, final float actual, final String message) {
		return equals(expected, actual, relFloatDefault, absFloatDefault, message); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Value
	  * @param actual   the actual   Value
	  * @param message  the Message sent when the Test fails
	  */
	public boolean equals(final double expected, final double actual, final String message) {
		return equals(expected, actual, relDoubleDefault, absDoubleDefault, message); }

	/** Asserts that both Objects are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean equals(final Object expected, final Object actual) {
		return equals(expected, actual, strFailure); }

	/** Asserts that both Objects are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean equals(final Object expected, final Object actual, final String message) {
		return treatComparison(
			ValidationRule.EQUALS(expected, actual),
			expected, actual, message, false); }

	////////////////////////////////////////////////////////////////////////////////////
	/// Object specific Tests
	////////////////////////////////////////////////////////////////////////////////////

	/** Asserts that both Objects are identical!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean same(final Object expected, final Object actual) {
		return same(expected, actual, strFailure); }

	/** Asserts that both Objects are identical!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean same(final Object expected, final Object actual, final String message) {
		return treatComparison(
//			ValidationRule.IS_SAME(expected, actual),
			expected == actual,
			expected,   actual, message, true); }

	/** Asserts that the given Object is null!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isNull(final Object x) {
		return isNull(x, strFailure); }

	/** Asserts that the given Object is null!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isNull(final Object x, final String message) {
		return treatComparison(
			null == x,
			null,   x, message, true); }

	/** Asserts that the given Object is not a Number!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notANumber(final double x) {
		return notANumber(x, strFailure); }

	/** Asserts that the given Object is not a Number!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notANumber(final double x, final String message) {
		return treatComparison(
			Double.isNaN(x),
			Double.toString(Double.NaN),
			Double.toString(x), message, false); }

	/** Asserts that the given Object is a Number!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isANumber(final double x) {
		return isANumber(x, strFailure); }

	/** Asserts that the given Object is a Number!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isANumber(final double x, final String message) {
		return treatComparison(
			!Double.isNaN(x),
			Double.toString(Double.NaN),
			Double.toString(x), message, false); }

	/** Asserts that the given Object is Positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isPositive(final double x) {
		return isPositive(x, strFailure); }

	/** Asserts that the given Object is Positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isPositive(final double x, final String message) {
		return treatComparison(
			x > 0,
			" > 0",
			Double.toString(x), message, false); }

	/** Asserts that the given Object is Positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isNegative(final double x) {
		return isNegative(x, strFailure); }

	/** Asserts that the given Object is Positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isNegative(final double x, final String message) {
		return treatComparison(
			x < 0,
			" < 0",
			Double.toString(x), message, false); }

	/** Asserts that the given Object is not Negative!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notNegative(final double x) {
		return notNegative(x, strFailure); }

	/** Asserts that the given Object is not Negative!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notNegative(final double x, final String message) {
		return treatComparison(
			x >= 0,
			" >= 0",
			Double.toString(x), message, false); }

	/** Asserts that the given Object is not Positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notPositive(final double x) {
		return notPositive(x, strFailure); }

	/** Asserts that the given Object is not Positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notPositive(final double x, final String message) {
		return treatComparison(
			x <= 0,
			" <= 0",
			Double.toString(x), message, false); }

	/** Asserts that the given Object is not finite in Size!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notFinite(final double x) {
		return notFinite(x, strFailure); }

	/** Asserts that the given Object is not finite in Size!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notFinite(final double x, final String message) {
		return treatComparison(
			Double.isInfinite(x),
			Double.toString(Double.POSITIVE_INFINITY),
			Double.toString(x), message, false); }

	/** Asserts that the given Object is finite in Size!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isFinite(final double x) {
		return isFinite(x, strFailure); }

	/** Asserts that the given Object is finite in Size!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean isFinite(final double x, final String message) {
		return treatComparison(
			!Double.isInfinite(x),
			 Double.toString(Double.POSITIVE_INFINITY),
			 Double.toString(x), message, false); }

	/** Asserts that the given Object is NOT null!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notNull(final Object x) {
		return notNull(x, strFailure); }

	/** Asserts that the given Object is NOT null!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean notNull(final Object x, final String message) {
		return treatComparison(
			null != x, "not null", x, message, true); }

	////////////////////////////////////////////////////////////////////////////////////
	/// Failure Methods:
	////////////////////////////////////////////////////////////////////////////////////

	/** A Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  * This can be used to assert an Exception:
	  * Surround the expected Exception with a try { } catch() Block
	  * and call Fail() after the Statement throwing the Exception.
	  * Thus Fail() is only called when no Exception is thrown. 	 */
	public void fail() { fail(strFailure); }

	/**
	  * A Failure is logged
	  * @throws a Runtime Exception optionally to exit the calling Routine.
	  * This can be used to assert an Exception:
	  * Surround the expected Exception with a try { } catch() Block
	  * and call Fail() after the Statement throwing the Exception.
	  * Thus Fail() is only called and the FailureException thrown
	  * when no Exception is thrown. 	 */
	public void fail(final String message, final Throwable x)
		throws FailureException {
		final  FailureException wrapper = new FailureException(message, x); //wrap it into a RuntimeException to save Declarations
		if (FailureHandler == null) {
			throw wrapper; //already contains the current Stack Trace!
		}
		wrapper.fillInStackTrace(); //add the Stack Trace for Reference Purposes
		FailureHandler.addItem(wrapper);
	}

	/**
	  * A Failure is logged
	  * @throws a Runtime Exception optionally to exit the calling Routine.
	  * This can be used to assert an Exception:
	  * Surround the expected Exception with a try { } catch() Block
	  * and call Fail() after the Statement throwing the Exception.
	  * Thus Fail() is only called and the FailureException thrown
	  * when no Exception is thrown. 	 */
	public void fail(final String message) throws FailureException {
		fail (message, null); } //gives a Stack Trace automatically!

	/**
	  * A Failure is logged
	  * @throws a Runtime Exception optionally to exit the calling Routine.
	  * This can be used to assert an Exception:
	  * Surround the expected Exception with a try { } catch() Block
	  * and call Fail() after the Statement throwing the Exception.
	  * Thus Fail() is only called and the FailureException thrown
	  * when no Exception is thrown. 	 */
	public void fail(final Throwable x) throws FailureException {
		fail (null, x); } //gives a Stack Trace automatically!

	//////////////////////////////////////////////////////////////////////////////////
	/// private Methods:
	//////////////////////////////////////////////////////////////////////////////////

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  * Formats the Message for a Failure due to Equality
	  * @param equal    Result of the Comparison
	  * @param expected expected Value
	  * @param actual   actual Value
	  * @param message  Message to accompany failures
	  * @param same     Flag to indicate whether tested for Identity or Equality
	  */
	protected boolean treatComparison(final boolean equal, final Object expected, final Object actual, String message, final boolean same)
		throws FailureException {
		if  (message == null) 
			 message  = ""; 
		message+=" expected:{"+expected+"} actual:{"+actual+"}"+(same?" and should be identical":"");
		if (equal) {
			runHeartBeat(message);
		} else {
			fail(message);
		} return equal; 
		}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : The Rest of the static Methods all forwarding to the static Member A!
	////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_EMPTY(final String obj) {	return A.isEmpty(obj); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_EMPTY(final Object obj, String message) {	return A.isEmpty(obj, message); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean CONFORMS_TO_REG_EXP(final String string, final String regExp) {
		return A.conformsToRegExp(string, regExp); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean CONFORMS_TO_REG_EXP(final String string, final String regExp, String message) {
		return A.conformsToRegExp(string, regExp, message); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean CONTAINS(final String container, final String contained) {
		return A.contains(container, contained); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean CONTAINS(final String container, final String contained, String message) {
		return A.contains(container, contained, message); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MAX_LENGTH(final int maxLength, final String strVal) {
		return A.isMaxLength(maxLength, strVal); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MAX_LENGTH(final int maxLength, final String strVal, String message) {
		return A.isMaxLength(maxLength, strVal, message); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MIN_LENGTH(final int minLength, final String value) {
		return A.isMinLength(minLength, value, strFailure); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MIN_LENGTH(final int minLength, final String strVal, String message) {
		return A.isMinLength(minLength, strVal, message); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MAX_VALUE(final double maxVal, final double dblVal) {
		return A.isMaxValue(maxVal, dblVal); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MAX_VALUE(final double maxVal, final double dblVal, String message) {
		return A.isMaxValue(maxVal, dblVal, message); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MIN_VALUE(final double minVal, final double dblVal) {
		return isMinValue(minVal, dblVal); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MIN_VALUE(final double minVal, final double dblVal, String message) {
		return isMinValue(minVal, dblVal, message); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MODULE(final double module, final double value) {
		return A.isModule(module, value); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	public boolean IS_MODULE(final double module, final double dblVal, String message) {
		return A.isModule(module, dblVal, message); }

	/** A Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  * This can be used to assert an Exception:
	  * Surround the expected Exception with a try { } catch() Block
	  * and call Fail() after the Statement throwing the Exception.
	  * Thus Fail() is only called when no Exception is thrown. 	 */
	final static public void FAIL() { A.fail(); }

	/** A Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  * This can be used to assert an Exception:
	  * Surround the expected Exception with a try { } catch() Block
	  * and call Fail() after the Statement throwing the Exception.
	  * Thus Fail() is only called when no Exception is thrown. 	 */
	final static public void FAIL(final String message) { A.fail(message); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_TRUE(boolean Condition) { return A.isTrue(Condition); }

	/** Asserts that the Condition is true!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_TRUE(boolean Condition, final String message) {
		return A.isTrue(Condition, message); }

	/** Asserts that both Objects are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final Object expected, final Object actual) {
		return A.equals(expected, actual); }

	/** Asserts that both Objects are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final Object expected, final Object actual, final String message) {
		return A.equals(expected, actual, message); }
	
	/** Asserts that both boolean Values are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final boolean expected, final boolean actual) {
		return A.equals(expected, actual); }

	/** Asserts that both boolean Values are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final boolean expected, final boolean actual, final String message) {
		return A.equals(expected, actual, message); }

	/** Asserts that both integer Numbers are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final long expected, final long actual) {
		return A.equals(expected, actual); }

	/** Asserts that both integer Numbers are equal!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final long expected, final long actual, final String message) {
		return A.equals(expected, actual, message); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final double expected, final double actual, final double rel, final double abs) {
		return A.equals(expected, actual, rel, abs); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final double expected, final double actual, final double rel, final double abs, final String message) {
		return A.equals(expected, actual, rel, abs, message); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final double expected, final double actual, final double rel) {
		return A.equals(expected, actual, rel); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final float expected, final float actual, final double rel) {
		return A.equals(expected, actual, rel); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final double expected, final double actual, final double rel, final String message) {
		return A.equals(expected, actual, rel, message); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final double expected, final double actual) {
		return A.equals(expected, actual); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final float expected, final double actual) {
		return A.equals(expected, actual); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final double expected, final float actual) {
		return A.equals(expected, actual); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final float expected, final float actual) {
		return A.equals(expected, actual); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final float expected, final float actual, final String message) {
		return A.equals(expected, actual, message); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance is defaulted and adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance is defaulted and guarantees Convergence
	  * even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean EQUALS(final double expected, final double actual, final String message) {
		return A.equals(expected, actual, message); }

	////////////////////////////////////////////////////////////////////////////////////
	/// Tests on Vectors
	////////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  */
	final static public boolean EQUALS(final Object[] expected, final Object[] actual) {
		return A.equals(expected, actual); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final Object[] expected, final Object[] actual, final String message) {
		return A.equals(expected, actual, message); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final Object[] expected, final Object[] actual, final int stop, final String message) {
		return A.equals(expected, actual, stop, message); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final Object[] expected, final Object[] actual, final int stop) {
		return A.equals(expected, actual, stop); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final Object[] expected, final Object[] actual, final int start, final int stop) {
		return A.equals(expected, actual, start, stop); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final Object[] expected, final Object[] actual, final int start, final int stop, final String message) {
		return A.equals(expected, actual, start, stop, message); }
	
	////////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  */
	final static public boolean EQUALS(final double[] expected, final double[] actual, final double rel, final double abs) {
		return A.equals(expected, actual, rel, abs); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  */
	final static public boolean EQUALS(final double[] expected, final float[] actual, final double rel, final double abs) {
		return A.equals(expected, actual, rel, abs); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  */
	final static public boolean EQUALS(final double[] expected, final double[] actual, final double rel) {
		return A.equals(expected, actual, rel); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  */
	final static public boolean EQUALS(final double[] expected, final float[] actual, final double rel) {
		return A.equals(expected, actual, rel); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(final double[] expected, final double[] actual) {
		return A.equals(expected, actual); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(final double[] expected, final float[] actual) {
		return A.equals(expected, actual); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final double[] expected, final double[] actual, final double rel, final double abs, final String message) {
		return A.equals(expected, actual, rel, abs, message); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final double[] expected, final float[] actual, final double rel, final double abs, final String message) {
		return A.equals(expected, actual, rel, abs, message); }
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  */
	final static public boolean EQUALS(final float[] expected, final float[] actual, final double rel, final double abs) {
		return A.equals(expected, actual, rel, abs); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  */
	final static public boolean EQUALS(final float[] expected, final float[] actual, final double rel) {
		return A.equals(expected, actual, rel); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(final float[] expected, final float[] actual) {
		return A.equals(expected, actual); }
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final float[] expected, final float[] actual, final double rel, final double abs, final String message) {
		return A.equals(expected, actual, rel, abs, message); }
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(final long[] expected, final long[] actual) {
		return A.equals(expected, actual); }

	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final long[] expected, final long[] actual, final String message) {
		return A.equals(expected, actual, message); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final long[] expected, final long[] actual, final int stop, final String message) {
		return A.equals(expected, actual, stop, message); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final long[] expected, final long[] actual, final int stop) {
		return A.equals(expected, actual, stop); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final long[] expected, final long[] actual, final int start, final int stop) {
		return A.equals(expected, actual, start, stop); }
	
	/** Asserts that the Numbers in both Arrays are equal
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final long[] expected, final long[] actual, final int start, final int stop, final String message) {
		return A.equals(expected, actual, start, stop, message); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(final int[] expected, final int[] actual) {
		return A.equals(expected, actual); }

	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final int[] expected, final int[] actual, final String message) {
		return A.equals(expected, actual, message); }
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	 * @return
	 */
	final static public boolean EQUALS(final int[] expected, final int[] actual, final int stop, final String message) {
		return A.equals(expected, actual, stop, message); }
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	 * @return
	 */
	final static public boolean EQUALS(final int[] expected, final int[] actual, final int stop) {
		return A.equals(expected, actual, stop); }
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	 * @return
	 */
	final static public boolean EQUALS(final int[] expected, final int[] actual, final int start, final int stop) {
		return A.equals(expected, actual, start, stop); }
	
	/** Asserts that the Numbers in both Arrays are equal
	 * Otherwise a Failure is logged.
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.
	 *
	 * @param expected the expected Values
	 * @param actual   the actual   Values
	 * @param start    the first Index to test 
	 * @param length   the Number of consecutive Indices to test
	 * @param message  the Message sent when the Test fails
	 * @return
	 */
	final static public boolean EQUALS(final int[] expected, final int[] actual, final int start, final int stop, final String message) {
		return A.equals(expected, actual, start, stop, message); }
	
	////////////////////////////////////////////////////////////////////////////////////
	/// Tests on Matrices
	////////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  */
	final static public boolean EQUALS(double[][] expected, double[][] actual, double rel, double abs) {
		return A.equals(expected, actual, rel, abs); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  */
	final static public boolean EQUALS(final double[][] expected, final float[][] actual, final double rel, final double abs) {
		return A.equals(expected, actual, rel, abs); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  */
	final static public boolean EQUALS(final double[][] expected, final double[][] actual, final double rel) {
		return A.equals(expected, actual, rel); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  */
	final static public boolean EQUALS(final double[][] expected, final float[][] actual, final double rel) {
		return A.equals(expected, actual, rel); }

	/** Asserts that both float Point Number Arrays are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(final double[][] expected, final double[][] actual) {
		return A.equals(expected, actual); }

	/** Asserts that both float Point Number Arrays are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(final double[][] expected, final float[][] actual) {
		return A.equals(expected, actual); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final double[][] expected, final double[][] actual, final double rel, final double abs, final String message) {
		return A.equals(expected, actual, rel, abs, message); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final double[][] expected, final float[][] actual, final double rel, final double abs, final String message) {
		return A.equals(expected, actual, rel, abs, message); }

	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  */
	final static public boolean EQUALS(final float[][] expected, final float[][] actual, final double rel, final double abs) {
		return A.equals(expected, actual, rel, abs); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  */
	final static public boolean EQUALS(float[][] expected, float[][] actual, double rel) {
		return A.equals(expected, actual, rel); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(float[][] expected, float[][] actual) {
		return A.equals(expected, actual); }

	/** Asserts that both float Point Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param rel      the maximum allowed relative Deviation
	  * @param abs      the maximum allowed absolute Deviation
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(float[][] expected, float[][] actual, double rel, double abs, final String message) {
		return A.equals(expected, actual, rel, abs, message); }

	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(long[][] expected, long[][] actual) {
		return A.equals(expected, actual); }

	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(long[][] expected, long[][] actual, final String message) {
		return A.equals(expected, actual, message); }

	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  */
	final static public boolean EQUALS(final int[][] expected, final int[][] actual) {
		return A.equals(expected, actual); }

	/** Asserts that both Numbers are equal
	  * considering both / either a given absolute and / or relative Tolerance!
	  * The relative Tolerance adjusts the allowed Difference
	  * to the Size of both expected and actual Value.
	  * The absolute Tolerance guarantees Convergence even with Values near Zero
	  * by being a lower bound to the desired and tested Accuracy.
	  * Otherwise a Failure is logged.
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.
	  *
	  * @param expected the expected Values
	  * @param actual   the actual   Values
	  * @param message  the Message sent when the Test fails
	  */
	final static public boolean EQUALS(final int[][] expected, final int[][] actual, final String message) {
		return A.equals(expected, actual, message); }

	////////////////////////////////////////////////////////////////////////////////////
	/// Object specific Tests
	////////////////////////////////////////////////////////////////////////////////////

	/** Asserts that both Objects are identical!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean SAME(final Object expected, final Object actual) {
		return A.same(expected, actual); }

	/** Asserts that both Objects are identical!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean SAME(final Object expected, final Object actual, final String message) {
		return A.same(expected, actual, message); }

	/** Asserts that the given Object is null!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_NULL(final Object x) { return A.isNull(x); }

	/** Asserts that the given Object is null!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_NULL(final Object x, final String message) {
		return A.isNull(x, message); }

	/** Asserts that the given Object is NOT null!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_NULL(final Object x) { return A.notNull(x); }

	/** Asserts that the given Object is NOT null!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_NULL(final Object x, final String message) {
		return A.notNull(x, message); }

	/** Asserts that the given Number is not a Number (NaN) 
	 * which happens by multiplying Infinity with 0 or dividing 0 by 0!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_A_NUMBER(final double x) {
		return A.notANumber(x); }

	/** Asserts that the given Number is not a Number (NaN) 
	 * which happens by multiplying Infinity with 0 or dividing 0 by 0!
	 * Otherwise a Failure is logged
	 * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_A_NUMBER(final double x, final String message) {
		return A.notANumber(x, message); }

	/** Asserts that the given Number is a real Number and not NaN!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_A_NUMBER(final double x) {
		return A.isANumber(x); }

	/** Asserts that the given Number is a real Number and not NaN!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_A_NUMBER(final double x, final String message) {
		return A.isANumber(x, message); }

	/** Asserts that the given Number is infinite!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_FINITE(final double x) {
		return A.notFinite(x); }

	/** Asserts that the given Number is infinite!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_FINITE(final double x, final String message) {
		return A.notFinite(x, message); }

	/** Asserts that the given Number is not infinite!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_FINITE(final double x) {
		return A.isFinite(x); }

	/** Asserts that the given Number is not infinite!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_FINITE(final double x, final String message) {
		return A.isFinite(x, message); }

	/** Asserts that the given Number is not positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_POSITIVE(final double x) {
		return A.notPositive(x); }

	/** Asserts that the given Number is not positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_POSITIVE(final double x, final String message) {
		return A.notPositive(x, message); }

	/** Asserts that the given Number is not negative!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_NEGATIVE(final double x) {
		return A.notNegative(x); }

	/** Asserts that the given Number is not negative!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean NOT_NEGATIVE(final double x, final String message) {
		return A.notNegative(x, message); }

	/** Asserts that the given Number is negative!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_POSITIVE(final double x) {
		return A.isPositive(x); }

	/** Asserts that the given Number is positive!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_POSITIVE(final double x, final String message) {
		return A.isPositive(x, message); }

	/** Asserts that the given Number is negative!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_NEGATIVE(final double x) {
		return A.isNegative(x); }

	/** Asserts that the given Number is negative!
	  * Otherwise a Failure is logged
	  * and optionally a Runtime Exception is thrown to exit the calling Routine.	 */
	final static public boolean IS_NEGATIVE(final double x, final String message) {
		return A.isNegative(x, message); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Compares the Speed of for() and while() Loops */
	public static void testCompareSpeed() {
		Log.N("" + new Date().getTime());
		int m = 0;
		int n = 1000000000;
		while (--n >= 0) {
//			m += n;
		}
		Log.N(m + " : " + n + " : " + new Date().getTime());
		m = 0;
		for (int l = 1000000000; l >= 0; --l) {
//			m += l;
		}
		Log.N(m + " : " + n + " : " + new Date().getTime());
		m = 0;
		n = 1000000000;
		while (--n >= 0) {
//			m += n;
		}
		Log.N(m + " : " + n + " : " + new Date().getTime());
	}

	/**
	 * the new 'assert' primitive is only supported from Java 1.4 on...
	 * but it provides a very convenient Feature for all orthogonal Functions:
	 * * Logging
	 * * Assertions (pre- and postconditions)
	 * * Profiling (part of Logging)
	 * Features:
	 * * no if() Clause to speed up by shortcutting Evaluation
	 * * no Evaluation of Properties or Functions
	 * * no Performance Cost when switched off (due to JIT Compilation)
	 * * convenient switchting on/off for individual Classes and whole Packages 
	 *   via System Properties 
	 * Drawbacks:
	 * * cannot be switched on/off at Runtime?!?
	 *
	 * useful Helper Classes create deep Copies of inner or Argument Data
	 * and as a PostCondition check whether the Data was never modified!
	 */
	public static void testAssert() {
		final String str1 = "Hallo";
		final String str2 = new String(str1);
		//assert Assert.EQUALS(str1, str2):str2; //no complicated if() Clause necessary...
		//assert Log.L(str1) != null; // the != null Idiom seems to be necessary!
		//assert false : "should never execute!"; //this is also a common Idiom with 'false'
		throw new AssertionError("should never execute!"); //is a better Alternative, ...
		//...because it compiles also with previous Versions
		//...and it poses a Standard Return Path to the Compiler.
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws java.io.IOException {
		final String str1 = "Hallo";
		final String strFailure = "OK!";
		final String strSuccess = ":"; //Expected Success!";
//		final String strExpected = "Expecting Failure Message:";
		Log.N("Testing " + Assert.class.getName());
		Log.N("Testing Return Values on Success");
///		Log.L("\n\nExpecting Success Messages:");

		if (Assert.NOT_FINITE(Double.POSITIVE_INFINITY)) Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.NOT_FINITE(Double.NEGATIVE_INFINITY)) Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.NOT_A_NUMBER  (Double.NaN))     Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.EQUALS  (A, A))           Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.EQUALS  (null,
		           (Object) null))           Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.EQUALS  (str1, str1))     Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.EQUALS  (7, 7))           Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.EQUALS  (7  , 7.000001))  Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.EQUALS  (7.1, 7.099999))  Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.EQUALS  (true , true ))   Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.EQUALS  (false, false))   Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert. IS_NULL(null))           Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.NOT_NULL(str1))           Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.IS_TRUE (true))           Log.L(strSuccess); else Log.N(strFailure); //
		if (Assert.SAME    (str1, str1))     Log.L(strSuccess); else Log.N(strFailure); //
		Log.N("Testing Exceptions being thrown:");
///		Assert.A.strFailure = "";
///		Assert.A.log = null; //prevent Logging!
//		Assert.A.setMaxFailures(0); //Demonstration of fail() Usage!

		try { Assert.NOT_FINITE(3);            Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.NOT_A_NUMBER     (3);            Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (str1, null);      Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (str1, null);      Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (null, str1);      Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (str1, "Du");      Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (false, true);     Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (true, false);     Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (7, 8);            Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (7,   7.00001);    Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.EQUALS  (7.1, 7.09999);    Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert. IS_NULL(str1);            Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.NOT_NULL(null);            Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert. IS_TRUE(false);           Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.SAME    (null, str1);      Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.SAME    (str1, null);      Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		try { Assert.SAME    (str1,
		                     new String(str1));Log.N(strFailure); } catch (FailureException x) { Log.L(strSuccess); }
		Log.N("Testing Return Value on Failure:");
		Assert.A.FailureHandler = DevNullOut.DevNullOut; //don't throw Exceptions!
		if (Assert.NOT_FINITE(3))            Log.L(strFailure); else Log.N(strSuccess); //
		if (Assert.NOT_A_NUMBER     (3))            Log.L(strFailure); else Log.N(strSuccess); //
		if (Assert.EQUALS  (str1 , null))     Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.EQUALS  (str1 , null))     Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.EQUALS  (null , str1))     Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.EQUALS  (str1 , "Du"))     Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.EQUALS  (false, true))     Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.EQUALS  (true , false))    Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.EQUALS  (7    , 8))        Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.EQUALS  (7    , 7.00001))  Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.EQUALS  (7.1  , 7.09999))  Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert. IS_NULL(str1))            Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.NOT_NULL(null))            Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert. IS_TRUE(false))           Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.SAME    (null , str1))     Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.SAME    (str1 , null))     Log.N(strFailure); else Log.L(strSuccess); //
		if (Assert.SAME    (str1 ,
		                   new String(str1)))Log.N(strFailure); else Log.L(strSuccess); //
		Assert.A.FailureHandler = null;
		try {
			int i = 0, j = 0;
			i/=j;
			Assert.FAIL();
		} catch (Exception x) {
			Assert.IS_TRUE(x instanceof ArithmeticException);
///			Log.L(strSuccess); //"Expected Exception! " + x);
		}
///		Log.L.L();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
//		Assert.EQUALS(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		testIt(args); }


}
