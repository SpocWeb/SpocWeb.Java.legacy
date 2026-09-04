package streamIO.real;

import streamIO.integer.random.IStreamIn_Bound_Int;

/** Random Number Filter Generator with a white Noise Spectrum,
  * i.e. the Noise Power is constant with the Frequency 
  *
  * A uniform Random Noise Generator also generates a uniform
  * Power Spectrum (heuristic Reason: Spectrum is as Random as the Input!)
  * Since P(0) = Infinity, the Signal Value could exceed any Bound.
  * Thus the Range is renormed to [-1,+1]
  *
  * Implements an Optimization by precalculating the Scaling Factor
  * and the Offset, taking the Generator's MaxValue into Account
  * thus	avoiding one float Point Multiplication
  * and optionally replacing one float Point Subtration
  * by an integer Subtraction.
  */
public class FilterInLin
extends FilterInMul {

	/** Shifting Factor taking the Generator MaxValue into Account
	  * Type is long, not double, because an Integer Generator is assumed. */
	protected long shift;

	/** Initializing Constructor for a White Noise Generator
	  * just Defaults the Scale of the Random Generator to [-1,+1]
	  * so that the integrated Result stays bounded around Zero.
	  * Min to -1
	  * Max to +1	 */
	public FilterInLin(IStreamIn_Bound_Int generator) { this(generator, -1.0, +1.0); }

	/** Initializing Constructor for a Generator
	  * delivering random Values between Min and Max	 */
	public FilterInLin(IStreamIn_Bound_Int generator, double min, double max) {
		super(generator, max - min);
		shift = Math.round(min / scale); //rounding should not be necessary!
	}

	/** Random Integer Number from Shift to Shift+MaxLong-1	 */
	public long nextLong() { return inStream.nextLong() + shift; }

	/** Random Integer Number from Shift to Shift+MaxLong-1	 */
	public int nextInt() { return inStream.nextInt() + (int) shift; }

	/** Random double Precision Number	 */
	public double nextDoubleInternal() { return scale*(inStream.nextLong() + shift); }

}
