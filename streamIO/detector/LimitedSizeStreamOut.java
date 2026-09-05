package streamIO.detector;

import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.IIStreamOut;

/**
  * Collects a limited Size of Objects and refuses to accept more,
  * first by returning null from the addItem Method,
  * and then by throwing an ArrayIndexOutOfBoundsException!
  *
  * Design Decisions / Implementation Details:
  * @see streamIO.Object.Byte.LimitedSizeOutputStream which splits up a Byte streamIO.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-19-2002, 01:45 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T08:59:38Z
  * digest: 23ff7be59b434838b2585468a119790fde9fa263ffb819ee1af02a29c658d38f
  * stale: false
  * tags: [code/fixed_size_buffer, code/overflow_detection]
  * concepts: [Capacity Management, Stream Output]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public class LimitedSizeStreamOut
extends AStreamOut {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** position of the last Object added to the streamIO:	 */
	protected int position; // = 0;

	/** Storage for the Objects added to the streamIO:	 */
	protected Object[] items;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the backing array holding every Object added so far.
	  * @return the Objects added to the streamIO:   */
	public Object[] getItems() { return items; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public LimitedSizeStreamOut(int size) { this.items = new Object[size]; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent AStreamOut: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public IIStreamOut addItem(Object arg) { //Hard Runtime Exception, if ignored:
		items[position] = arg; //IndexOutOfBoundsException automatically thrown!
		if (++position >= items.length) {
			return null; } //softly indicate End of Storage
		return this; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + LimitedSizeStreamOut.class.getName());
		final int TestSize = 5;
		LimitedSizeStreamOut out = new LimitedSizeStreamOut(TestSize);
		for(int i = TestSize; --i > 0;) {
			Assert.SAME(out, out.addItem(args)); }
		Assert. IS_NULL(out.addItem(args));
		try { out.addItem(args);
			Assert.FAIL("LimitedSizeStreamOut should throw an Exception when Capacity is exceeded!");
		} catch (ArrayIndexOutOfBoundsException x) { }
		Object[] items = out.getItems();
		Assert.EQUALS(items.length, TestSize);
		for(int i = TestSize; --i >= 0;) {  
			Object item = items[i];
			Assert.SAME(args, item); }
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

