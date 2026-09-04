package streamIO.copy;

//import Stream.*;
import java.lang.reflect.Array;

import streamIO.Log;

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
