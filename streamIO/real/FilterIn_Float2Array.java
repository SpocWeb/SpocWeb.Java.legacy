package streamIO.real;

import streamIO.object.AStreamIn;

/**
 * Vector Generator
 * Bridges the IStreamIn_Float Interface to the IStreamIn Interface.
 * Converts Streams of Float Numbers into Streams of Arrays of the same Length.
 * Reuses the same Array to save Instantiation 
 * when large Quantities of Values are needed.
 * 
 * @see streamIO.CopyStreamIn can be used to create new Instances.
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
	
	/**
	  * @return the current Elements in this streamIO.
	  */
	public Object currItem() {
		return doubleVals ? (Object) arrDouble : (Object) arrFloat; }

	/**
	  * @return the Number of Elements left in this streamIO.
	  */
	public long availAble() { return 1; }

	/**
	  * @return the next Element in this streamIO.
	  */
	public Object nextItem() {
		for (int i = arrDouble.length; --i >= 0; ) {
			arrFloat[i] = (float) (arrDouble[i] = in.nextDouble()); }
		return doubleVals ? (Object) arrDouble : (Object) arrFloat; }

	/**
	  * @return the Order of the Elements in this streamIO.
	  * -1 for descending
	  *  0 for unordered
	  * +1 for ascending
	  */
	public byte getOrder() { return in.getOrder(); }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return in.getMaxMarkSize(); }

	/** @see streamIO.object.AStreamIn#getPosition()	 */
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
