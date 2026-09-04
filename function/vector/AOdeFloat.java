/*
 * File Name: AOdeFloat.java
 * Created on: 24.01.2004
 *
 */
package function.vector;

/**
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
 */
public abstract class AOdeFloat 
implements IBinaryOpFloat {

	/** @see function.vector.IBinaryOpFloat#Funktion(float, float)	 */
	public float Funktion(float x, float y) {
		return (float) Funktion((double) x, (double) y); }

	/** @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public abstract double Funktion(double x, double y);

	/** @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public abstract void Funktion(double x, double[] y, double[] dydx);

}
