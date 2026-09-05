/*
 * Created on 06.06.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.vector;

import math.vector.VectorDouble;

/**
 * Stateless binary operator that keeps the last value seen, discarding the running Value, for
 * use e.g. building pivot matrices from streams of individual values/events.
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
 * mtime: 2026-09-05T20:45:30Z
 * digest: 262a821cb00f8a942fc08a90c58068866e3d7a8b06a28c84e2daffef47953ce2
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public final class OpLast extends AOdeFloat {

	/** the single Instance of this binary Operator 	 */
	public static final OpLast OpLast = new OpLast();

	/** Returns y, discarding the running Value x.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public double Funktion(final double x, final double y) { return y; }

	/** Copies y into dydx, discarding the running Value x.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		VectorDouble.COPY(y, dydx); }
	
}
