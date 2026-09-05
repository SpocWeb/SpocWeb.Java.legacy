package streamIO.copy.group;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;

/** Default Implementation of an additive SemiGroup (G,+).
  * The Interface is separated out, because of Symmetry
  * to the multiplicative SemiGroupM,
  * which  is used to simulate multiple Inheritance by Delegation.
  *
  * Design Decisions:
  * This Interface is also used to implement Object Stores,
  * although adding Objects may not be a commutable Operation
  * when applied to Streams only when applied to a 'Container'.
  * The Reason for this is the HomoMorphism between a Container and a Number:
  * |A.mul(B)| = |A|.mul(|B|)
  * |A.add(B)| = |A|.add(|B|)  when A and B denote Containers
  * This also means that if an Iterator or Container is added,
  * it is analyzed and the Elements are added (like addAll() in Java 1.2)
  * Unlike this the Method addItem() adds this Object without analyzing it.
  *
  * All Operations of SemiGroup are very rarely used in the Store Context
  * and make sense only if the Store also defines an Iterator to return it's Items!
  * The Iterator Context is close to the Monoid, because it can define an Order,
  * which makes Operations non-commutative.
  * although this does not even play a Role in distributive Laws.
  * A*(B+C) = A*B + A*C
  * (B+C)*A = B*A + C*A
  *
  * It is important when moving to the Group Operations:
  * A-B may not always be defined and
  * A+B-A != B = A-A+B
  * 'StrSearcher', 'PatternSearch' and 'StringMonoid' show how to handle such Iterators.
  *
  * This class does not extend Number, because not every Realization of a Group
  * maps to numeric Values. (e.g. Functions, Matrices etc.)
  * Also any other Argument Type than Object requires explicit casting on
  * every Operation which is a hassle.
  * You can also not derive any of the helper Classes from Number,
  * because you cannot cast between the branches, but you would have to
  * just to convert the results. So it is not possible!
  *
  * It would have been much easier and faster, if the Java Group
  * would have made 'Number' an Interface.
  *
  * The Conflict culminates in Fraction which can contain
  * Numbers as well as any other Metric Integrity Ring.
  *
  * Instead it presents the conversion Routines xxxxValue(Object arg)
  * to convert from Number Types.
  * The conversion Routines are moved to countable and measurable.
  *
  * Not all Methods are implemented as Defaults although that would be possible:
  * This is for one thing to keep this Class abstract
  * and for another to indicate the Methods that have in any case to be redefined.
  *
  * This abstract Class is not intended for Delegation.
  * This leads to the following Design Decisions:
  * The Return Type is chosen to be ASemiGroup instead of SemiGroup,
  * because this saves a cast from other Interface Types on returning the Result.
  * The Execution is not delegated to a Self_ Variable.
  *
  * Abstract Methods:
  * addAt (+=)
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:24Z
  * digest: cf238d2cdc4eff098ac94587bb4439f65bc6412f5f103badb998c3f2fdbe4eb3
  * stale: false
  * tags: [code/group_algebra, code/date_time]
  * concepts: [Group/SemiGroup Algebra]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public abstract class ASemiGroup
extends ACopyAble	//Number	//The Problem here is that not every Group maps to numeric Values.
implements ISemiGroup {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(ASemiGroup.class, 1);

	////////////////////////////////////////////////////////////////////////////
	//  Interface SemiGroup: abstract Methods
	////////////////////////////////////////////////////////////////////////////

	/** Addition in Place: +=
	  * This virtual Operation has to be implemented by each subclass.	 */
	public abstract ISemiGroup addAt(final Object arg);// { return this; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface SemiGroup: Implementation
	////////////////////////////////////////////////////////////////////////////

	/** Addition: +
	  * A Standard Implementation. Can be overwritten by faster Implementations.
	  * One Optimization is to first test, whether addAt can be implemented efficiently
	  * and if not, try to use Associativity to swap first and second Argument!  
	  */
	public ISemiGroup add (final Object arg) { return ((ISemiGroup)copy()).addAt (arg); }

	/** Double:   2x == x+x
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroup dbl ()	{ return ((ISemiGroup) copy()).dblAt(); }

	/** Double in Place: x+=x
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroup dblAt () { return addAt (this); }

	/** Triple: 3x == (2x)+=x
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroup trpl () { return ((ISemiGroup) copy()).trplAt(); }

	/** Triple in Place: x+=2x
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiGroup trplAt () { return addAt(dbl()); }

	/** Quadruple: 4x == 2(2x)	*/
	public ISemiGroup quad () { return ((ISemiGroup) copy()).quadAt(); }

	/** Quadruple in Place: 2(2x)	*/
	public ISemiGroup quadAt () { return dblAt().dblAt(); }

	/** Multiplication with an Integer Power of 2 in Place: *=2^n
	  * Here implemented as a concatenated doubling.	 */
	public ISemiGroup mul2PowAt(int n) {
		if (n < 0) throw new AbstractMethodError();
		int i = n; while (--i >= 0) dblAt();
		return this; }

	/** Multiplication with an Integer Power of 2:
	  * Here implemented as a concatenated doubling.	 */
	public ISemiGroup mul2Pow (int n) { return ((ISemiGroup) copy()).mul2PowAt(n); }

	/** Integer Multiplication: x*n	 */
	public ISemiGroup mul(int n) { return ((ISemiGroup) copy()).mulAt(n); }

	/** Integer Multiplication: x*=n
	  * This Implementation is only valid for positive n > 0 !
	  * You can implement a similar Algorithm for Multiplication.
	  * This Algorithm uses the binary Representation of the integer n.
	  * You could also write this with Prime Factors, but the Algorithm would be less
	  * elegant and also slower, because this one uses only 2*lb n Multiplications.	 */
	public ISemiGroup mulAt(int n) { //IntegrityRing n) {
		boolean odd;
		if			(n <  1) throw new AbstractMethodError();
		if (odd =  ((n &  1) != 0))
			if		(n == 1) return this;
		ISemiGroup B1 = (ISemiGroup) this.copy();	//contains the binary Powers
		while(n > 1){		//Use the Horner Scheme in the Exponent.
		    B1.dblAt();
			if (((n >>= 1) & 1) != 0) {	//(N1.isOdd()) {
				if (odd) addAt(B1);	//for even Powers
				else {  copyAt(B1); odd = true; }	//saves an Addition in the beginning here
			}
		}	//(! N1.halfAt().IntAt().equals(mZERO))
		return this; }

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

	////////////////////////////////////////////////////////////////////////////
	//	Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////

	/** All SemiGroups are commutative, otherwise rather use Monoid! 
	 * 
	 * @param a arbitrary first Object to add
	 * @param b arbitrary second Object to add
	 */
	private static final void testCommutativity(final ISemiGroup a, final ISemiGroup b) {
		Assert.EQUALS(a.add(b), b.add(a)); 
	}

	/** All SemiGroups are associative, otherwise rather use a Groupoid! 
	 * Actually, due to rounding Errors this does not apply to float Point Numbers! 
	 * @param a arbitrary first Object to add
	 * @param b arbitrary second Object to add
	 * @param c arbitrary third Object to add
	 */
	private static final void testAssociativity(final ISemiGroup a, final ISemiGroup b, final ISemiGroup c) {
		Assert.EQUALS(a.add(b).add(c), a.add(b.add(c))); 
	}

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt(final ICopyAble testInstance) throws Exception {	//testInstance inherited from ACopyAble;
		ISemiGroup test = (ISemiGroup) testInstance.random();
		ISemiGroup test1 =(ISemiGroup) testInstance.random();
		ISemiGroup test2 =(ISemiGroup) testInstance.random();
		L.n("Testing " + ASemiGroup.class.toString());
		testCommutativity(test, test1); 
		testAssociativity(test, test1, test2);
		//L.n(test+" addAt:" + test.addAt(test1));
		L.n(test+" add("+test1+"="+test.add(test1));
//		L.n(test+" dblAt:" + test.dblAt());
		L.n(test+" dbl()=" + test.dbl  ());
		final ISemiGroup binaryPower = (ISemiGroup) test.copy();
		for (int i = -1; ++i < 5; ) {
			final ISemiGroup result = test.mul2Pow(i);
			L.n(test+" mul2Pow("+i+")="+result);
			Assert.EQUALS(binaryPower, result);
			binaryPower.addAt(binaryPower);
//			L.n(test+" mul2PowAt("+i+"):"+test.mul2PowAt(i));
		}	//would modify the contents and make it less readable.
		L.n(test+" quad:	" + test.quad  ());
//		L.n(test+" quadAt:	" + test.quadAt());
		L.n(test+" trpl:	" + test.trpl  ());
//		L.n(test+" trplAt:	" + test.trplAt());
		final ISemiGroup product = (ISemiGroup) test.copy();
		for (int i = 0; ++i < 5; ) {
			final ISemiGroup result = test.mul(i);
			L.n(test+" mul("+i+")="+result);
			Assert.EQUALS(product, result);
			product.addAt(test);
//			L.n(test + "mulAt(" + i + ") = " + test.mulAt(i));
		}
		testIt(ACopyAble.class, testInstance);	//first test the Superclass Methods
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(ASemiGroup.class, args); 
	}

}
