/*
 * File Name: FuzzyIMPL.java
 * Created on: 30.12.2003
 *
 */
package streamIO.copy.boole.fuzzy;

/**
 * Title: FuzzyIMPL<p>
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
public class FuzzyIMPL 
extends AFuzzyBinaryOp {

	/** Initializing Constructor
	 * @param f1_
	 * @param f2_
	 */
	public FuzzyIMPL(final IFuzzifier f1_, final IFuzzifier f2_) {
		super(f1_, f2_);
	}

	/** A IMPL B = NOT (B AND (NOT A))
	 * @see streamIO.copy.boole.fuzzy.IFuzzifier#getMembership(java.lang.Object)
	 */
	public float getMembership(final Object arg) {
		return 1-(Math.min(f1.getMembership(arg), 1-f2.getMembership(arg)));
	}

}
