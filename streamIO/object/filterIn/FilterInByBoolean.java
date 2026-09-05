package streamIO.object.filterIn;

import streamIO.IIStreamIn;
import streamIO.object.AFilterIn;
import streamIO.object.IStreamIn;

/**
  * Filter that selects stream items whose position is marked {@code true} in a boolean array.
  * <p>
  * Title: FilterInByBitMask<p>
  * Description:
  * Column Filter
  * Filters a streamIO by an Array of Boolean that determines,
  * which Items will be chosen.
  * As an Optimization here, the skip() Command is used to save nextItem() Calls
  *
  * This is used to e.g. filter Columns from a Row in a Container
  * 
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-03, 06;44;48<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:46:45Z
  * digest: c9ce2ac927bff19a345edbdee040bb3ea46af211833ce047d368cb3613a976b0
  * stale: false
  * tags: [code/stream_filter, code/decorator_pattern]
  * concepts: [Stream Filter (Input)]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterInByBoolean
extends AFilterIn {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Mask for the actual Filter	 */
	protected int mMask = -1;
	
	/** The actual Filter	 */
	protected boolean[] mFilter; //pre-shifting to make Algorithm easier!
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public FilterInByBoolean(IIStreamIn Enum, boolean[] Filter) {
		super(Enum); this.mFilter = Filter; } //
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn:
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
		if (mMask >= mFilter.length) return null;
		do { currItem = nextItem();
		} while (!mFilter[mMask += 1]);
		return currItem; }
	
	/**Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(long Position) { //throws    NoSuchMethodException {
		((IStreamIn) in).reSet(); mMask = ((int) Position)-1; return Position; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		System.out.println("Testing " + FilterInByBitMask.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		testIt(); }
	
}
