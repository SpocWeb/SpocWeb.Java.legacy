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
 * @author heuerm
 *
 */
public class CombinationStream2 {

	/** Logger Instance for this Class	 */
	private static final Log L = new Log(CombinationStream.class, 0); 
	
	final int[] v; 
	int counter; 
	int value; 
	final int mask; 
	
	/**
	 * 
	 */
	public CombinationStream2(final int _dim) {
		v = new int[_dim]; 
		mask = (1 << _dim)-1; 
		reSet(); 
	}
	
	public void reSet() { 
	    value = -1; 
	    counter = -2; 
	    //return this; 
	}
	
	public int[] currInt() { return v; }
	
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
	
	public static void main(final String[] args) {
		final CombinationStream2 comb = new CombinationStream2(4); 
		for (int i = -1; ++i <= 150;) 
			L.n("#").l(i).l(comb.nextInt()); 
	}
	
}
