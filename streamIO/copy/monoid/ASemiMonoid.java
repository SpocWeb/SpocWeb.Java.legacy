package streamIO.copy.monoid;

import streamIO.copy.ACopyAble;
import streamIO.copy.ICopyAble;
import function.IFunction;

/**Default Implementation of a concatenative SemiMonoid (G,�).
 * This Implementation must be kept completely synchronous to ASemiGroup
 * The Interface is separated out,
 * because it is used to simulate multiple Inheritance by Delegation.
 *
 * Design Decisions:
 * Not all Methods are implemented as Defaults although that would be possible:
 * catAt (*=) is not missing, but raises an Error when being called.
 * This is for one thing to keep this Class abstract
 * and for another to indicate the Methods that have in any case to be redefined.
 *
 * This SemiMonoid must be concrete, because it is used for delegation,
 * instead of being used for inheritance!
 *
 * Must not be abstract, because it is used for Delegation!
 * Abstract Methods:
 * catAt (�=)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:37Z
 * digest: 502e05b2ae506ef4cbcb7a2cc0ab18146f03cd5ed875034dc33e51a405c68cd2
 * stale: false
 * tags: [code/abstract_base, code/delegation, code/concatenation]
 * concepts: [Monoid, Delegation Pattern]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class ASemiMonoid
extends ACopyAble
implements ISemiMonoid {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Local Reference to the Self, initialized by concrete classes.
	  * Used for the Simulation of (multiple) Inheritance with Delegation.
	  * Must be a virtual Interface Type to be able to take any Implementation.	 */
	protected ISemiMonoid self;	//any self that comes along here, implements the same interfaces as AHalfMonoid

	/** Empty Constructor used for inheriting Classes only.
	  * Self must be set explicitly.	 */
	protected ASemiMonoid(){ self = this; }//set the 'self' Reference for Inheritence
	//Delegation Classes have to set 'self' in the other Constructor.

	/** This Constructor is only used in Initialize and Terminate of abstract Classes
	  * and should normally be marked as 'protected' or 'friend',
	  * but all these Routines are not within one Package.
	  * It is needed for the Child Classes to call
	  * and replace Self by the Child Object with it's overloaded Methods.	 */
	public ASemiMonoid(ISemiMonoid this_) { self = this_; }//set the 'self' Reference for Delegation

	//Interfaces:

	//Implementations:

	/** Mapping from the Left :  this=�arg	*/
	public Object Map  (Object arg) {
		if (arg instanceof ISemiMonoid) return self.map((ISemiMonoid) arg);
		return self.mapAt(((ICopyAble) arg).copy()); }
//	  return ((SemiMonoid) arg).cat(self); }

	/** Mapping from the Left :  this�arg	*/
	public ISemiMonoid map  (Object arg) {
		return self.mapAt(((ISemiMonoid) ((ICopyAble) arg).copy())); }
