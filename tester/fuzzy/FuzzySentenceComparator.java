/*
 * Created on 28.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tester.fuzzy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import math.vector.VectorString;
import streamIO.Log;
import stringOp.EditMetric;
import tester.IMetric;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Parses given Strings into Sets of Words and compares these against other Strings. 
 * There are two Types of Searches:
 * @see #getMostKeyWordsSentence(String, double, double[]) Concordance: searches for the Sentence containing most Keywords  
 * @see #getMostSimilar_Sentence(String, double, double[]) Similarity : searches for the best overall Sentence Match 
 * 
 * Design Decisions / Implementation Details:
 * Can either delegate to or be derived from FuzzySetComparator. 
 * characterize the specific Differences to compare these.
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * similar Classes: 
 * @see tester.fuzzy.FuzzySetComparator compares Sets of Objects 
 * instead of Sets of Words. 
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:12:00Z
 * digest: 59b92329d7d7ef7362b0c662fdb4ae7c0212a545c7fc61e28aa731693777499b
 * stale: false
 * tags: [code/string_similarity]
 * concepts: [Fuzzy Sentence Comparator]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class FuzzySentenceComparator 
//extends FuzzySetComparator 
{
	
	/** the Logger for this Class	 */
	private static final Log L = new Log(FuzzySentenceComparator.class); 
	
	/** String containing most Keyboard Characters applicable as Separators	 
	 * Actually it would be better to negate this Criterion 
	 * and define only Characters and Letters as valid Parts of an Identifier. */
	final static public String ALL_SEPARATORS = " ,.?!\"\\+#*'-:;<>|@����$%&/()=?`�"; 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Separator Characters to parse the incoming Sentences by	 */
	final public String separators;
	
	/** Flag to treat Words Case-sensitive	 */
	final public boolean caseSensitive;
	
	/** minimum Word Length, since it doesn't help comparing 1 or 2 Character Words	 */
	final public int minWordLength; 
	
	/** List of String Substitutions to canonicalize Strings e.g. with Umlaut	 */
	final String[][] substitutions; 
	
	/** Reference to the Comparator to use	 */
	final public FuzzySetComparator comparator; 
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing Constructor
	 * defaulting all Parameters for german Sentences. 
	 */
	public FuzzySentenceComparator() {
		this(new EditMetric(true, 3, null, 
				 EditMetric.KEYBOARD_LAYOUT_GERMAN), //EditMetric.GET_METRIC_GERMAN(), 
				3.0, false, 
				 EditMetric.GERMAN_SUBSTITUTIONS, ALL_SEPARATORS, 4);
	} //4:2/5:s ; 3:2/11:2100s ; 1,2,3:1/10:1200s,, ; 0.5:1/11:760; 0.1,0.2:1/11:200s,360s; 0.05,0.01:1/12:120s,60s; 
	
	/**
	 * Initializing Constructor
	 * @param _metric the Metric to use for comparing Words
	 * @param _separators the Separator Characters for Words 
	 */
	public FuzzySentenceComparator(final IMetric _metric, final double _minRelDistanceToAdd, 
			final boolean _caseSensitive, final String[][] _substitutions,  
			final String _separators, final int _minWordLength) {
		//super(_metric, _minRelDistanceToAdd);
		this.comparator = new FuzzySetComparator(_metric, _minRelDistanceToAdd);
		this.caseSensitive = _caseSensitive; 
		this.substitutions = _substitutions; 
		this.minWordLength = _minWordLength; 
		this.separators = _separators; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * splits the given Sentence by the Separators
	 * and removes Strings shorter than minWordLength
	 * Additionally all partial Strings are preprocessed, 
	 * which greatly saves Conversion downstream! 
	 * @param sentence the Sentence to split
	 * @return a truncated Array containing only the longer Words
	 */
	public String[] splitSentence(final String sentence) 
	{
		final String[] wordSet = VectorString.SPLIT(sentence, separators);
		//reducing the Number of Words can speed up the O(S*N*M*w*v) Algorithm considerably! 
		//nice little Algorithm to compress an Array with Nulls in it! 
		int length = wordSet.length; 
		for (int i = -1; ++i < length;) {
			if (wordSet[i].length() < minWordLength) {
				wordSet[i] = wordSet[--length]; --i; }
		}
		final String[] compressed = VectorString.COPY(wordSet, length); 
		if ((substitutions != null) && 
			(substitutions.length > 0)) {
			final StringBuffer buffer = new StringBuffer(); 
			for(int i =compressed.length; --i >= 0; ) {
				buffer.setLength(0); buffer.append(compressed[i]); 
				EditMetric.SUBSTITUTE(buffer, substitutions); 
				compressed[i] = buffer.toString();
			}
		}
		if (!caseSensitive) 
			for(int i =compressed.length; --i >= 0; )
				compressed[i] = compressed[i].toUpperCase(); 
		return compressed;
	}
	
	/**
	 * 	Method to initialize the Sentences to compare to 
	 * without checking them for Similarities. 
	 * @param sentence the Sentence to add 
	 * @return the Number of the Sentence for later Identification (== #Sentences added so far)
	 */
	public int addSentence(final String sentence) { 
		comparator.sentences.addItem(splitSentence(sentence));
		return comparator.sentences.getInt() -1;
	}
	
	/**
	 * Concordance: 
	 * returns the Index of the sentences with the closest Match in the given Keyword Set
	 * 
	 * @param sentence the List of Words to search for. 
	 * @param maxRelDistance Threshold to skip testing and to speed up searching
	 * @param distances Output Parameter to get the actual Distances for MultiSelect. 
	 * @return the Index of the closest Match
	 */
	public int getMostKeyWordsSentence(String sentence, double maxRelDistance, double[] distances) {
		return comparator.getMostKeysSet(splitSentence(sentence), maxRelDistance, distances); }
	
	/**
	 * Similarity: 
	 * returns the Index of the closest Match in the sentences tested or added previously
	 * @param test the Sentence to search for. 
	 * @param minRelDistance Threshold controlling whether to add this Sentence to the Sentence List.
	 * To make sure the Sentence is always added, set it to Integer.MAX_VALUE 
	 * To make sure the Sentence is never  added, set it to any negative Value
	 * @return the Index of the most similar Sentence to the given one. 
	 */
	public int getMostSimilar_Sentence(final String sentence) {
		return comparator.getMostSimilarSet(splitSentence(sentence)); }
	
	/**
	 * Similarity: 
	 * returns the Index of the closest Match in the sentences tested or added previously
	 * @param test the Sentence to search for. 
	 * @param minRelDistance Threshold controlling whether to add this Sentence to the Sentence List.
	 * To make sure the Sentence is always added, set it to Integer.MAX_VALUE 
	 * To make sure the Sentence is never  added, set it to any negative Value
	 * @param distances optional (null allowed) Array to 
	 * either collect the Distances e.g. for applying Correlations 
	 * or to return the minimum / maximum Distance (when smaller than list)  
	 * @return the Index of the most similar Sentence to the given one. 
	 */
	public int getMostSimilar_Sentence(final String sentence, 
			final double minRelDistance, final double[] distances) {
		return comparator.getMostSimilarSet(splitSentence(sentence), minRelDistance, distances); 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reads characters from fis into buf up to the next separator character or end of stream.
	 * @param fis the stream to read from
	 * @param buf reused buffer that receives the characters read, cleared on entry
	 * @param sep the separator character terminating one record
	 * @return the separator character, or -1 when the stream ended first
	 */
	public static int read(final InputStream fis, final StringBuffer buf, final char sep) throws Exception {
		buf.setLength(0);
		// TODO: LOGIC: the loop condition compares against the hardcoded literal '?' instead of
		// the sep parameter, so a caller passing a different separator character never sees the
		// loop terminate on it - it only stops on this hardcoded character or end of stream.
		for(int curr; '�' !=(curr = fis.read());) {
			if (curr == -1)
				return curr;
			buf.append((char) curr);
		}
		return sep;
	}

	/**
	 * Reads sentences from the file named by args[0] and logs, for each, the closest previously
	 * seen sentence found via {@link #getMostSimilar_Sentence(String)}.
	 * @param args args[0] is the path of the file containing the sentences to compare
	 */
	public static void main(final String[] args) throws Exception {
		final FuzzySentenceComparator comp = new FuzzySentenceComparator(); 
		L.n(new Date()); 
		final char sep = '�'; 
		final StringBuffer buf = new StringBuffer(); 
		final FileInputStream fis = new FileInputStream(args[0]);
		int count = 0; 
		int total = 0; 
		for(int curr; -1 != (curr = read(fis, buf, sep));) {
			++total; 
			curr = read(fis, buf, sep); 
			final String str = buf.toString(); 
			final int similar = comp.getMostSimilar_Sentence(str);
			if (similar >= 0) {
				++count; 
				L.n(str);
				L.n().l(similar).l(count).l('/').l(total).l(comp.comparator.sentences.getAt(similar));
			}
		}
		L.n(new Date()); 
	}
	
}
