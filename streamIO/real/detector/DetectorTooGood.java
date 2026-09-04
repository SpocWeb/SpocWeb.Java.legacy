/*
 * File Name: DetectorTooGood.java
 * Created on: 16.03.2004
 *
 */
package streamIO.real.detector;

import streamIO.Assert;
import streamIO.real.IStreamOutFloat;

/**
 * Title: DetectorTooGood<p>
 * Description:
 * Fifteen sample means in a row are only within 1 standard error of the target.  
 * Suspected cause: the process is more consistent than the specifications call for. 
 * If this overly consistent process costs time or money, it should be loosened up. 
 * If this overly consistent process does not add time or money, 
 * finding out what Difference this process does — and replicating this change in the future — 
 * may be worthwhile.
 *
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
public class DetectorTooGood 
extends DetectorConsistency {
	
	/**
	 * @param threshold
	 * @param average_
	 * @param stdDev_ the Standard Deviation (or similar) to judge the Size of the Deviation
	 */
	public DetectorTooGood(final int threshold, final double average_, final double stdDev_) {
		super(threshold, average_, stdDev_);
	}
	
	/** @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		if (Math.abs(value - compareValue) <= tolerance) {
			if (++countInDirection > maxCountInDirection) {
				return this; } 
		} else {
			countInDirection = 0; 
		}
		return null;
	}
	
	/** @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	//public IStreamOutFloat addFloat(float value) {
	//	return super.addFloat(value); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//
		final int numItems = 5; 
		final DetectorTooGood tooGood = new DetectorTooGood(numItems, 0.5, 0.5);
		for (int i = 1<<(numItems-1); --i >= 0;) {
			Assert.IS_NULL(tooGood.addDouble(3*Math.random())); }
		for (int i = numItems+1; --i > 0;) {
			Assert.IS_NULL(tooGood.addDouble(Math.random())); }
		Assert.NOT_NULL(tooGood.addDouble(Math.random())); 
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
