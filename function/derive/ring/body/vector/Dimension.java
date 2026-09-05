package function.derive.ring.body.vector;

import streamIO.copy.group.ring.metric.body.vector.Tensor;
import function.AFunction;
import function.IInvertAble;
import function.derive.CCountAble;
import function.derive.IDeriveAble;
import function.derive.ring.Square;

/*
import BaseCopy.*;
import RingFuncs.*;
import Vector.*;
*/
/** Helper Function to enable partial Inversion, Integration and Derivation.
  * Selects the Dimension from a Vector passed as an Argument to it.
  * So it works like Identity for Dimension Dim
  * It's Derivatives are Delta[i,Dim], so they needn't be cached.
  * It's Integrals are partial. So far no Mechanism is defined
  * to consolidate partial Integrals into a whole Stem Function,
  * but the Test for whether that Integral can exist can be performed
  * using d^2f/(du,dv) == d^2f/(dv,du) (for derivable df/du and df/dv).
  *
  * This Class could implement a Singleton per Dimension,
  * but this is not necessary, since nothing is stored but the Dimension.
  * Instead it overwrites the equals Function.
  *
  * Design Decisions:
  * There are two ways to interpret the generic Derivative(), getInverse()
  * and getIntegral() Methods:
  * 1) return the 0th Derivative, Inverse or Integral
  * 2) return a Tensor
  *		containing the all the partial Derivatives, Inverses or Integrals.
  *
  * The Second Solution corresponds to Dimension.map(arg)
  * returning the full Argument and using Dim = -1 for signaling full Operations.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:43:45Z
  * digest: f4102c23ed379fd005dd76f561ac8c7c28f2867d03f3a014404cc71c70997e3e
  * stale: false
  * tags: [code/differential_integration, code/mathematical_function]
  * concepts: [Partial Derivatives, Vector Calculus]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  * The first Solution is so far implemented, except in Function(arg).  */
public class Dimension
extends AFunction
implements IPartialDerive {

	/**Dimension that is selected from the Vector	 */
	protected int Dim = -1;

	/**Initializing Constructor 	 */
	public Dimension(int Dim) { this.Dim = Dim; }

	/**Returns the Inverse Function to this one, see Identity:	 */
	public IInvertAble getInverse   () { return getInverse   (0); }

	/**Returns the Derivative	 */
	public IDeriveAble getDerivative() { return getDerivative(0); }

	/**Returns the Integral	 */
	public IDeriveAble getIntegral  () { return getIntegral  (0); }

	/**Sets the Inverse	*/
	public void setInverse   (IInvertAble Inverse   ) { throw new AbstractMethodError(); } // this.inverse   (0) = (IPartialDerive) Inverse; }

	/**Sets the Derivative	 */
	public void setDerivative(IDeriveAble Derivative) { throw new AbstractMethodError(); } //this.Derivative(0) = Derivative; }

	/**Sets the Integral	*/
	public void setIntegral  (IDeriveAble Integral  ) { throw new AbstractMethodError(); } //this.Integral  (0) = Integral  ; }

	/**This Function represents the Dimension Function.	 */
	public Object Map (Object arg) {
		if (Dim == -1) return arg;
		if (arg instanceof Tensor) return ((Tensor) arg).a[Dim];
		return ((Object[]) arg)[Dim]; }

	/**This Function represents the Dimension Function.	 */
	public Object UnMap  (Object arg) { return getInverse(0).Map(arg); }

	/**This Function represents the Dimension Function.	 */
	public Object UnMapAt(Object arg) { return getInverse(0).Map(arg); }

	//////////////////////////
	//	partial Functions:	//
	//////////////////////////

//	public IPartialDerive Derivative(int Dim)
	/**Returns the partial Derivative of this Function in Direction Dim	 */
	public IDeriveAble getDerivative(int Dim) {
//		throw new AbstractMethodError();	//Inverse in other Dimensions not possible!
		if (this.Dim == Dim) {
			return CCountAble.One; }	//== Identity.getDerivative()
			return CCountAble.Zero; }

	/**Returns the partial Integral of this Function in Direction Dim	 */
	public IPartialDerive getIntegral(int Dim) {	//Int[y*dy] = y^2/2;	Int[x*dy] = x*y
		if (this.Dim == Dim)
			return new  CatPartial(Square.xx_2, this); //u^2/2@(u=arg[Dim])
//			return new  CatPartial(Identity.Identity.getIntegral(), this);
			return new ProdPartial(new Dimension(Dim)          , this); }	//Int[x*dy] = x*y

	/**Returns the partial Inverse Function to this one in Direction Dim
	 * i.e. the Function that returns the identical Mapping,
	 * if concatenated with this Function (at least locally)	 */
	public IPartialDerive getInverse(int Dim) {
		if (this.Dim == Dim) return this;	//== Identity.getInverse()
		throw new AbstractMethodError();	//Inverse in other Dimensions not possible!
//		return Zero.Zero;
	}

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 	 */
	public boolean equals  (Object arg) {
		return (arg instanceof Dimension) && (Dim == ((Dimension)arg).Dim);}

	/**Returns the textual "x[Dim]" representation of this coordinate-projection Function.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return "x[" + Dim + "]"; }

	/**Helper Function to convert simple IDeriveAble Functions	 */
	public static IDeriveAble Derivative(IDeriveAble f, int Dim) {
		if (f instanceof IPartialDerive) {
			return ((IPartialDerive) f).getDerivative(Dim); }
			return f.getDerivative(); }

	/**Helper Function determine a Constant due to different Dimensions
	 * not necessary, because checking for actual Derivative being Zero is easier.	 */
/*	public static boolean isRelativeConst(IDeriveAble f, int Dim) {
		return (f instanceof Const) ||
			((f instanceof Dimension) &&	//optimization to avoid Additions of Zero
			 (((Dimension) f).Dim != Dim));	//which of course are also simplified!!!
	}
*/
}
