package function.real;

/** Random Number Filter Generator with a white Noise Spectrum,
  * i.e. the Power falls like f^-2 = 1/(f*f)
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:43:41Z
  * digest: d30363cfc46960a78f8390cb55bec8c712158a4d72bf3f945802bef9125ec13d
  * stale: false
  * tags: [code/running_aggregates, code/mathematical_function]
  * concepts: [Streaming Numeric Aggregator]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class LinearScale
extends Multiplier {

	/** Shift / Offset taking the Generator MaxValue into Account
	  * Type is long, not double, because an Integer Generator is assumed. */
	protected double shift;

	/** Initializing Constructor for a White Noise Generator
	  * just Defaults the Scale of the Random Generator to [-1,+1]
	  * so that the integrated Result stays bounded around Zero.
	  * Min to -1
	  * Max to +1	 */
	public LinearScale() { this(-1.0, +1.0); }

	/** Initializing Constructor for a Generator
	  * delivering random Values between Min and Max	 */
	public LinearScale(double Min, double Max) {
		super(Max - Min);
		shift = Min / _Value; //rounding should not be necessary!
	}

	/** @return the minimum Value for other Classes to determine
	  * The Type is chosen to be double,
	  * because this Value is supposed to be tested only once.  */
//	public double getMinValue() { return value * (shift + ((IStreamIn_Bound_Int) InStream).getMinValue()); }

	/** @return the maximum Value for other Classes to determine
	  * The Type is chosen to be double,
	  * because this Value is supposed to be tested only once.  */
//	public double getMaxValue() { return value * (shift + ((IStreamIn_Bound_Int) InStream).getMaxValue()); }

	/** Random Integer Number from Shift to Shift+MaxLong-1	 */
//	public long nextLong() { return Generator.nextLong() + Shift; }

	/** Random Integer Number from Shift to Shift+MaxLong-1	 */
//	public int nextInt() { return Generator.nextLong() + Shift; }

	/** Scales and shifts {@code value_} by the configured range.
	 * @return the next Random single Precision Number	 */
	public float Map(final float value_){ return (float) (_Value*(value_ + shift)); }

	/** Scales and shifts {@code value_} by the configured range.
	 * @return the next Random double Precision Number	 */
	public double Map(final double value_){ return _Value*(value_ + shift); }

}
