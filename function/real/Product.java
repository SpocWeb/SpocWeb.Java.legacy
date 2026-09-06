package function.real;

/**
  * Title: Product<p>
  * Description:
  * Multiplies the Elements of the streamIO into a running Product, but hands them on unchanged,
  * so also other Operations can take place on them.
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
  * mtime: 2026-09-05T16:44:42Z
  * digest: 350e2b4852860aaf8ca83c78dc698282926d636ed6e2b44c8a0f7d5fcbd8dc69
  * stale: false
  * tags: [code/running_aggregates, code/mathematical_function]
  * concepts: [Streaming Numeric Aggregator]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class Product 
	extends StatefulFloatFunction {

	/** Initializing Constructor
	  * defaulting the Value to 0.0
	  * @param Generator the actual IStreamInNumber 	*/
//	public Product() { super(); }

	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param Scale     Start Value returned by this Filter */
	public Product(final double value_) { super(value_); }

	/** Running Sum of the Reciprocals of all Values passed through, needed for {@link #getHMV()} */
	protected double _Reciprocals;

	/** Multiplies the running product by {@code value_} and passes it through unchanged.
	 * @return the next single Precision Number	 */
	public float Map(final float value_) { //
		++_Count; _Value *= value_; _Reciprocals += 1.0 / value_; return value_; }

	/** Multiplies the running product by {@code value_} and passes it through unchanged.
	 * @return the next double Precision Number	 */
	public double Map(final double value_) { //
		++_Count; _Value *= value_; _Reciprocals += 1.0 / value_; return value_; }

	/** Returns the harmonic mean of every value passed through this node so far.
	 * @return the HMV, Harmonic Mean Value
	  * of the Elements passed through this Node
	  * @throws NAN when no Element has passed this Node yet.  */
	public double getHMV() {
		return _Count / _Reciprocals;
	}

	/** Returns the geometric mean (the Nth Root of the running Product) of every value
	 * passed through this node so far.
	 * @return the GMV, Geometric Mean Value
	  * of the Elements passed through this Node
	  * @throws NAN when no Element has passed this Node yet.  */
	public double getGMV() {
		return Math.pow(_Value, 1.0 / _Count);
	}

}
