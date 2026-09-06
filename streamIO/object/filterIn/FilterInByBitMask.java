package streamIO.object.filterIn;

import streamIO.IIStreamIn;
import streamIO.object.AFilterIn;
import streamIO.object.IStreamIn;

/**
  * Filter that selects stream items whose position matches a set bit in a long bit mask.
  * <p>
  * Title: FilterInByBitMask<p>
  *
  * Description:
  * Column Filter
  * Filters a streamIO by a long Number interpreted as a Mask of Bits that determines,
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
  * mtime: 2026-09-05T20:46:43Z
  * digest: 208df024899ed7c631c62f736dbb7258a361516e50287c42172f0535dacb0bc4
  * stale: false
  * tags: [code/stream_filter, code/decorator_pattern]
  * concepts: [Stream Filter (Input)]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class FilterInByBitMask
extends AFilterIn {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Mask for the actual Filter	 */
	protected long mMask = 1;
	
	/** The actual Filter	 */
	protected long mFilter;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public FilterInByBitMask(final IIStreamIn Enum, final long _filter) {
		super(Enum); this.mFilter = _filter << 1; } //pre-shifting to make Algorithm easier!
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn:
	////////////////////////////////////////////////////////////////////////////////
	
	/**Returns the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
		if (mMask == 0) 
			return null;
		do { currItem = in.nextItem();
		} while ((mFilter & (mMask >>= 1)) != 0);
		return currItem; }
	
    /** Reports 1 while the bit mask still has a set bit to consume, 0 once exhausted.
     * @see streamIO.IAvailAble#availAble()     */
    public long availAble() {
		if (mMask == 0) 
	        return 0;
        return 1;
    }
    
	/**Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(final long _position) { //throws    NoSuchMethodException {
		((IStreamIn) in).reSet(); mMask = 1L << _position;
		return _position; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterInByBitMask.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		testIt(args); }
	
}
