package stringOp.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;


/**Rabin-Karp Algorithm to search, creates a polynomial Hash Code
 * to compare it with the Hash Code of the String.
 * Accesses the String only sequentially, has no Skips, but needn't call compare().
 * Most effective with very large Alphabets.
 * This one returns only Hits with the same HashCode,
 * but with a large Modulus the likelihood can be increased arbitrarily.
 * It requires on average LL+PL Operations.
 *
 * Uses the isomorphic Properties of the mapping to the Modulus Ring:
 * (a%m + b%m) % m = (a+b) % m
 * (a%m - b%m) % m = (a-b) % m
 * (a%m * b%m) % m = (a*b) % m
 * (a%m / b%m) % m = (a/b) % m
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 8cc143d6e7961383b4c796b0e4c999559338b90fdb1a27e7246ddb80ef9bc025
 * stale: false
 * tags: [code/string_search, code/search_algorithm]
 * concepts: [Rabin-Karp Search]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * 	 */
public class SearcherRK {

	/**Modulus of the HashCode Polynom, prime Number	 */
	private final int m = 33554393;

	/**Factor of the HashCode Polynom	 */
	private final int f = 32;

	/**contains the Horner Factor f^PL	 */
	protected int f_PL;

	/**Pattern Length	 */
	protected int PL;

	/**Pattern HashCode	 */
	protected int PHC;

	/**Calculates the HashCode for this Pattern.
	 * Since the Modulus is quite large,
	 * it is quite improbable for a miss. 	 */
	public int CalcHashCode(Object[] Pattern, int L) {
		int HC = 0;
		int i = -1; f_PL = 1;
		while (++i < L) {	//prepare the Codes for the Search
			if (i < PL-1) f_PL = (f*f_PL) % m;	//Horner Schema for the Bank List
			HC = (HC*f + Pattern[i].hashCode()) % m;	//Horner Schema
		}
		return HC; }

	/**Initializing Constructor, calculates the HashCode	 */
	public SearcherRK(Object[] Pattern) {
		PL  = Pattern	.length;
		PHC = CalcHashCode(Pattern, PL);
	}

	/**Searches for the HashCode in the List.
	 * A match in HashCodes is most probably also a match in Items	 */
	public int indexOf(Object[] List) {
		int LL  = List	.length;
		int LHC = CalcHashCode(List, PL);
		int i = -1;
		while ((PHC != LHC) && (++i < LL-PL)) {
			LHC = (LHC+f*m-List[i   ].hashCode()*f_PL) % m;	//remove the first Item
			LHC = (LHC*f  +List[i+PL].hashCode()   ) % m;	//add the next Item
		}
		return i+1; }

////////////////////////////////////////////////////////////////////////////
//	static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) throws java.io.IOException {
	System.out.println("Testing " + SearcherRK.class.getName());
	Collection AL = new LinkedList();
	AL = new ArrayList();
	AL = new HashSet();
	Iterator it = AL.iterator();
	Object obj = it.next (); //NoSuchElementException with all Iterators...
	obj = it.next ();
	obj = it.next ();
	System.out.println(obj);
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	testIt(args); }


}
