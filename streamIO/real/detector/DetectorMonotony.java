/*
 * File Name: DetectorMonotony.java
 * Created on: 16.03.2004
 *
 */
package streamIO.real.detector;

import streamIO.Assert;
import streamIO.real.IStreamOutFloat;

/**
 * Detects a run of monotonously increasing or decreasing values, a classic production
 * surveillance signal of slow process drift.
 *
 * <p>A common Rule for Production Surveillance is to stop
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:25:59Z
 * digest: a4d59d0b351f703a3f19e62aa476e7d42dc62b74584dd191762e7307b98db7dc
 * stale: false
 * tags: [code/anomaly_detection]
 * concepts: [Monotony Trend Detector]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class DetectorMonotony 
extends DetectorConsistency {
	
	/** Creates a monotony detector triggering after {@code threshold} consecutive increases or decreases.
	 * NaN is not comparable to any Number
	 * @param threshold the number of consecutive same-direction values that trigger detection
	 */
	public DetectorMonotony(final int threshold) {
		super(threshold, Float.NaN, 0); //IStreamIn_Float.EOF);
	}

	/** Delegates to {@link DetectorConsistency#addDouble(double)}, then re-seeds the comparison
	 * value with the value just added so each new value is compared to its predecessor.
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
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