/*
 * Created on 22.11.2005
 *
 */
package stringOp;

import streamIO.Assert;
import streamIO.Log;
import streamIO.integer.random.RandomQuick;
import tester.IMetric;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * This Class encapsulates the Algorithm for calculating an Extension of 
 * the double Levenshtein (Edit-) distance between two given Strings.  
 * Regular Expressions can capture some individual Variations and mistypings per Word, 
 * but not the general Strategy of 'mistyping'. 
 * Using a neuronal Net allows to rapidly match any Input to Output, 
 * but it requires Training and special preprocessing of the Input for optimum Results. 
 * 
 * This Algorithm is most similar to calculating the Longest Common Sequences (LCSs) 
 * as implemented in @see streamIO.diffPatch.Differ 
 * This is a quite expensive O(N*M) Runtime and O(min(N,M)) Memory Operation, 
 * so one should always consider other more direct Approaches:
 * Comparing 2 Strings for exact Match is a fast-failing O(min(N,M)) Operation! 
 * First calculating a HashCode for each Word in O(N) and then hashing for it, 
 * allows to compare k Words in O(N+k) instead of O(N*M*k) Operations. 
 * Since mistyping should be a fairly rare Event, 
 * one should first search for exact Matches and only then for fuzzy Matches. 
 * For exact Matches, a 1-dim Scalar Metric is possible, 
 * which e.g. weighs the core of a Word higher than it's tails. 
 * The Problem is how to define the 'core' of a word, 
 * so that it is tolerant of both Prefixes and Suffixes. 
 * A Solution for this is to maintain a manually controlled Dictionary
 * with only Word "Stems" and to weigh Prefixes and Suffixes very low. 
 * Simple Prefix OR(!) Suffix Tolerance is easy: just calculate a HashCode 
 * by summing up and shifting the same Number of Characters per Word.
 * The Problem is that this HashCode puts very high weight on the first Character 
 * and lower Weights to the following (which is good for ignoring Suffixes). 
 * A better HashCode would weigh each Character (of the Stem) similarly, 
 * concentrating on the 5 Bits relevant for Latin Characters. 
 * and use the available Bits to distribute the Character's Bits evenly. 
 * Or just ignore Words starting with the wrong Character(s). 
 * 
 * Yet another alternative is to use one of the 32 Bits for each of the Characters
 * occurring in a Word and to compare the Word by just one Integer XOR Operation.
 * But this 27 or 32 dimensional Space can still not be flattened to 1D to search for Neighbors.   
 * 
 * Design Decisions / Implementation Details:
 * It is not Thread-safe, due to internal WorkSpaces for Performance Optimization. 
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * similar Classes: 
 * @see streamIO.diffPatch.DifferInt uses a most similar Algorithm 
 * to determine the longest matching Sequence of int or Objects  
 * @see streamIO.diffPatch.DifferObject uses a most similar Algorithm 
 * to determine the longest matching Sequence of int or Objects  
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
final public class EditMetric 
implements IMetric 
{	
	/** Logger Instance for this Class	 */
	private static final Log L = new Log(EditMetric.class); 

	/** Sample Instance, not Thread-safe */
	final static public EditMetric GET_METRIC_GERMAN() { 
		return new EditMetric(false, 3, GERMAN_SUBSTITUTIONS, KEYBOARD_LAYOUT_GERMAN); }
	
	/** Sample Instance */
	final static public EditMetric GET_METRIC_GERMAN_10() { 
		return new EditMetric(false, 3, GERMAN_SUBSTITUTIONS, KEYBOARD_LAYOUT_GERMAN_10); }
	
	/** Character Substitutions for Normalization of german Strings:
	 * replace any of the left Characters with the right String	 */
	final static public String[][] GERMAN_SUBSTITUTIONS = {
			{"ä", "ae"}, 
			{"æ", "ae"}, 
			{"ö", "oe"}, 
			{"ø", "Oe"}, 
			{"œ", "Oe"}, 
			{"ü", "ue"}, 
			{"ß", "ss"}, 
			{"Ä", "Ae"}, 
			{"Æ", "Ae"}, 
			{"Ö", "Oe"}, 
			{"Ø", "Oe"}, 
			{"Ü", "Ue"}, 
	}; 
	
	/** Character Substitution for Normalization of other Characters in Latin-1 Strings
	 * replace any of the right Characters with the left Character.	 */
	final static public String[][] LATIN_SUBSTITUTIONS = {
			{"a", "áâàã"}, 
			{"c", "ç", }, 
			{"d", "ð"}, 
			{"e", "éêèë"}, 
			{"i", "íîìï"}, 
			{"n", "ñ"}, 
			{"o", "óôòõ"}, 
			{"s", "š"}, 
			{"t", "Þ"}, 
			{"u", "úûù"}, 
			{"y", "ýÿ"}, 
			{"z", "ž"}, 
			{"A", "ÁÂÀÃ"}, 
			{"C", "Ç"}, 
			{"D", "Ð"}, 
			{"E", "ÉÊÈË"}, 
			{"I", "ÍÎÌÏ"}, 
			{"N", "Ñ"}, 
			{"O", "ÓÔÒõ"}, 
			{"S", "Š"}, 
			{"T", "þ"}, 
			{"U", "ÚÛÙ"}, 
			{"Y", "ÝŸ"}, 
			{"Z", "Ž"}, 
	}; 
	
	/** Character Sound Similarities for Normalization of Strings 
	 * against typical Errors when only hearing an unknown Word. 
	 * Replace any of the right Characters with the left Character.	 */
	final static public String[][] CHAR_SIMILARITIES = {
			{"c", "kzq"}, //
		//	{"q", "k"}, 
			{"v", "fw"}, 
			{"j", "i"}, 
			{"n", "m"}, 
			{"ks", "x"}, 
			{"y", "ijü"}, 
			{"z", "s"}, 
			{"ä", "e"}, 
			{"å", "o"}, 
		/*	{"d", "t"}, //Linguistische Lautverschiebungen  
			{"p", "b"}, 
			{"l", "r"}, 
			{"b", "w"}, */ 
	}; 
	
	/** Strings consisting of the Keyboard Neighbors, 
	 * since it is typical to mistype by one Position
	 * Keyboard Keys have 6 nearest Neighbors, 
	 * modelling an inexperienced 2 Finger Typist. 
	 */
	final static public String[] KEYBOARD_LAYOUT_GERMAN = {
			"^1234567890ß", 
			" QWERTZUIOPÜ+", 
			" ASDFGHJKLÖÄ#", 
			"<YXCVBNM,.-", 
	}; 
	
	/** more restrictive Keyboard Layout 
	 * allowing only horizontal Glitches, since 
	 * with experienced 10 Finger Typists, 
	 * typically only the left and the right Key are frequently chosen. 
	 */
	final static public String[] KEYBOARD_LAYOUT_GERMAN_10 = {
			KEYBOARD_LAYOUT_GERMAN[0], "", 
			KEYBOARD_LAYOUT_GERMAN[1], "", 
			KEYBOARD_LAYOUT_GERMAN[2], "", 
			KEYBOARD_LAYOUT_GERMAN[3] 
	};
	
	/**
	 * Helper Method to rotate the Metric and calculate the hexagonal Neighbors of any Key
	 * This is a good example of transforming rectangular Locations
	 * into a hexagonal/triangular(in 3D: Tetrahedron) Topology!
	 * @param keyboardLayout row-wise 2D Array of Keys
	 * @return the x and y Coordinates of each Character in rotated Coordinates 
	 * to determine nearest Distances in hexagonal Arrangements. 
	 */
	final static public int[][] CALC_KEYBOARD_INDEX(final String[] keyboardLayout){
		if (keyboardLayout == null)
			return null; 
		int[][] ret = null; 
		int maxChar = -1; 
		for(boolean finish = false;;) {
			for(int i = keyboardLayout.length; --i >= 0;) {
				final String keys = keyboardLayout[i]; 
				for(int j = keys.length(); --j >= 0;) {
					final char curr = keys.charAt(j);
					if (finish) {
						final int[] index = new int[2]; 
						ret[curr] = index; //rotate the Axes by 45° 
						index[0] = i+j; 
						index[1] = i-j; 
					} else 
					if (maxChar < curr) 
						maxChar = curr; 
				}
			}
			if (finish)
				break; 
			finish = true; 
			ret = new int[maxChar+1][]; 
		} 
		return ret; 
	}
	
	/**
	 * Helper Method to rotate the Metric and calculate the hexagonal Neighbors of any Key
	 * This is a simple example of transforming rectangular Locations
	 * into a hexagonal/triangular(in 3D: Tetrahedron) Topology!
	 * The proper Rotation is done via the Tetrahedron Matrix 
	 * @param nonRotLayout row-wise 2D Array of Keys
	 * @return the x and y Coordinates of each Character in rotated Coordinates 
	 * to determine nearest Distances in hexagonal Arrangements. 
	 */
	final static public int[][] ROT_INDEX(final int[][] nonRotLayout){
		if (nonRotLayout == null)
			return null; 
		int[][] ret = null; 
		int maxChar = -1; 
		for(boolean finish = false;;) {
			for(int i = nonRotLayout.length; --i >= 0;) {
				final int[] keys = nonRotLayout[i]; 
				for(int j = keys.length; --j >= 0;) {
					final int curr = keys[j];
					if (finish) { //2nd loop: rotate
						final int[] index = new int[2]; 
						ret[curr] = index; //rotate the Axes by 45° 
						index[0] = i+j; 
						index[1] = i-j; 
					} else //1st Loop: determine the max. Index
					if (maxChar < curr) 
						maxChar = curr; 
				}
			}
			if (finish)
				break; 
			finish = true; 
			ret = new int[maxChar+1][]; 
		} 
		return ret; 
	}
	
	/**
	 * converts any given Object into a StringBuffer Instance
	 * @param _a the Object to convert into a StringBuffer
	 * @param _b an optional StringBuffer to hold the Contents of _a
	 * @return either _a or _b
	 */
	public static StringBuffer TO_STRING_BUFFER(final Object _a, final StringBuffer _b) {
		if (_a  == null)
			return null; 
		if (_a instanceof StringBuffer) 
			return  (StringBuffer) _a;
		_b.setLength(0); _b.append(_a); 
		return _b;
	}
	
	/**
	 * replaces Strings by Substitutes to normalize the String. 
	 * @param _a the StringBuffer to replace the Strings in
	 * @param substitutions the List of String Substitutions 
	 * @return the Number of substitutions performed. 
	 */
	final static public int SUBSTITUTE(final StringBuffer _a, final String[][] substitutions) {
		if (substitutions == null)
			return 0; 
		int ret  = 0; 
		for(int i = substitutions.length; --i >= 0; ) {
			final String[] subst = substitutions[i]; 
			for(;;) {
				final int pos = _a.indexOf(subst[0]); 
				if (pos <= 0)
					break;
				++ret; 
				_a.replace(pos, pos+subst[0].length(), subst[1]); 
			}
		}
		return ret; 
	}
	
	/** generates a random String of the given Length 
	 * @param length desired Length
	 */
	final static public String RANDOM(final int length) {
		return RANDOM(length, (char)('Z'+1), 'A'); 
	}
	
	/** generates a random String of the given Length 
	 * @param length desired Length
	 * @param maxChar maximum Character Value + 1
	 * @param minChar minimum Character Value 
	 * @return
	 */
	final static public String RANDOM(final int length, final char maxChar, final char minChar) {
		final char[] ret = new char[length]; 
		final int d = maxChar - minChar; 
		for(int i = length; --i >= 0;)
			ret[i] = (char) (minChar + RandomQuick.NEXT_INT(d)); 
		return new String(ret); 
	}
	
	/** returns the minimum of the three values given
	 * 
	 * @param a first  Value
	 * @param b second Value
	 * @param c third  Value
	 * @return the minimum of the three values given
	 */
	final static public int MIN(final int a, final int b, final int c) {
		if (a < b) {
			if (a < c)
				return a; 
			return c; 
		} //a >= b
		if (b < c)
			return b; 
		return c; 
	}
	
	/**
	 * return the double Levenshtein (Edit-) distance between the given Strings. 
	 * swapped or doubled Characters are counted only single.  
	 * This Measure has to be put in relation to the String Length, 
	 * since it is more probable to mistype in a longer Word. 
	 * An appropriate Limit for Similarity is the Maximum of 3 or 4 
	 * and the Word-Length divided by 3. 
	 * To compare Texts for Keywords, you have to parse by Spaces and Colons. 
	 * To skip frequent Words, either count their Frequency 
	 * or just skip all Words with less than 5 Characters 
	 * (since these frequent Words are typically brief, due to Evolution) 
	 *  
	 * @param a one String
	 * @param b the other String
	 * @return the double Levenshtein (Edit-) distance between the given Strings
	 */
	final static public int DIST(final StringBuffer a, final StringBuffer b) {
		return DIST(a, b, false); }
	
	/**
	 * return the double Levenshtein (Edit-) distance between the given Strings. 
	 * swapped or doubled Characters are counted only single.  
	 * This Measure has to be put in relation to the String Length, 
	 * since it is more probable to mistype in a longer Word. 
	 * An appropriate Limit for Similarity is the Maximum of 3 or 4 
	 * and the Word-Length divided by 3. 
	 * To compare Texts for Keywords, you have to parse by Spaces and Colons. 
	 * To skip frequent Words, either count their Frequency 
	 * or just skip all Words with less than 5 Characters 
	 * (since these frequent Words are typically brief, due to Evolution) 
	 *  
	 * @param a one String
	 * @param b the other String
	 * @return the double Levenshtein (Edit-) distance between the given Strings
	 */
	final static public int DIST(final StringBuffer a, final StringBuffer b, final boolean caseSensitive) {
		return DIST(a, b, 0, caseSensitive, null, null); }
	
	/**
	 * return the double Levenshtein (Edit-) distance between the given Strings. 
	 * swapped or doubled Characters are counted only single.  
	 * This Measure has to be put in relation to the String Length, 
	 * since it is more probable to mistype in a longer Word. 
	 * An appropriate Limit for Similarity is the Maximum of 3 or 4 
	 * and the Word-Length divided by 3. 
	 * To compare Texts for Keywords, you have to parse by Spaces and Colons. 
	 * To skip frequent Words, either count their Frequency 
	 * or just skip all Words with less than 5 Characters 
	 * (since these frequent Words are typically brief, due to Evolution) 
	 * To handle German plural Forms using Umlaut, just preprocess the Text 
	 * and replace all 'ä' with 'ae'. 
	 * To handle regular Conjugation and Declination, 
	 * the last 2 or 3 Characters can either be weighed differently 
	 * or simply be truncated (which also gets rid of all brief Adverbs and Pronouns). 
	 * 
	 * @param a one String
	 * @param b the other String
	 * @param caseSensitive Flag whether the Text is treated case-sensitive
	 * @param keyboardIndex an Index for the Keys: all Keys within a Distance 1 are considered as Glitches. 
	 * @return the double Levenshtein (Edit-) distance between the given Strings
	 */
	final static public int DIST(final StringBuffer a, final StringBuffer b, final int numPreSufFix, final boolean caseSensitive, final int[][] keyboardIndex, final int[] workSpace) {
		if (a == b)
			return 0; 
		if (a == null)
			return b.length(); 
		if (b == null)
			return a.length(); 
		if (a.length() == 0) 
		  return b.length(); 
		if (b.length() == 0) 
		  return a.length(); 
		//Initialization 
		final int[] d = (
				(workSpace != null) && 
				(workSpace.length >= b.length())) 
				?workSpace : new int[b.length()];
		for (int j = b.length(); --j >= 0; ) 
			d[j] = j+1; 
		for (int i = 0; i < a.length(); i++) {
			final char a_i = caseSensitive ? a.charAt(i) : Character.toUpperCase(a.charAt(i)); 
			final int[] idi = (
					(keyboardIndex != null) && 
					(keyboardIndex.length > a_i)) 
					?keyboardIndex[a_i] : null;
			int d_i = i; 
			int d_j = i+1; 
			for (int j = 0; j < b.length(); j++) {
				final char b_j = caseSensitive ? b.charAt(j) : Character.toUpperCase(b.charAt(j));
				final int[] idj = (
						(keyboardIndex != null) && 
						(keyboardIndex.length > b_j)) 
						?keyboardIndex[b_j] : null;
				final int cost; 
				if (a_i == b_j) 
					cost = 0; 
				else { //default for full Distance when no Match
					if ((i >= b.length()-numPreSufFix) || (i < numPreSufFix) ||
						(j >= a.length()-numPreSufFix) || (j < numPreSufFix)) //Characters outside the Bounds count only 1 each
						cost = 1; else 
					if (((j+1 < b.length()) && (a_i == b.charAt(j+1))) ||
						((j   > 0         ) && (a_i == b.charAt(j-1))))
						cost = 2; else //Characters swapped or double 
					if ((idi != null) && 
						(idj != null)) {
						if ((Math.abs(idi[0]-idj[0]) < 2) &&
							(Math.abs(idi[1]-idj[1]) < 3))
							cost = 2; else //Mistyped Key
							cost = 4; 
					} else  cost = 4; //
				}
				final int swap = d[j]; 
				d[j] = d_j = MIN(d[j]+1, d_j+1, d_i + cost);
				d_i = swap; 
			}
		}
		return d[b.length()-1]; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Variables
	///////////////////////////////////////////////////////////////////////////
	
	/** Number of Characters with reduced Error Weight 
	 * from the Beginning and End of any String 
	 * to account for declination and Conjugation. 
	 * Public Access makes this Class not Thread-safe!	 */
	public int numPreSufFix; 
	
	/** Flag whether String Comparison is case-sensitive  */
	public boolean caseSensitive; 
	
	/** WorkSpace Array for String Comparison
	 * This Workspace makes this Class not Thread-safe, 
	 * because parallel Execution of the dist-Method interferes!	 */
	protected int[] workSpace; 
	
	/** WorkSpace Array for String Comparison; 
	 * allows to restrict the Implementation to StringBuffers
	 * and handle StringBuffers handed over most effectively!	 */
	protected final StringBuffer a = new StringBuffer(); 
	
	/** WorkSpace Array for String Comparison; 
	 * allows to restrict the Implementation to StringBuffers
	 * and handle StringBuffers handed over most effectively!	 */
	protected final StringBuffer b = new StringBuffer(); 
	
	/** optional Character Substitution for Normalization of Strings	 */
	protected final String[][] substitutions; 
	
	/** optional Keyboard Layout for detecting Glitches	 */
	protected final int[][] keyboardIndex; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/** Constructor	 */
	public EditMetric() { 
		this (false, 0, null, (int[][]) null); 
		L.debug("Called empty Constructor"); 
	}
	
	/** Constructor	 */
	public EditMetric(final String[] keyboardLayout) {
		this(false, 0, null, CALC_KEYBOARD_INDEX(keyboardLayout)); }
	
	/** Constructor	 */
	public EditMetric(final int[][] _keyboardIndex) { this(false, 0, null, _keyboardIndex); }
	
	/** Constructor	 */
	public EditMetric(final boolean _caseSensitive) { this (_caseSensitive, 0, null, (int[][]) null); }
	
	/** Constructor	 */
	public EditMetric(final boolean _caseSensitive, final String[] keyboardLayout) {
		this(_caseSensitive, 0, null, CALC_KEYBOARD_INDEX(keyboardLayout)); }
	
	/** Constructor	 */
	public EditMetric(final boolean _caseSensitive, final int _numPreSufFix, final String[][] _substitutions, final String[] keyboardLayout) {
		this(_caseSensitive, _numPreSufFix, _substitutions, CALC_KEYBOARD_INDEX(keyboardLayout)); }
	
	/** Constructor	 */
	public EditMetric(final boolean _caseSensitive, final int _numPreSufFix, final String[][] _substitutions, final int[][] _keyboardIndex) {
		this.workSpace = new int[20]; //should be sufficient for most Words!
		this.caseSensitive = _caseSensitive; 
		this.keyboardIndex = _keyboardIndex; 
		this.substitutions = _substitutions; 
		this.numPreSufFix  = _numPreSufFix; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** @see tester.IScalarMetric#dist(java.lang.Object, java.lang.Object)	 */
	public double dist(final Object _a, final Object _b) { return distInt(_a, _b); }
	
	/** @see tester.IScalarMetric#dist(java.lang.Object, java.lang.Object)	 */
	public int distInt(final Object _a, final Object _b) {
		return dist(TO_STRING_BUFFER(_a, a), TO_STRING_BUFFER(_b, b)); 
	}
	
	/** @see tester.IScalarMetric#dist(java.lang.Object, java.lang.Object)	 */
	public int dist(final String _a, final String _b) {
		a.setLength(0); a.append(_a); 
		b.setLength(0); b.append(_b); 
		return dist(a, b); 
	}
	
	/**
	 * returns the Distance as defined in this Instance
	 * @param _a the first Object to compare
	 * @param _b the first Object to compare
	 * @return the Distance as defined in this Instance
	 */
	public int dist(final StringBuffer _a, final StringBuffer _b) {
		if (_a == _b)
			return 0; 
		if (_a == null)
			return _b.length(); 
		if (_b == null)
			return _a.length(); 
		if ((substitutions != null) &&
			(substitutions.length > 0)) {
			SUBSTITUTE(_a, substitutions);   
			SUBSTITUTE(_b, substitutions); 
		}
		if (workSpace.length <  b.length()) 
			workSpace = new int[b.length()]; 
		return DIST(_a, _b, numPreSufFix, caseSensitive, keyboardIndex, workSpace); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Tests whether the Metric is symmetic. 
	 * Since Swaps have a smaller Weight, the Metric is not completely symmetric, 
	 * but the Variance is at most 1.  */
	private static void testSymmetry(final EditMetric metric, final String test1, final String test2) {
		Assert.EQUALS(0,metric.dist(test1, test1)); 
		Assert.EQUALS(0,metric.dist(test2, test2)); 
		Assert.IS_TRUE (metric.dist(test1, test2) - 
						metric.dist(test2, test1) == 0); 
	}
	
	/** Tests all Methods of this Class */
	public static void testItOld() {
		final EditMetric metric = GET_METRIC_GERMAN(); 
	//	Assert.EQUALS(4, metric.dist("Haus" , "Häus"   ), "Plural Umlaut Forms should be normalized!"); 
		Assert.EQUALS(1, metric.dist("Haus" , "Häus"   ), "Plural Umlaut Forms should be normalized!"); 
		Assert.EQUALS(1, metric.dist("Haus" , "Haeus"  ), "Extra Characters within count only for 1"); 
		Assert.EQUALS(3, metric.dist("Haus" , "Haeuser"), "Extra Characters count only for 1"); 
		Assert.EQUALS(4, metric.dist("Gumbo", "Gamboli"), "Suffixes count only for 1"); 
		Assert.EQUALS(3, metric.dist("Gumbo", "Gambol" ), "Example"); 
		Assert.EQUALS(2, metric.dist("Gumbo", "Gambo"  ), "1 Character mistyped"); 
		Assert.EQUALS(2, metric.dist("Gumbo", "Gubmo"  ), "swapped Characters"); 
		Assert.EQUALS(1, metric.dist("Gumbo", "Gummbo" ), "doubled Character"); 
		Assert.EQUALS(0, metric.dist("Gumbo", "Gumbo"  ), "identical Strings"); 
		Assert.EQUALS(1, metric.dist("Gumbo", "fumbo"  ), "Keyboard Glitch"); 
		Assert.EQUALS(2, metric.dist("Gumbo", "Dumbo"  ), "really mistyped"); 
		for(int i = 500; --i >= 0;) 
			testSymmetry(metric, RANDOM(12), RANDOM(11));
	}
	
	/** Tests all Methods of this Class, now with Prefix and Suffix Correction */
	public static void testIt() {
		final EditMetric metric = GET_METRIC_GERMAN(); 
	//	Assert.EQUALS(4, metric.dist("Haus" , "Häus"   ), "Plural Umlaut Forms should be normalized!"); 
		Assert.EQUALS(1, metric.dist("Haus" , "Häus"   ), "Plural Umlaut Forms should be normalized!"); 
		Assert.EQUALS(1, metric.dist("Haus" , "Haeus"  ), "Extra Characters within count only for 1"); 
		Assert.EQUALS(3, metric.dist("Haus" , "Haeuser"), "Extra Characters count only for 1"); 
		Assert.EQUALS(3, metric.dist("Gumbo", "Gamboli"), "Suffixes count only for 1"); 
		Assert.EQUALS(2, metric.dist("Gumbo", "Gambol" ), "Example"); 
		Assert.EQUALS(1, metric.dist("Gumbo", "Gambo"  ), "1 Character mistyped"); 
		Assert.EQUALS(2, metric.dist("Gumbo", "Gubmo"  ), "swapped Characters"); 
		Assert.EQUALS(1, metric.dist("Gumbo", "Gummbo" ), "doubled Character"); 
		Assert.EQUALS(0, metric.dist("Gumbo", "Gumbo"  ), "identical Strings"); 
		Assert.EQUALS(1, metric.dist("Gumbo", "fumbo"  ), "Keyboard Glitch"); 
		Assert.EQUALS(1, metric.dist("Gumbo", "Dumbo"  ), "really mistyped"); 
		for(int i = 50000; --i >= 0;) 
			testSymmetry(metric, RANDOM(12), RANDOM(11));
	}
	
	/**
	 * The main entry point for the application.
	 * @param args strings with Words to compare against the first one. 
	 */
	public static void main (final String[] args) throws Exception {
		if (args.length == 0)
			testIt(); 
	}
	
}
