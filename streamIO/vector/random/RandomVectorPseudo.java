/*
 * Created on 28.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.vector.random;

import streamIO.Log;
import streamIO.vector.CombinationsRepeating;
import function.byref.ByRefInt;

/**
 * Uses Cantor's Diagonal Algorithm to generate binary Pseudo-Random Vectors 
 * without Correlations and Sequences. 
 * Thus Evaluation using these Vectors can be stopped at any Point in Time. 
 * @author heuerm
 * @see streamIO.object.Cantor which implements the Cantor Algorithm for Streams. 
 * @see streamIO.vector.random.RandomVectorPseudoSequential where you always have to complete one Cycle. 
 */
public class RandomVectorPseudo {
	
	/** Logger for Testing */
	private static final Log L = new Log(RandomVectorPseudo.class, 0);
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Vector to be returned by RandomFloat	 */
	final public float[] x;
	
	/** Vector to be returned by RandomFloat	 */
	final public int[] r;
	
	/** Pre-Calculated for Speed Optimization	 */
	final int octave; 
	
	/** Pre-Calculated for Speed Optimization	 */
	final int maxValue; 
	
	/** Pre-Calculated for Speed Optimization	 */
	final float maxFactor; 

	/** The Stream with increasing Combinations of Values 	 */
	final CombinationsRepeating combinations; 
	
	/** Integer Vector returned by CombinationsRepeating 	 */
	final int[] v;
	
	/**
	 * 
	 * @param _dim
	 * @param _maxOctave
	 */
	public RandomVectorPseudo(final int _dim, final int _maxOctave) {
		x = new float[_dim]; 
		r = new   int[_dim]; 
		octave = _maxOctave; 
		maxValue = 1 << _maxOctave;
		maxFactor = 1f/maxValue;
		combinations = new CombinationsRepeating(_dim); 
		v = combinations.currInt(); 
	}
	
	public int[] nextInt() {
		maxLevelOfNextChange(); 
		return r; 
	}
	
	protected int maxLevelOfNextChange() {
		final int level = v.length-1; //combinations.maxLevelOfNextChange();
		combinations.nextInt(); 
		for(int i = level+1; --i >= 0;) 
			r[i] = ByRefInt.REVERT(v[i], octave);
		return level; 
	}
	
	public float[] nextFloat() {
		final int level = maxLevelOfNextChange(); 
		for(int i = level+1; --i >= 0;) 
			x[i] = r[i]*maxFactor;
		return x;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() {
		final int octave = 5; 
		final int maxVal = 1 << octave; 
		final int[][] matrix = new int[maxVal][maxVal]; 
		final RandomVectorPseudo ran = new RandomVectorPseudo(2, octave); 
		for(int i = maxVal*maxVal; --i >= 0;) {
			final int[] v = ran.nextInt(); L.n(v); 
			++matrix[v[0]][v[1]]; 
		} //Generator generates only n*(n+1)/2 unique Values, the other ones are skipped! 
		//how do you replay the previous Values? 
		L.n(matrix); 
//		for(int i = matrix.length; --i >= 0; ) 
//			for(int j = matrix.length; --j >= 0; ) 
//				Assert.EQUALS(1, matrix[i][j]);
//		for(int i = maxVal*maxVal; --i >= 0;) {
//			float[] v = ran.nextFloat(); L.n(v); 
//		} //Generator generates only n*(n+1)/2 unique Values, the other ones are skipped! 
		
	}
	
	/** Main Method of this Class 	 */
	final static public void main(final String args[]) {
		testIt(); 
	}
	
}
