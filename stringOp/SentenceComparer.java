/*
 * Created on 24.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stringOp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.StringTokenizer;

import math.vector.VectorInt;
import math.vector.VectorString;

/**
 * This Class allows to compare Sentences for Similarity.
 * In parallel it can construct a Dictionary for any Language 
 * whose Alphabet and Separator Characters are known. 
 * 
 * Sentences are first parsed into Words using the given Separator Characters. 
 * Then each Word from each Sentence is compared to every Word from the other Sentence
 * and the Similarities are counted. 
 * Unfortunately this is an O(N*M) Algorithm. 
 * It would be more performant, if a Sequence-independent HashCode could be calculated, 
 * e.g. by XOR-ing all Word HashCodes and counting each Word only once. 
 * The HashCodes can be determined by normalizing the Words into a Dictionary. 
 * 
 * This is a Standard Algorithm to transform O(N*M) into O(N+M) Algorithms: 
 * Define a canonical ("normalized") Form for all alternative Forms ("Curryfication") 
 * transform both Arguments into canonical Form 
 * and compare the canonical Forms.
 * It is used e.g. to sign and/or encrypt XML Documents.  
 * 
 * similar Classes: 
 * @see tester.fuzzy.FuzzySentenceComparator
 * @see tester.fuzzy.FuzzySetComparator
 * @see tester.fuzzy.FuzzyDictionary
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
 * 
 * Alternative Parameter-�bergabe: 
 * 1. Dimension: globale Variable oder Stack-Parameter
 * 2. Dimension: 
 * primitive Parameter-Werte: copied ByVal, also Stack-intensiv, 
 * 	aber keine Seiteneffekte und Thread-safe  
 * schwach typisierte Parameter-Listen: 
 * brauchbar f�r zahlreiche optionale Werte gleichen Typs, 
 * aber besser sind stark typisierte Werte und Parameter-Objekte mit nulls.
 * Parameter-Objekte: handed over ByRef, auch f�r Return-Werte brauchbar.
 * typisch auch zur Verk�rzung der Parameter-Listen. 
 * Alle IMMER notwendigen Objekte und Werte sollten hier gesetzt sein. 
 * Parameter-Listen: Arrays & HashMaps
 * Ein Problem ist z.B. die Auffindung im Array, 
 * aber daf�r kann man dann auch HashMaps verwenden. 
 * 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:42:23Z
 * digest: 84f9788f127743c47ccd4f9a15d4a9f051442ee800c254be367e3fc3bdb6da1c
 * stale: false
 * tags: [code/string_algorithms]
 * concepts: [Sentence Similarity Comparer]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class SentenceComparer {
	
	/** Contains the distinct Words found so far, 
	 * mapped to their unique Index. 
	 * Used to perform rapid Comparisons between Sentences (Sets of Words)
	 */  
	protected final HashMap dictionary = new HashMap(); 
	
	/** Collects the WordSets of the Sentences analyzed so far. 	 */  
	protected final ArrayList sentenceSets = new ArrayList(); 
	
	/** Collects the Sentences analyzed so far. 	 */  
	protected final VectorString sentences = new VectorString(); 
	
	/** Separator Characters to parse the incoming Sentences by	 */
	protected final String separators;
	
	/** Holds the Similarity Counts for the last Operation for later querying. 	 */  
	final public VectorInt similarities = new VectorInt(); 
	
	/** temporary BitSet to perform the OR Operation	 */
	protected final BitSet work = new BitSet(); 
	
	/** Creates a Comparer that parses incoming Sentences into Words using the given Separator Characters. */
	public SentenceComparer(final String _separators) {
		this.separators = _separators; 
	}
	
	/**
	 * This is necessary to test whether a Sentence was found or is new. 
	 * @return the Number of distinct Sentences found so far 
	 */
	public int getNumDistinctSentences() { return sentenceSets.size(); }
	
	/**
	 * Compares the Word Set of sentence against every previously seen Sentence's Word Set by intersecting BitSets, optionally records the Sentence, and returns the Index of the best Match (-1 if there is none).
	 * @see #similarities stores the similarities for the last Operation for deeper Analysis.
	 * @param sentence the Sentence to search for.
	 * @param addToDictionary Flag whether to add the Words of this Sentence to the Dictionary
	 * @param minSimilarity Threshold controlling whether to add this Sentence to the Sentence List.
	 * To make sure the Sentence is always added, set it to Integer.MAX_VALUE
	 * To make sure the Sentence is never  added, set it to any negative Value
	 * @return the most similar Sentence to the given one.
	 */
	public int getMostSimilarSentence(final String sentence, final boolean addToDictionary,
			final int minSimilarity) {
		final BitSet set  = getWordSet(sentence, addToDictionary);
		work.clear(); //set.clone();
		int maxMatch = 0;
		int maxIndex = -1;
		for (int i = sentenceSets.size(); --i >= 0; ) {
			work.or(set); //set the bits, not necessary to clear, since at most set's bit are set!
			work.and((BitSet) sentenceSets.get(i));
			final int numMatch = work.cardinality();
			similarities.setAt(i, numMatch);
			if (maxMatch < numMatch) {
				maxMatch = numMatch;
				maxIndex = i;
			}
		}
		if (maxMatch < minSimilarity) {
			sentenceSets.add(set);
			sentences.addItem(sentence);
		}
		return maxIndex;
	}
	
	/**
	 * Parses the Sentence into Words and sets a Bit for each Word found or added in the Dictionary.
	 * @param sentence the Sentence to analyze for Occurrence of Words.
	 * @param addToDictionary Flag whether to add newly found Words to the Dictionary.
	 * @return the BitSet (instead of e.g. a HashSet, because that can be analyzed faster!
	 */
	public BitSet getWordSet(final String sentence, final boolean addToDictionary) {
		final BitSet ret = new BitSet(dictionary.size() << 1);
		if (sentence == null) {
			return ret; }
		///parse the Sentence into Words
		final StringTokenizer words = new StringTokenizer(sentence, separators);
		while (words.hasMoreTokens()) {
			final String word = words.nextToken();
			///search the Words in the Dictionary, which is an O(1) Operation on a HashMap
			Integer ndx = (Integer) dictionary.get(word);
			if (ndx == null) {
				if (!addToDictionary) {
					continue; } ///unknown Word and not allowed to add it => no Bit to set
				///either add to the Dictionary or just set the Bit
				ndx = new Integer(dictionary.size());
				dictionary.put(word, ndx); }
			ret.set(ndx.intValue()); }
		return ret;
	}
	
	/**
	 * the String hashCode() Function multiplies each Character by 31, nearly 5 Bits, 
	 * so after 6 Characters the hashCode 'nearly' loses it's most significant Bytes. 
	 * @param args
	 */
	public static void main(final String[] args) {
		final int hash = args[0].hashCode(); 
	}
	
}
