/*
 * File Name: AFuzzyUnaryOp.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;

import streamIO.object.IStreamIn;
import function.IFloatFunction;

/**
 * Title: AFuzzyUnaryOp<p>
 * Description:
 * Abstract Base Class for unary Fuzzy Functions. 
 *
 * Known SubClasses: 
 * @see streamIO.copy.boole.fuzzy.AFuzzyBinaryOp
 * @see streamIO.copy.boole.fuzzy.FuzzyNOT
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
public abstract class AFuzzyUnaryOp 
implements IFuzzifier, IFloatFunction 
{
    
	/** Reference to the first Fuzzifier 	*/
	protected final IFuzzifier f1;
	
	/** Initializing Constructor 	 */
	public AFuzzyUnaryOp(final IFuzzifier f1_) { this.f1 = f1_; }
	
	/** @see function.IFloatFunction#Map(double)	 */
	public double Map(final double arg) { return getMembership(new Double(arg)); }
	
	/** @see function.IFloatFunction#Map(float)	 */
	public float Map(final float arg) { return getMembership(new Float(arg)); }
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }    
    
}
