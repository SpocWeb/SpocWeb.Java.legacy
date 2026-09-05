/*
 * File Name: AOdeFloat.java
 * Created on: 24.01.2004
 *
 */
package function.vector;

/**
 * Base class for a binary real-valued operation ({@link IBinaryOpFloat}), adding a {@code float}
 * overload of {@code Funktion} that delegates to the {@code double} implementation.
 *
 * Title: AOdeFloat<p>
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
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:44:55Z
 * digest: a6366f6b9fadb6cb050ff127748ca985b5ba5796d3d7077f9789e54c0ca416e6
 * stale: false
 * tags: [code/differential_integration, code/vector_math]
 * concepts: [ODE Integration]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public abstract class AOdeFloat 
implements IBinaryOpFloat {

	/** Widens both arguments to {@code double} and delegates to {@link #Funktion(double, double)}.
	 * @see function.vector.IBinaryOpFloat#Funktion(float, float)	 */
	public float Funktion(float x, float y) {
		return (float) Funktion((double) x, (double) y); }

	/** Returns the 1st derivative in x of this function at point y.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public abstract double Funktion(double x, double y);

	/** Writes the 1st derivative in x of every coordinate of y into dydx.
	 * @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public abstract void Funktion(double x, double[] y, double[] dydx);

}
