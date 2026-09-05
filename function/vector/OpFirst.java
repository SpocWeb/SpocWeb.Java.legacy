/*
 * Created on 06.06.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.vector;

import math.vector.VectorDouble;

/**
 * Stateless binary operator that keeps the first (non-zero, non-NaN) value seen, for use e.g.
 * building pivot matrices from streams of individual values/events.
 *
 * Title: <p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
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
 * mtime: 2026-09-05T20:45:20Z
 * digest: 235aff503f0ff465d7ed2900888ff79c087725d2d547bde928db7d1b57317bcf
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class OpFirst extends AOdeFloat {

	/** the single Instance of this binary Operator 	 */
	public static final OpFirst OpFirst = new OpFirst();

	/** Returns the running Value x, unless it is still the zero/NaN sentinel, in which case y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public double Funktion(final double x, final double y) {
		if ((x == 0) ||
			(x != x)) //NaN
			return y;
		return x; }

	/** Fills dydx with the smaller of x and each element of y (used to latch the first value).
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		VectorDouble.MIN(dydx, y, y.length, x); }
	
}
