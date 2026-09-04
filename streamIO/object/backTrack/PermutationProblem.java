package streamIO.object.backTrack;

import streamIO.object.IPipe;
import streamIO.object.enumer.container.DeQueueArr;
import function.AFunction;

/** Generator and ITester Class for the Permutations
  * This can equivalently be seen as the Generator for the Chess Towers Problem,
  * where N Towers have to be placed on an N*N Chess Board so they cannot capture each other. 
  * 
  * Design Decisions:
  * No extra Class for representing the State necessary,
  * since this can be done efficiently by a String.
  * The Disadvantage is that you cannot reconstruct the actual Permutations from this,
  * only the SubSets to further permute. 
  */
class PermutationProblem
extends AFunction {

	/**Generator for the new Solutions for arg, could be static!	 */
	public Object Map(Object arg) {
		String tmp, str = (String) arg;
		int Length = str.length();
		String[] Return = new String[Length]; //create these many SubStrings by...
		while (--Length >= 0) {	//taking out the middle Character and do a Permutation on the Rest.
			tmp = Return[Length]
				= str.substring(0, Length) +
				  str.substring(Length+1); //+
//				  str.charAt(Length);	//use the middle Character for assembling the Result String
			System.out.println(tmp);
		}
		return Return; }

	/**Tests all Methods of this Class	 */
	public static void testIt() {
		System.out.println ("Testing PermutationProblem:");
		String StartPosition = "abc";
		IPipe Store;	//avoid reusing existing Elements
		PermutationProblem Problem = new PermutationProblem();

		//Stack with avoiding Duplicates, takes 50 Tries (Depth-first, FIFO)
		Store = new DeQueueArr(100, IPipe.ORDER_STACK);
		Store.addItem(StartPosition);
		BackTracker BT; 
		BT = new BackTracker(	Store, null, null, Problem, true);	//avoiding Duplicates
		System.out.println (BT.nextItem());

		//Queue with avoiding Duplicates, takes 20 Tries (Breadth-first, LIFO)
		Store = new DeQueueArr(100, IPipe.ORDER_QUEUE);
		Store.addItem(StartPosition);
		BT = new BackTracker(	Store, null, null, Problem, true);	//avoiding Duplicates
		System.out.println (BT.nextItem());

	}

}
