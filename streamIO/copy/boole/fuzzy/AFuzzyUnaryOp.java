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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:44:07Z
 * digest: 68906883966a52105cae21ce15cf91aa52524b0f80c6f535b0648cd8d206f0e4
 * stale: false
 * tags: [code/fuzzy_logic, code/abstract_base]
 * concepts: [Fuzzy Logic]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public abstract class AFuzzyUnaryOp 
implements IFuzzifier, IFloatFunction 
{
    
	/** Reference to the first Fuzzifier 	*/
	protected final IFuzzifier f1;
	
	/** Initializing Constructor 	 */
	public AFuzzyUnaryOp(final IFuzzifier f1_) { this.f1 = f1_; }
	
	/**Returns the fuzzy membership of the boxed argument, delegating to {@link #getMembership(Object)}.
	 * @see function.IFloatFunction#Map(double)	 */
	public double Map(final double arg) { return getMembership(new Double(arg)); }

	/**Returns the fuzzy membership of the boxed argument, delegating to {@link #getMembership(Object)}.
	 * @see function.IFloatFunction#Map(float)	 */
	public float Map(final float arg) { return getMembership(new Float(arg)); }

    /**Returns {@link IStreamIn#ORDER_NONE}, since a fuzzy function imposes no ordering.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }
    
}
