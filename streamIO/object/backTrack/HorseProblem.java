package streamIO.object.backTrack;

import streamIO.AStreamOut;
import streamIO.copy.ACopyAble;
import streamIO.object.IPipe;
import streamIO.object.enumer.container.DeQueueArr;
import function.AFunction;

/** Generator for the Knight's Tour (Horse Problem) search, for use with {@link BackTracker}.
 * <p>
 * The Task is to reach every Field in a Matrix exactly once
 * using Chess Horse / Knight Jumps.
 *
 * A lot of Solutions can be derived using Symmetries.
 * A good Strategy can be to always jump to the field with the least possible
 * following Possibilities; this eliminates some minor Dead Ends. 
 *
 * Design Decisions:
 * No extra Class for representing the State necessary,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:45:41Z
 * digest: 78ad7661dc59fb5b53780016a04511592b106601acb99e8a59d337f2d4450021
 * stale: false
 * tags: [code/backtracking, code/algorithm]
 * concepts: [Backtracking Search]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * since this can be done efficiently by an Array.	 */
public class HorseProblem
extends AFunction {

	/**Order of the Problem,
	 * Order of 5 creates 10 Solutions in about 10 Minutes Runtime.
	 * smaller Orders have no Solutions, larger Orders are exponentially slow. */
	final static int N = 5;

	/**Order of the Problem, squared	 */
	final static int N2 = N*N;

	/**Chess Board, filled with Positions visited by Number (>0).
     * Not visited Fields are 0,
     * Negative Numbers indicate the Number of possible Jumps from this field.
     * This allows quick identification of Dead Ends.
     */
	protected int[][] visited = new int[N][N];

	/**List of allowed Jumps	 */
	protected static final int[][] HorseJumps = {{+2,+1, 0},
												 {+2,-1, 0},
												 {-2,+1, 0},
												 {-2,-1, 0},
												 {+1,+2, 0},
												 {+1,-2, 0},
												 {-1,+2, 0},
												 {-1,-2, 0},
												 { 0, 0, 0}};	//last Item only for resetting the 'visited' Array.

	/**Generator for the new possible Solutions for arg, could be static!
	 * This Problem is only apted for recursive Processing,
	 * where you can re-use the same Array for storing the Progress,
	 * unless you create a Copy of the whole 2D Array.
     * TODO: improve this Algorithm:
     * If such a Field exists, there is no solution in this branch.
     * Order the proposed Solutions so that the ones with the least possible
     * Number of follow-up Jumps are chosen first (Speeds up first Solution).
     * Check the rest of the Matrix for Fields from which you can reach no other.
     * This avoids dead ends.
     */
	public Object Map(Object arg) {
		int[] Positions = (int[]) arg;
		visited	[Positions[0]]
				[Positions[1]] =
				 Positions[2];
		if (Positions[2] == 0)
			return null;
		if (Positions[2] == N2)	{
            System.out.println("Solution:");
			AStreamOut.ARRAY_TO_STREAM(System.out, visited, "\n ");
//            System.out.println( XMLOutputStream.toStringSafe(visited, "Solution", false));
            return null; }
//		int[][] Return = new int[HorseJumps.length][3];		//try all 8 new Positions
		int[][] Return = null;
		try {
			Return = (int[][]) ACopyAble.COPY(HorseJumps, 3);
		}
		catch (InstantiationException e) {}	//deep Copy of the Array
		catch (OutOfMemoryError e) { return null; }	//avoid Out of Memory Errors...
//		System.arraycopy(HorseJumps, 0, Return, 0, HorseJumps.length);	//does only a shallow Copy!
		int i = HorseJumps.length-1;	//always leave the last Item to clean up (works only on Stacks!)
		while (--i >= 0) {
			int[ ]Row = Return[i];
			Row[2] = Positions[2]+1;
			int j = 2; //3;	//the Test for Dimensions must only be done for x and y, not Nr.
			while (--j >= 0) {
				int Pos = Row[j] += Positions[j];
				if ((Pos < 0) || (Pos >= N)) {Return[i] = null; break;}
			}
			if (j >= 0) continue;
			if (visited[Row[0]][Row[1]] != 0) Return[i] = null;	//Test whether this Field is blocked
		}
		System.arraycopy(Positions, 0, Return[8], 0, 2);
		Return[8][2] = 0;
		return Return; }

	/**Tests all Methods of this Class	 */
	public static void testIt()	{
		System.out.println ("Testing HorseProblem:");
		int[] StartPosition = {0,0,1};
		IPipe Store;	//avoid reusing existing Elements
		HorseProblem Problem = new HorseProblem();

		//avoiding Duplicates is expensive, especially with Queues
		//and essentially useless here, because there will be no Duplicates created
		//by the Nature of this Generation Function!

		//Stack takes 50 Tries (Depth-first, FIFO)
		Store = new DeQueueArr(4, IPipe.ORDER_STACK);
		Store.addItem(StartPosition);
		BackTracker BT;
		BT = new BackTracker(Store, null, null, Problem, false);	//with avoiding Duplicates
		BT.nextItem();

		//Queue takes 20 Tries (Breadth-first, LIFO)
		Store = new DeQueueArr((N+1)*N*N, IPipe.ORDER_QUEUE);	//Breadth Search needs more Space!
		Store.addItem(StartPosition);
		BT = new BackTracker(Store, null, null, Problem, false);	//with avoiding Duplicates
		BT.nextItem();

	}

	/**
	 * Runs {@link #testIt()} from the command line.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		testIt();
	}

}
