/*
 * Created on 06.06.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.vector;

import math.vector.VectorDouble;

/**
 * Stateless binary operator computing a running maximum, for aggregating e.g. pivot matrices
 * from streams of individual values/events.
 *
 * Title: <p>
 * Description:
 * Purpose:
 * Implements the Max Operation for aggregating e.g. Pivot-Matrices
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
 * mtime: 2026-09-05T20:45:41Z
 * digest: 4a53a237bdf7fb24d5a402431deb7833dce002e7e4aaa4a6b36e2cd908ddec00
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class OpMax extends AOdeFloat {
	
	/** the single Instance of this binary Operator 	 */
	public static final OpMax OpMax = new OpMax(); 
	
	/** Returns the larger of x and y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public double Funktion(final double x, final double y) {
		if (x > y)
			return x;
		return y; }

	/** Fills dydx with the larger of x and each element of y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		VectorDouble.MAX(dydx, y, y.length, x); }
	
}
