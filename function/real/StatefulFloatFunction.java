/**
 * File  Name: StatefulFloatFunction.java
 * Created on: 17.04.2003
 */
package function.real;

import streamIO.object.IStreamIn;
import function.IFloatFunction;
import function.derive.CMeasurAble;

/**
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
