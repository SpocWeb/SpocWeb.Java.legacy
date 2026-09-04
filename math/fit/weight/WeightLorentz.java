/*
 * File Name: WeightLorentz.java
 * Created on: 22.02.2004
 *
 */
package math.fit.weight;

/**
 * Title: WeightLorentz<p>
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
public class WeightLorentz 
implements IWeightFunction {
	
	/** single Instance of this Class	 */
	public WeightLorentz SINGLETON = new WeightLorentz();
	
	/** private Constructor to enforce the Singleton	 */
	private WeightLorentz() { }
	
	/** @see math.fit.IWeightFunction#prob(double)	 */
	public double prob(double d) { return 1/(1+d*d*0.5); }
	
	/** @see math.fit.IWeightFunction#probCum(double)	 */
	public double probCum(double d) { return Math.exp(-Math.abs(d)); } //TODO:
	
	/** @see math.fit.IWeightFunction#weight(double)	 */
	public double weight(double d) { return d/(1+d*d*0.5); }
	
	/** @see math.fit.IWeightFunction#weightCum(double)	 */
	public double weightCum(double d) { return Math.log(1+d*d*0.5); }
	
}
