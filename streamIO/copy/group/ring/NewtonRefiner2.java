package streamIO.copy.group.ring;

import function.IFunction;

/**
 * Nullstellensuche with Newton Formula and 2nd Derivative
 * Works well, also for multiple Zeros,
 * Works only on R->R Value Functions.
 * Requires f and f' to be differentiable and f'' to be continuous.
 * Returns the 'Multiplicity' of the Zero! 
 */
public class NewtonRefiner2
extends ARefiner {
	
	public NewtonRefiner2(final IIntRing x, final IFunction f0, final IFunction f1, final IFunction f2) {
		super (x, f0);
		this.f1= f1;
		this.f2= f2;
	}

	/**The 1st Derivative of the Function for which the Zero is to be determined	 */
	private IIntRing y1l;

	/**The 1st Derivative of the Function for which the Zero is to be determined	 */
	private final IFunction f1;

	/**The 2nd Derivative of the Function for which the Zero is to be determined	 */
	private final IFunction f2;

	/**Performs a single approximating Step	 */
	public IIntRing refine() {
		yl	=  (IIntRing) f .Map(xl);
		y1l	=  (IIntRing) f1.Map(xl);
		multiplicity = (IIntRing) y1l.sqr();
		multiplicity.divAt(multiplicity.sub(yl.mul(f2.Map(xl))));
		dx	=  (IIntRing) multiplicity.div(y1l).mulAt(yl);  //{x-Abstand und y-Abstand werden kontrolliert}
		return (IIntRing) xl.subAt(dx);
	}


	/**Method to test all Implementations in this class.	 */
	public static void testIt() {	//RingFuncs only used for testing!
		L.n("Testing ").l(NewtonRefiner2.class);
		L.n("Searching for the Root of y = ").l(TEST_FUNCTION);
		final IIntRing test = (IIntRing) streamIO.copy.ACopyAble.testInstance.copy();
		test.copyAt(new Double(3));
		L.n("Startpoint:" + test);
		//Code commented out to reduce Dependency between Metric and RingFuncs
/*		NewtonRefiner2 NS = new NewtonRefiner2(test,	RingFuncs.Square  .Square, //Search the Zero for
												RingFuncs.DoubleAt.DoubleAt,
												RingFuncs.Two	  .Two);//y = x^2, y' = 2x;
		int i = 0;
		while (++i < 10) {NS.refine(); System.out.println("x=" + NS.xl + " Multiplicity: " + NS.Multiplicity);}
*/	}
}
