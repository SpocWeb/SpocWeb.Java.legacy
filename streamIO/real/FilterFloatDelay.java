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
 * Title: FilterFloatDelay<p>
 * Description:
 * Delays a Stream by a non-negative Number of read / write Operations. 
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
	
	/**
	 * @param inStream_
	 * @param mapper_
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamIn_Float inStream_, final int delay, final IFloatFunction mapper_, final double initValue) {
		super(inStream_, mapper_);
		this.cache = new double[delay+1];
		if (!Double.isNaN(initValue)) {
			Arrays.fill(cache, initValue); }
	}
	
	/**
	 * @param outStream_
	 * @param mapper_
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamOutFloat outStream_, final int delay, final IFloatFunction mapper_, final double initValue) {
		super(outStream_, mapper_);
		this.cache = new double[delay+1];
		if (!Double.isNaN(initValue)) {
			Arrays.fill(cache, initValue); }
	}
	
	/**
	 * @param inStream_
	 * @param mapper_
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamIn_Float inStream_, final int delay, final double initValue) {
		this(inStream_, delay, null, initValue); }
	
	/**
	 * @param outStream_
	 * @param mapper_
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamOutFloat outStream_, final int delay, final double initValue) {
		this(outStream_, delay, null, initValue); }
	
	/**
	 * @param inStream_
	 * @param mapper_
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamIn_Float inStream_, final int delay, final IFloatFunction mapper_) {
		this(inStream_, delay, mapper_, Double.NaN); }
	
	/**
	 * @param outStream_
	 * @param mapper_
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamOutFloat outStream_, final int delay, final IFloatFunction mapper_) {
		this(outStream_, delay, mapper_, Double.NaN); }
	
	/**
	 * @param inStream_
	 * @param delay controls the Delay of this Filter, must be non-negative
	 */
	public FilterFloatDelay(final IStreamIn_Float inStream_, final int delay) {
		this(inStream_, delay, null); }
	
	/**
	 * @param outStream_
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
