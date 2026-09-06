/*
 * File Name: WeightExp.java
 * Created on: 22.02.2004
 *
 */
package math.fit.weight;

import function.byref.ByRefDouble;

/**
 * Weighting function whose weight and probability density fall off exponentially with the
 * normalized deviation, giving outliers a bounded (not squared) influence.
 *
 * @author mheuer
 * @version	1.0
 * @see IWeightFunction the interface this implements
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:45:24Z
 * digest: 97d1334f12efa0f6529ee713b001873c3a1ab5ca980704a43ee565de390439f9
 * stale: false
 * tags: [code/weighting]
 * concepts: [Exponential Weight Function]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
final public class WeightExp
implements IWeightFunction {

	/** single Instance of this Class	 */
	public static final WeightExp SINGLETON = new WeightExp();

	/** private Constructor to enforce the Singleton	 */
	private WeightExp() { }

	/**
	 * Returns the exponential probability density {@code exp(-|d|)} at the given deviation.
	 * @see IWeightFunction#prob(double)
	 */
	public double prob(double d) { return Math.exp(-Math.abs(d)); }

	/**
	 * Returns the cumulative Laplace probability belonging to the density {@link #prob(double)},
	 * i.e. {@code 0.5*exp(d)} for negative and {@code 1-0.5*exp(-d)} for non-negative deviations.
	 * @see IWeightFunction#probCum(double)
	 */
	public double probCum(double d) {
		return (d < 0) ? 0.5*Math.exp(d) : 1-0.5*Math.exp(-d); }

	/**
	 * Returns the sign of the deviation as the weight, giving every point equal magnitude.
	 * @see IWeightFunction#weight(double)
	 */
	public double weight(double d) { return ByRefDouble.SIGN(d); }

	/**
	 * Returns the absolute deviation as the cumulative weight.
	 * @see IWeightFunction#weightCum(double)
	 */
	public double weightCum(double d) { return Math.abs(d); }

}
