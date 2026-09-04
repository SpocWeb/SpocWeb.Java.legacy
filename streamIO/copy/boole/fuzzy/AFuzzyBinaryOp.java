/*
 * File Name: AFuzzyBinaryOp.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;


/**
 * Title: AFuzzyBinaryOp<p>
 * Description:
 * Abstract Base Class for a binary Fuzzy Operation like AND, OR, IMPL etc. 
 *
 * Known SubClasses: 
 * @see streamIO.copy.boole.fuzzy.FuzzyAND
 * @see streamIO.copy.boole.fuzzy.FuzzyOR
 * @see streamIO.copy.boole.fuzzy.FuzzyIMPL
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
public abstract class AFuzzyBinaryOp 
extends AFuzzyUnaryOp
{

	/** Reference to the second Fuzzifier 	*/
	protected final IFuzzifier f2;

	/** Initializing Constructor	 */
	public AFuzzyBinaryOp(final IFuzzifier f1_, final IFuzzifier f2_) {
		super(f1_);
		this.f2 = f2_; 
	}

}
