package tester.process;

import streamIO.IIStreamIn;
import stringOp.search.StrSearcher;

/**
  * PatternSearcher
  * Searches any String for a Character Pattern.
  * 
  * Conceptually clean Class performing an Analysis of the given Pattern
  * and defining a discrete State Transition Function map() for an Automaton.
  * @see StrSearcher has a more concise Implementation which is not as readable though.
  * 
  * Created on 26. Mai 2001, 11:57
  *
  * @author  Matthias Heuer
  * @version
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:13:23Z
  * digest: f6d1b225fa9c4b117bdfcaa0d77883cc8aae728e4284ccf3c31c6499d544db97
  * stale: false
  * tags: [code/stream_processing]
  * concepts: [Pattern Searcher]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
class PatternSearcher
implements IDiscreteTransition {
	
	////////////////////////////////////////////////////////////////////////////
	//	Members
	////////////////////////////////////////////////////////////////////////////
	
	/**Helper Array encoding the finite State Machine
	 * defined by the Pattern's internal Similarities (Prefix Function). 	 */
	private int[] Prefix;
	
	/**Local Copy of the Pattern, must not be modified externally	 */
	private Object[] Pattern;
	
	/**Local Automaton for searching the Pattern	 */
	private DiscreteAutomaton A;
	
	/** Position in the current streamIO.
	  * Variable shared between the indexOf() and the map() Method. 	 */
	private int Position;
	
	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/** Helper Routine initializing the finite State Machine
	  * defined by the Pattern's internal Similarities.
	  * Could also use an IStreamIn to define a Pattern, but
	  * 1) Elements are accessed in arbitrary Order
	  * 2) Size should be known beforehand to dimension Prefix[] correctly.
	  */
	public PatternSearcher(Object[] Pattern_) {
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
		A = new DiscreteAutomaton(this);
	}

	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////

	/**Knuth Morris Pratt Algorithm to search, using the finite State Machine.
	 * Accesses the List only sequentially, so it is apted for Streams and IStreamIns.
	 * It requires at Maximum LL+PL Comparisons.	 */
//	public int indexOf(Object[] List) { return indexOf(List, 0); }

	/**Knuth Morris Pratt Algorithm to search, using the finite State Machine.
	 * Accesses the List only sequentially, so it is apted for Streams and IStreamIns.
	 * It requires at Maximum LL+PL Comparisons.	 */
/*	public int indexOf(Object[] List, int Start) {
		ArrayEnumerator Iter = new ArrayEnumerator(List);
		int f; Start = Iter.skip(Start);
		if (0 >= (f = indexOf(Iter))) return f;
		return Start + f; }

	/**Knuth Morris Pratt Algorithm to search, using the finite State Machine.
	 * Accesses the List only sequentially, so it is apted for Streams and IStreamIns.
	 * It requires at Maximum LL+PL Comparisons.
	 * @returns: The Start Position of the Pattern in the Stream, -1 if not found.
	 */
	/**
	 * Searches Iter for this instance's pattern using the Knuth-Morris-Pratt state machine,
	 * consuming items sequentially.
	 * @param Iter the stream to search
	 * @return the start position of the pattern in the stream, or -1 if not found
	 */
	public int indexOf(IIStreamIn Iter) {
		A.State = -1;
		Position = 0;
		Object currItem;
		while (A.State < Pattern.length) {
			if ((null == (currItem = Iter.nextItem () )) && !Iter.isValid()) {
				return -1; }
			A.Operation(currItem);
		} return Position; }

	/**Knuth Morris Pratt Algorithm to search, using the finite State Machine.
	 * Accesses the List only sequentially, so it is apted for Streams and IStreamIns.
	 * It requires at Maximum LL+PL Comparisons.
	 * This State Function is used by DiscreteAutomaton
	 * @return the next State depending on Item and current State
	 * @param item  the Input Value
	 * @param state the current State of the State Machine.
	 */
	public int map(final Object item, int state) {
		while (true) { //j is both the 'State' and the Length of the current Match.
			if ((state < 0) || (Pattern[state]   ==   item) //new Start or a Match
							|| (Pattern[state].equals(item))) { //
				++Position; return ++state; //increment State, you could cache the Length of the best Match yet!
			} else state = Prefix[state]; //no match, go back 1 Step using Prefix Function
		} //repeat until a Match is found or a new Match Sequence starts
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class
      * @param args the Pattern and the String to be searched
      */
    public static void testIt() {
/*		ByRefChar[] pattern = ByRefChar.String2ByRefChar("Pattern");
		ByRefChar[] List    = ByRefChar.String2ByRefChar("The AntiPattern Language is a richer Form of Pattern. ");
		PatternSearcher src = new PatternSearcher(pattern);
		int i = 0;
//		System.out.println(i = src.indexOf(List, i)); i += Pattern.length;
//		System.out.println(i = src.indexOf(List, i)); i += Pattern.length;
*/	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		testIt(); }

}
