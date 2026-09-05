package streamIO.copy.group.ring;

import streamIO.copy.groupM.IGroupM;
import streamIO.exception.ReadOnlyException;

/**Implements Constants for all Types of IIntRing Classes.
 * This Class inhibits the Use of ...At() Routines
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 5e2dfd69e00b8e3aa1ca8040d67cbcd6a6e217d63f5e1902c10e6ed3a730d4cb
 * stale: false
 * tags: [code/ring_theory, code/ode_solver]
 * concepts: [Ring Algebra and ODE Solvers]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * but still supports all other Methods of the IIntRing Class.	 */
public class CIntRing
extends  CRing
implements IIntRing {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor
	 * Caches the Result of the Test isZero()	 */
	public CIntRing(IIntRing cnst) { super(cnst); }

	/**Setting to Zero: 0	     */	public integer ZeroAt()         { throw new ReadOnlyException(strConst); }
	/**Division in Place: /=	 */	public IGroupM  divAt(Object arg){ throw new ReadOnlyException(strConst); }
	/**Inversion in Place: 1/x	 */	public IGroupM  invAt()			{ throw new ReadOnlyException(strConst); }
	/**Setting to  1 in Place:	 */	public IGroupM  oneAt()			{ throw new ReadOnlyException(strConst); }
	/**Setting to -1 in Place:	 */	public IIntRing _oneAt()	{ throw new ReadOnlyException(strConst); }
	/**Fractional Part: 	     */	public IIntRing FracAt()   { throw new ReadOnlyException(strConst); }
	/**Modulus: 	     */	public IIntRing ModlAt(Object arg) { throw new ReadOnlyException(strConst); }
	/**Integer Quotient: */	public IIntRing  DivAt(Object arg) { throw new ReadOnlyException(strConst); }
	/**Fractional and Integer Part: */
	public IIntRing FracAtIntAt(IIntRing Intgr) {
		throw new ReadOnlyException(strConst); }

	/**Modulus and Quotient: */
	public IIntRing ModAtDivAt(Object arg, IIntRing Intgr) {
		throw new ReadOnlyException(strConst); }

	/**Setting to Zero: 0	     */	public integer Zero()           { return (integer) zero(); }
	/**Division: /	 */				public IGroupM  div(Object arg)	{ return ((IGroupM) inner). div(arg); }
	/**Inversion:  1/x	 */			public IGroupM  inv()			{ return ((IGroupM) inner). inv(); }
	/**Setting to  1:	 */			public IGroupM  one()			{ return ((IGroupM) inner). one(); }
	/**Setting to -1:	 */			public IIntRing _one()		{ return ((IIntRing) inner)._one();}
	/**Testing for 1:	 */			public boolean isOne()			{ return bolIsOne; }
	/**Testing for 1:	 */			public boolean is_One()			{ return bolIs_One; }
	/**Fractional Part: 	     */	public IIntRing Frac()     { return ((IIntRing) inner). Frac(); }
	/**Modulus: 	     */	  public IIntRing Modl(Object arg) { return ((IIntRing) inner). Modl(arg); }
	/**Integer Quotient: */	  public IIntRing  Div(Object arg) { return ((IIntRing) inner). Div (arg); }

	/**Fractional and Integer Part: */
	public IIntRing FracIntAt(IIntRing Intgr){
		return ((IIntRing) inner).FracIntAt(Intgr); }

	/**Modulus and Quotient: */
	public IIntRing ModDivAt(IIntRing arg, IIntRing Intgr) {
		return ((IIntRing) inner).ModDivAt(arg, Intgr); }

	/**Local Cache for the Result of this Test	 */
	private final boolean bolIsOne = ((IIntRing) inner).isOne();

	/**Local Cache for the Result of this Test	 */
	private final boolean bolIs_One = ((IIntRing) inner).is_One();

	/**Conversion to Integer:	 */
	public IIntRing IntAt()	{throw new ReadOnlyException(strConst);}

	/**Conversion to Integer:	 */
	public IIntRing Int()	{return ((IIntRing) inner).Int();}

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry()			{throw new ReadOnlyException(strConst);}

	//Complement, necessary for gAdic
	/**Complement in Place: ~=	*/	public IIntRing CmplAt()	{throw new ReadOnlyException(strConst);}
	/**Complement: ~			*/	public IIntRing Cmpl()	{return ((IIntRing) inner).Int();}

	/**Returns the Value raised by one g-Adic Position in Place	 */
	public IIntRing toUpperAt(){throw new ReadOnlyException(strConst);}

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpper()	{return ((IIntRing) inner).toUpper();}

	/**Division and Modulo Operation:	 */
	public IIntRing ModDivAt(Object arg, Object Divsr)	{ throw new ReadOnlyException(strConst);}

	/**Division and Modulo Operation in Place:	 */
	public IIntRing ModAtDivAt(Object arg, Object Divsr)	{ throw new ReadOnlyException(strConst);}

	/**Local Cache for the Result of this Test
	 * This relies on the Compiler processing this Initialization
	 * after the Constructor.	 */
	protected boolean even = ((IIntRing) inner).isEven();

	/**Local Cache for the Result of this Test
	 * This relies on the Compiler processing this Initialization
	 * after the Constructor.	 */
	protected boolean odd  = ((IIntRing) inner).isOdd ();

	/**Returns true, when this is divisible by 2 (even Number).	 */
	public boolean isEven(){return even;}

	/**Returns true, when this is not divisible by 2 (odd Number).	 */
	public boolean isOdd (){return odd;}

	/**Calculates the greatest common Divisor of two numbers.	 */
	public IIntRing ggT(Object K2)	{return ((IIntRing) inner).ggT(K2);}

	/**Calculates the smallest common Multiple of two numbers.	 */
	public IIntRing kgV(Object K2)	{return ((IIntRing) inner).kgV(K2);}

	/**Calculates the greatest common Divisor of two numbers,
	 * as well as the two factors x and y so that ffT(this, K2) = this*x + K2*y	 */
	public IIntRing XggT(Object K2, IIntRing x, IIntRing y)
	{return ((IIntRing) inner).XggT(K2, x, y);}

	//////////////////
	//	Constants	//
	//////////////////

	/**Returns 2 in Place:	*/	public IIntRing twoAt  ()  { throw new ReadOnlyException(strConst); }
	/**Returns 3 in Place:	*/	public IIntRing threeAt()  { throw new ReadOnlyException(strConst); }
	/**Returns 4 in Place:	*/	public IIntRing fourAt ()  { throw new ReadOnlyException(strConst); }
	/**Returns 2:			*/	public IIntRing two    ()  { return ((IIntRing) inner).two  (); }
	/**Returns 3:			*/	public IIntRing three  ()  { return ((IIntRing) inner).three(); }
	/**Returns 4:			*/	public IIntRing four   ()  { return ((IIntRing) inner).four (); }
	/**Returns x/2 in Place:*/	public IIntRing halfAt ()  { throw new ReadOnlyException(strConst); }
	/**Returns x/3 in Place:*/	public IIntRing thirdAt()  { throw new ReadOnlyException(strConst); }
	/**Returns x/4 in Place:*/	public IIntRing quarterAt(){ throw new ReadOnlyException(strConst); }
	/**Returns x/2:			*/	public IIntRing half   ()  { return ((IIntRing) inner).half (); }
	/**Returns x/3:			*/	public IIntRing third  ()  { return ((IIntRing) inner).third(); }
	/**Returns x/4:			*/	public IIntRing quarter()  { return ((IIntRing) inner).quarter(); }

	/**Returns 1/2 in Place:*/	public IIntRing OneHalfAt ()  { throw new ReadOnlyException(strConst); }
	/**Returns 1/3 in Place:*/	public IIntRing OneThirdAt()  { throw new ReadOnlyException(strConst); }
	/**Returns 1/4 in Place:*/	public IIntRing OneQuarterAt(){ throw new ReadOnlyException(strConst); }
	/**Returns 1/2:			*/	public IIntRing OneHalf   ()  { return ((IIntRing) inner).OneHalf (); }
	/**Returns 1/3:			*/	public IIntRing OneThird  ()  { return ((IIntRing) inner).OneThird(); }
	/**Returns 1/4:			*/	public IIntRing OneQuarter()  { return ((IIntRing) inner).OneQuarter(); }

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
	public IIntRing Fact()	{return ((IIntRing) inner).Fact();}

	/**Calculates the Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n!! = Fact(n) = n * Fact(n-1); Fact(0) = 1;	 */
	public IIntRing FactAt()	{throw new ReadOnlyException(strConst);}

	/**Calculates the Double Factorial of this integer number.
	 * The Definition is recursive:
	 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
	public IIntRing dblFact()	{return ((IIntRing) inner).dblFact();}

	/**Calculates the Double Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
	public IIntRing dblFactAt()	{throw new ReadOnlyException(strConst);}

	/**Calculates the Variation(n,k) = n!/(n-k)!
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * with considering Sequence.	 */
	public IIntRing Variation(IIntRing k)
	{return ((IIntRing) inner).Variation(k);}

	/**Recursive Calculation of the Combination kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1 for the Factorial
	 * and calculates the Variation and the Factorial of k.
	 * The Combination can be calculated by dividing the Variation
	 * by the Factorial of k.	 */
	public IIntRing VariCombi(IIntRing k, IIntRing fact)
	{return ((IIntRing) inner).VariCombi(k, fact);}

	/**Calculates the Combination(n,k) = n!/(k!*(n-k)!)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * without considering the Sequence.
	 *
	 * This Calculation is optimized, because Comb(n, k) == Comb (n, n-k).	 */
	public IIntRing Combination(IIntRing k)
	{return ((IIntRing) inner).Combination(k);}

	//Counting:

	/**Increment: x++	 */
	public integer inc()	{return ((IIntRing) inner).inc();}

	/**Decrement: x--	 */
	public integer dec()	{return ((IIntRing) inner).dec();}

	/**Residual in Place: 1-x	 */
	public integer ResidAt()	{throw new ReadOnlyException(strConst);}

	//Delegations:

	/**Successor: x+1	 */
	public integer succ()	{return ((IIntRing) inner).succ();}

	/**Predecessor: x-1	 */
	public integer pred()	{return ((IIntRing) inner).pred();}

	/**Residual: 1-x	 */
	public integer Resid()	{return ((IIntRing) inner).Resid();}

	/**Returns the conjugate Complex Number:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjg()	{return ((IIntRing) inner).cjg(); }

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt()	{throw new ReadOnlyException(strConst); }

	/**Local Cache for the Result of this Test
	 * This relies on the Compiler processing this Initialization
	 * after the Constructor.	 */
	protected boolean complex = ((IIntRing) inner).isComplex();

	/**Testing Method, should be static or directly tested on the Types.
	 * Normally there are only these two Representations: Complex and Polar.
	 * This Implementation is quite elegant, because only cjg has to be defined,
	 * but it creates a great overhead.	 */
	public boolean isComplex(){return complex;}

	/**Addition of the conjugate complex argument: +=	 */
	public IIntRing addCjg(Object arg)	{return ((IIntRing) inner).addCjg(arg);}

	/**Subtraction of the conjugate complex argument: -=	 */
	public IIntRing subtCjg(Object arg)	{return ((IIntRing) inner).subtCjg(arg);}

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	public IIntRing mulCjg(Object arg)	{return ((IIntRing) inner).mulCjg(arg);}

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen �berlauf durch die Quadrierung
	 * und spart au�erdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IIntRing divCjg(Object arg)	{return ((IIntRing) inner).divCjg(arg);}

	/**Multiplies the Complex Number by i or divides it by -i:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90�	 */
	public IIntRing MulI()	{return ((IIntRing) inner).MulI();}

	/**Divides the Complex Number by i or multiplies it by -i:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90�	 */
	public IIntRing DivI()	{return ((IIntRing) inner).DivI();}

	/**Addition of the conjugate complex argument in Place: +=	 */
	public IIntRing addAtCjg(Object arg) {throw new ReadOnlyException(strConst); }

	/**Subtraction of the conjugate complex argument in Place: -=	 */
	public IIntRing subAtCjg(Object arg) {throw new ReadOnlyException(strConst); }

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	public IIntRing mulAtCjg(Object arg) {throw new ReadOnlyException(strConst); }

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen �berlauf durch die Quadrierung
	 * und spart au�erdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IIntRing divAtCjg(Object arg) {throw new ReadOnlyException(strConst); }

	/**Multiplies the Complex Number by i or divides it by -i in Place:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90�	 */
	public IIntRing mulIAt() {throw new ReadOnlyException(strConst); }

	/**Divides the Complex Number by i or multiplies it by -i in Place:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90�	 */
	public IIntRing divIAt() {throw new ReadOnlyException(strConst); }

}
