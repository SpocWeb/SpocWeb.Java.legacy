/*
 * File Name: FilterFloatWindow.java
 * Created on: 20.02.2004
 *
 */
package streamIO.real;

import streamIO.Assert;
import streamIO.Log;
import function.IFloatFunction;

/**
 * Averages the stream over a fixed-size sliding window in O(1) time per value, using the
 * delay cache to remove values as they leave the window.
 *
 * <p>Averages (sums up) the Stream through this Filter.
 * @see streamIO.real.FilterFloatAverage also averages, 
 * but with different Weights in O(m) Time.
 * This is an O(1) Operation. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:15:54Z
 * digest: fd4b733e8d27debda1a99e8c44d9786f5de3821d953aa433eb44ba8f6abcd4f6
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Sliding Window Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class FilterFloatWindow 
extends FilterFloatDelay {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(FilterFloatWindow.class);
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** the current Average	*/
	protected double average;
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	streaming Operation
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** O(1) Operation: adds the Value to the Cache and the Average, 
	 * removes the old Value moving out of the Window. 	 
	 */
	public double addValue(final double value) {
		return average+=value-super.addValue(value); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a window filter reading from {@code inStream_}, with the average pre-set to {@code initValue}.
	 * @param inStream_ the source stream to filter
	 * @param windowSize the number of trailing elements averaged over
	 * @param mapper_ optional function mapping each value before it enters the window
	 * @param initValue the value {@link #average} and the delay cache are initialized to
	 */
	public FilterFloatWindow(final IStreamIn_Float inStream_, final int windowSize,
	final IFloatFunction mapper_, final double initValue) {
		super(inStream_, windowSize, mapper_, initValue);
		if (!Double.isNaN(initValue)) {
			this.average = initValue; }
	}

	/** Creates a window filter writing to {@code outStream_}, with the average pre-set to {@code initValue}.
	 * @param outStream_ the destination stream for filtered output
	 * @param windowSize the number of trailing elements averaged over
	 * @param mapper_ optional function mapping each value before it enters the window
	 * @param initValue the value {@link #average} and the delay cache are initialized to
	 */
	public FilterFloatWindow(final IStreamOutFloat outStream_, final int windowSize,
	final IFloatFunction mapper_, final double initValue) {
		super(outStream_, windowSize, mapper_, initValue);
		if (!Double.isNaN(initValue)) {
			this.average = initValue; }
	}

	/** Creates a window filter reading from {@code inStream_}, with the delay cache pre-filled with NaN.
	 * @param inStream_ the source stream to filter
	 * @param windowSize the number of trailing elements averaged over
	 * @param mapper_ optional function mapping each value before it enters the window
	 */
	public FilterFloatWindow(final IStreamIn_Float inStream_, final int windowSize
	, final IFloatFunction mapper_) {
		this(inStream_, windowSize, mapper_, Double.NaN); }

	/** Creates a window filter writing to {@code outStream_}, with the delay cache pre-filled with NaN.
	 * @param outStream_ the destination stream for filtered output
	 * @param windowSize the number of trailing elements averaged over
	 * @param mapper_ optional function mapping each value before it enters the window
	 */
	public FilterFloatWindow(final IStreamOutFloat outStream_, final int windowSize
	, final IFloatFunction mapper_) {
		this(outStream_, windowSize, mapper_, Double.NaN); }

	/** Creates a window filter reading from {@code inStream_}, with no output-mapping function.
	 * @param inStream_ the source stream to filter
	 * @param windowSize the number of trailing elements averaged over
	 * @param initValue the value {@link #average} and the delay cache are initialized to
	 */
	public FilterFloatWindow(final IStreamIn_Float inStream_, final int windowSize
	, final double initValue) {
		this(inStream_, windowSize, null, initValue); }

	/** Creates a window filter writing to {@code outStream_}, with no output-mapping function.
	 * @param outStream_ the destination stream for filtered output
	 * @param windowSize the number of trailing elements averaged over
	 * @param initValue the value {@link #average} and the delay cache are initialized to
	 */
	public FilterFloatWindow(final IStreamOutFloat outStream_, final int windowSize
	, final double initValue) {
		this(outStream_, windowSize, null, initValue); }

	/** Creates a window filter reading from {@code inStream_}, with no mapping and no pre-fill.
	 * @param inStream_ the source stream to filter
	 * @param windowSize the number of trailing elements averaged over
	 */
	public FilterFloatWindow(final IStreamIn_Float inStream_, final int windowSize) {
		super(inStream_, windowSize); }

	/** Creates a window filter writing to {@code outStream_}, with no mapping and no pre-fill.
	 * @param outStream_ the destination stream for filtered output
	 * @param windowSize the number of trailing elements averaged over
	 */
	public FilterFloatWindow(final IStreamOutFloat outStream_, final int windowSize) {
		super(outStream_, windowSize); }

	/////////////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** tests averaging 1 Element	 */
	private static final void testAverage() throws Exception {
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(); 
		final StreamIn_Arithmetic s2 = new StreamIn_Arithmetic(); 
		final FilterFloatWindow avg1 = new FilterFloatWindow(s2, 1);
		for (int i = 10; --i >= 0; ) {
			Assert.EQUALS(s1.nextDouble(), avg1.nextDouble()); 
		}
	}

	/** tests averaging 0 Elements	 */
	private static void testAvg0() {
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(); 
		//no Average first
		final FilterFloatWindow avg0 = new FilterFloatWindow(s1, 0, null, Math.PI);
		for (int i = 10; --i >= 0; ) {
			Assert.EQUALS(Math.PI, avg0.nextDouble());
		}
	}
	
	/** tests all Methods of this Class 	 */
	public static void testIt() throws Exception {
		testAvg0();
		testAverage();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}
