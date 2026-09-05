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

	// TODO: LOGIC: SINGLETON is a non-static instance field initialized by "new WeightExp()",
	// but the only constructor is this same private one, so instantiating the class recurses
	// into this same field initializer forever, causing a StackOverflowError. This field must
	// be "static" for the singleton pattern implied by the private constructor to work.
	/** single Instance of this Class	 */
	public WeightExp SINGLETON = new WeightExp();

	/** private Constructor to enforce the Singleton	 */
	private WeightExp() { }

	/**
	 * Returns the exponential probability density {@code exp(-|d|)} at the given deviation.
	 * @see IWeightFunction#prob(double)
	 */
	public double prob(double d) { return Math.exp(-Math.abs(d)); }

	// TODO: LOGIC: probCum returns the same formula as prob() (an unintegrated density)
	// instead of a cumulative probability, so callers relying on probCum for an integrated
	// value get the density instead; the existing "//TODO:" marks this as already known-unfinished.
	/**
	 * Returns the exponential probability density again instead of an integrated value.
	 * @see IWeightFunction#probCum(double)
	 */
	public double probCum(double d) { return Math.exp(-Math.abs(d)); } //TODO:

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
