/*
 * Created on 01.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.vector;

import math.vector.VectorInt;
import streamIO.Log;

/**
 * Generates a Stream of Vectors with all Combinations to draw N Items
 * from a Selection of Dim Items
 * WITHOUT considering the Sequence
 * WITHOUT returning the drawn Elements into the Bin.
 *
 * N increases as soon as all Combinations are exhausted.
 * Also contains a static Method to generate all Combinations for fixed N and dim.
 * @see CombinationStream an alternative, Permutation-based implementation of the same idea.
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:32:34Z
 * digest: f3a7def258976cbb02b448721bdc6b295bf0895e2cac5d0a1af9985ecca43c1a
 * stale: false
 * tags: [code/combinatorics]
 * concepts: [Combinatorics]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class CombinationStream2 {

	/** Logger Instance for this Class	 */
	private static final Log L = new Log(CombinationStream2.class, 0);

	/** The Vector returned by {@link #currInt()}/{@link #nextInt()}, counting how often each Item was drawn. */
	final int[] v;
	/** Bit-mask Counter driving the next Combination's bit pattern. */
	int counter;
	/** N, the current Number of Items drawn; increases once all bit patterns for it are exhausted. */
	int value;
	/** Bit-mask covering the Dimension, precomputed for speed. */
	final int mask;

	/** Creates a Stream over all Combinations to draw from a Selection of {@code _dim} Items. */
	public CombinationStream2(final int _dim) {
		v = new int[_dim];
		mask = (1 << _dim)-1;
		reSet();
	}

	/** Resets the Stream to the beginning, N = 0. */
	public void reSet() {
	    value = -1;
	    counter = -2;
	    //return this;
	}

	/** Returns the Vector filled with the current Combination. */
	public int[] currInt() { return v; }

	/** Advances to and returns the Vector filled with the next Combination. */
	public int[] nextInt() {
		int bitSet = (++counter & mask) << 1; 
		if (bitSet == 0) {
			++counter; bitSet += 2; //avoid Sequences of 0,0
			++value; }
		VectorInt.FILL_AT(v, value);
		for(int i = v.length; --i >= 0;) {
			if(((bitSet >>= 1) & 1) == 1)
				++v[i]; 
		}
		return v;
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) {
		final CombinationStream2 comb = new CombinationStream2(4);
		for (int i = -1; ++i <= 150;)
			L.n("#").l(i).l(comb.nextInt());
	}
	
}
