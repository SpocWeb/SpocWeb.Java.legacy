package streamIO.copy.monoid;

import streamIO.copy.ICopyAble;
import streamIO.exception.OperationNotSupported;
import function.IInvertAble;

/**
  * Default Implementation of a non commutative Group (G,*,/,1), usually a Mapping.
  * This Implementation must be kept completely synchronous to AGroup
  * The Interface is separated out,
  * because it is used to simulate multiple Inheritance by Delegation.
  *
  * Design Decisions:
  * Not all Methods are implemented as Defaults although that would be possible:
  * /= is not missing, but raises an Error when being called.
  * This is for one thing to keep this Class abstract
  * and for another to indicate the Methods that have in any case to be redefined.
  *
  * This SemiGroup must be concrete,
  * because it is used for delegation,
  * but it it also used for inheritance,
  * so the primitive Methods throw Exceptions to fail fast,
  * but they are not made final!
  *
  * Abstract Methods:
  * catAt (�=)
  * unCatAt (\=)
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:40:38Z
  * digest: b4d9693cc2d990337565454957cf116a938343e2842b0354e5933837358babd6
  * stale: false
  * tags: [code/abstract_base, code/delegation, code/concatenation]
  * concepts: [Monoid, Delegation Pattern]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class AMonoid
extends ASemiMonoid
implements IMonoid {

	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////

	/**Cache for the calculated Inverse,
	 * because it is very frequently used!
	 * Caching is very dangerous, because any ...At() and the solve Method
	 * have to clear it!
	 */
//	protected Monoid Inverse;

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Empty Constructor used for inheriting Classes only. 	 */
	protected AMonoid() { } //set the 'self' Reference for Delegation

	/**Sets the 'self' Reference for Delegation.
	 * This Constructor is only used in 'Initialize' and 'Terminate' of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	public AMonoid (IMonoid this_) { super(this_); }	//call Constructor of SuperClass 'AHalfMonoid'

	////////////////////////////////////////////////////////////////////////////////
	//  Interface Monoid: abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/**Returns arg mapped by the Inverse of this Object: !this�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object UnMap (Object arg) {
		throw new OperationNotSupported(); }

	/**Returns arg mapped in Place by the Inverse of this Object: !this=�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object UnMapAt(Object arg) {
		throw new OperationNotSupported(); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface Monoid: Implementation
	////////////////////////////////////////////////////////////////////////////////

	/**Setting to 1 in Place:
	 * Can be implemented by unCat any number by itself. (except for 0!)
	 * A Standard Implementation. Should be overwritten by faster Implementations.	 */
	public IMonoid IdentityAt () {
		return (IMonoid) ((IIMonoid)self).pamAt(self); }
//	  return (Monoid) ((IMonoid)self).unCatAt(self); }

	/**Setting to 1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IMonoid Identity() { //uses newInstance() instead of copy() to save copying
		return ((IMonoid) self.newInstance()).IdentityAt(); }

	/**Testing for 1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public boolean isIdentity() { return self.equals(Identity()); }

	//Interface "IMonoid"

	/**Right-Concatenation with the Inverse in Place: this�=!arg  this\=arg
	 * This is the Inverse Operation to catAt(), not to map()!
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	//public IMonoid tacAt(Object arg) {
	//	return (IMonoid) self.catAt(((IMonoid) arg).rev());}

	/**Mapping / Left-Concat with !arg:  !this�arg	*/
	public IMonoid pam  (Object arg) {
		return ((IMonoid) self).pamAt((ISemiMonoid) ((ICopyAble) arg).copy()); }

	/**Mapping / Left-Concat with !arg:  !this�arg	*/
/*	public Object unMap  (Object arg) {
		if (arg instanceof SemiMonoid)
			return ((Monoid) self).unMap  ((SemiMonoid) arg);
			return ((Monoid) self).unMapAt(((CopyAble)  arg).copy()); }
//		return ((Monoid) self).invert().map  (arg); }
*/
	/**Mapping / Left-Concat with !arg in Place: !this=�arg
	 * Most efficient Implementation possible!
	 */
	public IMonoid pamAt(Object arg) {
		return (IMonoid) ((IMonoid) self).rev().mapAt(arg); }

	/**Mapping / Left-Concat in Place with !arg: !this=�arg */
