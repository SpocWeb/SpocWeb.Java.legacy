/*
 * File Name: WeightGauss.java
 * Created on: 22.02.2004
 *
 */
package math.fit.weight;

import function.derive.ring.body.Gauss;

/**
 * Weighting function for normal ("gaussian") distributions, delegating its probability
 * density and cumulative probability to {@link Gauss}.
 *
 * @author mheuer
 * @version	1.0
 * @see IWeightFunction the interface this implements
 * @see Gauss the shared gaussian probability implementation this delegates to
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:45:43Z
 * digest: 04b138ed4213168680e5974fb925e70421efa8b8b1352c719547cdc9eb634fdc
 * stale: false
 * tags: [code/weighting]
 * concepts: [Gaussian Weight Function]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
final public class WeightGauss
implements IWeightFunction {

	// TODO: LOGIC: SINGLETON is a non-static instance field initialized by "new WeightGauss()",
	// but the only constructor is this same private one, so instantiating the class recurses
	// into this same field initializer forever, causing a StackOverflowError. This field must
	// be "static" for the singleton pattern implied by the private constructor to work.
	/** single Instance of this Class	 */
	public WeightGauss SINGLETON = new WeightGauss();

	/** private Constructor to enforce the Singleton	 */
	private WeightGauss() { }

	/**
	 * Returns the gaussian probability density at the given deviation, delegating to
	 * {@link Gauss#pGauss(double)}.
	 * @see IWeightFunction#prob(double)
	 */
	public double prob(double d) { return Gauss.pGauss(d); }

	/**
	 * Returns the cumulative gaussian probability at the given deviation, delegating to
	 * {@link Gauss#pGaussCum(double)}.
	 * @see IWeightFunction#probCum(double)
	 */
	public double probCum(double d) { return Gauss.pGaussCum(d); }

	/**
	 * Returns the deviation itself as the weight.
	 * @see IWeightFunction#weight(double)
	 */
	public double weight(double d) { return d; }

	/**
	 * Returns half the squared deviation as the cumulative weight.
	 * @see IWeightFunction#weightCum(double)
	 */
	public double weightCum(double d) { return d*d*0.5; }

}
