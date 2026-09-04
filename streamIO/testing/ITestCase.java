package streamIO.testing;

import streamIO.IIStreamOut;

/**
  * Title: ITestCase<p>
  * Description:
  * Defines the Interface for a (composite) Test Case
  * that is to be run automatically.
  *
  * TODO: make all Methods static
  * and separate Logging from Assertion Methods
  * Hand over Loggers to the Test Methods
  * Define a Composite Pattern Test Suite.
  * Decide whether to use three OutputStreams
  * or a dedicated Interface to count the Failures
  * and to decide about whether to continue or not...
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-19-2002, 09:37 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface ITestCase {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Sets up the Test Fixture,
	  * i.e. all preparing Statements for the Set of all test...() Methods
	  */
	public void setUp();

	/** Tears down the Test Fixture,
	  * i.e. frees all Resources used by the Methods
	  */
	public void tearDown();

	/** Method to run a Test
	  * Instead of using a dedicated Error Handler that takes a Throwable
	  * a normal Output streamIO is used.
	  * Each Test Case instantiates its own Assert Object,
	  * to avoid Side Effects by global Criteria Changes
	  * (Defaults for relative and absolute Deviation in float and double).
	  * @return true when further Tests make sense.
	  */
	public boolean runTest(IIStreamOut FailureHandler, IIStreamOut ErrorHandler);

}

