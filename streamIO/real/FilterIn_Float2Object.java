package streamIO.real; //

import streamIO.object.AStreamIn;
import function.byref.ByRefDouble;

/**
  * Bridges an {@link IStreamIn_Float} stream to the {@code IStreamIn} interface by boxing
  * each value into a reused {@link ByRefDouble}.
  *
  * <p>Bridges the StreamIn_Float Interface to the StreamIn Interface.
  * Converts Streams of Float Numbers into Streams of Objects.
  * Reuses the same ByRefFloat to save Instantiation
  * when large Quantities of Values are needed.
  * Use @see streamIO.CopyStreamIn to create new Instances.
  * @see streamIO.Float.FilterIn_Int2Object for a formally identical Class.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-19-2001, 01:02 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:17:56Z
  * digest: 2d5e133101cbd9d0dc3aadc763242591e1a86eecd7f7d2f74fbd11e4c1eb785c
  * stale: false
  * tags: [code/stream_filter]
  * concepts: [Float-to-Object Adapter Filter]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public class FilterIn_Float2Object
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
	
	/** Reference to the Input streamIO being Transformed	 */
	protected IStreamIn_Float in;

	/** Local Reference to the Object returned by nextItem() */
	protected ByRefDouble Value = new ByRefDouble();
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public FilterIn_Float2Object(IStreamIn_Float in_) {
		this.in = in_; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the boxed value most recently filled by {@link #nextItem()}.
	  * @return the current Elements in this streamIO.
	  */
	public Object currItem() { return Value; }

	/** Returns 1, since a single reused holder is always the currently available item.
	  * @return the Number of Elements left in this streamIO.
	  */
	public long availAble() { return 1; }

	/** Reads the next value from the wrapped stream into the reused holder.
	  * @return the next Element in this streamIO.
	  */
	public Object nextItem() {
		Value.Value = in.nextDouble();
		return Value; }

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
