package function.derive.ring;

//import Functions.Derive.*;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.ring.metric.AMetricIRing;
import streamIO.copy.monoid.AMonoid;
import streamIO.copy.monoid.IMonoid;
import streamIO.copy.monoid.ISemiMonoid;
import function.IFunction;
import function.IInvertAble;

/** Defines an abstract Algebra or linear (Vector)Space
  * over a Body like the Real Numbers R or the complex Numbers C
  * by extending AIntRing with Monoid Operations.
  *
  * This Class combines the Methods for an IIntRing and a Monoid.
  * The Rules are the same as for a Vector Space respective Manifold.
  * The Implementation is similar to BodyDouble or RingLong,
  * because IFunction has no inner Components
  * that could be copied in copyAt().
  *
  * Design Decisions:
  * Instead of delegating all Operations to AMonoid or absHalfMonoid
  * I re-implement the Operations. 	 */
public abstract class AAlgebra
extends AMetricIRing
implements IMonoid {

	////////////////////////////////////////////////////////////////////////////
	//  Delegation:
	////////////////////////////////////////////////////////////////////////////

	/**Super Class to pass the methods to for Delegation	 */
	private IMonoid sMonoid;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**This Constructor is only used in Initialize and Terminate of abstract Classes
	 * and should normally be marked as 'protected' or 'friend',
	 * but all these Routines are not within one Package.
	 * It is needed for the Child Classes to call
	 * and replace Self by the Child Object with it's overloaded Methods.	 */
	protected AAlgebra(){ sMonoid = new AMonoid(this); }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IFunction: abstract Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Mapping from Left in Place:  this=°arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)    	   */
	public Object     MapAt(Object arg) { return sMonoid.MapAt(arg); }

	/**Mapping from the Left :  this=°arg	*/
	public Object Map  (Object arg) { return sMonoid.Map(arg); }

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.
	 * Returns false by Default,
	 * because most simple Functions are not even Algebras.	 */
	public boolean canProcess(Object arg) { return false; }

	/**Returns an alternative Representation that is 'simplified'	 */
	public IFunction simplify() { return this; }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface IInvertAble: abstract Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Returns the Inverse Function to this one: !this
	 * i.e. the Function that returns the identical Mapping,
	 * if Mapped / concatenated with this Function (at least locally)
	 * This is the same Inverse as returned from Monoid.invert()	 */
	public IInvertAble getInverse() { return sMonoid.getInverse(); }

	/** Sets the Inverse from outside.
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	public void setInverse(IInvertAble Inverse_) { sMonoid.setInverse(Inverse_); }

	/**Returns arg Mapped by the Inverse of this Object: !this°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object UnMap (Object arg) { return sMonoid.UnMap(arg); }

	/**Returns arg Mapped in Place by the Inverse of this Object: !this=°arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object UnMapAt(Object arg) { return sMonoid.UnMapAt(arg); }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface SemiMonoid: abstract Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Mapping from Left in Place:  this=°arg
	 * This Operation doesn't return 'this', but 'arg'!
	 * so to concatenate Mappings use B.mapAt(A.mapAt(a))
	 * which is more efficient than B.map(A.map(a)) or B.map(A).map(a)
	 * or A.cat(B).map(a)	       */
	public ISemiMonoid mapAt(Object arg) { return sMonoid.mapAt(arg); }

	/**Mapping from the Left :  this°arg	*/
	public ISemiMonoid map  (Object arg) { return sMonoid.map  (arg); }

	/**Mapping from the Right: °arg
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	//public ISemiMonoid cat  (Object arg) { return sMonoid.cat  (arg); }

	/**Mapping from the Right in Place: °arg
	 * Default Implementation to make this Class concrete and be able to delegate to this. 	 */
	//public ISemiMonoid catAt(Object arg) { return sMonoid.catAt(arg); }//

	///////////////////////////////////////////////////////////////////////////////
	//  Interface Monoid: abstract Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Mapping / Left-Concat with !arg in Place: !this=°arg */
	public IMonoid pamAt(Object arg) { return sMonoid.pamAt(arg); }

	/**Mapping / Left-Concat with !arg:  !this°arg	*/
	public IMonoid pam  (Object arg) { return sMonoid.pam(arg); }

	/**(Right) Concatenation with the Inverse of arg: this°!arg
	 * Resolves the Equation A°B = C = A.cat(B) for A:
	 * A =  C °!B = C.cat(B.invert()) = C.unCat(B)
	 * To solve it for B, you have to call solve():
	 * B = !A ° C = A.invert().cat(C) = A.unCat(C)
	 * If arg has no Inverse (i.e. the Inverse is a Relation, not a Function),
	 * you still can use unCat() to find certain unique Solutions.
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	//public IMonoid tac(Object arg) { return sMonoid.tac(arg); }

	/**Right-Concatenation with the Inverse in Place: this°=!arg  this\=arg
	 * This is the Inverse Operation to catAt(), not to map()!
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	//public IMonoid tacAt(Object arg) { return sMonoid.tacAt(arg); }

	/**Left-Concatenation with the Inverse: arg°!this
	 * Resolves the Equation A°B = C = A.map(B) for A:
	 * A = C°!B = C.map(B.invert()) = C.solve(B) = B.reSolve(C) = A.map(B).solve(B)
	 * Requires arg to be a Mapping and returns one!
	 */
	public ISemiMonoid reSolve(Object arg) { return sMonoid.reSolve(arg); }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface SemiMonoid: Delegation
	///////////////////////////////////////////////////////////////////////////////

	/**Concatenation: °
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid map(ISemiMonoid arg) {
		return   sMonoid.map(arg); }
//		return ((ISemiMonoid) this.copy()).MapAt (arg); }

	/**Duplication: x^2 == x°x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid dpl() {
		return   sMonoid.dpl(); }
//		return ((SemiMonoid) this.copy()).dplAt(); }

	/**Duplication in Place: x°=x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid dplAt() {
		return   sMonoid.dplAt(); }
//		return ((ISemiMonoid) this).catAt (this); }

	/**Triplication: x^3 == (x^2)°=x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid tpl() {
		return   sMonoid.tpl(); }
//		return ((SemiMonoid) this.copy()).tplAt(); }

	/**Triplication in Place: x°=x^2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid tplAt() {
		return   sMonoid.tplAt(); }
//		return ((SemiMonoid) this).catAt(((SemiMonoid) this).dpl()); }

	/**Quadruplication: x^4 = (x^2)^2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid qdl() {
		return   sMonoid.qdl(); }
//		return ((absHalfMonoid) copy()).qdlAt(); }

	/**Quadruplication in Place: x^=4 = (x^=2)^=2
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public ISemiMonoid qdlAt() {
		return   sMonoid.qdlAt(); }
//		return this.dplAt().dplAt(); }

	/**Concatenation by an Integer Power of 2 in Place: x^=(2^n)
	 * Here implemented as a concatenated doubling.	 */
	public ISemiMonoid mll2PowAt(int n) {
		return   sMonoid.mll2PowAt(n); }
