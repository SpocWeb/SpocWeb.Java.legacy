/**
 * File  Name: BiLinearSum.java
 * Created on: 17.04.2003
 */
package function.real;

import streamIO.Assert;
import streamIO.real.random.RandomLorentz;

/**
 * Title: enclosing_type<p>
 * Description:
 * Purpose:
 * This stateful Function combines the current Value 
 * in a weighted Manner with new incoming Values. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: 
 * @see RandomLorentz
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:43:36Z
 * digest: 93e66d9670d4db071264cc9d5eebdb86c9f4082011c4a605b178c823ee4c4a70
 * stale: false
 * tags: [code/running_aggregates, code/mathematical_function]
 * concepts: [Streaming Numeric Aggregator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class BiLinearSum 
extends StatefulFloatFunction {

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////

	/** 
	 * Factor to multiply the new Values with, between 0 and 1. 
	 * Attenuation for the old Values 
	 */
	protected double factorNew;

	/** 
	 * Factor to multiply the old Values with, between 0 and 1. 
	 * Scaling of the new Values
	 */
	protected double factorOld;

	/**
	 * Constructor for BiLinearSum.
	 * @param seed   The Start Value of the Function 
	 * @param scale  The Amplitude of the Function (symmetric to 0)
	 * @param fGrenz The upper cut off 'Frequency' of the Function:
	 * when 0       , the Values stay constant on the seed and do not vary.  
	 * when Infinity, the Values are identical to the incoming Values
	 */
	public BiLinearSum(double fGrenz, double seed, double scale) {
		super(scale * seed);
		Assert.NOT_NEGATIVE  (fGrenz); 
		factorOld = Math.exp (-fGrenz);
		factorNew = Math.sqrt(1.0-factorOld*factorOld) * scale; /*Normierung auf Amplitude 1.0 */
	}
	
	/** Widens {@code value_} to {@code double} and delegates to {@link #Map(double)}.
	 * @return the next Random single Precision Number	 */
	public float Map(final float value_){ return (float) Map((double) value_); }

	/** Blends the current Value with {@code value_} using {@link #factorOld}/{@link #factorNew}.
	 * @return the next Random double Precision Number	 */
	public double Map(final double value_){
		return _Value  =
			   _Value *factorOld +
			   value_*factorNew; }

}
