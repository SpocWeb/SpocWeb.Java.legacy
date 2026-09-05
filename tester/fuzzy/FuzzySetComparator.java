/*
 * Created on 24.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tester.fuzzy;

import math.vector.VectorObject;
import tester.IMetric;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * This Class allows to compare Sets (here: Arrays) of Words for Similarity. 
 * In parallel it can construct a Dictionary for any Language 
 * whose Alphabet and Separator Characters are known. 
 * 
 * There are two Types of Searches:
 * @see #getMostKeysSet   (Object[], double, double[]) Concordance: searches for the Set containing most Keys 
 * @see #getMostSimilarSet(Object[], double, double[]) Similarity : searches for the best overall Set Match 
 * 
 * Sentences are first parsed into Words using the given Separator Characters. 
 * Then each Word from each Sentence is compared to every Word from the other Sentence
 * and the Similarities are counted. 
 * Unfortunately this is an O(N*M) Algorithm per Sentence 
 * (not considering the Object Comparison 
 *  which may already be an expensive Operation in it's own!).  
 * It would be more performant, if a Sequence-independent HashCode could be calculated, 
 * e.g. by XOR-ing all Word HashCodes and counting each Word only once. 
 * The HashCodes can be determined by normalizing the Words into a Dictionary. 
 * 
 * This is a Standard Algorithm to transform O(N*M) into O(N+M) Algorithms: 
 * Define a canonical ("normalized") Form for all alternative Forms,  
 * transform both Arguments into canonical Form 
 * and compare the canonical Forms.
 * It is used e.g. to sign and/or encrypt XML Documents.  
 * 
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:33Z
 * digest: c53ec825e1e8bb7e44485065a0796c0334943bd6a38124d2b3953a5e61868216
 * stale: false
 * tags: [code/similarity_matching]
 * concepts: [Fuzzy Set Comparator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class FuzzySetComparator {
	
	/**
	 * returns the Sum across list1 of the Minimum or Maximum Distances
	 * weighed by the List Length. 
	 * 
	 * This corresponds to determining the total "Similarity" between Sentences, 
	 * It does NOT ignore Words in the one Sentence that don't exist in the other! 
	 * @see #SUM_MIN_MAX_DISTANCES(IMetric, boolean, Object[], Object[]) 
	 * which performs a Keyword Search 
	 * 
	 * @param metric the Metric to use. 
	 * @param maxRelDistance the maximum Distance (relative to Object Length). 
	 * When exceeded, summing is stopped for Performance Optimization. 
	 * @param min Flag whether to sum up the Minimum or Maximum 
	 * @param list1 the List to loop and sum up over 
	 * @param list2 the List to find the Minimum or Maximum of
	 * @return the Sum across list1 of the Minimum or Maximum Distances 
	 * to the Objects of list2 
	 */
	final static public double SUM_MIN_DISTANCES(
			final IMetric metric, final double maxRelDistance, 
			final Object[] list1, final Object[] list2) {
		if (list1.length > list2.length) //only this makes the Distance symmetric again
			/// Relevant only if the first List is small e.g. 1 Word 
			/// and the second List is large AND the Word occurs in List2: 
			/// then searching List1 in List2 is very fast and gives 0 Distance (ideal Match, also good for Keyword Search), 
			/// whereas searching List2 in List1 is slower and gives nearly maximum Distance. 
			return SUM_MIN_MAX_DISTANCES(metric, true, list1, list2, list1.length*maxRelDistance)/list1.length; 
			return SUM_MIN_MAX_DISTANCES(metric, true, list2, list1, list2.length*maxRelDistance)/list2.length; 
	}
	
	/**
	 * returns the Sum across list1 of the Minimum or Maximum Distances
	 * 
	 * This is a Keyword-Search, not a total Comparison of two Sentences. 
	 * @see #SUM_MIN_DISTANCES(IMetric, double, Object[], Object[]) 
	 * which performs a total Search for ALL Differences between both Lists.  
	 * 
	 * @param metric the Metric to use. 
	 * @param min Flag whether to sum up the Minimum or Maximum 
	 * @param keyWords the List to loop and sum up over 
	 * @param sentence the List to find the Minimum or Maximum of
	 * @return the Sum across list1 of the Minimum or Maximum Distances 
	 * to the Objects of list2 
	 */
	private static final double SUM_MIN_MAX_DISTANCES(
			final IMetric metric, final boolean min, 
			final Object[] keyWords, final Object[] sentence) {
		return SUM_MIN_MAX_DISTANCES(metric, min, keyWords, sentence, Double.POSITIVE_INFINITY); }
	
	/**
	 * returns the Sum across list1 of the Minimum or Maximum Distances
	 * This Sum is not symmetric: 
	 * when list1 is short and list2 is long , the result is a brief sum of small amounts
	 * when list1 is long  and list2 is short, the result is a long  sum of large amounts
	 * 
	 * This is a Keyword-Search, not a total Comparison of two Sentences. 
	 * @see #SUM_MIN_DISTANCES(IMetric, double, Object[], Object[]) 
	 * which performs a total Search for ALL Differences between both Lists.  
	 * 
	 * @param metric the Metric to use. 
	 * @param min Flag whether to sum up the Minimum or Maximum 
	 * @param keyWords the List to loop and sum up over 
	 * @param sentence the List to find the Minimum or Maximum of
	 * @param maxRelDistance the maximum Distance (relative to Object Length). 
	 * When exceeded, summing is stopped for Performance Optimization. 
	 * @return the Sum across list1 of the Minimum or Maximum Distances 
	 * to the Objects of list2 
	 */
	private static final double SUM_MIN_MAX_DISTANCES(
			final IMetric metric, final boolean min, 
			final Object[] keyWords, final Object[] sentence, final double maxDistance) {
		final double[] minDist = new double[1]; 
		double sumDist = 0; 
		for(int i = keyWords.length; --i >= 0;) {
			MIN_MAX_DISTANCE(metric, sentence, keyWords[i], min, minDist);
			sumDist += minDist[0]; 
			if (min && (sumDist > maxDistance))
				return sumDist; //stop early, since larger than the Threshold anyway.  
		}
		return sumDist; ///list1.length; //the Length must not consider brief Fill Words that match anyway like 'a(ny)' and 'a(ll)'! 
	}
	
	/**
	 * return the index of the closest or farthest Object
	 * also fills in the Distance of all or the found Object into the passed Array.  
	 * @param metric The Metric to apply. 
	 * @param sentence List of Objects to compare to 
	 * @param keyWord Object to compare to all Objects from the List
	 * @param min Flag whether to search for the Minimum or the Maximum Distance 
	 * @param _distances optional (null allowed) Array to 
	 * either collect the Distances e.g. for applying Correlations 
	 * or to return the minimum / maximum Distance (when smaller than list)  
	 * @return the index of the closest or farthest Object 
	 */
	public static int MIN_MAX_DISTANCE(final IMetric metric, final Object[] sentence, 
			final Object keyWord, final boolean min, final double[] _distances) {
		final double[] distances = (
				(_distances != null) && 
				(_distances.length >= 1)) 
				?_distances :  null; 
		int minIndex = -1; 
		double minVal = min 
			? Double.POSITIVE_INFINITY
			: Double.NEGATIVE_INFINITY; 
		for(int j  = Math.min((distances != null) 
				? distances.length 
				: sentence .length, sentence.length); 
		  --j >= 0;) {
			final Object word = sentence[j]; 
			final double dist = metric.dist(keyWord, word);
			if (distances != null)
				distances[j] = dist; 
			if((minVal > dist) == min) {
				minVal = dist; minIndex = j; }
		}
		if( (_distances != null) &&
			( distances == null) &&
			(_distances.length == 1))
			 _distances[0] = minVal; 
		return minIndex;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * using a Dictionary normalizes the Vocabulary, 
	 * but otherwise introduces only Overhead: 
	 * Instead of an O( N*M) Comparison of N Words with M Words 
	 * you now need  O((N+M)*K) Comparisons 
	 * with K being much larger than either N or M! 
	 */
	//protected final FuzzyDictionary dictionary; 
	
	/** the Metric to use for comparing Sentences	 */
	final public IMetric metric; 
	
	/** the minimum relative Distance to use for adding tested Objects to the Reference-Set.
	 * Objects father to any existing Object are added. 
	 * Objects closer to an existing Object are returned by Index. 
	 * To make sure any Object is always added, set it to 0 or any negative Value 
	 * To make sure any Object is never  added, set it to Double.POSITIVE_INFINITY 
	 */
	final public double minRelDistanceToAdd; 
	
	/** Collects the Sentences analyzed so far. 
	 * Design Decisions: 
	 * maintaining the Sentences in an Array is as good as anything else. 
	 * It doesn't pay off to implement a custom Mapping / Index here, 
	 * since the Mapping back can be performed by a simple Array Lookup e.g.: 
	 * Object[getMostSimilarSet(final Object[] test)]
	 */ 
	protected final VectorObject sentences = new VectorObject(); 
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing Constructor
	 * @param _metric the Metric to use for comparing Words
	 * @param _minRelDistanceToAdd 
	 */
	public FuzzySetComparator(final IMetric _metric, final double _minRelDistanceToAdd) {
		this.minRelDistanceToAdd = _minRelDistanceToAdd; 
		this.metric = _metric; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Similarity: 
	 * returns the Index of the closest Match in the Objects tested or added previously
	 * @param test the Object to search for. 
	 * @param minRelDistanceToAdd Threshold controlling whether to add this Object to the Object List.
	 * To make sure the Object is always added, set it to Integer.MAX_VALUE 
	 * To make sure the Object is never  added, set it to any negative Value
	 * @return the Index of the most similar Object to the given one,
	 * but only if it is more similar than minRelDistance; 
	 * -1 otherwise. 
	 */
	public int getMostSimilarSet(final Object[] test) {
		return getMostSimilarSet(test, minRelDistanceToAdd, null); }
	
	/** 
	 * Similarity: 
	 * returns the Index of the closest Match in the sentences tested or added previously
	 * 
	 * @param test the Object to search for. 
	 * @param distances optional (null allowed) Array of Distances to get all matches evaluated
	 * @param minRelDistance Threshold controlling whether to add this Sentence to the Sentence List.
	 * To make sure the Object is always added, set it to Integer.MAX_VALUE 
	 * To make sure the Object is never  added, set it to any negative Value
	 * @return the Index of the closest Match
	 */
	public int getMostSimilarSet(final Object[] test, final double minRelDistance, final double[] distances) {
		return getMinMaxDistSet(test, minRelDistance, distances, false, true); }
	
	/** Minimum Number of Objects in the Sentence to eliminate empty ones 	 */
	public char MinNumWords = 1; 
	
	/** 
	 * Concordance: 
	 * returns the Index of the sentences with the closest Match in the given Keyword Set 
	 * 
	 * @param _KeyWords the Objects to search for.  
	 * @param maxRelDistance Threshold to skip testing and to speed up searching
	 * @param distances optional (null allowed) to either collect the Distances 
	 * and to control the maximum Number of Sentences tested 
	 * or to return the minimum / maximum Value (of the closest Match) when of Length 1
	 * @return the Index of the closest / farthest Match 
	 */
	public int getMostKeysSet(final Object[] _KeyWords, final double maxRelDistance, final double[] distances) {
		return getMinMaxDistSet(_KeyWords, maxRelDistance, distances, true, true); 
	}
	
	/**
	 * returns the Index of the closest Match in the Objects tested or added previously
	 * @param _KeyWords the Object to search for. 
	 * @param maxRelDistance Threshold controlling whether 
	 * to return the Index of (one of) the closest Object or 
	 * to return -1 when the Distance is larger. (saves Evaluation of distances)
	 * @param distances optional (null allowed) Array to 
	 * either collect the Distances e.g. for applying Correlations 
	 * or to return the minimum / maximum Distance (when smaller than list)  
	 * @return the Index of the most similar Object to the given one. 
	 */
	public int getMinMaxDistSet(final Object[] _KeyWords, final double maxRelDistance, final double[] distances, 
			boolean keyWordSearch, boolean min) {
		int minIndex = -1; 
		double minDist = min 
			? Double.POSITIVE_INFINITY 
			: Double.NEGATIVE_INFINITY; 
		for (int i = sentences.getInt(); --i >= 0; ) {
			final Object[] sentence = (Object[]) sentences.getAt(i); 
			final double sumDist; 
			if (sentence.length < MinNumWords)
				sumDist = min
				? Double.POSITIVE_INFINITY
				: Double.NEGATIVE_INFINITY; 
			else {
				sumDist = keyWordSearch 
					? SUM_MIN_MAX_DISTANCES(metric, min, _KeyWords, sentence, _KeyWords.length*maxRelDistance)/_KeyWords.length 
					: SUM_MIN_DISTANCES(metric, maxRelDistance, _KeyWords, sentence); 
				if((minDist > sumDist) == min) { //similarity must be normed...
					minDist = sumDist; minIndex = i; } //...with the Number of Elements
			}
			if (distances != null) 
				distances[i] = sumDist; 
		} 
		if (minDist > minRelDistanceToAdd)
			sentences.addItem(_KeyWords); 
		if (minDist < maxRelDistance)
			return minIndex;
		return -1; 
	}
	
	///get the similarities from the Dictionary, which is an O(M) Operation
	///(searching for EXACT Matches would  only be an O(1) Operation with Hashing 
	/// or an O(Log(N)) Operation for sorted Structures!!!)
	/**
	 * the String hashCode() Function multiplies each Character by 31, nearly 5 Bits, 
	 * so after 6 Characters the hashCode 'nearly' loses it's most significant Bytes. 
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		final int hash = args[0].hashCode(); 
	}
	
}
