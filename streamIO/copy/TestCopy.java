package streamIO.copy;

//import Stream.*;
import java.lang.reflect.Array;

import streamIO.Log;

/**Manual test harness entry point for the {@code streamIO.copy} package, exercising
 * {@link ACopyAble}'s copy/serialization contract via {@link ACopyAble#testIt}.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:48:47Z
 * digest: 4ad2dcd84b7bfb9bfa648a9ec91a165d37aefaa27c24616bbdfec8661ab97b44
 * stale: false
 * tags: [code/manual_test_harness]
 * concepts: [Copy Semantics]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class TestCopy {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(1);

	/** tests copying */
	final static public void testIt(final Object testInstance_) throws Exception {
		//final CopyAble testInstance = (CopyAble) testInstance_;
	}
	
	/**The main entry point for the application.
	 * @param args Array of command line parameters passed to the application.
	 * Here I can test the static Methods of the Classes
	 * and call the testIt() Methods.	 */
	public static void main (final String[] args) throws Exception {
//		throws java.io.IOException, IllegalAccessException,
//		NoSuchFieldException, InstantiationException, ClassNotFoundException  {
		L.n("Testing Package BaseCopy");
		int[] arr = {1,2,3,4,5};
        arr = (int[]) Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr));
//      arr = (int[]) arr.getClass().newInstance(); //throws an InstantiationException!
		L.n("Standard Output for an array:" + arr);
		L.n("Output from toString(Object):" + ACopyAble.DefaultFormatter.addItem(arr));
//		L.n(absCopyAble.fromText("{RingLonsg:5}"));
//		ACopyAble.toText(new RingLong(5));
		ACopyAble.testIt(TestCopy.class, args); 
		ACopyAble.testIt(ACopyAble.class, args);	//
	}

}
