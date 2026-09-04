package streamIO.copy.group.ring;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.groupM.AGroupM;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;

/**Default Implementation of the Algebraic Integrity Ring (M,+,-,0,*,/,1):
 * Set of Objects with inner Operations +,-,*,/ , where
 * 1) (M,+,-,0,*) form an Algebraic Ring
 * 2) 1 exists            neutral Element for *
 * 3) a=0 v b=0 <=> a*b=0  i.e. free of Zero Divisors (exist in Mod Classes)
 *
 * Design Decisions:
 * No new operations are defined, but both Interfaces are integrated into one.
 * Instead of delegating to the ASemiGroup Class, I re-implement some Methods.
 *
 * Abstract Methods:
 * addAt()
 * subAt()
 * mulAt()
 * divAt()
 *
 * copyAt()
 * newInstance()
 *
 * Politics of the Carry, Signed and Modul:
 * Signed is only set in Containers like gAdic
 * The Carry is always given in the Constructor.
 * If it is given as null, it will at least be created on the copyAt();
 * All Array Elements are generated from the Carry Element.
 * The Carry Element is transferred to all newly generated Elements.
 * Elements can not be exchanged. Their copyAt() Method retains the Carry.
 * The Modulus has to be the same in this, Carry and the Elements.
 *
 * There is no copyAt() Routine for AIntRing,
 * because these local Variable are not intended to be copied.
 * That has to be taken care of by their Containers,
 * the Polynoms or gAdic Numbers.
 * Elements of these are not intended to be copied separately,
 * so after copying they are no longer part of the gAdic Number.
 * They lose the Reference to the Carry and their gAdic Flag,
 * because they would only confuse the original Polynom or gAdic.
 * It has to be done explicitly by calling
 *
 * Additionally the inherited classes needn't call super.copyAt().	 */

