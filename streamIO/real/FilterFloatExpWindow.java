/*
 * File Name: FilterFloatExpWindow.java
 * Created on: 06.03.2004
 *
 */
package streamIO.real;

import streamIO.Assert;
import streamIO.Log;
import function.IFloatFunction;

/**
 * An averaging filter with exponential window fall-off, where older values contribute
 * exponentially less to the running average.
 *
 * <p>An averaging Filter with exponential Window fall-off.
 * Previous Values are exponentially less relevant, 
 * the older they are. 
 * Should only be used for Data with an absolute Scale! 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Similar Classes: 
 * @see streamIO.real.FilterFloatAverage
 * @see streamIO.real.FilterFloatWindow
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:12:57Z
 * digest: 203c5365de779f203b18d6e1478ee09dcf064328fd60d9a0712573aaa7b8cc81
 * stale: false
 * tags: [code/stream_filter, code/running_statistics]
 * concepts: [Exponential Window Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class FilterFloatExpWindow 
extends FilterFloatByFunction {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(FilterFloatWindow.class);
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** the current Average	*/
	protected double average;
	
	/** the Shrinking Factor from (0,1]
	 * with 0 resulting in an Average over the total History
	 * and 1 resulting in only the current Value
	 */
	protected final double shrink;
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	streaming Operation
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** O(1) Operation: adds the Value to the Cache and the Average, 
	 * removes the old Value moving out of the Window. 	 
	 */
	public double addValue(final double value) {
		return average += shrink*(value - average);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a filter reading from {@code inStream_}, with the average pre-set to {@code initValue}.
	 * @param inStream_ the source stream to filter
	 * @param mapper_ optional function mapping each value before it enters the average
	 * @param shrink_ the exponential shrink factor in (0,1]
	 * @param initValue the value {@link #average} is initialized to
	 */
	public FilterFloatExpWindow(final IStreamIn_Float inStream_
	, final IFloatFunction mapper_, final double shrink_, final double initValue) {
		super(inStream_, mapper_);
		this.shrink = shrink_;
		this.average = initValue;
	}

	/** Creates a filter writing to {@code outStream_}, with the average pre-set to {@code initValue}.
	 * @param outStream_ the destination stream for filtered output
	 * @param mapper_ optional function mapping each value before it enters the average
	 * @param shrink_ the exponential shrink factor in (0,1]
	 * @param initValue the value {@link #average} is initialized to
	 */
	public FilterFloatExpWindow(final IStreamOutFloat outStream_
	, final IFloatFunction mapper_, final double shrink_, final double initValue) {
		super(outStream_, mapper_);
		this.shrink = shrink_;
		this.average = initValue;
	}

	/** Creates a filter reading from {@code inStream_}, with the average starting at 0.
	 * @param inStream_ the source stream to filter
	 * @param shrink_ the exponential shrink factor in (0,1]
	 * @param mapper_ optional function mapping each value before it enters the average
	 */
	public FilterFloatExpWindow(final IStreamIn_Float inStream_, final double shrink_
	, final IFloatFunction mapper_) {
		this(inStream_, mapper_, shrink_, 0); }

	/** Creates a filter writing to {@code outStream_}, with the average starting at 0.
	 * @param outStream_ the destination stream for filtered output
	 * @param shrink_ the exponential shrink factor in (0,1]
	 * @param mapper_ optional function mapping each value before it enters the average
	 */
	public FilterFloatExpWindow(final IStreamOutFloat outStream_, final double shrink_
	, final IFloatFunction mapper_) {
		this(outStream_, mapper_, shrink_, 0); }

	/** Creates a filter reading from {@code inStream_}, with no mapping function.
	 * @param inStream_ the source stream to filter
	 * @param shrink_ the exponential shrink factor in (0,1]
	 * @param initValue the value {@link #average} is initialized to
	 */
	public FilterFloatExpWindow(final IStreamIn_Float inStream_, final double shrink_
	, final double initValue) {
		this(inStream_, null, shrink_, initValue); }

	/** Creates a filter writing to {@code outStream_}, with no mapping function.
	 * @param outStream_ the destination stream for filtered output
	 * @param shrink_ the exponential shrink factor in (0,1]
	 * @param initValue the value {@link #average} is initialized to
	 */
	public FilterFloatExpWindow(final IStreamOutFloat outStream_, final double shrink_
	, final double initValue) {
		this(outStream_, null, shrink_, initValue); }

	/** Creates a filter reading from {@code inStream_}, with no mapping and the average starting at 0.
	 * @param inStream_ the source stream to filter
	 * @param shrink_ the exponential shrink factor in (0,1]
	 */
	public FilterFloatExpWindow(final IStreamIn_Float inStream_, final double shrink_) {
		this(inStream_, null, shrink_, 0); }

	/** Creates a filter writing to {@code outStream_}, with no mapping and the average starting at 0.
	 * @param outStream_ the destination stream for filtered output
	 * @param shrink_ the exponential shrink factor in (0,1]
	 */
	public FilterFloatExpWindow(final IStreamOutFloat outStream_, final double shrink_) {
		this(outStream_, null, shrink_, 0); }

	/////////////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** tests averaging a constant Stream 	 */
	private static final void testAverageConst() throws Exception {
		L.enter();
		final double shrink = 0.5;
		double startValue = 1;
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(startValue, 2, 0); 
		final FilterFloatByFunction avg1 = new FilterFloatExpWindow(s1, shrink, startValue);
		for (int i = 10; --i >= 0; ) {
			Assert.EQUALS(startValue, avg1.nextDouble()); 
		}
	}

	
	/** tests averaging a Delta Peak 	 */
	private static final void testAveragePeak() throws Exception {
		L.enter();
		final double shrink = 0.5;
		double peak = 1;
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(0, 1, 0); 
		final FilterFloatByFunction avg1 = new FilterFloatExpWindow(s1, shrink, peak);
		for (int i = 10; --i >= 0; ) {
			Assert.EQUALS(peak*=shrink, avg1.nextDouble()); 
		}
	}

	/** tests averaging 1 (only the last) Element	 */
	private static final void testAvg1() throws Exception {
		L.enter();
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(); 
		final StreamIn_Arithmetic s2 = new StreamIn_Arithmetic(); 
		final FilterFloatByFunction avg1 = new FilterFloatExpWindow(s2, 1);
		for (int i = 10; --i >= 0; ) {
			Assert.EQUALS(s1.nextDouble(), avg1.nextDouble()); 
		}
	}

	/** tests averaging 0 Elements	 */
	private static void testAvg0() {
		L.enter();
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(); 
		//no Average first
		final FilterFloatByFunction avg0 = new FilterFloatExpWindow(s1, 0, Math.PI);
		for (int i = 10; --i >= 0; ) {
			Assert.EQUALS(Math.PI, avg0.nextDouble());
		}
	}
	
	/** tests all Methods of this Class 	 */
	public static void testIt() throws Exception {
		L.enter();
		testAverageConst();
		testAvg0();
		testAvg1();
		testAveragePeak();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}
