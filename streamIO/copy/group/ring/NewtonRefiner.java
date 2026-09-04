package streamIO.copy.group.ring;

import function.IFunction;
import function.derive.IDeriveAble;

/**
 * Title: NewtonFloatRefiner<p>
 * Description:
 * Nullstellensuche with Newton Formula
 * Doesn't work well for multiple Zeros,
 * except if the Multiplicity is known and given
 * (can also act as a Relaxation Parameter!)
 * Works only on R^n->R^n Value Functions.
 * Requires f to be differentiable and f' to be continuous. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see math.refiner.NewtonFloatRefiner
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class NewtonRefiner
extends ARefiner {
	
	/**Initializes the Newton Stepper	 */
	public void Init(final IIntRing x, final IFunction f0, final IFunction f1) {
		init (x, f0);
		this.f1= f1;
	}

	/**Empty Constructor	 */
	public NewtonRefiner()	{}

	/**Initializing constructor	 */
	public NewtonRefiner(final IIntRing x, final IFunction f0, final IFunction f1) { Init(x, f0, f1); }

	/**The 1st Derivative of the Function for which the Zero is to be determined	 */
	protected IFunction f1;

	/** Performs a single approximating Step
	 * dy = dx°f' <=> dx ? dy°f'^-1 cat instead of mul! not commutative!
	 */
	public IIntRing refine() {
		yl = (IIntRing)       f .Map(xl) ;
		dx = (IIntRing)yl.div(f1.Map(xl));	//{x-Abstand und y-Abstand werden kontrolliert}
		if (null !=  multiplicity) { 
			dx.mulAt(multiplicity); } 
		return (IIntRing) xl.subAt(dx); }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() { 	//RingFuncs only used for testing!
		final IDeriveAble derivative = ((IDeriveAble)TEST_FUNCTION).getDerivative();
		L.n("Testing ").l(NewtonRefiner.class);
		L.n("Searching for the Solution of y = 0 = ").l(TEST_FUNCTION).l(" with y' = ").l(derivative);
		final NewtonRefiner refiner = new NewtonRefiner(TEST_FIX_POINT, TEST_FUNCTION, derivative); //
		TEST_REFINER(refiner, TEST_ZERO_POINT, 4); //extremely well-behaved!
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
