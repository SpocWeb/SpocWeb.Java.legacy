package function.real;

/**
  * Title: Minimum<p>
  * Description:
  * Evaluates the Minimum of the Elements of the streamIO, but hands them on unchanged,
  * so also other Operations can take place on them.
  * Can filter in both Directions (as Input and Output streamIO!)
  * The current Minimum and Number of Elements can be queried anytime!
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:43:57Z
  * digest: 95a5d9cd8e516ffbe1ffbbbdbceb929abf05d525d3714c9659e559079c4113e8
  * stale: false
  * tags: [code/running_aggregates, code/mathematical_function]
  * concepts: [Streaming Numeric Aggregator]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class Minimum
	extends StatefulFloatFunction {

	/** Initializing Constructor
	  * defaulting the Start Value to +Infinity
	  */
	public Minimum() { super(Double.POSITIVE_INFINITY); }

	/** Initializing Constructor
	  * @param startValue_ Maximum Value returned by this Filter */
	public Minimum(double startValue_) { super(startValue_); }

	/** Updates the running minimum with {@code value_} and passes it through unchanged.
	 * @return the next single Precision Number	 */
	public float Map(final float value_) {
		++_Count; if (_Value > value_) _Value = value_; return value_; }

	/** Updates the running minimum with {@code value_} and passes it through unchanged.
	 * @return the next double Precision Number	 */
	public double Map(final double value_) {
		++_Count; if (_Value > value_) _Value = value_; return value_; }

}