/*		if (n < 0) throw new AbstractMethodError();
		int i = 0; while (++i <= n) this.dplAt();
		return this; }
*/
	/**Concatenation by an Integer Power of 2: x^(2^n)
	 * Here implemented as a concatenated doubling.	 */
	public ISemiMonoid mll2Pow  (int n) {
		return   sMonoid.mll2Pow(n);}
//		return ((SemiMonoid) copy()).mll2PowAt(n);}

	/**Concatenation by an Integer Power: x^n	 */
	public ISemiMonoid mll(int n) {
		return   sMonoid.mll(n);}
//		return ((SemiMonoid) copy()).mllAt(n);}

	/**Integer Power: x^=n
	 * You can implement a similar Algorithm for Multiplication.
	 * This Algorithm uses the binary Representation of the integer n.
	 * You could also write this with Prime Factors, but the Algorithm would be less
	 * elegant and also slower, because this one uses only 2*lb n Multiplications.	 */
	public ISemiMonoid mllAt(int n) { return sMonoid.mllAt(n); }

	///////////////////////////////////////////////////////////////////////////////
	//  Interface Monoid: Delegation
	///////////////////////////////////////////////////////////////////////////////

	/**Setting to 1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public	 IMonoid Identity() {
		return ((IMonoid) ((ICopyAble) this).newInstance()).IdentityAt(); }
//		return sMonoid.Identity();}

	/**Testing for 1:
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public boolean  isIdentity() {
		return this.equals(Identity()); }
//		return sMonoid.isIdentity();}

	/**Setting to 1 in Place:
	 * Can be implemented by unCat any number by itself. (except for 0!)
	 * A Standard Implementation. Should be overwritten by faster Implementations.	 */
	public IMonoid IdentityAt () {
        return sMonoid.IdentityAt(); }
//      return (Monoid) ((intMonoid)sMonoid).unCatAt(sMonoid); }

	/**Mapping / Left-Concat with !arg:  !this°arg	*/
    public ISemiMonoid unMap  (ISemiMonoid arg) {
        return sMonoid.pam(arg); }
//		return ((Monoid) self).unMapAt((SemiMonoid) arg.copy()); }

    /**Mapping / Left-Concat with !arg in Place: !this=°arg
     * Most efficient Implementation possible!
     */
    public ISemiMonoid unMapAt(ISemiMonoid arg) {
        return sMonoid.pamAt(arg); }
//		return ((Monoid) self).invert().MapAt(arg); }

	/**Left-Concatenation with the Inverse: this°!arg
     * This is the Inverse Operation to Map(), not to cat()!
     * Requires arg to be a Monoid!
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IMonoid solve(Object arg) { //return Inverse.catAt(arg); } //there is not always an Inverse!
        return sMonoid.solve(arg); }
//		return (Monoid) self.MapAt(((Monoid) arg).invert()); }
//		return ((Monoid) arg).invert().catAt(self); }

	/**Left-Concatenation with the Inverse: arg°!this
     * Resolves the Equation A°B = C = A.Map(B) for A:
     * A = C°!B = C.Map(B.invert()) = C.solve(B) = B.reSolve(C) = A.Map(B).solve(B)
     * Requires arg to be a Mapping and returns one!
	 */
	public ISemiMonoid reSolve(ISemiMonoid arg) {
        return sMonoid.reSolve(arg); }
//		return arg.Map(((Monoid) self).invert()); }

	protected IMonoid Inverse;

	/**Inversion in Place: Id\x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IMonoid revAt() {
		IMonoid ths = (IMonoid) newInstance().shallowCopyAt(this);
		shallowCopyAt(rev());
		Inverse = ths;
		return this; }

	/**Inversion: Id\x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IMonoid rev() {
        if (Inverse != null) return Inverse;
        return Inverse = (IMonoid) unMapAt(Identity()); }

}
