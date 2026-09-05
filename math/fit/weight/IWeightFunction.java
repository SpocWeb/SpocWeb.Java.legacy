/*
 * File Name: IWeightFunction.java
 * Created on: 22.02.2004
 *
 */
package math.fit.weight;

/**
 * Defines a weighting function over the normalized deviation of a measured or random value
 * from a distribution's mean.
 *
 * <p>The weighting function is defined by the shape of the distribution: relative weight
 * should first increase with the deviation {@code d}, but as soon as {@code |d|} becomes
 * larger than 1, it should decrease, so outliers don't get too much weight in the estimation.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:45:13Z
 * digest: 7cdc963c0ff0591d7b3eaea529d914fba3a658dbaba4a2f92271cb041e47e9e0
 * stale: false
 * tags: [code/weighting]
 * concepts: [Weight Function Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IWeightFunction {

	/**
	 * Returns the probability density of the distribution at the given normalized deviation.
	 * @param d the normed Deviation: (y-yAvg)/stdDev
	 * @return the Probability(-density) for this Deviation
	 */
	double prob(double d);

	/**
	 * Returns the cumulative probability of the distribution up to the given normalized
	 * deviation.
	 * @param d the normed Deviation: (y-yAvg)/stdDev
	 * @return the integrated Probability for this Deviation
	 */
	double probCum(double d);

	/**
	 * Returns the weight to apply to a value at the given normalized deviation, penalizing
	 * outliers.
	 * @param d the normed Deviation: (y-yAvg)/stdDev
	 * @return the Weight for this Deviation
	 */
	double weight(double d);

	/**
	 * Returns the cumulative weight integrated up to the given normalized deviation.
	 * @param d the normed Deviation: (y-yAvg)/stdDev
	 * @return the integrated Weight for this Deviation
	 */
	double weightCum(double d);

}
