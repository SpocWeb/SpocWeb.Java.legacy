/*
 * Created on 06.06.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.vector;

import math.vector.VectorDouble;

/**
 * Stateless binary operator computing a running product, for aggregating e.g. pivot matrices.
 *
 * Title: <p>
 * Description:
 * Purpose:
 * Implements the (stateless) Prod Operation.
 * Used as a binary Functor e.g. on summing up Values in a Pivot-Matrix.
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
 * mtime: 2026-09-05T20:46:05Z
 * digest: 7cc9d05083c861c4c3a0a94dc89bd4d83061bcc17abcee81bfe42a76cc7ca4fc
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class OpProd extends AOdeFloat {
	
	/** the single Instance of this binary Operator 	 */
	public static final OpProd OpProd = new OpProd(); 
	
	/** Multiplies the running Value x by y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public double Funktion(final double x, final double y) { return x*y; }

	/** Multiplies each element of y by x into dydx.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		VectorDouble.MUL(dydx, y, x); }
	
}
