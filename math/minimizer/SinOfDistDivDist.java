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
 */
class SinOfDistDivDist
implements IFloatScalarField {
	
	public SinOfDistDivDist() {}
	
	public SinOfDistDivDist(final double[] center_) { this.center = center_; }
	
	/** this Vector is subtracted from the Argument if not null */
	public double[] center;

	public double Map(final double[] V) {
		final double ret;
		if (center == null) {
			ret = VectorDouble.NORM_SQR(V);
		} else {
			ret = VectorDouble.DIST_SQR(V, center); 
		}
		return -Math.sin(ret)/ret; }

	public float Map(final float[] V) {
		final double ret;
		if (center == null) {
			ret = VectorFloat.NORM_SQR(V);
		} else {
			ret = VectorFloat.DIST_SQR(V, center); 
		}
		return (float) (-Math.sin(ret)/ret); }

}
