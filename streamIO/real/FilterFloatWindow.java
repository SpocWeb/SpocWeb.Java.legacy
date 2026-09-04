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
 * Title: FilterFloatWindow<p>
 * Description:
 * Averages (sums up) the Stream through this Filter. 
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
	
	/**
	 * @param inStream_
	 * @param windowSize
	 * @param mapper_
	 * @param initValue
	 */
	public FilterFloatWindow(final IStreamIn_Float inStream_, final int windowSize,
	final IFloatFunction mapper_, final double initValue) {
		super(inStream_, windowSize, mapper_, initValue);
		if (!Double.isNaN(initValue)) {
			this.average = initValue; }
	}

	/**
	 * @param outStream_
	 * @param windowSize
	 * @param mapper_
	 * @param initValue
	 */
	public FilterFloatWindow(final IStreamOutFloat outStream_, final int windowSize,
	final IFloatFunction mapper_, final double initValue) {
		super(outStream_, windowSize, mapper_, initValue);
		if (!Double.isNaN(initValue)) {
			this.average = initValue; }
	}

	/**
	 * @param inStream_
	 * @param windowSize
	 * @param mapper_
	 */
	public FilterFloatWindow(final IStreamIn_Float inStream_, final int windowSize
	, final IFloatFunction mapper_) {
		this(inStream_, windowSize, mapper_, Double.NaN); }

	/**
	 * @param outStream_
	 * @param windowSize
	 * @param mapper_
	 */
	public FilterFloatWindow(final IStreamOutFloat outStream_, final int windowSize
	, final IFloatFunction mapper_) {
		this(outStream_, windowSize, mapper_, Double.NaN); }

	/**
	 * @param inStream_
	 * @param delay
	 * @param initValue
	 */
	public FilterFloatWindow(final IStreamIn_Float inStream_, final int windowSize
	, final double initValue) {
		this(inStream_, windowSize, null, initValue); }

	/**
	 * @param outStream_
	 * @param delay
	 * @param initValue
	 */
	public FilterFloatWindow(final IStreamOutFloat outStream_, final int windowSize
	, final double initValue) {
		this(outStream_, windowSize, null, initValue); }

	/**
	 * @param inStream_
	 * @param windowSize
	 */
	public FilterFloatWindow(final IStreamIn_Float inStream_, final int windowSize) {
		super(inStream_, windowSize); }

	/**
	 * @param outStream_
	 * @param windowSize
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
