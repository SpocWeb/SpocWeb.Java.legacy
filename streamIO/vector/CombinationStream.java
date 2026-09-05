/*
 * Created on 31.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.vector;

import math.vector.VectorInt;
import streamIO.Log;
import streamIO.copy.monoid.integer.Permutation;

/**
 * Generates a Stream of Vectors with all Combinations to draw N Items
 * from a Selection of Dim Items
 * WITHOUT considering the Sequence
 * WITHOUT returning the drawn Elements into the Bin.
 *
 * N increases as soon as all Combinations are exhausted.
 * Also contains a static Method to generate all Combinations for fixed N and dim.
 * @see CombinationStream2 an alternative, bit-mask-based implementation of the same idea.
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:32:16Z
 * digest: 61188453b577e8ff7bab66f3ebd377b098801bc74be769de3f0e9ee169512182
 * stale: false
 * tags: [code/combinatorics]
 * concepts: [Combinatorics]
 * facets: {layer: utility, status: stable, complexity: medium}
 * -->
 */
public class CombinationStream {
	
	/** Logger Instance for this Class	 */
	private static final Log L = new Log(CombinationStream.class, 0);

	/** N, the current Number of Items drawn; increases once all Combinations for it are exhausted. */
	int value;
	/** The Vector returned by {@link #currInt()}/{@link #nextInt()}, counting how often each Item was drawn. */
	final int[] v;
	/** Precomputed Combinations (as Permutations), indexed by the Number of Items drawn. */
	final Permutation[][] combs;
	/** Current row into {@link #combs}. */
	int row;
	/** Current column into the current row of {@link #combs}. */
	int col;

	/** Creates a Stream over all Combinations to draw from a Selection of {@code _dim} Items. */
	public CombinationStream(final int _dim) {
		v = new int[_dim]; 
		combs = Permutation.Combinations(_dim);
		reSet(); 
	}
	
	/** Resets the Stream to the beginning, N = 0. */
	public void reSet() {
		row = combs.length;
		col = value = 0;
	}

	/** Returns the Vector filled with the current Combination. */
	public int[] currInt() { return v; }

	/** Advances to and returns the Vector filled with the next Combination. */
	public int[] nextInt() {
		final Permutation[] perms; 
		final Permutation   perm; 
		if( --col < 0) {
			if (--row < 0) {
				row = combs.length-2; 
				++value; 
			}
			perms = combs[row]; 
			perm  = perms[col = perms.length-1]; 
		} else {
			perms = combs[row]; 
			perm  = perms[col]; 
		}
		if(row+row > v.length) { //increase the latter Positions
			VectorInt.FILL_AT(v, value); 
			for(int i = row-1; ++i < v.length;)
				++v[perm.map(i)]; 
		} else { //decrease the latter Positions
			VectorInt.FILL_AT(v, value+1); 
			for(int i = v.length-row-1; ++i < v.length;)
				--v[perm.map(i)]; 
		}
		return v;
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) {
		final CombinationStream comb = new CombinationStream(4);
		for (int i = -1; ++i <= 150;)
			L.n("#").l(i).l(comb.nextInt());
	}

	/** Tests all Methods of this Class	 */
	public static void testPermutation() {
		final int dim = 5; 
		final Permutation[][] combs = Permutation.Combinations(dim); 
		L.n(combs);
		for(int i = -1; ++i <= dim;) 
			testCombinations(dim, i);
	}

	/** Logs all Combinations to select {@code selects} Items from {@code dim} Values.
	 * @param dim
	 */
	private static void testCombinations(final int dim, final int selects) {
		Permutation[] perms; 
		perms = Permutation.Combinations(dim, selects); 
		L.n("Generating Combinations for k=").l(selects);
		for(int i = perms.length; --i >= 0;) 
			L.n(perms[i]); 
	}
	
}
