/*
 * Created on 06.06.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.vector;

import math.vector.VectorDouble;

/**
 * Stateless binary operator computing a running sum, for aggregating e.g. values in a matrix.
 *
 * Title: <p>
 * Description:
 * Purpose:
 * Implements the (stateless) Sum Operation.
 * Used as a binary Functor e.g. on summing up Values in a Matrix.
 *
 * Design Decisions / Implementation Details:
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
 * mtime: 2026-09-05T20:46:16Z
 * digest: 2bea29ffea4a92e634fd4c4ef250f701852750af7e5f1762d002f6513025f2a6
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class OpSum extends AOdeFloat {
	
	/** the single Instance of this binary Operator 	 */
	public static final OpSum OpSum = new OpSum(); 
	
	/** Adds y to the running Value x.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public double Funktion(final double x, final double y) { return x+y; }

	/** Adds x to each element of y into dydx.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		VectorDouble.ADD(dydx, y, x); }
	
}
