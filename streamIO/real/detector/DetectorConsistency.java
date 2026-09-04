/*
 * File Name: DetectorConsistency.java
 * Created on: 16.03.2004
 *
 */
package streamIO.real.detector;

import streamIO.Assert;
import streamIO.real.IStreamOutFloat;

/**
 * Title: DetectorConsistency<p>
 * Description:
 * Detects whether the incoming Values are consistently above/below the Average. 
 * If yes, the addItem Method returns 'null', otherwise 'this'. 
 * A common Production Surveillance Measure is to stop Production 
 * when 5 Values (typically Sample Means) are all above or below the desired Mean.  
 * Suspected cause: 
 * systematic overfilling or underfilling due to problems in the process.
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
public class DetectorConsistency 
extends DetectorThreshold {
	
	/** cache for the Value to compare with to identify the Direction 	 */
	final double tolerance; 
	
	/** threshold when the Alarm should be triggered	 */
	final int maxCountInDirection; 
	
	/**
	 * @param threshold_
	 */
	public DetectorConsistency(final int threshold, final double average_, final double tolerance_) {
		super(average_); 
		this.maxCountInDirection = threshold; 
		this.tolerance = tolerance_; 
	}
	
	/**@return this Object if the Event was not detected, null otherwise 
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		if (value >= compareValue+tolerance) {
			if (countInDirection < 0) {
				countInDirection = 1;
			} else {
				if (++countInDirection >  maxCountInDirection) {
					return this; } 
			}
		} else 
		if (value <= compareValue-tolerance) //excluding the Case of Equality! 
		{
			if (countInDirection > 0) {
				countInDirection = -1;
			} else {
				if (--countInDirection < -maxCountInDirection) {
					return this; } 
			}
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Testing and main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//
		final int numItems = 5; 
		final DetectorConsistency consistent = new DetectorConsistency(numItems, 0.5, 0);
		for (int i = 1<<(numItems-1); --i >= 0;) {
			Assert.IS_NULL(consistent.addDouble(Math.random())); }
		Assert.IS_NULL(consistent.addDouble(1)); 
		for (int i = numItems; --i >= 0;) {
			Assert.IS_NULL(consistent.addDouble(0)); 
		}
		Assert.NOT_NULL(consistent.addDouble(0)); 
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}
