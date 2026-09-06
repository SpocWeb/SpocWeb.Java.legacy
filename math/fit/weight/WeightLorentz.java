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

	/** single Instance of this Class	 */
	public static final WeightLorentz SINGLETON = new WeightLorentz();

	/** private Constructor to enforce the Singleton	 */
	private WeightLorentz() { }

	/**
	 * Returns the Lorentzian probability density {@code 1/(1+d^2/2)} at the given deviation.
	 * @see IWeightFunction#prob(double)
	 */
	public double prob(double d) { return 1/(1+d*d*0.5); }

	/**
	 * Returns the cumulative Lorentzian probability belonging to the density
	 * {@link #prob(double)}, i.e. {@code 0.5 + atan(d/sqrt(2))/PI}.
	 * @see IWeightFunction#probCum(double)
	 */
	public double probCum(double d) {
		return 0.5 + Math.atan(d/Math.sqrt(2))/Math.PI; }

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
