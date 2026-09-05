package stringOp.search;

import streamIO.IIStreamIn;
import streamIO.object.ArrayStreamIn;
import function.byref.ByRefChar;

/**Knuth Morris Pratt Algorithm to search a String within another. 
 * It creates a small finite State Machine.
 * It is most effective with very recursive Patterns,
 * i.e. long Strings in a small Alphabet. 
 * 
 * Accesses the List only sequentially, so it is apted for Streams and IStreamIns.
 * It requires at Maximum LL+PL Comparisons.
 * 
 * dumbIndexOf() is usually equally powerful for small Patterns and a single match.
 * 
 * 'contains' is a transitive (Order) Relation on (sequential) Streams and Sets 
 * as well as startsWith(), endsWith()
 * (resp. isPrefixOf() and isSuffixOf())
 * In ordered Containers (like Arrays and IStreamIns) a special containsOrdered()
 * can be defined which is only true, if the Elements appear in exactly this Order.
 * It obeys the following Laws:
 * A.contains(B) && B.contains(C) ==> A.contains(C) "Transitivity"
 * A.contains(B) => |A| >= |B| "HomoMorphism, no IsoMorphism"
 *
 * Given: A.isPrefix(B) && C.isPrefix(B) =>
 * Follows (the same for containsAt(int Position) and startsWith):
 * |A| <= |C| => A.endsWith(C)
 * |A| == |C| => A.equals  (C)
 * |A| >= |C| => C.endsWith(A)
 * 
 * @see stringOp.search.RegExp for a parameterizable Automaton 
 * for a more variable Set of Patterns than a simple fixed String. 
 * @see stringOp.search.SearcherBM 
 * @see stringOp.search.SearcherRK
 * Other Algorithms using hashCodes are implemented in SearcherBM and SearcherRK,
 * which are exploiting the Fact that numerical Operations are built in and fast.
 * @see tester.process.PatternSearcher implements the same Algorithm explicitly using an Automaton.
 * 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 50a3f64faeacf260e2a91ec77e64d62890c957164a94a17741e9248af91266ae
 * stale: false
 * tags: [code/string_search]
 * concepts: [String Searcher Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class StrSearcher {

	////////////////////////////////////////////////////////////////////////////
	//	static Members
	////////////////////////////////////////////////////////////////////////////

	/**Returns the Position of the second Array in the first
	 * This Implementation is not very smart,
	 * it needs at most LL*PL Comparisons in the worst Case,
	 * which happens if both consist of the same repeating Character
	 * except for the last one.
	 * This happens most likely in long Patterns expressed in a small Alphabet.
	 * Additionally, you have to roll back, if the Pattern doesn't match,
	 * so this Algorithm is not apted for Streams!!! 	 */
	public static int dumbIndexOf(Object[] Pattern, Object[] List) {
		return dumbIndexOf(Pattern, List, 0); }

	/**Returns the Position of the second Array in the first
	 * This Implementation is not very smart,
	 * it needs at most LL*PL Comparisons in the worst Case,
	 * which happens if both consist of the same repeating Character
	 * except for the last one.
	 * This happens most likely in long Patterns expressed in a small Alphabet.
	 * Additionally, you have to roll back, if the Pattern doesn't match,
	 * so this Algorithm is not apted for Streams!!! 	 */
	public static int dumbIndexOf(Object[] Pattern, Object[] List, int Start) {
		int i = Start, LL = List.length;
		int j =     0, PL = Pattern.length;
		do { //increment both, while they are equal
			if (! List[i].equals(Pattern[j])) { //if the matching Sequence breaks,
				i -= j; j = -1; } //reset the Search Position and start on the next one.
		} while ((++j < PL) && (++i < LL));	//until one is larger than the Array
		if (j >= PL) return i-PL+1; //found
		else return i; }

	////////////////////////////////////////////////////////////////////////////
	//	Members
	////////////////////////////////////////////////////////////////////////////

	/**Helper Array encoding the finite State Machine
	 * defined by the Pattern's internal Similarities (Prefix Function). 	 */
	private int[] Prefix;

	/**Local Copy of the Pattern, must not be modified externally	 */
	private Object[] Pattern;

	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////

	/** Helper Routine initializing the finite State Machine
	  * defined by the Pattern's internal Similarities.
	  * Could also use an IStreamIn to define a Pattern, but
	  * 1) Elements are accessed in arbitrary Order
	  * 2) Size should be known beforehand to dimension Prefix[]  */
	public StrSearcher(Object[] Pattern_) {
		Pattern = Pattern_; //Cache the Pattern, don't make a Copy, leave this to the Caller
		int i =  0, PL = Pattern.length;
		int j = -1; //
		Prefix = new int[PL]; Prefix[0] = -1; --PL;
		do { //j is both the 'State' and the Length of the current Match.
			if ((j == -1) || (Pattern[i].equals(Pattern[j]))) {
				++i; ++j; //j is always smaller then i, which runs through 0..PL-1
//				Prefix[i] = j;	//simple Implementation
				if (Pattern[i].equals(Pattern[j]))	//smarter Implementation
					 Prefix[i] = Prefix[j];		//also considers the next character (2nd Level of recursive Description)
				else Prefix[i] = j;	//possible to use Prefix[j], since j < i
			} else j = Prefix[j]; //go back in State
		} while (i < PL); //Also here the Loop for Prefix[] and the Loop to go back in j are combined into one.
	}

	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////

	/**Knuth Morris Pratt Algorithm to search, using the finite State Machine.
	 * Accesses the List only sequentially, so it is apted for Streams and IStreamIns.
	 * It requires at Maximum LL+PL Comparisons.	 */
	public int indexOf(IIStreamIn Iter) {
		Object currItem = Iter.nextItem ();
		int j = 0; //j is both the 'State' and the Length of the current Match.
		int i = 0, PL = Pattern.length; //saves acessing the Length (possible expensive Swapping or Cross Process Access)
		while(true) { //here the Loop for the State Machine AND the Loop for the Transition Function
			if ((j == -1) || (Pattern[j]   ==  (currItem))
						  || (Pattern[j].equals(currItem))) { //you could cache the Length of the best Match yet!
				++i; ++j; //match, increment State
				if ((null == (currItem = Iter.nextItem())) && !Iter.isValid()) {
					return -1; }
			} else
				j = Prefix[j]; //no match, go back 1 Step using Prefix Function
			if (j >= PL) return i-PL;  //are elegantly united, which makes it harder to understand.
		}
	}

	/**Knuth Morris Pratt Algorithm to search, using the finite State Machine.
	 * Accesses the List only sequentially, so it is apted for Streams and IStreamIns.
	 * It requires at Maximum LL+PL Comparisons.	 */
	public int indexOf(Object[] List) { return indexOf(List, 0); }

	/**Knuth Morris Pratt Algorithm to search, using the finite State Machine.
	 * Accesses the List only sequentially, so it is apted for Streams and IStreamIns.
	 * It requires at Maximum LL+PL Comparisons.	 */
	public int indexOf(Object[] List, int Start) {
		ArrayStreamIn Iter = new ArrayStreamIn(List);
		int f; Iter.jump(Start);
		if (0 >= (f = indexOf(Iter))) return f;
		return Start + f; }

	////////////////////////////////////////////////////////////////////////////
	//	Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class */
	public static void testIt(String[] args) {
		System.out.println("Testing " + StrSearcher.class.getName());
		ByRefChar[] pattern = ByRefChar.String2ByRefChar("Pattern");
		ByRefChar[] List    = ByRefChar.String2ByRefChar("The AntiPattern Language is a richer Form of Pattern. ");
		StrSearcher src = new StrSearcher(pattern);
		int i = 0;
		System.out.println(i = src.indexOf(List, i)); i += pattern.length;
		System.out.println(i = src.indexOf(List, i)); i += pattern.length;
		System.out.println(i = src.indexOf(List, i)); i += pattern.length;
		System.out.println(i = src.indexOf(List, i)); i += pattern.length;
		System.out.println(i = src.indexOf(List, i)); i += pattern.length;
		System.out.println(i = src.indexOf(List, i)); i += pattern.length;
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
