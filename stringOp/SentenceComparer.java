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

import math.vector.VectorInt;
import math.vector.VectorString;

/**
 * Title: <p>
 * Description:
 * Purpose:
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
 * Alternative Parameter-Übergabe: 
 * 1. Dimension: globale Variable oder Stack-Parameter
 * 2. Dimension: 
 * primitive Parameter-Werte: copied ByVal, also Stack-intensiv, 
 * 	aber keine Seiteneffekte und Thread-safe  
 * schwach typisierte Parameter-Listen: 
 * brauchbar für zahlreiche optionale Werte gleichen Typs, 
 * aber besser sind stark typisierte Werte und Parameter-Objekte mit nulls.
 * Parameter-Objekte: handed over ByRef, auch für Return-Werte brauchbar.
 * typisch auch zur Verkürzung der Parameter-Listen. 
 * Alle IMMER notwendigen Objekte und Werte sollten hier gesetzt sein. 
 * Parameter-Listen: Arrays & HashMaps
 * Ein Problem ist z.B. die Auffindung im Array, 
 * aber dafür kann man dann auch HashMaps verwenden. 
 * 
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
	
	/**
	 * 
	 */
	public SentenceComparer(final String _separators) {
		this.separators = _separators; 
	}
	
	/**
	 * This is necessary to test whether a Sentence was found or is new. 
	 * @return the Number of distinct Sentences found so far 
	 */
	public int getNumDistinctSentences() { return sentenceSets.size(); }
	
	/**
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
		for (int i = sentenceSets.size(); --i >= 0; ) {
			work.or(set); //set the bits, not necessary to clear, since at most set's bit are set!
			work.and((BitSet) sentenceSets.get(i)); 
			final int numMatch = work.cardinality(); 
			similarities.setAt(i, numMatch); 
			if (maxMatch < numMatch) {
				maxMatch = numMatch;
			}
		}
		if (maxMatch < minSimilarity) {
			sentenceSets.add(set); 
			sentences.addItem(sentence); 
		}
		return -1; 
	}
	
	/**
	 * 
	 * @param sentence the Sentence to analyze for Occurrence of Words. 
	 * @param addToDictionary Flag whether to add newly found Words to the Dictionary. 
	 * @return the BitSet (instead of e.g. a HashSet, because that can be analyzed faster!
	 */
	public BitSet getWordSet(final String sentence, final boolean addToDictionary) {
		final BitSet ret = new BitSet(dictionary.size() << 1); 
		///parse the Sentence into Words
		///search the Words in the Dictionary which is an O(M) Operation
		///(searching for EXACT Matches would  only be an O(1) Operation!!!)
		///either add to the Dictionary or just set the Bit
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
