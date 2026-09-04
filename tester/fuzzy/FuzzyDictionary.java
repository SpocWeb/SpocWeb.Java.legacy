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
	 * Aggretating (AND-Operation) the Matches is just a Multiplication 
	 *  
	 * @see #similarities stores the similarities for the last Operation for deeper Analysis. 
	 * @param sentence the Sentence to search for. 
	 * @param addToDictionary Flag whether to add the Words of this Sentence to the Dictionary
	 * @param minSimilarity Threshold controlling whether to add this Sentence to the Sentence List.
	 * To make sure the Sentence is always added, set it to Integer.MAX_VALUE 
	 * To make sure the Sentence is never  added, set it to any negative Value
	 * @return the most similar Sentence to the given one. 
	 * 
	 * @param token the Object to find 
	 * @param maxDist the maximum Distance to consider an Object to be distinct 
	 * and to be added to this Dictionary. 
	 * To always add any Object to the Dictionary, set it to 0 
	 * To never  add any Object to the Dictionary, set it to Double.POS_INFINITY
	 * @return the Index of the Object most similar to 'token'. 
	 */
	public int getMostSimilarItem(final Object token, final double maxDist) {
		final int minIndex = -1; 
		final double minDist = distances.getDoubleAt(minIndex); 
		if (minDist > maxDist) 
			words.addItem(token); 
		return minIndex; 
	}
	
	/**
	 * Aggretating (AND-Operation) the Matches is just a Multiplication 
	 *  
	 * @see #similarities stores the similarities for the last Operation for deeper Analysis. 
	 * @param sentence the Sentence to search for. 
	 * @param addToDictionary Flag whether to add the Words of this Sentence to the Dictionary
	 * @param minSimilarity Threshold controlling whether to add this Sentence to the Sentence List.
	 * To make sure the Sentence is always added, set it to Integer.MAX_VALUE 
	 * To make sure the Sentence is never  added, set it to any negative Value
	 * @return the most similar Sentence to the given one. 
	 * 
	 * @param token the Object to find 
	 * @param maxDist the maximum Distance to consider an Object to be distinct 
	 * and to be added to this Dictionary. 
	 * To always add any Object to the Dictionary, set it to 0 
	 * To never  add any Object to the Dictionary, set it to Double.POS_INFINITY
	 * @return the Index of the Object most similar to 'token'. 
	 */
	public int getMostSimilarItem(final Object token) {
		double minDist = 0; 
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
