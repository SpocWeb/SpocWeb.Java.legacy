/*
 * File Name: FuzzyNOT.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;


/**
 * Title: FuzzyNOT<p>
 * Description:
 * Fuzzy Complement / unary NOR Operation 
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
public class FuzzyNOT 
extends AFuzzyUnaryOp
{

	/** Initializing Constructor	 */
	public FuzzyNOT(final IFuzzifier f1_) {
		super(f1_);
	}

	/** @see streamIO.copy.boole.fuzzy.IFuzzifier#getMembership(java.lang.Object)	 */
	public float getMembership(final Object arg) {
		return 1-f1.getMembership(arg);
	}

}
