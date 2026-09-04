package streamIO.object.parser;

import java.util.ArrayList;

import streamIO.AStreamOut;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.object.AStreamIn;

/**
  * This Class encapsulates an ArrayList Object
  * and allows to performed buffered Reads and Writes up to a Limit.
  *
  * This is a small TestBed for the nested Streams.
  * @see streamIO.Object.ArrayStreamIn
  * @see streamIO.Object.Enumerator.Container.Array for the full Implementation
  */
public class Array2Stream
extends AStreamIn
implements IIStreamOut {

	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Recursively prints the whole Content of the given Input streamIO to System.out
	  */
	final static public void PRINT_CONTENTS(IIStreamIn SI) {
		Object obj;
		do {
			obj = SI.nextItem();
			if (obj instanceof IIStreamIn) {
				System.out.println("["); PRINT_CONTENTS((IIStreamIn) obj);
				System.out.println("]");
			} else {
				System.out.println(obj);
			}
		} while (obj != null);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants, Variables, Defaults
	////////////////////////////////////////////////////////////////////////////
	
	/** The Default Size for creating new Containers	*/
	public static int DefaultSize = 9;
	
	////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** The actual Array storing the Object Items	*/
	protected ArrayList arr = new ArrayList(DefaultSize);
	
	/** The current Object Items in the Array  	*/
	protected int curr = -1;

	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble() { return arr.size() - curr - 1; }

	/**
	 * @see streamIO.IIStreamIn#isValid()
	 */
	public boolean isValid() { return curr < arr.size(); }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return curr; }
	
	/** @see streamIO.object.AStreamIn#currItem()	 */
	public Object currItem() {
		if (curr >= arr.size()) 
			return IIStreamIn.EOI; 
		//if (ret instanceof Array2Stream) { //create an InputStream from the Object and reset it;
		//	return ret; }	//here the Object itself is an InputStream, so nothing to do!
		return arr.get(curr);
	}
	
	/** @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public Object nextItem() {
		if (++curr >= arr.size()) 
			  curr  = arr.size(); 
		return currItem(); }
	
	/** Adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * The Position of the Item is undefined either.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * @return this StreamOut to append more Items
	  */
	public IIStreamOut addItem(final Object arg) { //
		if (arg instanceof IIStreamIn) {
			Array2Stream tmp = new Array2Stream();
			arr.add(tmp); //.arr);
			//either stream all Items from the Input Stream here
			//or return tmp instead of this to indicate that new Streaming is necessary!
			return tmp; } //for adding the following Items to the new Container
			//You must use a recursive STREAM Method and not an iterative,
			//except if you reuse the same Stream Object over and over again!
		arr.add(arg);
		return this; }

	/** Creates an uninitalized new Instance of it's class.
	  * When overriding, use newInstance on all Components.	 */
	//public InstantiAble newInstance() { return new Array2Stream(); }
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Array2Stream.class.getName());
		Array2Stream arr1, arr3, arrOut, arr = new Array2Stream();
		arr .arr.set(0, "0");
		arr .arr.set(1, arr1 = new Array2Stream());
		arr .arr.set(2, "2");
		arr .arr.set(3, arr3 = new Array2Stream());
		arr .arr.set(4, "4");
		arr1.arr.set(0, "1,0");
		arr1.arr.set(1, "1,1");
		arr1.arr.set(2, "1,2");
		arr3.arr.set(0, "3,0");
		arr3.arr.set(1, "3,1");
		arr3.arr.set(2, "3,2");
//		printContents(arr);
		arrOut = new Array2Stream();
		System.out.println("Number of Item streamed: " + AStreamOut.STREAM(arr, arrOut, Integer.MAX_VALUE));
		PRINT_CONTENTS(arrOut);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