/*	public Object unMapAt(Object arg) {
		if (arg instanceof SemiMonoid)
			return ((Monoid) self).unMapAt((SemiMonoid) arg);
			return ((Monoid) self).invert().mapAt(arg); }

	/**(Right) Concatenation with the Inverse of arg: this�!arg
	 * Resolves the Equation A�B = C = A.cat(B) for A:
	 * A =  C �!B = C.cat(B.invert()) = C.unCat(B)
	 * To solve it for B, you have to call solve():
	 * B = !A � C = A.invert().cat(C) = A.unCat(C)
	 * If arg has no Inverse (i.e. the Inverse is a Relation, not a Function),
	 * you still can use unCat() to find certain unique Solutions.
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	//public IMonoid tac(Object arg) { //return self.Inverse.catAt(arg); } //there is not always an Inverse!
	//	return ((IMonoid) self.copy()).tacAt(arg);}

	/**Left-Concatenation with the Inverse: this�!arg
	 * This is the Inverse Operation to map(), not to cat()!
	 * Requires arg to be a Monoid!
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IMonoid solve(Object arg) { //return Inverse.catAt(arg); } //there is not always an Inverse!
		return (IMonoid) self.mapAt(((IMonoid) arg).rev()); }
//	  return ((Monoid) arg).invert().catAt(self); }

	/**Left-Concatenation with the Inverse: arg�!this
	 * Resolves the Equation A�B = C = A.map(B) for A:
	 * A = C�!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 * Requires arg to be a Mapping and returns one!
	 */
/*	public Object reSolve(Object arg) {
		if (arg instanceof SemiMonoid)
			return ((Monoid) self).reSolve((SemiMonoid) arg);
		return null; }
*/
	/**Left-Concatenation with the Inverse: arg�!this
	 * Resolves the Equation A�B = C = A.map(B) for A:
	 * A = C�!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 * Requires arg to be a Mapping and returns one!
	 */
	public ISemiMonoid reSolve(Object arg) {
		return ((IMonoid)arg).map(((IMonoid) self).rev()); }

	//Implementations:

	//Internal Delegation

	/**Inversion in Place: Id\x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IMonoid revAt() {
		return //Inverse =
						 (IMonoid) self.newInstance().shallowCopyAt(((IMonoid) self).rev()); }

	/**Inversion: Id\x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IMonoid rev() {
//		if (Inverse != null) return Inverse;
		return //Inverse =
						 (IMonoid) ((IMonoid) self).pamAt(((IMonoid) self).Identity()); }
//					  ((Monoid) ((Monoid) self).Identity()).unCatAt(self); }
//	{ return (Monoid) ((IMonoid) self.copy()).invertAt(); }
//	{ throw new AbstractMethodError(); }

	/**Integer Power: x^=n
	 * You can implement a similar Algorithm for Multiplication.
	 * This Algorithm uses the binary Representation of the integer n.
	 * You could also write this with Prime Factors, but the Algorithm would be less
	 * elegant and also slower, because this one uses only 2*lb n Multiplications.	 */
	public ISemiMonoid mllAt(int n) { //IntegrityRing n) {
		if (n == 0) return ((IMonoid)self).IdentityAt();
		if (n <  0) {((IMonoid)self).revAt(); n=-n;}
		return super.mllAt(n); }

	////////////////////////////////////////////////////////////////////////////
	//  Interface IInvertAble
	////////////////////////////////////////////////////////////////////////////


	/**Returns the Inverse Function to this one: !this
	 * i.e. the Function that returns the identical Mapping,
	 * if mapped / concatenated with this Function (at least locally)
	 * This is the same Inverse as returned from Monoid.invert()	 */
	public IInvertAble getInverse() { return rev(); }

	/** Sets the Inverse from outside.
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	public void	setInverse(IInvertAble inverse) {
		throw new OperationNotSupported(); }

	//////////////
	//	Testing	//
	//////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt() throws java.io.IOException {
		IMonoid test = (IMonoid) testInstance;
		IMonoid test1 =(IMonoid) testInstance.copy();
//		AHalfMonoid.testIt();	//call the super class
		System.out.println("Testing AMonoid:");

		System.out.println(test + ".isIdentity()=" + test.isIdentity());
		System.out.println(test + ".equals(	" + test1 + ")=" + test.equals(test1));
		System.out.println(test + ".invert()=	" + test.rev());
//		System.out.println(test + ".invertAt()=" + test.invertAt());
		System.out.println(test + ".unMap  (	" + test + ")=" + test.pam  (test1));
//		System.out.println(test + ".unMapAt(	" + test + ")=" + test.unMapAt(test1));
		System.out.println(test + ".Identity  ()=" + test.Identity  ());
//		System.out.println(test + ".IdentityAt()=" + test.IdentityAt());
		int i = -5;
		while (++i < 5) {
			System.out.println(test + "mll	(" + i + ") = " + test.mll	(i));
//			System.out.println(test + "mllAt(" + i + ") = " + test.mllAt(i));
		}
	}
}