public abstract class AIntRing
extends ARing
implements IIntRing {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(1);

	/**Setting to Zero: 0	 */	public integer ZeroAt() {  zeroAt(); return this; }
	/**Setting to Zero: 0	 */	public integer Zero  () {  return ((integer) newInstance()).ZeroAt(); }

	/**Returns 1/2: 0.5	 */
	public IIntRing OneHalf () { return ((IIntRing) newInstance()).OneHalfAt(); }

	/**Returns 1/2 in Place: 0.5	 */
	public IIntRing OneHalfAt () { oneAt(); halfAt(); return this; }

	/**Returns 1/3: 0.333333333333...	 */
	public IIntRing OneThird() { return ((IIntRing) newInstance()).OneThirdAt(); }

	/**Returns 1/3 in Place: 0.333333333333...	 */
	public IIntRing OneThirdAt() { oneAt(); thirdAt(); return this; }

	/**Returns 1/4 in Place: 0.25	 */
	public IIntRing OneQuarterAt() { oneAt(); quarterAt(); return this; }

	/**Returns 1/4: 0.25	 */
	public IIntRing OneQuarter() { return ((IIntRing) newInstance()).OneQuarterAt(); }

	/**Copies the behavioral local Variables from arg.
	 * This should be used carefully, because normally
	 * only the Array Container should manage the Carry Elements.
	 * Ideally the Carry Element can be handed over to the addAt
	 * and Shift Operations, so they are defined only when they are used.	 */
	protected void copyModulAt(AIntRing arg) {
		if ((Modul = arg.Modul) > 0) {
			Carry	= arg.Carry;
			Signed	= arg.Signed;
		}
	}

	/**Copies the behavioral local Variables from arg.
	 * This should be used carefully, because normally
	 * only the Array Container should manage the Carry Elements.	 */
	public ICopyAble copyAt(Object arg, int Depth) {
		if (arg instanceof AIntRing) {
			copyModulAt ((AIntRing) arg);
			if ((Depth > 0) && (Carry != null)) Carry = (AIntRing) Carry.copy(Depth);
		}
		return this; }

	//None of the following Variables are copied in copyAt(),
	//because they are used only in gAdic Representation,
	//which takes care of setting these Values itself!

	/**Determines, if the Number can assume negative Values in g-adic Representation,
	 * or a Carry is generated.
	 * Only the highest Coefficient in g-adic Numbers must be signed!	 */
	public boolean Signed;// = false;

	/**The Modulus, used for Operations in g-adic Representation
	 * to keep the Arguments in the Range {0..Modul-1}.
	 * If Modul == 1, the Constants HalfNumBits are used
	 * If Modul <= 0, this is no gAdic Representation.	 */
	public long Modul;// = 0;

	/**Carry Value, used for the following Operations:
	 * Carry for Modulus Operations with +,-,*
	 * Modulus  for Division '/'
	 * Carry Bit for single Shifting and Rotating
	 *
	 * The Carry is always of the same Type as all the Elements,
	 * since the Elements are generated from the Carry.	 */
	public IIntRing Carry;

	//	Constructors:	Normally you should always define Carry etc.
	//					but that is necessary only e.g. for Arrays.

	/**Initializing Constructor	 */
//	public AIntRing() { }

	/**Initializing Constructor, Carry should normally be set!	 */
//	public AIntRing(IIntRing Carry) { this.Carry = Carry;}

	/**Initializing Constructor	 */
/*	public AIntRing(IIntRing Carry, boolean Signed, long Modulus) {
		this.Carry	= Carry;
		this.Signed	= Signed;
		this.Modulus= Modulus;
	}
*/
	//Interfaces:

	//Implementations:

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpper() {
		AIntRing Copy = (AIntRing)copy();
		Copy.Modul = Modul;
		return Copy.toUpperAt(); }

	//Modulo Operations:

	/**Conversion to Integer:
	 * Truncates 'this' to simulate integer division for float operations.
	 * This is NOT the same Operation as FloorAt() defined in MetricIRing!
	 * Must be overloaded for float operations. Usually Floor() is used.	 */
	public IIntRing IntAt() { subAt(Frac()); return this; }

	/**Conversion to Integer:
	 * Truncates 'this' to simulate integer division for float operations.
	 * Must be overloaded for float operations.
	 * This is NOT the same Operation as FloorAt() defined in MetricIRing!
	 * Returns the integer part in place!	 */
	public IIntRing Int() {  return ((IIntRing)copy()).IntAt(); }

	/**Fractional Part of this Number
	 * asymmetric to 0: i.e. Frac(-x) = -Frac(x)   */
	public IIntRing FracAt() { subAt(Int()); return this; }

	/**Fractional Part of this Number
	 * asymmetric to 0: i.e. Frac(-x) = -Frac(x)
	 * Related to Rem() in MetricIRing
	 * Truncates 'this' to simulate integer division for float operations.
	 * Must be overloaded for float operations.
	 * This is NOT the same Operation as Rem() defined in intBody!
	 * Returns the integer part in place!	 */
	public IIntRing Frac() { return ((IIntRing)copy()).FracAt(); }

	/**Returns the Fractional Part of this Number in Place,
	 * and the integer Part in Object.
	 * You could redefine ModAtDivAt(arg, Div)  =  divAt(arg).FracAtIntAt(Div).mulAt(arg)	 */
	public IIntRing FracAtIntAt(IIntRing Intgr) {
		Intgr.shallowCopyAt(this); Intgr.IntAt(); subAt(Intgr);
		return this; }

	/**Returns the Fractional Part of this Number,
	 * and the integer Part in Object.
	 * You could redefine ModDivAt(arg, Div)  =  divAt(arg).FracIntAt(Div).mulAt(arg)	 */
	public IIntRing FracIntAt(IIntRing Intgr) {
		return ((IIntRing)copy()).FracAtIntAt(Intgr); }

	/**Modulo in Place: %=
	 * Returns the Integer Remainder when dividing self by arg	 */
	public IIntRing ModlAt(Object arg) {
		divAt(arg); FracAt();
		mulAt(arg); return this; }
//		return ModAtDivAt(arg, (IIntRing) newInstance()); }

	/**Modulo: %
	 * Returns the Integer Remainder when dividing self by arg	 */
	public IIntRing Modl(Object arg) { return ((IIntRing) copy()).ModlAt(arg); }

	/**Integer Division: \
	 * Returns the Integer Division by arg	 */
	public IIntRing DivAt	(Object arg) { divAt(arg); return IntAt(); }

	/**Division and Modulo Operation:
	 * Could also be implemented by iterated subtraction,
	 * when multiplicaton is defined by iterative Addition.
	 * Using this to calculate both is faster than calculating both separately.	 */
	public IIntRing ModDivAt(IIntRing arg, IIntRing Divsr) {
		return ((IIntRing) copy()).ModAtDivAt(arg, Divsr); }

	/**Modulo: %
	 * Returns the Integer Division by arg	 */
	public IIntRing Div (Object arg) { return ((IIntRing) copy()).DivAt(arg); }

	/**Division and Modulo Operation:
	 * Could also be implemented by iterated subtraction,
	 * when multiplicaton is defined by iterative Addition.
	 * Using this to calculate both is faster than calculating both separately.	 */
	public IIntRing ModDivAt(Object arg, IIntRing Divsr) {
		return ((IIntRing) copy()).ModAtDivAt(arg, Divsr);}

	/**Division and Modulo Operation in Place:
	 * Could also be implemented by iterated subtraction,
	 * when multiplicaton is defined by iterative Addition.
	 * Using this to calculate both is faster than calculating both separately.	 */
	public IIntRing ModAtDivAt(Object arg, IIntRing Divsr) { //Div = INT (K1/K2) truncAt must return a type of it's own!
		divAt(arg); FracAtIntAt(Divsr); 	//K1-K2*INT (K1/K2)
		mulAt(arg); return this; }
//		return (IIntRing)subAt(((GroupM)arg).mul(((CopyAble)Div).
//		shallowCopyAt(((IIntRing)div(arg)).IntAt()))); }

	/**Returns true, when this is divisible by 2 (even Number).	 */
	public boolean isEven() { return Modl(two()).isZero(); }

	/**Returns true, when this is not divisible by 2 (odd Number).	 */
	public boolean isOdd() { return ! isEven(); }

	/**Calculates the greatest common Divisor of two numbers.
	 * Uses the iterative Archimedean Algorithm for this.	 */
/*	public IIntRing ggT(Object K2)
	{	//Arguments are automatically swapped with the same Algorithm (although with the overhead of dividing once)
	    IIntRing H1 = (IIntRing) copy();
	    IIntRing H2 = (IIntRing) ((CopyAble) K2).copy();
	    IIntRing H3;
	    while (!H2.isZero())    //Also the Special Case of ggt(1,0) is caught here!
		{	//iterative Implementation, also recursive Implementation possible
	        H3 = H1.Modl(H2);
	        H1 = H2;
	        H2 = H3;
		}
	    return H1; }
*/
	/**Calculates the greatest common Divisor of two numbers.
	 * Uses the recursive Archimedean Algorithm for this,
	 * because that is apted for integer as well as for float Values.
	 * For integer Values the binary ggT is much faster.	 */
	public IIntRing ggT(Object K2) {
		IIntRing H2 = (IIntRing) K2;
		if (H2.isZero())
			return this;
			return H2.ggT(this.Modl (K2)); }

	/**Calculates the greatest common Divisor of two numbers,
	 * as well as the two factors x and y so that ggT(this, K2) = this*x + K2*y
	 * Uses the Archimedean Algorithm for this.	 */
	public IIntRing XggT(Object K2, IIntRing x, IIntRing y) {
		IIntRing H2 = (IIntRing) K2;
		IIntRing H1 = (IIntRing) H2.newInstance();
		if (H2.isZero()) {x.oneAt(); y.zeroAt(); return this;}
		IIntRing d = H2.XggT(this.ModDivAt(K2, H1), y, x);
//		IIntRing d = H2. ggT(this.Modl    (K2));
		y.subAt(H1.mulAt(x));
		return d; }

	/**Calculates the smallest common Multiple of two numbers.
	 * kgV(a,b)=a*b/ggT(a,b)	 */
	public IIntRing kgV(Object K2) {
		return (IIntRing) ((IIntRing) mul(K2)).div(ggT(K2));}	//don't need to use IntAt here, because the ggT divides


	//////////////////
	//	Constants	//
	//////////////////

	/**Returns -1 in Place:	*/
	public IIntRing _oneAt() { return (IIntRing) ((IIntRing) oneAt()).negAt(); }

	/**Returns 2 in Place:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing twoAt()  { return (IIntRing) ((IIntRing) oneAt()).dblAt(); }

	/**Returns 3 in Place:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing threeAt() {  return (IIntRing) ((IIntRing) oneAt()).trplAt(); }

	/**Returns 4 in Place:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing fourAt() {  return (IIntRing) ((IIntRing) twoAt()).dblAt(); }

	/**Returns -1:	*/
	public IIntRing _one() { return ((IIntRing) newInstance())._oneAt(); }

	/**Returns 2:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing two()  { return ((IIntRing) newInstance()).twoAt(); }

	/**Returns 3:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing three() { return ((IIntRing) newInstance()).threeAt(); }

	/**Returns 4:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing four() { return ((IIntRing) newInstance()).fourAt(); }

	/**Returns x/2 in Place:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing halfAt() { return (IIntRing) divAt(two()); }

	/**Multiplication with an Integer Power of 2:
	 * Here implemented as a concatenated doubling.	 */
//	public SemiGroup mul2Pow  (int n) { return ((ASemiGroup) copy()).mul2PowAt(n);}

	/**Returns x/3 in Place:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing thirdAt() { return (IIntRing) divAt(three()); }

	/**Returns x/4 in Place:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing quarterAt() { return (IIntRing) halfAt().halfAt(); }

	/**Returns x/2:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing half() { return ((IIntRing) copy()).halfAt(); }

	/**Returns x/3:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing third() { return ((IIntRing) copy()).thirdAt(); }

	/**Returns x/4:
	 * Implemented because of the frequent use of this Operation.	 */
	public IIntRing quarter() { return ((IIntRing) copy()).quarterAt(); }

	//Complement, necessary for gAdic Calculation
	/**Complement: ~			*/
	public IIntRing Cmpl() { return ((AIntRing)copy()).CmplAt();}

	//Counting:

	/**Increment: x++	 */
	public integer inc() { return (integer) addAt (one()); }

	/**Decrement: x--	 */
	public integer dec() { return (integer) subAt (one()); }

	/**Residual in Place: 1-x	 */
	public integer ResidAt() {
		return (integer) copyAt(((IGroup)one()).subAt(this)); }
//	{return dec().negAt(); }

	//Delegations:

	/**Successor: x+1	 */
	public integer succ() { return ((IInteger) copy()).inc(); }
//	{return sInteger.succ(); }	//too much overhead

	/**Predecessor: x-1	 */
	public integer pred() { return ((IInteger) copy()).dec(); }
//	{return sInteger.pred(); }	//too much overhead

	/**Residual: 1-x	 */
	public integer Resid() { return ((integer) copy()).ResidAt(); }
//	{return (integer)((Group)one()).subAt(this); }
//	{return sInteger.Resid(); }	//too much overhead

	//Interface "SemiGroupM" already delegated to a local SemiGroupM by ARing

	/**Super Class to pass the methods to for Delegation	 */
	private IGroupM  sGroupM;		//AGroupM Super to pass the methods to

	/**Super Class to pass the methods to for Delegation	 */
	//private integer sInteger;	//AInteger Super to pass the methods to,
								//taken out, too much overhead, instead re- implemented

	/**This Constructor is only used in Initialize and Terminate of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	protected AIntRing() {
		sGroupM    = new AGroupM   (this);	//
//		sInteger   = new AInteger  (this);	//taken out, too much overhead
	}


	//////////////////////////////////////////
	//	Interface "GroupM" with Delegation	//
	//////////////////////////////////////////

	//I have reduced the Delegation on some basic Operations to speed up

	/**Inversion in Place: 1/x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM invAt() { return sGroupM.invAt(); }

	/**Inversion: 1/x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM inv() { return ((IGroupM) copy()).invAt(); }
//		return sGroupM.inv(); }

	/**Division: /
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM div(Object arg) { return ((IGroupM) copy()).divAt(arg); }
//		return sGroupM.div (arg); }

	/**Integer Power: x^n	 */	//public SemiGroupM Pow		(int n) { return sGroupM.Pow	(n);}
	/**Integer Power: x^=n	 */	//public SemiGroupM PowAt	(int n) { return sGroupM.PowAt(n);}

	/**Integer Power: x^=n
	 * This Implementation is also valid for negative n < 0 !	 */
	public ISemiGroupM PowAt(int n) { return sGroupM.PowAt(n); }

	/**Setting to 1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM one() { return ((IGroupM) newInstance()).oneAt(); }
//	{return sGroupM.one(); }

	/**Setting to 1 in Place:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IGroupM oneAt() { return sGroupM.oneAt(); }

	/**Testing for 1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public boolean isOne() { return sGroupM.isOne(); }

	/**Testing for -1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public synchronized boolean is_One() { negAt(); boolean ret = isOne(); negAt(); return ret; }

	//////////////////////
	//	Helper Methods	//
	//////////////////////

	/**Copies the Array into a new Array of greater Length newLength	 */
	public static IIntRing[] ArrayCopy(IIntRing[] arg, int newLength)	{
		IIntRing[] tmp = new IIntRing[newLength];
		System.arraycopy(arg, 0, tmp, 0, arg.length);
		return tmp; }

	//////////////////////////////
	//	Interface IComplex	//
	//////////////////////////////


	/**Returns the conjugate Complex Number:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjg() { return ((IIntRing)copy()).cjgAt(); }

	/**Testing Method, should be static or directly tested on the Types.
	 * Normally there are only these two Representations: Complex and Polar.
	 * This Implementation is quite elegant, because only cjg has to be defined,
	 * but it creates a great overhead.	 */
//	public boolean isComplex() { return equals(cjg()); }

	/**Addition of the conjugate complex argument: +=	 */
	public IIntRing addCjg(Object arg)	{ return ((IComplex) copy()).addAtCjg(arg); }

	/**Subtraction of the conjugate complex argument: -=	 */
	public IIntRing subtCjg(Object arg) { return ((IComplex) copy()).subAtCjg(arg); }

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	public IIntRing mulCjg(Object arg)	{ return ((IComplex) copy()).mulAtCjg(arg); }

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen Überlauf durch die Quadrierung
	 * und spart außerdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IIntRing divCjg(Object arg)	{ return ((IComplex) copy()).divAtCjg(arg); }

	/**Multiplies the Complex Number by i or divides it by -i:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90°	 */
	public IIntRing MulI() { return ((IComplex) copy()).mulIAt(); }

	/**Divides the Complex Number by i or multiplies it by -i:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90°	 */
	public IIntRing DivI() { return ((IComplex) copy()).divIAt(); }

	/**Addition of the conjugate complex argument in Place: +=	 */
	public IIntRing addAtCjg(Object arg) { addAt(arg); return this; }

	/**Subtraction of the conjugate complex argument in Place: -=	 */
	public IIntRing subAtCjg(Object arg) { subAt(arg); return this; }

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	public IIntRing mulAtCjg(Object arg) { mulAt(arg); return this; }

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen Überlauf durch die Quadrierung
	 * und spart außerdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IIntRing divAtCjg(Object arg) { divAt(arg); return this; }

	/**Multiplies the Complex Number by i or divides it by -i in Place:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90°	 */
	public IIntRing mulIAt() { throw new AbstractMethodError(); }

	/**Divides the Complex Number by i or multiplies it by -i in Place:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90°	 */
	public IIntRing divIAt() { throw new AbstractMethodError(); }

	//////////////////////////////////////////////
	//	Combinatoric Methods					//
	//	could be implemented in IIntRing	//
	//	but only for integer Arguments.			//
	//	Real Arguments lead to infinite Loops!	//
	//	Use the Gamma Function instead			//
	//////////////////////////////////////////////

	/**Calculates the Variation(n,k) = n!/(n-k)!
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * with considering Sequence.	 */
	public IIntRing Variation(IIntRing k) { return VariCombi(k, null); }

	/**Recursive Calculation of the Combination kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1 for the Factorial
	 * and calculates the Variation and the Factorial of k.
	 * The Combination can be calculated by dividing the Variation
	 * by the Factorial of k.
	 * With VariCombi both the Variation Var (this, k) =
	 * and the Combination Comb (this, k) = Var(this, k)/k!
	 * can be calculated.
	 * 'this' may be real, but k has to be integer.
	 * Otherwise you have to use the Gamma Function with Gamma(n+1) = n!. 	 */
	public IIntRing VariCombi(IIntRing k, IIntRing fact) {
		boolean calcFact = (fact != null);
		if (k.isZero()) {if (calcFact) fact.oneAt(); return (IIntRing) ((IIntRing) newInstance()).oneAt();}
		IIntRing Prod = (IIntRing)this.copy();
		IIntRing Vari = (IIntRing)this.copy();
		if (calcFact) fact.copyAt(k);
		k = (IIntRing) k.copy(); //preserve the old Value!
		while (!((IIntRing)k.dec()).isZero()) { //positive()) {
			Prod.mulAt(Vari.dec());		//multiplication with 1 unnecessary
			if (calcFact) fact.mulAt(k);//multiplication with 1 unnecessary also!
		}
		return Prod; }

	/**Calculates the Combination(n,k) = n!/(k!*(n-k)!)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * without considering the Sequence.
	 * It is also used with real n on calculating the Power Series
	 * of 'small' Disturbances: (1+-x)^m = 1 +- mx + ... + Comb(m,k)(+-x)^k
	 *
	 * This Calculation is optimized only in MetricIRing,
	 * because Comb(n, k) == Comb (n, n-k).
	 * The only Problem is that for large n and k
	 * the Division takes place after the Calculation of Vari(n, k),
	 * which may result in an Overflow.	 */
	public IIntRing Combination(IIntRing k) {
		AIntRing fact = (AIntRing)k.newInstance();
//		if (((MetricIRing)this).less(k.dbl())) //with long Numbers the Division had to be singled out.
//			return (AIntRing) VariCombi((MetricIRing) subt(k), fact).divAt(fact);
			return (AIntRing) VariCombi(					k, fact).divAt(fact); }	//'else' is unnecessary!

	/**Calculation of fact using VariCombi
	 * Recursive Calculation of Factorial kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1	 */
/*	public IIntRing fact() { return VariCombi((MetricIRing) this.copy(), null); }
*/
	//alternative Implementations:
/*
		IIntRing Prod = (IIntRing)copy();
		IIntRing Fact = (IIntRing)copy();
		while (((MetricIRing)Fact.dec()).positive()) Prod.mulAt(Fact);	//multiplication with 1 unnecessary
		return Prod; }

	return   (IIntRing)mul(	//Recursive Solution
			((IIntRing)pred()).fact());}
*/

	/**Calculates the Factorial of this integer number.
	 * The Definition is recursive:
	 * n! = Fact(n) = n * Fact(n-1); Fact(0) = 1;
	 *
	 * For real Numbers use the Gamma Function: Gamma (n+1) = n!	 */
	public IIntRing Fact() { return ((IIntRing)copy()).FactAt(); }

	/**Calculates the Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n! = Fact(n) = n * Fact(n-1); Fact(0) = 1;
	 *
	 * Because the Factorial can also be defined for fractions,
	 * this is not yet defined in IIntRing.
	 *
	 * For real Numbers use the Gamma Function: Gamma (n+1) = n!
	 * This Funtion cannot be calculated this way,
	 * so I limit the Arguments to integer Numbers.	 */
	public IIntRing FactAt() {
		if (isZero() || isOne()) return (IIntRing) oneAt();	//check for the special Case
		IIntRing Factor = (IIntRing)copy();
		while (!((IIntRing)Factor.dec()).isOne())	//grtr(one))
			mulAt(Factor);
		return this; }

	/**Calculates the Double Factorial of this integer number.
	 * The Definition is recursive:
	 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
	public IIntRing dblFact() { return ((IIntRing)copy()).dblFactAt(); }

	/**Calculates the Double Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
	public IIntRing dblFactAt() {
		if (isZero()) return (IIntRing) oneAt();	//check for the special Case
//		IIntRing one = (IIntRing)((IIntRing)newInstance()).oneAt();
		IIntRing Factor = (IIntRing)copy();
		while (!(((IIntRing)Factor.dec().dec()).isZero() || Factor.isOne())) //grtr(one))
			mulAt(Factor);	//The Test in this Loop is optimized in absMetricIRing!
			return this; }

	/**Recursive Calculation of Double Factorial kills the Stack.
	 * Iterative Solution, gives 0!! = 0, 1!! = 1	 */
/*	public AIntRing dblFact() {
		AIntRing Prod = (AIntRing)copy();
		AIntRing Fact = (AIntRing)copy();
		while (((MetricIRing)Fact.dec().dec()).positive()) Prod.mulAt(Fact);	//multiplication with 1 unnecessary
		return Prod; }
//	return   (AIntRing)mul(	//Recursive Solution
//			((AIntRing)pred()).dblFact());}
*/

	//////////////////////
	//	Optimizations	//
	//////////////////////

	//////////////////
	//	Extensions	//
	//////////////////

	/**Multiplication with an Integer Power of 2 in Place:
	 * Here implemented as a concatenated halfing.	 */
	public ISemiGroup mul2PowAt(int n) {
		if (n >= 0) return super.mul2PowAt(n);
		int i = 0; while (--i >= n) halfAt();
		return this; }

	/**Raised by an Integer Power of 2 in Place: x^=(2^n)	 */
//	public SemiGroupM Pow2PowAt(int n) { return sGroupM.Pow2PowAt(n);}

	//////////////
	//	Testing	//
	//////////////

	/**Method to test all Implementations in this class.	 */
	public static void testIt(final ICopyAble testInstance) throws Exception {
		IIntRing test = (IIntRing) testInstance.copy();
		IIntRing test1 =(IIntRing) testInstance.newInstance();
		L.n("Testing IIntRing:");

		IIntRing a = test.three();
		IIntRing b = test.four();
		IIntRing c = (IIntRing) b.succ();
		IIntRing d;
		IIntRing r;
		IIntRing x = test.two();
		IIntRing z = (IIntRing) (b.succ());
		L.n(x + ".LinAt(" + a + "," + z + ")= 11 = 2*3 + 5 =" + x.LinAt(a,z));
		L.n(x + ".BiLinAt(" + a + "," + z + "," + b + ")= 33 + 20 = 53 = 11*3 + 5 * 4 = " + x.BiLinAt(a,z,b));

		L.n(test + ".one()=" + test.one ());
		L.n(test + ".two()=" + test.two ());
		L.n(test + ".three()=" + test.three());
		L.n(test + ".four()=" + test.four());

		L.n(test + ".half()=" + test.half());
		L.n(test + ".third()=" + test.third());
		L.n(test + ".quarter()=" + test.quarter());

		L.n(test + ".isEven()=" + test.isEven());
		L.n(test + ".isOdd()=" + test.isOdd ());
		L.n(test + ".isOne()=" + test.isOne());

		L.n(test + ".pred()=" + test.pred());
		L.n(test + ".succ()=" + test.succ());

		L.n(test + ".cjg()=" + test.cjg ());
//		L.n(test + ".Cmpl()=" + test.Cmpl());

		L.n(test + ".div(" + test1 + ")=" + test.div (test1));
		L.n(test + ".inv()=" + test.inv ());
		L.n(test + ".ModDivAt(" + test1 + "," + test1 + ")=" + test.ModDivAt(test1, test1));
		L.n(test + ".Modl(" + test1 + ")=" + test.Modl(test1));
		L.n(test + ".Resid()=" + test.Resid ());

//		L.n(test + ".ggT(" + test1 + ")=" + test.ggT (test1));
//		L.n(test + ".kgV(" + test1 + ")=" + test.kgV (test1));
//		L.n(test + ".XggT(" + test1 + "," + test1 + "," + test1 + ")=" + test.XggT(test1, test1, test1));

		L.n(test + ".IntAt()=" + test.IntAt	());
//		L.n(test + ".toUpper()=" + test.toUpper	());

		//////////////////////
		//	free Testing	//
		//////////////////////

		//test ...At Operations
		L.n ("Soll: 12	Ist: " + a.mul (b));
		L.n ("Soll:  3	Ist: " + (r = (IIntRing) a.copy()));
		L.n ("Soll: false	Ist: " + r.equals (b));
		L.n ("Soll: true		Ist: " + r.equals (a));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.addAt(r), new Double(6));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.negAt(), new Double(-3));
		Assert.EQUALS(r.cbcAt(), new Double(-27));
