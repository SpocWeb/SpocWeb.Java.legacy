/*
 * File Name: DetectorAlternation.java
 * Created on: 16.03.2004
 *
 */
package streamIO.real.detector;

import streamIO.Assert;
import streamIO.real.IStreamOutFloat;

/**
 * Title: DetectorAlternation<p>
 * Description:
 * Detects alternating Results. 
 * A common Production Control Measure is to stop Production 
 * when 14 Elements in a Row are alternating about the Target Value. 
 * Suspected cause: 
 * two different operators, machines, or suppliers are feeding into one system 
 * but are not in agreement. 
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
public class DetectorAlternation 
extends DetectorConsistency {

	/**
	 * @param threshold
	 * @param average_
	 */
	public DetectorAlternation(final int threshold, final double average_, final double tolerance_) {
		super(threshold, average_, tolerance_);
	}
	
	/** @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		countInDirection = -countInDirection;
		return super.addDouble(value);
	}
	
	/** @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	//public IStreamOutFloat addFloat(float value) {
	//	return super.addFloat(value); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// #region Testing and main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//
		final int numItems = 5; 
		final DetectorAlternation alternating = new DetectorAlternation(numItems, 0.5, 0);
		for (int i = 1<<(numItems-1); --i >= 0;) {
			Assert.IS_NULL(alternating.addDouble(Math.random())); }
		Assert.IS_NULL(alternating.addDouble(numItems&1)); 
		Assert.IS_NULL(alternating.addDouble(numItems&1)); 
		for (int i = numItems+1; --i > 0;) {
			Assert.IS_NULL(alternating.addDouble(i & 1)); 
		}
		Assert.NOT_NULL(alternating.addDouble(0)); 
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
