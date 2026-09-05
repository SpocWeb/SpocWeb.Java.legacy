/*
 * Created on 26.07.2005
 *
 */
package streamIO.vector.random;

import streamIO.Assert;
import streamIO.Log;

/**
 * Creates Pseudo-random Vectors 
 * that span the whole SuperCube. 
 * Unfortunately the Sequence of Values cannot be stopped at any Point in Time, 
 * but requires a Loop until the Step Size changes, 
 * otherwise the higher Areas will not be covered as thoroughly as others. 
 * 
 * @author heuerm
 * @see streamIO.vector.random.RandomVectorPseudo can be stopped at any time, 
 * but does not distribute it's Points evenly. 
 * @see 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:33:20Z
 * digest: c77041925250afa25a26231bb0785d68501f972a9cb6ac4e7e18b618a0a06ffb
 * stale: false
 * tags: [code/random_number_generation]
 * concepts: [Random Sampling, Monte Carlo]
 * facets: {layer: utility, status: stable, complexity: high}
 * -->
 */
public class RandomVectorPseudoSequential {
	
	/** Logger for Testing */
	private static final Log L = new Log(RandomVectorPseudoSequential.class, 0);
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Step Size of the Vector Elements	 */
	protected int step;
	
	/** Local State of the Vector	 */
	protected final int[] v;
	
	/** Local State of the Counters	 */
	protected final boolean[] unique;
	
	/** Vector to be returned by RandomFloat	 */
	final public float[] x;
	
	/** Pre-Calculated for Speed Optimization	 */
	final int maxValue; 
	
	/** Pre-Calculated for Speed Optimization	 */
	final float norm; 
	
	/** Local State of the Vector	 */
	//protected final RandomPseudo[] generators;
	
	/** Creates a Generator for {@code dim}-dimensional Vectors quantized to {@code maxOctave} Bits.
	 * @param dim
	 * @param maxOctave
	 */
	public RandomVectorPseudoSequential(final int dim, final int maxOctave) {
		unique = new boolean[dim]; 
		v = new int  [dim]; 
		x = new float[dim];
		/*generators = new RandomPseudo[dim]; 
		RandomPseudo parent = generators[0] = new RandomPseudo(maxOctave); 
		for(int i = 0; ++i < dim;) 
			parent = generators[i] = new RandomPseudo(parent);
		*/ 
		maxValue = 1 << maxOctave;
		norm = 1f/maxValue; 
		step = maxValue >> 1;
	}
	
	/** Advances to and returns the next integer Vector in the sequential Cover of the SuperCube. */
	public int[] nextInt() { calcNextInt(); return v; }

	/** Advances to and returns the next Vector, normalized to [0,1), only recomputing changed Indices. */
	public float[] nextFloat() {
		int minIndex = calcNextInt();
		if (minIndex < 0)
			minIndex = 0; 
		for(int i = v.length; --i>=minIndex;) 
			x[i] = v[i]*norm;
		return x;
	}
	
	/** Advances {@link #v} to its next State, starting the recursive search from Index 0.
	 * @return the lowest changed Index
	 */
	protected int calcNextInt() { return calcNextInt(0, false); }
	
	/**
	 * calculates the next Vector. 
	 * @param i Index into the Result Vector 
	 * @param uniqueParent Flag whether the Value Combination of the Parent Elements is unique so far. 
	 * @return the lowest changed Index
	 */
	protected int calcNextInt(final int i, final boolean uniqueParent) {
		boolean currUnique = unique[i]; 
		boolean isUnique = uniqueParent || currUnique; //
		for(;;) {
			if(i+1 < v.length) { //search in the highest Index 
				int ret = calcNextInt(i+1, isUnique);
				if (ret >= 0)
					return ret; 
			}
			currUnique = unique[i] = !currUnique; //every second Value is not unique! 
			if ((v[i] += step) >= maxValue)
				break; //give up...
			isUnique = uniqueParent || currUnique; //
			if (isUnique) 
				return i; //
		} 
		v[i] = 0; //reset the Counters
		if (i == 0) {
			if((step >>= 1) != 0) 
				return calcNextInt(0, false); //avoid duplicate 0,0,... Vectors
			step = maxValue >> 1; //reset the Step Size
		}
		return -1;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() {
		final int octave = 5; 
		final int maxVal = 1 << octave; 
		final int[][] matrix = new int[maxVal][maxVal]; 
		final RandomVectorPseudoSequential ran = new RandomVectorPseudoSequential(2, octave); 
		for(int i = maxVal*maxVal; --i >= 0;) {
			float[] v = ran.nextFloat(); L.n(v); 
		} //Generator generates only n*(n+1)/2 unique Values, the other ones are skipped! 
		for(int i = maxVal*maxVal; --i >= 0;) {
			int[] v = ran.nextInt(); L.n(v); 
			++matrix[v[0]][v[1]]; 
		} //Generator generates only n*(n+1)/2 unique Values, the other ones are skipped! 
		//how do you replay the previous Values? 
		L.n(matrix); 
		for(int i = matrix.length; --i >= 0; ) 
			for(int j = matrix.length; --j >= 0; ) 
				Assert.EQUALS(1, matrix[i][j]);
		
	}
	
	/** Main Method of this Class 	 */
	final static public void main(final String args[]) {
		testIt(); 
	}
	
}