//		return ((SemiMonoid) arg).cat(self); }

	/** Mapping from Left in Place:  this=�arg
	  * This Operation doesn't return 'this', but 'arg'!
	  * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	  * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	  * or A.cat(B).map(a)    	   */
	public Object     MapAt(Object arg) {
		if (arg instanceof ISemiMonoid) return self.mapAt((ISemiMonoid) arg);
		return ((ICopyAble) arg).shallowCopyAt(self.map(arg)); }

	/** Mapping from Left in Place:  this=�arg
	  * This Operation doesn't return 'this', but 'arg'!
	  * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	  * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	  * or A.cat(B).map(a)	       */
	public ISemiMonoid mapAt(Object arg) {
//		return ((SemiMonoid) arg).catAt(this); }
		ISemiMonoid arg_ = (ISemiMonoid) arg;
		arg_.shallowCopyAt(self.map(arg));
		return arg_; }

	/** Mapping from the Right in Place: �arg
	  * @return  this, mapped by arg.
	  * Default Implementation to make this Class concrete and be able to delegate to this. 	 */
	//public ISemiMonoid catAt(Object arg) { //
	//	return (ISemiMonoid) shallowCopyAt(cat(arg)); }	//used e.g. in Permutation, gAdic and Tensor

	//Delegation:

	/** Mapping from the Right: �arg
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	//public ISemiMonoid cat (Object arg) { //throws CloneNotSupportedException {
	//	return ((ISemiMonoid) self.copy()).catAt(arg); }

	/** Duplication: x^2 == x�x
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid dpl () { //throws CloneNotSupportedException {
		return ((ISemiMonoid) self.copy()).dplAt(); }

	/** Duplication in Place: x�=x
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid dplAt () {
		self.mapAt (self); return self; }
//	  return (SemiMonoid) ((ISemiMonoid) self).catAt (self); }

	/** Triplication: x^3 == (x^2)�=x
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid tpl () { //throws CloneNotSupportedException {
		return ((ISemiMonoid) self.copy()).tplAt(); }

	/** Triplication in Place: x�=x^2
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid tplAt (){ //throws CloneNotSupportedException {
		self.mapAt(self.dpl()); return self; }
//		return (SemiMonoid) ((SemiMonoid) self).catAt(((SemiMonoid) self).dpl()); }

	/** Quadruplication: x^4 = (x^2)^2
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid qdl () { return ((ASemiMonoid) self.copy()).qdlAt(); }

	/** Quadruplication in Place: x^=4 = (x^=2)^=2
	  * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid qdlAt(){ return self.dplAt().dplAt(); }

	/** Mapping / Concatenation by an Integer Power of 2 in Place: x^=(2^n)
	  * Here implemented as a concatenated doubling.	 */
	public ISemiMonoid mll2PowAt(int n) {
		if (n < 0) throw new AbstractMethodError();
		while (--n >= 0) self.dplAt();
		return self; }

	/** Mapping / Concatenation by an Integer Power of 2: x^(2^n)
	  * Here implemented as a concatenated doubling.	 */
	public ISemiMonoid mll2Pow(int n) { return ((ISemiMonoid) self.copy()).mll2PowAt(n); }

	/** Mapping / Concatenation by an Integer Power: x^n	 */
	public ISemiMonoid mll    (int n) { return ((ISemiMonoid) self.copy()).mllAt(n); }

	/** Mapping / Concatenation by an Integer Power: x^=n
	  * This Implementation is only valid for positive n > 0 !
	  * You can implement a similar Algorithm for Multiplication.
	  * This Algorithm uses the binary Representation of the integer n.
	  * You could also write this with Prime Factors, but the Algorithm would be less
	  * elegant and also slower, because this one uses only 2*lb n Multiplications.	 */
	public ISemiMonoid mllAt(int n) { //IntegrityRing n) {
		ISemiMonoid B1;
		ISemiMonoid B2 = null;
		if ( n <  1) throw new AbstractMethodError();
		if ( n == 1) return self;
		if ((n &  1) != 0) B2 = self;	// one();
		B1 = (ISemiMonoid) self.copy();
		//First Implementation: Use the Horner Scheme in the Exponent.
		do{
			B1.dplAt();    //you can save a SQR in the end by skipping this
			if (((n >>= 1) & 1) != 0) {	//(N1.odd()) {
				if (B2 == null) B2 = (ISemiMonoid) self.copyAt(B1);	//for even Powers
				else B1.mapAt(B2);	//you could save a cattiplication in the beginning here
//				else B2.catAt(B1);	//you could save a cattiplication in the beginning here
			}
		} while (n != 0);	//(! N1.halfAt().IntAt().equals(mZERO))
		return self; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface IFunction: Dummy Implementations
	////////////////////////////////////////////////////////////////////////////

	/** Returns false by default, since most simple functions are not even algebras.
	  * @return  true, when this Class can operate on Arguments of this Type
	  * This Function makes sense at this Level,
	  * because here there is always the Alternative
	  * not to operate on the Constants,
	  * but to operate on the Functions and operate the Results on evaluation.
	  * Returns false by Default,
	  * because most simple Functions are not even Algebras.	 */
	public boolean canProcess(Object arg) { return false; }

	/** Returns this instance unchanged as its own simplified representation.
	  * @return  an alternative Representation that is 'simplified'	 */
	public IFunction simplify() {
		return this; }

	////////////////////////////////////////////////////////////////////////////
	//  Interface ICopyAble
	////////////////////////////////////////////////////////////////////////////

	/** Complement to copyAt() and shallopCopyAt().
	  * Does a 'deepCopy', to a certain Level
	  * i.e. also inner Components are copied up to the Depth.
	  * Returns the itself for further use. */
	public ICopyAble copyAt(Object arg, int Depth) { throw new AbstractMethodError(); }

	/** Always throws, since only a concrete subclass knows how to construct itself.
	  * @return a new, uninitalized Instance of it's class.
	  * This can in VB also be achieved by 'CreateObjectFromInstance',
	  * which may be slower.
	  * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() { throw new AbstractMethodError(); }

	/** Fills this Instance with the Contents read from the String.
	  * This is not defined in an Interface yet... */
	public ICopyAble fromStreamAt(java.io.StreamTokenizer arg) throws java.io.IOException {
		throw new AbstractMethodError(); }

	//////////////
	//	Testing	//
	//////////////

	/** Method to test all Implementations in this class.
	  * Must call testIt of the super Class.	 */
	public static void testIt() throws java.io.IOException {
		ISemiMonoid test = (ISemiMonoid) testInstance;
		ISemiMonoid test1 =(ISemiMonoid) testInstance.copy();
//		BaseCopy.absCopyAble.testIt();	//call the super class
		System.out.println("Testing AHalfMonoid:");

//		System.out.println(test + "catAt:	" + test.catAt(test1));
		System.out.println(test + "map:	 " + test.map  (test1));
//		System.out.println(test + "dplAt:" + test.dplAt());
		System.out.println(test + "dpl:	 " + test.dpl  ());
//		System.out.println(test + "tplAt:" + test.tplAt ());
		System.out.println(test + "tpl:	 " + test.tpl   ());
		int i = -2;
		while (++i < 5) {
			System.out.println(test + "mll	(" + i + ")= " + test.mll	(i));
//			System.out.println(test + "mllAt(" + i + ")= " + test.mllAt (i));
		}
		i = -2;
		while (++i < 5) {
			System.out.println(test + "mll2Pow	(" + i + ")= " + test.mll2Pow	(i));
//			System.out.println(test + "mll2PowAt(" + i + ")= " + test.mll2PowAt (i));
		}
	}
	
}
