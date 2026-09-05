package streamIO.copy.group.ring;

import streamIO.copy.groupM.IGroupM;

/**Integrity Ring integrates full multiplicative and additive Group Capabilities.
 * I.e. not only 0, but also 1 and thus also -1 are defined.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 52ea65e70b8a0275ab844f2aa86ec0e9a2f0cf06490cc277dd4e9ce4c3d4b0fd
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * It also defines the Complement for the Processing of gAdic Numbers.  */
public interface IIntRing
extends IRing, IGroupM, integer, IComplex {

	/**Integer Part of this Number
	 * asymmetric to 0 (odd): i.e. Int(-x) = -Int(x) 
	 * @see streamIO.copy.group.ring.metric.body.IBody#floor which is not symmetric
	 */
	IIntRing IntAt();

	/**Integer Part of this Number in Place
	 * asymmetric to 0: i.e. Int(-x) = -Int(x)
	 * Truncates 'this' to simulate integer division for float operations.
	 * Cannot call this Method 'int', because this is a reserved Word in Java!
	 * Must be overloaded for float operations.
	 * This is NOT the same Operation as FloorAt() defined in intBody!
	 * Returns the integer part in place!	 */
	IIntRing Int();

	/**Fractional Part of this Number
	 * asymmetric to 0: i.e. Frac(-x) = -Frac(x)   */
	IIntRing FracAt();

	/**Fractional Part of this Number
	 * asymmetric to 0: i.e. Frac(-x) = -Frac(x)
	 * Related to Rem() in MetricIRing
	 * Truncates 'this' to simulate integer division for float operations.
	 * Must be overloaded for float operations.
	 * This is NOT the same Operation as Rem() defined in intBody!
	 * Returns the integer part in place!	 */
	IIntRing Frac();

	/**Returns the Fractional Part of this Number in Place,
	 * and the integer Part in Object.
	 * asymmetric to 0: i.e. Frac(-x) = -Frac(x)
	 * ModAtDivAt(arg, Div)  =  divAt(arg).FracAtIntAt(Div).mulAt(arg)	 */
	IIntRing FracAtIntAt(IIntRing Intgr);

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.
	 * ModDivAt(arg, Div)  =  divAt(arg).FracIntAt(Div).mulAt(arg)	 */
	IIntRing FracIntAt(IIntRing Intgr);

	/**Modulo in Place: %=
	 * Returns the Integer Remainder when dividing self by arg	 */
	IIntRing ModlAt(Object arg);

	/**Modulo: %
	 * Returns the Integer Division by arg	 */
	IIntRing DivAt	(Object arg);

	/**Modulo: %
	 * Returns the IIntRing Remainder when dividing self by arg	 */
	IIntRing Modl(Object arg);

	/**Modulo: %
	 * Returns the Integer Division by arg	 */
	IIntRing Div (Object arg);

	/**Division and Modulo Operation in Place:
	 * Could also be implemented by iterated subtraction,
	 * when multiplicaton is defined by iterative Addition.
	 * Using this to calculate both is faster than calculating both separately.	
	 * This Function is asymmetric to 0 in both Subject and Object, i.e.
	 * (-a).Mod(b) = -(a.Mod(b)) 
	 * (-a).Div(b) = -(a.Div(b)) 
	 * a.Mod(-b) = -(a.Mod(b)) 
	 * a.Div(-b) = -(a.Div(b)) 
	 */
	IIntRing ModAtDivAt(Object divisor, IIntRing quotient);

	/**Division and Modulo Operation:
	 * Could also be implemented by iterated subtraction,
	 * when multiplicaton is defined by iterative Addition.
	 * Using this to calculate both is faster than calculating both separately.	
	 * 
	 * @param divisor Number to divide by
	 * @param quotient the Quotient of this/divisor
	 * @return the Remainder (Module) of the Division
	 */
	IIntRing ModDivAt(IIntRing divisor, IIntRing quotient);

	//////////////////////////
	//	IIntRing Ops
	//////////////////////////

	/** Carry the Overflow through the g-adic Representation.	 */
	void addCarry();

	//Complement, necessary for gAdic
	/**Complement in Place: ~=	*/	IIntRing CmplAt();
	/**Complement: ~			*/	IIntRing Cmpl();

	/**Returns the Value raised by one g-Adic Position in Place	 */
	IIntRing toUpperAt();

	/**Returns the Value raised by one g-Adic Position	 */
	IIntRing toUpper();

	/**Returns true, when this is divisible by 2 (even Number).	 */
	boolean isEven();

	/**Returns true, when this is not divisible by 2 (odd Number).	 */
	boolean isOdd();

	/**Returns true, when this is -1.	 */
	boolean is_One();

	/**Calculates the greatest common Divisor of two numbers.	 */
	IIntRing ggT(Object K2);

	/**Calculates the smallest common Multiple of two numbers.	 */
	IIntRing kgV(Object K2);

	/**Calculates the greatest common Divisor of two numbers,
	 * as well as the two factors x and y so that ffT(this, K2) = this*x + K2*y	 */
	IIntRing XggT(Object K2, IIntRing x, IIntRing y);

	//////////////////
	//	Constants	//
	//////////////////

	/**Returns-1 in Place:	*/	IIntRing _oneAt();
	/**Returns 2 in Place:	*/	IIntRing twoAt();
	/**Returns 3 in Place:	*/	IIntRing threeAt();
	/**Returns 4 in Place:	*/	IIntRing fourAt();
	/**Returns-1:			*/	IIntRing _one();
	/**Returns 2:			*/	IIntRing two();
	/**Returns 3:			*/	IIntRing three();
	/**Returns 4:			*/	IIntRing four();

	/**Returns 1/2: 0.5	 */				IIntRing OneHalf ();
	/**Returns 1/2 in Place: 0.5	 */	IIntRing OneHalfAt ();

	/**Returns 1/3: 0.333333...	 */			IIntRing OneThird();
	/**Returns 1/3 in Place: 0.333333..	 */	IIntRing OneThirdAt();

	/**Returns 1/4: 0.25	 */			IIntRing OneQuarter ();
	/**Returns 1/4 in Place: 0.25	 */	IIntRing OneQuarterAt ();

	/**Returns x/2 in Place:*/	IIntRing halfAt();
	/**Returns x/3 in Place:*/	IIntRing thirdAt();
	/**Returns x/4 in Place:*/	IIntRing quarterAt();
	/**Returns x/2:			*/	IIntRing half();
	/**Returns x/3:			*/	IIntRing third();
	/**Returns x/4:			*/	IIntRing quarter();

	//////////////////////////////
	//	Combinatoric Methods:	//
	//////////////////////////////

	// only possible when additionally to counting the notion of a Sign
	// (Positiveness) is defined!

	/**Recursive Calculation of Factorial kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1	 */
//	public IIntRing fact();

	/**Recursive Calculation of Double Factorial kills the Stack.
	 * Iterative Solution, gives 0!! = 0, 1!! = 1	 */
//	public IIntRing dblFact();

	/**Calculates the Factorial of this integer number.
	 * The Definition is recursive:
	 * n!! = Fact(n) = n * Fact(n-1); Fact(0) = 1;	 */
	IIntRing Fact();

	/**Calculates the Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n!! = Fact(n) = n * Fact(n-1); Fact(0) = 1;	 */
	IIntRing FactAt();

	/**Calculates the Double Factorial of this integer number.
	 * The Definition is recursive:
	 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
	IIntRing dblFact();

	/**Calculates the Double Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
	IIntRing dblFactAt();

	/**Calculates the Variation(n,k) = n!/(n-k)!
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * with considering Sequence.	 */
	IIntRing Variation(IIntRing k);

	/**Recursive Calculation of the Combination kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1 for the Factorial
	 * and calculates the Variation and the Factorial of k.
	 * The Combination can be calculated by dividing the Variation
	 * by the Factorial of k.	 */
	IIntRing VariCombi(IIntRing k, IIntRing fact);

	/**Calculates the Combination(n,k) = n!/(k!*(n-k)!)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * without considering the Sequence.
	 *
	 * This Calculation is optimized, because Comb(n, k) == Comb (n, n-k).	 */
	IIntRing Combination(IIntRing k);

}
