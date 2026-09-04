/*
 * Created on 31.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.vector;

import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.Log;
import function.byref.combinatoric.CombiFuncs;

/**
 * Generates a Stream of Vectors with all Combinations to draw N Items of dim Types
 * resp. drawing N times from a Selection of Dim Items 
 * WITHOUT considering the Sequence 
 * WITH returning the drawn Elements into the Bin. 
 *   
 * N increases as soon as all Combinations are exhausted.  
 * Also contains a static Method to generate all Combinations for fixed N and dim.  
 * @author heuerm
 *
 */
public class CombinationsRepeating {

	/** Logger for Testing */
	private static final Log L = new Log(CombinationsRepeating.class, 0);
	
	///////////////////////////////////////////////////////////////////////////
	/// Generation of Combinations with repeating Elements 
	/// since these are no Permutations, but int[] Vectors. 
	/// see Permutation for Generation of Permutations, Combinations & Variations
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * generates all possible Combinations with repeating Elements
	 * @see CombiFuncs#CombRep(int, int) returns the Number of possible Combinations
	 * @param dim the Number of Values to choose from 
	 * @param numItems the number of Items to select alltogether. 
	 * @return all possible Combinations to select numItems from dim Values 
	 * WITHOUT considering the Sequence and 
	 * WITH returning the Elements. 
	 */
	final static public int[][] CombRep(final byte dim, final byte numItems) {
		final int[][] ret = new int[(int) CombiFuncs.CombRep(dim, numItems)][dim];
		final int count = generateCombRep(ret, 0, dim-1, numItems); Assert.EQUALS(count, ret.length); 
		return ret; 
	}
	
	/**
	 * recursively generates all possible Combinations to select numItems from dim Values 
	 * WITHOUT considering the Sequence and 
	 * WITH returning the Elements. 
	 * @see CombiFuncs#CombRep(int, int) returns the Number of possible Combinations
	 * 
	 * @param vectors  the Vectors to fill 
	 * @param k the current Vector to fill
	 * @param dim the current Dimension to fill 
	 * @param remainder the remaining Number of Items to select  
	 * @return the new current Vector. 
	 */
	private static final int generateCombRep(final int[][] vectors, int k, 
			final int dim, final int remainder) { 
		for(int i = remainder+1; --i >= 0;) { 
			vectors[k][dim] = i; 
			if (dim > 0) 
				k = generateCombRep(vectors, k, dim-1, remainder-i);
			else { //dim == 0
				if (++k < vectors.length) //transfer the Values in the other Dimensions. 
					System.arraycopy(vectors[k-1], 2, vectors[k], 2, vectors[k].length-2); 
				return k; 
			}
		}
		return k; 
	} 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Vector to be returned by the Stream	 */
	protected final int[] v;
	
	/** Vector of Remainders	 */
	protected final int[] r;
	
	/** current Dimension in Generation 	*/
	protected int dim; //==SP
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/** initializing Constructor 
	 * 
	 * @param _dim the Number of Components for the Vectors returned. 
	 */
	public CombinationsRepeating(final int _dim) {
		v = new int  [_dim]; 
		r = new int  [_dim]; 
		reSet(); 
	}
	
	/** resets the Stream	*/
	public void reSet() { initDim(v.length-1, 0); }
	
	/** 
	 * returns the Vector filled with a new Combination
	 * @return the Vector filled with a new Combination
	 */
	public int[] currInt() { return v; }
	
	/** 
	 * returns the Vector filled with a new Combination
	 * @return the Vector filled with a new Combination
	 */
	public int[] nextInt() { maxLevelOfNextChange(); return v; }
	
	/**
	 * @param _dim
	 * @param _remainder
	 */
	private void initDim(final int _dim, final int _remainder) {
		dim = _dim; 
		r[dim] = _remainder; //Call Parameters
		v[dim] = _remainder+1; //for Initialization
	}
	
	/** Method Skeleton for the Recursion in generateCombRep()
	 * 
	 * @param _dim the next Dimension 
	 * @param _remainder the remaining Number of Items to draw  
	 * @return the highest Level at which a Change took place.  
	 */
	private int maxLevelOfNextChange(final int _dim, final int _remainder) {
		initDim(_dim, _remainder);	
		return maxLevelOfNextChange();
	}
	
	/** recursive Generation of Combinations. 
	 * @see #CombRep(byte, byte) which is the Model for this Routine 
	 * where the Loops have been unrolled. 
	 * @return the highest Level at which a Change took place.  
	 */
	public int maxLevelOfNextChange() {  
		if (--v[dim]>= 0) { //for-loop Condition
			if (dim == 0)
				return ++dim; //exit, Vector complete 
			final int ret = dim; //return highest successful Modification
			r[dim-1] = r[dim]-v[dim]; --dim; //Call Parameters
			v[dim  ] = r[dim]+1; //for-loop Initialization
			maxLevelOfNextChange(); 
			return ret; 
		} else {
			if(++dim >= v.length) { //increase the Number of Items
				final int remainder = v[0]+1; v[0] = 0;
				return maxLevelOfNextChange(v.length-1, remainder);  
			}
			return maxLevelOfNextChange(); 
		}
	} 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() {
		final byte dim = 4;  
		final CombinationsRepeating generator1 = new CombinationsRepeating(dim); 
		for(byte n = -1; ++n < 7;) {
			final long num = CombiFuncs.CombRep(dim, n); // ByRefLong.Combination()
			for(int i = -1; ++i < num;) {
				int level = generator1.maxLevelOfNextChange();  
				L.n("#").l(i).l("Change At:").l(level).l("Vector:").l(generator1.v); 
			}
			Assert.EQUALS(n, generator1.v[0]); //test that all Items end up in the first Dimension 
		}
		L.n("Testing Generation in one Call: "); 
		testCombRep(5, 3); 
		testCombRep(5, 7); 		
	}

	/**
	 * @return the Array generated
	 */
	private static int[][] testCombRep(final int dim, final int count) {
		final int[][] combinations = CombRep((byte) dim, (byte) count); 
		for(int i = combinations.length; --i >= 0;) {
			L.n().l(combinations[i]); 
			Assert.EQUALS(count, VectorInt.SUM(combinations[i]));
		}
		return combinations;
	}

	/** Main Method of this Class 	 */
	final static public void main(final String args[]) {
		testIt(); 
	}
	
}
