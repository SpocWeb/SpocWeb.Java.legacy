/*
 * File Name: WeightLorentz.java
 * Created on: 22.02.2004
 *
 */
package math.fit.weight;

/**
 * Weighting function for Lorentzian ("Cauchy") distributions, whose weight and probability
 * density fall off with the square of the normalized deviation rather than exponentially.
 *
 * @author mheuer
 * @version	1.0
 * @see IWeightFunction the interface this implements
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:45:55Z
 * digest: 15584148ca38ee244ea416d87eadb60d734fffe61a06737f152dc4f9eaa8be9f
 * stale: false
 * tags: [code/weighting]
 * concepts: [Lorentzian Weight Function]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
public class WeightLorentz
implements IWeightFunction {

	// TODO: LOGIC: SINGLETON is a non-static instance field initialized by "new WeightLorentz()",
	// but the only constructor is this same private one, so instantiating the class recurses
	// into this same field initializer forever, causing a StackOverflowError. This field must
	// be "static" for the singleton pattern implied by the private constructor to work.
	/** single Instance of this Class	 */
	public WeightLorentz SINGLETON = new WeightLorentz();

	/** private Constructor to enforce the Singleton	 */
	private WeightLorentz() { }

	/**
	 * Returns the Lorentzian probability density {@code 1/(1+d^2/2)} at the given deviation.
	 * @see IWeightFunction#prob(double)
	 */
	public double prob(double d) { return 1/(1+d*d*0.5); }

	// TODO: LOGIC: probCum returns the same exponential-tail formula as WeightExp's probCum
	// instead of a Lorentzian-consistent cumulative (e.g. based on atan for prob(d)=1/(1+d^2/2)),
	// so it is inconsistent with this class's own prob()/weight()/weightCum() formulas.
	/**
	 * Returns the exponential-tail cumulative formula, not the Lorentzian one — see the
	 * TODO: LOGIC note above.
	 * @see IWeightFunction#probCum(double)
	 */
	public double probCum(double d) { return Math.exp(-Math.abs(d)); } //TODO:

	/**
	 * Returns the Lorentzian weight {@code d/(1+d^2/2)} at the given deviation.
	 * @see IWeightFunction#weight(double)
	 */
	public double weight(double d) { return d/(1+d*d*0.5); }

	/**
	 * Returns the log-based cumulative weight {@code ln(1+d^2/2)} at the given deviation.
	 * @see IWeightFunction#weightCum(double)
	 */
	public double weightCum(double d) { return Math.log(1+d*d*0.5); }

}
