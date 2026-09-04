package streamIO.copy.group.ring;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.AGroup;
import streamIO.copy.groupM.AGroupM;
import streamIO.copy.groupM.ASemiGroupM;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;

/**Default Implementation of the Algebraic Ring (M,+,-,0,*):
 * Set of Objects with inner Operations +,-,* , where
 * 1) (M,+,-,0) form a commutative Group
 * 2) (M,*) form a SemiGroup
 * 3) and the Distributive Laws apply: a*(b+c)=a*b+a*b und (a+b)*c =a*c+b*c
 *
 * It can be proved that...
 * a=0 v b=0 => a*b=0 (from distributive Laws)
 *
 * Design Decisions:
 * No new operations are defined, but both Interfaces are integrated into one.
 * Instead of delegating to the ASemiGroup Class, I re-implement some Methods.
 *
 * Implementation of SemiGroupM is done by Delegation.
 *
 * Not all Methods are implemented as Defaults although that would be possible:
 * This is for one thing to keep this Class abstract
 * and for another to indicate the Methods that have in any case to be redefined.
 *
 * Abstract Methods:
 *  addAt(+=)
 * subAt(-=)
 *  mulAt(*=)	 */
public abstract class ARing
extends AGroup
implements  IRing {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(1);

	//Implementations:

	//////////////////////
	//	Delegations:	//
	//////////////////////

	/** Super Class to pass the methods to for Delegation
	  * By making is concrete, some Optimizations are possible!	 */
	private ASemiGroupM sSemiGroupM;

	/**This Constructor is only used in Initialize and Terminate of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	protected ARing() { sSemiGroupM = new ASemiGroupM(this); }

	//////////////////////////////
	//	Interface "SemiGroupM":	//
	//////////////////////////////

	//I have taken out the Delegation here, because it creates Overhead
	//on so frequently used Operations like mul and div.

	/**Multiplication: *
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM mul (Object arg) { return ((ISemiGroupM) copy()).mulAt(arg); }
//	{return sSemiGroupM.mul (arg);};

	/**Square: x^2 == x*x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM sqr () { return ((ISemiGroupM) copy()).sqrAt(); }
//	{return sSemiGroupM.sqr();}

	/**Square in Place: x*=x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM sqrAt () { return mulAt(this); }
//	{return sSemiGroupM.sqrAt();}

	/**Cubic: x^3 == (x^2)*=x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM cbc () { return ((ISemiGroupM) copy()).cbcAt(); }
//	{return sSemiGroupM.cbc();}

	/**Cubic in Place: x*=x^2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM cbcAt () { return mulAt(sqr()); }
//	{return sSemiGroupM.cbcAt();}

	/**Quad: x^4 == (x^2)^=2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM qad () { return ((ISemiGroupM) copy()).qadAt(); }
//	{return sSemiGroupM.qad();}

	/**Quad in Place: (x^=2)^=2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM qadAt () { return sqrAt().sqrAt(); }
//	{return sSemiGroupM.qadAt();}

	/**Integer Power: x^n	 */
	public ISemiGroupM Pow(int n) { return ((ISemiGroupM) copy()).PowAt(n); }

	/**Integer Power: x^=n
	 * This Implementation is only valid for positive n > 0 !
	 * You can implement a similar Algorithm for Multiplication.
	 * This Algorithm uses the binary Representation of the integer n.
	 * You could also write this with Prime Factors, but the Algorithm would be less
	 * elegant and also slower, because this one uses only 2*lb n Multiplications.	 */
	public ISemiGroupM PowAt(int n) { return sSemiGroupM.PowAt(n); }

	/**Raised by an Integer Power of 2 in Place: x^=(2^n)	 */
	public ISemiGroupM Pow2PowAt(int n) { return sSemiGroupM.Pow2PowAt(n); }

	/**Raised by an Integer Power of 2: x^(2^n)
	 * Here implemented as a concatenated doubling.	 */
	public ISemiGroupM Pow2Pow  (int n) { return ((ISemiGroupM) copy()).Pow2PowAt(n); }

	//////////////////////
	//	New Operations:	//
	//////////////////////

	/**Linear Mapping in Place: x*=a + y <=> x*=a; x+=b;
	 * A Standard Implementation. Can be overwritten by faster Implementations.
	 * The only possible Optimizations with Vectors here are:
	 * -the saving of a second loop and
	 * -the storage of the intermediate Result	 */
	public IRing LinAt (Object a, Object y) {
		mulAt(a); addAt(y); return this; }

	/**Bilinear Mapping in Place: x*=a + y*b
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IRing BiLinAt (Object a, Object y, Object b) {
		mulAt(a); addAt(((ISemiGroupM)y).mul(b)); return this; }

	/**  adding a Product in Place: x+=a * y
	 * The only Optimizations with Vectors here are:
	 * -the saving of a second loop and
	 * -the creation and Storage of an intermediate Result.	*/
	public IRing addProdAt (Object a, Object y) {
		addAt(((IGroupM)y).mul(a)); return this; }	//no optimization possible, except in the basic Implementation.

	/**  Linear Mapping in Place: x-=a * y	*/
	public IRing subtProdAt (Object a, Object y) {
		subAt(((IGroupM)y).mul(a)); return this; }	//no optimization possible, except in the basic Implementation.


	//////////////////
	//	Delegation	//
	//////////////////

	/**  Linear Mapping: x + a*y			*/
	public IRing addProd   (Object a, Object y) {
		return ((IRing)copy()).addProdAt(a, y); }

	/**  Linear Mapping: x - a*y			*/
	public IRing subtProd   (Object a, Object y) {
		return ((IRing)copy()).subtProdAt(a, y); }

	/**Linear Mapping: x*a + y
	 * A Standard Implementation. Can be overwritten by faster Implementations.
	 * This only makes sense on elementary Operations.
	 * Saving a loop or an intermediate Result is not too effective.	 */
	public IRing Lin (Object a, Object y) {
		return ((IRing)copy()).LinAt(a, y); }

	/**Bilinear Mapping: x*a + y*b
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IRing BiLin (Object a, Object y, Object b) {
		return ((IRing)copy()).BiLinAt(a, y, b); }

	//////////////////////
	//	Optimizations	//
	//////////////////////

	//did not make sense: Java did not resolve the Ambiguity of Arguments
	//by using proximity in the inheritance tree!
	//Instead it threw the following Compile Error:
	//"Ambiguity between 'SemiGroupM gAdic.mulAt(Object)' and 'Ring ARing.mulAt(Ring)'"

	/**Addition in Place: +=	 */
//	public Ring addAt (Ring arg){return (Ring) addAt(arg);}

	/**Addition: +	 */
//	public Ring add	(Ring arg){return ((Ring) copy()).addAt(arg);}


	/**Subtraction in Place: -=	 */
//	public Ring subAt (Ring arg){return (Ring) subAt(arg);}

	/**Subtraction: -	 */
//	public Ring subt (Ring arg){return ((Ring) copy()).subAt(arg);}


	/**Multiplication in Place: *=	 */
//	public Ring mulAt (Ring arg){return (Ring) mulAt(arg);}

	/**Multiplication: *	 */
//	public Ring mul (Ring arg) 	{return ((Ring) copy()).mulAt(arg);}


	////////////////////////////////////////////////////////////////////////////
	//	Optimizations	
	////////////////////////////////////////////////////////////////////////////


	/////////////////////////////////////////////////////////////////////////////////////
	//	Test Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/**Method to test all Implementations in this class.	 */
	private static final void testDistributivity(final IRing a, final IRing b, final IRing c) throws Exception {
		Assert.EQUALS(a.mul(b.add(c)), ((IRing) a.mul(b)).add(a.mul(c)));
	}

	/**Method to test all Implementations in this class.	 */
	private static final void testZeroInRing(final IRing test) {
		Assert.EQUALS(test.mul(test.zero()), test.zero());
	}

	/**Method to test all Implementations in this class.	 */
	public static void testIt(final ICopyAble testInstance) throws Exception {
		final IRing test = (IRing) testInstance.random();
		final IRing test1 =(IRing) testInstance.random();
		final IRing test2 =(IRing) testInstance.random();
		final IRing test3 =(IRing) testInstance.random();
		testDistributivity(test, test1, test2);
		testZeroInRing(test);

//		L.n(test+".LinAt("+test+","+test1+")="+test.LinAt(test, test1));
		IRing expected = (IRing) test1.add(test2.mul(test));
		IRing result = test2.Lin(test, test1);
		L.n(test2+".Lin("+test+","+test1+")="+result);
		Assert.EQUALS(expected, result);
//		L.n(test+".BiLinAt("+test+","+test1+","+test1+")="+test.BiLinAt(test, test1, test1));
		expected = (IRing)((IRing) test.mul(test1)).add(test2.mul(test3));
		result = test.BiLin(test1, test2, test3);
		L.n(test+".BiLin("+test1+","+test2+","+test3+")="+result);
		Assert.EQUALS(expected, result);

//		L.n(test+".addProdAt("+test+","+test1+")="+test.addProdAt(test, test1));
		expected = (IRing) test.add(test1.mul(test2));
		result = test.addProd(test1, test2);
		L.n(test+".addProd("+test1+","+test2+")="+result);
		Assert.EQUALS(expected, result);

//		L.n(test+".subtProdAt("+test+","+test1+")="+test.subtProdAt(test, test1));
		expected = (IRing) test.sub(test1.mul(test2));
		result = test.subtProd(test1, test2);
		L.n(test+".subtProd("+test+","+test1+")="+test.subtProd(test, test1));
		Assert.EQUALS(expected, result);

		testIt(AGroupM.class, testInstance);	//first test the Superclass Methods
		testIt(AGroup.class, testInstance);	//first test the Superclass Methods
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(ARing.class, args); 
	}

}
