package streamIO.copy.group.ring;

import function.IFunction;

/**
 * Title: FixPtRefiner<p>
 * Description:
 * Fixpoint Search according to Banach 
 * Works on R^n->R^n Value Functions.
 * Requires f to be differentiable and |f'| < 1. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see refiner.FixPtFloatImprover
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 */
public class FixPtRefiner
extends ARefiner {

	/**Empty Constructor	 */
	public FixPtRefiner(){}

	/**Initializing Constructor	 */
	public FixPtRefiner(IIntRing x, IFunction f0) { super (x, f0); }

	/**Performs a single approximating Step: x = f(x)	 */
	public IIntRing refine() {
		dx = (yl = (IIntRing) f.Map(xl));	//{x-Abstand und y-Abstand werden kontrolliert}
		return (IIntRing) xl.copyAt(dx); }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() { 	//RingFuncs only used for testing!
		L.n("Testing ").l(FixPtRefiner.class);
		L.n("Searching for the Solution of x = ").l(TEST_FUNCTION);
		final IIntRing xLeft = (IIntRing) TEST_FIX_POINT.newInstance();
		xLeft.copyAt(new Double(3));
		TEST_REFINER(new FixPtRefiner(xLeft, TEST_FUNCTION), TEST_FIX_POINT, 45);
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
