/*
 * File Name: SinOfDistDivDist.java
 * Created on: 20.03.2004
 *
 */
package math.minimizer;

import math.vector.VectorDouble;
import math.vector.VectorFloat;
import function.vector.IFloatScalarField;

/**
 * This Class implements a smooth Test Function 
 * that returns the negative Sine of the Square of the Euklidean Distance of the Input
 * to a certain Vector or the Origin divided by this Distance. 
 * This Function has a local Minimum in the Origin 
 * surrounded by Rings of lesser Minima and Maxima. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:45:40Z
 * digest: 54f29d8dca6bedeba2a72b2bd93bd6a8a35337ca009587411ca0b1ecf82180b3
 * stale: false
 * tags: [code/test_fixture]
 * concepts: [Sinc-Like Test Function]
 * facets: {layer: test, status: broken, complexity: low}
 * -->
 */
class SinOfDistDivDist
implements IFloatScalarField {
	
	/** Creates an instance with no center, so the field is measured from the origin. */
	public SinOfDistDivDist() {}

	/**
	 * Creates an instance measuring distance from the given center vector.
	 *
	 * @param center_ the vector subtracted from the argument before evaluating the field
	 */
	public SinOfDistDivDist(final double[] center_) { this.center = center_; }

	/** this Vector is subtracted from the Argument if not null */
	public double[] center;

	/**
	 * Returns the negative sine of the squared distance from {@link #center} (or the
	 * origin), divided by that same squared distance.
	 *
	 * @param V the point to evaluate
	 * @return the field value at {@code V}
	 */
	public double Map(final double[] V) {
		final double ret;
		if (center == null) {
			ret = VectorDouble.NORM_SQR(V);
		} else {
			ret = VectorDouble.DIST_SQR(V, center);
		}
		// TODO: LOGIC: ret is exactly 0.0 when V equals center (or the origin), so
		// -Math.sin(ret)/ret computes 0.0/0.0 = NaN instead of the mathematical limit
		// -1.0; a minimizer that converges exactly onto the minimum observes NaN here.
		return -Math.sin(ret)/ret; }

	/**
	 * Returns the negative sine of the squared distance from {@link #center} (or the
	 * origin), divided by that same squared distance.
	 *
	 * @param V the point to evaluate
	 * @return the field value at {@code V}
	 */
	public float Map(final float[] V) {
		final double ret;
		if (center == null) {
			ret = VectorFloat.NORM_SQR(V);
		} else {
			ret = VectorFloat.DIST_SQR(V, center);
		}
		// TODO: LOGIC: ret is exactly 0.0 when V equals center (or the origin), so
		// -Math.sin(ret)/ret computes 0.0/0.0 = NaN instead of the mathematical limit
		// -1.0; a minimizer that converges exactly onto the minimum observes NaN here.
		return (float) (-Math.sin(ret)/ret); }

}
