/*
 * File Name: FilterFloatDelay.java
 * Created on: 20.02.2004
 *
 */
package streamIO.real;

import java.util.Arrays;

import streamIO.Assert;
import function.IFloatFunction;

/**
 * Delays a stream by a fixed, non-negative number of read/write operations, using a
 * ring-buffer cache.
 *
 * <p>Design Decisions / Implementation Details:
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
 * mtime: 2026-09-05T11:12:06Z
 * digest: 53271d99195a7e2dc3befdff83f357e605a8fe9fb418a9c5593cae07a5a5a488
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Delay Line Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class FilterFloatDelay 
extends FilterFloatByFunction {

	/////////////////////////////////////////////////////////////////////////////////////
	//	Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** stores the currently used Elements, used as a Queue	*/
	protected final double[] cache;
	
	/** Pointer of the current Queue Beginning and End	*/
	protected int cachePtr;
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a delay reading from {@code inStream_}, pre-filling the cache with {@code initValue}.
	 * @param inStream_ the source stream to delay
	 * @param mapper_ optional function mapping each value before it enters the delay cache
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamIn_Float inStream_, final int delay, final IFloatFunction mapper_, final double initValue) {
		super(inStream_, mapper_);
		this.cache = new double[delay+1];
		if (!Double.isNaN(initValue)) {
			Arrays.fill(cache, initValue); }
	}

	/** Creates a delay writing to {@code outStream_}, pre-filling the cache with {@code initValue}.
	 * @param outStream_ the destination stream for delayed output
	 * @param mapper_ optional function mapping each value before it enters the delay cache
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamOutFloat outStream_, final int delay, final IFloatFunction mapper_, final double initValue) {
		super(outStream_, mapper_);
		this.cache = new double[delay+1];
		if (!Double.isNaN(initValue)) {
			Arrays.fill(cache, initValue); }
	}

	/** Creates a delay reading from {@code inStream_}, with no output-mapping function.
	 * @param inStream_ the source stream to delay
	 * @param initValue the value the delay cache is pre-filled with
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamIn_Float inStream_, final int delay, final double initValue) {
		this(inStream_, delay, null, initValue); }

	/** Creates a delay writing to {@code outStream_}, with no output-mapping function.
	 * @param outStream_ the destination stream for delayed output
	 * @param initValue the value the delay cache is pre-filled with
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamOutFloat outStream_, final int delay, final double initValue) {
		this(outStream_, delay, null, initValue); }

	/** Creates a delay reading from {@code inStream_}, with the delay cache pre-filled with NaN.
	 * @param inStream_ the source stream to delay
	 * @param mapper_ optional function mapping each value before it enters the delay cache
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamIn_Float inStream_, final int delay, final IFloatFunction mapper_) {
		this(inStream_, delay, mapper_, Double.NaN); }

	/** Creates a delay writing to {@code outStream_}, with the delay cache pre-filled with NaN.
	 * @param outStream_ the destination stream for delayed output
	 * @param mapper_ optional function mapping each value before it enters the delay cache
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamOutFloat outStream_, final int delay, final IFloatFunction mapper_) {
		this(outStream_, delay, mapper_, Double.NaN); }

	/** Creates a delay reading from {@code inStream_}, with no mapping and no cache pre-fill.
	 * @param inStream_ the source stream to delay
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamIn_Float inStream_, final int delay) {
		this(inStream_, delay, null); }

	/** Creates a delay writing to {@code outStream_}, with no mapping and no cache pre-fill.
	 * @param outStream_ the destination stream for delayed output
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamOutFloat outStream_, final int delay) {
		this(outStream_, delay, null); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	//	streaming Operation
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** adds a single Value to the Cache, called by the stream Methods. 	 */
	public double addValue(final double value) {
		cache[cachePtr] = value; 
		if (++cachePtr >= cache.length) { 
			cachePtr = 0; }
		return cache[cachePtr];
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods
	/////////////////////////////////////////////////////////////////////////////////////
		
	/** tests whether the Delay works 	 */
	private static final void testDelay() throws Exception {
		testDelay(0);
		testDelay(3);
	}

	/** tests whether the given Delay works 	 */
	private static void testDelay(final int numDelays) {
		final StreamIn_Arithmetic s1 = new StreamIn_Arithmetic(); 
		final StreamIn_Arithmetic s2 = new StreamIn_Arithmetic(); 
		final FilterFloatDelay delay = new FilterFloatDelay(s2, numDelays);
		for (int i = numDelays; --i >= 0;) {
			delay.nextDouble(); }
		for (int i = 10; --i >= 0; ) { //check the first n Numbers
			Assert.EQUALS(s1.nextDouble(), delay.nextDouble());
		}
	}
	
	/** tests all Methods of this Class 	 */
	public static void testIt() throws Exception {
		testDelay();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}
	
}
