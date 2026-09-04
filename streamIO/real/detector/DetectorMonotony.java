/*
 * File Name: DetectorMonotony.java
 * Created on: 16.03.2004
 *
 */
package streamIO.real.detector;

import streamIO.Assert;
import streamIO.real.IStreamOutFloat;

/**
 * Title: DetectorMonotony<p>
 * Description:
 * Detects Production Failures consisting of a monotonous Sequence. 
 * A common Rule for Production Surveillance is to stop 
 * when 6 Measurements (usually Sample Means) 
 * are monotonously increasing or decreasing.  
 * 
 * Suspected cause: 
 * The products coming off the line are slowly drifting 
 * farther and farther away from the intended average value, 
 * probably due to problems with one or more machines.
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
public class DetectorMonotony 
extends DetectorConsistency {
	
	/**
	 * NaN is not comparable to any Number
	 * @param threshold
	 */
	public DetectorMonotony(final int threshold) {
		super(threshold, Float.NaN, 0); //IStreamIn_Float.EOF); 
	}
	
	/** @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		final IStreamOutFloat ret = super.addDouble(value);
		compareValue = value; //to detect Monotony, replace the last Value! 
		return ret; 
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
		final DetectorMonotony monotonous = new DetectorMonotony(numItems);
		for (int i = 1<<numItems; --i >= 0;) {
			Assert.IS_NULL(monotonous.addDouble(Math.random()));
		}
		for (int i = numItems+1; --i >= 0;) {
			Assert.IS_NULL(monotonous.addDouble(0)); 
		}
		Assert.NOT_NULL(monotonous.addDouble(0)); 
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
}