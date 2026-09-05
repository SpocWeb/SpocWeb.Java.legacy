package streamIO.testing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import streamIO.IIStreamOut;
import streamIO.exception.BaseException;
import streamIO.exception.FailureException;

/**
  * Abstract Test Case, defaults the setUp() and tearDown() Methods to empty Methods,
  * and drives Tests either directly, or reflectively over every public no-argument
  * {@code test...()} Method of a Class or Object.
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
  * Created on	10-19-2002, 05:32 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:19:35Z
  * digest: e5b7732d0250b9556bba1a79d4509a41d72c3a11e269372a2e5c260cee9611ad
  * stale: false
  * tags: [code/test_harness, code/reflection_based_dispatch]
  * concepts: [Testing, Reflection]
  * facets: {layer: test, status: broken, complexity: medium}
  * -->
  */
public abstract class ATestCase
implements ITestCase {

////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////

	/** Prefix to be used in the test Method */
	public static String Test_Prefix = "test";

////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////

	/**
	  * Test Harness
	  * @param r Runnable Procedure to be called for Testing.
	  */
	public static boolean test(Runnable r) {
		return test(r, null, null, null); }

	/**
	  * Test Harness
	  * @param r Runnable Procedure to be called for Testing.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  */
	public static boolean test(Runnable r, IIStreamOut FailureHandler) {
		return test(r, FailureHandler, null, null); }

	/**
	  * Test Harness
	  * @param r Runnable Procedure to be called for Testing.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  */
	public static boolean test(Runnable r, IIStreamOut FailureHandler, IIStreamOut ErrorHandler) {
		return test(r, FailureHandler, ErrorHandler, null); }

	/**
	  * Test Harness
	  * @param r Runnable Procedure to be called for Testing.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  * @param TestLogger     Log of all Test Cases
	  */
	public static boolean test(Runnable r, IIStreamOut FailureHandler, IIStreamOut ErrorHandler, IIStreamOut TestLogger) {
		if (null != TestLogger) {
			TestLogger.addItem(r); }
		try { r.run();
		} catch (FailureException x) { return handleException(x, FailureHandler); //normal Failure
		} catch (Throwable        x) { return handleException(x,   ErrorHandler); //Runtime Error
		} return true; }

	/**
	  * Test Harness; being used by a Reflection Framework
	  * calling all Methods starting with "test...()"
	  * @param obj Object to be used for Testing.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  */
	public static boolean test(ITestCase obj) {
		return test(obj, null, null, null); }

	/**
	  * Test Harness; being used by a Reflection Framework
	  * calling all Methods starting with "test...()"
	  * @param obj Object to be used for Testing.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  */
	public static boolean test(ITestCase obj, IIStreamOut FailureHandler) {
		return test(obj, FailureHandler, null, null); }

	/**
	  * Test Harness; being used by a Reflection Framework
	  * calling all Methods starting with "test...()"
	  * @param obj Object to be used for Testing.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  */
	public static boolean test(ITestCase obj, IIStreamOut FailureHandler, IIStreamOut ErrorHandler) {
		return test(obj, FailureHandler, ErrorHandler, null); }

	/**
	  * Test Harness; being used by a Reflection Framework
	  * calling all Methods starting with "test...()"
	  * @param obj Object to be used for Testing.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  * @param TestLogger     Log of all Test Cases
	  */
	public static boolean test(ITestCase obj, IIStreamOut FailureHandler, IIStreamOut ErrorHandler, IIStreamOut TestLogger) {
		if (null != TestLogger) {
			TestLogger.addItem(obj); }
		try { obj.runTest(FailureHandler, ErrorHandler);
		} catch (FailureException x) { return handleException(x, FailureHandler); //normal Failure
		} catch (Throwable        x) { return handleException(x,   ErrorHandler); //Runtime Error
		} return true; }

	/**
	  * Test Harness; being used by a Reflection Framework
	  * calling all Methods starting with "test...()"
	  * @param cls Class whose Instances are to be used for Testing. Must have a public empty Constructor.
	  */
	public static boolean test(Class cls)
		throws InstantiationException, IllegalAccessException {
		return test(cls, null, null, null); }

	/**
	  * Test Harness; being used by a Reflection Framework
	  * calling all Methods starting with "test...()"
	  * @param cls Class whose Instances are to be used for Testing. Must have a public empty Constructor.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  */
	public static boolean test(Class cls, //Object obj,
		IIStreamOut FailureHandler)
		throws InstantiationException, IllegalAccessException {
		return test(cls, FailureHandler, null, null); }


	/**
	  * Test Harness; being used by a Reflection Framework
	  * calling all Methods starting with "test...()"
	  * @param cls Class whose Instances are to be used for Testing. Must have a public empty Constructor.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  */
	public static boolean test(Class cls, //Object obj,
		IIStreamOut FailureHandler,
		IIStreamOut ErrorHandler)
		throws InstantiationException, IllegalAccessException {
		return test(cls, FailureHandler, ErrorHandler, null); }

	/**
	  * Test Harness; being used by a Reflection Framework
	  * calling all Methods starting with "test...()"
	  * @param obj Object to be used for Testing.
	  * @param cls Class whose Instances are to be used for Testing. Must have a public empty Constructor.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  * @param TestLogger     Log of all Test Cases
	  */
	public static boolean test(Class cls, //Object obj,
		IIStreamOut FailureHandler,
		IIStreamOut ErrorHandler,
		IIStreamOut TestLogger)
		throws InstantiationException, IllegalAccessException {
//		Class cls = obj.getClass();
		Method[] methods = cls.getMethods(); //get all public Methods
		for(int i = methods.length; --i >= 0; ) {
			Method method = methods[i];
			if  (method.getName().startsWith(Test_Prefix) && //starts with 'test...()'
				(!Modifier.isStatic(method.getModifiers())) && //not static
				(method.getParameterTypes().length == 0)) { //no Parameters
				Object obj = cls.newInstance();
				if (obj instanceof ITestCase) {
					((ITestCase) obj).setUp(); }
				boolean success = test(obj, method, FailureHandler, ErrorHandler, TestLogger);
				if (obj instanceof ITestCase) {
					((ITestCase) obj).tearDown(); }
				if (!success) { //to avoid Side Effects!
					return false; } } }
		return true; }

	/**
	  * used internally by test() to call a single Method starting with "test...()"
	  * @param obj Object to be used for Testing.
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  * @param TestLogger     Log of all Test Cases
	  */
	private static boolean test(Object obj, Method method,
		IIStreamOut FailureHandler,
		IIStreamOut ErrorHandler,
		IIStreamOut TestLogger) {
		if (null != TestLogger) {
			TestLogger.addItem(method); }
		try { method.invoke(obj, null); //no Parameters
		} catch (   IllegalAccessException x) { throw new BaseException(x); //should never happen!
		} catch (InvocationTargetException x) {
			Throwable inner = x.getTargetException();
			// TODO: LOGIC: both branches log/rethrow the wrapping InvocationTargetException
			// `x` instead of `inner`, the exception the test Method actually threw. The
			// reported stack trace and any FailureHandler/ErrorHandler item is therefore
			// always the reflection wrapper, not the real cause.
			if (inner instanceof FailureException) {
				return handleException(x, FailureHandler); } //normal Failure
				return handleException(x,   ErrorHandler); //Runtime Error
		} return true; }

	/** Handles the Throwable using the given Handler */
	private static boolean handleException(Throwable x, IIStreamOut ErrorHandler) {
		if (false) { //just for Formatting
		} else if (null == ErrorHandler) { x.printStackTrace(System.err); return false; //log the Error to the Default Error Stream => interleaved and unreadable! => stop
		} else if (null == ErrorHandler.addItem(x)) {                     return false; }
		return true; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface ITestCase: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Method to run a Test
	  * Instead of using a dedicated Error Handler that takes a Throwable
	  * a normal Output streamIO is used.
	  * Each Test Case instantiates its own Assert Object,
	  * to avoid Side Effects by global Criteria Changes
	  * (Defaults for relative and absolute Deviation in float and double).
	  * @return true when further Tests make sense.
	  */
	public boolean runTest(IIStreamOut FailureHandler, IIStreamOut ErrorHandler) {
		try {
			return test(getClass(), FailureHandler, ErrorHandler, null);
		} catch (InstantiationException x) {
			x.printStackTrace(System.err); //should never happen!
		} catch (IllegalAccessException x) {
			x.printStackTrace(System.err); //should never happen!
		}
		return false; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface ITestCase: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Sets up the Test Fixture,
	  * i.e. all preparing Statements for the Set of all test...() Methods
	  */
	public void setUp() { }

	/** Tears down the Test Fixture,
	  * i.e. frees all Resources used by the Methods
	  */
	public void tearDown() { }

}

