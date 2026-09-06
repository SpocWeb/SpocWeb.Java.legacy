/*
 * Created on 27.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tester.fuzzy;

import math.vector.VectorDouble;
import math.vector.VectorObject;
import tester.IMetric;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Implements a String Dictionary 
 * with a fuzzy Match Algorithm. 
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: 
 * @see tester.fuzzy.FuzzySetComparator
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:11:44Z
 * digest: 389020fe00936d0522eccfc8dc9287faf55ce747f984c58b0234af2a6845c7b2
 * stale: false
 * tags: [code/fuzzy_search, code/similarity_matching]
 * concepts: [Fuzzy Dictionary Lookup]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class FuzzyDictionary {
	
	/** Contains the distinct Words found so far, 
	 * mapped to their unique Index. 
	 * Indexed Access is used to perform rapid Comparisons between Sentences (Sets of Words)
	 */
	final public VectorObject words = new VectorObject(); 
	
	/** Holds the Distances from the last Operation for later querying. 	 */  
	final public VectorDouble distances = new VectorDouble(); 
	
	/** The Metric to derive the Distance from 	 */
	final protected IMetric metric; 
	
	/**
	 * Initializing Constructor 
	 * @param _metric
	 */
	public FuzzyDictionary(final IMetric _metric) {
		this.metric = _metric; 
	}
	
	/**
	 * Searches {@link #words} for the entry closest to token and adds token to {@link #words}
	 * when that closest Distance exceeds maxDist.
	 * @param token the Object to find
	 * @param maxDist the maximum Distance to consider an Object to be distinct
	 * and to be added to this Dictionary.
	 * To always add any Object to the Dictionary, set it to 0
	 * To never  add any Object to the Dictionary, set it to Double.POS_INFINITY
	 * @return the Index of the Object most similar to 'token'.
	 */
	public int getMostSimilarItem(final Object token, final double maxDist) {
		final int minIndex = getMostSimilarItem(token);
		final double minDist = (minIndex < 0) //nothing to compare against yet
			? Double.POSITIVE_INFINITY
			: distances.getDoubleAt(minIndex);
		if (minDist > maxDist)
			words.addItem(token);
		return minIndex;
	}

	/**
	 * Searches {@link #words} for the entry closest to token by {@link #metric}, recording every
	 * distance into {@link #distances}.
	 * @param token the Object to find
	 * @return the Index of the Object most similar to 'token'.
	 */
	public int getMostSimilarItem(final Object token) {
		double minDist = Double.POSITIVE_INFINITY;
		int minIndex = -1;
		for (int i = words.getInt(); --i >= 0; ) {
			final Object word = words.getAt(i); 
			final double  distance = metric.dist(token, word); //symmetric
			if (minDist > distance) {
				minDist = distance; minIndex = i; }
			distances.setAt(i, distance); 
		}
		return minIndex; 
	}
	
}
