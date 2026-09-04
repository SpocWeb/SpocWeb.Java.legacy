package streamIO.copy.groupM;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;

/** Default Implementation of a multiplicative SemiGroup (G,*).
  * This Implementation must be kept completely synchronous to ASemiGroup
  * The Interface is separated out,
  * because it is used to simulate multiple Inheritance by Delegation.
  * It is an Extension of the abstract Base Class and a complete Delegator.
  *
  * Design Decisions:
  * This class does not extend Number, because not every Realization of a Group
  * maps to numeric Values. (e.g. Functions, Matrices etc.)
  * Instead it presents the conversion Routines xxxxValue(Object arg)
  * to convert from Number Types.
  *
  * Not all Methods are implemented as Defaults although that would be possible:
  * mulAt (*=) is not missing, but raises an Error when being called.
  * This is for one thing to keep this Class abstract
  * and for another to indicate the Methods that have in any case to be redefined.
  *
  * This SemiGroup must be concrete, not abstract, because it is used for delegation,
  * instead of being used for inheritance!
  * The primitive Methods throw Exceptions to warn Developers early.
  *
  * Abstract Methods:
  * mulAt (*=)	 */
public class ASemiGroupM //All
extends ACopyAble
implements ISemiGroupM {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(10);

	/////////////////////////////////////////////////////////////////////////////////////

	/** Local Reference to the Self, initialized by concrete classes.
	  * Used for the Simulation of (multiple) Inheritance with Delegation.
	  * Must be a virtual Interface Type to be able to take any Implementation.
	  */
	protected ISemiGroupM self;	//any self that comes along here, implements the same interfaces as ASemiGroupM

	/**This empty Constructor is only used in inheriting Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	protected ASemiGroupM() { self = this; } 	//set the 'self' Reference for Delegation

	/**This Constructor is only used in Initialize and Terminate of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	public ASemiGroupM(ISemiGroupM this_) { self = this_; } 	//set the 'self' Reference for Delegation

	//Interfaces:

	//Implementations:

	/**Abstract Method, only implemented to make this Class concrete
	 * to be able to delegate to this. Throws the #AbstractMethodError'	 */
	public ISemiGroupM mulAt(Object arg) { return (ISemiGroupM) shallowCopyAt(mul(arg)); } 	//used e.g. in Permutation, gAdic and Tensor

	//Delegation:

	/**Multiplication: *
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM mul(Object arg) { return ((IISemiGroupM) self.copy()).mulAt (arg); }

	/**Square: x^2 == x*x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM sqr() { return ((ISemiGroupM) self.copy()).sqrAt(); }

	/**Square in Place: x*=x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM sqrAt () { self.mulAt(self); return self; }

	/**Cubic: x^3 == (x^2)*=x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM cbc () { return ((ISemiGroupM) self.copy()).cbcAt(); }

	/**Cubic in Place: x*=x^2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM cbcAt () { return self.mulAt(self.sqr()); }

	/**Quad: x^4 == (x^2)^2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM qad () { return ((ISemiGroupM) self.copy()).qadAt(); }

	/**Quad in Place: x^2^2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroupM qadAt () {return self.sqrAt().sqrAt(); }

	/**Raised by an Integer Power of 2 in Place: x^=(2^n)
	 * Here implemented as a concatenated doubling.	 */
	public ISemiGroupM Pow2PowAt(int n) {
		if (n < 0) throw new AbstractMethodError();
		int i = n; while (--i >= 0) self.sqrAt();
		return self; }

	/**Raised by an Integer Power of 2: x^(2^n)
	 * Here implemented as a concatenated doubling.	 */
	public ISemiGroupM Pow2Pow  (int n) { return ((ISemiGroupM) self.copy()).Pow2PowAt(n); }

	/**Integer Power: x^n	 */
	public ISemiGroupM Pow(int n) { return ((ISemiGroupM) self.copy()).PowAt(n); }

	/**Integer Power: x^=n
	 * This Implementation is only valid for positive n > 0 !
	 * You can implement a similar Algorithm for Multiplication.
	 * This Algorithm uses the binary Representation of the integer n.
	 * You could also write this with Prime Factors, but the Algorithm would be less
	 * elegant and also slower, because this one uses only 2*lb n Multiplications.	 */
	public ISemiGroupM PowAt(int n) { //IntegrityRing n)
		boolean odd;
		if			(n <  1) throw new AbstractMethodError();
		if (odd =  ((n &  1) != 0))
			if		(n == 1) return self;
		ISemiGroupM B1 = (ISemiGroupM) self.copy();	//contains the binary Powers
		while(n > 1){		//Use the Horner Scheme in the Exponent.
		    B1.sqrAt();
			if (((n >>= 1) & 1) != 0) { 	//(N1.isOdd())
				if (odd) self.mulAt(B1);	//for even Powers
				else {self.copyAt(B1); odd = true; } 	//saves an Addition in the beginning here
			}
		}	//(! N1.halfAt().IntAt().equals(mZERO))
		return self; }

/*	    'Alternative Implementation
	    'This one takes (n-1) Multiplication in the worst case, when n is a prime number
	    'and as many division, because of the checking for divisibility
*/
/*	'    Dim Z1 As Long
	'    Dim mOne As absMetricIRing
	'    Set mOne = Self.ONE
	'    Dim N2 As absMetricIRing
	'    Dim M As absMetricIRing
	'    Dim Z As absMetricIRing
	'    Set Z = mOne
	'    Set M = mZERO   'force a Square first
	'    Do While N1.grtr(mOne)
	'        If M.equal(mZERO) Then
	'            Set N1 = N2
	'            Set B2 = B1
	'            For Z1 = 2 To Z.LongValue: B2.MULat (B1): Next Z1
	'        Else
	'            Z.INC
	'        End If
	'        Set N2 = N1.DivMod(Z, M)    'Checking happens too often, should be done only at prime numbers
	'    Loop
*/

	//////////////////////////////
	//  Interface ICopyAble
	//////////////////////////////

	/**Complement to copyAt() and shallopCopyAt().
	 * Does a 'deepCopy', to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use. */
	public ICopyAble copyAt(Object arg, int Depth) {
		throw new AbstractMethodError();
		//should never be called, instead, always 'self' should have been used before calling 'copyAt'
//		return self.copyAt(arg, Depth);
	}

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		throw new AbstractMethodError();
		//should never be called, instead, always 'self' should have been used before calling 'newInstance'
//		return self.newInstance();
	}

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg)
		throws java.io.IOException {
		throw new AbstractMethodError();
		//should never be called, instead, always 'self' should have been used before calling 'fromStreamAt'
//		return self.fromStreamAt(arg);
	}

	////////////////////////////////////////////////////////////////////////////////////
	//	Testing	
	////////////////////////////////////////////////////////////////////////////////////

	/** All SemiGroups are commutative, otherwise rather use Monoid! 
	 * 
	 * @param a arbitrary first Object to add
	 * @param b arbitrary second Object to add
	 */
	private static final void testCommutativity(final ISemiGroupM a, final ISemiGroupM b) {
		Assert.EQUALS(a.mul(b), b.mul(a)); 
	}

	/** All SemiGroups are associative, otherwise rather use a Groupoid! 
	 * Actually, due to rounding Errors this does not apply to float Point Numbers! 
	 * @param a arbitrary first Object to add
	 * @param b arbitrary second Object to add
	 * @param c arbitrary third Object to add
	 */
	private static final void testAssociativity(final ISemiGroupM a, final ISemiGroupM b, final ISemiGroupM c) {
		Assert.EQUALS(a.mul(b).mul(c), a.mul(b.mul(c))); 
	}

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt(final ICopyAble testInstance) throws Exception {
		ISemiGroupM test = (ISemiGroupM) testInstance.random();
		ISemiGroupM test1 =(ISemiGroupM) testInstance.random();
		ISemiGroupM test2 =(ISemiGroupM) testInstance.random();
		testCommutativity(test, test1);
		testAssociativity(test, test1, test2);

//		L.n(test+"mulAt("+test1+")="+test.mulAt(test1));
		L.n(test+"mul("+test1+")="+test.mul(test1));
//		L.n(test+"sqrAt()"+test.sqrAt());
		L.n(test+"sqr()"+test.sqr());
//		L.n(test+"cbcAt()"+test.cbcAt());
		L.n(test+"cbc()"+test.cbc());
		final ISemiGroupM power = (ISemiGroupM) test.copy(); 
		for (int i = 0; ++i < 5;) {
			ISemiGroupM result = test.Pow(i); 
			L.n(test+"Pow("+i+")= "+result);
			Assert.EQUALS(power, result);
//			L.n(test+"PowAt("+i+")="+test.PowAt (i));
			power.mulAt(test);
		}
		final ISemiGroupM binaryPower = (ISemiGroupM) test.copy(); 
		for (int i = -1; ++i < 5;) {
			ISemiGroupM result=test.Pow2Pow(i);
			L.n(test+"Pow2Pow("+i+")= "+result);
//			L.n(test+"Pow2PowAt("+i+")= "+test.Pow2PowAt(i));
			Assert.EQUALS(binaryPower, result);
			binaryPower.mulAt(binaryPower);
		}
		testIt(ACopyAble.class, testInstance);	//call the super class
	}
}
