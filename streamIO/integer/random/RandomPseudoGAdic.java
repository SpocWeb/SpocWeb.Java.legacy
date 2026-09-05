/*
 * Pseudo-Random Number Generator for arbitrary base Numbers (preferably Primes). 
 * Created on 27.07.2005
 *
 */
package streamIO.integer.random;

import java.io.Console;

import streamIO.Assert;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.object.IStreamIn;
import function.byref.ByRefInt;
import function.byref.ByRefLong;
import function.byref.combinatoric.Prime;

/**
 * Pseudo-Random Number Generator for arbitrary base Numbers (preferably Primes). 
 * Only Generators with different (prime) Bases per Dimension can be combined 
 * into a multidimensional Pseudo-Random Vector Generator. 
 * @see streamIO.integer.random.RandomPseudoBinary, a very fast Algorithm for a binary Generator. 
 * @see streamIO.vector.random.RandomVectorPseudoSequential creates Vectors directly, 
 * but returns Values in ascending Order, 
 * so you have to work through the whole Set of one Granularity to get unbiased Estimates. 
 * @see streamIO.vector.random.RandomVectorPseudo creates Vectors directly, 
 * and uses Cantor's Algorithm to avoid sequential Data. 
 * 0+0, 
 * 0+1, 1+0
 * 0+2, 1+1, 2+0
 * 0+3, 1+2, 2+1, 3+0 
 * etc. 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:56:36Z
 * digest: 23403b54a571279179142ac8ab3f6b303f4454385f92f1144f4ad1002be0ccc3
 * stale: false
 * tags: [code/random_number_generation, code/quasi_random_sequence]
 * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class RandomPseudoGAdic 
extends ARandomInt {

	private static final Log L = new Log(RandomPseudoGAdic.class); 
	
	/** 
	 * returns an Array of uncorrelated Pseudo-random Generators. 
	 * The Bases of these Generators are different Primes, 
	 * so the returned Values are not correlated. 
	 * @param dim the Number of independent Generators. 
	 * @param power the minimum Period of the Generators (Degree of the gAdic Representations).  
	 * @return an Array of uncorrelated Pseudo-random Generators 
	 */
	final static public RandomPseudoGAdic[] GET_RANDOM_VECTOR(final int dim, final long minPeriod) {
		final RandomPseudoGAdic[] ret = new RandomPseudoGAdic[dim]; 
		for(int i = ret.length; --i >= 0; ) {
			final int base = Prime.NUMBER(i); 
			ret[i] = new RandomPseudoGAdic(base, 
					(byte)(1+ByRefLong.LOG(minPeriod, base)));
		}
		return ret; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/** the current Number in the given Base with 'octave' #Digits. 	*/
	protected final int[] v; 
	
	/** the Logarithm Base 	*/
	protected final int base; 
	
	/** Reports this Generator's fixed sub-random Order.
	 * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return IStreamIn.ORDER_RANDOM_SUB; }

	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return maxValue; }
	
	/**Decomposes {@code _seed} into this Generator's gAdic Digit Vector {@link #v}.
	 * Changed Semantics! instead of returning to the indicated Position,
	 * this Method reSets the internal random Value.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) {
		long val = _seed;
		int i = -1; 
		for(; val != 0; ) { //parse _seed into gAdic Representation
			long newVal = val / base; //the Quotient
			v[++i] = (int)(val - newVal*base); //the Remainder
			val = newVal; 
		}
		for(; ++i < v.length;)
			v[i] = 0; 
		return _seed; } 
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return currItem.Value; } 
	
	/** Resets this generator's gAdic Digit Vector to represent 0.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { reSet(0); return this; }

	/** Creates a Generator with the given Base and Digit Count (Power).
	 * @param _base  the maximum Value of this Generator is given by base^power
	 * @param _power the maximum Value of this Generator is given by base^power
	 */
	public RandomPseudoGAdic(final int _base, final byte _power) {
		super(ByRefInt.POW(_base, _power));
		v = new int[_power]; 
		base = _base; 
	}
	
	/** @see streamIO.real.AAStreamIn_Float#nextInt()	 */
	protected long nextLongInternal() {
		final int ret = reverseGAdic(); //returns 0 first!  
		inc();
		return ret; 
	}
	
	/**
	 * @return the Value of the reverse gAdic Vector Representation in v
	 */
	private int reverseGAdic() {
		int ret = v[0]; 
		for(int i = 0; ++i < v.length;)
			ret = ret*base + v[i]; 
		return ret;
	}
	
	/** increments the current Number	 */
	private void inc() {
		for(int i = -1; (++i < v.length); ) {
			if (++v[i] < base)
				break; 
			v[i] = 0; //gAdic Incrementation
		}
	}
	
	/** Cross-checks this Generator's Base-2 Sequence against {@link RandomPseudoBinary}
	 * and prints a Base-3 Sequence for visual inspection.	 */
	public static void testIt() {
		final RandomPseudoGAdic  bin1 = new RandomPseudoGAdic(2, (byte)4); bin1.nextInt();
		final RandomPseudoBinary bin2 = new RandomPseudoBinary(4); 
		for(int i = 40; --i >= 0;) {
			Assert.EQUALS(bin1.nextInt(), bin2.nextInt());
			//System.out.println(bin1.nextInt() + "\t" + bin2.nextInt()); 
		}
		final RandomPseudoGAdic  bin3 = new RandomPseudoGAdic(3, (byte)4); 
		for(int i = 40; --i >= 0;)
			L.n().l(bin3.nextFloat()); 
		GET_RANDOM_VECTOR(5, 8); 
	}
	
	/** Runs {@link #testIt()} as a Command-Line Entry Point.	 */
	public static void main(final String[] args) {
		testIt();
	}
	
}
