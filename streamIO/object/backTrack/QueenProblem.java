package streamIO.object.backTrack;

import streamIO.AStreamOut;
import streamIO.object.IPipe;
import streamIO.object.enumer.container.DeQueueArr;
import function.AFunction;
/** Generator for the N-Queens placement search, for use with {@link BackTracker}.
 * <p>
 * The Task is to distribute n Queens on an n*n Matrix in a way that these
 * cannot attack each other.
 *
 * Design Decisions:
 * No extra Class for representing the State necessary,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:45:44Z
 * digest: f1d3e570f543ff84df9076343fdbae22da2b982a7ca39526e49a974c91ea2e83
 * stale: false
 * tags: [code/backtracking, code/algorithm]
 * concepts: [Backtracking Search]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * since this can be done efficiently by an Array.	 */
public class QueenProblem
extends AFunction {

	/**Generator for the new Solutions for arg, could be static!	 */
	public Object Map(Object arg) {
		int[] tmp, Positions = (int[]) arg;
		int Col = Positions.length;	//Start at the End to find out, which Positions are open
		while (--Col >= 0) if (Positions[Col] < 0) break;	//End is marked by a negative Number
		if (Col < 0) {
            System.out.println("\nSolution:");
			AStreamOut.ARRAY_TO_STREAM(System.out, Positions, " ");
//            System.out.println( XMLOutputStream.toStringSafe(Positions, "Solution", false));
            return null;
        }
		int[][] Return = new int[Positions.length][];
		int Row = Positions.length;
		while (--Row >= 0) {	//try each Position
			//first test it, whether it is valid...
			//...the Column is free anyway, since we progress by Column
			int testCol = Positions.length;
			while (--testCol > Col) {
				int tst = Positions[testCol];
				if			(tst == Row) break;	//...test for the Row
				if (Math.abs(tst -  Row) ==
						 testCol -  Col) break;	//...test for Diagonal
			}
			if (testCol > Col) continue;	//invalid, try the next one
			//if valid, add it to the Solution
			tmp = Return[Row] = new int[Positions.length];
			System.arraycopy(Positions, 0, tmp, 0, Positions.length);	//copy the -1 s too
			tmp[Col] = Row; //tmp[Row-1] = -1;	//mark the End
		}
		return Return; }

    /**Size of the Sample Problem in testIt()
     * 4 has 2 Solutions
     * 5 has 
     */
	final static int N = 4;

	/**Tests all Methods of this Class	 */
	public static void testIt()	{
		System.out.println ("Testing QueenProblem:");
		int[] StartPosition = new int[N];
		int i = N; while (--i >= 0) StartPosition[i] = -1;
		IPipe Store;	//avoid reusing existing Elements
		QueenProblem Problem = new QueenProblem();

		//Stack with avoiding Duplicates, takes 50 Tries (Depth-first, FIFO)
		Store = new DeQueueArr(100, IPipe.ORDER_STACK);
		Store.addItem(StartPosition);
		BackTracker BT;
		BT = new BackTracker(	Store, null, null, Problem, true);	//avoiding Duplicates
		System.out.println (BT.nextItem());

		//Queue with avoiding Duplicates, takes 20 Tries (Breadth-first, LIFO)
		Store = new DeQueueArr((N+1)*N*N, IPipe.ORDER_QUEUE);	//Breadth Search needs more Space!
		Store.addItem(StartPosition);
		BT = new BackTracker(	Store, null, null, Problem, true);	//avoiding Duplicates
		System.out.println (BT.nextItem());

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
