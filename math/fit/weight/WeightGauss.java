/*
 * File Name: WeightGauss.java
 * Created on: 22.02.2004
 *
 */
package math.fit.weight;

import function.derive.ring.body.Gauss;

/**
 * Title: WeightGauss<p>
 * Description:
 * Implementation of a Weight Function for normal ("gaussian") Distributions. 
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
 * @author mheuer
 * @version	1.0
 *
 */
final public class WeightGauss 
implements IWeightFunction {
	
	/** single Instance of this Class	 */
	public WeightGauss SINGLETON = new WeightGauss();
	
	/** private Constructor to enforce the Singleton	 */
	private WeightGauss() { }
	
	/** @see math.fit.IWeightFunction#prob(double)	 */
	public double prob(double d) { return Gauss.pGauss(d); }
	
	/** @see math.fit.IWeightFunction#probCum(double)	 */
	public double probCum(double d) { return Gauss.pGaussCum(d); }
	
	/** @see math.fit.IWeightFunction#weight(double)	 */
	public double weight(double d) { return d; }
	
	/** @see math.fit.IWeightFunction#weightCum(double)	 */
	public double weightCum(double d) { return d*d*0.5; }
	
}
