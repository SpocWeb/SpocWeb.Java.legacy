/*
 * Created on 06.06.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.vector;

import math.vector.VectorDouble;

/**
 * Stateless binary operator computing a running minimum, for aggregating e.g. pivot matrices
 * from streams of individual values/events.
 *
 * Title: <p>
 * Description:
 * Purpose:
 * Implements the Min Operation for aggregating e.g. Pivot-Matrices
 * from Streams of individual Values/Events.
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:45:53Z
 * digest: 36fb1c97615563355f27d726f47af919e2c4682246f0076fb009980ea3f692d9
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class OpMin extends AOdeFloat {
	
	/** the single Instance of this binary Operator 	 */
	public static final OpMin OpMin = new OpMin(); 
	
	/** Returns the smaller of x and y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public double Funktion(final double x, final double y) {
		if (x < y)
			return x;
		return y; }

	/** Fills dydx with the smaller of x and each element of y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		VectorDouble.MIN(dydx, y, y.length, x); }
	
}
