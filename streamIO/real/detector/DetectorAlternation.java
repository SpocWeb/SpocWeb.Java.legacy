/*
 * File Name: DetectorAlternation.java
 * Created on: 16.03.2004
 *
 */
package streamIO.real.detector;

import streamIO.Assert;
import streamIO.real.IStreamOutFloat;

/**
 * Detects a run of values alternating above and below the target, a classic production
 * control signal that two disagreeing sources are feeding one system.
 *
 * <p>A common Production Control Measure is to stop Production
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:24:44Z
 * digest: 1fb954b7a47a5f5e15d5ba8257b8c4fd8d26d32c01ae2d07dd40c356f04d8a95
 * stale: false
 * tags: [code/anomaly_detection]
 * concepts: [Alternation Pattern Detector]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class DetectorAlternation 
extends DetectorConsistency {

	/** Creates an alternation detector triggering after {@code threshold} consecutive direction flips.
	 * @param threshold the number of alternations that trigger detection
	 * @param average_ the reference value values are compared against
	 * @param tolerance_ the tolerance around the reference value treated as neutral
	 */
	public DetectorAlternation(final int threshold, final double average_, final double tolerance_) {
		super(threshold, average_, tolerance_);
	}

	/** Flips the tracked direction before delegating, so every value counts as alternating.
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
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
