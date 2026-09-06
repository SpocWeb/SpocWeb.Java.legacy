/*
 * Created on 15.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.random;

import function.IIntFunction;

/**
 * Adds a configurable amount of Noise to the Bits handed over.
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:51:32Z
 * digest: 7717ef41157b610dcd072cafafc7273ff74abbf9e12d1779f086b0d874c7984c
 * stale: false
 * tags: [code/random_number_generation, code/quasi_random_sequence]
 * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class BitNoise 
implements IIntFunction {

	/** the Noise Generator to use.	 */
	final public IStreamIn_Bound_Int rnd; 
	
	/** the average Number of Bits before the next Bit Flip	 */
	public int flipDistance; 
	
	/** to allow for more or less Bits set in the given Bytes	 */
	public int bitsPerValue; 
	
	/** The next Bit to flip	 */
	int nextBit; 
	
	/**
	 * Creates a noise function flipping roughly one bit every flipDistance values.
	 * @param _rnd the Noise Generator to use.
	 */
	public BitNoise(final IStreamIn_Bound_Int _rnd, final int _flipDistance, final int _bitsPerValue) {
		this.rnd = _rnd; 
		this.flipDistance = _flipDistance; 
		this.bitsPerValue = _bitsPerValue; 
		this.nextBit = rnd.nextInt(flipDistance); 
	}
	
	/** Flips one Bit of {@code value} whenever the internal Countdown reaches Zero.
	 * @see function.IIntFunction#Map(long)	 */
	public long Map(long value) {
		if (nextBit >= bitsPerValue) {
			nextBit -= bitsPerValue;
		} else {
			nextBit  = rnd.nextInt(flipDistance);
			value ^= (1 << nextBit);
		}
		return value;
	}

	/** Flips one Bit of {@code value} whenever the internal Countdown reaches Zero.
	 * @see function.IIntFunction#Map(int)	 */
	public int Map(int value) {
		if (nextBit >= bitsPerValue) {  
			nextBit -= bitsPerValue; 
		} else {
			nextBit  = rnd.nextInt(flipDistance);
			value ^= (1 << nextBit);			
		}
		return value; 
	}
	
	/** Currently runs no demonstration; present as an entry point for manual testing. */
	public static void main(final String[] args) throws Exception {
	}
	
}
