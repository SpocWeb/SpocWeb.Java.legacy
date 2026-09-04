/*
 * Created on 06.06.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.vector;

import math.vector.VectorDouble;

/**
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
 */
public class OpCount 
extends AOdeFloat {
	
	/** the single Instance of this binary Operator 	 */
	public static final OpCount OpCount = new OpCount(); 
	
	/** @see function.vector.IBinaryOpFloat#Funktion(double, double)	 */
	public double Funktion(final double x, final double y) { return x+1; }
	
	/** @see function.vector.IBinaryOpFloat#Funktion(double, double[], double[])	 */
	public void Funktion(final double x, final double[] y, final double[] dydx) {
		VectorDouble.ADD(dydx, 1); }
	
}