//									   r.CbcRtAt ();//-3 / -4
		r = (IIntRing) a.copy(); Assert.EQUALS(r.copyAt(b), new Double(4));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.dblAt(), new Double(6));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.divAt(b), new Double(3.0/4));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.invAt(), new Double(1.0/3));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.mulAt(b), new Double(12));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.negAt(), new Double(-3));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.oneAt(), new Double(1));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.sqrAt(), new Double(9));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.subAt(b), new Double(-1));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.trplAt(), new Double(9));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.zeroAt(), new Double(0));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.dec(), new Double(2));
		r = (IIntRing) a.copy(); Assert.EQUALS(r.inc(), new Double(4));

		//test normal Operations
		//r =new Complex (a.add (b)); //7
		Assert.EQUALS(a.cbc(), new Double(27));
		Assert.EQUALS(a.dbl(), new Double(6));
		Assert.EQUALS(a.div(b), new Double(3.0/4));
		Assert.EQUALS(a.inv(), new Double(1.0/3));
		Assert.EQUALS(a.mul(b.add(a)), new Double(21));
		Assert.EQUALS(a.neg(), new Double(-3));
		Assert.EQUALS(a.one(), new Double(1));
		Assert.EQUALS(a.sqr(), new Double(9));
		Assert.EQUALS(a.sub(b), new Double(-1));
		Assert.EQUALS(a.trpl(), new Double(9));
		Assert.EQUALS(a.zero(), new Double(0));
		Assert.EQUALS(a.pred(), new Double(2));
		Assert.EQUALS(a.succ(), new Double(4));
		Assert.EQUALS(b.dblAt(), new Double(8));
		Assert.EQUALS(d = (IIntRing) c.trpl(), new Double(15));//UpCast geschieht immer automatisch, aber DownCast nicht!
		Assert.EQUALS(d = (IIntRing) d.sub(b), new Double(7));
		Assert.EQUALS(r = (IIntRing) a.Pow(5), new Double(243)); //243 = 3^5
		Assert.EQUALS(c.mulAt(a), new Double(15));
		Assert.EQUALS(b.mulAt(a), new Double(24));
		Assert.IS_TRUE(b.isEven());
		Assert.IS_TRUE(!b.isOdd ());
		Assert.IS_TRUE(!c.isEven());
		Assert.IS_TRUE(c.isOdd ());
		Assert.EQUALS(c.ggT(b), new Double(3));
		Assert.EQUALS(c.kgV(b), new Double(120));
		Assert.EQUALS(b.ModDivAt(c,a), new Double(9)); //24 = 1*15 + 9
		Assert.EQUALS(a, new Double(1));//
		b.negAt();
		Assert.EQUALS(b.ModDivAt (c,a), new Double(-9));  //-24 = -1*15 - 9
		Assert.EQUALS(a, new Double(-1));//
		c.negAt();
		Assert.EQUALS(b.ModDivAt (c,a), new Double(-9)); //-24 = 1*-15 -9
		Assert.EQUALS(a, new Double(1));//
		b.negAt();
		Assert.EQUALS(b.ModDivAt (c,a), new Double(9)); //24 = -1*-15 + 9
		Assert.EQUALS(a, new Double(-1));//
		testIt(ARing.class, testInstance);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(AIntRing.class, args); 
	}
	
}
