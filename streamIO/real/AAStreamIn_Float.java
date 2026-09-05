package streamIO.real;

import streamIO.AStreamOut;
import streamIO.integer.filter.LimitedSizeOutputStream;
import streamIO.object.AStreamIn;
import streamIO.object.backTrack.BackTracker;
import streamIO.object.enumer.container.AContainer;
import tester.process.StreamProcessor;


/** 
 * Abstract Random Number Generator using a Float Point (double) Generator
 * and emulating various Generators of other primitive Types.
 * 
 * The Optimization here supports Generation of derived float Numbers
 * with minimal float Point Arithmetics (Overflow possible though!). 
 * Expects the derived Class to return a normed Distribution in [0..1)
 *
 * Subclasses:
 * <none>
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:08:37Z
 * digest: 3c63bea187c61f78b946bd31f30d92cecbeddee07fb8166a58fe271f21aa63e9
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Float Stream Input Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public abstract class AAStreamIn_Float
extends AStreamIn
implements IStreamIn_Bound_Float {
    
	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Float: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
    /** Returns the number of items still available from this stream.
     * @see streamIO.IAvailAble#availAble()     */
    abstract public long availAble();

    /** Returns the maximum number of items that can be marked and reset.
     * @see streamIO.IMarkAble#getMaxMarkSize()     */
    abstract public long getMaxMarkSize();

    /** Returns the lower bound of the generated distribution.
     * @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()     */
    abstract public double getMinDouble();

    /** Returns the next double-precision random value from this generator.
     * @see streamIO.real.IStreamIn_Float#nextDouble()     */
    abstract public double nextDouble();

    /** Returns the current position within this stream.
     * @see streamIO.IAvailAble#getPosition()     */
    abstract public long getPosition();

    /** Returns the current item of this stream.
     * @see streamIO.object.IStreamIn#currItem()     */
    abstract public Object currItem();

    /** Advances to and returns the next item of this stream.
     * @see streamIO.IFactory#nextItem()     */
    abstract public Object nextItem();
    
	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	final static public int FILL_ARRAY(final IStreamIn_Float stream, final float[] arr, final int start, final int stop) {
		for (int i = start-1; ++i < stop; ) {
			if ((arr[i] = stream.nextFloat()) == IStreamIn_Float.EOS) { //
				return i-start-1; }   
		}
		return stop-start; 
	}
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */
	final static public int FILL_ARRAY(final IStreamIn_Float stream, final double[] arr, final int start, final int stop) {
		for (int i = start-1; ++i < stop; ) {
			if ((arr[i] = stream.nextDouble()) == IStreamIn_Float.EOS) { //
				return i-start-1; }   
		}
		return stop-start; 
	}
	
	/** streams all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening
	  */
	final static public long STREAM(final IStreamIn_Float iter, final IStreamOutFloat out) {
		return STREAM(iter, out, Integer.MAX_VALUE); }

	/** streams all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  *
	  * Transfers the whole Content of the given Input streamIO to the Output streamIO.
	  * Recursion is necessary, because both Streams should not have to know their Parents.
	  * In fact they could even be shared by different Parents in a Diamond Shape.
	  * The Depth makes it clear that it is possible to do shallow Copies
	  * and Copies up to a certain Depth.
	  * If the Objects are providing StreamIn Instances or are themselves
	  * Instances of StreamIn, handing them over ByRef
	  * allows to transfer later Changes via the Object Reference
	  * but also the Danger of encountering mysterious Changes and Side Effects!
	  *
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening
	  */
	final static public long STREAM(final IStreamIn_Float iter, final IStreamOutFloat out, long numItems) {
	//	if (iter instanceof IStreamIn_Float) {
	//		try { iter.reSet();
	//		} catch (NoSuchMethodException x) { } //throw new NoSuchMethodError(x.toString()); } //ignore it
	//	}
		//long ret = 0;
		while(--numItems >= 0) {
			out.addDouble(iter.nextDouble()); //++ret;
		}
		return numItems; //ret; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Float: default Implementations 
	////////////////////////////////////////////////////////////////////////////
	
	/** Reads the next float value without advancing the stream position.
	 * @return the next Value without moving to it.	 */
	public float peekFloat() { //throws    NoSuchMethodException {
		//throw new NoSuchMethodException("No generic Implementation!");
		final float ret = nextFloat();
		pushBack();
		return ret;
	}

	/** Reads the next double value without advancing the stream position.
	 * @return the next Value without moving to it.	 */
	public double peekDouble() { //throws    NoSuchMethodException {
		//throw new NoSuchMethodException("No generic Implementation!");
		final double ret = nextDouble(); 
		pushBack(); 
		return ret; 
	}
	
	/**Random single Precision Number	 */
	public float nextFloat() { return (float) nextDouble(); }
	
	/**Public Method for other Classes to determine the maximum Value	 */
	public double getMaxDouble() { return 1; }
	
	/** Public Method for other Classes to determine the maximum Value	 */
	public long getMaxValue() { return 1; }
	
	/**Random single Precision Number from 0 to MaxFloat	 */
	public float nextFloat(final float MaxFloat) {
		return MaxFloat*nextFloat(); }
	
	/**Random double Precision Number from 0 to MaxFloat	 */
	public double nextDouble(final double MaxDouble) {
		return MaxDouble*nextDouble(); }
	
	/**Random Long Number 	 */
	public long nextLong() { return (long) (Long.MAX_VALUE*nextDouble()); }
	
	/**Random Integer Number 	 */
	public int nextInt() {
		return (int) (Integer.MAX_VALUE*nextDouble()); }
//		return (int) (Integer.MAX_VALUE*nextFloat ()); }
	
	/**Random Integer Number from 0 to MaxInt-1	 */
	public int nextInt(final int MaxInt) {
		return (int) (MaxInt*nextDouble()); }
//		return (int) (MaxInt*nextFloat ()); }
	
	/**Random Integer Number from 0 to MaxInt-1	 */
	public long nextLong(final long MaxLong) {
		return (long) (MaxLong*nextDouble()); }
	
	/**Cloning creates only a shallow Copy.  
	 * @see streamIO.integer.IStreamIn_Int#Iterator()	 */
	public IStreamIn_Float FloatIterator() {
		try { 
			final IStreamIn_Float ret = (IStreamIn_Float) clone();
			ret.reSet(); 
			return ret;
		} catch(final CloneNotSupportedException x) {
			//throw new BaseException(x); 
			return null; 
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */ 	
	public int fillArray(final float[] arr, final int start, final int stop) {
		return FILL_ARRAY(this, arr, start, stop); }
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */ 	
	public int fillArray(final float[] arr) { return fillArray(arr, 0, arr.length); }
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 */
	public int fillArray(final double[] arr) { return fillArray(arr, 0, arr.length); }
	
	/** fills the Array Range from this Stream 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */
	public int fillArray(final double[] arr, final int start, final int stop) {
		return FILL_ARRAY(this, arr, start, stop);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** fills the Array Range from this Stream but only with Values below the given maximum Value 
	 * @param arr the Array to fill 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 */
	public int fillArray(final float[] arr, final float maxVal, final int start, final int stop) {
		for (int i = start-1; ++i < stop; ) {
			if ((arr[i] = nextFloat()) == IStreamIn_Float.EOS) { //
				return i-start-1; }   
		}
		return stop-start; 
	}
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 */ 	
	public int fillArray(final float[] arr, final float maxVal) { 
		return fillArray(arr, maxVal, 0, arr.length); }
	
	/** fills the Array Range from this Stream but only with Values below the given maximum Value 
	 * @param arr the Array to fill 
	 * @param start the first Index to fill (inclusive) 
	 * @param stop the first Index not to fill (exclusive) 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 */ 	
	public int fillArray(final double[] arr, final float maxVal, final int start, final int stop) {
		for (int i = start-1; ++i < stop; ) {
			if ((arr[i] = nextDouble()) == IStreamIn_Float.EOS) { //
				return i-start-1; }   
		}
		return stop-start; 
	}
	
	/** fills the whole Array from this Stream 
	 * @param arr the Array to fill 
	 * @param maxVal the maximum Value to fill with (exclusive) 
	 */ 	
	public int fillArray(final double[] arr, final float maxVal) { 
		return fillArray(arr, maxVal, 0, arr.length); }
	
}
