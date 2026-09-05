/*
 * Created on 26.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.random;

import streamIO.Assert;
import streamIO.IReSetAble;
import streamIO.object.IStreamIn;
import function.IIntFunction;
import function.byref.ByRefInt;
import function.byref.ByRefLong;

/** 
 * Generates a Sub-Random Binary Sequence that equally fills up any given Space. 
 * (Halton's quasi-random sequence 7.7)
 * This can be very well implemented in Assembler.
 * 
 * The Bit Sequence of the Counter is simply reverted and the Dot placed in front of it, 
 * so that the resulting Sequence is:
 * 000, 001, 010, 011, 100, 101, ...
 *.000,.100,.010,.110,.001,.101, ...
 *   0,.5  ,.25 ,.75 ,.125,.625, ...   
 * 
 * @see streamIO.integer.random.RandomPseudoGAdic To get a sequence of n-tuples in n-space, 
 * you make each Component a Halton sequence with a different prime base b. 
 * Typically, the first n Primes are used. 
 * @see streamIO.vector.random.RandomVectorPseudo uses Cantor's Algorithm 
 * to create a Cross Product of several binary Generators. 
 * 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:56:16Z
 * digest: ba8ca2ac6d7ed571f14dcd72c474278234796a354d501a8550ba56bb5007264b
 * stale: false
 * tags: [code/random_number_generation, code/quasi_random_sequence]
 * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class RandomPseudoBinary 
extends ARandomInt 
implements IIntFunction {
	
	/** the Binary Logarithm of the maximum Value 	*/
	protected final int octave; 
	
	/** Local Storage for the current State of the Generator.
	 * Required, since the returned Value is a Transformation.	 */
	protected int value;	//
	
	/** Reports this Generator's fixed sub-random Order.
	 * @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return IStreamIn.ORDER_RANDOM_SUB; }

	/** Resets this generator's Counter, so the next Value is 0.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { value = -1; return this; }
	
	/**Changed Semantics! instead of returning to the indicated Position, 
	 * this Method reSets the internal random Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public long reSet(final long _seed) { return value = (int) _seed; }
	
	/**Changed Semantics! Always returns the Period of the random Numbers, 
	 * which is at most the Modulus, but only if Factor and Increment are chosen carefully!   
	 * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return maxValue; }
	
	/**Changed Semantics! Always returns the full internal random Value 
	 * to be cached on mark() and restored on reSet()  
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return value; } 
	
	/** Reference to the Parent Generator to coordinate with, didn't suffice though! 	 */
	//protected final RandomPseudoBinary parent; 
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Creates a Generator with the given Octave, i.e. Number of Bits.
	 * @param maxValue_ the maximum Value of this Generator is given by 2_maxOctav
	 */
	public RandomPseudoBinary(final int _maxOctave) {
		super(1 << _maxOctave);
		octave  =  _maxOctave ;
		//this.parent = null;
	}

	/** Creates a Generator sharing the same Octave as an existing one.
	 * @param maxValue_ the maximum Value of this Generator is given by 2_maxOctav
	 */
	public RandomPseudoBinary(final RandomPseudoBinary _parent) {
		super((int) _parent.maxValue);
		octave = _parent.octave;
		//parent = _parent; 
	}
	
	/** @see streamIO.real.AAStreamIn_Float#nextInt()	 */
	protected long nextLongInternal() {
		if((++value >= maxValue))// || ((parent != null) && (value > parent.value))) 
			  value  = 0;  
		return ByRefInt.REVERT(value, octave); }
	
	///////////////////////////////////////////////////////////////////////////
	/// IIntFunction allows to directly return the random Values by Index. 
	///////////////////////////////////////////////////////////////////////////
	
	/** Bit-reverses {@code _value} within this Generator's Octave.
	 * @see function.IIntFunction#Map(long)	 */
	public long Map(final long _value) { return ByRefLong.REVERT(_value, octave); }

	/** Bit-reverses {@code _value} within this Generator's Octave.
	 * @see function.IIntFunction#Map(int)	 */
	public int Map(final int _value) { return ByRefInt.REVERT(_value, octave); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** tests all Methods of this Class 	 */
	final static public void testIt() {
		final RandomPseudoBinary ran = new RandomPseudoBinary(12); 
		Assert.EQUALS(.0  , ran.nextDouble()); 
		Assert.EQUALS(.5  , ran.nextDouble()); 
		Assert.EQUALS(.25 , ran.nextDouble()); 
		Assert.EQUALS(.75 , ran.nextDouble()); 
		Assert.EQUALS(.125, ran.nextDouble()); 
		Assert.EQUALS(.625, ran.nextDouble()); 
	}
	
	/** Main Method of this Class 	 */
	final static public void main(final String args[]) {
		testIt(); 
	}

}
