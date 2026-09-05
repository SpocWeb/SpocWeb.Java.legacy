package streamIO.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import streamIO.IIStreamOut;

/**
  * Composite Pattern typed for Test Cases: runs a nested Collection of
  * {@link ITestCase}s depth-first before running its own reflective Tests.
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
  * Created on	10-19-2002, 12:04 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:19:40Z
  * digest: 88dee787283c86215e596233f8c60efcab7e72958aba601b05e96a34b542e516
  * stale: false
  * tags: [code/test_harness, code/composite_pattern]
  * concepts: [Testing, Composite Pattern]
  * facets: {layer: test, status: stable, complexity: low}
  * -->
  */
public class TestCollection
extends ATestCase
implements ITestCase {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Container for the Test Cases	 */
	protected Collection cases = new ArrayList();

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** adds a new Test Case to this composite Test.   */
	public TestCollection addTestCase(ITestCase testCase) {
		cases.add(testCase);
		return this; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface ITestCase: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Method to run a Test
	  * Instead of using a dedicated Error Handler that takes a Throwable
	  * a normal Output streamIO is used.
	  * Each Test Case instantiates its own Assert Object,
	  * to avoid Side Effects by global Criteria Changes
	  * (Defaults for relative and absolute Deviation in float and double).
	  * @param FailureHandler Log of all Test Failures, if null stops testing on the first Failure.
	  * @param ErrorHandler   Log of all Test Failures, if null stops testing on the first Error.
	  * @return true iif all Tests returned true.
	  */
	public boolean runTest(IIStreamOut FailureHandler, IIStreamOut ErrorHandler) {
		//Depth-first: first run the nested Tests...
		ITestCase testCase;
		Iterator iter = cases.iterator();
		while (iter.hasNext()) {
			testCase = (ITestCase) iter.next();
			testCase.setUp();
			boolean success = testCase.runTest(FailureHandler, ErrorHandler);
			testCase.tearDown();
			if (!success) {
				return false; }
		} //Depth-first: ...then run the Tests for this Class
		return super.runTest(FailureHandler, ErrorHandler); }

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests Method of this Instance	 */
public void test() throws Exception {
	System.out.println("Test was called in '" + this + "'!");
}

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) throws Exception {
	TestCollection tc = new TestCollection();
	tc.addTestCase(new TestCollection());
	tc.runTest(null, null);
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws Exception {
	testIt(args); }

}

