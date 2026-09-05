package stringOp.search;

/**Boyer- Moore Algorithm to search a String within another String.
 * It maintains a List of possible positive Jumps
 * and accesses the List with these,
 * but it searches from the end of these Jumps, so it is not good for Streams.
 * 
 * Assumes a unique HashCode, i.e. same HashCode implicates Equality.
 * It is most effective with a nonrecursive Pattern in a large Alphabet.
 * (although that means a larger skip Array)
 * It requires at Maximum N+M Comparisons, but is fast due to Jumps and
 * usually only N/M Comparisons, since most Characters of the List
 * don't appear in the Search Pattern. 	 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:41:54Z
 * digest: 5c4b6a52a2ddf98df17b278141a8aaaea6ef49ad1f971a45f23755379d33d7d2
 * stale: false
 * tags: [code/string_search, code/search_algorithm]
 * concepts: [Boyer-Moore Search]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class SearcherBM {

	/**Defines the maximum Character of the Alphabet.
	 * Determines the Size of the skip Array and the Effort to initialize it. 	 */
	protected int Max_Char = Character.MAX_VALUE;

	/**Helper Array encoding the Positions to skip when the Character doesn't match.
	 * The Problem here is that this Array may become quite large,
	 * since it has to define a Skip Distance for each Character of the Alphabet. 	 */
	protected int[] skip = new int[Max_Char];

	/**Local Storage of the Pattern.
	 * To guarantee that the Algorithm works,
	 * you would have to create copies of the original Pattern.	 */
	protected Object[] Pattern;

	/**Constructor initializing the finite State Machine
	 * defined by the Pattern's internal Similarities. 	 */
	public SearcherBM(Object[] Pattern_) {
		Pattern = Pattern_;
		int i = -1, M = Pattern.length;
		int j = Max_Char;
		while (--j >= 0) skip[j] = M; M--; //pre-initialize the Array of Skips
//		while (--i >= 0) skip[Pattern[i].hashCode() % Max_Char] = M-i;
		// TODO: LOGIC: Object.hashCode() can be negative (e.g. Integer, Long, many Strings); Java's '%' keeps
		// the sign of the dividend, so skip[Pattern[i].hashCode() % Max_Char] can index with a negative value
		// and throw ArrayIndexOutOfBoundsException. Same issue in indexOf() below. Only safe today because the
		// callers in this codebase pass ByRefChar (non-negative char-derived hashCode).
		while (++i <= M) skip[Pattern[i].hashCode() % Max_Char] = M-i;
	}	//the Sequence (0..M) or (M..0) determines, whether the Algorithm works,
		//because it now replaces larger skips by smaller ones,
		//which prevents too large skips

	/**Boyer- Moore Algorithm to search, maintains a List of possible Jumps.
	 * most effective with a nonrecursive Pattern in a large Alphabet.
	 * It requires at Maximum N+M Comparisons,
	 * but usually N/M Comparisons, since most Characters of the List
	 * don't appear in the Search Pattern. 	 */
	public int indexOf(Object[] List) {
		int M = Pattern	.length, i = M-1, s;
		int N = List	.length, j = M-1;
		do {
			if (List[i].equals(Pattern[j])) {--i; --j;}	//as long as Pattern and List are equal
			else {	//Choose the larger Skip.
				// TODO: LOGIC: same negative-hashCode risk as the constructor above - a negative
				// List[i].hashCode() % Max_Char indexes skip[] with a negative value and throws
				// ArrayIndexOutOfBoundsException.
				if (M-j+1 > (s = skip[List[i].hashCode() % Max_Char]))
					 i += M-j+1;
				else i += s;
				j = M-1; }
		} while ((j >= 0) && (i < N));
		return i+1;	}

}
