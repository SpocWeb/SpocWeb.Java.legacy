/*
 * File Name: WeightExp.java
 * Created on: 22.02.2004
 *
 */
package math.fit.weight;

import function.byref.ByRefDouble;

/**
 * Title: WeightExp<p>
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
final public class WeightExp 
implements IWeightFunction {
	
	/** single Instance of this Class	 */
	public WeightExp SINGLETON = new WeightExp();
	
	/** private Constructor to enforce the Singleton	 */
	private WeightExp() { }
	
	/** @see math.fit.IWeightFunction#prob(double)	 */
	public double prob(double d) { return Math.exp(-Math.abs(d)); }
	
	/** @see math.fit.IWeightFunction#probCum(double)	 */
	public double probCum(double d) { return Math.exp(-Math.abs(d)); } //TODO:
	
	/** @see math.fit.IWeightFunction#weight(double)	 */
	public double weight(double d) { return ByRefDouble.SIGN(d); }
	
	/** @see math.fit.IWeightFunction#weightCum(double)	 */
	public double weightCum(double d) { return Math.abs(d); }
	
}
