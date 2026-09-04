/*
 * File Name: NewtonFloatRefiner.java
 * Created on: 31.01.2004
 *
 */
package math.refiner;

import function.IFloatFunction;
import function.byref.ByRefDouble;
import function.derive.IFloatDeriveAble;

/**
 * Title: NewtonFloatRefiner<p>
 * Description:
 * Root Finding with Newton Formula using the 1st Derivative. 
 * 
 * Doesn't work well for multiple Zeros,
 * except if the Multiplicity is known and given
 * (Multiplicity can also act as a Relaxation Parameter!)
 * Works on R^n->R^n Value Functions with any n.
 * Requires f to be differentiable and f' to be continuous. 
 * 
 * Design Decision: 
 * can also exploit the Optimization of a IFloatDeriveAble, 
 * to calculate both Function and Derivative at the same Time, 
 * because both share the same Characteristics. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see streamIO.copy.group.ring.NewtonRefiner
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class NewtonFloatRefiner 
extends AFloatRefiner {
	
	/**Initializes the Newton Stepper	 */
	public void init(final double x, final IFloatFunction f0, final IFloatFunction f1) {
		init (x, f0.Map(x));
		this.f0= f0; 
		this.f1= f1;
	}

	/**Initializes the Newton Stepper	 */
	public void init(final double x, final IFloatDeriveAble f01_) {
		init (x, (IFloatFunction) f01_);
		this.f01 = f01_;
	}

	/**Empty Constructor	 */
	public NewtonFloatRefiner()	{}

	/**Initializing constructor	 */
	public NewtonFloatRefiner(final double x, final IFloatDeriveAble f01_) { 
		init(x, f01_); }

	/**Initializing constructor	 */
	public NewtonFloatRefiner(final double x, final IFloatFunction f0, final IFloatFunction f1) { 
		init(x, f0, f1); }
	
	/** The 1st Derivative of the Function for which the Zero is to be determined	 */
	protected IFloatFunction f0;
	
	/** The 1st Derivative of the Function for which the Zero is to be determined	 */
	protected IFloatFunction f1;

	/** A Function that calculates both Function and Derivative faster than individually	 */
	protected IFloatDeriveAble f01;

	/** A Function that calculates both Function and Derivative faster than individually	 */
	protected ByRefDouble dydx = new ByRefDouble();

	/**Performs a single approximating Step	
	 * @return xl, the new Estimate for the Root
	 */
	public double refine() {
		if (f01 != null) {
			yl = f01.getFuncDerive(xl, dydx);
			dx = multiplicity*yl / dydx.Value;	//{x-Abstand und y-Abstand werden kontrolliert}
		} else {
			yl = f .Map(xl) ;
			dx = multiplicity*yl / f1.Map(xl);	//{x-Abstand und y-Abstand werden kontrolliert}
		}
		return xl -= dx; }

	/**Method to test all Implementations in this class.	 */
	public static void testIt() { 	//RingFuncs only used for testing!
		final IFloatDeriveAble func = (IFloatDeriveAble) TEST_FUNCTION;
		//final IFloatFunction derivative = (IFloatFunction) ((IDeriveAble)TEST_FUNCTION).getDerivative();
		L.n("Testing ").l(NewtonFloatRefiner.class);
		L.n("Searching for the Solution of y = 0 = ").l(TEST_FUNCTION);
		final NewtonFloatRefiner stepper = new NewtonFloatRefiner(TEST_FIX_POINT, func); //
		TEST_REFINER(stepper, TEST_ZERO_POINT, 4); //extremely well-behaved!
	}

	/** Main Method to be called from the Command Line 	 */
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}

}
