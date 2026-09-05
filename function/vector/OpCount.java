/*
 * Created on 06.06.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.vector;

import math.vector.VectorDouble;

/**
 * Stateless binary operator counting the elements aggregated so far, for use e.g. building
 * pivot matrices from streams of individual values/events.
 *
 * Title: <p>
 * Description:
 * Purpose:
 * Implements the Counting Operation for aggregating e.g. Pivot-Matrices
 * from Streams of individual Values/Events.
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
 * mtime: 2026-09-05T20:45:08Z
 * digest: f6df7bd06bda38f96a4c6aac86e6e580cae24963413ae70d4aae1e4d54b42523
 * stale: false
 * tags: [code/vector_math, code/function_composition]
 * concepts: [Vector Field Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class OpCount 
extends AOdeFloat {
	
	/** the single Instance of this binary Operator 	 */
	public static final OpCount OpCount = new OpCount(); 
	
	/** Increments the running count x by one, ignoring y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public double Funktion(final double x, final double y) { return x+1; }

	/** Increments every element of the running count dydx by one, ignoring x and y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		VectorDouble.ADD(dydx, 1); }
	
}
