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
 * @author heuerm
 *
 */
public class CombinationStream {
	
	/** Logger Instance for this Class	 */
	private static final Log L = new Log(CombinationStream.class, 0);  
	
	int value; 
	final int[] v; 
	final Permutation[][] combs; 
	int row; 
	int col; 
	
	/**
	 * 
	 */
	public CombinationStream(final int _dim) {
		v = new int[_dim]; 
		combs = Permutation.Combinations(_dim);
		reSet(); 
	}
	
	public void reSet() {
		row = combs.length; 
		col = value = 0; 
	}
	
	public int[] currInt() { return v; }
	
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
	
	public static void main(final String[] args) {
		final CombinationStream comb = new CombinationStream(4); 
		for (int i = -1; ++i <= 150;)
			L.n("#").l(i).l(comb.nextInt());
	}
	
	public static void testPermutation() {
		final int dim = 5; 
		final Permutation[][] combs = Permutation.Combinations(dim); 
		L.n(combs);
		for(int i = -1; ++i <= dim;) 
			testCombinations(dim, i);
	}

	/**
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
