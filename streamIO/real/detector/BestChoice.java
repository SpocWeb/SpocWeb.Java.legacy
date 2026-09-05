/*
 * File Name: BestChoice.java
 * Created on: 02.05.2004
 *
 */
package streamIO.real.detector;

import streamIO.Log;
import streamIO.real.IStreamOutFloat;

/**
 * Implements the "secretary problem" strategy for choosing the best of a fixed-length
 * sequence of offers seen one at a time.
 *
 * <p>This can e.g. be used to choose the best Offer of a Sale with a Probability of 1/e.
 * This can e.g. be used to choose the best Offer of a Sale with a Probability of 1/e.  
 * Conditions: 
 * * Offer has to be accepted right away! 
 *   (you cannot go back to a previous Offer, "burned Earth") 
 * * 'no Deal' is acceptable (happens with 1/e Probability) 
 * * incoming Values are independent of each other 
 *   (Partners don't know each other's Offers)
 * * Number of Offers is limited (mostly due to Time) 
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
 * mtime: 2026-09-05T11:24:29Z
 * digest: b78f59f3284848b521e662f4b74da227979bb72e810b9542b20b733bb198d378
 * stale: false
 * tags: [code/anomaly_detection]
 * concepts: [Best-Choice Selector]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class BestChoice 
implements IStreamOutFloat {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(BestChoice.class, 0);
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Maximum Number of Items to test	 */
	int maxNumItems; 
	
	/** current maximum Value	 */
	double maxValue; 
	
	/** Counter of Values	 */
	int count; 
	
	/** initializing Constructor 
	 * @param maxNumItems_
	 */
	public BestChoice(final int maxNumItems_) {
		this.maxNumItems = (int) Math.round(maxNumItems_/Math.E); 
	}
	
	/** initializing Constructor 
	 * @param maxNumItems_
	 */
	public int getNumItems() {
		return (int) Math.round(maxNumItems*Math.E); 
	}
	
	/** Forwards a float value to {@link #addDouble(double)}.
	 * @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public IStreamOutFloat addFloat(final float value) {
		return addDouble(value); }
	
	/** Tracks the running maximum and applies the secretary-problem cutoff rule.
	 * @return this Stream as long as a Best Choice has not been made.
	 * Null on the Best Choice
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		++count; 
		if (maxValue < value) {
			maxValue = value; //search for the Maximum
			if (maxNumItems < count) { //
				maxNumItems = count; 
				return null; 
			}
		}
		return this;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// #region Testing and main Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt(final String[] args) {	//
		final int numIterations = 100*100; //1% Accuracy 
		final int maxNumItems = 3;//27;//2; 
		int numMaxFound = 0; 
		int numNoDeals = 0; 
		double successRatio = 0; 
		for (int i = numIterations; --i >= 0;) {
			final BestChoice choice = new BestChoice(maxNumItems);
			double foundValue = 0;  
			double maxValue = 0;  
			boolean found = false; 
			for (int j = maxNumItems; --j >= 0;) {
				final double value = Math.random();
				if (maxValue < value) {
					maxValue = value; }
				if (choice.addDouble(value) == null) { //Maximum found
					if (!found) { 
						found = true; 
						foundValue = value; 
					}
				}
			}
			if (!found) {
				++numNoDeals; 
			} else {
				if (foundValue == maxValue) {
					++numMaxFound; }
				successRatio += foundValue/maxValue;
			}
		}
		L.n("#Iterations").l(numIterations);
		L.n("#Values tested").l(maxNumItems);
		L.n("#Deals  ").l(numMaxFound).l("expected:").l(numIterations/Math.E);
		L.n("#NoDeals").l(numNoDeals ).l("expected:").l(numIterations/Math.E);
		L.n("#Success-Ratio: ").l(successRatio/(numIterations-numNoDeals));
		L.n("The high Success-Ratio indicates that in the Deal Cases");
		L.n("nearly always the Optimum of these is found (assuming homogeneous Distribution)");
		L.n("even with only ").l(maxNumItems).l(" Values tested.");
	}
	
	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(args); 
	}
	
}
