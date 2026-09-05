/**
 * File  Name: StatefulFloatFunction.java
 * Created on: 17.04.2003
 */
package function.real;

import streamIO.object.IStreamIn;
import function.IFloatFunction;
import function.derive.CMeasurAble;

/**
 * Base class for the stateful streaming filters in this package (sum, product, mean, min/max,
 * ...), each accumulating a running {@code double} value across successive {@link #Map} calls.
 *
 * Title: enclosing_type<p>
 * Description:
 * Purpose:
 * Base Class for several stateful FloatFunctions
 * (which not Functions in their original Sense!)
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: 
 * @see function.real.RunningMean
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
 * mtime: 2026-09-05T16:44:56Z
 * digest: 9e3ae7dd07b0a37ae58f7b8d7c960b001a59d2f64b7c409631529971916db7d4
 * stale: false
 * tags: [code/running_aggregates, code/mathematical_function]
 * concepts: [Streaming Numeric Aggregator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public abstract class StatefulFloatFunction 
extends CMeasurAble //
implements IFloatFunction
{
	
	/** the current internal Value of the Function */
//	protected double value; 

	/** the current Count of the Function Evaluations */
	protected int _Count; 

	/**
	 * Constructor for StatefulFloatFunction. 
	 */
	public StatefulFloatFunction(final double value_) {
		super(value_); 
	}

    /** typically not monotonous, since depending on the Past
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }    
    
}
