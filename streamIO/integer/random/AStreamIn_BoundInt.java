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
 * saving a call to getMaxValue() for Performance Reasons. 	 */
public abstract class AStreamIn_BoundInt
extends AStreamIn_Bound
implements IStreamIn_Bound_Int, IStreamIn_Float {
	
	/**Modulus to keep the Values in Range	 */
	protected long maxValue;
	
	/** @return the minimum Value	 */
	public long getMinValue() { return 0; }
	
	/** @return the maximum Value	 */
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
		return nextInt((int) _maxLong); } //may not exhaust the Space up to MaxLong!
	
	//no Optimization, only with a cached MaxValue!
	
	/**Random single Precision Number from 0 to MaxFloat	 */
	public float nextFloat(final float _maxFloat) {
		return (_maxFloat*nextInt())/maxValue; }
	
	/**Random double Precision Number from 0 to MaxFloat	 */
	public double nextDouble(double _maxDouble) {
		return (_maxDouble*nextInt())/maxValue; }
	
}
