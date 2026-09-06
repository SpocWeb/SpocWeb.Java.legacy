package streamIO.integer.random;

import streamIO.integer.AStreamIn_Bound;
import streamIO.real.IStreamIn_Float;

/**Abstract Random Number Generator using an Integer (int) Generator
 * and emulating various Generators of other primitive Types.
 * Optimizations are very important, since most Algorithms with random Numbers
 * need many Data, for their Accuracy typically is of O(SqRt(n))
 * 
 * The Optimization here supports Generation of derived integer Numbers
 * without using float Point Arithmetics (Overflow possible though!)
 * and float Point Generators working without Norming twice,
 * first to the Range [0..1) and then to [Min..Max)
 * Expects the Subclasses to set MaxValue,
 * because it is being used for norming the Results,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:50:42Z
 * digest: 5c4d0a54a60ac3d4e02ace9e86abf196e2115634b8a22bc2f6259c4fdc1d6ff8
 * stale: false
 * tags: [code/random_number_generation, code/quasi_random_sequence]
 * concepts: [Pseudo-Random and Quasi-Random Integer Generator Family with Mark/Restore Replay]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * saving a call to getMaxValue() for Performance Reasons. 	 */
public abstract class AStreamIn_BoundInt
extends AStreamIn_Bound
implements IStreamIn_Bound_Int, IStreamIn_Float {
	
	/**Modulus to keep the Values in Range	 */
	protected long maxValue;
	
	/** Returns zero, the lower bound of every generator in this hierarchy.
	 * @return the minimum Value	 */
	public long getMinValue() { return 0; }

	/** Returns this generator's modulus.
	 * @return the maximum Value	 */
	public long getMaxValue() { return maxValue; }
	
	/** Initializing Constructor
	  * enforces Initialization of MaxValue */
	public AStreamIn_BoundInt(final long _maxValue) { this.maxValue = _maxValue; }
	
	/**Random Integer Number 	 */
	//public long nextLong() { return nextInt(); }
	
	//////////////////////////////
	//	Scaled random Numbers	//
	//////////////////////////////
	
	//Optimization: only Integer Arithmetics, only one Module Operation.
	
	/**Random Integer Number from 0 to MaxInt-1	 */
	public int nextInt(final int _maxInt) {
//		return	(int) (((long)nextInt()* _maxInt)/maxValue);}
		return  (nextInt()% _maxInt); } //may not equally distribute the Space up to MaxInt!
	
	/**Random Long Number from 0 to MaxLong-1	 */
	public long nextLong(final long _maxLong) {
		return (nextLong()% _maxLong); } //may not exhaust the Space up to MaxLong!
	
	//no Optimization, only with a cached MaxValue!
	
	/**Random single Precision Number from 0 to MaxFloat	 */
	public float nextFloat(final float _maxFloat) {
		return (_maxFloat*nextInt())/maxValue; }
	
	/**Random double Precision Number from 0 to MaxFloat	 */
	public double nextDouble(double _maxDouble) {
		return (_maxDouble*nextInt())/maxValue; }
	
}
