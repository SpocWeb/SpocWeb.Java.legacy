package streamIO.real;

import streamIO.object.AStreamIn;

/**
 * Bridges the {@link IStreamIn_Float} interface to the {@code IStreamIn} interface by
 * converting a stream of float numbers into a stream of reused fixed-length arrays.
 *
 * <p>Converts Streams of Float Numbers into Streams of Arrays of the same Length.
 * Converts Streams of Float Numbers into Streams of Arrays of the same Length.
 * Reuses the same Array to save Instantiation 
 * when large Quantities of Values are needed.
 * 
 * @see streamIO.CopyStreamIn can be used to create new Instances.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:17:23Z
 * digest: d16a43fd58163794a4f662cc313e71c40ae248bc99c7f8a7a64a22b3d6b8a37f
 * stale: false
 * tags: [code/stream_filter, code/vector_math]
 * concepts: [Float-to-Array Adapter Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class FilterIn_Float2Array
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Input streamIO of Float Numbers	 */
	protected final IStreamIn_Float in;

	/** Array of Float Numbers	 */
	protected final float[] arrFloat;

	/** Array of Float Numbers	 */
	protected final double[] arrDouble;

	/** Flag to switch between Float and Double Precision Numbers	 */
	boolean doubleVals;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Input streamIO of Float Numbers	*/
	public FilterIn_Float2Array(final IStreamIn_Float in, final int dim, final boolean doubleVals) {
		this.in   = in;
		this.doubleVals = doubleVals;
		arrFloat  = new float [dim];
		arrDouble = new double[dim]; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the array most recently filled by {@link #nextItem()}, float or double.
	  * @return the current Elements in this streamIO.
	  */
	public Object currItem() {
		return doubleVals ? (Object) arrDouble : (Object) arrFloat; }

	/** Returns 1, since a single reused array is always the currently available item.
	  * @return the Number of Elements left in this streamIO.
	  */
	public long availAble() { return 1; }

	/** Fills the reused array with the next {@code dim} values read from the wrapped stream.
	  * @return the next Element in this streamIO.
	  */
	public Object nextItem() {
		for (int i = arrDouble.length; --i >= 0; ) {
			arrFloat[i] = (float) (arrDouble[i] = in.nextDouble()); }
		return doubleVals ? (Object) arrDouble : (Object) arrFloat; }

	/** Returns the sort order of the wrapped float stream.
	  * @return the Order of the Elements in this streamIO.
	  * -1 for descending
	  *  0 for unordered
	  * +1 for ascending
	  */
	public byte getOrder() { return in.getOrder(); }

	/** Returns the maximum mark size of the wrapped stream.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return in.getMaxMarkSize(); }

	/** Returns the current position of the wrapped stream.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return in.getPosition(); }

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterIn_Float2Object.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
